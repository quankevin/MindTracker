package com.ppkj.mindrays.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.ppkj.mindrays.R;
import com.ppkj.mindrays.adapter.FileAdapter;
import com.ppkj.mindrays.adapter.CloudAdapter.OnFileSelectedListener;
import com.ppkj.mindrays.adapter.UpLoadAdapter.OnUploadCancelListener;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.localbean.ImageFile;
import com.ppkj.mindrays.localbean.PODocu;
import com.ppkj.mindrays.service.MediaScannerService.IMediaScannerObserver;
import com.ppkj.mindrays.utils.DateUtil;
import com.ppkj.mindrays.utils.FileUtils;
import com.ppkj.mindrays.utils.ToastUtils;

public class FileActivity extends BaseActivity implements OnItemClickListener,
	IMediaScannerObserver, OnFileSelectedListener, OnUploadCancelListener {
    private ListView mListView;
    private LinearLayout mLoading;
    private TextView mEmptyView;
    private FileAdapter mFileAdapter;
    private List<ImageFile> fileItem;
    private ConcurrentHashMap<String, String> mScanMap;

    private ConcurrentHashMap<String, List<ImageFile>> mFileMap;
    // private LoadingDialog mLoading;
    private PhotoAsyncTask mTask;

    @Override
    public void onCancel(PODocu file) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onFileSelected(PODocu file) {
	// TODO Auto-generated method stub

    }

    @Override
    public void update(int flag, PODocu media) {

    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	mTask.cancel(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
	    long id) {
	String name = DateUtil.formatDateString(this, fileItem.get(position)
		.getPhotoDate());
	ArrayList<ImageFile> ifs = (ArrayList<ImageFile>) mFileMap.get(name);

	Intent intent = new Intent(this, ImageGridActivity.class);
	intent.putExtra("name", name);
	intent.putParcelableArrayListExtra(ImageGridActivity.EXTRA_IMAGE_LIST,
		ifs);
	startActivity(intent);
    }

    @Override
    public void setContentLayout() {
	// TODO Auto-generated method stub
	setContentView(R.layout.activity_file);
    }

    @Override
    public void dealLogicBeforeInitView() {
	// TODO Auto-generated method stub

    }

    @Override
    public void initView() {
	// TODO Auto-generated method stub
	mListView = (ListView) findViewById(R.id.file_list);
	mLoading = (LinearLayout) findViewById(R.id.file_wait);
	mEmptyView = (TextView) findViewById(R.id.file_empty);
	mListView.setOnItemClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
	// TODO Auto-generated method stub
	if (!FileUtils.sdAvailable()) {
	    mEmptyView.setVisibility(View.VISIBLE);
	    mListView.setVisibility(View.GONE);
	    mLoading.setVisibility(View.GONE);
	    ToastUtils.showLongToast(this, "SD卡不存在...");
	    return;
	}
	if (mScanMap == null) {
	    mScanMap = new ConcurrentHashMap<String, String>();
	}
	if (mFileMap == null) {
	    mFileMap = new ConcurrentHashMap<String, List<ImageFile>>();
	}
	mFileMap.clear();
	mScanMap.put(FileUtils.sdExtCard(this) + "/DCIM", "");
	mScanMap.put(FileUtils.sdCard(this) + "/DCIM", "");
	mTask = new PhotoAsyncTask();
	mTask.execute();
	fileItem = new ArrayList<ImageFile>();

	mFileAdapter = new FileAdapter(this, fileItem);
	mListView.setAdapter(mFileAdapter);
    }

    @Override
    public void onClickEvent(View view) {
	// TODO Auto-generated method stub

    }

    /** 扫描文件夹 */
    private void scanDirectory(String path) {
	eachAllMedias(new File(path));
    }

    /** 递归查找视频 */
    private void eachAllMedias(File f) {
	if (f != null && f.exists() && f.isDirectory()) {
	    File[] files = f.listFiles();
	    if (files != null) {
		for (File file : f.listFiles()) {
		    if (file.isDirectory()) {
			// 忽略.开头的文件夹
			if (!file.getName().startsWith("."))
			    eachAllMedias(file);
		    } else if (file.exists() && file.canRead()
			    && FileUtils.isImage(file)) {
			try {
			    save(file);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		}
	    }
	}
    }

    private void save(File file) {

	ImageFile ifs = new ImageFile();
	ifs.setIsSelect(0);
	ifs.setPhotoDate(file.lastModified());
	ifs.setPhotoPath(file.getAbsolutePath());
	ifs.setPhotoTotal(0);
	ifs.setPhotoName(file.getName());
	String realTime = DateUtil.formatDateString(this, ifs.getPhotoDate());
	if (!mFileMap.containsKey(realTime)) {
	    ArrayList<ImageFile> ii = new ArrayList<ImageFile>();
	    mFileMap.put(realTime, ii);
	}
	mFileMap.get(realTime).add(ifs);

    }

    public class FileComparator implements Comparator<ImageFile> {
	public int compare(ImageFile file1, ImageFile file2) {
	    if (file1.getPhotoDate() > file2.getPhotoDate()) {
		return -1;
	    } else {
		return 1;
	    }
	}
    }

    /** 扫描 */
    private void scan() {

	while (mScanMap.keySet().size() > 0) {

	    String path = "";
	    for (String key : mScanMap.keySet()) {
		path = key;
		break;
	    }
	    if (mScanMap.containsKey(path)) {
		String mimeType = mScanMap.get(path);
		if ("".equals(mimeType)) {
		    scanDirectory(path);
		} else {
		    // scanFile(path, mimeType);
		}

		// 扫描完成一个
		mScanMap.remove(path);
	    }
	}
    }

    private class PhotoAsyncTask extends AsyncTask<Void, Void, List<ImageFile>> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    mLoading.setVisibility(View.VISIBLE);
	    mListView.setVisibility(View.GONE);
	    mEmptyView.setVisibility(View.GONE);
	}

	@Override
	protected List<ImageFile> doInBackground(Void... params) {

	    scan();
	    List<ImageFile> object = new ArrayList<ImageFile>();
	    Iterator<String> it = mFileMap.keySet().iterator();
	    while (it.hasNext()) {
		String n = it.next();
		ImageFile iif = new ImageFile();
		iif.setIsSelect(0);
		iif.setPhotoDateString(n);
		iif.setPhotoDate(mFileMap.get(n).get(0).getPhotoDate());
		iif.setPhotoTotal(mFileMap.get(n).size());
		List<ImageFile> i = mFileMap.get(n);
		String paths = "";
		if (mFileMap.get(n).size() == 1) {
		    paths = i.get(0).getPhotoPath();
		} else if (mFileMap.get(n).size() == 2) {
		    paths = i.get(0).getPhotoPath() + ","
			    + i.get(1).getPhotoPath() + ",";
		} else if (mFileMap.get(n).size() == 3) {
		    paths = i.get(0).getPhotoPath() + ","
			    + i.get(1).getPhotoPath() + ","
			    + i.get(2).getPhotoPath() + ",";

		} else {
		    paths = i.get(0).getPhotoPath() + ","
			    + i.get(1).getPhotoPath() + ","
			    + i.get(2).getPhotoPath() + ","
			    + i.get(3).getPhotoPath() + ",";

		}
		iif.setPaths(paths);
		object.add(iif);
	    }
	    Collections.sort(object, new FileComparator());
	    return object;

	}

	@Override
	protected void onPostExecute(List<ImageFile> result) {
	    super.onPostExecute(result);
	    mLoading.setVisibility(View.GONE);
	    mListView.setVisibility(View.VISIBLE);
	    fileItem.addAll(result);
	    mFileAdapter.notifyDataSetChanged();
	    if (result.size() == 0) {
		mLoading.setVisibility(View.GONE);
		mListView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.VISIBLE);
	    }
	}
    }

}
