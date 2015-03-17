package com.kid.picturebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kid.picturebook.R;

/**
 * @������com.kid.picturebook.activity
 * @������HomeActivity
 * @������������
 * @ʱ�䣺2015��3��12������2:13:03
 * @�汾��1.0.0
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
	
	// ������ҵĻ汾����ť�����ҵĻ汾���棨onMine����activity_home.xml�ļ��ж�Ӧ��ť��android:onclick�����õģ�
	public void onMine(View v) {
		startActivity(new Intent(HomeActivity.this, GridBookActivity.class));
	}
	
	// �����¼��汾����ť����¼��汾���棨onInput����activity_home.xml�ļ��ж�Ӧ��ť��android:onclick�����õģ�
	public void onInput(View v) {
		startActivity(new Intent(HomeActivity.this, RecordActivity.class));
		
	}
	
	// �������������ť���򿪰������棨onHelp����activity_home.xml�ļ��Ķ�Ӧ��ť��android:onclick�����õģ�
	public void onHelp(View v) {
		startActivity(new Intent(HomeActivity.this, HelpActivity.class));
		
	}
	
}
