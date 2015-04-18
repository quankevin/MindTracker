package com.ppkj.mindrays.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.bean.MsgResponse;
import com.ppkj.mindrays.bean.UserBean;
import com.ppkj.mindrays.bean.UserResponse;
import com.ppkj.mindrays.city.CityUtils;
import com.ppkj.mindrays.city.MyRegion;
import com.ppkj.mindrays.network.HttpRequestAsyncTask;
import com.ppkj.mindrays.network.HttpRequestAsyncTask.OnCompleteListener;
import com.ppkj.mindrays.network.Request;
import com.ppkj.mindrays.network.RequestMaker;
import com.ppkj.mindrays.uploadimage.FormFile;
import com.ppkj.mindrays.uploadimage.UpLoadImageHelper;
import com.ppkj.mindrays.uploadimage.UpLoadImageHelper.OnUploadImageCompleteListener;
import com.ppkj.mindrays.utils.StringUtils;
import com.ppkj.mindrays.utils.ToastUtils;
import com.ppkj.mindrays.view.LoadingDialog;
import com.ppkj.mindrays.widget.RoundedImageView;

public class InfoActivity extends BaseActivity implements OnDateSetListener {

    private TextView tv_title;
    private TextView btn_next;
    private TextView btn_back;
    private RoundedImageView head_view;
    private TextView tv_name;
    private TextView tv_gender;
    private TextView tv_birthday;
    private TextView tv_city;
    // ////
    private LoadingDialog mLoadingDialog;
    private RequestMaker mRequestMaker;
    private UserBean mUserBean;

    // ///
    public static final int CROP_BIG_PICTURE = 3024;
    private String picturePath;
    private Uri cropImageUri;
    private UpLoadImageHelper mLoadImageHelper;
    private BitmapUtils bitmapUtils;

    // ////
    @Override
    public void setContentLayout() {
	// TODO Auto-generated method stub
	setContentView(R.layout.userinfo_me);
    }

    @Override
    public void dealLogicBeforeInitView() {
	// TODO Auto-generated method stub
	mRequestMaker = RequestMaker.getInstance();
	mLoadingDialog = new LoadingDialog(this);
	mUserBean = mSharedPrefHelper.readUserBean();
	mLoadImageHelper = UpLoadImageHelper.getInstance(this);
	bitmapUtils = new BitmapUtils(this);
	bitmapUtils.configDiskCacheEnabled(true);
	bitmapUtils.configMemoryCacheEnabled(true);
	bitmapUtils.configThreadPoolSize(1);
    }

    @Override
    public void initView() {
	// TODO Auto-generated method stub
	tv_title = (TextView) findViewById(R.id.u_complete_title);
	btn_next = (TextView) findViewById(R.id.u_complete_next);
	btn_next.setOnClickListener(this);
	head_view = (RoundedImageView) findViewById(R.id.info_head);
	tv_birthday = (TextView) findViewById(R.id.info_birthday);
	tv_gender = (TextView) findViewById(R.id.info_gender);
	tv_name = (TextView) findViewById(R.id.info_nickname);
	tv_city = (TextView) findViewById(R.id.info_city);
	btn_back = (TextView) findViewById(R.id.title_back);
	findViewById(R.id.userinfo_click_head).setOnClickListener(this);
	findViewById(R.id.userinfo_click_name).setOnClickListener(this);
	findViewById(R.id.userinfo_click_gender).setOnClickListener(this);
	findViewById(R.id.userinfo_click_birth).setOnClickListener(this);
	findViewById(R.id.userinfo_click_city).setOnClickListener(this);
	findViewById(R.id.userinfo_save).setOnClickListener(this);
	btn_back.setOnClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
	boolean isFromReg = getIntent().getBooleanExtra("isFromReg", false);
	if (isFromReg) {
	    tv_title.setText("完善资料");
	    btn_next.setVisibility(View.VISIBLE);
	} else {
	    tv_title.setText("个人资料");
	    btn_next.setVisibility(View.GONE);
	}

	initUser();
	Request request = mRequestMaker.getUserInfo(mUserBean.getUser_id(),
		mUserBean.getUser_token());
	mLoadingDialog.show("获取用户信息中，请稍候...");
	HttpRequestAsyncTask hat = new HttpRequestAsyncTask();
	hat.execute(request);
	hat.setOnCompleteListener(new OnCompleteListener<UserResponse>() {

	    @Override
	    public void onComplete(UserResponse result, String resultString) {
		mLoadingDialog.dismiss();
		if (result != null) {
		    if (result.userinfo != null) {
			mUserBean = result.userinfo;
			mSharedPrefHelper.writeUserBean(mUserBean);
			initUser();
		    } else {
			ToastUtils.showLongToast(InfoActivity.this,
				result.reMsg);
		    }
		}
	    }
	});
    }

