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
	private static final int REQUEST_CODE_AUDIO_RECORD = 101;
	private static final int REQUEST_CODE_AUDIO_LOCAL = 102;
	private ImageView preview;
	private TextView path_audio;
	private TextView tv_page;// �ڼ�ҳ
	private TextView tv_title;// ����
	private DBHelper dbHelper;
	private PictureBook temPictureBook;
	private BookContent temBookContent;
	private int pages = 0;
	private Button btn_next, btn_done, btn_pic, btn_audio;
	
	private PopupWindow pop;
	private View view;
	private Button dragBtn;
	private int screenWidth, screenHeight;
	private static final String LASTX = "lastx";
	private static final String LASTY = "lasty";
	private SharedPreferences sp;
	
	private void initPopView() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels - 50;
		final LayoutInflater inflater = LayoutInflater.from(this);
		// ���봰�������ļ�
		view = inflater.inflate(R.layout.popwindow, null);
		// ����PopupWindow����
		pop = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
		dragBtn = (Button)view.findViewById(R.id.btn);
		// ��Ҫ����һ�´˲����������߿���ʧ
		pop.setBackgroundDrawable(new BitmapDrawable());
		// ���õ��������ߴ�����ʧ
		pop.setOutsideTouchable(false);
		// ���ô˲�����ý��㣬�����޷����
		pop.setFocusable(true);
		setPopBtnTouch();
	}
	
	private void initPopData() {
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		int lastx = sp.getInt(LASTX, 0);
		int lasty = sp.getInt(LASTY, 0);
		RelativeLayout.LayoutParams params = (LayoutParams)this.dragBtn.getLayoutParams();
		params.leftMargin = lastx;
		params.topMargin = lasty;
		dragBtn.setLayoutParams(params);
	}
	
	public void onPopConfirm(View view) {
		Editor editor = sp.edit();
		editor.putInt(LASTX, dragBtn.getLeft());
		editor.putInt(LASTY, dragBtn.getTop());
		editor.commit();
		dismissPop();
		onAudioDialog();
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
			// ���ش��ڣ���������˵��������Сʱ������Ҫ�˷�ʽ����
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
						
						Log.i("", "position��" + left + ", " + top + ", " + right + ", " + bottom);
						
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
	
	// �̳�BaseActivity���´�ҳ��ʱ��Ĭ��ִ�и�����onCreate()������Ȼ�������ִ��initView()��initDate()
	@Override
	public void initView() {// ��ʼ���ؼ�
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
		btn_audio.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void initDate() {// ��ʼ������
		// TODO Auto-generated method stub
		dbHelper = DBHelper.getInstance();
		// ����Ĭ�ϵ�ImageLoader���ò���
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
			new AlertDialog.Builder(this).setTitle("��ʾ").setIcon(android.R.drawable.ic_dialog_info).setMessage("�������λ汾�����ݣ�")
					.setPositiveButton("ȷ��", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dbHelper.delete(Contract.PictureBookContract.TABLE_NAME, temPictureBook.getId());
							finish();
						}
					}).setNegativeButton("ȡ��", null).show();
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
			case REQUEST_CODE_PIC_LOCAL:// �õ�����ͼƬ
				Uri selectedImage = data.getData();
				if(selectedImage != null) {
					tv_page.setText("��" + (pages + 1) + "ҳ");
					btn_audio.setVisibility(View.VISIBLE);
					setControlView(View.VISIBLE);
					// setMediaInputView(View.INVISIBLE);
					doHandeLocalPic(selectedImage);
				}
				break;
			case REQUEST_CODE_AUDIO_LOCAL:// �õ���������
				if(resultCode == RESULT_OK) {
					setControlView(View.VISIBLE);
					// setMediaInputView(View.INVISIBLE);
					tv_page.setText("��" + (pages + 1) + "ҳ");
					Uri uri = data.getData();
					doHandeAudio(uri);
				}
				break;
			
			case REQUEST_CODE_AUDIO_RECORD:// ¼�Ƶ�����
				if(resultCode == RESULT_OK) {
					setControlView(View.VISIBLE);
					// setMediaInputView(View.INVISIBLE);
					tv_page.setText("��" + (pages + 1) + "ҳ");
					Uri uri = data.getData();
					doHandeAudio(uri);
				}
				break;
		
		}
	}
	
	// ����汾����
	private void showDialog() {
		final EditText et = new EditText(this);
		et.setText("�ҵĻ汾");
		new AlertDialog.Builder(this).setTitle("������汾����").setIcon(android.R.drawable.ic_dialog_info).setView(et)
				.setPositiveButton("ȷ��", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						doInsertTitle(et.getText().toString());
					}
				}).setNegativeButton("ȡ��", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}).show();
	}
	
	// ����汾���⡢ʱ�䵽���ݿ�
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
	 * @��������onPicture
	 * @���������ѡ��ͼƬ
	 * @param v
	 * @�����void
	 */
	public void onPicture(View v) {
		doSelectPicture();
	}
	
	/**
	 * @��������onAduio
	 * @����������ѡ������ѡ���
	 * @param v
	 * @�����void
	 */
	public void onAudio(View v) {
		showPop(v);
	}
	
	private void onAudioDialog() {
		new AlertDialog.Builder(this).setSingleChoiceItems(new String[] {"¼������", "ѡ�񱾵�����" }, 0, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(which == 0) {// ��¼������
					try {
						Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
						startActivityForResult(intent, REQUEST_CODE_AUDIO_RECORD);
						// Intent intentFromRecord=new Intent();
						// intentFromRecord.setType("audio/*");
						// intentFromRecord.setAction(Intent.ACTION_GET_CONTENT);
						// intentFromRecord.putExtra("return-data", true);
						// startActivityForResult(intentFromRecord,REQUEST_CODE_AUDIO_RECORD);
					}
					catch(Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						showToast("��ϵͳ¼��������");
					}
				}
				else {// ���ļ�ѡ��
					showFileChooser();
				}
			}
		}).show();
	}
	
	/** �����ļ�ѡ��app��ѡ���ļ� **/
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(Intent.createChooser(intent, "��ѡ��һ�������ļ�"), REQUEST_CODE_AUDIO_LOCAL);
		}
		catch(android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(getApplicationContext(), "�밲װ�ļ�������", Toast.LENGTH_SHORT).show();
		}
	}
	
	// ��һҳ.
	public void onNext(View v) {
		if(temBookContent == null || (temBookContent.getPath_pic() == null && temBookContent.getPath_audio() == null)) {
			showToast("����������������ͼƬ��Ϣ");
			return;
		}
		setControlView(View.VISIBLE);
		// setMediaInputView(View.VISIBLE);
		pages++;
		temBookContent = new BookContent(temPictureBook.getId());
		tv_page.setText("��" + (pages + 1) + "ҳ");
		ImageLoader.getInstance().displayImage(null, preview);
		preview.setBackgroundResource(R.drawable.welcom);
		path_audio.setText("");
	}
	
	// ���
	public void onDone(View v) {
		System.out.println("onDone");
		new AlertDialog.Builder(this).setTitle("��ʾ").setIcon(android.R.drawable.ic_dialog_info).setMessage("���沢�������εı༭��")
				.setPositiveButton("ȷ��", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						DataHandle.getInstance().addPictureBook(temPictureBook);
						temPictureBook = null;
						finish();
					}
				}).setNegativeButton("�����༭", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).show();
		
	}
	
	// ��һ������ɰ�ť����
	private void setControlView(int flag) {
		btn_next.setVisibility(flag);
		btn_done.setVisibility(flag);
	}
	
	// ѡ��ͼƬ��������ť����
	private void setMediaInputView(int flag) {
		// btn_pic.setVisibility(flag);
		// btn_audio.setVisibility(flag);
	}
	
	// ����ᣬѡ��ͼƬ
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
	 * @��������doHandeLocalPic
	 * @����������õ�����ͼƬ
	 * @param selectedImage
	 * @�����void
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
				// ��������
				temBookContent.setPage(pages);
				value.put(Contract.BookContentContract._PAGE, pages);
				value.put(Contract.BookContentContract._BOOK_ID, temBookContent.getBookId());
				long row = dbHelper.insert(Contract.BookContentContract.TABLE_NAME, value);
				temBookContent.setId((int)row);
			}
			else {
				// ��������
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
	
	private void doHandeAudio(Uri selectedImage) {
		try {
			String realSelectedPath = CommonUtils.getRealPathFromURI(RecordActivity.this, selectedImage);
			ContentValues value = new ContentValues();
			value.put(Contract.BookContentContract._PATH_AUDIO, realSelectedPath);
			path_audio.setText(realSelectedPath);
			if(temBookContent.getPage() != pages) {
				// ��������
				temBookContent.setPage(pages);
				value.put(Contract.BookContentContract._PAGE, pages);
				value.put(Contract.BookContentContract._BOOK_ID, temBookContent.getBookId());
				long row = dbHelper.insert(Contract.BookContentContract.TABLE_NAME, value);
				temBookContent.setId((int)row);
			}
			else {
				// ��������
				dbHelper.update(Contract.BookContentContract.TABLE_NAME, temBookContent.getId(), value);
			}
			temBookContent.setPath_audio(realSelectedPath);
			temPictureBook.addBookContent(temBookContent);
		}
		catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
