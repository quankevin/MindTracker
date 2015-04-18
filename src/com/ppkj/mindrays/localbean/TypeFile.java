package com.ppkj.mindrays.localbean;

import java.io.File;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.ppkj.mindrays.utils.FileUtils;

@Table(name = "TypeFile")
// 建议加上注解， 混淆后表名不受影响
public class TypeFile extends EntityBase {

	/** 标题 */
	@Column(column = "title")
	private String title;

	/** 最后一次访问时间 */
	@Column(column = "last_modify_time")
	private long last_modify_time;
	/** 文件内个数 */
	@Column(column = "fileCount")
	private int fileCount;
	/** 文件类型 */
	@Column(column = "file_type")
	private String file_type;

	public TypeFile() {

	}

	public TypeFile(File f) {
		title = f.getName();
		file_type = FileUtils.getFileExtension(f);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getLast_access_time() {
		return last_modify_time;
	}

	public void setLast_access_time(long last_modify_time) {
		this.last_modify_time = last_modify_time;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public String getFile_type() {
		return file_type;
	}

	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}

}
