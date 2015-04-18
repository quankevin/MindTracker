package com.ppkj.mindrays.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

import com.ppkj.mindrays.utils.LogUtil;

public class FtpUtil {

    private static final String EXCEPTION_NAME = "ftputil";

    /**
     * Description: 向FTP申请路径
     * 
     * @param url
     *            FTP服务器hostname
     * @param port
     *            FTP服务器端口，如果默认端口请写-1
     * @param username
     *            FTP登录账号
     * @param password
     *            FTP登录密码
     * @param path
     *            FTP服务器保存目录
     * @param filename
     *            上传到FTP服务器上的文件名
     * @param input
     *            输入流
     * @return 成功返回true，否则返回false
     */
    public static FTPFile[] listFile(String url, int port, String username,
	    String password, String path) {
	LogUtil.log("path" + path);
	FTPFile[] remoteFiles = null;
	FTPClient ftp = new FTPClient();
	ftp.setControlEncoding("GBK");
	try {
	    int reply;

	    // 连接FTP服务器
	    if (port > -1) {
		ftp.connect(url, port);
	    } else {
		ftp.connect(url);
	    }
	    ftp.enterLocalPassiveMode();
	    // 登录FTP
	    ftp.login(username, password);
	    reply = ftp.getReplyCode();
	    if (!FTPReply.isPositiveCompletion(reply)) {
		ftp.disconnect();
		return null;
	    }
	    ftp.changeWorkingDirectory(path);
	    remoteFiles = ftp.listFiles(path);

	    ftp.logout();
	} catch (IOException e) {
	    Log.e(EXCEPTION_NAME, "", e);
	} finally {
	    if (ftp.isConnected()) {
		try {
		    ftp.disconnect();
		} catch (IOException e) {
		    Log.e(EXCEPTION_NAME, "", e);
		}
	    }
	}
	return remoteFiles;
    }

    /**
     * Description: 向FTP服务器上传文件
     * 
     * @param url
     *            FTP服务器hostname
     * @param port
     *            FTP服务器端口，如果默认端口请写-1
     * @param username
     *            FTP登录账号
     * @param password
     *            FTP登录密码
     * @param path
     *            FTP服务器保存目录
     * @param filename
     *            上传到FTP服务器上的文件名
     * @param input
     *            输入流
     * @return 成功返回true，否则返回false
     */
    public static String uploadFile(String url, int port, String username,
	    String password, String path, File inputFile) {
	String filepath = null;
	FTPClient ftp = new FTPClient();
	ftp.setControlEncoding("GBK");

	try {
	    InputStream input = new FileInputStream(inputFile);
	    int reply;

	    // 连接FTP服务器
	    if (port > -1) {
		ftp.connect(url, port);
	    } else {
		ftp.connect(url);
	    }
	    ftp.enterLocalPassiveMode();
	    // 登录FTP
	    ftp.login(username, password);
	    reply = ftp.getReplyCode();
	    if (!FTPReply.isPositiveCompletion(reply)) {
		ftp.disconnect();
		return null;
	    }
	    ftp.changeWorkingDirectory(path);
	    ftp.storeFile(inputFile.getName(), input);

	    input.close();
	    ftp.logout();
	    filepath = inputFile.getAbsolutePath();
	} catch (IOException e) {
	    filepath = null;
	    Log.e(EXCEPTION_NAME, "", e);
	} catch (Exception e) {

	} finally {
	    if (ftp.isConnected()) {
		try {
		    ftp.disconnect();
		} catch (IOException e) {
		    Log.e(EXCEPTION_NAME, "", e);
		}
	    }
	}
	return filepath;
    }

    /**
     * Description: 向FTP服务器创建文件夹
     * 
     * @param url
     *            FTP服务器hostname
     * @param port
     *            FTP服务器端口，如果默认端口请写-1
     * @param username
     *            FTP登录账号
     * @param password
     *            FTP登录密码
     * @param path
     *            FTP服务器保存目录
     * @param filename
     *            上传到FTP服务器上的文件名
     * @param input
     *            输入流
     * @return 成功返回true，否则返回false
     */
    public static String mkDir(String url, int port, String username,
	    String password, String filePath) {

	FTPClient ftp = new FTPClient();
	ftp.setControlEncoding("GBK");

	try {

	    int reply;

	    // 连接FTP服务器
	    if (port > -1) {
		ftp.connect(url, port);
	    } else {
		ftp.connect(url);
	    }
	    ftp.enterLocalPassiveMode();
	    // 登录FTP
	    ftp.login(username, password);
	    reply = ftp.getReplyCode();
	    if (!FTPReply.isPositiveCompletion(reply)) {
		ftp.disconnect();
		return null;
	    }
	    ftp.changeWorkingDirectory("");
	    ftp.mkd(filePath);

	    ftp.logout();

	} catch (IOException e) {
	    filePath = null;

	} catch (Exception e) {

	} finally {
	    if (ftp.isConnected()) {
		try {
		    ftp.disconnect();
		} catch (IOException e) {
		    Log.e(EXCEPTION_NAME, "", e);
		}
	    }
	}
	return filePath;
    }

    /**
     * Description: 从FTP服务器下载文件
     * 
     * @Version1.0
     * @param url
     *            FTP服务器hostname
     * @param port
     *            FTP服务器端口
     * @param username
     *            FTP登录账号
     * @param password
     *            FTP登录密码
     * @param remotePath
     *            FTP服务器上的相对路径
     * @param fileName
     *            要下载的文件名
     * @param localPath
     *            下载后保存到本地的路径
     * @return
     */
    public static File downloadFile(String url, int port, String username,
	    String password, String remotePath, String fileName,
	    String localPath) {
	FTPClient ftp = new FTPClient();
	ftp.setControlEncoding("GBK");
	try {
	    int reply;

	    // 连接FTP服务器
	    if (port > -1) {
		ftp.connect(url, port);
	    } else {
		ftp.connect(url);
	    }
	    ftp.enterLocalPassiveMode();
	    ftp.login(username, password);// 登录
	    reply = ftp.getReplyCode();
	    if (!FTPReply.isPositiveCompletion(reply)) {
		ftp.disconnect();
	    }
	    ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
	    FTPFile[] fs = ftp.listFiles();
	    for (FTPFile ff : fs) {
		if (ff.getName().equals(fileName)) {

		    File localFile = new File(localPath + "/" + ff.getName());
		    if (!localFile.exists()) {
			if (!localFile.getParentFile().exists()) {
			    localFile.getParentFile().mkdirs();
			}
			OutputStream is = new FileOutputStream(localFile);
			ftp.retrieveFile(ff.getName(), is);
			is.close();
		    }
		    return localFile;
		}
	    }

	    ftp.logout();

	} catch (IOException e) {
	    Log.e(EXCEPTION_NAME, "", e);
	} finally {
	    if (ftp.isConnected()) {
		try {
		    ftp.disconnect();
		} catch (IOException e) {
		    Log.e(EXCEPTION_NAME, "", e);
		}
	    }
	}
	return null;
    }

}
