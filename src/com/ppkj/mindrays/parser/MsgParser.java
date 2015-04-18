package com.ppkj.mindrays.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ppkj.mindrays.bean.MsgResponse;

public class MsgParser extends BaseParser<MsgResponse> {

    @Override
    public MsgResponse parse(String paramString) {
	MsgResponse br = new MsgResponse();
	JSONObject jo = JSON.parseObject(paramString);

	br.reCode = jo.getIntValue("reCode");
	br.reMsg = jo.getString("reMsg");
	if (jo.containsKey("head_url")) {
	    br.head_url = jo.getString("head_url");
	}
	return br;
    }

}
