package com.example.mapper;

public class TodoListMapper {

	private final String totalCnt = "select id from todoList2";
	private final String selectList = "select id,text,checked,color,moment,image from todoList2 where useYn='Y'";
	private final String insert = "insert into todoList2 (id,text,color,image) values (?,?,?,?)";
	private final String checked = "update todoList2 set checked = ? where id = ?";
	private final String delete = "update todoList2 set useYn = 'N' where id = ?";
	private final String update = "update todoList2 set text = ? , color = ? , checked = ? where id = ?";
	private final String todosList = "select id, text, checked, color, moment, useYn, image from todoList2";
	
	
	public String getInsert() {
		return insert;
	}

	public String getChecked() {
		return checked;
	}

	public String getDelete() {
		return delete;
	}

	public String getUpdate() {
		return update;
	}

	public String getSelectList() {
		return selectList;
	}

	public String getTotalCnt() {
		return totalCnt;
	}
	
	public String getTodosList() {
		return todosList;
	}
	

}
