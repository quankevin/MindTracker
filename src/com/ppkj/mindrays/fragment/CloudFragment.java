package com.ppkj.mindrays.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.activity.CloudActivity;
import com.ppkj.mindrays.activity.CloudFtpActivity;
import com.ppkj.mindrays.adapter.TypeAdapter;
import com.ppkj.mindrays.base.BaseFragment;
import com.ppkj.mindrays.bean.UserBean;
import com.ppkj.mindrays.localbean.TypeFile;
import com.ppkj.mindrays.utils.FileUtils;
import com.ppkj.mindrays.utils.ToastUtils;

public class CloudFragment extends BaseFragment implements OnItemClickListener {
    private ListView mListView;
    private TypeAdapter mTypeAdapter;
    private TextView mGetFtp;
    private UserBean mUserBean;
    private static CloudFragment cf;

    public static CloudFragment getInstance() {
	if (cf == null) {
	    cf = new CloudFragment();
	}
	return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.main_frg_cloud, container, false);
	getAndSetViews(view);
	return view;
    }

    private void getAndSetViews(View view) {
	mListView = (ListView) view.findViewById(R.id.file_list);
	mListView.setOnItemClickListener(this);
	mGetFtp = (TextView) view.findViewById(R.id.cloud_getftp);
	mGetFtp.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);
	mUserBean = mSharedPrefHelper.readUserBean();

	if (mUserBean != null) {
	    mGetFtp.setVisibility(View.VISIBLE);
	} else {
	    mGetFtp.setVisibility(View.GONE);

	}
	if (!FileUtils.sdAvailable()) {
	    ToastUtils.showLongToast(getActivity(), "SD卡不存在...");
	    return;
	}
	ArrayList<TypeFile> ais = new ArrayList<TypeFile>();
	TypeFile video = new TypeFile();
	video.setTitle("Video");
	video.setFile_type("Video");
	ais.add(video);
	TypeFile image = new TypeFile();
	image.setTitle("Image");
	image.setFile_type("Image");
	ais.add(image);

	for (int i = 0; i < Constants.CLOUD_EXTENSIONS.length; i++) {
	    TypeFile item = new TypeFile();
	    item.setTitle(Constants.CLOUD_EXTENSIONS[i].toUpperCase());
	    item.setFile_type(Constants.CLOUD_EXTENSIONS[i].toLowerCase());
	    ais.add(item);
	}

	mTypeAdapter = new TypeAdapter(getActivity(), ais);
	mListView.setAdapter(mTypeAdapter);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	Intent next = new Intent();
	next.setClass(getActivity(), CloudActivity.class);
	next.putExtra("filetype", mTypeAdapter.getItem(arg2).getFile_type());
	next.putExtra("title_name", mTypeAdapter.getItem(arg2).getTitle());
	getActivity().startActivity(next);

    }

    @Override
    public void onClickEvent(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.cloud_getftp:
	    if (mUserBean != null) {

		startActivity(new Intent(getActivity(), CloudFtpActivity.class));
	    } else {
		ToastUtils.showToast(getActivity(), "您还没有登录，请登录后再试");
	    }
	    break;

	default:
	    break;
	}
    }
}