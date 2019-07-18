package com.example.starter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.netty.handler.codec.http.HttpContentEncoder.Result;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		System.out.println("Vert.x is run...");

		Router router = Router.router(vertx); // vert.x 라우터연결
		HttpServer server = vertx.createHttpServer(); // vert.x 서버 생성
		JsonObject config = new JsonObject() // DataSource 객체 생성
				.put("url",
						"jdbc:mysql://14.49.38.55:3306/kbsystodo?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=false")
				.put("driver_class", "com.mysql.jdbc.Driver").put("user", "kbsystodo").put("password", "kbsystodo");

		SQLClient client = JDBCClient.createShared(vertx, config); // SQL Client 생성

		Set<String> allowedHeaders = new HashSet<>(); // CORS
		allowedHeaders.add("x-requested-with");
		allowedHeaders.add("Access-Control-Allow-Origin");
		allowedHeaders.add("origin");
		allowedHeaders.add("Content-Type");
		allowedHeaders.add("accept");
		allowedHeaders.add("X-PINGARUNER");
		Set<HttpMethod> allowedMethods = new HashSet<>();
		allowedMethods.add(HttpMethod.GET);
		allowedMethods.add(HttpMethod.POST);
		allowedMethods.add(HttpMethod.DELETE);
		allowedMethods.add(HttpMethod.PATCH);
		router.route("/insert").handler(BodyHandler.create());
		router.route().handler(CorsHandler.create("*") // Cors handler
				.allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

		// Mapping주소 "/" get method REST API
		router.get("/").handler(routingContext -> {
			Map<String, Object> result = new HashMap<String, Object>(); // 결과 담는 객체
			HttpServerResponse response = routingContext.response();

			response.putHeader("content-type", "application/json"); // json 타입으로

			client.getConnection(res -> { // DB와연결
				if (res.succeeded()) { // 접속성공일시

					SQLConnection connection = res.result();
					connection.query("select id from todoList1 ", res2 -> { // TotalCnt 만드는 쿼리
						ResultSet rs = res2.result();
						result.put("totalCnt", rs.getNumRows());

					});

					connection.query("select id,text,checked,color," + "moment from todoList1 where useYn='Y' ",
							res3 -> { // 전체테이블 가져오는 쿼리
								ResultSet rs = res3.result();
								result.put("todoList", rs.getRows());
								response.end(Json.encodePrettily(result)); // result값을 리턴

							});
				} else {
					System.out.println("접속실패");
					// Failed to get connection - deal with it
				}
			});
		});
		// Mapping주소 "/insert" Post method REST API
		router.post("/insert").handler(routingContext -> {
//			System.out.println("insert 들어옴");
			HttpServerRequest request = routingContext.request();

			JsonArray params = new JsonArray().add(request.getParam("id")).add(request.getParam("text"))
					.add(request.getParam("color"));

			client.getConnection(res -> { // DB와연결
				if (res.succeeded()) { // 접속성공일시
					SQLConnection connection = res.result();
					connection.updateWithParams("insert into todoList1 (id,text,color) values (?,?,?)", params, e -> {
						// 필요시 핸들러 작성
//						UpdateResult updateResult = e.result();
//						System.out.println("No. of rows inserted: " + updateResult.getUpdated());
					});
				} else {

				}
			});

		});

		// Mapping주소 "/checked" patch method REST API
		router.patch("/checked").handler(routingContext -> {
//System.out.println("체크 들어옴");
			HttpServerRequest request = routingContext.request();

			JsonArray params = new JsonArray() // update parameters 생성
					.add(request.getParam("checked")).add(request.getParam("id"));

			client.getConnection(res -> { // DB와연결
				if (res.succeeded()) { // 접속성공일시

					SQLConnection connection = res.result();
					connection.updateWithParams("update todoList1 set checked = ? where id = ?", params, e -> {
						// 필요시 핸들러 작성
//						UpdateResult updateResult = e.result();
//						System.out.println("No. of rows updated: " + updateResult.getUpdated());
					});
				} else {

				}
			});

		});
		// Mapping주소 "/delete" delete method REST API
		router.delete("/delete").handler(routingContext -> {
//			System.out.println("딜리트 들어옴");
			HttpServerRequest request = routingContext.request();

			JsonArray params = new JsonArray() // update parameters 생성
					.add(request.getParam("id"));
			
			client.getConnection(res -> { // DB와연결
				if (res.succeeded()) { // 접속성공일시
					SQLConnection connection = res.result();
					connection.updateWithParams("update todoList1 set useYn = 'N' where id = ?", params, e -> {
						// 필요시 핸들러 작성
						UpdateResult updateResult = e.result();
						System.out.println("No. of rows updated: " + updateResult.getUpdated());
					});
				} else {

				}
			});

		});
		
		// Mapping주소 "/todoitem" patch method REST API
		router.patch("/todoitem").handler(routingContext -> {
			System.out.println("업데이트 들어옴");
			HttpServerRequest request = routingContext.request();

			JsonArray params = new JsonArray() // update parameters 생성
			.add(request.getParam("text")).add(request.getParam("color")).add(request.getParam("checked")).add(request.getParam("id"));

			client.getConnection(res -> { // DB와연결
				if (res.succeeded()) { // 접속성공일시
					
					SQLConnection connection = res.result();
					connection.updateWithParams("update todoList1 set text = ? , color = ? , checked = ? where id = ?", params, e -> {
						// 필요시 핸들러 작성
//						UpdateResult updateResult = e.result();
//						System.out.println("No. of rows updated: " + updateResult.getUpdated());
					});
				} else {

				}
			});

		});
		
		
		
		router.get("/metadata").handler(routingContext -> {
			System.out.println("metadata 진입");	
			HttpServerRequest request = routingContext.request();
			HttpServerResponse response = routingContext.response();
			
			response.putHeader("content-type", "application/json");
			
			JsonArray params = new JsonArray().add(request.getParam("url"));
			Map<String, Object> result = new HashMap<String, Object>();

			client.getConnection(res->{
				if(res.succeeded()) {
					SQLConnection connection = res.result();
					connection.queryWithParams("SELECT url, title, description, image, saveTime FROM metaData WHERE url = ?", params, 
							e -> {
								ResultSet rs = e.result();
								
								System.out.println(rs.getNext());
								
							});
					
					
				} else {
					
				}
				
				
			});
			
		});
		

		server.requestHandler(router).listen(8080);
	}

}
