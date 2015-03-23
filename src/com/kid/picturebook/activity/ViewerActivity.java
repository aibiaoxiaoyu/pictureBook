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
	 * @��������onHome
	 * @�������ص�������
	 * @param v
	 * @�����void
	 */
	public void onHome(View v) {
		startActivity(new Intent(ViewerActivity.this, HomeActivity.class));
		finish();
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
	
	// ����Զ�����
	public void onPlay(View v) {
		isAutoPlay = true;
		showToast("�л����Զ�����");
		btn_pause.setVisibility(View.VISIBLE);
		btn_play.setVisibility(View.GONE);
		mHandler.postDelayed(runnable, 3000);
	}
	
	// �����ͣ���ֶ�����
	public void onPause(View v) {
		isAutoPlay = false;
		showToast("�л����ֶ�����");
		btn_play.setVisibility(View.VISIBLE);
		btn_pause.setVisibility(View.GONE);
		mHandler.removeCallbacks(runnable);
	}
	
	private boolean showPage(int page) {
		if(page < 0) {
			showToast("�Ѵﵽ��һҳ");
			this.page = 0;
			findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_previous).setVisibility(View.GONE);
			return true;
		}
		if(bookId == -1) {
			if(page >= exps.length) {
				showToast("�Ѵﵽ���һҳ");
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
				showToast("�Ѵﵽ���һҳ");
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
	 * ���ر���ͼƬ
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /����ת��ΪBitmapͼƬ
			
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
