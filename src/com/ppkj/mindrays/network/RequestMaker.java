package com.ppkj.mindrays.network;

import java.util.HashMap;
import java.util.Map;

import com.ppkj.mindrays.parser.AboutParser;
import com.ppkj.mindrays.parser.MsgParser;
import com.ppkj.mindrays.parser.UserParser;
import com.ppkj.mindrays.parser.VersionParser;
import com.ppkj.mindrays.secert.Base32;
import com.ppkj.mindrays.secert.MD5;

public class RequestMaker {
    private final static String AUTH_KEY = "key";
    private final static String AUTH_VALUE = "driverecord";
    private final static String AUTH_TIME = "time";
    private final static String AUTH_AUTH = "auth";
    private final static String AUTH_TOKEN = "token";
    private String time;

    /**
     * 得到JsonMaker的实例
     * 
     * @param context
     * @return
     */
    public static RequestMaker getInstance() {
	if (requestMaker == null) {
	    requestMaker = new RequestMaker();
	    return requestMaker;
	} else {
	    return requestMaker;
	}
    }

    /**
     * 生成时间要在生成这个类的时候，
     */
    private RequestMaker() {
	time = getTimeString();
    }

    private static RequestMaker requestMaker = null;

    /**
     * 生成base32加密串
     * 
     * @param p
     * @return
     */
    private String generateBase32(String... p) {
	StringBuffer encode = new StringBuffer();
	for (int i = 0; i < p.length; i++) {
	    encode.append(p[i]);
	    if (i < p.length - 1)
		encode.append(",");
	}
	return Base32.encode(encode.toString().getBytes());
    }

    /**
     * 生成md5加密串
     * 
     * @param p
     * @return
     */
    private String generateMD5(String... p) {
	StringBuffer encode = new StringBuffer();
	for (int i = 0; i < p.length; i++) {
	    encode.append(p[i]);

	}

	return MD5.GetMD5Code(encode.toString());
    }

