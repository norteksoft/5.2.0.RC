package org.jasig.cas.util;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;


public class PropUtils {

	private static final String DEFAULT_PROP_FILE = "cas.properties";
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
			propert.load(PropUtils.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return propert.getProperty(propName);
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

}
