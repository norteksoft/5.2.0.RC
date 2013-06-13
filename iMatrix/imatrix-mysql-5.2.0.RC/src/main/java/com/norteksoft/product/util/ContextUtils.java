package com.norteksoft.product.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;
import org.springframework.web.context.ContextLoader;

import com.norteksoft.acs.base.enumeration.SecretGrade;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.security.User;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 获取当前登陆用户的用户信息及其公司信息、当前业务系统信息
 * 获取指定名称的bean
 */
public class ContextUtils {
    
    private static final String COMPANY_ID = "companyId";
    private static final String USER_ID = "userId";
    private static final String LOGIN_NAME = "loginName";
    private static final String COMPANY_CODE = "companyCode";
    private static final String COMPANY_NAME = "companyName";
    private static final String USER_NAME = "userName";
    protected static Log logger = LogFactory.getLog(ContextUtils.class);
    private static String anonymous = "roleAnonymous";
    private static String anonymousRole = "ROLE_ANONYMOUS";
    
    
    private ContextUtils(){}
    /**
     * 获取公司ID
     * @return
     */
    public static Long getCompanyId(){
        Long id = getCurrentUser().getCompanyId();
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
    public static String getCompanyCode(){
        String companyCode = getCurrentUser().getCompanyCode();
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
    public static String getCompanyName(){
        String companyName = getCurrentUser().getCompanyName();
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
    public static Long getUserId(){
        Long id = getCurrentUser().getUserId();
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
    public static String getLoginName(){
        String loginName = getCurrentUser().getUsername();
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
    public static String getUserName(){
        String userName = getCurrentUser().getTrueName();
        if(userName == null){
            userName=ParameterUtils.getUserName();
        }
        if(userName == null){
            userName = getParameter(USER_NAME);
        }
        return userName;
    }
    
    
    /**
     * 获取系统ID（要求当前线程的context classloader为web应用的classloader）
     * @return
     */
    public static Long getSystemId(){
        BusinessSystemManager bsm=(BusinessSystemManager)getBean("businessSystemManager");
        BusinessSystem system=bsm.getSystemBySystemCode(getSystemCode());
        Long id =null;
        if(system!=null){
        	id = system.getId();
        }
        if(id == null){
            id=ParameterUtils.getSystemId();
        }
        return id;
    }
    
    /**
     * 获取系统编号（要求当前线程的context classloader为web应用的classloader）
     * @return
     */
    public static String getSystemCode(){
        /**
         * struts2的ServletActionContext.getServletContext()要求当前线程必须经过了struts2的filter的处理，
         * 所以在某些情况下是取不到ServletContext的。比如在自己创建的线程中或定时器的任务中
         * ContextLoader.getCurrentWebApplicationContext()是从静态的map中取得WebApplicationContext，
         * 这个map中的值是在ContextLoaderListner中赋值的，所以在这个web应用中的任何地方都可以取到值（只要是web应用的classloader加载的类）
         * 如果在代码中用了自定义的classloader加载了某个类，在这个类中调用这个方法就会取不到值了（目前是不会出现这种情况的）
         */
        
        return ContextLoader.getCurrentWebApplicationContext().getServletContext().getInitParameter("systemCode");
    }
    
    /**
     * 获取系统名称（要求当前线程的context classloader为web应用的classloader）
     * @return
     */
    public static String getSystemName(){
        BusinessSystemManager bsm=(BusinessSystemManager)getBean("businessSystemManager");
        return bsm.getSystemBySystemCode(getSystemCode()).getName();
    }
    
    public static boolean isSystemAdmin(){
    	String codes = getRoleCodesStartComma();
        return codes != null && codes.contains(",acsSystemAdmin,");
    }
    
    public static boolean isAuditAdmin(){
    	String codes = getRoleCodesStartComma();
        return codes != null && codes.contains(",acsAuditAdmin,");
    }
    
    public static boolean isSecurityAdmin(){
    	String codes = getRoleCodesStartComma();
        return codes != null && codes.contains(",acsSecurityAdmin,");
    }
    
    private static Long getLongParameter(String name){
        String property = getParameter(name);
        Long value = null;
        if(property != null){
            value = Long.valueOf(property);
        }
        return value;
    }
    
    private static String getParameter(String name){
        HttpServletRequest request = Struts2Utils.getRequest();
        if(request==null){
        	return null;
        }
        String property = request.getParameter(name);
        if(StringUtils.isBlank(property)){
            property = null;
        }
        return property;
    }
    
    /**
     * 从spring bean factory中得到指定名称的bean
     * @param beanName
     * @return
     */
	public static Object getBean(String beanName){
		return ContextLoader.getCurrentWebApplicationContext().getBean(beanName);
	}
	
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
	 * 对于该资源判断当前用户是否有权限
	 * @param urlKey
	 * @return
	 */
	public static boolean isAuthority(String url,String systemCode){
		FunctionManager funM=(FunctionManager)getBean("functionManager");
		BusinessSystemManager bsm=(BusinessSystemManager)getBean("businessSystemManager");
		BusinessSystem system=bsm.getSystemBySystemCode(systemCode);
		if(system==null)return false;
		Function function=funM.getFunctionByPath(url, system.getId());
		if(function==null)return false;
		GrantedAuthority[] autorities = getCurrentUser().getAuthorities();
		for(GrantedAuthority autority : autorities){
			if(function.getCode().equals(autority.getAuthority())){
				return true;
			}
		}
		return false;
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
		String codes = getCurrentUser().getRoleCodes();
		if(StringUtils.isEmpty(codes)){
			com.norteksoft.acs.entity.organization.User user = BeanUtil.turnToUser(ApiFactory.getAcsService().getUserByLoginName(getLoginName()));
			codes = ApiFactory.getAcsService().getRolesExcludeTrustedRole(BeanUtil.turnToModelUser(user));
		}
		return codes;
	}
	
	public static String getTheme(){
		return getCurrentUser().getTheme();
	}
	
	public static void setTheme(String theme){
		getCurrentUser().setTheme(theme);
	}
	
	private static String getRoleCodesStartComma(){
		String roleCode = getRoleCodes();
		if(!roleCode.startsWith(",")) roleCode=","+roleCode+",";
		return roleCode;
	}
	
	public static boolean isAdmin(){
		String roleCode = getRoleCodesStartComma();
		if(roleCode != null && (roleCode.contains(",acsSystemAdmin,") || roleCode.contains(",acsSecurityAdmin,") || roleCode.contains(",acsAuditAdmin,"))){
			return true;
		}
		return false;
	}
	
	/**
	 * 根据系统编码获得系统ID
	 * @param code
	 * @return
	 */
	public static Long getSystemId(String code){
		return ApiFactory.getAcsService().getSystemByCode(code)==null?null:ApiFactory.getAcsService().getSystemByCode(code).getId();
	}
	
}