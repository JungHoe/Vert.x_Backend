package com.example.service;

import java.util.HashMap;
import java.util.Map;
import com.example.mapper.TodoListMapper;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.RoutingContext;

public class ServiceTest {
	TodoListMapper query = new TodoListMapper();

	public void getTodoList(RoutingContext routingContext, SQLClient con) {
		// TODO Auto-generated method stub 리스트가져옴
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
		// TODO Auto-generated method stub 보드생성
		HttpServerRequest request = routingContext.request();

		JsonArray params = new JsonArray().add(request.getParam("id")).add(request.getParam("text"))
				.add(request.getParam("color"));

		con.updateWithParams(query.getInsert(), params, e -> {
			// 필요시 핸들러 작성
//					UpdateResult updateResult = e.result();
//					System.out.println("No. of rows inserted: " + updateResult.getUpdated());
		});

	}

	public void checkTodo(RoutingContext routingContext, SQLClient con) {
		// TODO Auto-generated method stub 체크 함
		HttpServerRequest request = routingContext.request();

		JsonArray params = new JsonArray() // update parameters 생성
				.add(request.getParam("checked")).add(request.getParam("id"));

		con.updateWithParams(query.getChecked(), params, e -> {
			// 필요시 핸들러 작성
//					UpdateResult updateResult = e.result();
//					System.out.println("No. of rows updated: " + updateResult.getUpdated());

		});
	}

	public void deleteTodo(RoutingContext routingContext, SQLClient con) {
		// TODO Auto-generated method stub
		HttpServerRequest request = routingContext.request();

		JsonArray params = new JsonArray() // update parameters 생성
				.add(request.getParam("id"));
		con.updateWithParams(query.getDelete(), params, e -> {
			// 필요시 핸들러 작성
//			UpdateResult updateResult = e.result();
//			System.out.println("No. of rows updated: " + updateResult.getUpdated());
		});
		
	}

	public void updateTodo(RoutingContext routingContext, SQLClient con) {
		// TODO Auto-generated method stub
		HttpServerRequest request = routingContext.request();

		JsonArray params = new JsonArray() // update parameters 생성
				.add(request.getParam("text")).add(request.getParam("color")).add(request.getParam("checked"))
				.add(request.getParam("id"));
		con.updateWithParams(query.getUpdate(), params, e -> {
			// 필요시 핸들러 작성
//					UpdateResult updateResult = e.result();
//					System.out.println("No. of rows updated: " + updateResult.getUpdated());
		});

		
	}

}
