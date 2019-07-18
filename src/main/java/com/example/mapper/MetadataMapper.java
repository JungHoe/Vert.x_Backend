package com.example.mapper;

public class MetadataMapper {
	
	private final String getData = "SELECT url, title, description, image, saveTime FROM metaData WHERE url = ?";
	private final String insertData = "INSERT INTO metaData(url, title, description, image) VALUES( ?, ?, ?, ? )";
	private final String updateData = "UPDATE metaData SET title = ?, description = ?, image = #{image}, saveTime = ? WHERE url = ?";
	
	public String getGetData() {
		return getData;
	}
	public String getInsertData() {
		return insertData;
	}
	public String getUpdateData() {
		return updateData;
	}
	
}
