package com.ppkj.mindrays.utils;

import java.io.File;

import com.ppkj.mindrays.R;

import android.view.View;
import android.widget.ImageView;

public class ViewUtil {
    public static final boolean DBG_H = true;

    /**
     * Refresh view status
     */
    public static void setVisibleGone(View view, View... views) {
	if (null != view && view.getVisibility() != View.VISIBLE)
	    view.setVisibility(View.VISIBLE);
	setGone(views);
    }

    public static void setGone(View... views) {
	if (views != null && views.length > 0) {
	    for (View view : views) {
		if (null != view && view.getVisibility() != View.GONE)
		    view.setVisibility(View.GONE);
	    }
	}
    }

    public static void setVisible(View... views) {
	if (views != null && views.length > 0) {
	    for (View view : views) {
		if (null != view && view.getVisibility() != View.VISIBLE)
		    view.setVisibility(View.VISIBLE);
	    }
	}
    }

    public static void setEnable(View... views) {
	if (views != null && views.length > 0) {
	    for (View view : views) {
		if (view != null && !view.isEnabled()) {
		    view.setEnabled(true);
		}
	    }
	}
    }

    public static void setDisable(View... views) {
	if (views != null && views.length > 0) {
	    for (View view : views) {
		if (view != null && view.isEnabled()) {
		    view.setEnabled(false);
		}
	    }
	}
    }

    public static void setInvisible(View... views) {
	if (views != null && views.length > 0) {
	    for (View view : views) {
		if (null != view && view.getVisibility() != View.INVISIBLE)
		    view.setVisibility(View.INVISIBLE);
	    }
	}
    }

    /**
     * File Operations
     * 
     * @param args
     *            file names
     * @return
     */
    public static String getFilePathName(String... args) {
	String[] param = args;
	String Str = param[0];
	for (int i = 1, len = param.length; i < len; i++) {
	    if (null == Str)
		return null;
	    if (Str.endsWith(File.separator)
		    && param[i].startsWith(File.separator))
		Str = Str.substring(0, Str.length() - 1) + param[i];
	    else if (Str.endsWith(File.separator)
		    || param[i].startsWith(File.separator))
		Str += param[i];
	    else
		Str += File.separator + param[i];
	}
	return Str;
    }

    public final static String FILE_BASE_NAME = "PPKJ";
    public final static String FILE_ASSETS_PATH = "source";

    public static void setImageLogo(ImageView iv, String filetype) {
	if (filetype.toLowerCase().equals("video")) {
	    iv.setImageResource(R.drawable.type_video);
	} else if (filetype.toLowerCase().equals("image")) {
	    iv.setImageResource(R.drawable.type_image);
	} else if (filetype.toLowerCase().equals("txt")) {
	    iv.setImageResource(R.drawable.txt);

	} else if (filetype.toLowerCase().equals("xls")) {
	    iv.setImageResource(R.drawable.excel);

	} else if (filetype.toLowerCase().equals("xlsx")) {
	    iv.setImageResource(R.drawable.excel);

	} else if (filetype.toLowerCase().equals("doc")) {
	    iv.setImageResource(R.drawable.word);

	} else if (filetype.toLowerCase().equals("docx")) {
	    iv.setImageResource(R.drawable.word);

	} else if (filetype.toLowerCase().equals("ppt")) {
	    iv.setImageResource(R.drawable.ppt);

	} else if (filetype.toLowerCase().equals("pdf")) {
	    iv.setImageResource(R.drawable.pdf);

	} else {
	    iv.setImageDrawable(null);
	}
    }
}
