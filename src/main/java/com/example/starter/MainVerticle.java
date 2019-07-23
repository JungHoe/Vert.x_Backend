package com.example.starter;

import com.example.db.DataSourceConfig;
import com.example.service.MatadataService;
import com.example.service.TodoListService;

import io.vertx.core.AbstractVerticle;
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

@SuppressWarnings("deprecation")
public class MainVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		System.out.println("Vert.x is run...");
		
		Router router = Router.router(vertx); 
		HttpServer server = vertx.createHttpServer(); 
		
		DataSourceConfig ds = new DataSourceConfig();
		SQLClient con = JDBCClient.createShared(vertx, ds.getConfig()).getConnection(res -> {
			if (res.succeeded()) {
				System.out.println("DB connect Success");
			} else {
				System.out.println("DB Connect Fail");
			}
		});
		
		router.route("/eventbus/*").handler(eventBusHandler());
		router.mountSubRouter("/api", ApiRouter(con));
		server.requestHandler(router).listen(8080);
	}
	
	
	private Router ApiRouter(SQLClient con) throws Exception {	
		MatadataService mService = new MatadataService();
		TodoListService tService = new TodoListService();
		
		Router router = Router.router(vertx);
		router.route().handler(CorsHandler.create("*").allowedHeader("Access-Control-Allow-Origin"));
		router.route().handler(BodyHandler.create().setUploadsDirectory("files"));
        router.route().consumes("application/json");
        router.route().produces("application/json");
        
        // Get Todos
		router.get("/get").handler(routingContext -> {
			tService.getTodoList(routingContext, con);
		});
		
		// Insert Todo
		router.post("/insert").handler(routingContext -> {
			tService.insertTodo(routingContext, con);
		});

		// Checked Todo
		router.post("/checked").handler(routingContext -> {
			tService.checkTodo(routingContext, con);
		});
		
		// Delete Todo
		router.post("/delete").handler(routingContext -> {
			tService.deleteTodo(routingContext, con);
		});
		
		// Update Todo
		router.post("/todoitem").handler(routingContext -> {
			tService.updateTodo(routingContext,con);
		});
		
		// Get Opengraph
		router.get("/metadata").handler(routingContext->{
			mService.getData(routingContext, con);
		});
		
		// Get Image
		router.get("/image").handler(routingContext->{
			tService.getImage(routingContext, con);
		});		
        
		return router;
	}
	
	// SockJS EventBus Handler - Socket Create
	private SockJSHandler eventBusHandler() {
		BridgeOptions options = new BridgeOptions()
				
				.addOutboundPermitted(new PermittedOptions().setAddress("todos"));
        return SockJSHandler.create(vertx).bridge(options, event -> {
            if (event.type() == BridgeEventType.SOCKET_CREATED) {
            	//System.out.println("socket created");
            }
            event.complete(true);
        });
	}
	
}