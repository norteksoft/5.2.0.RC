package com.norteksoft.acs.service.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;

import com.norteksoft.acs.base.enumeration.OperatorType;
import com.norteksoft.product.util.PropUtils;

public class DynamicAuthority {
	
	private final static String PROP_FILE_NAME = "applicationContent.properties";
	private final static String SYSTEM_ADMIN = ",acsSystemAdmin,";
	private final static String SECURITY_ADMIN = ",acsSecurityAdmin,";
	private final static String AUDIT_ADMIN = ",acsAuditAdmin,";
	private static Map<OperatorType, List<OperatorType>> systemLogAuthority = new HashMap<OperatorType, List<OperatorType>>();
	private static Map<OperatorType, List<OperatorType>> loginLogAuthority = new HashMap<OperatorType, List<OperatorType>>();

	
	public static List<GrantedAuthority> getLogAuthority(List<GrantedAuthority> authsList, StringBuilder roles){
		if(roles.indexOf(SYSTEM_ADMIN)>=0||roles.indexOf(SECURITY_ADMIN)>=0 ||roles.indexOf(AUDIT_ADMIN)>=0){
			boolean hasLogAuth = false;
			// 系统日志权限
			if(getSystemLogAuthority().get(OperatorType.SYSTEM_ADMIN).isEmpty()){
				removeSystemLogAuthority(authsList);
			}else{
				addSystemLogAuthority(authsList);
				hasLogAuth = true;
			}
			// 登陆日志权限
			if(getLoginLogAuthority().get(OperatorType.SYSTEM_ADMIN).isEmpty()){
				removeLoginLogAuthority(authsList);
			}else{
				addLoginLogAuthority(authsList);
				hasLogAuth = true;
			}
			// 二级菜单权限
			if(hasLogAuth) authsList.add(new GrantedAuthorityImpl("systemOperateLog"));
		}
		return authsList;
	}
	
	static{
		initSystemLogAuthority();
		initLoginLogAuthority();
	}
	
	public static Map<OperatorType, List<OperatorType>> getSystemLogAuthority(){
		return systemLogAuthority;
	}
	
	public static Map<OperatorType, List<OperatorType>> getLoginLogAuthority(){
		return loginLogAuthority;
	}
	
	static void initSystemLogAuthority(){
		List<OperatorType> authList = null;
		String allAuth = null;
		String[] auths = null;
		String[] auth = null;
		for(SystemLogAuthorityKeys key : SystemLogAuthorityKeys.values()){
			authList = new ArrayList<OperatorType>();
			allAuth = PropUtils.getProp(PROP_FILE_NAME, key.code);
			auths = allAuth.split(",");
			for(String authStr : auths){
				auth = authStr.split(":");
				if(!("0").equals(auth[1])){
					authList.add(getOperatorTypeByCode(auth[0]));
				}
			}
			systemLogAuthority.put(getOperatorTypeByName(key.toString()), authList);
		}
	}
	
	static void initLoginLogAuthority(){
		List<OperatorType> authList = null;
		String allAuth = null;
		String[] auths = null;
		String[] auth = null;
		for(LoginLogAuthorityKeys key : LoginLogAuthorityKeys.values()){
			authList = new ArrayList<OperatorType>();
			allAuth = PropUtils.getProp(PROP_FILE_NAME, key.code);
			auths = allAuth.split(",");
			for(String authStr : auths){
				auth = authStr.split(":");
				if(!("0").equals(auth[1])){
					authList.add(getOperatorTypeByCode(auth[0]));
				}
			}
			loginLogAuthority.put(getOperatorTypeByName(key.toString()), authList);
		}
	}
	
	private static OperatorType getOperatorTypeByName(String name){
		for(OperatorType type : OperatorType.values()){
			if(type.toString().equals(name)){
				return type;
			}
		}
		return null;
	}
	
	private static OperatorType getOperatorTypeByCode(String code){
		for(OperatorType type : OperatorType.values()){
			if(type.getCode().equals("operator.type."+code)){
				return type;
			}
		}
		return null;
	}
	
	private static void removeSystemLogAuthority(List<GrantedAuthority> authsList){
		String[] sysLogAuth = getSystemLogAuth();
		for(String auth : sysLogAuth){
			authsList.remove(auth);
		}
	}
	
	private static void addSystemLogAuthority(List<GrantedAuthority> authsList){
		String[] sysLogAuth = getSystemLogAuth();
		for(String auth : sysLogAuth){
			authsList.add(new GrantedAuthorityImpl(auth));
		}
	}
	
	private static void removeLoginLogAuthority(List<GrantedAuthority> authsList){
		String[] loginLogAuth = getLoginLogAuth();
		for(String auth : loginLogAuth){
			authsList.remove(auth);
		}
	}
	
	private static void addLoginLogAuthority(List<GrantedAuthority> authsList){
		String[] loginLogAuth = getLoginLogAuth();
		for(String auth : loginLogAuth){
			authsList.add(new GrantedAuthorityImpl(auth));
		}
	}
	
	private static String[] getSystemLogAuth(){
		String auth = PropUtils.getProp("applicationContent.properties", "log.system.log");
		return auth.split(",");
	}
	
	private static String[] getLoginLogAuth(){
		String auth = PropUtils.getProp("applicationContent.properties", "log.login.log");
		return auth.split(",");
	}
	
	public static OperatorType getOperatorType(String roles){
		if(isSystemAdmin(roles)){
			return OperatorType.SYSTEM_ADMIN;
		}else if(isSecurityAdmin(roles)){
			return OperatorType.SECURITY_ADMIN;
		}else if(isAuditAdmin(roles)){
			return OperatorType.AUDIT_ADMIN;
		}
		return OperatorType.COMMON_USER;
	}
	
	public static boolean isSystemAdmin(String roles){
		return roles.contains(SYSTEM_ADMIN);
	}
	
	public static boolean isSecurityAdmin(String roles){
		return roles.contains(SECURITY_ADMIN);
	}
	
	public static boolean isAuditAdmin(String roles){
		return roles.contains(AUDIT_ADMIN);
	}
	
	enum SystemLogAuthorityKeys{
		SYSTEM_ADMIN("systemAdmin.system.log.authority"),
		SECURITY_ADMIN("securityAdmin.system.log.authority"),
		AUDIT_ADMIN("auditAdmin.system.log.authority");
		
		String code;
		
		SystemLogAuthorityKeys(String code){
			this.code = code;
		}
	}
	
	enum LoginLogAuthorityKeys{
		SYSTEM_ADMIN("systemAdmin.login.log.authority"),
		SECURITY_ADMIN("securityAdmin.login.log.authority"),
		AUDIT_ADMIN("auditAdmin.login.log.authority");
		
		String code;
		
		LoginLogAuthorityKeys(String code){
			this.code = code;
		}
	}
}
