package com.ppkj.mindrays.wxapi;

import android.view.View;

import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.utils.ToastUtils;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信回调Activity，只能在工程下建立wxapi包，新建WXEntryActivity
 * 
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dealLogicBeforeInitView() {
		// TODO Auto-generated method stub

		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dealLogicAfterInitView() {
		// TODO Auto-generated method stub
		api.handleIntent(getIntent(), this);
	}

	@Override
	public void onClickEvent(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResp(BaseResp arg0) {
		// TODO Auto-generated method stub
		switch (arg0.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			ToastUtils.showToast(WXEntryActivity.this, "分享成功！");

			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:

			break;
		default:
			break;
		}
		finish();
	}

}
