package com.ppkj.mindrays.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.ppkj.mindrays.R;

public class ToastUtils {

	public static void showToast(Context ctx, int resID) {
		showToast(ctx, Toast.LENGTH_SHORT, resID);
	}

	public static void showToast(Context ctx, String text) {
		showToast(ctx, Toast.LENGTH_SHORT, text);
	}

	public static void showLongToast(Context ctx, int resID) {
		showToast(ctx, Toast.LENGTH_LONG, resID);
	}

	public static void showLongToast(Context ctx, String text) {
		showToast(ctx, Toast.LENGTH_LONG, text);
	}

	public static void showToast(Context ctx, int duration, int resID) {
		showToast(ctx, duration, ctx.getString(resID));
	}

	public static void showToast(Context ctx, int duration, String text) {
		Toast toast = Toast.makeText(ctx, text, duration);
		View mNextView = toast.getView();
		if (mNextView != null)
			mNextView.setBackgroundResource(R.drawable.toast_frame);
		toast.show();
		// Toast.makeText(ctx, text, duration).show();
	}

	/** 在UI线程运行弹出 */
	public static void showToastOnUiThread(final Activity ctx, final String text) {
		if (ctx != null) {
			ctx.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					showToast(ctx, text);
				}
			});
		}
	}
}
