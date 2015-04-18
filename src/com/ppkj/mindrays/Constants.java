package com.ppkj.mindrays;

/**
 * 常量配置类
 * 
 * @author chenxuquan
 * 
 */
public class Constants {
    // ////////配置的url地址
    /**
     * 配置的url地址
     */
    public static final String URL = "http://42.120.17.134:8082/DriveRecord";
    // //////////
    public static final String FTP_URL = "42.120.17.134";

    public static final String FTP_PORT = "21";

    /**
     * 视频格式
     */
    public static final String[] VIDEO_EXTENSIONS = { "mp4", "avi", "rm",
	    "rmvb", "3gp", "wmv", "ts", "mkv", };
    /**
     * 图片格式
     */
    public static final String[] IMAGE_EXTENSIONS = { "jpg", "jpeg", "bmp",
	    "png", };

    public static final String[] CLOUD_EXTENSIONS = { "doc", "docx", "xls",
	    "xlsx", "ppt", "pdf", "txt" };
    // ///////////////////////////////
    /**
     * QQ互联（空间）
     */
    public static final String QQ_APPID = "222222";

    /**
     * 新浪微博
     */
    public static final String SINAWEIBO_USER_APPKEY = "3185577437";// 新浪appkey
    public static final String SINAWEIBO_USER_REDIRECTURL = "http://www.sina.com";// 新浪回调地址
    public static final String SINAWEIBO_USER_SCOPE = "email,direct_messages_read,direct_messages_write,"
	    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
	    + "follow_app_official_microblog," + "invitation_write";

    /**
     * 微信
     */
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String WX_APP_ID = "wxc76d20d83a5dd974";
}
