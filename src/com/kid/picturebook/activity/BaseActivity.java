package com.kid.picturebook.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {
	// 作为所有页面的基类，新打开页面时，默认执行onCreate()方法，然后会依次执行initView()和initDate()，其他子类只需重写这两个方法即可
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initDate();
	}
	
	public abstract void initView();// 初始化控件
	
	public abstract void initDate();// 初始化数据
	
	// 点击“返回”按钮，（onBack是在对应xml文件的返回按钮的android:onclick中设置的）
	public void onBack(View v) {
		finish();
	}
	
	// 弹出提示框
	public void showToast(String text) {
		Toast.makeText(getApplicationContext(), text, 0).show();
	}
}
