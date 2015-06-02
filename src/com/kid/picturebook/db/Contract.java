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
		public static final String _PATH_BG_AUDIO = "_path_bg_audio";// 某页的背景语音地址
		public static final String _PATH_CLICK_AUDIO = "_path_click_audio";// 某页的点读语音地址
		public static final String _AUDIO_TYPE = "_audio_type";// 语音类型，背景音乐还是点读
		public static final String _DESCRIBE = "_describe";// 某页的图片文字描述
		
	}
}
