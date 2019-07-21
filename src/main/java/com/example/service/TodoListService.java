package com.example.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
		});
		
		response.end(Json.encodePrettily(result));
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
		
		// File Upload ::: RenameFile = Save, OriginFile = Delete 
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
		
		// uploadFile Null Check
		if(fileName == null) {
			params.addNull();
			pubData.put("image", "null");
		} else {
			params.add("http://localhost:8080/api/image?fileName="+id+"_"+fileName);
			pubData.put("image", "http://localhost:8080/api/image?fileName="+id+"_"+fileName);
		}
		
		// Save Todo in DB
		con.updateWithParams(query.getInsert(), params, e -> {});
		
		// Publish Insert data to another Socket
		routingContext.vertx().eventBus().publish("todos", pubData.toString());
		routingContext.response().end();
	}

	// Checked Todo
	public void checkTodo(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		
		JsonArray params = new JsonArray()
				.add(request.getParam("checked"))
				.add(request.getParam("id"));
		
		con.updateWithParams(query.getChecked(), params, e -> {});
	}

	// Delete Todo
	public void deleteTodo(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		JsonArray params = new JsonArray() // update parameters 생성
				.add(request.getParam("id"));
		con.updateWithParams(query.getDelete(), params, e -> {});
	}

	// Update Todo
	public void updateTodo(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		JsonArray params = new JsonArray() // update parameters 생성
				.add(request.getParam("text")).add(request.getParam("color")).add(request.getParam("checked"))
				.add(request.getParam("id"));
		con.updateWithParams(query.getUpdate(), params, e -> {});
	}
	
	// Get Image
	public void getImage(RoutingContext routingContext, SQLClient con) {
		HttpServerRequest request = routingContext.request();
		String fileName = request.getParam("fileName");
		request.response().sendFile("files/"+fileName, 0, e ->{});
	}

}
