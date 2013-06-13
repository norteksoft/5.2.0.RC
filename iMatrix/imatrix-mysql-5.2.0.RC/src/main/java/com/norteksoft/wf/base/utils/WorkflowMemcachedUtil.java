package com.norteksoft.wf.base.utils;


import com.norteksoft.product.util.MemCachedUtils;

public class WorkflowMemcachedUtil {
	public static String getDefinitionFile(String processId){
		return (String)MemCachedUtils.get(processId);
	}
	public static Object get(String key){
		return MemCachedUtils.get(key);
	}

}
