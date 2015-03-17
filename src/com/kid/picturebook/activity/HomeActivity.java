package com.kid.picturebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kid.picturebook.R;

/**
 * @包名：com.kid.picturebook.activity
 * @类名：HomeActivity
 * @描述：主界面
 * @时间：2015年3月12日下午2:13:03
 * @版本：1.0.0
 * 
*/
public class HomeActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}
	
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void initDate() {
		// TODO Auto-generated method stub
		
	}
	
	// 点击“我的绘本”按钮，打开我的绘本界面（onMine是在activity_home.xml文件中对应按钮的android:onclick中设置的）
	public void onMine(View v) {
		startActivity(new Intent(HomeActivity.this, GridBookActivity.class));
	}
	
	// 点击“录入绘本”按钮，打开录入绘本界面（onInput是在activity_home.xml文件中对应按钮的android:onclick中设置的）
	public void onInput(View v) {
		startActivity(new Intent(HomeActivity.this, RecordActivity.class));
		
	}
	
	// 点击“帮助”按钮，打开帮助界面（onHelp是在activity_home.xml文件的对应按钮的android:onclick中设置的）
	public void onHelp(View v) {
		startActivity(new Intent(HomeActivity.this, HelpActivity.class));
		
	}
	
}
