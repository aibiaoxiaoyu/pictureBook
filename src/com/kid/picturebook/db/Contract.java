package com.kid.picturebook.db;

public class Contract {
	public static class PictureBookContract {
		public static final String TABLE_NAME = "picturebooks";
		
		public static final String _ID = "_id";
		public static final String _TITLE = "_title";// �汾����
		public static final String _CREATE_TIME = "_create_time";// �汾��������
		
	}
	
	public static class BookContentContract {
		public static final String TABLE_NAME = "bookcontent";
		public static final String _ID = "_id";
		public static final String _BOOK_ID = "_book_id";// �汾id
		public static final String _PAGE = "_page";// �û汾�ĵڼ�ҳ
		public static final String _PATH_PIC = "_path_pic";// ĳҳ��ͼƬ��ַ
		public static final String _PATH_BG_AUDIO = "_path_bg_audio";// ĳҳ�ı���������ַ
		public static final String _PATH_CLICK_AUDIO = "_path_click_audio";// ĳҳ�ĵ��������ַ
		public static final String _AUDIO_TYPE = "_audio_type";// �������ͣ��������ֻ��ǵ��
		public static final String _DESCRIBE = "_describe";// ĳҳ��ͼƬ��������
		
	}
}
