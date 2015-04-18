package com.ppkj.mindrays.adapter;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.utils.FileUtils;
import com.ppkj.mindrays.utils.ViewUtil;

public class FtpAdapter extends BaseAdapter {

    private Context context;

    private List<FTPFile> ftpList;

    public FtpAdapter(Context context, List<FTPFile> ftpList) {
	this.context = context;
	this.ftpList = ftpList;

    }

    @Override
    public int getCount() {

	return ftpList.size();
    }

    @Override
    public FTPFile getItem(int position) {

	return ftpList.get(position);
    }

    @Override
    public long getItemId(int position) {

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
	    convertView.setTag(holder);
	} else {
	    holder = (Holder) convertView.getTag();
	}
	holder.tv_type.setText(getItem(position).getName());

	holder.tv_date.setText(FileUtils.showFileSize((getItem(position)
		.getSize())));
	if (getItem(position).isDirectory()) {
	    holder.tv_date.setVisibility(View.GONE);
	} else {
	    holder.tv_date.setTextSize(15);
	    holder.tv_date.setVisibility(View.VISIBLE);
	}
	String fileType = FileUtils
		.getUrlExtension(getItem(position).getName());
	ViewUtil.setImageLogo(holder.iv_type, fileType);
	if (getItem(position).isDirectory()) {
	    holder.iv_type.setImageResource(R.drawable.folder);
	} else if (Arrays.asList(Constants.IMAGE_EXTENSIONS).contains(fileType)) {
	    holder.iv_type.setImageResource(R.drawable.type_image);
	} else if (Arrays.asList(Constants.VIDEO_EXTENSIONS).contains(fileType)) {
	    holder.iv_type.setImageResource(R.drawable.type_video);
	}
	return convertView;
    }

    private static class Holder {

	private ImageView iv_type;
	private TextView tv_type;
	private TextView tv_date;
	// private TextView tv_count;

    }

}