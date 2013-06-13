package com.norteksoft.product.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

public class PropUtils {

	private static Log log = LogFactory.getLog(PropUtils.class);
	private static final String DEFAULT_PROP_FILE = "application.properties";
	public static final String LOG_METHOD_BEGIN="底层日志方法开始***";
	public static final String LOG_METHOD_END="底层日志方法结束***";
	public static final String LOG_CONTENT="底层日志***";
	public static final String LOG_FLAG="***";
	public static final String DATABASE_ORACLE="oracle";
	public static final String DATABASE_MYSQL="mysql";
	public static final String DATABASE_SQLSERVER="sqlserver";
	
	public static String getProp(String key){
		Properties propert = new Properties();
		try {
			propert.load(PropUtils.class.getClassLoader().getResourceAsStream(DEFAULT_PROP_FILE));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return propert.getProperty(key);
	}
	
	public static String getProp(String fileName, String propName){
		Properties propert = new Properties();
		try {
			InputStream ins = PropUtils.class.getClassLoader().getResourceAsStream(fileName);
			if(ins!=null){
				propert.load(ins);
			}
		} catch (IOException e) {
			log.debug(getExceptionInfo(e));
		}
		return propert.getProperty(propName);
	}
	
	public static String getExceptionInfo(Exception e){
		StringBuilder sb=new StringBuilder();
		sb.append(e);
        StackTraceElement[] trace = e.getStackTrace();
        for (int i=0; i < trace.length; i++)
        	sb.append("\n\t " + trace[i]);
        return sb.toString();
	}
	public static boolean isOracle(){
		boolean isOracle=true;
		//数据库方言
		String databaseDialect=PropUtils.getProp("hibernate.dialect");
		if("org.hibernate.dialect.MySQLDialect".equals(databaseDialect)){
			isOracle=false;
		}
		return isOracle;
	}
	/**
	 * 获得数据库类型
	 * @return
	 */
	public static String getDataBase(){
		//数据库连接地址
		String databaseLink=PropUtils.getProp("hibernate.connection.url");
		if(databaseLink.startsWith("jdbc:oracle")){//oracle
			return DATABASE_ORACLE;
		}else if(databaseLink.startsWith("jdbc:sqlserver")){//sqlserver
			return DATABASE_SQLSERVER;
		}else{//mysql
			return DATABASE_MYSQL;
		}
	}
	/**
	 * 是否是底层平台
	 * @param url
	 * @return
	 */
	public static boolean isBasicSystem(String url){
		return (url.endsWith("/acs")||url.endsWith("/acs/"))||
		(url.endsWith("/mms")||url.endsWith("/mms/"))||
		(url.endsWith("/wf")||url.endsWith("/wf/"))||
		(url.endsWith("/bs")||url.endsWith("/bs/"))||
		(url.endsWith("/task")||url.endsWith("/task/"))||
		(url.endsWith("/portal")||url.endsWith("/portal/"));
	}
	
	public static Set<Object> getPropertyKeys(String fileName){
		Properties propert = new Properties();
		try {
			propert.load(PropUtils.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Set<Object> keys=propert.keySet();
		return keys;
	}
	
	 /**
	 * 显示图片
	 * @param file
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public static String showPic(File file) throws IOException{
		BufferedInputStream bis =null;
		OutputStream out=null;
		try {
			FileInputStream fileinput =new FileInputStream(file);
			bis = new BufferedInputStream(fileinput);
			HttpServletResponse response = ServletActionContext.getResponse();
			response.reset();
			response.setContentType("image/jpeg;charset=UTF-8");

			response.addHeader("Content-Transfer-Encoding","base64");
			out = response.getOutputStream();
			byte[] buffer = new byte[4096];
			int size = 0;
			while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("显示图片出错");
		} catch (IOException e) {
			throw new RuntimeException("显示图片出错");
		}finally{
			if(out!=null){
				out.close();
				bis.close();
			}
		}
		return null;
	}

}
