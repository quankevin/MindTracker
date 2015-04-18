package com.ppkj.mindrays.network;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

import com.ppkj.mindrays.Constants;
import com.ppkj.mindrays.network.ServerInterfaceDefinition.RequestMethod;
import com.ppkj.mindrays.utils.LogUtil;

public class HttpRequestAsyncTask extends AsyncTask<Request, Void, Object> {
    // private static final int RESPONSE_TIME_OUT = 30000;
    // private static final int REQUEST_TIME_OUT = 30000;

    private String resultString;
    private OnCompleteListener onCompleteListener;

    @Override
    protected void onPreExecute() {
	super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Request... params) {

	Object object = null;

	try {
	    // HttpClient httpClient = buildHttpClient();
	    Request request = params[0];

	    /**
	     * 获取BaseUrl
	     */
	    String urlString = Constants.URL;
	    // LogUtil.log(urlString);

	    HttpResponse httpResponse;
	    HttpGet httpGet = null;
	    HttpPost httpPost = null;

	    /**
	     * GET请求方式
	     */
	    if (RequestMethod.GET.equals(request.getServerInterfaceDefinition()
		    .getRequestMethod())) {

		StringBuffer stringBuffer = new StringBuffer(urlString
			+ request.getServerInterfaceDefinition().getOpt() + "?");
		for (Map.Entry<String, String> entry : request.getParamsMap()
			.entrySet()) {
		    stringBuffer.append(entry.getKey());
		    stringBuffer.append('=');
		    stringBuffer.append(URLEncoder.encode(entry.getValue(),
			    "UTF-8"));
		    stringBuffer.append('&');
		    LogUtil.log("参数" + entry.getKey() + "值:" + entry.getValue());

		}
		stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		LogUtil.log("GET:" + stringBuffer.toString());
		httpGet = new HttpGet(stringBuffer.toString());
		httpResponse = HttpManager.execute(httpGet);
	    } else {
		/**
		 * POST请求方式
		 */
		httpPost = new HttpPost(urlString
			+ request.getServerInterfaceDefinition().getOpt());
		ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>();
		for (Map.Entry<String, String> entry : request.getParamsMap()
			.entrySet()) {
		    localArrayList.add(new BasicNameValuePair(entry.getKey(),
			    entry.getValue()));
		    LogUtil.log("参数：" + entry.getKey() + "值："
			    + entry.getValue());
		}
		httpPost.setEntity(new UrlEncodedFormEntity(localArrayList,
			"UTF-8"));
		httpResponse = HttpManager.execute(httpPost);
	    }

	    if (httpResponse.getStatusLine().getStatusCode() != 200) {
		LogUtil.log("返回getStatusCode="
			+ httpResponse.getStatusLine().getStatusCode());
		if (null != httpPost) {
		    httpPost.abort();
		}
		if (null != httpGet) {
		    httpGet.abort();
		}
		// 将来可能添加回调
	    } else {
		resultString = EntityUtils.toString(httpResponse.getEntity(),
			"UTF-8");
		LogUtil.log("返回result=" + resultString);

		object = request.getJsonParser().parse(resultString);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return object;

    }

    @Override
    protected void onPostExecute(Object result) {
	super.onPostExecute(result);
	if (null != onCompleteListener) {
	    if (null == result) {
		onCompleteListener.onComplete(null, null);
	    } else {
		onCompleteListener.onComplete(result, resultString);
	    }
	}
    }

    public interface OnCompleteListener<T> {
	public void onComplete(T result, String resultString);
    }

    public OnCompleteListener getOnCompleteListener() {
	return onCompleteListener;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
	this.onCompleteListener = onCompleteListener;
    }
}
