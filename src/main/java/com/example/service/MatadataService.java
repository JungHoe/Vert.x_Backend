package com.example.service;

import java.util.HashMap;
import java.util.Map;

import com.example.mapper.MetadataMapper;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.RoutingContext;

public class MatadataService {
	
	Opengraph open = new Opengraph();
	MetadataMapper query = new MetadataMapper();

	public void getData(RoutingContext routingContext, SQLClient con) {
		HttpServerResponse res = routingContext.response();
		HttpServerRequest req = routingContext.request();
		
		res.putHeader("content-type", "application/json");
		JsonArray params = new JsonArray().add(req.getParam("url"));
		
		con.queryWithParams(query.getGetData(), params, e ->{
			ResultSet rs = e.result();
			if(rs.getNumRows() == 1) {
				res.end(Json.encodePrettily(rs.getRows().get(0)));
			} else {
				con.updateWithParams(query.getInsertData(), open.getMetaData(req.getParam("url")), handler ->{
					if(handler.succeeded()) {
						con.queryWithParams(query.getGetData(), params, e3 -> {
							ResultSet rs2 = e3.result();
							res.end(Json.encodePrettily(rs2.getRows().get(0)));
						});
					}
				});
				
			}
		});
	};
	
}
