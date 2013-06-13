package com.norteksoft.acs.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Service;

import com.norteksoft.acs.base.enumeration.SecretGrade;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.WebContextUtils;
/**
 * 
 * @deprecated 请使用ContextUtils
 *
 */
@Service
@Deprecated
public class ContextService {
	private static final String COMPANY_ID = "companyId";
	private static final String USER_ID = "userId";
	private static final String LOGIN_NAME = "loginName";
	private static final String COMPANY_CODE = "companyCode";
	private static final String COMPANY_NAME = "companyName";
	private static final String USER_NAME = "userName";

	/**
	 * 获取公司ID
	 * @return
	 */
	public Long getCompanyId(){
		Long id = WebContextUtils.getCompanyId();
		if(id==null){
			id=ParameterUtils.getCompanyId();
		}
		if(id == null){
			id = getLongParameter(COMPANY_ID);
		}
		return id;
	}
	
	/**
	 * 获取公司名称编码
	 * @return
	 */
	public String getCompanyCode(){
		String companyCode = WebContextUtils.getCompanyCode();
		if(companyCode==null){
			companyCode= ParameterUtils.getCompanyCode();
		}
		if(companyCode == null){
			companyCode = getParameter(COMPANY_CODE);
		}
		return companyCode;
	}

	/**
	 * 获取公司名称
	 * @return
	 */
	public String getCompanyName(){
		String companyName = WebContextUtils.getCompanyName();
		if(companyName == null){
			companyName=ParameterUtils.getCompanyName();
		}
		if(companyName == null){
			companyName = getParameter(COMPANY_NAME);
		}
		return companyName;
		
	}
	
	/**
	 * 获取当前用户ID
	 * @return
	 */
	public Long getUserId(){
		Long id = WebContextUtils.getUserId();
		if(id == null){
			id=ParameterUtils.getUserId();
		}
		if(id == null){
			id = getLongParameter(USER_ID);
		}
		return id;
	}
	
	/**
	 * 获取当前用户的登录名
	 * @return
	 */
	public String getLoginName(){
		String loginName = WebContextUtils.getLoginName();
		if(loginName==null || "roleAnonymous".equals(loginName)){
			loginName=ParameterUtils.getLoginName();
		}
		if(loginName == null || "roleAnonymous".equals(loginName)){
			loginName = getParameter(LOGIN_NAME);
		}
		return loginName;
	}
	
	/**
	 * 获取当前用户的用户名
	 * @return
	 */
	public String getUserName(){
		String userName = WebContextUtils.getTrueName();
		if(userName == null){
			userName=ParameterUtils.getUserName();
		}
		if(userName == null){
			userName = getParameter(USER_NAME);
		}
		return userName;
	}
	
	/**
	 * 获取spring对象
	 * @param beanName
	 * @return
	 */
	public Object getBean(String beanName){
		return WebContextUtils.getBean(beanName);
	}
	
	/**
	 * 获取系统ID
	 * @return
	 */
	public Long getSystemId(){
		Long id = WebContextUtils.getSystemId();
		if(id == null){
			id=ParameterUtils.getSystemId();
		}
		return id;
	}
	
	/**
	 * 获取系统编号
	 * @return
	 */
	public String getSystemCode(){
		return WebContextUtils.getSystemCode();
	}
	
	/**
	 * 获取系统名称
	 * @return
	 */
	public String getSystemName(){
		return WebContextUtils.getSystemName();
	}
	
	public SecretGrade getSecretGrade(){
		return WebContextUtils.getSecretGrade();
	}
	
	public String getRoleCodes(){
		return WebContextUtils.getRoleCodes();
	}
	
	public boolean isAdmin(){
		return WebContextUtils.isAdmin();
	}
	
	public boolean isSystemAdmin(){
		return WebContextUtils.getRoleCodes() != null && WebContextUtils.getRoleCodes().contains(",acsSystemAdmin,");
	}
	
	public boolean isAuditAdmin(){
		return WebContextUtils.getRoleCodes() != null && WebContextUtils.getRoleCodes().contains(",acsAuditAdmin,");
	}
	
	public boolean isSecurityAdmin(){
		return WebContextUtils.getRoleCodes() != null && WebContextUtils.getRoleCodes().contains(",acsSecurityAdmin,");
	}
	
	private Long getLongParameter(String name){
		String property = getParameter(name);
		Long value = null;
		if(property != null){
			value = Long.valueOf(property);
		}
		return value;
	}
	
	private String getParameter(String name){
		HttpServletRequest request = ServletActionContext.getRequest();
		String property = request.getParameter(name);
		if(StringUtils.isBlank(property)){
			property = null;
		}
		return property;
	}
}
