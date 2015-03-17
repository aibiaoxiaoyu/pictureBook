package com.kid.picturebook.activity;

import android.content.Intent;
import android.view.View;

import com.kid.picturebook.R;

public class ViewerActivity extends BaseActivity {
	
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_viewer);
	}
	
	@Override
	public void initDate() {
		// TODO Auto-generated method stub
		
	}
	
	public void onHome(View v) {
		startActivity(new Intent(ViewerActivity.this, HomeActivity.class));
		finish();
	}
	
}
