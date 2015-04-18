package com.ppkj.mindrays.activity;

import tv.danmaku.ijk.media.widget.MediaController;
import tv.danmaku.ijk.media.widget.OTTMediaPlayer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;

import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;

public class VideoPlayerActivity extends BaseActivity {

    private MediaController mMediaController;
    private OTTMediaPlayer mOTTMediaPlayer;
    private SurfaceView mSurfaceView;
    private String mVideoPath;

    @Override
    protected void onStart() {

	super.onStart();
    }

    @Override
    protected void onResume() {

	if (mOTTMediaPlayer != null)
	    mOTTMediaPlayer.resume();
	super.onResume();
    }

    @Override
    protected void onRestart() {

	if (mOTTMediaPlayer != null)
	    play();
	super.onRestart();
    }

    @Override
    protected void onPause() {

	if (mOTTMediaPlayer != null)
	    mOTTMediaPlayer.pause();
	super.onPause();
    }

    @Override
    protected void onStop() {
	if (mOTTMediaPlayer != null)
	    mOTTMediaPlayer.stopPlayback();
	super.onStop();
    }

    @Override
    protected void onDestroy() {

	super.onDestroy();
    }

    private void play() {

	if (mOTTMediaPlayer != null) {
	    mOTTMediaPlayer.setVideoPath(mVideoPath);
	    mOTTMediaPlayer.start();
	}
    }

    private void stop() {

	if (mOTTMediaPlayer != null) {
	    mMediaController.hide();
	    mOTTMediaPlayer.stopPlayback();
	}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

	boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
		&& keyCode != KeyEvent.KEYCODE_VOLUME_UP
		&& keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
		&& keyCode != KeyEvent.KEYCODE_MENU
		&& keyCode != KeyEvent.KEYCODE_CALL
		&& keyCode != KeyEvent.KEYCODE_ENDCALL;

	if (mOTTMediaPlayer.isInPlaybackState() && isKeyCodeSupported
		&& mMediaController != null) {
	    if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
		    || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
		    || keyCode == KeyEvent.KEYCODE_SPACE) {
		if (mOTTMediaPlayer.isPlaying()) {
		    mOTTMediaPlayer.pause();
		    mMediaController.show();
		} else {
		    mOTTMediaPlayer.start();
		    mMediaController.hide();
		}
		return true;
	    } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
		    && mOTTMediaPlayer.isPlaying()) {
		mOTTMediaPlayer.pause();
		mMediaController.show();
	    } else {
		mOTTMediaPlayer.toggleMediaControlsVisiblity();
	    }
	}

	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    showExitDialog();
	    return true;
	}
	return super.onKeyDown(keyCode, event);
    }

    private void showExitDialog() {

	new AlertDialog.Builder(this)
		.setTitle("温馨提示")
		.setMessage("你确定退出吗")
		.setPositiveButton("再看一会",
			new DialogInterface.OnClickListener() {

			    @Override
			    public void onClick(DialogInterface arg0, int arg1) {

				// Toast.makeText(OTTPortalActivity.this,
				// "你取消了退出", Toast.LENGTH_SHORT).show();
			    }
			})
		.setNegativeButton("退出", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface arg0, int arg1) {

			stop();
			finish();
		    }
		}).show();
    }

    @Override
    public void setContentLayout() {
	// TODO Auto-generated method stub
	setContentView(R.layout.replay_fullscreen);
    }

    @Override
    public void dealLogicBeforeInitView() {
	// TODO Auto-generated method stub
	Intent intent = getIntent();
	mVideoPath = intent.getStringExtra("videopath");
    }

    @Override
    public void initView() {
	// TODO Auto-generated method stub
	mSurfaceView = (SurfaceView) findViewById(R.id.replay_fullscreen_surfaceview);
    }

    @Override
    public void dealLogicAfterInitView() {
	// TODO Auto-generated method stub
	mMediaController = new MediaController(this);
	mOTTMediaPlayer = new OTTMediaPlayer(this, mSurfaceView);

	mOTTMediaPlayer.setMediaController(mMediaController);
	play();
    }

    @Override
    public void onClickEvent(View view) {
	// TODO Auto-generated method stub

    }

}
