package com.kid.picturebook.entity;

public class BookContent {
	private int id;// id
	private int bookId;// 绘本id
	
	private int page = -1;// 该绘本的第几页
	private String path_pic;// 某页的图片地址
	private String path_audio;// 某页的语音地址
	private String describe;// 某页的图片文字描述
	
	public BookContent(int bookId) {
		super();
		this.bookId = bookId;
	}
	
	public int getBookId() {
		return bookId;
	}
	
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getPage() {
		return page;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public String getPath_pic() {
		return path_pic;
	}
	
	public void setPath_pic(String path_pic) {
		this.path_pic = path_pic;
	}
	
	public String getPath_audio() {
		return path_audio;
	}
	
	public void setPath_audio(String path_audio) {
		this.path_audio = path_audio;
	}
	
	public String getDescribe() {
		return describe;
	}
	
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
}
