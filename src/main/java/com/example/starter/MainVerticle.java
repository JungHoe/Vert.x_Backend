package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
	  System.out.println("run...");
	  HttpServer server = vertx.createHttpServer();
	  JsonObject config = new JsonObject()
			  .put("url", "jdbc:mysql://14.49.38.55:3306/kbsystodo?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=false")
			  .put("driver_class", "com.mysql.jdbc.Driver")
			  .put("user", "kbsystodo")
			  .put("password", "kbsystodo");

	  SQLClient client = JDBCClient.createShared(vertx, config);
	  
	  client.getConnection(res -> {
		  if (res.succeeded()) {
System.out.println("접속성공");
		    SQLConnection connection = res.result();

		    connection.query("select * from todoList1", res2 -> {
		      if (res2.succeeded()) {

		        ResultSet rs = res2.result();
		        System.out.println(rs.getResults());
		      }
		    });
		  } else {
			  System.out.println("접속실패");
		    // Failed to get connection - deal with it
		  }
		});

	  
	  Router router = Router.router(vertx);

	  router.route("/").handler(routingContext -> {
		  
	    HttpServerResponse response = routingContext.response();
	    response.putHeader("content-type", "text/html");

	    // Write to the response and end it
	    response.end("<h1>Home Page</h1>");
	  });
	  
	  router.route("/user/:id").handler(routingContext -> {
		  
		    HttpServerResponse response = routingContext.response();
		    response.putHeader("content-type", "text/html");
		    String id =routingContext.request().getParam("id");
		    // Write to the response and end it
		    response.end("ID is..."+id);
		  });
	  
	  router.get("/api/list").handler(this::getAll);
	
		  
	  server.requestHandler(router).listen(8080);
  }
  
  
  private void getAll(RoutingContext routingContext) {
	  routingContext.response()
	      .putHeader("content-type", "application/json; charset=utf-8")
	      .end(Json.encodePrettily(""));
	}
  
}
