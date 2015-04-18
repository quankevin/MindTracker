package com.ppkj.mindrays.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.utils.ToastUtils;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class QQShareActivity extends BaseActivity {

	private View mContainer_title = null;
	private View mContainer_summary = null;

	private TextView title = null;
	private TextView targetUrl = null;
	private TextView summary = null;

	private RadioButton mRadioBtnShareTypeDefault;
	private RadioButton mRadioBtnShareTypeImg;
	private int shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;

	private int mExtarFlag = 0x00;
	private String imagePath;
	private QQShare mQQShare = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shareqq_commit: // 提交
			final Bundle params = new Bundle();
			if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
				params.putString(QQShare.SHARE_TO_QQ_TITLE, title.getText()
						.toString());
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl
						.getText().toString());
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary.getText()
						.toString());
			}
			if (shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imagePath);
			} else {
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imagePath);
			}
			params.putString(
					shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL
							: QQShare.SHARE_TO_QQ_IMAGE_URL, imagePath);
			params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "迈瑞思行车记录");
			params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
			params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

			if ((mExtarFlag & QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN) != 0) {
				// showToast("在好友选择列表会自动打开分享到qzone的弹窗~~~");
			} else if ((mExtarFlag & QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE) != 0) {
				// showToast("在好友选择列表隐藏了qzone分享选项~~~");
			}
			doShareToQQ(params);
			return;

		case R.id.radioBtn_share_type_default:
			shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
			break;
		case R.id.radioBtn_share_type_image:
			shareType = QQShare.SHARE_TO_QQ_TYPE_IMAGE;
			break;
		}
		initShareUI(shareType);
	}

	/**
	 * 初始化UI
	 * 
	 * @param shareType
	 */
	private void initShareUI(int shareType) {
		switch (shareType) {
		case QQShare.SHARE_TO_QQ_TYPE_IMAGE:
			mContainer_title.setVisibility(View.GONE);
			mContainer_summary.setVisibility(View.GONE);
			return;

		case QQShare.SHARE_TO_QQ_TYPE_DEFAULT:
			break;
		}
		mContainer_title.setVisibility(View.VISIBLE);
		mContainer_summary.setVisibility(View.VISIBLE);
	}

	/**
	 * 用异步方式启动分享
	 * 
	 * @param params
	 */
	private void doShareToQQ(final Bundle params) {
		final Activity activity = QQShareActivity.this;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mQQShare.shareToQQ(activity, params, new IUiListener() {

					@Override
					public void onCancel() {
						if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
							ToastUtils.showToast(QQShareActivity.this, "取消分享");
						}
					}

					@Override
					public void onComplete(Object response) {
						// TODO Auto-generated method stub
						ToastUtils.showToast(QQShareActivity.this,
								response.toString());
					}

					@Override
					public void onError(UiError e) {
						// TODO Auto-generated method stub
						ToastUtils.showToast(QQShareActivity.this, "节目出错");
					}

				});
			}
		}).start();
	}

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.qq_share_activity);
	}

	@Override
	public void dealLogicBeforeInitView() {
		// TODO Auto-generated method stub
		imagePath = getIntent().getStringExtra("imagepath");
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		title = (TextView) findViewById(R.id.shareqq_title);
		summary = (TextView) findViewById(R.id.shareqq_summary);

		findViewById(R.id.shareqq_commit).setOnClickListener(this);

		// mContainer_qzone_special =
		// findViewById(R.id.qzone_specail_radio_container);
		mContainer_title = findViewById(R.id.qqshare_title_container);
		mContainer_summary = findViewById(R.id.qqshare_summary_container);

		mRadioBtnShareTypeDefault = (RadioButton) findViewById(R.id.radioBtn_share_type_default);
		mRadioBtnShareTypeDefault.setOnClickListener(this);
		mRadioBtnShareTypeImg = (RadioButton) findViewById(R.id.radioBtn_share_type_image);
		mRadioBtnShareTypeImg.setOnClickListener(this);

		initShareUI(shareType);
		QQAuth mQQAuth = QQAuth.createInstance(Constants.QQ_APPID, this);
		mQQShare = new QQShare(this, mQQAuth.getQQToken());
	}

	@Override
	public void dealLogicAfterInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickEvent(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		case R.id.shareqq_commit: // 提交
			final Bundle params = new Bundle();
			if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
				params.putString(QQShare.SHARE_TO_QQ_TITLE, title.getText()
						.toString());
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl
						.getText().toString());
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary.getText()
						.toString());
			}
			if (shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imagePath);
			} else {
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imagePath);
			}
			params.putString(
					shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL
							: QQShare.SHARE_TO_QQ_IMAGE_URL, imagePath);
			params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "迈瑞思行车记录仪");
			params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
			params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

			doShareToQQ(params);
			return;

		case R.id.radioBtn_share_type_default:
			shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
			break;
		case R.id.radioBtn_share_type_image:
			shareType = QQShare.SHARE_TO_QQ_TYPE_IMAGE;
			break;
		}
		initShareUI(shareType);

	}
}
