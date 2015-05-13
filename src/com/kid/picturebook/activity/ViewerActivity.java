package com.kid.picturebook.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kid.picturebook.R;
import com.kid.picturebook.dealdate.DataHandle;
import com.kid.picturebook.entity.BookContent;
import com.kid.picturebook.entity.PictureBook;
import com.kid.picturebook.utils.CommonUtils;
import com.kid.picturebook.utils.Constants;
import com.kid.picturebook.utils.VoiceRecorder;
import com.kid.picturebook.utils.VoiceRecorder.MediaPlayerCallback;

public class ViewerActivity extends BaseActivity {
	private static final int[] exps = new int[] {R.drawable.exp1, R.drawable.exp2, R.drawable.exp3 };
	private static final String[] expPaths=new String[] {Constants.PICTUREBOOK_PATH + "/exp1.mp3", Constants.PICTUREBOOK_PATH + "/exp2.mp3",
			Constants.PICTUREBOOK_PATH + "/exp3.mp3" };;
    private static final int DELAYTIME = 10000;
	private int bookId, page = 0;
	private PictureBook book;
	private List<BookContent> mList;
	private ImageView img;
	private TextView view_page;
	private Button btn_play, btn_pause;
	private VoiceRecorder voiceRecorder;
	private boolean isAutoPlay = true;
	private SharedPreferences sp;
	private static final String LASTX = "lastx";
	private static final String LASTY = "lasty";
	
	private String getLastX() {
		Log.e("getLastX:",(bookId == 0 ? "0" : bookId) + LASTX + (page == 0 ? "0" : page));
		return (bookId == 0 ? "0" : bookId) + LASTX + (page == 0 ? "0" : page);
	}
	
	private String getLastY() {
//		Log.e("getLastY:", (bookId == 0 ? "" : bookId) + LASTY + (page == 0 ? "0" : page));
		return (bookId == 0 ? "" : bookId) + LASTY + (page == 0 ? "0" : page);
	}
	private Handler mHandler = new Handler();
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			boolean isEnd = showPage(++page);
			if(!isEnd) {
				// 自动循环播放
				mHandler.postDelayed(runnable, DELAYTIME);
			}
			else {
				//取消循环
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
		voiceRecorder = new VoiceRecorder(ViewerActivity.this, null);
		book = DataHandle.getInstance().getPictureBookByBookId(bookId);	// 查找数据
		if(book!=null){
			mList = book.getBookContentList();
		}
		showPage(page);//显示第一页
		mHandler.postDelayed(runnable, DELAYTIME);//自动播放
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		img.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				setOnClicked(v, event);
				return true;
			}
		});
		
	}
	
	//回到主界面
	public void onHome(View v) {
		stopPlay();//停止播放
		startActivity(new Intent(ViewerActivity.this, HomeActivity.class));
		finish();
	}
	
	//停止播放
	private void stopPlay() {
		if(voiceRecorder != null && voiceRecorder.isPlaying) {
			voiceRecorder.stopPlayVoice();
		}
		mHandler.removeCallbacks(runnable);
	}
	
	 //点读功能
	public void setOnClicked(View v, MotionEvent event) {
		int lastx = sp.getInt(getLastX(), 0);
		int lasty = sp.getInt(getLastY(), 0);
		Log.e("","lastx:" + lastx + ",getRawX:"+event.getRawX() + "");
		Log.e("", "lasty:" + lasty + ",getRawY" + event.getRawY() + "");
		if(event.getRawX() > lastx && (lastx < (event.getRawX() + 180)) && event.getRawY() > lasty && (event.getRawY() < (lasty + 120))) {
			showToast("点读");
			Log.e("", "点读");
			showPage(page);
		}
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
			mHandler.postDelayed(runnable, DELAYTIME);
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
		mHandler.postDelayed(runnable, DELAYTIME);
	}
	
	// 点击暂停，手动播放
	public void onPause(View v) {
		isAutoPlay = false;
		showToast("切换至手动播放");
		btn_play.setVisibility(View.VISIBLE);
		btn_pause.setVisibility(View.GONE);
		mHandler.removeCallbacks(runnable);
	}
	//显示某页
	private boolean showPage(int page) {
		if(page < 0) {
			setViewWhenFirstOne();// 当点击上一页到第一页时，对布局进行设置
			return true;
		}
		if(bookId == -1) {
			return showExamplePage(page);//显示示例图片
		}
		showLocalPage(page);//显示我自己录入的绘本
		view_page.setText((page + 1) + "");
		return false;
	}
	
	//显示我自己录入的绘本
	private boolean showLocalPage(int page) {
		if(mList != null && mList.size() > 0) {
			int size = mList.size();
			if(page >= size) {
				setViewWhenLastOne(); // 当显示到最后一页时，对布局进行设置
				return true;
			}
			if(mList.get(0).getPath_pic() == null) {
				img.setBackgroundResource(R.drawable.input_book);//默认图片
			}
			else {
				if(mList.get(page).getPath_pic()==null){
					img.setBackgroundResource(R.drawable.input_book);//默认图片
				}else{
					img.setImageBitmap(CommonUtils.getLoacalBitmap(mList.get(page).getPath_pic()));
				}
			}
			if(voiceRecorder.isPlaying) {
				voiceRecorder.stopPlayVoice();
			}
			voiceRecorder.playVoice(mList.get(page).getPath_audio(),null);
		}
		else {
			img.setBackgroundResource(R.drawable.input_book);//默认图片
		}
		return false;
	}

	//显示例子图片
	private boolean showExamplePage(int page) {
		if(page >= exps.length) {
			setViewWhenLastOne();
			return true;
		}
		view_page.setText((page + 1) + "");
		img.setBackgroundResource(exps[page]);
		if(voiceRecorder.isPlaying) {
			voiceRecorder.stopPlayVoice();
		}
		voiceRecorder.playVoice(expPaths[page],null);
		return false;
	}
	
	// 当显示到最后一页时，对布局进行设置
	private void setViewWhenLastOne() {
		btn_play.setVisibility(View.GONE);
		btn_pause.setVisibility(View.GONE);
		findViewById(R.id.btn_previous).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_next).setVisibility(View.GONE);
//		showToast("已达到最后一页");
//		mHandler.removeCallbacks(runnable);
		this.page--;
	}
	
	// 当点击上一页到第一页时，对布局进行设置
	private void setViewWhenFirstOne() {
		showToast("已达到第一页");
		this.page = 0;
		findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_previous).setVisibility(View.GONE);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		stopPlay();
	}
}
