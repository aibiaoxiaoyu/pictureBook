package com.kid.picturebook.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {
	// ��Ϊ����ҳ��Ļ��࣬�´�ҳ��ʱ��Ĭ��ִ��onCreate()������Ȼ�������ִ��initView()��initDate()����������ֻ����д��������������
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initDate();
	}
	
	public abstract void initView();// ��ʼ���ؼ�
	
	public abstract void initDate();// ��ʼ������
	
	// ��������ء���ť����onBack���ڶ�Ӧxml�ļ��ķ��ذ�ť��android:onclick�����õģ�
	public void onBack(View v) {
		finish();
	}
	
	// ������ʾ��
	public void showToast(String text) {
		Toast.makeText(getApplicationContext(), text, 0).show();
	}
}
