package com.kid.picturebook.adapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import com.kid.picturebook.R;
import com.kid.picturebook.entity.BookContent;
import com.kid.picturebook.entity.PictureBook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GridBookAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	List<PictureBook> data;
	
	public GridBookAdapter(Context context, List<PictureBook> data) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.inflater = LayoutInflater.from(this.context);
		this.data = data;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PictureBook picbook = data.get(position);
		convertView = inflater.inflate(R.layout.item_book, null);
		ImageView img = (ImageView)(convertView.findViewById(R.id.book_img));
		TextView text = (TextView)(convertView.findViewById(R.id.book_name));
		text.setText(picbook.getTitle());
		List<BookContent> mList = picbook.getBookContentList();
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
		return convertView;
		
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
