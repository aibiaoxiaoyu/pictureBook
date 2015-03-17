package com.kid.picturebook.utils;

/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class UriImage {
    private static final String TAG = "Meeting-Weibo";

    private final Context mContext;
    private final Uri mUri;
    private String mContentType;
    private String mPath;
    private String mSrc;
    private int mWidth;
    private int mHeight;

    /**
     * The quality parameter which is used to compress JPEG images.
     */
    public static final int IMAGE_COMPRESSION_QUALITY = 70;
    /**
     * The minimum quality parameter which is used to compress JPEG images.
     */
    public static final int MINIMUM_IMAGE_COMPRESSION_QUALITY = 30;

    public UriImage(Context context, Uri uri) {
        if ((null == context) || (null == uri)) {
            throw new IllegalArgumentException();
        }

        String scheme = uri.getScheme();
        if (scheme.equals("content")) {
            initFromContentUri(context, uri);
        } else if (uri.getScheme().equals("file")) {
            initFromFile(context, uri);
        }

        mSrc = mPath.substring(mPath.lastIndexOf('/') + 1);

        // Some MMSCs appear to have problems with filenames
        // containing a space. So just replace them with
        // underscores in the name, which is typically not
        // visible to the user anyway.
        mSrc = mSrc.replace(' ', '_');

        mContext = context;
        mUri = uri;

        decodeBoundsInfo();
    }

    private void initFromFile(Context context, Uri uri) {
        mPath = uri.getPath();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension = MimeTypeMap.getFileExtensionFromUrl(mPath);
        if (TextUtils.isEmpty(extension)) {
            // getMimeTypeFromExtension() doesn't handle spaces in filenames nor
            // can it handle
            // urlEncoded strings. Let's try one last time at finding the
            // extension.
            int dotPos = mPath.lastIndexOf('.');
            if (0 <= dotPos) {
                extension = mPath.substring(dotPos + 1);
            }
        }
        mContentType = mimeTypeMap.getMimeTypeFromExtension(extension);
        // It's ok if mContentType is null. Eventually we'll show a toast
        // telling the
        // user the picture couldn't be attached.
    }

    private void initFromContentUri(Context context, Uri uri) {
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);

        if (c == null) {
            throw new IllegalArgumentException("Query on " + uri + " returns null result.");
        }

        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
                throw new IllegalArgumentException("Query on " + uri + " returns 0 or multiple rows.");
            }

            String filePath;
            filePath = c.getString(c.getColumnIndexOrThrow(Images.Media.DATA));
            mContentType = c.getString(c.getColumnIndexOrThrow(Images.Media.MIME_TYPE));

            mPath = filePath;
        } finally {
            c.close();
        }
    }

    private void decodeBoundsInfo() {
        InputStream input = null;
        try {
            input = mContext.getContentResolver().openInputStream(mUri);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, opt);
            mWidth = opt.outWidth;
            mHeight = opt.outHeight;
        } catch (FileNotFoundException e) {
            // Ignore
            //Log.e(TAG, "IOException caught while opening stream", e);
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    public String getContentType() {
        return mContentType;
    }

    public String getSrc() {
        return mSrc;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    private static final int NUMBER_OF_RESIZE_ATTEMPTS = 8;
    
    public int getSizeOfImage() {
        int size = Integer.MAX_VALUE;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = 1;
        InputStream input = null;
        try {
            input = mContext.getContentResolver().openInputStream(mUri);
            Bitmap b = BitmapFactory.decodeStream(input, null, options);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            b.compress(CompressFormat.JPEG, 100, os);
            size = os.size();
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

    public byte[] getResizedImageData(int widthLimit, int heightLimit, int byteLimit) {
        int outWidth = mWidth;
        int outHeight = mHeight;

        int scaleFactor = 1;
        //fix scaleFactor to one, use createScaledBitmap(...)
/*        while ((outWidth / scaleFactor > widthLimit) || (outHeight / scaleFactor > heightLimit)) {
            scaleFactor *= 2;
        }*/

/*        Log.d("XXX", "getResizedImageData: wlimit=" + widthLimit + ", hlimit=" + heightLimit + ", sizeLimit=" + byteLimit + ", mWidth=" + mWidth + ", mHeight=" + mHeight
                + ", initialScaleFactor=" + scaleFactor);*/

        InputStream input = null;
        try {
            ByteArrayOutputStream os = null;
            int attempts = 1;

            do {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = scaleFactor;
                input = mContext.getContentResolver().openInputStream(mUri);
                int quality = IMAGE_COMPRESSION_QUALITY;
                try {
                    Bitmap b = BitmapFactory.decodeStream(input, null, options);
                    if (b == null) {
                        Log.e("XXX", "Fail to decode original stream");
                        return null;
                    }
                    if (options.outWidth > widthLimit || options.outHeight > heightLimit) {
                        // The decoder does not support the inSampleSize option.
                        // Scale the bitmap using Bitmap library.
                        int scaledWidth = outWidth ;
                        int scaledHeight = outHeight ;
                        
                        float pivot = 1.0f;
                        if(options.outWidth > options.outHeight){
                            pivot = (float)options.outWidth/options.outHeight;
                            scaledWidth = widthLimit;
                            scaledHeight = (int)(scaledWidth/pivot);
                        } else {
                            pivot = (float)options.outHeight/options.outWidth;
                            scaledHeight = heightLimit;
                            scaledWidth = (int)(scaledHeight/pivot);
                        }
                        b = Bitmap.createScaledBitmap(b, scaledWidth, scaledHeight, false);
                        if (b == null) {
                            Log.e("XXX", "Fail to get scale bitmap");
                            return null;
                        }
                        
                        Log.d("XXX", "Scaled size:" + scaledWidth +" / " + scaledHeight);
                    }

                    // Compress the image into a JPG. Start with
                    // MessageUtils.IMAGE_COMPRESSION_QUALITY.
                    // In case that the image byte size is still too large
                    // reduce the quality in
                    // proportion to the desired byte size. Should the quality
                    // fall below
                    // MINIMUM_IMAGE_COMPRESSION_QUALITY skip a compression
                    // attempt and we will enter
                    // the next round with a smaller image to start with.
                    os = new ByteArrayOutputStream();
                    b.compress(CompressFormat.JPEG, quality, os);
                    int jpgFileSize = os.size();
                    if (jpgFileSize > byteLimit) {
                        int reducedQuality = quality * byteLimit / jpgFileSize;
                        if (reducedQuality >= MINIMUM_IMAGE_COMPRESSION_QUALITY) {
                            quality = reducedQuality;

                            Log.v(TAG, "getResizedImageData: compress(2) w/ quality=" + quality);

                            os = new ByteArrayOutputStream();
                            b.compress(CompressFormat.JPEG, quality, os);
                        }
                    }
                } catch (java.lang.OutOfMemoryError e) {
                    //Log.w(TAG, "getResizedImageData - image too big (OutOfMemoryError), will try " + " with smaller scale factor, cur scale factor: " + scaleFactor);
                    // fall through and keep trying with a smaller scale factor.
                    e.printStackTrace();
                    Log.e("XXX", "Out of memory when decode bitmap at quality of : " + quality);
                }

                //Log.v(TAG, "attempt=" + attempts + " size=" + (os == null ? 0 : os.size()) + " width=" + outWidth / scaleFactor + " height=" + outHeight / scaleFactor
//                        + " scaleFactor=" + scaleFactor + " quality=" + quality);

                scaleFactor *= 2;
                attempts++;
            } while ((os == null || os.size() > byteLimit) && attempts < NUMBER_OF_RESIZE_ATTEMPTS);
            Log.d("XXX", "^^^^^^^^^^^^^^^^^^Zipped picture size is " + os.size()/1024 + " K");
            return os == null ? null : os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    //Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }
}
