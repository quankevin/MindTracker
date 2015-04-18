package com.ppkj.mindrays.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public class VersionUtil {

	public static int getVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			int versionCode = packInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {

			e.printStackTrace();
			return 0;
		}
	}

	public static String getVersionName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			String versionName = packInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public static void installLoadedApkFile(Context context, File file) {
		Intent installIntent = new Intent();
		installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		installIntent.setAction(android.content.Intent.ACTION_VIEW);
		installIntent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(installIntent);
	}
}
