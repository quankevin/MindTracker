package com.ppkj.mindrays.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.localbean.PODocu;
import com.ppkj.mindrays.utils.DateUtil;
import com.ppkj.mindrays.utils.ViewUtil;

public class UpLoadAdapter extends BaseAdapter {
    OnUploadCancelListener onUploadCancelListener = null;

    public static interface OnUploadCancelListener {
	void onCancel(PODocu file);
    }

    public void setOnUploadCancelListener(
	    OnUploadCancelListener onUploadCancelListener) {
	this.onUploadCancelListener = onUploadCancelListener;
    }

    private Context context;
    private BitmapUtils bitmapUtils;
    private BitmapDisplayConfig bigPicDisplayConfig;
    private ArrayList<PODocu> uploading = null;

    public UpLoadAdapter(Context ctx, ArrayList<PODocu> l) {
	uploading = l;
	this.context = ctx;

	bitmapUtils = new BitmapUtils(context);
	bitmapUtils.configDiskCacheEnabled(true);
	bitmapUtils.configMemoryCacheEnabled(true);
	bitmapUtils.configThreadPoolSize(5);
	bigPicDisplayConfig = new BitmapDisplayConfig();
	bigPicDisplayConfig.setBitmapMaxSize(new BitmapSize(50, 50));
    }

    @Override
    public PODocu getItem(int position) {

	return uploading.get(position);
    }

    @Override
    public long getItemId(int position) {

	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	final PODocu f = getItem(position);
	Holder holder = null;
	if (convertView == null) {

	    holder = new Holder();
	    convertView = LayoutInflater.from(context).inflate(
		    R.layout.list_item_upload, null);
	    holder.iv_upload = (ImageView) convertView
		    .findViewById(R.id.item_upload_image);
	    holder.tv_name = (TextView) convertView
		    .findViewById(R.id.item_upload_name);
	    holder.iv_cancel = (ImageView) convertView
		    .findViewById(R.id.item_upload_cancel);

	    convertView.setTag(holder);
	} else {
	    holder = (Holder) convertView.getTag();
	}
	holder.tv_name.setText(f.title);
	if (position == 0) {
	    holder.iv_cancel.setVisibility(View.GONE);
	    holder.tv_name.setText(f.title + "  上传中...");
	}
	if (onUploadCancelListener != null) {
	    holder.iv_cancel
		    .setBackgroundResource(R.drawable.selector_list_item);
	    holder.iv_cancel.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    onUploadCancelListener.onCancel(f);
		}
	    });
	}
	ViewUtil.setImageLogo(holder.iv_upload, f.file_type);

	if (Arrays.asList(Constants.IMAGE_EXTENSIONS).contains(f.file_type)) {

	    bitmapUtils.display(holder.iv_upload, f.path, bigPicDisplayConfig);
	}

	return convertView;
    }

    private static class Holder {
	ImageView iv_upload;
	TextView tv_name;
	ImageView iv_cancel;
    }

    @Override
    public int getCount() {

	return uploading.size();
    }

}