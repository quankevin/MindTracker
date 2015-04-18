package com.ppkj.mindrays.activity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.bean.MsgResponse;
import com.ppkj.mindrays.bean.UserBean;
import com.ppkj.mindrays.network.HttpRequestAsyncTask;
import com.ppkj.mindrays.network.HttpRequestAsyncTask.OnCompleteListener;
import com.ppkj.mindrays.network.Request;
import com.ppkj.mindrays.network.RequestMaker;
import com.ppkj.mindrays.utils.StringUtils;
import com.ppkj.mindrays.utils.ToastUtils;
import com.ppkj.mindrays.utils.VersionUtil;
import com.ppkj.mindrays.view.LoadingDialog;

public class SuggestAcitivity extends BaseActivity {
	private TextView btn_sendcode;
	private EditText et_suggest;
	private RequestMaker mRequestMaker;
	private LoadingDialog mLoadingDialog;

	@Override
	public void setContentLayout() {
		setContentView(R.layout.usersuggest_me);
	}

	@Override
	public void dealLogicBeforeInitView() {
		mLoadingDialog = new LoadingDialog(this);
		mRequestMaker = RequestMaker.getInstance();
	}

	@Override
	public void initView() {
		btn_sendcode = (TextView) findViewById(R.id.usersuggest_send);
		btn_sendcode.setOnClickListener(this);
		et_suggest = (EditText) findViewById(R.id.user_input_suggest);
		findViewById(R.id.title_back).setOnClickListener(this);
	}

	@Override
	public void dealLogicAfterInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickEvent(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.usersuggest_send:
			onSendSuggest();
			break;

		default:
			break;
		}
	}

	/**
	 * 获取mac地址作为唯一识别码
	 * 
	 * @return
	 */
	private String getUUID() {
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
		return m_szWLANMAC;
	}

	/**
	 * 
	 * 发送
	 * 
	 */
	private void onSendSuggest() {
		String suggest = et_suggest.getText().toString();
		if (StringUtils.isEmpty(suggest)) {
			ToastUtils.showLongToast(this, "您的建议不能为空..");
			return;
		}
		UserBean ub = mSharedPrefHelper.readUserBean();
		mLoadingDialog.show("发送中，请稍候...");
		Request params = mRequestMaker.getFeedback(ub.getUser_id(), getUUID(),
				"android", android.os.Build.MODEL,
				android.os.Build.VERSION.RELEASE,
				VersionUtil.getVersionName(this), suggest);
		HttpRequestAsyncTask hat = new HttpRequestAsyncTask();
		hat.execute(params);
		hat.setOnCompleteListener(new OnCompleteListener<MsgResponse>() {

			@Override
			public void onComplete(MsgResponse result, String resultString) {
				// TODO Auto-generated method stub
				mLoadingDialog.dismiss();
				if (result != null) {
					ToastUtils.showLongToast(SuggestAcitivity.this,
							result.reMsg);
					finish();
				}
			}
		});
	}
}
