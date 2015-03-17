package com.kid.picturebook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kid.picturebook.PictureBookApplication;
import com.kid.picturebook.db.Contract.BookContent;
import com.kid.picturebook.db.Contract.PictureBook;

public class DBHelper extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "db_picturebook";
	private final static int DATABASE_VERSION = 1;
	private static DBHelper dbHelper;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// �������ݿ�
		// TODO Auto-generated method stub
		String sql = "Create table " + PictureBook.TABLE_NAME + "(" + PictureBook._ID + " integer primary key autoincrement, " + PictureBook._TITLE
				+ " text );";
		db.execSQL(sql);
		sql = "Create table " + BookContent.TABLE_NAME + "(" + BookContent._ID + " integer primary key autoincrement, " + BookContent._DESCRIBE
				+ " text, " + BookContent._BOOK_ID + " text, " + BookContent._PATH_AUDIO + " text," + BookContent._PATH_PIC + " text,"
				+ BookContent._PAGE + " text );";
		db.execSQL(sql);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// �������ݿ�
		String sql = " DROP TABLE IF EXISTS " + PictureBook.TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}
	
	public static DBHelper getInstance() {
		if(dbHelper == null)
			dbHelper = new DBHelper(PictureBookApplication.getInstatce());
		return dbHelper;
	}
	
	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(PictureBook.TABLE_NAME, null, null, null, null, null, " _id desc");
		return cursor;
	}
	
	/**
	 * @��������insert
	 * @�������������ݿ�
	 * @param tableName
	 * @param cv
	 * @return
	 * @�����long
	 */
	public long insert(String tableName, ContentValues cv) {
		SQLiteDatabase db = this.getWritableDatabase();
		long row = db.insert(tableName, null, cv);
		return row;
	}
	
	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = PictureBook._ID + "=?";
		String[] whereValue = {Integer.toString(id) };
		db.delete(PictureBook.TABLE_NAME, where, whereValue);
	}
	
	/**
	 * @��������update
	 * @�������������ݿ�
	 * @param tableName
	 * @param id
	 * @param cv
	 * @�����void
	 */
	public void update(String tableName, int id, ContentValues cv) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "_id" + "=?";
		String[] whereValue = {Integer.toString(id) };
		db.update(tableName, cv, where, whereValue);
	}
	
	// public void update(int id, String Title) {
	// SQLiteDatabase db = this.getWritableDatabase();
	// String where = PictureBook._ID + "=?";
	// String[] whereValue = {Integer.toString(id) };
	// ContentValues cv = new ContentValues();
	// cv.put(PictureBook._TITLE, Title);
	// db.update(PictureBook.TABLE_NAME, cv, where, whereValue);
	// }
	//
	// public long insert(String Title) {
	// SQLiteDatabase db = this.getWritableDatabase();
	// ContentValues cv = new ContentValues();
	// cv.put(PictureBook._TITLE, Title);
	// long row = db.insert(PictureBook.TABLE_NAME, null, cv);
	// return row;
	// }
	
}
