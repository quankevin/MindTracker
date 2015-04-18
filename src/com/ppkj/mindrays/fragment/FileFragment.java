package com.ppkj.mindrays.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ppkj.mindrays.R;
import com.ppkj.mindrays.activity.FileActivity;
import com.ppkj.mindrays.base.BaseFragment;

public class FileFragment extends BaseFragment {
    private RelativeLayout device_file;
    private RelativeLayout local_file;
    private static FileFragment ff;

    public static FileFragment getInstance() {
	if (ff == null) {
	    ff = new FileFragment();

	}
	return ff;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.main_frg_file, container, false);
	getAndSetViews(view);
	return view;
    }

    private void getAndSetViews(View v) {
	device_file = (RelativeLayout) v.findViewById(R.id.device_file);
	local_file = (RelativeLayout) v.findViewById(R.id.local_file);

	device_file.setOnClickListener(this);
	local_file.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

	super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDetach() {

	super.onDetach();
    }

    @Override
    public void onClickEvent(View v) {
	Intent i = new Intent();
	switch (v.getId()) {
	case R.id.device_file:
	    // i.setClass(this, cls)
	    break;
	case R.id.local_file:
	    i.setClass(getActivity(), FileActivity.class);
	    getActivity().startActivity(i);
	    break;

	default:
	    break;
	}
    }

}
