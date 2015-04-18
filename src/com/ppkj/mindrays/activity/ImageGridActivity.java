package com.ppkj.mindrays.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.comcast.freeflow.core.AbsLayoutContainer;
import com.comcast.freeflow.core.FreeFlowContainer;
import com.comcast.freeflow.core.FreeFlowContainer.OnScrollListener;
import com.comcast.freeflow.core.FreeFlowItem;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.adapter.ArtBookAdapter;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.localbean.ImageFile;
import com.ppkj.mindrays.utils.LogUtil;
import com.ppkj.mindrays.view.ArtbookLayout;

public class ImageGridActivity extends BaseActivity {
    public static final String EXTRA_IMAGE_LIST = "imagelist";

    public static final String EXTRA_IMAGE_POSITION = "imageposition";

    private ArrayList<ImageFile> dataList;
    private TextView tv_title;
    private String str_title;
    // ////
    private FreeFlowContainer container;
    private ArtbookLayout custom;
    private ArtBookAdapter adapter;

    @Override
    public void setContentLayout() {
	// TODO Auto-generated method stub
	setContentView(R.layout.activity_image_grid);

    }

    @Override
    public void dealLogicBeforeInitView() {
	// TODO Auto-generated method stub
	dataList = getIntent().getParcelableArrayListExtra(EXTRA_IMAGE_LIST);
	str_title = getIntent().getStringExtra("name");
    }

    @Override
    public void initView() {
	findViewById(R.id.title_back).setOnClickListener(this);
	container = (FreeFlowContainer) findViewById(R.id.container);
	custom = new ArtbookLayout();
	adapter = new ArtBookAdapter(this);
	tv_title = (TextView) findViewById(R.id.title_head);
	container.setLayout(custom);
	container.setAdapter(adapter);
	adapter.update(dataList);
	container.dataInvalidated();
	container
		.setOnItemClickListener(new FreeFlowContainer.OnItemClickListener() {
		    @Override
		    public void onItemClick(AbsLayoutContainer parent,
			    FreeFlowItem proxy) {
			Intent intent = new Intent(ImageGridActivity.this,
				ViewPagerActivity.class);
			intent.putParcelableArrayListExtra(
				ImageGridActivity.EXTRA_IMAGE_LIST, dataList);
			intent.putExtra(ImageGridActivity.EXTRA_IMAGE_POSITION,
				proxy.itemIndex);
			startActivity(intent);
		    }
		});

	container.addScrollListener(new OnScrollListener() {

	    @Override
	    public void onScroll(FreeFlowContainer container) {
		LogUtil.log("scroll percent " + container.getScrollPercentY());
	    }
	});

    }

    @Override
    public void dealLogicAfterInitView() {
	// TODO Auto-generated method stub
	tv_title.setText(str_title);
    }

    @Override
    public void onClickEvent(View view) {
	// TODO Auto-generated method stub

    }
}
