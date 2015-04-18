package com.ppkj.mindrays.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.activity.LocationActivity;
import com.ppkj.mindrays.activity.LocationActivity.MyLocationListenner;
import com.ppkj.mindrays.base.BaseFragment;

public class VideoFragment extends BaseFragment {
    private FrameLayout m_btn_map;
    private TextView versionTv;
    private TextView tv_time;
    // private DigitalClock tc_clock;
    private static final LatLng GEO_SHENGZHENG = new LatLng(22.560, 114.064);
    private static final int MAPID = 11111;
    // /////////////////////////////////
    private BaiduMap mBaiduMap;
    private BitmapDescriptor mCurrentMarker;
    private LocationClient mLocClient;
    private MapView u4;
    private MyLocationListenner myListener = new MyLocationListenner();
    private static VideoFragment vf;

    public static VideoFragment getInstance() {
	if (vf == null) {
	    vf = new VideoFragment();
	}
	return vf;
    }

    // //////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.main_frg_video, container, false);
	getViews(view);

	return view;
    }

    private String getCurrDate() {
	Calendar c = Calendar.getInstance();

	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd ");
	return df.format(c.getTime());

    }

    private void getViews(View v) {
	versionTv = (TextView) v.findViewById(R.id.video_status);
	m_btn_map = (FrameLayout) v.findViewById(R.id.map4);
	tv_time = (TextView) v.findViewById(R.id.video_time);
	tv_time.setText(getCurrDate());
	// tc_clock = (DigitalClock) v.findViewById(R.id.digitalClock1);

	u4 = new MapView(getActivity(), new BaiduMapOptions()
		.zoomControlsEnabled(false)
		.zoomGesturesEnabled(false)
		.compassEnabled(false)
		.overlookingGesturesEnabled(true)
		.scrollGesturesEnabled(false)
		.mapStatus(
			new MapStatus.Builder().zoom(11).target(GEO_SHENGZHENG)
				.build()));

	m_btn_map.addView(u4);
	// mBaiduMap = u4.getMap();// 开启定位图层
	// mBaiduMap.setMyLocationEnabled(true);
	// 定位初始化
	// mLocClient = new LocationClient(getActivity());
	// mLocClient.registerLocationListener(myListener);
	// mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
	// LocationMode.FOLLOWING, true, mCurrentMarker));
	// LocationClientOption option = new LocationClientOption();
	// option.setOpenGps(true);// 打开gps
	// option.setCoorType("bd09ll"); // 设置坐标类型
	// option.setScanSpan(1000);
	// mLocClient.setLocOption(option);
	// mLocClient.start();
	LinearLayout l = new LinearLayout(getActivity());
	l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		LayoutParams.MATCH_PARENT));
	l.setId(MAPID);
	l.setBackgroundResource(R.drawable.main_map_bg2);
	m_btn_map.addView(l);
	setListener(l);
    }

    private void setListener(View v) {
	// TODO Auto-generated method stub
	v.setOnClickListener(this);
    }

    @Override
    public void onPause() {

	u4.onPause();
	super.onPause();
    }

    @Override
    public void onResume() {
	u4.onResume();
	// TODO Auto-generated method stub
	super.onResume();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

	@Override
	public void onReceiveLocation(BDLocation location) {
	    // map view 销毁后不在处理新接收的位置
	    if (location == null || u4 == null)
		return;
	    MyLocationData locData = new MyLocationData.Builder()
		    .accuracy(location.getRadius())
		    // 此处设置开发者获取到的方向信息，顺时针0-360
		    .direction(100).latitude(location.getLatitude())
		    .longitude(location.getLongitude()).build();
	    mBaiduMap.setMyLocationData(locData);

	    LatLng ll = new LatLng(location.getLatitude(),
		    location.getLongitude());
	    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
	    mBaiduMap.animateMapStatus(u);

	}

	public void onReceivePoi(BDLocation poiLocation) {
	}
    }

    @Override
    public void onClickEvent(View v) {
	switch (v.getId()) {
	case MAPID:
	    Intent i_map = new Intent();
	    i_map.setClass(getActivity(), LocationActivity.class);
	    startActivity(i_map);
	    break;

	default:
	    break;
	}
    }

}
