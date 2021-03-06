package com.ppkj.mindrays.uploadimage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.WindowManager.BadTokenException;

import com.ppkj.mindrays.bean.MsgResponse;
import com.ppkj.mindrays.network.Request;
import com.ppkj.mindrays.parser.MsgParser;

public class UpLoadImageHelper {
	private OnUploadImageCompleteListener onUploadImageCompleteListener;
	private OnUploadImageArrayCompleteListener onUploadImageArrayCompleteListener;
	private static UpLoadImageHelper sharedPrefHelper = null;
	private ProgressDialog progressDialog;
	private static Context context;

	public static synchronized UpLoadImageHelper getInstance(Context context2) {
		context = context2;
		if (null == sharedPrefHelper) {
			sharedPrefHelper = new UpLoadImageHelper();
		}
		return sharedPrefHelper;
	}

	public UpLoadImageHelper() {
		super();
	}

	public void upLoadingImageArray(
			Request request,
			FormFile formFile,
			OnUploadImageArrayCompleteListener onUploadImageArrayCompleteListener) {
		this.onUploadImageArrayCompleteListener = onUploadImageArrayCompleteListener;
		new UploadingImageArrayTask(formFile, request).execute();
	}

	private class UploadingImageArrayTask extends
			AsyncTask<String, Integer, String> {
		private FormFile formFile;
		private Request request;

		public UploadingImageArrayTask(FormFile formFile, Request request) {
			super();
			this.formFile = formFile;
			this.request = request;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog("正在发表...");
		}

		@Override
		protected String doInBackground(String... params) {
			String postResult = HttpRequester.postArray(request, formFile);
			return postResult;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dismissProgressDialog();
			if (onUploadImageArrayCompleteListener != null) {
				if (!TextUtils.isEmpty(result)) {
					MsgParser parser = (MsgParser) request.getJsonParser();
					MsgResponse response = parser.parse(result);
					if (response != null && response.reCode == 0) {
						onUploadImageArrayCompleteListener
								.onUploadImageArraySuccess(response);
					} else {
						onUploadImageArrayCompleteListener
								.onUploadImageArrayFailed();
					}
				} else {
					onUploadImageArrayCompleteListener
							.onUploadImageArrayFailed();
				}
			}
		}

	}

	public void upLoadingImage(Request request, FormFile formFile,
			OnUploadImageCompleteListener onUploadImageCompleteListener) {
		this.onUploadImageCompleteListener = onUploadImageCompleteListener;
		new UploadingImageTask(formFile, request).execute();
	}

	private class UploadingImageTask extends AsyncTask<String, Integer, String> {
		private FormFile formFile;
		private Request request;

		public UploadingImageTask(FormFile formFile, Request request) {
			super();
			this.formFile = formFile;
			this.request = request;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog("正在上传图片，请稍候...");
		}

		@Override
		protected String doInBackground(String... params) {
			String postResult = HttpRequester.post(request, formFile);
			return postResult;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dismissProgressDialog();
			if (onUploadImageCompleteListener != null) {
				if (!TextUtils.isEmpty(result)) {
					MsgParser parser = (MsgParser) request.getJsonParser();
					MsgResponse response = parser.parse(result);
					if (response != null && response.reCode == 0) {
						onUploadImageCompleteListener
								.onUploadImageSuccess(response);
					} else {
						onUploadImageCompleteListener.onUploadImageFailed();
					}
				} else {
					onUploadImageCompleteListener.onUploadImageFailed();
				}
			}
		}

	}

	/**
	 * 显示进度条对话框
	 * 
	 * @param msg
	 */
	public void showProgressDialog(String msg) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(msg);
		try {
			progressDialog.show();
		} catch (BadTokenException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * 隐藏正在加载的进度条
	 * 
	 */
	public void dismissProgressDialog() {
		if (null != progressDialog && progressDialog.isShowing() == true) {
			progressDialog.dismiss();
		}
	}

	public interface OnUploadImageCompleteListener {
		public abstract void onUploadImageSuccess(MsgResponse response);

		public abstract void onUploadImageFailed();
	}

	public interface OnUploadImageArrayCompleteListener {
		public abstract void onUploadImageArraySuccess(MsgResponse response);

		public abstract void onUploadImageArrayFailed();
	}

}
