package com.kid.picturebook.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PictureBook implements Serializable {
	private int id;
	
	private String title;// 绘本名称
	private long createTime;// 绘本创建日期
	private List<BookContent> bookContentList;
	
	public PictureBook(String title, long time) {
		this.title = title;
		this.createTime = time;
	}
	
	public long getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
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
	
	public void addBookContent(BookContent bookContent) {
		if(bookContentList == null) {
			bookContentList = new ArrayList<BookContent>();
		}
		if(!bookContentList.contains(bookContent))
			bookContentList.add(bookContent);
	}
	
	public void setBookContentList(List<BookContent> bookContentList) {
		this.bookContentList = bookContentList;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getBookDir() {
		return createTime + title;
	}
	
	public void setTitle(String title) {
		this.title = title;
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
