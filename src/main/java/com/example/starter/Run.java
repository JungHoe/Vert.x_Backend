package com.example.starter;

import io.vertx.core.Vertx;

public class Run {

	public static void main(String[] args) {
		Vertx v = Vertx.vertx();
		v.deployVerticle(new MainVerticle());
	}

}
