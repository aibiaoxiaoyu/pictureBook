package com.kid.picturebook.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.kid.picturebook.R;
import com.kid.picturebook.adapter.GridBookAdapter;

public class GridBookActivity extends BaseActivity {
	private GridView gridView;
	private GridBookAdapter adapter;
	private final static String ITEMIMAGE = "ItemImage";
	private final static String ITEMNAME = "ItemName";
	
	// 继承BaseActivity，新打开页面时，默认执行父类中onCreate()方法，然后会依次执行initView()和initDate()
	@Override
	public void initView() {
		
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_gridbook);
		gridView = (GridView)findViewById(R.id.gridView);
	}
	
	@Override
	public void initDate() {
		// TODO Auto-generated method stub
		// 生成动态数组，并且转入数据
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		for(int i = 0; i < 2; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(ITEMIMAGE, R.drawable.input_book);// 添加图像资源的ID
			map.put(ITEMNAME, "NO." + String.valueOf(i));// 按序号做ItemText
			lstImageItem.add(map);
		}
		
		adapter = new GridBookAdapter(GridBookActivity.this, lstImageItem, R.layout.item_book, new String[] {ITEMIMAGE, ITEMNAME }, new int[] {
				R.id.book_img, R.id.book_name });
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				startActivity(new Intent(GridBookActivity.this, ViewerActivity.class));
			}
		});
		
	}
}
