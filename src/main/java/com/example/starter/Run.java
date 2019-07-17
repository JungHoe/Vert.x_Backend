package com.example.starter;

import io.vertx.core.Vertx;

public class Run {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Vertx v = Vertx.vertx();
		v.deployVerticle(new MainVerticle());
	}

}
