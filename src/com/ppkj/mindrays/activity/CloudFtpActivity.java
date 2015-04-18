package com.ppkj.mindrays.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.ppkj.mindrays.R;
import com.ppkj.mindrays.adapter.FtpAdapter;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.ftp.FtpUtil;
import com.ppkj.mindrays.localbean.PODocu;
import com.ppkj.mindrays.utils.FileUtils;
import com.ppkj.mindrays.utils.IntentUtil;
import com.ppkj.mindrays.utils.LogUtil;
import com.ppkj.mindrays.utils.PinyinUtils;
import com.ppkj.mindrays.utils.ToastUtils;

public class CloudFtpActivity extends BaseActivity implements
	OnItemClickListener {
    private ListView mListView;
    private FtpAdapter mTypeAdapter;

    private List<FTPFile> listftpfile;
    private static final int LISTVIEW_REFRESH = 0x111;
    private String localpath = "";

    private ArrayList<String> remotePaths;

    @Override
    public void setContentLayout() {

	setContentView(R.layout.activity_cloudftp);
    }

    @Override
    public void dealLogicBeforeInitView() {
	listftpfile = new ArrayList<FTPFile>();

	remotePaths = new ArrayList<String>();
	// remotePaths.add("/");
	if (!FileUtils.sdAvailable()) {
	    ToastUtils.showLongToast(this, "SD卡不存在...");
	}
	localpath = FileUtils.sdCard(this) + "/ppkjremote";
    }

    @Override
    public void initView() {
	// TODO Auto-generated method stub
	mListView = (ListView) findViewById(R.id.file_list);
	findViewById(R.id.title_back).setOnClickListener(this);
	mTypeAdapter = new FtpAdapter(this, listftpfile);
	mListView.setAdapter(mTypeAdapter);
	mListView.setOnItemClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
	new processTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	LogUtil.log("listftpfile.get(arg2).getGroup()=="
		+ listftpfile.get(arg2).toFormattedString());
	FTPFile fp = listftpfile.get(arg2);

	if (fp.isDirectory()) {

	    if (fp.getName().equals(".") || fp.getName().equals("..")) {
		if (remotePaths.size() > 0) {
		    remotePaths.remove(remotePaths.size() - 1);
		}
	    } else {

		remotePaths.add("/" + fp.getName());
	    }
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < remotePaths.size(); i++) {
		sb.append(remotePaths.get(i));
	    }
	    new processTask().execute(sb.toString());
	} else {
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < remotePaths.size(); i++) {
		sb.append(remotePaths.get(i));
	    }
	    new DownLoadTask().execute(sb.toString(), listftpfile.get(arg2)
		    .getName());
	}
    }

    @Override
    public void onBackPressed() {
	if (remotePaths.size() > 0) {
	    remotePaths.remove(remotePaths.size() - 1);
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < remotePaths.size(); i++) {
		sb.append(remotePaths.get(i));
	    }
	    new processTask().execute(sb.toString());
	    return;
	}
	super.onBackPressed();
    }

    @Override
    public void onClickEvent(View view) {
	// TODO Auto-generated method stub

    }

    private Handler mHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    // TODO Auto-generated method stub
	    switch (msg.what) {
	    case LISTVIEW_REFRESH:
		mTypeAdapter.notifyDataSetChanged();
		break;

	    default:
		break;
	    }
	}
    };

    private class processTask extends AsyncTask<String, Void, Void> {
	private ProgressDialog Dialog = new ProgressDialog(
		CloudFtpActivity.this);

	protected void onPreExecute() {
	    Dialog.setMessage("获取文件内容中...");
	    Dialog.show();
	}

	@Override
	protected Void doInBackground(String... arg0) {
	    String path = "/";
	    if (arg0.length > 0) {
		path = arg0[0];
	    }
	    SharedPreferences myPref = PreferenceManager
		    .getDefaultSharedPreferences(getBaseContext());
	    String ipaddr = myPref.getString("etServer", "42.120.17.134");
	    int port = Integer.parseInt(myPref.getString("port", "21"));
	    // String filePath = myPref.getString("path", "/");
	    String uname = myPref.getString("uname", "test01");
	    String pass = myPref.getString("pass", "test01");

	    FTPFile[] ftpFiles = FtpUtil.listFile(ipaddr, port, uname, pass,
		    path);
	    listftpfile.clear();
	    for (int i = 0; i < ftpFiles.length; i++) {
		listftpfile.add(ftpFiles[i]);
	    }
	    mHandler.sendEmptyMessage(LISTVIEW_REFRESH);

	    return null;
	}

	protected void onPostExecute(Void unused) {
	    Dialog.dismiss();
	}
    }

    private class DownLoadTask extends AsyncTask<String, Void, File> {
	private ProgressDialog Dialog = new ProgressDialog(
		CloudFtpActivity.this);

	protected void onPreExecute() {
	    Dialog.setMessage("下载文件中...");
	    Dialog.show();
	}

	@Override
	protected File doInBackground(String... arg0) {
	    String path = arg0[0];
	    String fileName = arg0[1];

	    SharedPreferences myPref = PreferenceManager
		    .getDefaultSharedPreferences(getBaseContext());
	    String ipaddr = myPref.getString("etServer", "42.120.17.134");
	    int port = Integer.parseInt(myPref.getString("port", "21"));
	    String uname = myPref.getString("uname", "test01");
	    String pass = myPref.getString("pass", "test01");
	    if (localpath.equals("")) {
		ToastUtils.showLongToast(CloudFtpActivity.this, "SD卡不存在，不能下载");
	    } else {
		return FtpUtil.downloadFile(ipaddr, port, uname, pass, path,
			fileName, localpath);
	    }

	    return null;
	}

	protected void onPostExecute(File file) {

	    Dialog.dismiss();
	    ToastUtils.showLongToast(CloudFtpActivity.this, "文件已下载至本地");

	    if (file != null) {
		save(new PODocu(file));
		IntentUtil.startIntent(CloudFtpActivity.this,
			file.getAbsolutePath());
	    }
	}
    }

    private void save(PODocu media) {
	media.isNet = true;
	DbUtils db = DbUtils.create(this);
	db.configAllowTransaction(true);
	db.configDebug(false);
	// 检测
	PODocu pos = null;
	try {
	    pos = db.findFirst(Selector.from(PODocu.class).where("path", "=",
		    media.path));
	    pos.isNet = true;
	} catch (DbException e) {

	}
	if (pos == null) {
	    try {
		if (media.title != null && media.title.length() > 0)
		    media.title_key = PinyinUtils.chineneToSpell(media.title
			    .charAt(0) + "");
	    } catch (Exception ex) {
		LogUtil.log(ex);
	    }
	    media.last_access_time = System.currentTimeMillis();
	    try {
		db.save(media);
	    } catch (DbException e) {
		e.printStackTrace();
	    }
	} else {
	    try {
		db.update(pos, "isNet");
	    } catch (DbException e) {
		e.printStackTrace();
	    }
	}
    }
}
