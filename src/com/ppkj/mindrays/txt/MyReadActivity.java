package com.ppkj.mindrays.txt;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import com.ppkj.mindrays.base.BaseActivity;

/**
 * title: 文本显示的界面
 * 
 * @author zzj
 * @date 2014-7-28
 */
public class MyReadActivity extends BaseActivity {

	private MyPaperTextShow myPaperTextShow;

	private void init(final String filePath) {

		myPaperTextShow = new MyPaperTextShow(this);
		myPaperTextShow.loadBook(filePath);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dealLogicBeforeInitView() {
		SetupShared.initSetupShared(getApplication());
		Intent intent = getIntent();
		String filePath = intent.getStringExtra("filePath");

		init(filePath);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dealLogicAfterInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickEvent(View view) {
		// TODO Auto-generated method stub

	}

}
