package com.ppkj.mindrays.adapter;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.lidroid.xutils.BitmapUtils;
import com.ppkj.mindrays.localbean.ImageFile;

public class SimplePagerAdapter extends PagerAdapter {
	private List<ImageFile> dataList;
	private Context c;
	private OnPhotoTapListener listener;
	private BitmapUtils bitmapUtils;

	public SimplePagerAdapter(Context c, List<ImageFile> dataList,
			OnPhotoTapListener listener) {
		this.dataList = dataList;
		this.c = c;
		this.listener = listener;
		bitmapUtils = new BitmapUtils(c);
		bitmapUtils.configDiskCacheEnabled(true);
		bitmapUtils.configMemoryCacheEnabled(true);
		bitmapUtils.configThreadPoolSize(5);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		PhotoView photoView = new PhotoView(container.getContext());
		bitmapUtils.display(photoView, dataList.get(position).getPhotoPath());
		// Now just add PhotoView to ViewPager and return it
		container.addView(photoView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		photoView.setOnPhotoTapListener(listener);
		return photoView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}
