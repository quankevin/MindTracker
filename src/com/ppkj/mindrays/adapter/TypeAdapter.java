package com.ppkj.mindrays.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppkj.mindrays.R;
import com.ppkj.mindrays.localbean.TypeFile;
import com.ppkj.mindrays.utils.DateUtil;
import com.ppkj.mindrays.utils.ViewUtil;

public class TypeAdapter extends BaseAdapter {

    private Context context;

    private List<TypeFile> listImage;

    public TypeAdapter(Context context, List<TypeFile> listImage) {
	this.context = context;
	this.listImage = listImage;

    }

    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return listImage.size();
    }

    @Override
    public TypeFile getItem(int position) {
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
		    R.layout.list_item_type, null);

	    holder.iv_type = (ImageView) convertView
		    .findViewById(R.id.item_file_type_image);
	    holder.tv_type = (TextView) convertView
		    .findViewById(R.id.item_file_type);
	    holder.tv_date = (TextView) convertView
		    .findViewById(R.id.item_file_date);
	    // holder.tv_count = (TextView) convertView
	    // .findViewById(R.id.item_file_typecount);
	    convertView.setTag(holder);
	} else {
	    holder = (Holder) convertView.getTag();
	}
	holder.tv_type.setText(getItem(position).getTitle());

	holder.tv_date.setText(DateUtil.formatDateString(context,
		(getItem(position).getLast_access_time())));
	ViewUtil.setImageLogo(holder.iv_type, getItem(position).getTitle());

	return convertView;
    }

    private static class Holder {

	private ImageView iv_type;
	private TextView tv_type;
	private TextView tv_date;
	// private TextView tv_count;

    }

}