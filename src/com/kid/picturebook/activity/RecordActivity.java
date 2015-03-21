package com.kid.picturebook.activity;

import java.io.File;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	private TextView tv_page;
	private DBHelper dbHelper;
	private PictureBook temPictureBook;
	private BookContent temBookContent = new BookContent();
	private int pages = 0;
	
	// 继承BaseActivity，新打开页面时，默认执行父类中onCreate()方法，然后会依次执行initView()和initDate()
	@Override
	public void initView() {// 初始化控件
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_record);
		tv_page = (TextView)findViewById(R.id.tv_page);
		preview = (ImageView)findViewById(R.id.preview);
	}
	
	@Override
	public void initDate() {// 初始化数据
		// TODO Auto-generated method stub
		// File cacheDir = StorageUtils.getCacheDirectory(context);
		dbHelper = DBHelper.getInstance();
		// 创建默认的ImageLoader配置参数
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(configuration);
		// dbHelper.insert("啦啦");
		showDialog();
		// tv_page.setText(pages + "");
		
	}
	
	// 输入绘本名字
	private void showDialog() {
		final EditText et = new EditText(this);
		et.setHint("我的绘本名字");
		new AlertDialog.Builder(this).setTitle("请输入绘本名字").setIcon(android.R.drawable.ic_dialog_info).setView(et)
				.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String title = et.getText().toString();
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
						temPictureBook.setId((int)row);
						temBookContent.setBookId(temPictureBook.getId());
					}
				}).setNegativeButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}).show();
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
	 * @描述：弹出选择声音选择框
	 * @param v
	 * @输出：void
	 */
	public void onAudio(View v) {
		new AlertDialog.Builder(this).setSingleChoiceItems(new String[] {"录制声音", "选择本地语音" }, 0, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(which == 0) {// 打开录音机，
					Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
					startActivityForResult(intent, REQUEST_CODE_AUDIO_RECORD);
				}
				else {// 打开文件选择
					showFileChooser();
				}
			}
		}).show();
		
	}
	
	/** 调用文件选择app来选择文件 **/
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(Intent.createChooser(intent, "请选择一个语音文件"), REQUEST_CODE_AUDIO_LOCAL);
		}
		catch(android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(getApplicationContext(), "请安装文件管理器", Toast.LENGTH_SHORT).show();
		}
	}
	
	// 下一页.
	public void onNext(View v) {
		pages++;
		temBookContent = new BookContent();
		
		preview.setBackground(null);
		ImageLoader.getInstance().displayImage(null, preview);
	}
	
	// 完成
	public void onDone(View v) {
		
		DataHandle.getInstance().addPictureBook(temPictureBook);
		temPictureBook = null;
		finish();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null)
			return;
		switch(requestCode) {
			case REQUEST_CODE_PIC_LOCAL:// 得到本机图片
				Uri selectedImage = data.getData();
				if(selectedImage != null) {
					tv_page.setText(pages + "");
					doHandeLocalPic(selectedImage);
				}
				break;
			case REQUEST_CODE_AUDIO_LOCAL:// 得到本机语音
				if(resultCode == RESULT_OK) {
					tv_page.setText(pages + "");
					Uri uri = data.getData();
				}
				break;
			
			case REQUEST_CODE_AUDIO_RECORD:// 录制的声音
				if(resultCode == RESULT_OK) {
					tv_page.setText(pages + "");
					Uri uri = data.getData();
				}
				break;
		
		}
	}
	
	/**
	 * @方法名：doHandeLocalPic
	 * @描述：处理得到本机图片
	 * @param selectedImage
	 * @输出：void
	 */
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
}
