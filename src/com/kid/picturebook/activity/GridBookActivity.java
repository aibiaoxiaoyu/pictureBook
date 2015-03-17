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
	
	// �̳�BaseActivity���´�ҳ��ʱ��Ĭ��ִ�и�����onCreate()������Ȼ�������ִ��initView()��initDate()
	@Override
	public void initView() {
		
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_gridbook);
		gridView = (GridView)findViewById(R.id.gridView);
	}
	
	@Override
	public void initDate() {
		// TODO Auto-generated method stub
		// ���ɶ�̬���飬����ת������
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		for(int i = 0; i < 2; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(ITEMIMAGE, R.drawable.input_book);// ���ͼ����Դ��ID
			map.put(ITEMNAME, "NO." + String.valueOf(i));// �������ItemText
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
