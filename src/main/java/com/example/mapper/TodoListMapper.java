package com.example.mapper;

public class TodoListMapper {

	private final String totalCnt = "select id from todoList2";
	private final String selectList = "select id,text,checked,color,moment,image from todoList2 where useYn='Y'";
	private final String insert = "insert into todoList2 (id,text,color,image) values (?,?,?,?)";
	private final String checked = "update todoList2 set checked = ? where id = ?";
	private final String delete = "update todoList2 set useYn = 'N' where id = ?";
	private final String update1 = "update todoList2 set text = ? , color = ? , checked = ? where id = ?";
	private final String update2 = "update todoList2 set text = ? , color = ? , checked = ?, image = ? where id = ?";
	private final String todosList = "select id, text, checked, color, moment, useYn, image from todoList2";
	private String shareTodo = "select id, text, checked, color, moment, useYn, image from todoList2 where id =";

	
	public String getInsert() {
		return insert;
	}

	public String getChecked() {
		return checked;
	}

	public String getDelete() {
		return delete;
	}

	public String getUpdate1() {
		return update1;
	}
	
	public String getUpdate2() {
		return update2;
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

	public String getShareTodo(String id) {
		return shareTodo+id;
	}

}
