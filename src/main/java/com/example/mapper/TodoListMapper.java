package com.example.mapper;

public class TodoListMapper {

	private final String totalCnt = "select id from todoList1";
	private final String selectList = "select id,text,checked,color,moment from todoList1 where useYn='Y'";
	private final String insert = "insert into todoList1 (id,text,color) values (?,?,?)";
	private final String checked = "update todoList1 set checked = ? where id = ?";
	private final String delete = "update todoList1 set useYn = 'N' where id = ?";
	private final String update = "update todoList1 set text = ? , color = ? , checked = ? where id = ?";

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

}
