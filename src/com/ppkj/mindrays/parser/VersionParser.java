package com.ppkj.mindrays.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ppkj.mindrays.bean.VersionResponse;

public class VersionParser extends BaseParser<VersionResponse> {

	@Override
	public VersionResponse parse(String paramString) {
		VersionResponse vr = new VersionResponse();
		JSONObject jo = JSON.parseObject(paramString);

		vr.reCode = jo.getIntValue("reCode");
		vr.reMsg = jo.getString("reMsg");
		if (vr.reCode == 0) {
			JSONObject userjson = jo.getJSONObject("entity");
			if (userjson.containsKey("app_url")) {

				vr.versionBean.setApp_url(userjson.getString("app_url"));
			}
			if (userjson.containsKey("content"))
				vr.versionBean.setContent(userjson.getString("content"));
			if (userjson.containsKey("updatetype"))
				vr.versionBean.setUpdatetype(userjson.getString("updatetype"));
			if (userjson.containsKey("version"))
				vr.versionBean.setVersion(userjson.getString("version"));
			vr.versionBean.setShowflag("0");
		} else {
			vr.versionBean.setShowflag(jo.getString("showflag"));
		}
		return vr;
	}

}
