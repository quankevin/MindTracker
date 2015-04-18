package com.ppkj.mindrays;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainApp extends Application {

	private static MainApp mainApp = null;
	private MyCrashHandler crashHandler = MyCrashHandler.getInstance();
	public boolean isLogin = false;
	public int prePage = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	private void init() {
		mainApp = this;
		crashHandler.init(this);
		Thread.currentThread().setUncaughtExceptionHandler(crashHandler);
		SDKInitializer.initialize(this);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.threadPoolSize(5).memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator()).build();

		ImageLoader.getInstance().init(config);
	}

	public static MainApp getInstance() {
		return mainApp;
	}

}
