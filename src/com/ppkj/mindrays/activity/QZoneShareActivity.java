package com.ppkj.mindrays.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.utils.ToastUtils;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tencent.utils.Util;

public class QZoneShareActivity extends BaseActivity {

	private RadioButton mRadioBtnShareTypeImgAndText;
	// private RadioButton mRadioBtnShareTypeImg;
	private EditText title = null;
	private EditText summary = null;
	private EditText targetUrl = null;
	private int shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
	private Tencent tencent;
	private String imagePath;

	/**
	 * 用异步方式启动分享
	 * 
	 * @param params
	 */
	private void doShareToQzone(final Bundle params) {
		final Activity activity = QZoneShareActivity.this;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tencent.shareToQzone(activity, params, new IUiListener() {

					@Override
					public void onCancel() {
						ToastUtils.showToastOnUiThread(QZoneShareActivity.this,
								"取消分享");
					}

					@Override
					public void onError(UiError e) {
						// TODO Auto-generated method stub
						ToastUtils.showToastOnUiThread(QZoneShareActivity.this,
								e.errorMessage);
					}

					@Override
					public void onComplete(Object response) {
						// TODO Auto-generated method stub
						ToastUtils.showToastOnUiThread(QZoneShareActivity.this,
								response.toString());
					}

				});
			}
		}).start();
	}

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.qzone_share_activity);
	}

	@Override
	public void dealLogicBeforeInitView() {
		// TODO Auto-generated method stub
		imagePath = getIntent().getStringExtra("imagepath");
		tencent = Tencent.createInstance(Constants.QQ_APPID,
				QZoneShareActivity.this);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		title = (EditText) findViewById(R.id.shareqq_title);
		targetUrl = (EditText) findViewById(R.id.shareqq_targetUrl);
		summary = (EditText) findViewById(R.id.shareqq_summary);

		findViewById(R.id.shareqq_commit).setOnClickListener(this);

		mRadioBtnShareTypeImgAndText = (RadioButton) findViewById(R.id.QZoneShare_radioBtn_image_text_share);
		mRadioBtnShareTypeImgAndText.setOnClickListener(this);
		// mRadioBtnShareTypeImg = (RadioButton)
		// findViewById(R.id.QZoneShare_radioBtn_image_share);
		// mRadioBtnShareTypeImg.setOnClickListener(this);
	}

	@Override
	public void dealLogicAfterInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickEvent(View v) {
		// TODO Auto-generated method stub

		int id = v.getId();
		switch (id) {
		case R.id.QZoneShare_radioBtn_image_text_share:
			shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
			break;
		// case R.id.QZoneShare_radioBtn_image_share:
		// shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE;
		// break;
		case R.id.shareqq_commit: // 提交
			if (title.getText().toString().equals("")) {
				ToastUtils.showToastOnUiThread(QZoneShareActivity.this,
						"标题不能为空");
				return;
			}

			final Bundle params = new Bundle();
			params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title.getText()
					.toString());
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary.getText()
					.toString());
			params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl
					.getText().toString());
			// 支持传多个imageUrl
			ArrayList<String> imageUrls = new ArrayList<String>();
			imageUrls.add(imagePath);
			// String imageUrl = "XXX";
			// params.putString(Tencent.SHARE_TO_QQ_IMAGE_URL, imageUrl);
			params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
					imageUrls);
			doShareToQzone(params);
			return;

		}

	}

}
