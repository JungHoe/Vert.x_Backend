package com.example.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.example.mapper.TodoListMapper;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

public class TodoListService {
	TodoListMapper query = new TodoListMapper();

	public void getTodoList(RoutingContext routingContext, SQLClient con) {
		Map<String, Object> result = new HashMap<String, Object>(); // 결과 담는 객체
		HttpServerResponse response = routingContext.response();

		response.putHeader("content-type", "application/json"); // json 타입으로

		con.query(query.getTotalCnt(), res2 -> { // TotalCnt 만드는 쿼리
			ResultSet rs = res2.result();
			result.put("totalCnt", rs.getNumRows());
		});
		
		con.query(query.getSelectList(), res3 -> { // 전체테이블가져오는 쿼리
			ResultSet rs = res3.result();
			result.put("todoList", rs.getRows());
			response.end(Json.encodePrettily(result)); // result값을 리턴
		});
	}

	public void insertTodo(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		System.out.println("진입");
		String id = routingContext.request().getFormAttribute("id");
		String fileName = routingContext.request().getFormAttribute("fileName");
			
		Set<FileUpload> uploads = routingContext.fileUploads();
		for(FileUpload upload : uploads) {
	        File uploadedFile = new File(upload.uploadedFileName());
	        uploadedFile.renameTo(new File("files/" +id+"_"+upload.fileName()));
	        try {
				uploadedFile.createNewFile();
			} 
	        	catch (Exception e1) {
			}
	        new File(upload.uploadedFileName()).delete();
		}
		
		JsonArray params = new JsonArray()
				.add(request.getParam("id"))
				.add(request.getParam("text"))
				.add(request.getParam("color"));
		
		if(fileName == null) {
			params.addNull();
		} else {
			params.add("http://localhost:8080/image?fileName="+id+"_"+fileName);
		}
		
		con.updateWithParams(query.getInsert(), params, e -> {});
	}

	public void checkTodo(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();

		JsonArray params = new JsonArray() // update parameters 생성
				.add(request.getParam("checked")).add(request.getParam("id"));

		con.updateWithParams(query.getChecked(), params, e -> {
		});
	}

	public void deleteTodo(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		JsonArray params = new JsonArray() // update parameters 생성
				.add(request.getParam("id"));
		con.updateWithParams(query.getDelete(), params, e -> {
		});
		
	}

	public void updateTodo(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		JsonArray params = new JsonArray() // update parameters 생성
				.add(request.getParam("text")).add(request.getParam("color")).add(request.getParam("checked"))
				.add(request.getParam("id"));
		con.updateWithParams(query.getUpdate(), params, e -> {
		});
	}
	
	public void getImage(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		String fileName = request.getParam("fileName");
		request.response().sendFile("files/"+fileName, 0, ev ->{
		});
	};

}
