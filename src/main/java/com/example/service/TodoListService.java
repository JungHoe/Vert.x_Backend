package com.example.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.example.mapper.TodoListMapper;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

public class TodoListService {
	TodoListMapper query = new TodoListMapper();
	Logger log;
	
	
	// Get TodoList InitData
	public void getTodoList(RoutingContext routingContext, SQLClient con) {	
		Map<String, Object> result = new HashMap<String, Object>(); 
		HttpServerResponse response = routingContext.response();
	
		// Get TotalCnt
		con.query(query.getTotalCnt(), res2 -> { 
			ResultSet rs = res2.result();
			result.put("totalCnt", rs.getNumRows());
		});
		
		// Get Todos
		con.query(query.getSelectList(), res3 -> {
			ResultSet rs = res3.result();
			result.put("todoList", rs.getRows());
			response.end(Json.encodePrettily(result));
		});
		
	}
	
	// Insert Todo
	public void insertTodo(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
	
		String id = routingContext.request().getFormAttribute("id");
		String fileName = routingContext.request().getFormAttribute("fileName");
		
		JsonObject pubData = new JsonObject();
			pubData.put("id", request.getParam("id"));
			pubData.put("text", request.getParam("text"));
			pubData.put("color", request.getParam("color"));
		
		Set<FileUpload> uploads = routingContext.fileUploads();
		for(FileUpload upload : uploads) {
	        File uploadedFile = new File(upload.uploadedFileName());
	        uploadedFile.renameTo(new File("files/" +id+"_"+upload.fileName()));
	        try {
				uploadedFile.createNewFile();
			} catch (IOException e1) {
			}
	        new File(upload.uploadedFileName()).delete();
		}
		
		JsonArray params = new JsonArray()
				.add(request.getParam("id"))
				.add(request.getParam("text"))
				.add(request.getParam("color"));
		
		if(fileName == null) {
			params.addNull();
			pubData.put("image", "null");
		} else {
			params.add("http://localhost:8080/api/image?fileName="+id+"_"+fileName);
			pubData.put("image", "http://localhost:8080/api/image?fileName="+id+"_"+fileName);
		}
		
		con.updateWithParams(query.getInsert(), params, e -> {
			if(e.succeeded()) {
				con.query(query.getShareTodo(request.getParam("id")), e1 -> {
					if(e1.succeeded()) {
						ResultSet rs = e1.result();
						List<JsonObject> list = rs.getRows();
						routingContext.vertx().eventBus().publish("todos", list.get(0).toString());					
						routingContext.response().end();
					} else {
						System.out.println("퍼블리싱 쿼리 실행오류 ::"+e1.cause());
						routingContext.response().end();
					}
				});
			}else {
				System.out.println("입력쿼리 실행오류 ::"+e.cause());
				routingContext.response().end();
			}
		});
	}

	// Checked Todo
	public void checkTodo(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		
		JsonArray params = new JsonArray()
				.add(request.getParam("checked"))
				.add(request.getParam("id"));
		
		con.updateWithParams(query.getChecked(), params, e -> {
			if(e.succeeded()) {
				con.query(query.getShareTodo(request.getParam("id")), e1 -> {
					if(e1.succeeded()) {
						ResultSet rs = e1.result();
						List<JsonObject> list = rs.getRows();
						routingContext.vertx().eventBus().publish("todos", list.get(0).toString());					
						routingContext.response().end();
					} else {
						System.out.println("퍼블리싱 쿼리 실행오류 ::"+e1.cause());
						routingContext.response().end();
					}
				});
			}else {
				System.out.println("체크쿼리 실행오류 ::"+e.cause());
				routingContext.response().end();
			}
		});
	}

	// Delete Todo
	public void deleteTodo(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		
		JsonArray params = new JsonArray().add(request.getParam("id"));
		
		con.updateWithParams(query.getDelete(), params, e -> {	
			if(e.succeeded()) {
				con.query(query.getShareTodo(request.getParam("id")), e1 -> {
					if(e.succeeded()) {
						ResultSet rs = e1.result();
						List<JsonObject> list = rs.getRows();
						routingContext.vertx().eventBus().publish("todos", list.get(0).toString());					
						routingContext.response().end();
					}else {
						System.out.println("퍼블리싱 쿼리 실행오류 ::"+e1.cause());
						routingContext.response().end();
					}
				});	
			}else {
				System.out.println("삭제쿼리 실행오류 ::"+e.cause());
				routingContext.response().end();
			}
		});
	}

	// Update Todo
	public void updateTodo(RoutingContext routingContext, SQLClient con) {
        HttpServerRequest request = routingContext.request();
        JsonArray params = new JsonArray(); // update parameters 생성
        String updateQuery= "";
        
        params.add(request.getFormAttribute("text")).add(request.getFormAttribute("color"))
        .add(request.getFormAttribute("checked"));
        
        
        if(request.getFormAttribute("action").equals("notImgUpdated")) {
        	System.out.println("변경없음");
            params.add(request.getParam("id"));
            updateQuery=query.getUpdate1();
		
        }else if(request.getFormAttribute("action").equals("imgDeleted")) {
        	System.out.println("이미지 삭제");
            params.addNull().add(request.getParam("id"));
            updateQuery=query.getUpdate2();
		
        }else if(request.getFormAttribute("action").equals("insertImage")) {
        	System.out.println("이미지 업로드");
        	updateQuery=query.getUpdate2();

            String fileName = request.getFormAttribute("fileName");
            Set<FileUpload> uploads = routingContext.fileUploads();
            for(FileUpload upload : uploads) {
                File uploadedFile = new File(upload.uploadedFileName());
                uploadedFile.renameTo(new File("files/" +request.getParam("id")+"_"+upload.fileName()));
                try {
                    uploadedFile.createNewFile();
                }
                    catch (Exception e1) {
                }
                new File(upload.uploadedFileName()).delete();
            }
            params.add("http://localhost:8080/api/image?fileName="+request.getParam("id")+"_"+fileName).add(request.getParam("id"));
        }
        
        con.updateWithParams(updateQuery, params, e -> {
			if(e.succeeded()) {
				con.query(query.getShareTodo(request.getParam("id")), e1 -> {
					if(e1.succeeded()) {
						ResultSet rs = e1.result();
						List<JsonObject> list = rs.getRows();
						routingContext.vertx().eventBus().publish("todos", list.get(0).toString());					
						routingContext.response().end();
					} else {
						System.out.println("퍼블리싱 쿼리 실행오류 ::"+e1.cause());
						routingContext.response().end();
					}
				});
			}else {
				System.out.println("수정쿼리 실행오류 ::"+e.cause());
				routingContext.response().end();
			}
        });
    }
	
	// Get Image
	public void getImage(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		String fileName = request.getParam("fileName");
		request.response().sendFile("files/"+fileName, 0, e ->{});
	}

}
