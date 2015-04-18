package com.ppkj.mindrays.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.bean.UserResponse;
import com.ppkj.mindrays.network.HttpRequestAsyncTask;
import com.ppkj.mindrays.network.HttpRequestAsyncTask.OnCompleteListener;
import com.ppkj.mindrays.network.Request;
import com.ppkj.mindrays.network.RequestMaker;
import com.ppkj.mindrays.utils.StringUtils;
import com.ppkj.mindrays.utils.ToastUtils;
import com.ppkj.mindrays.view.LoadingDialog;

public class RegActivity extends BaseActivity {
    private TextView btn_sendcode;
    private EditText mail;
    private EditText pwd;
    private EditText rePwd;
    // //
    private LoadingDialog mLoadingDialog;
    private RequestMaker mRequestMaker;

    @Override
    public void setContentLayout() {
	// TODO Auto-generated method stub
	setContentView(R.layout.userreg_me);
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
	btn_sendcode = (TextView) findViewById(R.id.userreg_getvericode);
	btn_sendcode.setOnClickListener(this);
	mail = (EditText) findViewById(R.id.user_input_mail);
	pwd = (EditText) findViewById(R.id.user_input_pwd);
	rePwd = (EditText) findViewById(R.id.user_input_comfirm_pwd);
    }

    @Override
    public void dealLogicAfterInitView() {

    }

    @Override
    public void onClickEvent(View view) {
	// TODO Auto-generated method stub
	switch (view.getId()) {
	case R.id.userreg_getvericode:
	    sendMail();

	    break;

	default:
	    break;
	}
    }

    private void sendMail() {
	String mail = this.mail.getText().toString();
	String pwd = this.pwd.getText().toString();
	String rePwd = this.rePwd.getText().toString();

	if (StringUtils.isEmpty(mail)) {
	    ToastUtils.showLongToast(RegActivity.this, "邮箱不能为空");
	    return;
	}

	if (StringUtils.isEmpty(pwd)) {
	    ToastUtils.showLongToast(RegActivity.this, "密码不能为空");
	    return;
	}

	if (StringUtils.isEmpty(rePwd)) {
	    ToastUtils.showLongToast(RegActivity.this, "确认密码不能为空");
	    return;
	}
	if (!pwd.equals(rePwd)) {
	    ToastUtils.showLongToast(RegActivity.this, "两次密码不匹配");
	    return;
	}

	mLoadingDialog.show("请求中，请稍候...");
	Request request = mRequestMaker.getReg(mail, pwd, rePwd);
	HttpRequestAsyncTask hat = new HttpRequestAsyncTask();
	hat.execute(request);
	hat.setOnCompleteListener(new OnCompleteListener<UserResponse>() {

	    @Override
	    public void onComplete(UserResponse result, String resultString) {
		mLoadingDialog.dismiss();
		if (result != null) {
		    if (result.userinfo != null) {
			softApplication.isLogin = true;
			mSharedPrefHelper.writeUserBean(result.userinfo);

			Intent next = new Intent();
			next.setClass(RegActivity.this, InfoActivity.class);
			next.putExtra("isFromReg", true);
			startActivity(next);
			finish();
		    }
		}
		ToastUtils.showLongToast(RegActivity.this, "注册时发生错误，请重新保存");
	    }
	});
    }

}
