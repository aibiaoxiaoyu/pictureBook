package com.kid.picturebook.utils;

import com.kid.picturebook.PictureBookApplication;

import android.content.Context;
import android.database.Cursor;
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
}
