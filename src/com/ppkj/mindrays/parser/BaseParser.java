package com.ppkj.mindrays.parser;

import com.ppkj.mindrays.bean.BaseResponse;

public abstract class BaseParser<T extends BaseResponse> {
    public static final String ERROR_CODE = "code";
    public static final String MSG = "msg";

    public abstract T parse(String paramString);
}
