package com.example.db;

import io.vertx.core.json.JsonObject;

public class DataSourceConfig {

	private JsonObject config;
	private String url;
	private String driver_class;
	private String user;
	private String password;

	public JsonObject getConfig() {
		return config;
	}

	public void setConfig(JsonObject config) {
		this.config = config;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriver_class() {
		return driver_class;
	}

	public void setDriver_class(String driver_class) {
		this.driver_class = driver_class;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "DataSourceConfig [config=" + config + ", url=" + url + ", driver_class=" + driver_class + ", user="
				+ user + ", password=" + password + "]";
	}

	public DataSourceConfig() {
		super();
		setUrl("jdbc:mysql://14.49.38.55:3306/kbsystodo?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=false");
		setDriver_class("com.mysql.jdbc.Driver");
		setUser("kbsystodo");
		setPassword("kbsystodo");
		setConfig(new JsonObject()
				.put("url",this.url)
				.put("driver_class",this.driver_class)
				.put("user",this.user)
				.put("password",this.password));
	}

}
