package com.norteksoft.product.api;

import java.io.File;

/**
 * 通用导入api
 * @author Administrator
 *
 */
public interface DataImporterService {
	/**
	 * 导入文件
	 * 
	 */
	public String importData(File file,String fileName)throws Exception;
	
	public String importData(File file, String fileName, DataImporterCallBack callBack)throws Exception;
}
