package com.ppkj.mindrays.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.adapter.CloudAdapter;
import com.ppkj.mindrays.adapter.CloudAdapter.OnFileSelectedListener;
import com.ppkj.mindrays.adapter.UpLoadAdapter;
import com.ppkj.mindrays.adapter.UpLoadAdapter.OnUploadCancelListener;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.ftp.FtpUtil;
import com.ppkj.mindrays.localbean.PODocu;
import com.ppkj.mindrays.service.MediaScannerService;
import com.ppkj.mindrays.service.MediaScannerService.IMediaScannerObserver;
import com.ppkj.mindrays.service.MediaScannerService.MediaScannerServiceBinder;
import com.ppkj.mindrays.utils.FileUtils;
import com.ppkj.mindrays.utils.IntentUtil;
import com.ppkj.mindrays.utils.ToastUtils;
import com.ppkj.mindrays.view.LoadingDialog;

public class CloudActivity extends BaseActivity implements OnItemClickListener,
	IMediaScannerObserver, OnFileSelectedListener, OnUploadCancelListener {

    private TextView tv_title;
    private TextView tv_upload;
    private String str_title;
    private String filetype;
    private ListView mListView;
    private CloudAdapter mFileAdapter;
    private UpLoadAdapter mUpLoadAdapter;
    private MediaScannerService mMediaScannerService;
    private DataTask mTask;
    private UpLoadTask mUpLoadTask;
    private boolean uploadStatue = false;
    private LoadingDialog mLoadingDialog;
    private final HashSet<PODocu> selectedPos = new HashSet<PODocu>();
    // ///////////////////
    private SharedPreferences myPref;
    private String ipaddr;
    private int port;
    private String uname;
    private String pass;
    private String path;
    // ////////////////////
    private ServiceConnection mMediaScannerServiceConnection = new ServiceConnection() {

	@Override
	public void onServiceDisconnected(ComponentName name) {
	    mMediaScannerService = null;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
	    mMediaScannerService = ((MediaScannerServiceBinder) service)
		    .getService();
	    mMediaScannerService.addObserver(CloudActivity.this);
	}
    };

    @Override
    public void setContentLayout() {
	// TODO Auto-generated method stub
	setContentView(R.layout.activity_cloud);
    }

    @Override
    public void dealLogicBeforeInitView() {
	myPref = PreferenceManager
		.getDefaultSharedPreferences(getBaseContext());
	ipaddr = myPref.getString("etServer", "42.120.17.134");
	port = Integer.parseInt(myPref.getString("port", "21"));
	uname = myPref.getString("uname", "test01");
	pass = myPref.getString("pass", "test01");
	path = "";
	filetype = getIntent().getStringExtra("filetype");
	str_title = getIntent().getStringExtra("title_name");
	mLoadingDialog = new LoadingDialog(this);
	alpos = new ArrayList<PODocu>();
    }

    @Override
    public void initView() {
	findViewById(R.id.title_back).setOnClickListener(this);
	tv_title = (TextView) findViewById(R.id.title_cloud_name);
	mListView = (ListView) findViewById(R.id.file_list);
	mListView.setOnItemClickListener(this);
	// mListView.setOnItemLongClickListener(this);
	tv_title.setText(str_title);
	tv_upload = (TextView) findViewById(R.id.cloud_upload);
	tv_upload.setOnClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
	// TODO Auto-generated method stub
	mTask = new DataTask();
	mUpLoadTask = new UpLoadTask();
	mTask.execute(filetype);
	bindService(new Intent(this.getApplicationContext(),
		MediaScannerService.class), mMediaScannerServiceConnection,
		Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
	unbindService(mMediaScannerServiceConnection);

	super.onDestroy();
    }

    @Override
    public void onClickEvent(View view) {
	switch (view.getId()) {
	case R.id.cloud_upload:
	    if (!uploadStatue) {
		uploadStatue = true;
		tv_upload.setText("确定");
		mFileAdapter.setUploadStatue(uploadStatue);
	    } else {

		if (alpos.size() > 0) {

		    showPopupWindow(view);
		} else {
		    EditText et = new EditText(this);
		    et.setMaxLines(3);
		    et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		    AlertDialog.Builder b = new AlertDialog.Builder(this);
		    b.setTitle("添加标签")
			    .setView(et)
			    .setPositiveButton("确定",
				    new DialogInterface.OnClickListener() {

					@Override
					public void onClick(
						DialogInterface dialog,
						int which) {
					    
					}
				    }).setCancelable(true).create().show();

		    setUploadPODocu();
		}
	    }
	    break;
	case R.id.upload_:
	    setUploadPODocu();

	    break;

	default:
	    break;
	}
    }

    private PopupWindow popupWindow;

    private void showPopupWindow(View vv) {
	View v = LayoutInflater.from(this).inflate(R.layout.uploadlist, null);
	popupWindow = new PopupWindow(v, getScreenWidth() * 2 / 3,
		getScreenHeight() * 2 / 3);

	Button cancel = (Button) v.findViewById(R.id.upload_);
	ListView lv = (ListView) v.findViewById(R.id.upload_lv);
	lv.setAdapter(mUpLoadAdapter);
	cancel.setOnClickListener(this);
	popupWindow.setContentView(v);
	popupWindow.setOutsideTouchable(true);
	popupWindow.setFocusable(true);
	popupWindow.setBackgroundDrawable(new BitmapDrawable());
	popupWindow.showAsDropDown(vv);

    }

    @Override
    public void onCancel(PODocu po) {
	alpos.remove(po);
	mUpLoadAdapter.notifyDataSetChanged();
    }

    private ArrayList<PODocu> alpos;

    private void setUploadPODocu() {
	if (selectedPos.size() > 0) {

	    for (PODocu each : selectedPos) {
		if (!alpos.contains(each)) {
		    alpos.add(each);
		}
	    }
	    mUpLoadAdapter.notifyDataSetChanged();

	    if (alpos.size() > 0
		    && mUpLoadTask.getStatus() != AsyncTask.Status.RUNNING) {
		// LogUtil.log(" mUpLoadTask.execute()");

		mUpLoadTask.cancel(false);
		mUpLoadTask = null;
		mUpLoadTask = new UpLoadTask();
		mUpLoadTask.execute();
	    }
	} else {
	    ToastUtils.showToast(this, "请选择要上传的文件...");
	}
	selectedPos.clear();
	mFileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFileSelected(PODocu file) {
	// TODO Auto-generated method stub
	toggleFileSelected(file);
    }

    void setFileSelected(PODocu po, boolean selected) {
	uploadStatue = true;

	if (selected)
	    selectedPos.add(po);
	else
	    selectedPos.remove(po);
	if (mFileAdapter != null) {
	    mFileAdapter.setUploadStatue(uploadStatue);
	    mFileAdapter.notifyDataSetChanged();
	}
	if (selectedPos.isEmpty()) {
	    uploadStatue = false;
	}
    }

    void toggleFileSelected(PODocu po) {
	setFileSelected(po, !selectedPos.contains(po));
    }

    private class UpLoadTask extends AsyncTask<Void, Void, String> {

	protected void onPreExecute() {

	}

	@Override
	protected String doInBackground(Void... arg0) {

	    PODocu po = alpos.get(0);
	    File inputfile = new File(po.path);

	    return FtpUtil.uploadFile(ipaddr, port, uname, pass, path,
		    inputfile);
	}

	protected void onPostExecute(String filepath) {

	    if (filepath != null) {
		updatePODocu(filepath);
		alpos.remove(0);

		mUpLoadAdapter.notifyDataSetChanged();
	    }

	    if (alpos.size() > 0) {

		mUpLoadTask.cancel(false);
		mUpLoadTask = null;
		mUpLoadTask = new UpLoadTask();
		mUpLoadTask.execute();

	    } else {
		uploadStatue = false;
		if (mFileAdapter != null) {
		    mFileAdapter.setUploadStatue(uploadStatue);
		    mFileAdapter.notifyDataSetChanged();
		}
		tv_upload.setText("上传");
	    }
	}
    }

    private void updatePODocu(String filepath) {
	DbUtils db = DbUtils.create(this);
	db.configAllowTransaction(true);
	db.configDebug(false);
	// 检测
	PODocu po = null;

	try {
	    po = db.findFirst(Selector.from(PODocu.class).where("path", "=",
		    filepath));
	} catch (DbException e) {

	}
	if (po != null) {
	    po.isNet = true;
	    try {
		db.update(po);
	    } catch (DbException e) {
		e.printStackTrace();
	    }
	    mFileAdapter.setItem(po);
	}

    }

    @Override
    public void update(int flag, PODocu media) {

	switch (flag) {
	case MediaScannerService.SCAN_STATUS_START:

	    break;
	case MediaScannerService.SCAN_STATUS_END:// 扫描完成

	    break;
	case MediaScannerService.SCAN_STATUS_RUNNING:// 扫到一个文件
	    if (mFileAdapter != null && media != null) {
		if (filetype.equals("Video")) {
		    if (Arrays.asList(Constants.VIDEO_EXTENSIONS).contains(
			    media.file_type)) {
			mFileAdapter.add(media);
			mFileAdapter.notifyDataSetChanged();
		    }
		} else if (filetype.equals("Image")) {
		    if (Arrays.asList(Constants.IMAGE_EXTENSIONS).contains(
			    media.file_type)) {
			mFileAdapter.add(media);
			mFileAdapter.notifyDataSetChanged();
		    }
		} else {
		    if (media.file_type.equals(filetype)) {
			mFileAdapter.add(media);
			mFileAdapter.notifyDataSetChanged();
		    }
		}
	    }
	    break;
	}

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	Object p = arg0.getItemAtPosition(arg2);
	if (p instanceof PODocu) {
	    if (uploadStatue) {
		PODocu selectedFile = (PODocu) p;
		if (selectedFile.isNet)
		    ToastUtils.showToast(this, "文件已上传");
		else
		    toggleFileSelected(selectedFile);
	    } else {
		String string = ((PODocu) p).path;
		IntentUtil.startIntent(this, string);
	    }
	}

    }

    public class DataTask extends AsyncTask<String, Void, List<PODocu>> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    mLoadingDialog.show("获取文件中...");
	    mListView.setVisibility(View.GONE);
	}

	@Override
	protected List<PODocu> doInBackground(String... params) {
	    String type = params[0];
	    if (type.equals("Video")) {
		return FileUtils.getAllSortFiles(CloudActivity.this, 1);
	    } else if (type.equals("Image")) {
		return FileUtils.getAllSortFiles(CloudActivity.this, 2);
	    } else {
		return FileUtils.getAllSortFiles(CloudActivity.this, type);
	    }
	}

	@Override
	protected void onPostExecute(List<PODocu> result) {
	    super.onPostExecute(result);
	    mLoadingDialog.dismiss();
	    mFileAdapter = new CloudAdapter(CloudActivity.this, result);
	    mUpLoadAdapter = new UpLoadAdapter(CloudActivity.this, alpos);
	    mUpLoadAdapter.setOnUploadCancelListener(CloudActivity.this);
	    mFileAdapter.setSelectedPos(selectedPos);
	    mFileAdapter.setOnFileSelectedListener(CloudActivity.this);
	    mListView.setAdapter(mFileAdapter);
	    mListView.setVisibility(View.VISIBLE);
	}
    }

}
