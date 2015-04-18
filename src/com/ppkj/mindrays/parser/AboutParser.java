package com.ppkj.mindrays.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ppkj.mindrays.bean.AboutResponse;

public class AboutParser extends BaseParser<AboutResponse> {

	@Override
	public AboutResponse parse(String paramString) {
		AboutResponse ar = new AboutResponse();
		JSONObject jo = JSON.parseObject(paramString);

		ar.reCode = jo.getIntValue("reCode");
		ar.reMsg = jo.getString("reMsg");
		JSONObject userjson = jo.getJSONObject("entity");
		ar.mAboutBean.setEntity_desc(userjson.getString("entity_desc"));
		ar.mAboutBean.setEntity_id(userjson.getString("entity_id"));
		ar.mAboutBean.setEntity_title(userjson.getString("entity_title"));

		return ar;
	}

}
