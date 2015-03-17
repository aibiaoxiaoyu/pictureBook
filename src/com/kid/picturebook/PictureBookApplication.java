package com.kid.picturebook;

import java.io.File;

import android.app.Application;

import com.kid.picturebook.utils.CommonUtils;
import com.kid.picturebook.utils.Constants;

public class PictureBookApplication extends Application {
	private static PictureBookApplication mAppInstance = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}
	
	// ��ʼ��
	private void init() {
		mAppInstance = this;
		configurePath();
	}
	
	public static PictureBookApplication getInstatce() {
		return mAppInstance;
	}
	
	/**
	 * @��������configurePath
	 * @�������½��ļ���
	 * @�����void
	 */
	private void configurePath() {
		String rootDir = CommonUtils.getDataDir(getApplicationContext());
		Constants.PICTUREBOOK_PATH = rootDir + Constants.EXTRA_PARENT_DIR;
		Constants.PICTUREBOOK_PATH_MYBOOKS = Constants.PICTUREBOOK_PATH + "/myBooks";
		File file = new File(Constants.PICTUREBOOK_PATH);
		file.mkdirs();
		file = new File(Constants.PICTUREBOOK_PATH_MYBOOKS);
		file.mkdirs();
	}
	
}
