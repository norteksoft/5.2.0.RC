package com.norteksoft.product.api;

import java.io.File;
import java.io.OutputStream;

/**
 * 文件的api
 * 
 *
 */
public interface FileService {
	/**
	 * 保存文件
	 * 
	 */
	public String saveFile(File file);
	/**
	 * 保存文件
	 * 
	 */
	public String saveFile(byte[] file);
	/**
	 * 获得文件
	 * 
	 */
	public byte[] getFile(String filePath);
	/**
	 * 获得文件
	 * 
	 */
	public void writeTo(String filePath,OutputStream out);
	/**
	 * 删除文件
	 * @param filePath
	 */
	public void deleteFile(String filePath);
}
