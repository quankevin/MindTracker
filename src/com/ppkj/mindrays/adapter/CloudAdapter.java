package com.ppkj.mindrays.adapter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.localbean.PODocu;
import com.ppkj.mindrays.utils.DateUtil;
import com.ppkj.mindrays.utils.ToastUtils;
import com.ppkj.mindrays.utils.ViewUtil;

public class CloudAdapter extends ArrayAdapter<PODocu> {

    OnFileSelectedListener onFileSelectedListener = null;

    public static interface OnFileSelectedListener {
	void onFileSelected(PODocu file);
    }

    public void setOnFileSelectedListener(
	    OnFileSelectedListener onFileSelectedListener) {
	this.onFileSelectedListener = onFileSelectedListener;
    }

    private Context context;
    private BitmapUtils bitmapUtils;
    private BitmapDisplayConfig bigPicDisplayConfig;
    private Set<PODocu> selectedPos = null;
    private boolean uStatue = false;

    public CloudAdapter(Context ctx, List<PODocu> l) {
	super(ctx, l);
	this.context = ctx;
	bitmapUtils = new BitmapUtils(context);
	bitmapUtils.configDiskCacheEnabled(true);
	bitmapUtils.configMemoryCacheEnabled(true);
	bitmapUtils.configThreadPoolSize(5);
	bigPicDisplayConfig = new BitmapDisplayConfig();
	bigPicDisplayConfig.setBitmapMaxSize(new BitmapSize(50, 50));
    }

    @Override
    public long getItemId(int position) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	final PODocu f = getItem(position);
	Holder holder = null;
	if (convertView == null) {

	    holder = new Holder();
	    convertView = LayoutInflater.from(context).inflate(
		    R.layout.list_item_cloud, null);
	    holder.iv_info = (ImageView) convertView
		    .findViewById(R.id.item_file_image);
	    holder.iv_net = (ImageView) convertView
		    .findViewById(R.id.item_net_flag);
	    holder.tv_date = (TextView) convertView
		    .findViewById(R.id.item_file_date);
	    holder.tv_count = (TextView) convertView
		    .findViewById(R.id.item_file_imagecount);
	    holder.iv_upload = (ImageView) convertView
		    .findViewById(R.id.item_upload_flag);
	    convertView.setTag(holder);
	} else {
	    holder = (Holder) convertView.getTag();
	}
	holder.tv_count.setText(f.title);

	holder.tv_date.setText(DateUtil.formatDateString(context,
		f.last_modify_time));
	if (!uStatue) {
	    holder.iv_upload.setVisibility(View.GONE);
	} else {
	    holder.iv_upload.setVisibility(View.VISIBLE);
	}
	if (f.isNet) {
	    holder.iv_net.setVisibility(View.VISIBLE);
	    holder.iv_upload.setVisibility(View.GONE);
	} else {
	    holder.iv_net.setVisibility(View.GONE);
	}
	if (isSelected(f)) {
	    holder.iv_upload.setBackgroundResource(R.drawable.upcheck);
	} else {
	    holder.iv_upload.setBackgroundResource(R.drawable.upuncheck);
	}
	if (onFileSelectedListener != null) {
	    holder.iv_upload.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {

		    onFileSelectedListener.onFileSelected(f);

		}
	    });
	}
	ViewUtil.setImageLogo(holder.iv_info, f.file_type);

	if (Arrays.asList(Constants.IMAGE_EXTENSIONS).contains(f.file_type)) {

	    bitmapUtils.display(holder.iv_info, f.path, bigPicDisplayConfig);
	} else if (Arrays.asList(Constants.VIDEO_EXTENSIONS).contains(
		f.file_type)) {
	    holder.iv_info.setImageResource(R.drawable.type_video);
	}

	return convertView;
    }

    protected boolean isSelected(PODocu po) {
	return (selectedPos != null && selectedPos.contains(po));
    }

    public void setSelectedPos(Set<PODocu> selectedPos) {
	this.selectedPos = selectedPos;
    }

    public void setUploadStatue(boolean isForUp) {
	this.uStatue = isForUp;
	this.notifyDataSetChanged();
    }

    private static class Holder {
	ImageView iv_info;
	ImageView iv_net;
	ImageView iv_upload;
	TextView tv_date;
	TextView tv_count;
    }

    public void setItem(PODocu po) {
	for (int i = 0; i < getCount(); i++) {
	    if (getItem(i).path.equals(po.path)) {
		getItem(i).isNet = po.isNet;
		break;
	    }
	}
	this.notifyDataSetChanged();
    }

}