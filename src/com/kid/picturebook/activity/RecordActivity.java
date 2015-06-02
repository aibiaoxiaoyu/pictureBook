package com.kid.picturebook.activity;

import java.io.File;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.kid.picturebook.R;
import com.kid.picturebook.db.Contract;
import com.kid.picturebook.db.DBHelper;
import com.kid.picturebook.dealdate.Constant.AudioType;
import com.kid.picturebook.dealdate.DataHandle;
import com.kid.picturebook.entity.BookContent;
import com.kid.picturebook.entity.PictureBook;
import com.kid.picturebook.utils.CommonUtils;
import com.kid.picturebook.utils.CompressPicUtil;
import com.kid.picturebook.utils.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class RecordActivity extends BaseActivity {
	private static final int REQUEST_CODE_PIC_LOCAL = 100;
	private static final int REQUEST_CODE_BACKGROUND_RECORD = 101;
	private static final int REQUEST_CODE_BACKGROUND_LOCAL = 102;
	private static final int REQUEST_CODE_CLICK_RECORD = 103;
	private static final int REQUEST_CODE_CLICK_LOCAL = 104;
	private ImageView preview;
	private TextView path_audio;
	private TextView tv_page;// 第几页
	private TextView tv_title;// 标题
	private DBHelper dbHelper;
	private PictureBook temPictureBook;
	private BookContent temBookContent;
	private int pages = 0;
	private Button btn_next, btn_done, btn_pic, btn_audio, btn_add_talking;
	
	private PopupWindow pop;
	private View view;
	private Button dragBtn;
	private int screenWidth, screenHeight;
	private static final String LASTX = "lastx";
	private static final String LASTY = "lasty";
	private SharedPreferences sp;
	private int selectedType;
	
	private String getLastX() {
		String x = (temPictureBook == null ? "" : temPictureBook.getId()) + LASTX + (temBookContent == null ? "" : temBookContent.getPage());
		Log.e("getLastX:", x);
		return x;
	}
	
	private String getLastY() {
		String y = (temPictureBook == null ? "" : temPictureBook.getId()) + LASTY + (temBookContent == null ? "" : temBookContent.getPage());
		Log.e("getLastY:", y);
		return y;
	}
	
	private void initPopView() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels - 50;
		final LayoutInflater inflater = LayoutInflater.from(this);
		// 引入窗口配置文件
		view = inflater.inflate(R.layout.popwindow, null);
		// 创建PopupWindow对象
		pop = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
		dragBtn = (Button)view.findViewById(R.id.btn);
		// 需要设置一下此参数，点击外边可消失
		pop.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		pop.setOutsideTouchable(false);
		// 设置此参数获得焦点，否则无法点击
		pop.setFocusable(true);
		setPopBtnTouch();
	}
	
	private void initPopData() {
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		int lastx = sp.getInt(getLastX(), 0);
		int lasty = sp.getInt(getLastY(), 0);
		RelativeLayout.LayoutParams params = (LayoutParams)this.dragBtn.getLayoutParams();
		params.leftMargin = lastx;
		params.topMargin = lasty;
		dragBtn.setLayoutParams(params);
	}
	
	public void onPopConfirm(View view) {
		Editor editor = sp.edit();
		editor.putInt(getLastX(), dragBtn.getLeft());
		editor.putInt(getLastY(), dragBtn.getTop());
		editor.commit();
		dismissPop();
		onAudioDialog(AudioType.CLICK.nativeInt);
	}
	
	private void showPop(View view) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		// pop.showAsDropDown(view);
		// pop.showAsDropDown(view, lastx, lasty);
		// pop.showAtLocation(view, Gravity.NO_GRAVITY,
		// location[0]-pop.getWidth(), lasty);
		pop.showAtLocation(view, Gravity.CENTER, 0, 0);
		initPopData();
	}
	
	private void dismissPop() {
		if(pop.isShowing()) {
			// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
			pop.dismiss();
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initPopView();
		initPopData();
	}
	
	private void setPopBtnTouch() {
		dragBtn.setOnTouchListener(new OnTouchListener() {
			
			int lastX, lastY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int ea = event.getAction();
				Log.i("TAG", "Touch:" + ea);
				
				switch(ea) {
					case MotionEvent.ACTION_DOWN:
						lastX = (int)event.getRawX();
						lastY = (int)event.getRawY();
						break;
					/**
					 * layout(l,t,r,b) l Left position, relative to parent t Top
					 * position, relative to parent r Right position, relative
					 * to parent b Bottom position, relative to parent
					 */
					case MotionEvent.ACTION_MOVE:
						int dx = (int)event.getRawX() - lastX;
						int dy = (int)event.getRawY() - lastY;
						
						int left = v.getLeft() + dx;
						int top = v.getTop() + dy;
						int right = v.getRight() + dx;
						int bottom = v.getBottom() + dy;
						
						if(left < 0) {
							left = 0;
							right = left + v.getWidth();
						}
						
						if(right > screenWidth) {
							right = screenWidth;
							left = right - v.getWidth();
						}
						
						if(top < 0) {
							top = 0;
							bottom = top + v.getHeight();
						}
						
						if(bottom > screenHeight) {
							bottom = screenHeight;
							top = bottom - v.getHeight();
						}
						
						v.layout(left, top, right, bottom);
						
						Log.i("", "position：" + left + ", " + top + ", " + right + ", " + bottom);
						
						lastX = (int)event.getRawX();
						lastY = (int)event.getRawY();
						
						break;
					case MotionEvent.ACTION_UP:
						break;
				}
				return false;
			}
		});
	}
	
	// 继承BaseActivity，新打开页面时，默认执行父类中onCreate()方法，然后会依次执行initView()和initDate()
	@Override
	public void initView() {// 初始化控件
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_record);
		tv_page = (TextView)findViewById(R.id.tv_page);
		tv_title = (TextView)findViewById(R.id.tv_title);
		path_audio = (TextView)findViewById(R.id.path_audio);
		preview = (ImageView)findViewById(R.id.preview);
		btn_next = (Button)findViewById(R.id.btn_next);
		btn_done = (Button)findViewById(R.id.btn_done);
		btn_pic = (Button)findViewById(R.id.btn_pic);
		btn_audio = (Button)findViewById(R.id.btn_audio);
		btn_add_talking = (Button)findViewById(R.id.btn_add_talking);
		btn_audio.setVisibility(View.INVISIBLE);
		btn_add_talking.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void initDate() {// 初始化数据
		// TODO Auto-generated method stub
		dbHelper = DBHelper.getInstance();
		// 创建默认的ImageLoader配置参数
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(configuration);
		showDialog();
		// tv_page.setText(pages + "");
		
	}
	
	@Override
	public void onBack(View v) {
		// TODO Auto-generated method stub
		if(temPictureBook.getBookContentList() == null) {
			new AlertDialog.Builder(this).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("放弃本次绘本的内容？")
					.setPositiveButton("确定", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dbHelper.delete(Contract.PictureBookContract.TABLE_NAME, temPictureBook.getId());
							finish();
						}
					}).setNegativeButton("取消", null).show();
		}
		else {
			onDone(v);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null)
			return;
		switch(requestCode) {
			case REQUEST_CODE_PIC_LOCAL:// 得到本机图片
				Uri selectedImage = data.getData();
				if(selectedImage != null) {
					tv_page.setText("第" + (pages + 1) + "页");
					btn_audio.setVisibility(View.VISIBLE);
					btn_add_talking.setVisibility(View.VISIBLE);
					setControlView(View.VISIBLE);
					// setMediaInputView(View.INVISIBLE);
					doHandeLocalPic(selectedImage);
				}
				break;
			case REQUEST_CODE_BACKGROUND_LOCAL:// 得到本机背景语音
				if(resultCode == RESULT_OK) {
					setControlView(View.VISIBLE);
					// setMediaInputView(View.INVISIBLE);
					tv_page.setText("第" + (pages + 1) + "页");
					Uri uri = data.getData();
					doHandeAudio(uri, AudioType.BACKGROUND.nativeInt);
				}
				break;
			case REQUEST_CODE_CLICK_LOCAL:// 得到点读本机语音
				if(resultCode == RESULT_OK) {
					setControlView(View.VISIBLE);
					// setMediaInputView(View.INVISIBLE);
					tv_page.setText("第" + (pages + 1) + "页");
					Uri uri = data.getData();
					doHandeAudio(uri, AudioType.CLICK.nativeInt);
				}
				break;
			
			case REQUEST_CODE_BACKGROUND_RECORD:// 录制的声音
				if(resultCode == RESULT_OK) {
					setControlView(View.VISIBLE);
					// setMediaInputView(View.INVISIBLE);
					tv_page.setText("第" + (pages + 1) + "页");
					Uri uri = data.getData();
					doHandeAudio(uri, AudioType.BACKGROUND.nativeInt);
				}
				break;
			case REQUEST_CODE_CLICK_RECORD:// 录制的点读声音
				if(resultCode == RESULT_OK) {
					setControlView(View.VISIBLE);
					// setMediaInputView(View.INVISIBLE);
					tv_page.setText("第" + (pages + 1) + "页");
					Uri uri = data.getData();
					doHandeAudio(uri, AudioType.CLICK.nativeInt);
				}
				break;
		
		}
	}
	
	// 输入绘本名字
	private void showDialog() {
		final EditText et = new EditText(this);
		et.setText("我的绘本");
		new AlertDialog.Builder(this).setTitle("请输入绘本名字").setIcon(android.R.drawable.ic_dialog_info).setView(et)
				.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						doInsertTitle(et.getText().toString());
					}
				}).setNegativeButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}).show();
	}
	
	// 插入绘本标题、时间到数据库
	private void doInsertTitle(String title) {
		if(TextUtils.isEmpty(title)) {
			title = "myPictureBook";
		}
		temPictureBook = new PictureBook(title, System.currentTimeMillis());
		String dir = Constants.PICTUREBOOK_PATH_MYBOOKS + "/" + temPictureBook.getBookDir();
		File file = new File(dir);
		file.mkdirs();
		ContentValues cv = new ContentValues();
		cv.put(Contract.PictureBookContract._TITLE, title);
		cv.put(Contract.PictureBookContract._CREATE_TIME, temPictureBook.getCreateTime());
		long row = dbHelper.insert(Contract.PictureBookContract.TABLE_NAME, cv);
		if(temBookContent == null) {
			temBookContent = new BookContent((int)row);
		}
		temPictureBook.setId((int)row);
		tv_title.setText(title);
	}
	
	/**
	 * @方法名：onPicture
	 * @描述：点击选择图片
	 * @param v
	 * @输出：void
	 */
	public void onPicture(View v) {
		doSelectPicture();
	}
	
	/**
	 * @方法名：onAduio
	 * @描述：弹出选择背景音乐选择框
	 * @param v
	 * @输出：void
	 */
	public void onAudio(View v) {
		onAudioDialog(AudioType.BACKGROUND.nativeInt);
	}
	
	/**
	 * @方法名：onAddTalking
	 * @描述：增加点读
	 * @param v
	 * @输出：void
	 * @作者：caizhibiao
	 */
	public void onAddTalking(View v) {
		showPop(v);
	}
	
	private void onAudioDialog(final int type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if(type == AudioType.BACKGROUND.nativeInt) {
			builder.setTitle("添加背景音乐");
		}
		else {
			builder.setTitle("添加点读声音");
		}
		builder.setSingleChoiceItems(new String[] {"录制声音", "选择本地语音" }, 0, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(which == 0) {// 打开录音机，
					try {
						Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
						if(type == AudioType.BACKGROUND.nativeInt) {
							startActivityForResult(intent, REQUEST_CODE_BACKGROUND_RECORD);
						}
						else {
							startActivityForResult(intent, REQUEST_CODE_CLICK_RECORD);
						}
						// Intent intentFromRecord=new Intent();
						// intentFromRecord.setType("audio/*");
						// intentFromRecord.setAction(Intent.ACTION_GET_CONTENT);
						// intentFromRecord.putExtra("return-data", true);
						// startActivityForResult(intentFromRecord,REQUEST_CODE_AUDIO_RECORD);
					}
					catch(Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						showToast("打开系统录音机出错");
					}
				}
				else {// 打开文件选择
					showFileChooser(type);
				}
			}
		});
		builder.show();
	}
	
	/** 调用文件选择app来选择文件 **/
	private void showFileChooser(int type) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			if(type == AudioType.BACKGROUND.nativeInt) {
				startActivityForResult(Intent.createChooser(intent, "请选择一个语音文件"), REQUEST_CODE_BACKGROUND_LOCAL);
			}
			else {
				startActivityForResult(Intent.createChooser(intent, "请选择一个语音文件"), REQUEST_CODE_CLICK_LOCAL);
			}
		}
		catch(android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(getApplicationContext(), "请安装文件管理器", Toast.LENGTH_SHORT).show();
		}
	}
	
	// 下一页.
	public void onNext(View v) {
		if(temBookContent == null || (temBookContent.getPath_pic() == null && temBookContent.getPath_bg_audio() == null)) {
			showToast("请先输入语音或者图片信息");
			return;
		}
		setControlView(View.VISIBLE);
		// setMediaInputView(View.VISIBLE);
		pages++;
		temBookContent = new BookContent(temPictureBook.getId());
		tv_page.setText("第" + (pages + 1) + "页");
		ImageLoader.getInstance().displayImage(null, preview);
		preview.setBackgroundResource(R.drawable.welcom);
		path_audio.setText("");
	}
	
	// 完成
	public void onDone(View v) {
		System.out.println("onDone");
		new AlertDialog.Builder(this).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("保存并结束本次的编辑？")
				.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						DataHandle.getInstance().addPictureBook(temPictureBook);
						temPictureBook = null;
						finish();
					}
				}).setNegativeButton("继续编辑", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).show();
		
	}
	
	// 下一步和完成按钮隐藏
	private void setControlView(int flag) {
		btn_next.setVisibility(flag);
		btn_done.setVisibility(flag);
	}
	
	// 选择图片和语音按钮隐藏
	private void setMediaInputView(int flag) {
		// btn_pic.setVisibility(flag);
		// btn_audio.setVisibility(flag);
	}
	
	// 打开相册，选择图片
	private void doSelectPicture() {
		Intent intent;
		if(Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			
		}
		else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_PIC_LOCAL);
	}
	
	/**
	 * @方法名：doHandeLocalPic
	 * @描述：处理得到本机图片
	 * @param selectedImage
	 * @输出：void
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void doHandeLocalPic(Uri selectedImage) {
		try {
			String localFilePath = Constants.PICTUREBOOK_PATH_MYBOOKS + "/" + temPictureBook.getBookDir() + "/" + System.currentTimeMillis()
					+ ".jpeg";
			String realSelectedPath = CommonUtils.getRealPathFromURI(RecordActivity.this, selectedImage);
			CompressPicUtil.compressImage(RecordActivity.this, realSelectedPath, localFilePath, 100);
			preview.setBackground(null);
			ImageLoader.getInstance().displayImage("file:///" + localFilePath, preview);
			ContentValues value = new ContentValues();
			value.put(Contract.BookContentContract._PATH_PIC, localFilePath);
			if(temBookContent.getPage() != pages) {
				// 插入数据
				temBookContent.setPage(pages);
				value.put(Contract.BookContentContract._PAGE, pages);
				value.put(Contract.BookContentContract._BOOK_ID, temBookContent.getBookId());
				long row = dbHelper.insert(Contract.BookContentContract.TABLE_NAME, value);
				temBookContent.setId((int)row);
			}
			else {
				// 更新数据
				dbHelper.update(Contract.BookContentContract.TABLE_NAME, temBookContent.getId(), value);
			}
			temBookContent.setPath_pic(localFilePath);
			temPictureBook.addBookContent(temBookContent);
		}
		catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void doHandeAudio(Uri selectedImage, int type) {
		try {
			String realSelectedPath = CommonUtils.getRealPathFromURI(RecordActivity.this, selectedImage);
			ContentValues value = new ContentValues();
			value.put(Contract.BookContentContract._AUDIO_TYPE, type);// 语音路径
			if(type == AudioType.BACKGROUND.nativeInt) {
				value.put(Contract.BookContentContract._PATH_BG_AUDIO, realSelectedPath);// 语音路径
				temBookContent.setPath_bg_audio(realSelectedPath);
			}
			else {
				value.put(Contract.BookContentContract._PATH_CLICK_AUDIO, realSelectedPath);// 语音路径
				temBookContent.setPath_click_audio(realSelectedPath);
			}
			path_audio.setText(realSelectedPath);
			if(temBookContent.getPage() != pages) {
				// 插入数据
				temBookContent.setPage(pages);
				value.put(Contract.BookContentContract._PAGE, pages);
				value.put(Contract.BookContentContract._BOOK_ID, temBookContent.getBookId());
				long row = dbHelper.insert(Contract.BookContentContract.TABLE_NAME, value);
				temBookContent.setId((int)row);
			}
			else {//当前页
				// 更新数据
				dbHelper.update(Contract.BookContentContract.TABLE_NAME, temBookContent.getId(), value);
			}
			temPictureBook.addBookContent(temBookContent);
		}
		catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
