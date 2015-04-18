package com.ppkj.mindrays.fragment;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.activity.AboutActivity;
import com.ppkj.mindrays.activity.InfoActivity;
import com.ppkj.mindrays.activity.MindTracker;
import com.ppkj.mindrays.activity.SuggestAcitivity;
import com.ppkj.mindrays.base.BaseFragment;
import com.ppkj.mindrays.bean.UserBean;
import com.ppkj.mindrays.bean.VersionResponse;
import com.ppkj.mindrays.network.HttpRequestAsyncTask;
import com.ppkj.mindrays.network.HttpRequestAsyncTask.OnCompleteListener;
import com.ppkj.mindrays.network.Request;
import com.ppkj.mindrays.network.RequestMaker;
import com.ppkj.mindrays.utils.ToastUtils;
import com.ppkj.mindrays.utils.UpdateDownUtil;
import com.ppkj.mindrays.utils.VerifyCheck;
import com.ppkj.mindrays.utils.VersionUtil;
import com.ppkj.mindrays.view.LoadingDialog;
import com.ppkj.mindrays.widget.RoundedImageView;

public class MeFragment extends BaseFragment {
    private RelativeLayout btn_userinfo;
    private RelativeLayout btn_suggest;
    private RelativeLayout btn_upgrade;
    private RelativeLayout btn_aboutmrs;
    private Button btn_userlogout;
    private RoundedImageView mImageView;
    // private BitmapUtils bitmapUtils;
    //
    private LoadingDialog mLoadingDialog;
    private RequestMaker mRequestMaker;
    // //
    private Notification notification;
    private NotificationManager notificationManager;
    private static final int NO_SD_CARD = 1;
    private static final int PROGRESS_UPDATE = 2;
    private static final int PROGRESS_COMPLETE = 3;
    private boolean isUpdating;
    private File downloadAppFile;
    private static MeFragment mf;

    public static MeFragment getInstance() {
	if (mf == null) {
	    mf = new MeFragment();
	}
	return mf;
    }

    // ///

