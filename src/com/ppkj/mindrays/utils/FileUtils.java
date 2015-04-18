package com.ppkj.mindrays.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.webkit.MimeTypeMap;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.localbean.PODocu;

public class FileUtils {

	private static final HashSet<String> mHashImage;
	private static final HashSet<String> mHashVideo;
	private static final HashSet<String> mHashDocument;

	private static final double KB = 1024.0;
	private static final double MB = KB * KB;
	private static final double GB = KB * KB * KB;

	static {
		mHashVideo = new HashSet<String>(
				Arrays.asList(Constants.VIDEO_EXTENSIONS));
		mHashImage = new HashSet<String>(
				Arrays.asList(Constants.IMAGE_EXTENSIONS));

		mHashDocument = new HashSet<String>(
				Arrays.asList(Constants.CLOUD_EXTENSIONS));
	}

	public static boolean isVideo(File f) {
		final String ext = getFileExtension(f);
		return mHashVideo.contains(ext);
	}

	public static boolean isImage(File f) {
		final String ext = getFileExtension(f);
		return mHashImage.contains(ext);
	}

	public static boolean isDocument(File f) {
		final String ext = getFileExtension(f);
		return mHashDocument.contains(ext);
	}

	/** 获取文件后缀 */
	public static String getFileExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
		return null;
	}

	public static String getUrlFileName(String url) {
		int slashIndex = url.lastIndexOf('/');
		int dotIndex = url.lastIndexOf('.');
		String filenameWithoutExtension;
		if (dotIndex == -1) {
			filenameWithoutExtension = url.substring(slashIndex + 1);
		} else {
			filenameWithoutExtension = url.substring(slashIndex + 1, dotIndex);
		}
		return filenameWithoutExtension;
	}

	public static String getUrlExtension(String url) {
		if (!StringUtils.isEmpty(url)) {
			int i = url.lastIndexOf('.');
			if (i > 0 && i < url.length() - 1) {
				return url.substring(i + 1).toLowerCase();
			}
		}
		return "";
	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static String showFileSize(long size) {
		String fileSize;
		if (size < KB)
			fileSize = size + "B";
		else if (size < MB)
			fileSize = String.format("%.1f", size / KB) + " KB";
		else if (size < GB)
			fileSize = String.format("%.1f", size / MB) + " MB";
		else
			fileSize = String.format("%.1f", size / GB) + " GB";

		return fileSize;
	}

	/** 显示SD卡剩余空间 */
	public static String showFileAvailable() {
		String result = "";
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			StatFs sf = new StatFs(Environment.getExternalStorageDirectory()
					.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			return showFileSize(availCount * blockSize) + " / "
					+ showFileSize(blockSize * blockCount);
		}
		return result;
	}

	/** 如果不存在就创建 */
	public static boolean createIfNoExists(String path) {
		File file = new File(path);
		boolean mk = false;
		if (!file.exists()) {
			mk = file.mkdirs();
		}
		return mk;
	}

	public static String sdCard(Context c) {
		// ToastUtils.showToast(paths.toString());
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.toString();
		}

		return "";
	}

	public static String sdExtCard(Context c) {
		StorageManager sm = (StorageManager) c.getApplicationContext()
				.getSystemService(Context.STORAGE_SERVICE);
		try {
			String[] paths = (String[]) sm.getClass()
					.getMethod("getVolumePaths", null).invoke(sm, null);
			// ToastUtils.showToast(paths.toString());
			String innerPath = "";
			File sdDir = null;
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
			if (sdCardExist) {
				sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			}
			String sdCardPath = sdDir.toString();
			for (String string : paths) {
				if (!string.equals(sdCardPath)) {
					innerPath = string /* + "/DCIM" */;
					return innerPath;
				}
			}

		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		}
		return "";
	}

	public static List<PODocu> getAllSortFiles(Context c, String file_type) {

		DbUtils db = DbUtils.create(c);
		try {
			List<PODocu> list = db.findAll(Selector.from(PODocu.class)
					.orderBy("last_modify_time", true)
					.where("file_type", "=", file_type));
			return list;
		} catch (DbException e) {

			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new ArrayList<PODocu>();
	}

	public static List<PODocu> getAllSortFiles(Context c, int type) {

		DbUtils db = DbUtils.create(c);
		try {
			List<PODocu> list = db
					.findAll(Selector.from(PODocu.class)
							.orderBy("last_modify_time", true)
							.where("type", "=", type));
			return list;
		} catch (DbException e) {

			e.printStackTrace();
		}
		return new ArrayList<PODocu>();
	}

	public static boolean sdAvailable() {
		return Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment
				.getExternalStorageState())
				|| Environment.MEDIA_MOUNTED.equals(Environment
						.getExternalStorageState());
	}

	public static String makeDocFile() {

		String sdStateString = android.os.Environment.getExternalStorageState();

		if (sdStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			try {
				File sdFile = android.os.Environment
						.getExternalStorageDirectory();

				String path = sdFile.getAbsolutePath() + File.separator
						+ "PPKJ" + File.separator + "doc";

				File dirFile = new File(path);
				if (!dirFile.exists()) {
					dirFile.mkdir();
				}

				File myFile = new File(path + File.separator + "doc.html");

				if (!myFile.exists()) {
					myFile.createNewFile();
				}

				return myFile.getAbsolutePath();
			} catch (Exception e) {

			}
		}
		return null;
	}

	public static String makeExcelFile() {

		String sdStateString = android.os.Environment.getExternalStorageState();

		if (sdStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			try {
				File sdFile = android.os.Environment
						.getExternalStorageDirectory();

				String path = sdFile.getAbsolutePath() + File.separator
						+ "loveReader" + File.separator + "excel";

				// String temp = path + File.separator + "excel.html";

				File dirFile = new File(path);
				if (!dirFile.exists()) {
					dirFile.mkdir();
				}

				File myFile = new File(path + File.separator + "excel.html");

				if (!myFile.exists()) {
					myFile.createNewFile();
				}

				return myFile.getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String getFileMimeType(File file) {
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				getFileExtension(file));
		if (type == null)
			return "*/*";
		return type;
	}
}