    private String getTimeString() {
	return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 登录接口
     * 
     * @param uname
     * @param pwd
     * @return
     */
    public Request getLogin(String uname, String pwd) {
	UserParser jsonParser = new UserParser();
	Map<String, String> paramsMap = new HashMap<String, String>();

	paramsMap.put(AUTH_KEY, AUTH_VALUE);
	paramsMap.put(AUTH_TIME, time);
	paramsMap.put("uname", uname);
	paramsMap.put("pwd", pwd);
	paramsMap.put(AUTH_AUTH,
		generateBase32(AUTH_KEY, AUTH_TIME, "uname", "pwd"));
	paramsMap.put(AUTH_TOKEN, generateMD5(AUTH_VALUE, time, uname, pwd));
	Request mLoginRequest = new Request(
		ServerInterfaceDefinition.OPT_LOGIN, paramsMap, jsonParser);
	return mLoginRequest;
    }

    public Request getLoginOther(String other_id, String other_type,
	    String nickName, String access_secret, String access_key,
	    String expires_in) {
	UserParser jsonParser = new UserParser();
	Map<String, String> paramsMap = new HashMap<String, String>();

	paramsMap.put(AUTH_KEY, AUTH_VALUE);
	paramsMap.put(AUTH_TIME, time);
	paramsMap.put("other_id", other_id);
	paramsMap.put("other_type", other_type);
	paramsMap.put("nickName", nickName);
	if (access_secret != null) {
	    paramsMap.put("access_secret", access_secret);
	    paramsMap.put("access_key", access_key);
	    paramsMap.put("expires_in", expires_in);
	    paramsMap.put(
		    AUTH_AUTH,
		    generateBase32(AUTH_KEY, AUTH_TIME, "other_id",
			    "other_type", "nickName", "access_secret",
			    "access_key", "expires_in"));
	    paramsMap.put(
		    AUTH_TOKEN,
		    generateMD5(AUTH_VALUE, time, other_id, other_type,
			    nickName, access_secret, access_key, expires_in));
	} else {
	    paramsMap.put(
		    AUTH_AUTH,
		    generateBase32(AUTH_KEY, AUTH_TIME, "other_id",
			    "other_type", "nickName"));
	    paramsMap.put(
		    AUTH_TOKEN,
		    generateMD5(AUTH_VALUE, time, other_id, other_type,
			    nickName));
	}

	Request mLoginRequest = new Request(
		ServerInterfaceDefinition.OPT_LOGIN2, paramsMap, jsonParser);
	return mLoginRequest;

    }

    /**
     * 注册接口
     * 
     * @param uname
     * @param pwd
     * @param confpwd
     * @return
     */
    public Request getReg(String uname, String pwd, String confpwd) {
	UserParser jsonParser = new UserParser();
	Map<String, String> paramsMap = new HashMap<String, String>();

	paramsMap.put(AUTH_KEY, AUTH_VALUE);
	paramsMap.put(AUTH_TIME, time);
	paramsMap.put("uname", uname);
	paramsMap.put("pwd", pwd);
	paramsMap.put("confpwd", confpwd);
	paramsMap.put(AUTH_AUTH,
		generateBase32(AUTH_KEY, AUTH_TIME, "uname", "pwd", "confpwd"));
	paramsMap.put(AUTH_TOKEN,
		generateMD5(AUTH_VALUE, time, uname, pwd, confpwd));
	Request mLoginRequest = new Request(ServerInterfaceDefinition.OPT_REG,
		paramsMap, jsonParser);
	return mLoginRequest;
    }

    /**
     * 找回密码接口
     * 
     * @param uname
     * @return
     */
    public Request getRePwd(String uname) {
	MsgParser jsonParser = new MsgParser();
	Map<String, String> paramsMap = new HashMap<String, String>();

	paramsMap.put(AUTH_KEY, AUTH_VALUE);
	paramsMap.put(AUTH_TIME, time);
	paramsMap.put("uname", uname);
	paramsMap.put(AUTH_AUTH, generateBase32(AUTH_KEY, AUTH_TIME, "uname"));
	paramsMap.put(AUTH_TOKEN, generateMD5(AUTH_VALUE, time, uname));
	Request mLoginRequest = new Request(
		ServerInterfaceDefinition.OPT_FORGETPWD, paramsMap, jsonParser);
	return mLoginRequest;
    }

    /**
     * 更新个人资料
     * 
     * @param user_id
     * @param nickName
     * @param gender
     * @param birthday
     * @param local
     * @param user_token
     * @return
     */
    public Request getUpdate(String user_id, String nickName, String gender,
	    String birthday, String local, String user_token) {
	UserParser jsonParser = new UserParser();
	Map<String, String> paramsMap = new HashMap<String, String>();

	paramsMap.put(AUTH_KEY, AUTH_VALUE);
	paramsMap.put(AUTH_TIME, time);
	paramsMap.put("user_id", user_id);
	paramsMap.put("nickName", nickName);
	paramsMap.put("gender", gender);
	paramsMap.put("birthday", birthday);
	paramsMap.put("local", local);
	paramsMap.put("user_token", user_token);
	paramsMap.put(
		AUTH_AUTH,
		generateBase32(AUTH_KEY, AUTH_TIME, "user_id", "nickName",
			"gender", "birthday", "local", "user_token"));
	paramsMap.put(
		AUTH_TOKEN,
		generateMD5(AUTH_VALUE, time, user_id, nickName, gender,
			birthday, local, user_token));
	Request mLoginRequest = new Request(
		ServerInterfaceDefinition.OPT_UPDATE, paramsMap, jsonParser);
	return mLoginRequest;
    }

    /**
     * 查看个人资料
     * 
     * @param user_id
     * @param user_token
     * @return
     */
    public Request getUserInfo(String user_id, String user_token) {
	UserParser jsonParser = new UserParser();
	Map<String, String> paramsMap = new HashMap<String, String>();

	paramsMap.put(AUTH_KEY, AUTH_VALUE);
	paramsMap.put(AUTH_TIME, time);
	paramsMap.put("user_id", user_id);
	paramsMap.put("user_token", user_token);
	paramsMap.put(AUTH_AUTH,
		generateBase32(AUTH_KEY, AUTH_TIME, "user_id", "user_token"));
	paramsMap.put(AUTH_TOKEN,
		generateMD5(AUTH_VALUE, time, user_id, user_token));
	Request mLoginRequest = new Request(
		ServerInterfaceDefinition.OPT_GETUSERINFO, paramsMap,
		jsonParser);
	return mLoginRequest;
    }

    /**
     * 上传头像
     * 
     * @param user_id
     * @param user_token
     * @return
     */
    public Request getUploadHead(String user_id, String user_token) {
	MsgParser jsonParser = new MsgParser();
	Map<String, String> paramsMap = new HashMap<String, String>();

	paramsMap.put(AUTH_KEY, AUTH_VALUE);
	paramsMap.put(AUTH_TIME, time);
	paramsMap.put("user_id", user_id);
	paramsMap.put("user_token", user_token);
	paramsMap.put(AUTH_AUTH,
		generateBase32(AUTH_KEY, AUTH_TIME, "user_id", "user_token"));
	paramsMap.put(AUTH_TOKEN,
		generateMD5(AUTH_VALUE, time, user_id, user_token));
	Request mLoginRequest = new Request(
		ServerInterfaceDefinition.OPT_UPLOADHEAD, paramsMap, jsonParser);
	return mLoginRequest;
    }

    /**
     * 查看关于
     * 
     * @param user_id
     * @param user_token
     * @return
     */
    public Request getAbout() {
	AboutParser jsonParser = new AboutParser();
	Map<String, String> paramsMap = new HashMap<String, String>();

	paramsMap.put(AUTH_KEY, AUTH_VALUE);
	paramsMap.put(AUTH_TIME, time);
	paramsMap.put("type", "0");
	paramsMap.put(AUTH_AUTH, generateBase32(AUTH_KEY, AUTH_TIME, "type"));
	paramsMap.put(AUTH_TOKEN, generateMD5(AUTH_VALUE, time, "0"));
	Request mLoginRequest = new Request(
		ServerInterfaceDefinition.OPT_ABOUT, paramsMap, jsonParser);
	return mLoginRequest;
    }

    /**
     * 检查版本更新
     * 
     * @param user_id
     * @param version
     * @param platform
     * @return
     */
    public Request getVersion(/* String user_id, */String version,
	    String resolution, String packagename, String platform) {
	VersionParser jsonParser = new VersionParser();
	Map<String, String> paramsMap = new HashMap<String, String>();

	paramsMap.put(AUTH_KEY, AUTH_VALUE);
	paramsMap.put(AUTH_TIME, "9");
	// paramsMap.put("user_id", user_id);
	paramsMap.put("resolution", resolution);
	paramsMap.put("packagename", packagename);
	paramsMap.put("version", version);
	paramsMap.put("platform", platform);
	paramsMap.put(
		AUTH_AUTH,
		generateBase32(AUTH_KEY, AUTH_TIME, /* "user_id", */"version",
			"resolution", "packagename", "platform"));
	paramsMap.put(
		AUTH_TOKEN,
		generateMD5(AUTH_VALUE, "9",/* user_id, */version, resolution,
			packagename, platform));
	Request mLoginRequest = new Request(
		ServerInterfaceDefinition.OPT_VERSION, paramsMap, jsonParser);
	return mLoginRequest;
    }

    /**
     * 发送意见反馈
     * 
     * @param user_id
     * @param uid
     * @param platform
     * @param model
     * @param device_version
     * @param app_version
     * @param content
     * @return
     */
    public Request getFeedback(String user_id, String uid, String platform,
	    String model, String device_version, String app_version,
	    String content) {
	MsgParser jsonParser = new MsgParser();
	Map<String, String> paramsMap = new HashMap<String, String>();

	paramsMap.put(AUTH_KEY, AUTH_VALUE);
	paramsMap.put(AUTH_TIME, time);
	paramsMap.put("user_id", user_id);
	paramsMap.put("uid", uid);
	paramsMap.put("platform", platform);
	paramsMap.put("model", model);
	paramsMap.put("device_version", device_version);
	paramsMap.put("app_version", app_version);
	paramsMap.put("content", content);
	paramsMap.put(
		AUTH_AUTH,
		generateBase32(AUTH_KEY, AUTH_TIME, "user_id", "uid",
			"platform", "model", "device_version", "app_version",
			"content"));
	paramsMap.put(
		AUTH_TOKEN,
		generateMD5(AUTH_VALUE, time, user_id, uid, platform, model,
			device_version, app_version, content));
	Request mLoginRequest = new Request(
		ServerInterfaceDefinition.OPT_FEEDBACK, paramsMap, jsonParser);
	return mLoginRequest;
    }

}
