package com.ppkj.mindrays.activity;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.ppkj.mindrays.R;
import com.ppkj.mindrays.base.BaseActivity;
import com.ppkj.mindrays.bean.AboutResponse;
import com.ppkj.mindrays.network.HttpRequestAsyncTask;
import com.ppkj.mindrays.network.HttpRequestAsyncTask.OnCompleteListener;
import com.ppkj.mindrays.network.Request;
import com.ppkj.mindrays.network.RequestMaker;
import com.ppkj.mindrays.view.LoadingDialog;

public class AboutActivity extends BaseActivity {
	private WebView web_about;
	private TextView tv_title;
	private LoadingDialog mLoadingDialog;
	private RequestMaker mRequestMaker;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_about);
	}

	@Override
	public void dealLogicBeforeInitView() {
		// TODO Auto-generated method stub
		mRequestMaker = RequestMaker.getInstance();
		mLoadingDialog = new LoadingDialog(this);

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		web_about = (WebView) findViewById(R.id.web_about);
		tv_title = (TextView) findViewById(R.id.title_head);
		findViewById(R.id.title_back).setOnClickListener(this);
	}

	@Override
	public void dealLogicAfterInitView() {
		mLoadingDialog.show("请求中，请稍候...");
		Request request = mRequestMaker.getAbout();
		HttpRequestAsyncTask hat = new HttpRequestAsyncTask();
		hat.execute(request);
		hat.setOnCompleteListener(new OnCompleteListener<AboutResponse>() {

			@Override
			public void onComplete(AboutResponse result, String resultString) {
				mLoadingDialog.dismiss();
				if (result != null) {
					web_about.loadDataWithBaseURL(null,
							result.mAboutBean.getEntity_desc(), "text/html",
							"utf-8", null);

					tv_title.setText(result.mAboutBean.getEntity_title());
				}
			}
		});
		// String html =
		// "<html> <body> 图书封面<br><table width='200' border='1' ><tr><td><a onclick='alert(\"Java Web开发速学宝典\")' ><img style='margin:10px' src='http://images.china-pub.com/ebook45001-50000/48015/cover.jpg' width='100'/></a></td><td><a onclick='alert(\"大象--Thinking in UML\")' ><img style='margin:10px' src='http://images.china-pub.com/ebook125001-130000/129881/zcover.jpg' width='100'/></td></tr><tr><td><img style='margin:10px' src='http://images.china-pub.com/ebook25001-30000/27518/zcover.jpg' width='100'/></td><td><img  style='margin:10px' src='http://images.china-pub.com/ebook30001-35000/34838/zcover.jpg' width='100'/></td></tr></table></body></html>";
		//
		// tv_about.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
		web_about.getSettings().setJavaScriptEnabled(true);
		web_about.setWebChromeClient(new WebChromeClient());
	}

	@Override
	public void onClickEvent(View view) {
		// TODO Auto-generated method stub

	}

}
