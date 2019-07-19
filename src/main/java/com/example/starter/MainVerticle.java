package com.example.starter;

import java.util.HashSet;
import java.util.Set;


import com.example.db.DataSourceConfig;
import com.example.service.MatadataService;
import com.example.service.TodoListService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		System.out.println("Vert.x is run...");

		TodoListService service = new TodoListService();
		MatadataService mservice = new MatadataService();
		Router router = Router.router(vertx); // vert.x 라우터연결
		HttpServer server = vertx.createHttpServer(); // vert.x 서버 생성
		DataSourceConfig ds = new DataSourceConfig();

		SQLClient con = JDBCClient.createShared(vertx, ds.getConfig()).getConnection(res -> {
			if (res.succeeded()) {
				System.out.println("접속성공");
			} else {
				System.out.println("접속실패");
			}
		}); // SQL Connection 생성

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

		router.route().handler(BodyHandler.create().setUploadsDirectory("files"));
		
		router.route().handler(CorsHandler.create("*") // Cors handler
				.allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

		// Mapping주소 "/" get method REST API
		router.get("/").handler(routingContext -> {
			service.getTodoList(routingContext, con);
		});
		
		
		// Mapping주소 "/insert" Post method REST API
		router.post("/insert").handler(routingContext -> {
			service.insertTodo(routingContext, con);
		});

		// Mapping주소 "/checked" patch method REST API
		router.patch("/checked").handler(routingContext -> {
		//System.out.println("체크 들어옴");
			service.checkTodo(routingContext, con);
		});
		
		// Mapping주소 "/delete" delete method REST API
		router.delete("/delete").handler(routingContext -> {
		// System.out.println("딜리트 들어옴");
			service.deleteTodo(routingContext, con);
		});

		// Mapping주소 "/todoitem" patch method REST API
		router.patch("/todoitem").handler(routingContext -> {
		//			System.out.println("업데이트 들어옴");
			service.updateTodo(routingContext,con);
		});
		
		
		router.get("/metadata").handler(routingContext->{
			System.out.println("metadata 진입");
			mservice.getData(routingContext, con);
		});
		

		server.requestHandler(router).listen(8080);
	}

}
