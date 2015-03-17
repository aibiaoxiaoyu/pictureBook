package com.kid.picturebook.dealdate;

import java.util.ArrayList;
import java.util.List;

import com.kid.picturebook.db.DBHelper;
import com.kid.picturebook.entity.PictureBook;

public class DataHandle {
	private static DataHandle instance;
	private List<PictureBook> mList;
	
	public static DataHandle getInstance() {
		if(instance == null)
			instance = new DataHandle();
		return instance;
	}
	
	public List<PictureBook> getAllPictureBooks() {
		if(mList == null) {
			DBHelper helper = DBHelper.getInstance();
			mList = new ArrayList<PictureBook>();
			helper.select();
		}
		
		return mList;
	}
	
	public void addPictureBook(PictureBook pictureBook) {
		if(mList == null) {
			mList = new ArrayList<PictureBook>();
		}
		if(!mList.contains(pictureBook))
			mList.add(pictureBook);
	}
}
