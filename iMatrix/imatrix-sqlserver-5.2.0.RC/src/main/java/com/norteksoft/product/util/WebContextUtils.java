package com.norteksoft.product.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;

import com.norteksoft.acs.base.enumeration.SecretGrade;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.security.User;

/**
 * 权限系统工具类:
 * 用户获取当前登陆用户的用户信息及其公司信息. 
 * @deprecated 请使用ContextUtils
 */
@Deprecated
public class WebContextUtils {
	private static ApplicationContext context;
	
	public static void setContext(ApplicationContext applicationContext){
		context = applicationContext;
	}
	
	public static Object getBean(String beanName){
		return context.getBean(beanName);
	}
	
	protected static Log logger = LogFactory.getLog(WebContextUtils.class);
	private static String anonymous = "roleAnonymous";
	private static String anonymousRole = "ROLE_ANONYMOUS";
	private static BusinessSystem businessSystem = null;
	
	
	private WebContextUtils(){}
	
	private static User getCurrentUser(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null){
			if(authentication instanceof AnonymousAuthenticationToken){
				Object obj = authentication.getDetails();
				if(obj instanceof User){
					return (User)obj;
				}
			}
			Object obj = authentication.getPrincipal();
			if(obj instanceof User) {
				return (User)obj;
			}
		}
		User user = new User(anonymous, anonymous, false, false, false, false, 
				new GrantedAuthority[]{new GrantedAuthorityImpl(anonymousRole)});
		
		authentication = new AnonymousAuthenticationToken(anonymous, user, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return user;
	}
	
	/**
	 * 对于该资源判断当前用户是否有权限
	 * @param urlKey
	 * @return
	 */
	public static boolean isAuthority(String urlKey){
		GrantedAuthority[] autorities = getCurrentUser().getAuthorities();
		for(GrantedAuthority autority : autorities){
			if(urlKey.equals(autority.getAuthority())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取当前登录用户的ID
	 */
	public static Long getUserId(){
		return getCurrentUser().getUserId();
	}
	
	/**
	 *  获取当前登录用户的用户名。
	 * @return
	 */
	public static String getUserName(){
		return getCurrentUser().getTrueName();
	}
	
	public static String getLoginName(){
		return getCurrentUser().getUsername();
	}
	
	/**
	 *  获取当前登录用户的公司ID。
	 * @return
	 */
	public static Long getCompanyId(){
		return getCurrentUser().getCompanyId();
	}
	
	/**
	 * 获取当前登录用户的公司编码。
	 * @return
	 */
	public static String getCompanyCode(){
		return getCurrentUser().getCompanyCode();
	}
	
	/**
	 * 获取当前登录用户的公司名称。
	 * @return
	 */
	public static String getCompanyName(){
		return getCurrentUser().getCompanyName();
	}
	
	/**
	 * 获取当前所登录系统的ID
	 * @return Long (system id)
	 */
	public static Long getSystemId() {
		if(businessSystem != null){
			return businessSystem.getId();
		}
		return null;
	}

	/**
	 * 获取当前所登录系统的名称
	 * @return String (system name)
	 */
	public static String getSystemName() {
		if(businessSystem != null){
			return businessSystem.getName();
		}
		return null;
	}

	/**
	 * 获取当前登陆系统的编码
	 * @return
	 */
	public static String getSystemCode(){
		if(businessSystem != null){
			return businessSystem.getCode();
		}
		return null;
	}

	public static void setBusinessSystem(BusinessSystem businessSystem) {
		WebContextUtils.businessSystem = businessSystem;
	}
	
	public static String getTrueName(){
		return getCurrentUser().getTrueName();
	}
	
	/**
	 * 获取当前用户的Password
	 * @return
	 */
	public static String getPassword(){
		return getCurrentUser().getPassword();
	}
	
	public static String getHonorificTitle(){
		if(StringUtils.isEmpty(getCurrentUser().getHonorificTitle())){
			return getTrueName()==null?"":getTrueName();
		}else{
			return getCurrentUser().getHonorificTitle();
		}
	}
	
	/**
	 * 获取当前用户的Email
	 * @return
	 */
	public static String getEmail(){
		return getCurrentUser().getEmail();
	}
	
	public static SecretGrade getSecretGrade(){
		return getCurrentUser().getSecretGrade();
	}
	
	public static String getRoleCodes(){
		return getCurrentUser().getRoleCodes();
	}
	
	public static String getTheme(){
		return getCurrentUser().getTheme();
	}
	
	public static void setTheme(String theme){
		getCurrentUser().setTheme(theme);
	}
	
	public static boolean isAdmin(){
		String roleCode = getRoleCodes();
		if(roleCode != null && (roleCode.contains(",acsSystemAdmin,") || roleCode.contains(",acsSecurityAdmin,") || roleCode.contains(",acsAuditAdmin,"))){
			return true;
		}
		return false;
	}
	
}