    @Override
    public void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	notificationManager = (NotificationManager) getActivity()
		.getSystemService(Context.NOTIFICATION_SERVICE);
	notification = new Notification();
    }

    // //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.main_frg_me, container, false);
	getViews(view);

	return view;
    }

    @Override
    public void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	setHeader();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);
	mLoadingDialog = new LoadingDialog(getActivity());
	mRequestMaker = RequestMaker.getInstance();
	// bitmapUtils = new BitmapUtils(getActivity());
	// bitmapUtils.configDiskCacheEnabled(true);
	// bitmapUtils.configMemoryCacheEnabled(true);
	// bitmapUtils.configThreadPoolSize(1);

	setHeader();
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// Bitmap bitmap = null;
	//
	// bitmap = NetUtil.getbitmap(mUserBean.getHead_url());
	//
	// Message msg = new Message();
	// msg.obj = bitmap;
	// msg.what = 1;
	// mHandler.sendMessage(msg);
	// }
	// }).start();

    }

    // Handler mHandler = new Handler() {
    //
    // @Override
    // public void handleMessage(Message msg) {
    // if (msg.what == 1) {
    // Bitmap bitmap = (Bitmap) msg.obj;
    // mImageView.setImageBitmap(bitmap);
    // mImageView.setVisibility(android.view.View.VISIBLE);
    // }
    // }
    //
    // };
    private void setHeader() {
	final UserBean mUserBean = mSharedPrefHelper.readUserBean();
	if (mUserBean.getHead_url() != null
		|| !mUserBean.getHead_url().equals("")) {
	    ImageLoader.getInstance().displayImage(mUserBean.getHead_url(),
		    mImageView);
	} else {
	    mImageView.setImageResource(R.drawable.default_user_head);
	}
    }

    private void getViews(View v) {
	btn_aboutmrs = (RelativeLayout) v.findViewById(R.id.user_about_click);
	btn_suggest = (RelativeLayout) v.findViewById(R.id.user_suggest_click);
	btn_upgrade = (RelativeLayout) v.findViewById(R.id.user_upgrade_click);
	btn_userinfo = (RelativeLayout) v
		.findViewById(R.id.user_userinfo_click);
	btn_userlogout = (Button) v.findViewById(R.id.user_logout);
	mImageView = (RoundedImageView) v.findViewById(R.id.user_head);
	btn_userinfo.setOnClickListener(this);
	btn_suggest.setOnClickListener(this);
	btn_upgrade.setOnClickListener(this);
	btn_aboutmrs.setOnClickListener(this);
	btn_userlogout.setOnClickListener(this);
    }

    /**
     * 版本更新
     */
    private void getUpgrade() {
	mLoadingDialog.show("发送中，请稍候...");
	String uname = "";
	Request request = mRequestMaker.getVersion("1.6", "3", "3", "0");
	HttpRequestAsyncTask hat = new HttpRequestAsyncTask();
	hat.execute(request);
	hat.setOnCompleteListener(new OnCompleteListener<VersionResponse>() {

	    @Override
	    public void onComplete(VersionResponse result, String resultString) {
		mLoadingDialog.dismiss();
		if (result != null) {
		    if (result.versionBean.getShowflag().equals("1")) {

			Toast.makeText(getActivity(), result.reMsg,
				Toast.LENGTH_SHORT).show();
			return;
		    }
		    if (VersionUtil.getVersionCode(getActivity()) < Float
			    .valueOf(result.versionBean.getVersion())) {
			appUpgradDialog(false, result.versionBean.getContent(),
				result.versionBean.getApp_url());
		    }
		}
	    }
	});
    }

    /**
     * 更新dialog
     */
    private void appUpgradDialog(final boolean forcedUpgrade, final String msg,
	    final String upgradeUrl) {
	AlertDialog.Builder builder = new Builder(getActivity());
	builder.setTitle("升级提示");
	builder.setMessage("检测到新版本，确认升级？\n\n更新内容：\n" + msg);
	builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		if (!VerifyCheck.isUrl(upgradeUrl)
			|| upgradeUrl.endsWith(".apk")) {
		    ToastUtils.showLongToast(getActivity(), "下载地址不正确。");
		    return;
		}
		if (isUpdating) {
		    ToastUtils.showLongToast(getActivity(), "当前正在升级");
		} else {
		    // 通知栏显示下载通知
		    showDownLoadNotice();

		    // 子线程下载更新
		    new Thread() {
			public void run() {
			    isUpdating = true;// 正在下载App
			    // 文件格式
			    if (upgradeUrl != null) {
				UpdateDownUtil.downLoadNewApp(handler,
					upgradeUrl, downloadAppFile);
			    }
			};
		    }.start();
		}

		dialog.dismiss();
	    }
	});

	builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
	    }
	});

	builder.create().show();
    }

    /**
     * 设置更新通知栏
     */
    private void showDownLoadNotice() {
	// 下载过程中点击通知栏回到程序
	Intent noticeIntent = new Intent(getActivity(), MindTracker.class);
	noticeIntent.setAction(Intent.ACTION_MAIN);
	noticeIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),
		0, noticeIntent, 0);

	// 设置通知的显示内容
	notification.icon = R.drawable.ic_launcher;
	notification.tickerText = "";
	notification.contentView = new RemoteViews(getActivity()
		.getPackageName(), R.layout.update_notifiview);
	notification.contentIntent = pendingIntent;
	notificationManager.notify(0, notification);
    }

    /**
     * 更新状态变更处理
     */
    private Handler handler = new Handler() {
	public void handleMessage(android.os.Message msg) {
	    switch (msg.what) {
	    case NO_SD_CARD:// 没有SD卡
		isUpdating = false;
		Toast.makeText(getActivity(), "升级失败，请插入SD卡", Toast.LENGTH_SHORT)
			.show();
		break;
	    case PROGRESS_UPDATE:// 进度条更新
		notification.contentView.setProgressBar(
			R.id.update_progressbar, 100, msg.arg1, false);
		notificationManager.notify(0, notification);
		break;
	    case PROGRESS_COMPLETE:// 更新完成
		isUpdating = false;
		notificationManager.cancel(0);
		VersionUtil
			.installLoadedApkFile(getActivity(), downloadAppFile);
		break;
	    }
	};
    };

    @Override
    public void onClickEvent(View v) {
	// TODO Auto-generated method stub

	Intent next = new Intent();
	switch (v.getId()) {

	case R.id.user_about_click:
	    next.setClass(getActivity(), AboutActivity.class);
	    startActivity(next);
	    break;
	case R.id.user_suggest_click:
	    next.setClass(getActivity(), SuggestAcitivity.class);
	    startActivity(next);
	    break;
	case R.id.user_upgrade_click:
	    getUpgrade();
	    break;
	case R.id.user_userinfo_click:
	    next.setClass(getActivity(), InfoActivity.class);
	    next.putExtra("isFromReg", false);
	    startActivity(next);
	    break;
	case R.id.user_logout:
	    mSharedPrefHelper.cleanUserBean();
	    ((MindTracker) getActivity()).setVideoFragment();
	    break;
	}

    }
}
