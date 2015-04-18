package com.ppkj.mindrays.network;

public enum ServerInterfaceDefinition {

	OPT_LOGIN("/api/user/login.do"),

	OPT_LOGIN2("/api/user/login2.do"),

	OPT_REG("/api/user/register.do"),

	OPT_FORGETPWD("/api/user/forgetpwd.do"),

	OPT_UPLOADHEAD("/api/user/uploadHead.do"),

	OPT_UPDATE("/api/user/updateInfo.do"),

	OPT_GETUSERINFO("/api/user/getUserInfo.do"),

	OPT_FEEDBACK("/api/client/feedback.do"),

	OPT_VERSION("/api/client/checkversion.do"),

	OPT_ABOUT("/api/article/aboutUS.do"),

	;

	private String opt;
	private RequestMethod requestMethod = RequestMethod.POST;
	private int retryNumber = 1;

	private ServerInterfaceDefinition(String opt) {
		this.opt = opt;
	}

	private ServerInterfaceDefinition(String opt, RequestMethod requestMethod) {
		this.opt = opt;
		this.requestMethod = requestMethod;
	}

	private ServerInterfaceDefinition(String opt, RequestMethod requestMethod,
			int retryNumber) {
		this.opt = opt;
		this.requestMethod = requestMethod;
		this.retryNumber = retryNumber;
	}

	public String getOpt() {
		return opt;
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	public int getRetryNumber() {
		return retryNumber;
	}

	public enum RequestMethod {
		POST("POST"), GET("GET");
		private String requestMethodName;

		RequestMethod(String requestMethodName) {
			this.requestMethodName = requestMethodName;
		}

		public String getRequestMethodName() {
			return requestMethodName;
		}
	}
}
