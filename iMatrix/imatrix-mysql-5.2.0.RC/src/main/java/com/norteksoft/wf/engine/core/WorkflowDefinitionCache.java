package com.norteksoft.wf.engine.core;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.dao.DataRetrievalFailureException;

/**
 * 工作流定义缓存
 * @author xiaoj
 */
public class WorkflowDefinitionCache {
	private static Cache cache;
	private static int MAX_ELEMENTS_IN_MEMORY = 500;
	private static int TIME_TO_LIVE_SECONDS = 40;
	private static int TIME_TO_IDLE_SECONDS = 30;
	
	static{
		if(cache==null){
			CacheManager cacheManager = CacheManager.getInstance();
			cache = new Cache("workflow_definitions", MAX_ELEMENTS_IN_MEMORY, 
					true, true, TIME_TO_LIVE_SECONDS, TIME_TO_IDLE_SECONDS);
			cacheManager.addCache(cache);
		}
	}
	
	public synchronized static void putCache(String processId, String document){
		Element element = new Element(processId,document);
		cache.put(element);
	}

	public synchronized static void removeCache(String processId){
		cache.remove(processId);
	}

	public synchronized static String getCache(String processId){
		Element element = null;
		try {
			element = cache.get(processId);
		} catch (CacheException cacheException) {
			throw new DataRetrievalFailureException("workflow definitions failure: " + cacheException.getMessage(), cacheException);
		}
		if (element == null) {
			return null;
		} else {
			return (String)element.getValue();
		}
	}
	
}
