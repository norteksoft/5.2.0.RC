package com.norteksoft.mms.base;

import org.apache.commons.lang.StringUtils;


public class GenerateCodeUtils {
	/**
	 * 将字符串第一个字母大写
	 * @param name
	 * @return
	 */
	public static String firstCharUpperCase(String name){
		if(StringUtils.isNotEmpty(name)){
			return name.substring(0,1).toUpperCase()+name.substring(1,name.length());
		}
		return null;
	}
	/**
	 * 将字符串第一个字母小写
	 * @param name
	 * @return
	 */
	public static String firstCharLowerCase(String name){
		if(StringUtils.isNotEmpty(name)){
			return name.substring(0,1).toLowerCase()+name.substring(1,name.length());
		}
		return null;
	}
	/**
	 * 传一个java类package路径如com.norteksoft.mms.Report,返回一个文件夹路径如：com/norteksoft/mms
	 * @param classPath
	 * @return
	 */
	public static String getExportPath(String classPath){
		if(StringUtils.isNotEmpty(classPath)){
			String[] array = classPath.substring(0,classPath.lastIndexOf(".")).split("\\.");
			String path = "";
			for(int i=0;i<array.length;i++){
				path+=array[i]+"/";
			}
			return path.substring(0, path.length()-1);
		}
		return null;
	}
	/**
	 * 取得实体路径的外层路径
	 * @param classPath
	 * @return
	 */
	public static String getLastLayerPath(String classPath){
		if(StringUtils.isNotEmpty(classPath)){
		  String entityPath=classPath.substring(0,classPath.lastIndexOf("."));
			return entityPath.substring(0,entityPath.lastIndexOf("."));
		}
		return null;
	}
}
