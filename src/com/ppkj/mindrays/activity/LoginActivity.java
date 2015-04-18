package com.ppkj.mindrays.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.bean.QQToken;
import com.ppkj.mindrays.bean.UserResponse;
import com.ppkj.mindrays.network.HttpRequestAsyncTask;
import com.ppkj.mindrays.network.HttpRequestAsyncTask.OnCompleteListener;
import com.ppkj.mindrays.network.Request;
import com.ppkj.mindrays.network.RequestMaker;
import com.ppkj.mindrays.utils.StringUtils;
import com.ppkj.mindrays.utils.ToastUtils;
import com.ppkj.mindrays.view.LoadingDialog;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginActivity extends BaseActivity {
	private TextView btn_reg;
	private TextView btn_back;
	private TextView btn_forgetPwd;
	private EditText et_username;
	private EditText et_passwd;
	private ImageButton qq;
	private ImageButton sina;
	private ImageButton weixin;
	private static final String MESSAGE_STRING = "登录中，请稍候...";
	// /////
	private LoadingDialog mLoadingDialog;
	private RequestMaker mRequestMaker;
	// ///
	private QQAuth mQQAuth;
	private Tencent mTencent;
	private QQToken mQToken;
	private UserInfo mInfo;

	// ///
	private Oauth2AccessToken mAccessToken;
	private WeiboAuth mWeiboAuth;
	private SsoHandler mSsoHandler;
	private UsersAPI mUsersAPI;

	// ////

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.userlogin_me);
	}

	@Override
	public void dealLogicBeforeInitView() {
		// TODO Auto-generated method stub
		// ////
		mLoadingDialog = new LoadingDialog(this);
		mRequestMaker = RequestMaker.getInstance();
		// //

		mQQAuth = QQAuth.createInstance(Constants.QQ_APPID,
				getApplicationContext());
		mTencent = Tencent.createInstance(Constants.QQ_APPID,
				LoginActivity.this);
		mQToken = mSharedPrefHelper.readQQToken();

		// ///////////////////////////////
		mWeiboAuth = new WeiboAuth(this, Constants.SINAWEIBO_USER_APPKEY,
				Constants.SINAWEIBO_USER_REDIRECTURL,
				Constants.SINAWEIBO_USER_SCOPE);
		// //////////////////////////

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction("userconfirmed");
		registerReceiver(userConfirmed, mFilter);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(userConfirmed);
	}

	private BroadcastReceiver userConfirmed = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("userconfirmed")) {
				finish();
			}
		}

	};

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		btn_reg = (TextView) findViewById(R.id.userlogin_reg);
		btn_back = (TextView) findViewById(R.id.title_back);
		btn_forgetPwd = (TextView) findViewById(R.id.login_forgetpwd);
		btn_forgetPwd.setOnClickListener(this);
		btn_reg.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		et_username = (EditText) findViewById(R.id.user_input_mail);
		et_passwd = (EditText) findViewById(R.id.user_input_password);
		qq = (ImageButton) findViewById(R.id.login_by_qq);
		sina = (ImageButton) findViewById(R.id.login_by_sina);
		weixin = (ImageButton) findViewById(R.id.login_by_weixin);
		qq.setOnClickListener(this);
		sina.setOnClickListener(this);
		weixin.setOnClickListener(this);
	}

	@Override
	public void dealLogicAfterInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickEvent(View view) {
		Intent next = new Intent();
		switch (view.getId()) {
		case R.id.user_login:
			doLogin();
			break;
		case R.id.userlogin_reg:
			next.setClass(this, RegActivity.class);
			startActivity(next);
			break;
		case R.id.title_back:
			finish();
			break;
		case R.id.login_forgetpwd:
			next.setClass(this, RePwdActivity.class);
			startActivity(next);
			break;
		case R.id.login_by_qq:
			loginbyqq();
			break;
		case R.id.login_by_sina:
			loginbysina();
			break;
		case R.id.login_by_weixin:
			loginbyweixin();
			break;
		default:
			break;
		}
	}

	/**
	 * 微信登录
	 */
	private void loginbyweixin() {
		// TODO Auto-generated method stub

	}

	/**
	 * 新浪登录
	 */
	private void loginbysina() {
		// TODO Auto-generated method stub
		mSsoHandler = new SsoHandler(this, mWeiboAuth);
		mSsoHandler.authorize(new AuthListener());
	}

	/**
	 * QQ登录
	 */
	private void loginbyqq() {

		if (!mQQAuth.isSessionValid()) {
			IUiListener listener = new BaseUiListener();
			mTencent.login(this, "all", listener);
		} else {
			mQQAuth.logout(this);

		}
	}

	private void doLogin() {
		String uname = et_username.getText().toString();
		String pwd = et_passwd.getText().toString();
		if (StringUtils.isEmpty(uname)) {
			ToastUtils.showLongToast(this, "请输入帐号!");
			return;
		}

		if (StringUtils.isEmpty(pwd)) {
			ToastUtils.showLongToast(this, "请输入密码!");
			return;
		}

		Request login = mRequestMaker.getLogin(uname, pwd);
		HttpRequestAsyncTask hat = new HttpRequestAsyncTask();
		hat.execute(login);
		mLoadingDialog.show(MESSAGE_STRING);
		hat.setOnCompleteListener(new OnCompleteListener<UserResponse>() {

			@Override
			public void onComplete(UserResponse result, String resultString) {
				mLoadingDialog.dismiss();
				if (result != null) {
					if (result.reCode == 0) {
						mSharedPrefHelper.writeUserBean(result.userinfo);
						softApplication.isLogin = true;
						finish();
					} else {
						ToastUtils.showLongToast(LoginActivity.this,
								result.reMsg);
					}

				}
			}
		});
	}

	public void doLoginOther(String other_type, String other_id, String name,
			String access_secret, String access_key, String expires_in) {

		Request login = RequestMaker.getInstance().getLoginOther(other_id,
				other_type, name, access_secret, access_key, expires_in);
		HttpRequestAsyncTask hat = new HttpRequestAsyncTask();
		hat.execute(login);
		mLoadingDialog.show(MESSAGE_STRING);
		hat.setOnCompleteListener(new OnCompleteListener<UserResponse>() {

			@Override
			public void onComplete(UserResponse result, String resultString) {
				mLoadingDialog.dismiss();
				if (result != null) {
					if (result.reCode == 0) {
						mSharedPrefHelper.writeUserBean(result.userinfo);
						softApplication.isLogin = true;
						finish();
					} else {
						ToastUtils.showLongToast(LoginActivity.this,
								result.reMsg);
					}
				} else {
					ToastUtils.showLongToast(LoginActivity.this,
							"登录时发生错误，请重新登录");
				}

			}
		});
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			// Util.showResultDialog(MainActivity.this, response.toString(),
			// "登录成功");
			JSONObject values = JSON.parseObject(response.toString());
			mQToken.setQqid(values.getString("openid"));
			mQToken.setQqToken(values.getString("access_token"));
			mQToken.setQqExpires_in(values.getString("expires_in"));
			updateUserInfo();
		}

		@Override
		public void onError(UiError e) {
			ToastUtils.showLongToast(LoginActivity.this, "onError: "
					+ e.errorDetail);
			// Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			ToastUtils.showLongToast(LoginActivity.this, "onCancel: ");
			// Util.dismissDialog();
		}
	}

	private void updateUserInfo() {
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {

				}

				@Override
				public void onComplete(final Object response) {
					doLoadQQComplete(JSON.parseObject(response.toString()));
				}

				@Override
				public void onCancel() {

				}
			};
			mInfo = new UserInfo(this, mQQAuth.getQQToken());
			mInfo.getUserInfo(listener);
		}
	}

	protected void doLoadQQComplete(JSONObject response) {
		// TODO Auto-generated method stub
		mQToken.setQqName(response.getString("nickname"));
		mSharedPrefHelper.writeQQToken(mQToken);
		doLoginOther("qq", mQToken.getQqid(), mQToken.getQqName(), "",
				mQToken.getQqToken(), mQToken.getQqExpires_in());
	}

	// ////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////
	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@SuppressLint("NewApi")
		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				mLoadingDialog.show(MESSAGE_STRING);

				mSharedPrefHelper.writeSinaToken(mAccessToken);
				mUsersAPI = new UsersAPI(mAccessToken);
				mUsersAPI.show(mAccessToken.getUid(), new RequestListener() {

					@Override
					public void onWeiboException(WeiboException arg0) {

					}

					@Override
					public void onComplete(String response) {
						if (!TextUtils.isEmpty(response)) {
							// 调用 User#parse 将JSON串解析成User对象
							User user = User.parse(response);
							mSharedPrefHelper.writeSinaName(user.screen_name);
							doLoginOther("sina", mAccessToken.getUid(),
									user.screen_name, "", mAccessToken
											.getToken(), String
											.valueOf(mAccessToken
													.getExpiresTime()));
						}
					}
				});
			}
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub
			arg0.printStackTrace();
		}
	}
}
