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
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		System.out.println("Vert.x is run...");
		
		Router router = Router.router(vertx); // vert.x 라우터연결
		HttpServer server = vertx.createHttpServer(); // vert.x 서버 생성

		DataSourceConfig ds = new DataSourceConfig(); // DB connection
		SQLClient con = JDBCClient.createShared(vertx, ds.getConfig()).getConnection(res -> {
			if (res.succeeded()) {
				System.out.println("DB connection Succes");
			} else {
				System.out.println("DB Connection Failed");
			}
		});
		
		router.route("/eventbus/*").handler(eventBusHandler());
		router.mountSubRouter("/api", ApiRouter(con));
		server.requestHandler(router).listen(8080);
	}
	
	
	private Router ApiRouter(SQLClient con) {	
		MatadataService mService = new MatadataService();
		TodoListService tService = new TodoListService();
		
		Router router = Router.router(vertx);
		router.route().handler(CorsHandler.create("*").allowedHeader("Access-Control-Allow-Origin"));
		router.route().handler(BodyHandler.create().setUploadsDirectory("files"));
		
        router.route().consumes("application/json");
        router.route().produces("application/json");
            
             
		router.get("/get").handler(routingContext -> {
			tService.getTodoList(routingContext, con);
		});
		
		router.post("/insert").handler(routingContext -> {
			tService.insertTodo(routingContext, con);
		});

		router.patch("/checked").handler(routingContext -> {
			tService.checkTodo(routingContext, con);
		});
		
		router.delete("/delete").handler(routingContext -> {
			tService.deleteTodo(routingContext, con);
		});

		router.patch("/todoitem").handler(routingContext -> {
			tService.updateTodo(routingContext,con);
		});
		
		router.get("/metadata").handler(routingContext->{
			mService.getData(routingContext, con);
		});
		
		router.get("/image").handler(routingContext->{
			tService.getImage(routingContext, con);
		});		
        
		return router;
	}
	
	private SockJSHandler eventBusHandler() {
		BridgeOptions options = new BridgeOptions()
				.addOutboundPermitted(new PermittedOptions().setAddressRegex("todos"));
        return SockJSHandler.create(vertx).bridge(options, event -> {
            if (event.type() == BridgeEventType.SOCKET_CREATED) {
            	System.out.println("socket created");
            }
            event.complete(true);
        });
	}
	
}