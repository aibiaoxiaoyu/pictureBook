package com.kid.picturebook.entity;

public class BookContent {
	private int id;// id
	private int bookId;// 绘本id
	
	private int page = -1;// 该绘本的第几页
	private String path_pic;// 某页的图片地址
	private String path_click_audio;// 某页的点读语音地址
	private String path_bg_audio;// 某页的背景语音地址
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
	
	
	public String getPath_click_audio() {
		return path_click_audio;
	}

	public void setPath_click_audio(String path_click_audio) {
		this.path_click_audio = path_click_audio;
	}

	public String getPath_bg_audio() {
		return path_bg_audio;
	}

	public void setPath_bg_audio(String path_bg_audio) {
		this.path_bg_audio = path_bg_audio;
	}

	public String getDescribe() {
		return describe;
	}
	
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
}
