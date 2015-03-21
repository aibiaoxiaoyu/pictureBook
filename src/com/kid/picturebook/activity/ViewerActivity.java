package com.kid.picturebook.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.view.View;
import android.widget.ImageView;

import com.kid.picturebook.R;
import com.kid.picturebook.entity.BookContent;
import com.kid.picturebook.entity.PictureBook;

public class ViewerActivity extends BaseActivity {
	private ImageView img;
	
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_viewer);
		img = (ImageView)findViewById(R.id.img);
	}
	
	@Override
	public void initDate() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		PictureBook book = intent.getIntExtra("bookId",-1);
		List<BookContent> mList = book.getBookContentList();
		if(mList != null && mList.size() > 0) {
			if(mList.get(0).getPath_pic() == null) {
				img.setBackgroundResource(R.drawable.input_book);
				
			}
			else {
				
				img.setImageBitmap(getLoacalBitmap(mList.get(0).getPath_pic()));
			}
		}
		else {
			img.setBackgroundResource(R.drawable.input_book);
			
		}
	}
	
	/**
	 * @方法名：onHome
	 * @描述：回到主界面
	 * @param v
	 * @输出：void
	 */
	public void onHome(View v) {
		startActivity(new Intent(ViewerActivity.this, HomeActivity.class));
		finish();
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
}
