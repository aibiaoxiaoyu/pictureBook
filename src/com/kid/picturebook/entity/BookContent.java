package com.kid.picturebook.entity;

public class BookContent {
	private int id;// id
	private int bookId;// �汾id
	
	public int getBookId() {
		return bookId;
	}
	
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
	private int page;// �û汾�ĵڼ�ҳ
	private String path_pic;// ĳҳ��ͼƬ��ַ
	private String path_audio;// ĳҳ��������ַ
	private String describe;// ĳҳ��ͼƬ��������
	
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
