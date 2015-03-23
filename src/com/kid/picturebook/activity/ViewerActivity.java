package com.kid.picturebook.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kid.picturebook.R;
import com.kid.picturebook.dealdate.DataHandle;
import com.kid.picturebook.entity.BookContent;
import com.kid.picturebook.entity.PictureBook;

public class ViewerActivity extends BaseActivity {
	private ImageView img;
	private int[] exps = new int[] {R.drawable.exp1, R.drawable.exp2, R.drawable.exp3 };
	private int bookId, page = 0;
	private PictureBook book;
	private List<BookContent> mList;
	private TextView view_page;
	private Button btn_play, btn_pause;
	private boolean isAutoPlay = true;
	Handler mHandler = new Handler();
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			boolean isEnd = showPage(++page);
			if(!isEnd) {
				mHandler.postDelayed(runnable, 3000);
			}
			else {
				mHandler.removeCallbacks(runnable);
				
			}
		}
	};
	
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_viewer);
		img = (ImageView)findViewById(R.id.img);
		view_page = (TextView)findViewById(R.id.view_page);
		btn_play = (Button)findViewById(R.id.play);
		btn_pause = (Button)findViewById(R.id.pause);
	}
	
	@Override
	public void initDate() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		bookId = intent.getIntExtra("bookId", -1);
		findViewById(R.id.btn_previous).setVisibility(View.GONE);
		
		showPage(page);
		
		mHandler.postDelayed(runnable, 3000);
	}
	
	/**
	 * @方法名：onHome
	 * @描述：回到主界面
	 * @param v
	 * @输出：void
	 */
	public void onHome(View v) {
		startActivity(new Intent(ViewerActivity.this, HomeActivity.class));
		finish();
	}
	
	// 显示下一页
	public void onNext(View v) {
		if(isAutoPlay) {
			btn_play.setVisibility(View.GONE);
			btn_pause.setVisibility(View.VISIBLE);
		}
		else {
			btn_play.setVisibility(View.VISIBLE);
			btn_pause.setVisibility(View.GONE);
		}
		findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_previous).setVisibility(View.VISIBLE);
		showPage(++page);
	}
	
	// 显示上一页
	public void onPrevious(View v) {
		if(isAutoPlay) {
			btn_play.setVisibility(View.GONE);
			btn_pause.setVisibility(View.VISIBLE);
			mHandler.removeCallbacks(runnable);
			mHandler.postDelayed(runnable, 3000);
		}
		else {
			btn_play.setVisibility(View.VISIBLE);
			btn_pause.setVisibility(View.GONE);
		}
		findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_previous).setVisibility(View.VISIBLE);
		showPage(--page);
	}
	
	// 点击自动播放
	public void onPlay(View v) {
		isAutoPlay = true;
		showToast("切换至自动播放");
		btn_pause.setVisibility(View.VISIBLE);
		btn_play.setVisibility(View.GONE);
		mHandler.postDelayed(runnable, 3000);
	}
	
	// 点击暂停，手动播放
	public void onPause(View v) {
		isAutoPlay = false;
		showToast("切换至手动播放");
		btn_play.setVisibility(View.VISIBLE);
		btn_pause.setVisibility(View.GONE);
		mHandler.removeCallbacks(runnable);
	}
	
	private boolean showPage(int page) {
		if(page < 0) {
			showToast("已达到第一页");
			this.page = 0;
			findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_previous).setVisibility(View.GONE);
			return true;
		}
		if(bookId == -1) {
			if(page >= exps.length) {
				showToast("已达到最后一页");
				this.page--;
				btn_play.setVisibility(View.GONE);
				btn_pause.setVisibility(View.GONE);
				findViewById(R.id.btn_previous).setVisibility(View.VISIBLE);
				findViewById(R.id.btn_next).setVisibility(View.GONE);
				return true;
			}
			
			view_page.setText((page + 1) + "");
			img.setBackgroundResource(exps[page]);
			return false;
		}
		book = DataHandle.getInstance().getPictureBookByBookId(bookId);
		mList = book.getBookContentList();
		if(mList != null && mList.size() > 0) {
			if(page > mList.size()) {
				btn_play.setVisibility(View.GONE);
				btn_pause.setVisibility(View.GONE);
				showToast("已达到最后一页");
				this.page--;
				return true;
			}
			
			if(mList.get(0).getPath_pic() == null) {
				img.setBackgroundResource(R.drawable.input_book);
				
			}
			else {
				
				img.setImageBitmap(getLoacalBitmap(mList.get(page).getPath_pic()));
			}
		}
		else {
			img.setBackgroundResource(R.drawable.input_book);
			
		}
		view_page.setText((page + 1) + "");
		return false;
	}
	
	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片
			
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
