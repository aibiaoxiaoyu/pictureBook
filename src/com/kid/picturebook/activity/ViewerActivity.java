package com.kid.picturebook.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import android.app.Notification.Action;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kid.picturebook.R;
import com.kid.picturebook.dealdate.DataHandle;
import com.kid.picturebook.entity.BookContent;
import com.kid.picturebook.entity.PictureBook;
import com.kid.picturebook.utils.ClickToPlayRecorder;
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
	private ClickToPlayRecorder clickRecorder;
	private boolean isAutoPlay = true;
	private SharedPreferences sp;
	private static final String LASTX = "lastx";
	private static final String LASTY = "lasty";
//	private boolean isClickedAndPlaying=false;
	private String getLastX() {
//		Log.e("getLastX:",(bookId == 0 ? "0" : bookId) + LASTX + (page == 0 ? "0" : page));
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
				// �Զ�ѭ������
				mHandler.postDelayed(runnable, DELAYTIME);
			}
			else {
				//ȡ��ѭ��
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
		clickRecorder = new ClickToPlayRecorder(ViewerActivity.this, null);
		book = DataHandle.getInstance().getPictureBookByBookId(bookId);	// ��������
		if(book!=null){
			mList = book.getBookContentList();
		}
		showPage(page);//��ʾ��һҳ
		mHandler.postDelayed(runnable, DELAYTIME);//�Զ�����
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		img.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
//				if(!isClickedAndPlaying) {
					setOnClicked(v, event);
//				}
				return true;
			}
		});
//		img.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				setOnClicked(v, event);
//				
//			}
//		});
	}
	
	//�ص�������
	public void onHome(View v) {
		stopPlay();//ֹͣ����
		startActivity(new Intent(ViewerActivity.this, HomeActivity.class));
		finish();
	}
	
	//ֹͣ����
	private void stopPlay() {
		if(voiceRecorder != null && voiceRecorder.isPlaying) {
			voiceRecorder.stopPlayVoice();
		}
		mHandler.removeCallbacks(runnable);
	}
	
	 //�������
	public void setOnClicked(View v, MotionEvent event) {

		
		int lastx = sp.getInt(getLastX(), 0);
		int lasty = sp.getInt(getLastY(), 0);
		Log.e("","lastx:" + lastx + ",getRawX:"+event.getRawX() + "");
		Log.e("", "lasty:" + lasty + ",getRawY" + event.getRawY() + "");
//		if(event.getRawX() > (lastx - 200) && (event.getRawX() < (lastx + 380)) && event.getRawY() > (lasty - 200)
//				&& (event.getRawY() < (lasty + 380))) {
			if(event.getRawX() > lastx && (event.getRawX() < (lastx + 540)) && event.getRawY() > (lasty)
					&& (event.getRawY() < (lasty + 360))) {
			// isClickedAndPlaying = true;
			switch(event.getAction()) {
				case MotionEvent.ACTION_UP:
					Log.e("ACTION_UP","ACTION_UP");
					showToast("���");
					Log.e("", "���");
					if(page < 0) {
						setViewWhenFirstOne();// �������һҳ����һҳʱ���Բ��ֽ�������
						return ;
					}
					showClicktoPlayLocalPage(page);//��ʾ���Լ�¼��Ļ汾,�������
					view_page.setText((page + 1) + "");
					break;
				case MotionEvent.ACTION_DOWN:
					Log.e("ACTION_DOWN","ACTION_DOWN");
					if(clickRecorder != null && clickRecorder.isPlaying) {
						clickRecorder.stopPlayVoice();
					}
					break;
				
				default:
					break;
			}
			
		}
	}
	
	// ��ʾ��һҳ
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
	
	// ��ʾ��һҳ
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
	
	// ����Զ�����
	public void onPlay(View v) {
		isAutoPlay = true;
		showToast("�л����Զ�����");
		btn_pause.setVisibility(View.VISIBLE);
		btn_play.setVisibility(View.GONE);
		mHandler.postDelayed(runnable, DELAYTIME);
	}
	
	// �����ͣ���ֶ�����
	public void onPause(View v) {
		isAutoPlay = false;
		showToast("�л����ֶ�����");
		btn_play.setVisibility(View.VISIBLE);
		btn_pause.setVisibility(View.GONE);
		mHandler.removeCallbacks(runnable);
	}
	//��ʾĳҳ
	private boolean showPage(int page) {
		if(page < 0) {
			setViewWhenFirstOne();// �������һҳ����һҳʱ���Բ��ֽ�������
			return true;
		}
		if(bookId == -1) {
			return showExamplePage(page);//��ʾʾ��ͼƬ
		}
		showLocalPage(page);//��ʾ���Լ�¼��Ļ汾
		view_page.setText((page + 1) + "");
		return false;
	}
	//��ʾ���Լ�¼��Ļ汾
		private boolean showClicktoPlayLocalPage(int page) {
			if(mList != null && mList.size() > 0) {
				int size = mList.size();
				if(page >= size) {
//					setViewWhenLastOne(); // ����ʾ�����һҳʱ���Բ��ֽ�������
					return true;
				}
				if(mList.get(0).getPath_pic() == null) {
					img.setBackgroundResource(R.drawable.input_book);//Ĭ��ͼƬ
				}
				else {
					if(mList.get(page).getPath_pic()==null){
						img.setBackgroundResource(R.drawable.input_book);//Ĭ��ͼƬ
					}else{
						img.setImageBitmap(CommonUtils.getLoacalBitmap(mList.get(page).getPath_pic()));
					}
				}
				if(clickRecorder.isPlaying) {
					clickRecorder.stopPlayVoice();
				}
			clickRecorder.playVoice(mList.get(page).getPath_click_audio(), null);
			}
			else {
				img.setBackgroundResource(R.drawable.input_book);//Ĭ��ͼƬ
			}
			return false;
		}
	//��ʾ���Լ�¼��Ļ汾
	private boolean showLocalPage(int page) {
		if(mList != null && mList.size() > 0) {
			int size = mList.size();
			if(page >= size) {
				setViewWhenLastOne(); // ����ʾ�����һҳʱ���Բ��ֽ�������
				return true;
			}
			if(mList.get(0).getPath_pic() == null) {
				img.setBackgroundResource(R.drawable.input_book);//Ĭ��ͼƬ
			}
			else {
				if(mList.get(page).getPath_pic()==null){
					img.setBackgroundResource(R.drawable.input_book);//Ĭ��ͼƬ
				}else{
					img.setImageBitmap(CommonUtils.getLoacalBitmap(mList.get(page).getPath_pic()));
				}
			}
			if(voiceRecorder.isPlaying) {
				voiceRecorder.stopPlayVoice();
			}
			voiceRecorder.playVoice(mList.get(page).getPath_bg_audio(),new MediaPlayerCallback() {
				
				@Override
				public void onStop() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					
				}
			});
		}
		else {
			img.setBackgroundResource(R.drawable.input_book);//Ĭ��ͼƬ
		}
		return false;
	}

	//��ʾ����ͼƬ
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
	
	// ����ʾ�����һҳʱ���Բ��ֽ�������
	private void setViewWhenLastOne() {
		btn_play.setVisibility(View.GONE);
		btn_pause.setVisibility(View.GONE);
		findViewById(R.id.btn_previous).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_next).setVisibility(View.GONE);
//		showToast("�Ѵﵽ���һҳ");
//		mHandler.removeCallbacks(runnable);
		this.page--;
	}
	
	// �������һҳ����һҳʱ���Բ��ֽ�������
	private void setViewWhenFirstOne() {
		showToast("�Ѵﵽ��һҳ");
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
