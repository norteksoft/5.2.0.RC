package com.norteksoft.product.util;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.product.api.ApiFactory;


public class ParameterUtils {
	private static ThreadLocal<ThreadParameters> threadParameters=new ThreadLocal<ThreadParameters>();
	
	public static void setParameters(ThreadParameters parameters){
		threadParameters.set(parameters);
	}
	
	public static Long getCompanyId(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getCompanyId();
	}
	
	public static Long getUserId(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getUserId();
	}
	public static String getCompanyCode(){
		 CompanyManager cm=(CompanyManager)ContextUtils.getBean("companyManager");
		 Company company=cm.getCompany(getCompanyId());
		 if(company==null)return null;
		 return company.getCode();
	}
	
	public static String getCompanyName(){
		 CompanyManager cm=(CompanyManager)ContextUtils.getBean("companyManager");
		 Company company=cm.getCompany(getCompanyId());
		 if(company==null)return null;
		 return company.getName();
	}
	
	public static String getUserName(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		String userName= parameter.getUserName();
		if(StringUtils.isNotEmpty(userName))return userName;
		Long userId=getUserId();
		if(userId==null)return null;
		com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserById(userId);
		if(user==null)return null;
		return user.getName();
	}
	public static String getPassword(){
		Long userId=getUserId();
		if(userId==null)return null;
		com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserById(userId);
		if(user==null)return null;
		return user.getPassword();
	}
	public static String getHonorificTitle(){
		Long userId=getUserId();
		if(userId==null)return null;
		com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserById(userId);
		if(user==null)return null;
		return user.getHonorificName();
	}
	public static String getLoginName(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		String loginName= parameter.getLoginName();
		if(StringUtils.isNotEmpty(loginName))return loginName;
		Long userId=getUserId();
		if(userId==null)return null;
		com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserById(userId);
		if(user==null)return null;
		return user.getLoginName();
	}
	public static Long getSystemId(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getSystemId();
	}
}
