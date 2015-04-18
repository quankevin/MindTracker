package com.ppkj.mindrays.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.adapter.SimplePagerAdapter;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.localbean.ImageFile;
import com.ppkj.mindrays.utils.ToastUtils;
import com.ppkj.mindrays.view.HackyViewPager;
import com.ppkj.mindrays.view.LoadingDialog;
import com.ppkj.mindrays.widget.ActionSheet;
import com.ppkj.mindrays.widget.ActionSheet.OnActionSheetSelected;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class ViewPagerActivity extends BaseActivity implements
	OnPageChangeListener, OnPhotoTapListener, OnActionSheetSelected,
	OnCancelListener {
    /** 支持发送到朋友圈最小版本号 */
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    private static final String ISLOCKED_ARG = "isLocked";
    private ArrayList<ImageFile> dataList;
    private ViewPager mViewPager;
    private LoadingDialog mloadingDialog;
    private String mShareMsg = "";

    private int selectPage = 1;
    private RelativeLayout rl_head;
    private TextView tv_title_head;
    private TextView top_share;

    private Oauth2AccessToken mSinaToken;
    private WeiboAuth mWeiboAuth;
    private SsoHandler mSsoHandler;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    /** IWXAPI 是第三方app和微信通信的openapi接口 */
    private IWXAPI api;

    private boolean isViewPagerActive() {
	return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
	if (isViewPagerActive()) {
	    outState.putBoolean(ISLOCKED_ARG,
		    ((HackyViewPager) mViewPager).isLocked());
	}
	super.onSaveInstanceState(outState);
    }

    @Override
    public void setContentLayout() {

	setContentView(R.layout.activity_view_pager);
    }

    @Override
    public void dealLogicBeforeInitView() {
	// TODO Auto-generated method stub
	dataList = getIntent().getParcelableArrayListExtra(
		ImageGridActivity.EXTRA_IMAGE_LIST);
	selectPage = getIntent().getIntExtra(
		ImageGridActivity.EXTRA_IMAGE_POSITION, 0);
	// 通过WXAPIFactory工厂，获取IWXAPI的实例
	api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);
	api.registerApp(Constants.WX_APP_ID);
	mSinaToken = mSharedPrefHelper.readSinaToken();
	mloadingDialog = new LoadingDialog(this);
	mShareMsg = "迈瑞思行车记录仪";
    }

    @Override
    public void initView() {
	// TODO Auto-generated method stub
	mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
	rl_head = (RelativeLayout) findViewById(R.id.view_pager_title);
	tv_title_head = (TextView) findViewById(R.id.title_head);
	top_share = (TextView) findViewById(R.id.title_share);
	findViewById(R.id.title_back).setOnClickListener(this);
	top_share.setOnClickListener(this);

    }

    @Override
    public void dealLogicAfterInitView() {
	// TODO Auto-generated method stub
	mViewPager.setAdapter(new SimplePagerAdapter(this, dataList, this));
	mViewPager.setOnPageChangeListener(this);
	tv_title_head.setText(selectPage + 1 + "/" + dataList.size());
	mViewPager.setCurrentItem(selectPage, true);
    }

    @Override
    public void onClickEvent(View view) {

	switch (view.getId()) {
	case R.id.title_share:
	    onShowShare();
	    break;
	case R.id.title_back:
	    finish();
	    break;
	default:
	    break;
	}
    }

    private void onShowShare() {
	// TODO Auto-generated method stub
	ActionSheet.showSheet(this, this, this);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
	selectPage = arg0;
	tv_title_head.setText(arg0 + 1 + "/" + dataList.size());
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
	if (rl_head.isShown()) {
	    rl_head.setVisibility(View.GONE);
	    // 进行全屏
	    setFullScreen();
	} else {
	    rl_head.setVisibility(View.VISIBLE);
	    quitFullScreen();
	}
    }

    private void setFullScreen() {
	// requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void quitFullScreen() {
	final WindowManager.LayoutParams attrs = getWindow().getAttributes();
	attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
	getWindow().setAttributes(attrs);
	getWindow()
		.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    }

    @Override
    public void onClickSheet(int whichButton) {
	Class<?> cls = null;
	switch (whichButton) {
	case 0:
	    onSinaShare();
	    break;
	case 1:
	    onWeixinShare();
	    return;
	case 2:
	    onWeixinFriendShare();
	    return;
	case 3:
	    cls = QZoneShareActivity.class;
	    break;
	case 4:
	    cls = QQShareActivity.class;
	    break;
	default:
	    break;
	}
	if (cls != null) {
	    Intent intent = new Intent(this, cls);
	    intent.putExtra("imagepath", dataList.get(selectPage)
		    .getPhotoPath());
	    startActivity(intent);
	}
    }

    /**
     * 微信分享
     */
    private void onWeixinShare() {
	if (api.isWXAppInstalled()) {
	    shareWeixinTo(false);
	} else {
	    ToastUtils.showToast(this, "您的手机没有下载微信，请您下载最新版本！");
	}
    }

    private void onWeixinFriendShare() {
	int wxSdkVersion = api.getWXAppSupportAPI();
	if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
	    shareWeixinTo(true);

	} else {
	    ToastUtils.showToast(this, "您的微信版本不支持分享到朋友圈，请您下载最新版本！");
	}
    }

    /**
     * 微信分享之朋友圈或是微博
     * 
     * @paramn isTimeline 若为ture则分享至朋友圈，否则直接分享给微信好友
     */
    private void shareWeixinTo(boolean isTimeline) {
	// 初始化一个WXTextObject对象
	WXTextObject textObj = new WXTextObject();
	textObj.text = mShareMsg;

	// 用WXTextObject对象初始化一个WXMediaMessage对象
	WXMediaMessage msg = new WXMediaMessage();
	msg.mediaObject = textObj;
	// 发送文本类型的消息时，title字段不起作用
	msg.description = mShareMsg;

	// 构造一个Req
	SendMessageToWX.Req req = new SendMessageToWX.Req();
	req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
	req.message = msg;
	req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline
		: SendMessageToWX.Req.WXSceneSession;

	// 调用api接口发送数据到微信
	api.sendReq(req);
    }

    private String buildTransaction(final String type) {
	return (type == null) ? String.valueOf(System.currentTimeMillis())
		: type + System.currentTimeMillis();
    }

    // /////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////
    /**
     * 新浪分享，需要验证Token信息，若没有Token则需要授权验证后才可以分享
     */
    private void onSinaShare() {
	if (mSinaToken != null && mSinaToken.isSessionValid()) {
	    mStatusesAPI = new StatusesAPI(mSinaToken);
	    String pathName = dataList.get(selectPage).getPhotoPath();
	    Bitmap b = BitmapFactory.decodeFile(pathName);

	    // mStatusesAPI.update(mShareMsg, null, null, mListener);
	    mStatusesAPI.upload(mShareMsg, comp(b), null, null, mListener);
	    mloadingDialog.show("发送消息中，请稍候...");
	} else {
	    mWeiboAuth = new WeiboAuth(this, Constants.SINAWEIBO_USER_APPKEY,
		    Constants.SINAWEIBO_USER_REDIRECTURL,
		    Constants.SINAWEIBO_USER_SCOPE);
	    mSsoHandler = new SsoHandler(this, mWeiboAuth);
	    mSsoHandler.authorize(new AuthListener());
	}
    }

    /**
     * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
     * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
     * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
     * SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

	@Override
	public void onComplete(Bundle values) {
	    // 从 Bundle 中解析 Token

	    mSinaToken = Oauth2AccessToken.parseAccessToken(values);
	    if (mSinaToken.isSessionValid()) {
		mSharedPrefHelper.writeSinaToken(mSinaToken);
	    } else {

	    }
	}

	@Override
	public void onCancel() {

	}

	@Override
	public void onWeiboException(WeiboException arg0) {

	}
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
	@Override
	public void onComplete(String response) {
	    mloadingDialog.dismiss();
	    if (!TextUtils.isEmpty(response)) {
		if (response.startsWith("{\"created_at\"")) {
		    // 调用 Status#parse 解析字符串成微博对象
		    // Status status = Status.parse(response);
		    // Toast.makeText(ShareActivity.this,
		    // "发送一送微博成功, id = " + status.id,
		    // Toast.LENGTH_LONG).show();

		    ToastUtils.showToast(ViewPagerActivity.this, "分享成功！");

		} else {

		    ToastUtils.showToast(ViewPagerActivity.this, "分享失败！");

		}
	    }
	}

	@Override
	public void onWeiboException(WeiboException arg0) {
	    // TODO Auto-generated method stub

	}
    };

    private Bitmap comp(Bitmap image) {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
	    baos.reset();// 重置baos即清空baos
	    image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
	}
	ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
	BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
	newOpts.inJustDecodeBounds = true;
	Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
	newOpts.inJustDecodeBounds = false;
	int w = newOpts.outWidth;
	int h = newOpts.outHeight;
	// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
	float hh = 800f;// 这里设置高度为800f
	float ww = 480f;// 这里设置宽度为480f
	// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	int be = 1;// be=1表示不缩放
	if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
	    be = (int) (newOpts.outWidth / ww);
	} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
	    be = (int) (newOpts.outHeight / hh);
	}
	if (be <= 0)
	    be = 1;
	newOpts.inSampleSize = be;// 设置缩放比例
	// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	isBm = new ByteArrayInputStream(baos.toByteArray());
	bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
	return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 压缩bitmap
     * 
     * @param image
     * @return
     */
    private Bitmap compressImage(Bitmap image) {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	int options = 100;
	while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
	    baos.reset();// 重置baos即清空baos
	    image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
	    options -= 10;// 每次都减少10
	}
	ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
	Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
	return bitmap;
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

}
