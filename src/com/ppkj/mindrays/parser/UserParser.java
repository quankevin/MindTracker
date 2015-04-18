package com.ppkj.mindrays.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ppkj.mindrays.bean.UserBean;
import com.ppkj.mindrays.bean.UserResponse;

public class UserParser extends BaseParser<UserResponse> {

	@Override
	public UserResponse parse(String paramString) {
		UserResponse lr = new UserResponse();
		JSONObject jo = JSON.parseObject(paramString);

		lr.reCode = jo.getIntValue("reCode");
		lr.reMsg = jo.getString("reMsg");
		JSONObject userjson = jo.getJSONObject("userInfo");
		if (userjson != null) {
			lr.userinfo = new UserBean();
			lr.userinfo.setUser_name(userjson.getString("user_name"));
			lr.userinfo.setUser_id(userjson.getString("user_id"));
			lr.userinfo.setBirthday(userjson.getString("birthday"));
			lr.userinfo.setGender(userjson.getString("gender"));
			lr.userinfo.setHead_url(userjson.getString("head_url"));
			lr.userinfo.setUser_token(userjson.getString("user_token"));
			lr.userinfo.setLocal(userjson.getString("local"));
			lr.userinfo.setNickname(userjson.getString("nickname"));
		}
		return lr;
	}

}
