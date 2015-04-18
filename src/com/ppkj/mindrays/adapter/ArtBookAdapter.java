/*******************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ppkj.mindrays.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.core.SectionedAdapter;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.localbean.ImageFile;

public class ArtBookAdapter implements SectionedAdapter {

    public static final String TAG = "DribbbleDataAdapter";

    private Context context;
    private Section section;
    private BitmapUtils bitmapUtils;
    private BitmapDisplayConfig bigPicDisplayConfig;

    private int[] colors = new int[] { 0xcc152431, 0xff264C58, 0xffF5C543,
	    0xffE0952C, 0xff9A5325, 0xaaE0952C, 0xaa9A5325, 0xaa152431,
	    0xaa264C58, 0xaaF5C543, 0x44264C58, 0x44F5C543, 0x44152431 };

    private boolean hideImages = false;

    public ArtBookAdapter(Context context) {
	this.context = context;
	section = new Section();
	section.setSectionTitle("Pics");
	bitmapUtils = new BitmapUtils(context);

	bitmapUtils.configDiskCacheEnabled(true);
	bitmapUtils.configMemoryCacheEnabled(true);
	bitmapUtils.configThreadPoolSize(3);
	bitmapUtils.configDefaultReadTimeout(500);
	bigPicDisplayConfig = new BitmapDisplayConfig();
	bigPicDisplayConfig.setBitmapMaxSize(new BitmapSize(120, 120));
    }

    public void update(ArrayList<ImageFile> list) {

	for (ImageFile o : list) {
	    section.getData().add(o);
	}

	Log.d(TAG, "Data updated to: " + section.getDataCount());

    }

    @Override
    public long getItemId(int section, int position) {
	return section * 1000 + position;
    }

    @Override
    public View getItemView(int sectionIndex, int position, View convertView,
	    ViewGroup parent) {
	if (convertView == null) {
	    convertView = LayoutInflater.from(context).inflate(
		    R.layout.pic_view, parent, false);
	}
	ImageView img = (ImageView) convertView.findViewById(R.id.pic);
	if (hideImages) {
	    int idx = position % colors.length;
	    img.setBackgroundColor(colors[idx]);

	} else {
	    ImageFile s = (ImageFile) (this.section.getData().get(position));
	    // Picasso.with(context).load(s.getImage_teaser_url()).into(img);
	    bitmapUtils.display(img, s.getPhotoPath(), bigPicDisplayConfig);
	}

	return convertView;
    }

    @Override
    public View getHeaderViewForSection(int section, View convertView,
	    ViewGroup parent) {
	return null;
    }

    @Override
    public int getNumberOfSections() {
	if (section.getData().size() == 0)
	    return 0;
	return 1;
    }

    @Override
    public Section getSection(int index) {
	return section;
    }

    @Override
    public Class[] getViewTypes() {
	return new Class[] { LinearLayout.class };
    }

    @Override
    public Class getViewType(FreeFlowItem proxy) {
	return LinearLayout.class;
    }

    @Override
    public boolean shouldDisplaySectionHeaders() {
	return false;
    }

}
