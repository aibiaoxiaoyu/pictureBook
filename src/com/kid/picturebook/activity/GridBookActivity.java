package com.kid.picturebook.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.kid.picturebook.R;
import com.kid.picturebook.adapter.GridBookAdapter;
import com.kid.picturebook.dealdate.DataHandle;
import com.kid.picturebook.entity.PictureBook;

// �ҵĻ汾����
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
		final ArrayList<PictureBook> lstImageItem = new ArrayList<PictureBook>();
		Map<Integer, PictureBook> mMapPictureBook = DataHandle.getInstance().getAllBooks();
		Iterator<Entry<Integer, PictureBook>> entryKeyIterator = mMapPictureBook.entrySet().iterator();
		while(entryKeyIterator.hasNext()) {
			Entry<Integer, PictureBook> e = entryKeyIterator.next();
			PictureBook value = e.getValue();
			lstImageItem.add(value);
		}
		PictureBook exp = new PictureBook("ʾ��", 0);
		exp.setId(-1);
		lstImageItem.add(0, exp);
		adapter = new GridBookAdapter(GridBookActivity.this, lstImageItem);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GridBookActivity.this, ViewerActivity.class);
				intent.putExtra("bookId", lstImageItem.get(position).getId());
				startActivity(intent);
			}
		});
		
	}
}
