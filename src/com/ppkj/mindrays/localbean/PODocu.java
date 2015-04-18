package com.ppkj.mindrays.localbean;

import java.io.File;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.ppkj.mindrays.utils.FileUtils;

@Table(name = "PODocu")
// 建议加上注解， 混淆后表名不受影响
public class PODocu extends EntityBase {

    /** 标题 */
    @Column(column = "title")
    public String title;
    /** 标题拼音 */
    @Column(column = "title_key")
    public String title_key;
    /** 路径 */
    @Column(column = "path")
    public String path;
    /** 最后一次访问时间 */
    @Column(column = "last_access_time")
    public long last_access_time;
    /** 最后一次修改时间 */
    @Column(column = "last_modify_time")
    public long last_modify_time;
    /** 缩略图路径 */
    @Column(column = "thumb_path")
    public String thumb_path;
    /** 文件大小 */
    @Column(column = "file_size")
    public long file_size;
    /** 文件类型 */
    @Column(column = "file_type")
    public String file_type;
    /** 网络是否存在 */
    @Column(column = "isNet")
    public boolean isNet = false;

    /**
     * 0文件，1视频，2图片
     */
    @Column(column = "type")
    public int type = 0;

    public PODocu() {

    }

    public PODocu(File f) {
	title = f.getName();
	path = f.getAbsolutePath();
	last_modify_time = f.lastModified();
	file_size = f.length();
	if (FileUtils.isDocument(f)) {
	    this.type = 0;
	} else if (FileUtils.isVideo(f)) {
	    this.type = 1;
	} else if (FileUtils.isImage(f)) {
	    this.type = 2;
	}
	file_type = FileUtils.getFileExtension(f);

    }

    public PODocu(String path2, String mimeType) {
	new PODocu(new File(path2));

    }

}
