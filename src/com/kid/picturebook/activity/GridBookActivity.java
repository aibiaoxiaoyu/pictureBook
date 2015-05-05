package com.kid.picturebook.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.kid.picturebook.R;
import com.kid.picturebook.adapter.GridBookAdapter;
import com.kid.picturebook.dealdate.DataHandle;
import com.kid.picturebook.entity.PictureBook;

// 我的绘本界面
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
		final ArrayList<PictureBook> lstImageItem = new ArrayList<PictureBook>();
		Map<Integer, PictureBook> mMapPictureBook = DataHandle.getInstance().getAllBooks();
		Iterator<Entry<Integer, PictureBook>> entryKeyIterator = mMapPictureBook.entrySet().iterator();
		while(entryKeyIterator.hasNext()) {
			Entry<Integer, PictureBook> e = entryKeyIterator.next();
			PictureBook value = e.getValue();
			lstImageItem.add(value);
		}
		PictureBook exp = new PictureBook("示例", 0);
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
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				// TODO Auto-generated method stub
				final int pbid = lstImageItem.get(position).getId();
				if(pbid == -1) {
					return true;
				}
				final CharSequence[] items = {"删除该绘本" }; // 设置选择内容
				AlertDialog.Builder builder = new AlertDialog.Builder(GridBookActivity.this);
				builder.setItems(items, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						lstImageItem.remove(position);
						DataHandle.getInstance().deletePictureBook(pbid);
						adapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				});
				builder.show();
				return true;
			}
		});
		
	}
}
