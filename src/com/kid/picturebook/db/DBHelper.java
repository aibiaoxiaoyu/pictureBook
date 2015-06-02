package com.kid.picturebook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kid.picturebook.PictureBookApplication;
import com.kid.picturebook.db.Contract.BookContentContract;
import com.kid.picturebook.db.Contract.PictureBookContract;

public class DBHelper extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "db_picturebook";
	private final static int DATABASE_VERSION = 4;
	private static DBHelper dbHelper;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建数据库
		// TODO Auto-generated method stub
		String sql = "Create table " + PictureBookContract.TABLE_NAME + "(" + PictureBookContract._ID + " integer primary key autoincrement, "
				+ PictureBookContract._TITLE + " text, " + PictureBookContract._CREATE_TIME + " text );";
		db.execSQL(sql);
		sql = "Create table " + BookContentContract.TABLE_NAME + "(" + BookContentContract._ID + " integer primary key autoincrement, "
				+ BookContentContract._BOOK_ID + " text, " + BookContentContract._DESCRIBE + " text, " 
				+ BookContentContract._PATH_BG_AUDIO + " text,"
				+ BookContentContract._PATH_CLICK_AUDIO + " text,"
				+ BookContentContract._PATH_PIC + " text," + BookContentContract._AUDIO_TYPE + " integer," + BookContentContract._PAGE + " text );";
		db.execSQL(sql);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// 升级数据库
		String sql = " DROP TABLE IF EXISTS " + PictureBookContract.TABLE_NAME;
		db.execSQL(sql);
		sql = " DROP TABLE IF EXISTS " + BookContentContract.TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}
	
	public static DBHelper getInstance() {
		if(dbHelper == null)
			dbHelper = new DBHelper(PictureBookApplication.getInstatce());
		return dbHelper;
	}
	
	public Cursor select(String tableName) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, null, null, null, null, " _id desc");
		return cursor;
	}
	
	public Cursor select(String tableName, int bookId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, "_id=?", new String[] {bookId + "" }, null, null, " _id desc");
		return cursor;
	}
	
	/**
	 * @方法名：insert
	 * @描述：插入数据库
	 * @param tableName
	 * @param cv
	 * @return
	 * @输出：long
	 */
	public long insert(String tableName, ContentValues cv) {
		SQLiteDatabase db = this.getWritableDatabase();
		long row = db.insert(tableName, null, cv);
		return row;
	}
	
	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = PictureBookContract._ID + "=?";
		String[] whereValue = {Integer.toString(id) };
		db.delete(PictureBookContract.TABLE_NAME, where, whereValue);
	}
	
	public void delete(String tableName, int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = " _id =?";
		String[] whereValue = {Integer.toString(id) };
		db.delete(tableName, where, whereValue);
	}
	
	public void isExistPathAudio(String tableName, int id, ContentValues cv) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, "_id=?", new String[] {id + "" }, null, null, " _id desc");
	}
	
	/**
	 * @方法名：update
	 * @描述：更新数据库
	 * @param tableName
	 * @param id
	 * @param cv
	 * @输出：void
	 */
	public void update(String tableName, int id, int type, ContentValues cv) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "_id" + "=? and _audio_type=?";
		String[] whereValue = {Integer.toString(id), "" + type };
		db.update(tableName, cv, where, whereValue);
	}
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
