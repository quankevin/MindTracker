package com.ppkj.mindrays.bean;

import java.io.Serializable;

public abstract class BaseResponse implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 5375804597574885028L;
    public int reCode = -1;
    public String reMsg;
}
