package com.kid.picturebook.activity;

import com.kid.picturebook.R;
import com.kid.picturebook.R.id;
import com.kid.picturebook.R.layout;
import com.kid.picturebook.R.menu;
import com.kid.picturebook.dealdate.DataHandle;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class FlashActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flash);
		
	}
	
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		// 2s后页面自动消失，跳到主界面
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				startActivity(new Intent(FlashActivity.this, HomeActivity.class));
				finish();
				
			}
		}, 2000);
	}
	
	@Override
	public void initDate() {
		// TODO Auto-generated method stub
		DataHandle.getInstance().initAllPictureBooks();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
