package com.kid.picturebook.entity;

import java.util.List;

public class PictureBook {
	private int id;
	
	private String title;// 绘本名称
	private String create_time;// 绘本创建日期
	private List<BookContent> bookContentList;
	
	public PictureBook(String title) {
		this.title = title;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public List<BookContent> getBookContentList() {
		return bookContentList;
	}
	
	public void setBookContentList(List<BookContent> bookContentList) {
		this.bookContentList = bookContentList;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCreate_time() {
		return create_time;
	}
	
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		PictureBook other = (PictureBook)obj;
		if(id != other.id)
			return false;
		return true;
	}
	
}
