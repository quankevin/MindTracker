package com.ppkj.mindrays.utils;

import java.io.File;
import java.util.ArrayList;

import org.apache.poi.hssf.record.formula.Ptg;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.artifex.mupdf.MuPDFActivity;
import com.ppkj.mindrays.activity.ImageGridActivity;
import com.ppkj.mindrays.activity.PPTActivity;
import com.ppkj.mindrays.activity.VideoPlayerActivity;
import com.ppkj.mindrays.activity.ViewPagerActivity;
import com.ppkj.mindrays.localbean.ImageFile;
import com.ppkj.mindrays.txt.MyReadActivity;
import com.ppkj.mindrays.wordandexcel.ExcelRead;
import com.ppkj.mindrays.wordandexcel.ViewFile;

public class IntentUtil {

    public static void startIntent(Context c, String string) {

	File f = new File(string);
	if (string.endsWith(".xls")) {
	    Intent i = new Intent();
	    i.setClass(c, ExcelRead.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("name", string);
	    i.putExtras(bundle);
	    c.startActivity(i);

	} else if (string.endsWith(".doc")) {
	    Intent i = new Intent();
	    i.setClass(c, ViewFile.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("name", string);
	    i.putExtras(bundle);
	    c.startActivity(i);

	} else if (string.endsWith(".pdf")) {
	    Uri uri = Uri.parse(string);
	    Intent intent = new Intent(c, MuPDFActivity.class);
	    intent.setAction(Intent.ACTION_VIEW);
	    intent.setData(uri);
	    c.startActivity(intent);
	} else if (string.endsWith(".txt")) {
	    Intent intent = new Intent(c, MyReadActivity.class);
	    intent.putExtra("filePath", string);

	    c.startActivity(intent);
	} else if (string.endsWith(".ppt")) {
	    Intent intent = new Intent(c, PPTActivity.class);
	    intent.putExtra("filePath", string);

	    c.startActivity(intent);
	} else if (FileUtils.isImage(f)) {
	    ArrayList<ImageFile> ifs = new ArrayList<ImageFile>();
	    ImageFile i = new ImageFile();
	    i.setIsSelect(0);
	    i.setPhotoDate(f.lastModified());
	    i.setPhotoPath(f.getAbsolutePath());
	    i.setPhotoTotal(0);
	    i.setPhotoName(f.getName());
	    ifs.add(i);
	    Intent intent = new Intent(c, ViewPagerActivity.class);
	    intent.putParcelableArrayListExtra(
		    ImageGridActivity.EXTRA_IMAGE_LIST, ifs);
	    intent.putExtra(ImageGridActivity.EXTRA_IMAGE_POSITION, 0);
	    c.startActivity(intent);
	} else if (FileUtils.isVideo(f)) {

	    Intent netIntent = new Intent(c, VideoPlayerActivity.class);
	    netIntent.putExtra("videopath", string);
	    c.startActivity(netIntent);
	} else {

	    Intent intent = createFileOpenIntent(f);
	    try {
		c.startActivity(intent);
	    } catch (ActivityNotFoundException e) {
		c.startActivity(Intent.createChooser(intent, "打开" + f.getName()
			+ "为："));
	    } catch (Exception e) {
		new AlertDialog.Builder(c).setMessage(e.getMessage())
			.setTitle("错误")
			.setPositiveButton(android.R.string.ok, null).show();
	    }
	}
    }

    public static Intent createFileOpenIntent(File file) {
	Intent intent = new Intent(Intent.ACTION_VIEW);
	intent.setDataAndType(Uri.fromFile(file),
		FileUtils.getFileMimeType(file));
	return intent;
    }
}
