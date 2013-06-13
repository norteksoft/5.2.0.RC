package com.norteksoft.acs.service.security;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.ConfigAttributeEditor;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.intercept.web.FilterInvocationDefinitionSource;
import org.springframework.security.util.AntUrlPathMatcher;
import org.springframework.security.util.RegexUrlPathMatcher;
import org.springframework.security.util.UrlMatcher;

import com.norteksoft.portal.service.IndexManager;
import com.norteksoft.product.util.ContextUtils;

/**
 * 权限系统资源过滤，过滤所有客户端的请求
 * 根据请求的资源判断是否为系统中已经注册的受保护的资源，
 * 并返回访问该资源需要的权限
 * 
 * @author xiaoj
 */
@SuppressWarnings("deprecation")
public class FilterInvocationDefinitionSourceImpl implements
		FilterInvocationDefinitionSource ,InitializingBean {
	private Log log = LogFactory.getLog(FilterInvocationDefinitionSourceImpl.class);
	private UrlMatcher urlMatcher;
	private boolean useAntPath = true;  
	private boolean lowercaseComparisons = true;
	
	/**
	 *  FilterInvocationDefinitionSource 方法。
	 *  是权限判断的核心方法：
	 *  判断是否对当前URL设置了安全角色访问机制, 
	 *  有则返回相应的ConfigAttributeDefinition, 
	 *  否则返回null
	 */
	public ConfigAttributeDefinition getAttributes(Object filter)
			throws IllegalArgumentException {
		log.debug("*** getAttributes 开始");
		FilterInvocation filterInvocation = (FilterInvocation) filter;
		//请求的URI
		String requestURI = filterInvocation.getRequestUrl();
		boolean reloadTheme = false;
		if(requestURI.contains("_r=1")) reloadTheme = true;
		log.debug("*** 请求的URI:[" + requestURI + "]");
 		if(requestURI.contains("?"))
			requestURI = requestURI.substring(0, requestURI.indexOf('?'));
 		if(requestURI.startsWith("/js/")) return null;
		String grantedAuthorities = SecurityResourceCache.getAuthoritysInCache(requestURI);

		ConfigAttributeEditor configAttrEditor = new ConfigAttributeEditor();
		if (grantedAuthorities != null) {
			configAttrEditor.setAsText(grantedAuthorities);
		}else{
			configAttrEditor.setAsText("DEMO-ALL");
		}
		log.debug("*** getAttributes 结束");
		
		if(reloadTheme&&ContextUtils.getUserId()!=null&&ContextUtils.getCompanyId()!=null){
			IndexManager indexManager = (IndexManager) ContextUtils.getBean("indexManager");
			String theme = indexManager.getThemeByUser(
					ContextUtils.getUserId(), 
					ContextUtils.getCompanyId());
			if(StringUtils.isNotEmpty(theme)) ContextUtils.setTheme(theme);
		}
		
		return (ConfigAttributeDefinition) configAttrEditor.getValue();
	}

	@SuppressWarnings("unchecked")
	public Collection getConfigAttributeDefinitions() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
        this.urlMatcher = new RegexUrlPathMatcher();  
        if (useAntPath) {
            this.urlMatcher = new AntUrlPathMatcher();  
        }  
        if (lowercaseComparisons && !useAntPath) {  
            ((RegexUrlPathMatcher) this.urlMatcher).setRequiresLowerCaseUrl(true);  
        } else if (lowercaseComparisons && useAntPath) {  
            ((AntUrlPathMatcher) this.urlMatcher).setRequiresLowerCaseUrl(false);  
        }
	}
	
	public void setUseAntPath(boolean useAntPath) {
		this.useAntPath = useAntPath;
	}

	public void setLowercaseComparisons(boolean lowercaseComparisons) {
		this.lowercaseComparisons = lowercaseComparisons;
	}

}
