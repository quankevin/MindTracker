package com.ppkj.mindrays.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.ppkj.mindrays.MainApp;
import com.ppkj.mindrays.localbean.PODocu;
import com.ppkj.mindrays.utils.FileUtils;
import com.ppkj.mindrays.utils.LogUtil;
import com.ppkj.mindrays.utils.PinyinUtils;
import com.ppkj.mindrays.utils.StringUtils;

/** 文件扫描 */
public class MediaScannerService extends Service implements Runnable {

    private static final String SERVICE_NAME = "com.ppkj.mindrays.service.MediaScannerService";
    /** 扫描文件夹 */
    public static final String EXTRA_DIRECTORY = "scan_directory";
    public static final String EXTRA_DIRECTORY1 = "scan_directory1";
    /** 扫描文件 */
    public static final String EXTRA_FILE_PATH = "scan_file";
    public static final String EXTRA_MIME_TYPE = "mimetype";

    public static final int SCAN_STATUS_NORMAL = -1;
    /** 开始扫描 */
    public static final int SCAN_STATUS_START = 0;
    /** 正在扫描 扫描到一个视频文件 */
    public static final int SCAN_STATUS_RUNNING = 1;
    /** 扫描完成 */
    public static final int SCAN_STATUS_END = 2;
    /** 观察 */
    private ArrayList<IMediaScannerObserver> observers = new ArrayList<IMediaScannerObserver>();
    private ConcurrentHashMap<String, String> mScanMap = new ConcurrentHashMap<String, String>();

    /** 当前状态 */
    private volatile int mServiceStatus = SCAN_STATUS_NORMAL;

    // private DbHelper<PODocu> mDbHelper;
    // private Map<String, Object> mDbWhere = new HashMap<String, Object>(2);
    // private DbUtils db;

    @Override
    public void onCreate() {
	super.onCreate();

	// mDbHelper = new DbHelper<PODocu>();
    }

    /** 是否正在运行 */
    public static boolean isRunning() {
	ActivityManager manager = (ActivityManager) MainApp.getInstance()
		.getSystemService(Context.ACTIVITY_SERVICE);
	for (RunningServiceInfo service : manager
		.getRunningServices(Integer.MAX_VALUE)) {
	    if (SERVICE_NAME.equals(service.service.getClassName()))
		return true;
	}
	return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	if (intent != null)
	    parseIntent(intent);

	return super.onStartCommand(intent, flags, startId);
    }

    /** 解析Intent */
    private void parseIntent(final Intent intent) {
	final Bundle arguments = intent.getExtras();
	if (arguments != null) {
	    if (arguments.containsKey(EXTRA_DIRECTORY)) {
		String directory1 = arguments.getString(EXTRA_DIRECTORY);
		String directory2 = arguments.getString(EXTRA_DIRECTORY1);

		if (directory1 != null && !mScanMap.containsKey(directory1))
		    mScanMap.put(directory1, "");
		if (directory2 != null && !mScanMap.containsKey(directory2))
		    mScanMap.put(directory2, "");
	    } else if (arguments.containsKey(EXTRA_FILE_PATH)) {
		// 单文件
		String filePath = arguments.getString(EXTRA_FILE_PATH);
		LogUtil.log("onStartCommand:" + filePath);
		if (!StringUtils.isEmpty(filePath)) {
		    if (!mScanMap.containsKey(filePath))
			mScanMap.put(filePath,
				arguments.getString(EXTRA_MIME_TYPE));
		}
	    }
	}

	if (mServiceStatus == SCAN_STATUS_NORMAL
		|| mServiceStatus == SCAN_STATUS_END) {
	    new Thread(this).start();
	    // scan();
	}
    }

    @Override
    public void run() {
	scan();
    }

