package com.example.service;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import io.vertx.core.json.JsonArray;

public class Opengraph {

	
	public JsonArray getMetaData(String url) {
		
		JsonArray result = new JsonArray();
				
		try {
			String connUrl = "https://"+url;
			Document doc = Jsoup.connect(connUrl).get();
			String title;
			String description;
			String image ;
			
			// og:title 
			Elements metaTitle = doc.getElementsByAttributeValue("property", "og:title");		
			if(metaTitle.attr("content").equals("")) {
				title=doc.select("title").text();
			}else{
				 title=metaTitle.attr("content");
			};
			
			// og:description
			Elements metaDescription = doc.getElementsByAttributeValue("property", "og:description");
			if(metaDescription.attr("content").equals("")) {
				description =  doc.getElementsByAttributeValue("name", "description").attr("content");
				if(description.equals("")) {
					description = "등록된 설명이 없습니다.";
				}
			}else {
				description	 = metaDescription.attr("content");
			}
			
			// og:image
			Elements metaImage = doc.getElementsByAttributeValue("property", "og:image");			
			if(metaImage.attr("content").equals("")) {
				image="https://user-images.githubusercontent.com/24848110/33519396-7e56363c-d79d-11e7-969b-09782f5ccbab.png";
			}else {
				image= metaImage.attr("content");
			}
			
			if(url.contains("youtube")) {
				image = "https://"+url+image;
			}
			
			// DB insert data setting
			 result.add(url);
			 result.add(title);
			 result.add(description);
			 result.add(image);
			
			System.out.println("---------- 크롤링 결과");
			System.out.println(url+", "+title+", "+description+", "+image);
			System.out.println("-----------------------");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
}
