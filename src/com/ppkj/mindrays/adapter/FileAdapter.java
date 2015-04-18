package com.ppkj.mindrays.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.localbean.ImageFile;

public class FileAdapter extends BaseAdapter {

    private Context context;
    // private DisplayImageOptions options;
    private List<ImageFile> listImage;

    private BitmapUtils bitmapUtils;
    private BitmapDisplayConfig bigPicDisplayConfig;

    public FileAdapter(Context context, List<ImageFile> listImage) {
	this.context = context;
	this.listImage = listImage;
	bitmapUtils = new BitmapUtils(context);
	bitmapUtils.configDiskCacheEnabled(true);
	bitmapUtils.configMemoryCacheEnabled(true);
	bitmapUtils.configThreadPoolSize(5);
	bigPicDisplayConfig = new BitmapDisplayConfig();
	bigPicDisplayConfig.setBitmapMaxSize(new BitmapSize(50, 50));
    }

    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return listImage.size();
    }

    @Override
    public ImageFile getItem(int position) {
	// TODO Auto-generated method stub
	return listImage.get(position);
    }

    @Override
    public long getItemId(int position) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	Holder holder = null;
	if (convertView == null) {
	    holder = new Holder();
	    convertView = LayoutInflater.from(context).inflate(
		    R.layout.list_item_file, null);
	    holder.ll_up = (LinearLayout) convertView.findViewById(R.id.ll_up);
	    holder.ll_down = (LinearLayout) convertView
		    .findViewById(R.id.ll_down);
	    holder.iv_info1 = (ImageView) convertView.findViewById(R.id.one);
	    holder.iv_info2 = (ImageView) convertView.findViewById(R.id.two);
	    holder.iv_info3 = (ImageView) convertView.findViewById(R.id.three);
	    holder.iv_info4 = (ImageView) convertView.findViewById(R.id.four);
	    holder.tv_date = (TextView) convertView
		    .findViewById(R.id.item_file_date);
	    holder.tv_count = (TextView) convertView
		    .findViewById(R.id.item_file_imagecount);
	    convertView.setTag(holder);
	} else {
	    holder = (Holder) convertView.getTag();
	}
	holder.tv_count.setText("共 "
		+ String.valueOf(getItem(position).getPhotoTotal()) + " 张");
	holder.tv_date.setText(getItem(position).getPhotoDateString());
	holder.iv_info1.setImageDrawable(null);
	holder.iv_info2.setImageDrawable(null);
	holder.iv_info3.setImageDrawable(null);
	holder.iv_info4.setImageDrawable(null);
	String[] paths = getItem(position).getPaths().split(",");
	if (paths.length == 1) {
	    holder.ll_down.setVisibility(View.GONE);
	    holder.iv_info2.setVisibility(View.GONE);
	    bitmapUtils.display(holder.iv_info1, paths[0], bigPicDisplayConfig);
	} else if (paths.length == 2) {
	    holder.ll_up.setVisibility(View.VISIBLE);
	    holder.ll_down.setVisibility(View.VISIBLE);
	    holder.iv_info2.setVisibility(View.VISIBLE);
	    holder.iv_info3.setVisibility(View.VISIBLE);
	    holder.iv_info4.setVisibility(View.VISIBLE);
	    bitmapUtils.display(holder.iv_info1, paths[0], bigPicDisplayConfig);
	    bitmapUtils.display(holder.iv_info4, paths[1], bigPicDisplayConfig);
	} else if (paths.length == 3) {
	    holder.ll_up.setVisibility(View.VISIBLE);
	    holder.ll_down.setVisibility(View.VISIBLE);
	    holder.iv_info2.setVisibility(View.VISIBLE);
	    holder.iv_info3.setVisibility(View.VISIBLE);
	    holder.iv_info4.setVisibility(View.VISIBLE);

	    bitmapUtils.display(holder.iv_info1, paths[0], bigPicDisplayConfig);
	    bitmapUtils.display(holder.iv_info2, paths[1], bigPicDisplayConfig);
	    bitmapUtils.display(holder.iv_info3, paths[2], bigPicDisplayConfig);
	} else {
	    holder.ll_up.setVisibility(View.VISIBLE);
	    holder.ll_down.setVisibility(View.VISIBLE);
	    holder.iv_info2.setVisibility(View.VISIBLE);
	    holder.iv_info3.setVisibility(View.VISIBLE);
	    holder.iv_info4.setVisibility(View.VISIBLE);
	    bitmapUtils.display(holder.iv_info1, paths[0], bigPicDisplayConfig);
	    bitmapUtils.display(holder.iv_info2, paths[1], bigPicDisplayConfig);
	    bitmapUtils.display(holder.iv_info3, paths[2], bigPicDisplayConfig);
	    bitmapUtils.display(holder.iv_info4, paths[3], bigPicDisplayConfig);
	}

	return convertView;
    }

    private static class Holder {
	private LinearLayout ll_up;
	private LinearLayout ll_down;
	private ImageView iv_info1;
	private ImageView iv_info2;
	private ImageView iv_info3;
	private ImageView iv_info4;
	private TextView tv_date;
	private TextView tv_count;

    }

}