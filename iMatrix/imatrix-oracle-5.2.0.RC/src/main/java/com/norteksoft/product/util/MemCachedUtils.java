package com.norteksoft.product.util;
import java.util.Map;
import java.util.Map.Entry;

import com.danga.MemCached.MemCachedClient;

/**
 * MemCached 工具类
 * @author xiao j
 */
public class MemCachedUtils {

	private MemCachedUtils(){}
	
	public static MemCachedClient getMemCachedClient(){
		return (MemCachedClient) ContextUtils.getBean("memcachedClient");
	}
	
	/**
	 * 向缓存中添加信息
	 * @param key
	 * @param value
	 * @return 是否添加成功
	 */
	public static boolean add(String key, Object value){
		return getMemCachedClient().set(key, value);
	}
	
	/**
	 * 批量添加信息
	 * @param map
	 */
	public static void add(Map<String, Object> map){
		for(Entry<String, Object> keyValue : map.entrySet()){
			add(keyValue.getKey(), keyValue.getValue());
		}
	}
	
	/**
	 * 从缓存中获取信息
	 * @param key
	 * @return
	 */
	public static Object get(String key){
		MemCachedClient client = getMemCachedClient();
		Object obj =  client.get(key);
		return obj;
	}
	/**
	 * 删除缓存信息
	 * @param key
	 * @return
	 */
	public static boolean delete(String key){
		return getMemCachedClient().delete(key);
	}
}
