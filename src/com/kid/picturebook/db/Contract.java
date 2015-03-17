package com.kid.picturebook.db;

public class Contract {
	public static class PictureBookContract {
		public static final String TABLE_NAME = "picturebooks";
		
		public static final String _ID = "_id";
		public static final String _TITLE = "_title";// 绘本名称
		public static final String _CREATE_TIME = "_create_time";// 绘本创建日期
		
	}
	
	public static class BookContentContract {
		public static final String TABLE_NAME = "bookcontent";
		public static final String _ID = "_id";
		public static final String _BOOK_ID = "_book_id";// 绘本id
		public static final String _PAGE = "_page";// 该绘本的第几页
		public static final String _PATH_PIC = "_path_pic";// 某页的图片地址
		public static final String _PATH_AUDIO = "_path_audio";// 某页的语音地址
		public static final String _DESCRIBE = "_describe";// 某页的图片文字描述
		
	}
}
