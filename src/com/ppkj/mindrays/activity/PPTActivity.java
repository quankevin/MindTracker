package com.ppkj.mindrays.activity;

import android.view.View;

import com.itsrts.pptviewer.PPTViewer;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;

public class PPTActivity extends BaseActivity {

    private PPTViewer pptViewer;
    private String path;

    @Override
    public void setContentLayout() {

	setContentView(R.layout.ppt_activity);
    }

    @Override
    public void dealLogicBeforeInitView() {

	path = getIntent().getStringExtra("filePath");
    }

    @Override
    public void initView() {

	pptViewer = (PPTViewer) findViewById(R.id.pptviewer);
	pptViewer.setNext_img(R.drawable.next).setPrev_img(R.drawable.prev)
		.setSettings_img(R.drawable.settings)
		.setZoomin_img(R.drawable.zoomin)
		.setZoomout_img(R.drawable.zoomout);
    }

    @Override
    public void dealLogicAfterInitView() {

	pptViewer.loadPPT(this, path);
    }

    @Override
    public void onClickEvent(View view) {

    }
}
