package com.ppkj.mindrays.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.bean.MsgResponse;
import com.ppkj.mindrays.network.HttpRequestAsyncTask;
import com.ppkj.mindrays.network.HttpRequestAsyncTask.OnCompleteListener;
import com.ppkj.mindrays.network.Request;
import com.ppkj.mindrays.network.RequestMaker;
import com.ppkj.mindrays.utils.StringUtils;
import com.ppkj.mindrays.utils.ToastUtils;
import com.ppkj.mindrays.view.LoadingDialog;

public class RePwdActivity extends BaseActivity {
	private TextView btn_sendcode;
	private EditText et_uname;
	private RequestMaker mRequestMaker;
	private LoadingDialog mLoadingDialog;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.userrepwd_me);
	}

	@Override
	public void dealLogicBeforeInitView() {
		// TODO Auto-generated method stub
		mRequestMaker = RequestMaker.getInstance();
		mLoadingDialog = new LoadingDialog(this);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		btn_sendcode = (TextView) findViewById(R.id.userrepwd_getvericode);
		btn_sendcode.setOnClickListener(this);
		et_uname = (EditText) findViewById(R.id.user_input_mail);
	}

	@Override
	public void dealLogicAfterInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickEvent(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.userrepwd_getvericode:
			getPassword();
			break;

		default:
			break;
		}
	}

	private void getPassword() {
		String uname = et_uname.getText().toString();
		if (StringUtils.isEmpty(uname)) {
			ToastUtils.showLongToast(this, "请输入您的邮箱");
			return;
		}
		mLoadingDialog.show("发送中，请稍候...");
		Request request = mRequestMaker.getRePwd(uname);
		HttpRequestAsyncTask hat = new HttpRequestAsyncTask();
		hat.execute(request);
		hat.setOnCompleteListener(new OnCompleteListener<MsgResponse>() {

			@Override
			public void onComplete(MsgResponse result, String resultString) {
				mLoadingDialog.dismiss();
				if (result != null) {
					ToastUtils.showLongToast(RePwdActivity.this, result.reMsg);
				}
			}
		});
	}
}