    private void initUser() {
	tv_name.setText(mUserBean.getNickname());
	tv_birthday.setText(mUserBean.getBirthday());
	int gen = 0;
	try {
	    gen = Integer.parseInt(mUserBean.getGender());
	} catch (Exception e) {
	}

	if (gen == 0) {
	    tv_gender.setText("男");
	} else {
	    tv_gender.setText("女");
	}
	tv_city.setText(mUserBean.getLocal());
	if (mUserBean.getHead_url() != null
		|| !mUserBean.getHead_url().equals("")) {
	    ImageLoader.getInstance().displayImage(mUserBean.getHead_url(),
		    head_view);
	} else {
	    head_view.setImageResource(R.drawable.default_user_head);
	}

    }

    @Override
    public void onClickEvent(View view) {
	switch (view.getId()) {
	case R.id.userinfo_click_head:
	    changeHead();
	    break;
	case R.id.userinfo_click_gender:
	    changeGender();
	    break;
	case R.id.userinfo_click_name:
	    changeName();
	    break;
	case R.id.userinfo_click_birth:
	    changeBirthday();
	    break;
	case R.id.userinfo_click_city:
	    changeCity();
	    break;
	case R.id.userinfo_save:
	    onSave();
	case R.id.u_complete_next:
	    Intent i = new Intent();
	    i.setAction("userconfirmed");
	    sendBroadcast(i);
	    finish();
	    break;
	}
    }

    private void onSave() {
	// TODO Auto-generated method stub
	String nickname = tv_name.getText().toString();
	if (StringUtils.isEmpty(nickname)) {
	    ToastUtils.showLongToast(InfoActivity.this, "昵称不能为空");
	    return;
	}
	String gen = "";
	if (tv_gender.getText().toString().equals("男")) {
	    gen = "0";
	} else if (tv_gender.getText().toString().equals("女")) {
	    gen = "1";
	}
	String birth = tv_birthday.getText().toString();
	String city = tv_city.getText().toString();

	mLoadingDialog.show("发送中，请稍候...");
	Request request = mRequestMaker.getUpdate(mUserBean.getUser_id(),
		nickname, gen, birth, city, mUserBean.getUser_token());
	HttpRequestAsyncTask hat = new HttpRequestAsyncTask();
	hat.execute(request);
	hat.setOnCompleteListener(new OnCompleteListener<UserResponse>() {

	    @Override
	    public void onComplete(UserResponse result, String resultString) {
		mLoadingDialog.dismiss();
		if (result != null) {
		    if (result.userinfo != null) {
			mSharedPrefHelper.writeUserBean(result.userinfo);
			finish();
		    } else {
			ToastUtils.showLongToast(InfoActivity.this,
				result.reMsg);
		    }
		} else {
		    ToastUtils.showLongToast(InfoActivity.this,
			    "修改资料时发生错误，请重新保存");
		}
	    }
	});
    }

