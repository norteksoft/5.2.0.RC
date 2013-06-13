package com.norteksoft.acs.service.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.dao.DataRetrievalFailureException;

import com.norteksoft.product.util.AuthFunction;
import com.norteksoft.product.util.MemCachedUtils;

/**
 * 权限资源信息缓存
 * 
 * @author xiaoj
 */
public class SecurityResourceCache {
	private static int MAX_ELEMENTS_IN_MEMORY = 500;
	private static int TIME_TO_LIVE_SECONDS = 40;
	private static int TIME_TO_IDLE_SECONDS = 30;
	

	@SuppressWarnings("unchecked")
	
	public synchronized static String getAuthoritysInCache(String funcPath){
		AuthFunction autoAuth=(AuthFunction)MemCachedUtils.get(String.valueOf(funcPath.hashCode()));
		if(autoAuth == null){
			return null;
		}
		return autoAuth.getFunctionId();
	}
}