    /** 扫描 */
    private void scan() {
	// 开始扫描
	notifyObservers(SCAN_STATUS_START, null);

	while (mScanMap.keySet().size() > 0) {

	    String path = "";
	    for (String key : mScanMap.keySet()) {
		path = key;
		break;
	    }
	    if (mScanMap.containsKey(path)) {
		String mimeType = mScanMap.get(path);
		if ("".equals(mimeType)) {
		    scanDirectory(path);
		} else {
		    scanFile(path, mimeType);
		}

		// 扫描完成一个
		mScanMap.remove(path);
	    }

	}

	// 全部扫描完成
	notifyObservers(SCAN_STATUS_END, null);

	// 停止服务
	stopSelf();
    }

    private Handler mHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    super.handleMessage(msg);
	    for (IMediaScannerObserver s : observers) {
		if (s != null) {
		    s.update(msg.what, (PODocu) msg.obj);
		}
	    }
	}
    };

    /** 扫描文件 */
    private void scanFile(String path, String mimeType) {
	save(new PODocu(path, mimeType));
    }

    /** 扫描文件夹 */
    private void scanDirectory(String path) {
	eachAllMedias(new File(path));
    }

    /** 递归查找视频 */
    private void eachAllMedias(File f) {
	if (f != null && f.exists() && f.isDirectory()) {
	    File[] files = f.listFiles();
	    if (files != null) {
		for (File file : f.listFiles()) {
		    // LogUtil.log(f.getAbsolutePath());
		    if (file.isDirectory()) {
			// 忽略.开头的文件夹
			if (!file.getName().startsWith("."))
			    eachAllMedias(file);
		    } else if (file.exists()
			    && file.canRead()
			    && (FileUtils.isVideo(file)
				    || FileUtils.isDocument(file) || FileUtils
					.isImage(file))) {
			save(new PODocu(file));
		    }
		}
	    }
	}
    }

    /**
     * 保存入库
     * 
     * @throws FileNotFoundException
     */
    private void save(PODocu media) {
	DbUtils db = DbUtils.create(this);
	db.configAllowTransaction(true);
	db.configDebug(false);
	// 检测
	List<PODocu> pos = null;
	try {
	    pos = db.findAll(Selector.from(PODocu.class).where("path", "=",
		    media.path));
	} catch (DbException e) {

	}
	if (pos == null || pos.size() == 0) {
	    try {
		if (media.title != null && media.title.length() > 0)
		    media.title_key = PinyinUtils.chineneToSpell(media.title
			    .charAt(0) + "");
	    } catch (Exception ex) {
		LogUtil.log(ex);
	    }
	    media.last_access_time = System.currentTimeMillis();

	    try {

		db.save(media);
	    } catch (DbException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    // 扫描到一个
	    notifyObservers(SCAN_STATUS_RUNNING, media);
	}
    }

    // ~~~ 状态改变

    /** 通知状态改变 */
    private void notifyObservers(int flag, PODocu media) {
	mHandler.sendMessage(mHandler.obtainMessage(flag, media));
    }

    /** 增加观察者 */
    public void addObserver(IMediaScannerObserver s) {
	synchronized (this) {
	    if (!observers.contains(s)) {
		observers.add(s);
	    }
	}
    }

    /** 删除观察者 */
    public synchronized void deleteObserver(IMediaScannerObserver s) {
	observers.remove(s);
    }

    /** 删除所有观察者 */
    public synchronized void deleteObservers() {
	observers.clear();
    }

    public interface IMediaScannerObserver {
	/**
	 * 
	 * @param flag
	 *            0 开始扫描 1 正在扫描 2 扫描完成
	 * @param file
	 *            扫描到的视频文件
	 */
	public void update(int flag, PODocu media);
    }

    // ~~~ Binder

    private final MediaScannerServiceBinder mBinder = new MediaScannerServiceBinder();

    public class MediaScannerServiceBinder extends Binder {
	public MediaScannerService getService() {
	    return MediaScannerService.this;
	}
    }

    @Override
    public IBinder onBind(Intent intent) {
	return mBinder;
    }

}
