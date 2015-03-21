package com.kid.picturebook.dealdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.database.Cursor;

import com.kid.picturebook.db.Contract.BookContentContract;
import com.kid.picturebook.db.DBHelper;
import com.kid.picturebook.db.Contract.PictureBookContract;
import com.kid.picturebook.entity.BookContent;
import com.kid.picturebook.entity.PictureBook;

public class DataHandle {
	private static DataHandle instance;
	private Map<Integer, PictureBook> mMapPictureBook;
	private Map<Integer, List<BookContent>> mMapBookContents;
	
	public static DataHandle getInstance() {
		if(instance == null)
			instance = new DataHandle();
		return instance;
	}
	
	public void initAllPictureBooks() {
		if(mMapPictureBook == null) {
			DBHelper helper = DBHelper.getInstance();
			mMapPictureBook = new HashMap<Integer, PictureBook>();
			Cursor cursor = helper.select(PictureBookContract.TABLE_NAME);
			if(cursor != null) {
				while(cursor.moveToNext()) {
					PictureBook tem = new PictureBook(cursor.getString(cursor.getColumnIndex(PictureBookContract._TITLE)), cursor.getLong(cursor
							.getColumnIndex(PictureBookContract._CREATE_TIME)));
					tem.setId(cursor.getInt(cursor.getColumnIndex(PictureBookContract._ID)));
					mMapPictureBook.put(tem.getId(), tem);
					
				}
			}
			// Iterator<Entry<Integer, PictureBook>> entryKeyIterator =
			// mMapPictureBook.entrySet().iterator();
			// while(entryKeyIterator.hasNext()) {
			// Entry<Integer, PictureBook> e = entryKeyIterator.next();
			// PictureBook value = e.getValue();
			// }
			
			cursor = helper.select(BookContentContract.TABLE_NAME);
			if(cursor != null) {
				while(cursor.moveToNext()) {
					BookContent bookContent = new BookContent(cursor.getInt(cursor.getColumnIndex(BookContentContract._BOOK_ID)));
					bookContent.setPage(cursor.getInt(cursor.getColumnIndex(BookContentContract._PAGE)));
					bookContent.setPath_pic(cursor.getString(cursor.getColumnIndex(BookContentContract._PATH_PIC)));
					bookContent.setPath_audio(cursor.getString(cursor.getColumnIndex(BookContentContract._PATH_AUDIO)));
					bookContent.setId(cursor.getInt(cursor.getColumnIndex(BookContentContract._ID)));
					mMapPictureBook.get(bookContent.getBookId()).addBookContent(bookContent);
				}
			}
		}
		
	}
	
	public Map<Integer, PictureBook> getAllBooks() {
		if(mMapPictureBook == null) {
			initAllPictureBooks();
		}
		return mMapPictureBook;
	}
	
	public PictureBook getPictureBookByBookId(int id) {
		return mMapPictureBook.get(id);
	}
	
	public List<BookContent> getBookContentsByBookId(int id) {
		return mMapBookContents.get(id);
	}
	
	public void addPictureBook(PictureBook pictureBook) {
		if(mMapPictureBook == null) {
			mMapPictureBook = new HashMap<Integer, PictureBook>();
		}
		if(!mMapPictureBook.containsKey(pictureBook))
			mMapPictureBook.put(pictureBook.getId(), pictureBook);
		
	}
}
