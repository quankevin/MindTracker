package com.ppkj.mindrays.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.localbean.ImageFile;

public class ImageGridAdapter extends BaseAdapter {

	private List<ImageFile> dataList;
	private Context mContext;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig bigPicDisplayConfig;

	public ImageGridAdapter(Context context, List<ImageFile> list) {
		this.mContext = context;
		dataList = list;
		bitmapUtils = new BitmapUtils(context);
		bitmapUtils.configDiskCacheEnabled(true);
		bitmapUtils.configMemoryCacheEnabled(true);
		bitmapUtils.configThreadPoolSize(3);
		bigPicDisplayConfig = new BitmapDisplayConfig();
		bigPicDisplayConfig.setBitmapMaxSize(new BitmapSize(120, 120));
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public ImageFile getItem(int position) {

		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	private class Holder {
		private ImageView iv;
		private TextView text;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = View
					.inflate(mContext, R.layout.item_image_grid, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			// holder.selected = (ImageView) convertView
			// .findViewById(R.id.isselected);
			holder.text = (TextView) convertView
					.findViewById(R.id.item_image_grid_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		bitmapUtils.display(holder.iv, getItem(position).getPhotoPath(),
				bigPicDisplayConfig);
		return convertView;
	}
}
