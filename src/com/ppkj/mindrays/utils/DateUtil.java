package com.ppkj.mindrays.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

public final class DateUtil {
    /**
     * 将日期信息转换成昨天，今天
     * 
     * @param date
     * @return
     */

    public static String formatDateString(Context context, long time) {
	DateFormat dateFormat = android.text.format.DateFormat
		.getDateFormat(context);
	long now = System.currentTimeMillis();
	// Date today = new Date(now);
	Date date = new Date(time);
	long intervalMilli = time - now;
	int xcts = (int) (intervalMilli / (24 * 60 * 60 * 1000));
	if (xcts == -1) {
	    return "昨天";
	} else if (xcts == 0) {
	    return "今天";
	} else {
	    return dateFormat.format(date);
	}
    }

    public static String currentTime() {
	SimpleDateFormat formatter = new SimpleDateFormat(
		"yyyyMMddHHmmss");
	Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
	String str = formatter.format(curDate);
	return str;
    }
}
