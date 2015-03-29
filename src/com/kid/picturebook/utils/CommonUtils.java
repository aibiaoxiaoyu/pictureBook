package com.kid.picturebook.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import com.kid.picturebook.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class CommonUtils {
	public static String getDataDir(Context context) {
		String state = Environment.getExternalStorageState();
		String dir = null;
		if(Environment.MEDIA_MOUNTED.equals(state)) {
			dir = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		else {
			dir = context.getFilesDir().getAbsolutePath();
		}
		return dir;
	}
	
	public static String getRealPathFromURI(Context mContext, Uri contentUri) {
		String res = null;
		String[] proj = {MediaStore.Images.Media.DATA };
		Cursor cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
		if(cursor != null) {
			if(cursor.moveToFirst()) {
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				res = cursor.getString(column_index);
			}
			cursor.close();
		}
		return res;
	}
	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片
			
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 把APK的资源文件copy到SD卡下的实现。 /*
	// * 将raw里的文件copy到sd卡下
	// * */
	public static void copyResToSdcard(Context context) {
		Field[] raw = R.raw.class.getFields();
		for(Field r : raw) {
			try {
				int id = context.getResources().getIdentifier(r.getName(), "raw", context.getPackageName());
				String path = Constants.PICTUREBOOK_PATH + "/" + r.getName() + ".mp3";
				BufferedOutputStream bufEcrivain = new BufferedOutputStream((new FileOutputStream(new File(path))));
				BufferedInputStream VideoReader = new BufferedInputStream(context.getResources().openRawResource(id));
				byte[] buff = new byte[20 * 1024];
				int len;
				while((len = VideoReader.read(buff)) > 0) {
					bufEcrivain.write(buff, 0, len);
				}
				bufEcrivain.flush();
				bufEcrivain.close();
				VideoReader.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
