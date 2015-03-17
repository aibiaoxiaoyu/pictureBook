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
	private DBHelper dbHelper;
	private PictureBook pictureBook;
	private BookContent bookContent = new BookContent();
	private int pages = 0;
	
	// �̳�BaseActivity���´�ҳ��ʱ��Ĭ��ִ�и�����onCreate()������Ȼ�������ִ��initView()��initDate()
	@Override
	public void initView() {// ��ʼ���ؼ�
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_record);
		preview = (ImageView)findViewById(R.id.preview);
	}
	
	@Override
	public void initDate() {// ��ʼ������
		// TODO Auto-generated method stub
		// File cacheDir = StorageUtils.getCacheDirectory(context);
		dbHelper = DBHelper.getInstance();
		// ����Ĭ�ϵ�ImageLoader���ò���
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(configuration);
		// dbHelper.insert("����");
		showDialog();
		
	}
	
	// ����汾����
	private void showDialog() {
		final EditText et = new EditText(this);
		et.setHint("�ҵĻ汾����");
		new AlertDialog.Builder(this).setTitle("������汾����").setIcon(android.R.drawable.ic_dialog_info).setView(et)
				.setPositiveButton("ȷ��", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String title = et.toString();
						if(TextUtils.isEmpty(title)) {
							title = "myPictureBook";
						}
						String dir = Constants.PICTUREBOOK_PATH_MYBOOKS + "/" + title;
						File file = new File(dir);
						file.mkdirs();
						pictureBook = new PictureBook(title);
						ContentValues cv = new ContentValues();
						cv.put(Contract.PictureBook._TITLE, title);
						long row = dbHelper.insert(Contract.PictureBook.TABLE_NAME, cv);
						pictureBook.setId((int)row);
						bookContent.setId(pictureBook.getId());
					}
				}).setNegativeButton("ȡ��", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}).show();
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
		new AlertDialog.Builder(this).setSingleChoiceItems(new String[] {"¼������", "ѡ�񱾵�����" }, 0, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(which == 0) {// ��¼������
					Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
					startActivityForResult(intent, REQUEST_CODE_AUDIO_RECORD);
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
	
	// ��һҳ
	public void onNext(View v) {
		pages++;
		preview.setBackground(null);
	}
	
	// ���
	public void onDone(View v) {
		
		DataHandle.getInstance().addPictureBook(pictureBook);
		pictureBook = null;
		finish();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null)
			return;
		switch(requestCode) {
			case REQUEST_CODE_PIC_LOCAL:// �õ�����ͼƬ
				Uri selectedImage = data.getData();
				if(selectedImage != null) {
					doHandeLocalPic(selectedImage);
				}
				break;
			case REQUEST_CODE_AUDIO_LOCAL:// �õ���������
				if(resultCode == RESULT_OK) {
					Uri uri = data.getData();
				}
				break;
			
			case REQUEST_CODE_AUDIO_RECORD:// ¼�Ƶ�����
				if(resultCode == RESULT_OK) {
					Uri uri = data.getData();
				}
				break;
		
		}
	}
	
	private void doHandeLocalPic(Uri selectedImage) {
		
		try {
			String localFilePath = Constants.PICTUREBOOK_PATH_MYBOOKS + "/" + System.currentTimeMillis() + ".jpeg";
			String realSelectedPath = CommonUtils.getRealPathFromURI(RecordActivity.this, selectedImage);
			CompressPicUtil.compressImage(RecordActivity.this, realSelectedPath, localFilePath, 100);
			preview.setBackground(null);
			ImageLoader.getInstance().displayImage("file:///" + localFilePath, preview);
			ContentValues value = new ContentValues();
			value.put(Contract.BookContent._PATH_PIC, localFilePath);
			if(bookContent.getPath_pic() == null) {
				// ��������
				value.put(Contract.BookContent._PAGE, pages);
				value.put(Contract.BookContent._ID, bookContent.getId());
				dbHelper.insert(Contract.BookContent._PATH_PIC, value);
			}
			else {
				// ��������
				dbHelper.update(Contract.BookContent.TABLE_NAME, bookContent.getId(), value);
			}
			bookContent.setPath_pic(localFilePath);
		}
		catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
