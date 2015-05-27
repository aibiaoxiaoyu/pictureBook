package com.kid.picturebook.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

public class ClickToPlayRecorder {
	
	MediaRecorder recorder;
	AudioManager mAudioMgr;
	public static final String PREFIX = "voice";
	public static final String EXTENSION = ".amr";
	private boolean isRecording = false;
	private long startTime;
	public int voice_duration;
	private String voiceFilePath = null;
	private String fileName = null;
	private File file;
	public static final int MAX_DURATION = 180;// �?��录音时长
	public static final int TIME_TO_COUNT_DOWN = 10;// 倒计时开�?
	private Handler handler;
	private static MediaPlayer mediaPlayer = null;
	private static SimpleDateFormat mFormat = new SimpleDateFormat("yyyMMddHHmmssSSS");
	private static String playSource = null;
	private static String vmsg_uuid = null;
	public static boolean isPlaying = false;
	public static ClickToPlayRecorder currentPlayListener = null;
	private Context context;
	private MediaPlayerCallback mMediaPlayerCallback;
	private static AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = null;
	
	public ClickToPlayRecorder(Context context, Handler paramHandler) {
		this.handler = paramHandler;
		this.context = context;
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
			mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
				@Override
				public void onAudioFocusChange(int focusChange) {
					if(focusChange == AudioManager.AUDIOFOCUS_LOSS) {
						if(isPlaying) {
							stopPlayVoice();
						}
					}
					else if(focusChange == AudioManager.AUDIOFOCUS_GAIN) {
					}
				}
			};
		}
	}
	
	public interface MediaPlayerCallback {
		void onStart();
		
		void onStop();
	}
	
	public void discardRecording() {
		if(this.recorder != null) {
			try {
				this.recorder.stop();
				this.recorder.release();
				this.recorder = null;
				if((this.file != null) && (this.file.exists()) && (!this.file.isDirectory())) {
					this.file.delete();
				}
			}
			catch(IllegalStateException localIllegalStateException) {
			}
			this.isRecording = false;
		}
	}
	
	public int stopRecoding() {
		if(this.recorder != null) {
			this.isRecording = false;
			this.voice_duration = 0;
			this.recorder.stop();
			this.recorder.release();
			this.recorder = null;
			int i = (int)(new Date().getTime() - this.startTime) / 1000;
			return i;
		}
		return 0;
	}
	
	protected void finalize() throws Throwable {
		super.finalize();
		if(this.recorder != null) {
			this.recorder.release();
		}
	}
	
	public int getAudioTime(String audioPath) {
		int duration = 0;
		File f = new File(audioPath);
		String string = f.getName();
		if(string.contains("_")) {
			try {
				// duration =
				// Integer.parseInt(f.getName().split("_")[1].split(EXTENSION)[0]);
				duration = Integer.parseInt(string.substring(string.lastIndexOf("_") + 1, string.lastIndexOf(".")));
			}
			catch(Exception e) {
				e.printStackTrace();
				duration = 0;
			}
			
		}
		else {
			duration = Math.round(f.length() / (33 * 1000));
			
		}
		return duration;
	}
	
	public String getVoiceFilePath(int length) {
		fileName = file.getAbsolutePath().split(EXTENSION)[0] + "_" + length + EXTENSION;
		
		file.renameTo(new File(fileName));
		return fileName;
	}
	
	public boolean isRecording() {
		return this.isRecording;
	}
	
	public void playVoice(String filePath, MediaPlayerCallback callback) {
		if(TextUtils.isEmpty(filePath)||!(new File(filePath).exists())) {
			Log.d("IM", "not exits");
			return;
		}
		if(isPlaying) {
			stopPlayVoice();
			if(playSource.equals(filePath)) {
				return;
			}
			else {
				doPlay(filePath, callback);
			}
		}
		else {
			doPlay(filePath, callback);
		}
	}
	
	private void doPlay(String filePath, MediaPlayerCallback callback) {
		mMediaPlayerCallback = callback;
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		
		audioManager.setSpeakerphoneOn(true);
		audioManager.setMode(AudioManager.MODE_NORMAL);
		
		((Activity)context).setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					if(mMediaPlayerCallback!=null){
						mMediaPlayerCallback.onStop();
					}
					stopPlayVoice(); // stop animation
				}
				
			});
			isPlaying = true;
			playSource = filePath;
			currentPlayListener = this;
			mediaPlayer.start();
			if(mMediaPlayerCallback!=null){
				mMediaPlayerCallback.onStart();
			}
		}
		catch(Exception e) {
			Log.d("IM", e.toString());
		}
	}
	
	public void stopPlayVoice() {
		// stop play voice
		if(mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			if(mMediaPlayerCallback!=null){
				mMediaPlayerCallback.onStop();
			}
		}
		isPlaying = false;
	}
	
}
