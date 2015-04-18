package com.ppkj.mindrays.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;

import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.base.BaseFragment;
import com.ppkj.mindrays.bean.UserBean;
import com.ppkj.mindrays.fragment.CloudFragment;
import com.ppkj.mindrays.fragment.FileFragment;
import com.ppkj.mindrays.fragment.MeFragment;
import com.ppkj.mindrays.fragment.VideoFragment;
import com.ppkj.mindrays.service.MediaScannerService;
import com.ppkj.mindrays.utils.FileUtils;
import com.ppkj.mindrays.utils.ToastUtils;

public class MindTracker extends BaseActivity {
    private static final String TAG = MindTracker.class.getSimpleName();
    private SparseArray<BaseFragment> navigateMap = new SparseArray<BaseFragment>();
    private UserBean mUserBean;

    private void setContentViews() {
	FragmentManager fm = getSupportFragmentManager();

	// 添加导航内容

	navigateMap.clear();
	mapNaviToFragment(R.id.navi_item_video, VideoFragment.getInstance()); // 视频
	mapNaviToFragment(R.id.navi_item_file, FileFragment.getInstance()); // 文件
	mapNaviToFragment(R.id.navi_item_cloud, CloudFragment.getInstance()); // 云存储
	mapNaviToFragment(R.id.navi_item_me, MeFragment.getInstance()); // 我
	// 设置首页默认显示
	replaceFragment(fm, R.id.navi_item_video);

    }

    /**
     * 初始化map
     * 
     * @param id
     *            导航view ID
     * @param fragment
     */
    private void mapNaviToFragment(int id, BaseFragment fragment) {
	View view = findViewById(id);
	// LogUtil.log(TAG, "mapNaviToFragment " + id + " view: " + view);
	view.setOnClickListener(this);
	view.setSelected(false);
	navigateMap.put(id, fragment);
    }

    /**
     * 点击后，切换内容
     * 
     * @param view
     *            点击view
     * @return 点击view，是否为导航view
     */
    private boolean clickSwitchContent(View view) {
	int id = view.getId();
	if (navigateMap.indexOfKey(id) < 0) {
	    // 点击非导航view
	    return false;
	}
	// LogUtil.log(TAG,
	// "switchContent " + id + " select: " + view.isSelected()
	// + " view: " + view);
	if (!view.isSelected()) {
	    // 当前非选中状态：需切换到新内容
	    if (id == R.id.navi_item_me) {
		if (!softApplication.isLogin) {
		    Intent next = new Intent();
		    next.setClass(this, LoginActivity.class);
		    startActivity(next);
		    return true;
		}
	    }
	    replaceFragment(getSupportFragmentManager(), id);
	} else {

	}
	return true;
    }

    /**
     * 执行内容切换
     * 
     * @param fm
     * @param id
     *            导航view ID
     */
    private void replaceFragment(FragmentManager fm, int id) {
	softApplication.prePage = id;
	String tag = String.valueOf(id);
	// 执行替换
	FragmentTransaction trans = fm.beginTransaction();
	if (null == fm.findFragmentByTag(tag)) {
	    trans.replace(R.id.content_frame, navigateMap.get(id), tag);
	    // 不存在时，添加到stack，避免切换时，先前的被清除{fm.getFragments()}
	    // {存在时，不添加，避免BackStackEntry不断累加}
	    // LogUtil.log(TAG, "null +++ add to back");
	    trans.addToBackStack(tag);
	} else {
	    trans.replace(R.id.content_frame, fm.findFragmentByTag(tag), tag);
	}
	trans.commit();
	// LogUtil.log(TAG, "replace map: " + navigateMap.get(id) + "\n"
	// + "---- fm tag: " + fm.findFragmentByTag(tag));
	// 重置导航选中状态
	for (int i = 0, size = navigateMap.size(); i < size; i++) {
	    int curId = navigateMap.keyAt(i);
	    // LogUtil.log(TAG, "curId: " + curId);
	    if (curId == id) {
		findViewById(id).setSelected(true);
	    } else {
		findViewById(curId).setSelected(false);
	    }
	}
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (KeyEvent.KEYCODE_BACK == keyCode) {
	    if ((System.currentTimeMillis() - exitTime) > 2000) {
		ToastUtils.showLongToast(getApplicationContext(), "再按一次退出");
		exitTime = System.currentTimeMillis();
	    } else {
		finish();
	    }
	    return true;
	}
	return super.onKeyDown(keyCode, event);
    }

    public void showExitDialog() {
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    @Override
    public void setContentLayout() {
	// TODO Auto-generated method stub
	setContentView(R.layout.main_content_frame);
    }

    @Override
    public void dealLogicBeforeInitView() {
	// TODO Auto-generated method stub
	mUserBean = mSharedPrefHelper.readUserBean();
	if (mUserBean != null) {
	    softApplication.isLogin = true;
	}
	if (FileUtils.sdAvailable()) {

	    Intent intent = new Intent();
	    intent.setClass(getApplicationContext(), MediaScannerService.class);
	    intent.putExtra(MediaScannerService.EXTRA_DIRECTORY,
		    FileUtils.sdCard(this));
	    intent.putExtra(MediaScannerService.EXTRA_DIRECTORY1,
		    FileUtils.sdExtCard(this));

	    getApplicationContext().startService(intent);
	}
    }

    @Override
    public void initView() {
	// TODO Auto-generated method stub
	setContentViews();
    }

    @Override
    public void dealLogicAfterInitView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void onClickEvent(View v) {
	// TODO Auto-generated method stub
	if (clickSwitchContent(v)) {
	    return;
	}
	// 处理其他点击事件
    }

    public void setVideoFragment() {
	softApplication.isLogin = false;
	onClick(findViewById(R.id.navi_item_video));
    }

}
