package com.ppkj.mindrays.uploadimage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * 上传文件
 */
public class FormFile {
	public String inputStreamKey;
	public InputStream inputStreamValue;
	public List<InputStream> inputStreamValueList;
	public String fileName;

	public FormFile(String inputStreamKey, String filePath, String fileName) {
		this.fileName = fileName;
		try {
			this.inputStreamValue = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// this.inputStreamValue = inStream;
		this.inputStreamKey = inputStreamKey;
	}

	public FormFile(String inputStreamKey,
			List<InputStream> inputStreamValueList, String fileName) {
		this.fileName = fileName;
		this.inputStreamValueList = inputStreamValueList;
		this.inputStreamKey = inputStreamKey;
	}

}
