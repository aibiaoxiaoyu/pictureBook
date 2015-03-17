package com.kid.picturebook.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;


public class CompressPicUtil {
    private static final int MAX_IMAGE_HEIGHT = 768;
    private static final int MAX_IMAGE_WIDTH = 1024;
    public static final int MAX_IMAGE_SIZE = 500 * 1024; // max 500k
	
	public static long compressImage(Context context, String filePath,
			String newFilePath, int q) throws FileNotFoundException {

		Bitmap bm = zipPicture(context, Uri.fromFile(new File(filePath)));

		int degree = ImageUtil.readPictureDegree(filePath);

		if (degree != 0) {
			bm = ImageUtil.rotatePicture(bm, degree);
		}

		File outputFile = new File(newFilePath);

		FileOutputStream out = new FileOutputStream(outputFile);

		bm.compress(Bitmap.CompressFormat.JPEG, q, out);
		
		if(out != null){
		    try{
		        out.close();
		    } catch(Exception e){
		        e.printStackTrace();
		    }
		}
		return outputFile.length();

	}

	public static Bitmap zipPicture(Context context, Uri picUri) {
		if(picUri == null){
			return null;
		}
		
		final UriImage uriImage = new UriImage(context, picUri);
		Bitmap pic = null;
		final byte[] result = uriImage.getResizedImageData(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT, MAX_IMAGE_SIZE);
		if (result == null) {
			//Log.e("XXX", "Fail to zip picture, the original size is " + width+" * "+ height);
			return null;
		}
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inPurgeable = true;
		//option.inSampleSize = 4; //too aggressive: the bitmap will be uploaded to server, not the the thumbnail
		pic = BitmapFactory.decodeByteArray(result, 0, result.length, option);
		return pic;
	}
}