    private void changeBirthday() {
	// TODO Auto-generated method stub
	Calendar today = Calendar.getInstance();
	int year = today.get(Calendar.YEAR);
	int monthOfYear = today.get(Calendar.MONTH);
	int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);

	new DatePickerDialog(this, this, year, monthOfYear, dayOfMonth).show();
    }

    @Override
    public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
	int year = arg1;
	int month = arg2 + 1;
	int day = arg3;

	StringBuffer sb = new StringBuffer();
	if (year < 10) {
	    sb.append("0");
	}
	sb.append(year);
	sb.append("-");
	if (month < 10) {
	    sb.append("0");
	}
	sb.append(month);
	sb.append("-");
	if (day < 10) {
	    sb.append("0");
	}
	sb.append(day);
	tv_birthday.setText(sb.toString());
    }

    private void changeName() {
	final EditText et_name = new EditText(this);
	new AlertDialog.Builder(this).setTitle("输入昵称").setView(et_name)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface arg0, int arg1) {

			if (StringUtils.isEmpty(et_name.getText().toString())) {
			    ToastUtils.showToast(InfoActivity.this, "请输入昵称");
			    return;
			}
			tv_name.setText(et_name.getText().toString());
		    }
		}).create().show();
    }

    private void changeGender() {
	new AlertDialog.Builder(this)
		.setTitle("选择")
		.setItems(new String[] { "男", "女" },
			new DialogInterface.OnClickListener() {

			    @Override
			    public void onClick(DialogInterface arg0, int arg1) {
				tv_gender
					.setText(new String[] { "男", "女" }[arg1]);
			    }
			}).create().show();

    }

    private CityUtils util;
    private CityAdapter adapter;
    private ArrayList<MyRegion> regions;

    private void changeCity() {
	util = new CityUtils(this, hand);
	regions = new ArrayList<MyRegion>();
	adapter = new CityAdapter(this);

	util.initProvince();

    }

    private class CityAdapter extends ArrayAdapter<MyRegion> {

	LayoutInflater inflater;

	public CityAdapter(Context con) {
	    super(con, 0);
	    inflater = LayoutInflater.from(InfoActivity.this);
	}

	@Override
	public View getView(int arg0, View v, ViewGroup arg2) {
	    v = inflater.inflate(R.layout.city_item, null);
	    TextView tv_city = (TextView) v.findViewById(R.id.tv_city);
	    tv_city.setText(getItem(arg0).getName());
	    return v;
	}

	public void update() {
	    this.notifyDataSetChanged();
	}
    }

    @SuppressLint("HandlerLeak")
    Handler hand = new Handler() {
	@SuppressWarnings("unchecked")
	public void handleMessage(android.os.Message msg) {

	    switch (msg.what) {

	    case 1:

		regions = (ArrayList<MyRegion>) msg.obj;
		adapter.clear();
		adapter.addAll(regions);
		adapter.update();
		new AlertDialog.Builder(InfoActivity.this)
			.setTitle("省份")
			.setOnCancelListener(
				new DialogInterface.OnCancelListener() {

				    @Override
				    public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					tv_city.setText(mUserBean.getLocal());
				    }
				})
			.setAdapter(adapter,
				new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface arg0,
					    int arg1) {
					// TODO Auto-generated method stub
					// 点击省份列表中的省份就初始化城市列表
					util.initCities(regions.get(arg1)
						.getId());
					tv_city.setText(regions.get(arg1)
						.getName());
				    }
				}).create().show();
		break;

	    case 2:
		regions = (ArrayList<MyRegion>) msg.obj;
		adapter.clear();
		adapter.addAll(regions);
		adapter.update();
		new AlertDialog.Builder(InfoActivity.this)
			.setTitle("省份")
			.setOnCancelListener(
				new DialogInterface.OnCancelListener() {

				    @Override
				    public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					tv_city.setText(mUserBean.getLocal());
				    }
				})
			.setAdapter(adapter,
				new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface arg0,
					    int arg1) {
					// TODO Auto-generated method stub
					// 点击省份列表中的省份就初始化城市列表
					String a = tv_city.getText().toString();
					tv_city.setText(a + " "
						+ regions.get(arg1).getName());
				    }
				}).create().show();
		break;

	    }
	};
    };

    private void changeHead() {
	Intent i = new Intent(Intent.ACTION_PICK,
		android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	startActivityForResult(i, 1301);
    }

    /**
     * 用户本地设置成功头像后，更新头像并上传至服务器，由父fragment主动调用
     */
    private void initImg(String picturePath) {
	Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
	if (bitmap == null) {
	    Toast.makeText(this, "未获取到图片信息", Toast.LENGTH_SHORT).show();
	    return;
	}
	head_view.setImageBitmap(bitmap);
	// Toast.makeText(getActivity(), "bitmap"+bitmap, Toast.LENGTH_LONG)
	// .show();
	String fileNameString = picturePath.substring(picturePath
		.lastIndexOf("/"));
	FormFile formFile = new FormFile("uploadHead", picturePath,
		fileNameString);
	mLoadImageHelper.upLoadingImage(
		mRequestMaker.getUploadHead(mUserBean.getUser_id(),
			mUserBean.getUser_token()), formFile,
		new OnUploadImageCompleteListener() {

		    @Override
		    public void onUploadImageSuccess(MsgResponse response) {
			Toast.makeText(InfoActivity.this, "上传成功！",
				Toast.LENGTH_SHORT).show();
			mUserBean.setHead_url(response.head_url);
			mSharedPrefHelper.writeUserBean(mUserBean);
		    }

		    @Override
		    public void onUploadImageFailed() {
			Toast.makeText(InfoActivity.this, "上传失败！",
				Toast.LENGTH_SHORT).show();
		    }
		});
    }

    /**
     * 
     * resultcode==1301用于用户个人信息界面从本地选取图像后显示调用回调方法
     * 
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if (requestCode == 1301 && null != data) {

	    Uri selectedImage = data.getData();
	    String[] filePathColumn = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getContentResolver().query(selectedImage,
		    filePathColumn, null, null, null);
	    cursor.moveToFirst();
	    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	    picturePath = cursor.getString(columnIndex);
	    cursor.close();
	    cropImageUri = cropImageUri(selectedImage, 120, 120,
		    CROP_BIG_PICTURE);

	} else if (requestCode == CROP_BIG_PICTURE) {

	    /*
	     * System.out.println("picturePath = " + picturePath);
	     * System.out.println("cropImageUri = " + cropImageUri);
	     */
	    if (cropImageUri == null || cropImageUri.getPath() == null) {
		return;
	    }
	    initImg(cropImageUri.getPath());
	}
    }

    /**
     * 裁剪照片
     * 
     * @param uri
     * @param outputX
     * @param outputY
     * @param requestCode
     */
    private Uri cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
	File file = new File(Environment.getExternalStorageDirectory()
		+ "/ppkj_mindrays/icon");
	if (!file.exists()) {
	    file.mkdirs();
	}
	File file2 = new File(Environment.getExternalStorageDirectory()
		+ "/ppkj_mindrays/icon" + System.currentTimeMillis() + ".jpg");
	Uri desUri = Uri.fromFile(file2);
	Intent intent = new Intent("com.android.camera.action.CROP");
	intent.setDataAndType(uri, "image/*");
	intent.putExtra("crop", "true");
	intent.putExtra("aspectX", 1);
	intent.putExtra("aspectY", 1);
	intent.putExtra("outputX", outputX);
	intent.putExtra("outputY", outputY);
	intent.putExtra("scale", true);
	intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
	intent.putExtra("return-data", false);
	intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	intent.putExtra("noFaceDetection", true); // no face detection
	startActivityForResult(intent, requestCode);
	return desUri;

    }

}
