package com.norteksoft.acs.base.utils;


import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.sysSetting.LdapType;
import com.norteksoft.acs.entity.sysSetting.ServerConfig;
import com.norteksoft.product.util.ContextUtils;

public class Ldaper {
	private static Log log = LogFactory.getLog(Ldaper.class);
	protected static SessionFactory sessionFactory;
	private static SimpleHibernateTemplate<ServerConfig, Long> serverConfigDao;
	private static String message = "";
	
	static {
		init();
	}

	private Ldaper() {
		
	}

	private static void init() {
		sessionFactory = getSessionFactory();
		serverConfigDao = new SimpleHibernateTemplate<ServerConfig, Long>(sessionFactory, ServerConfig.class);
	}
	
	protected static SessionFactory getSessionFactory() {
		sessionFactory = (SessionFactory)ContextUtils.getBean("sessionFactory");
		return sessionFactory;
	}
	/**
	 * 判断是否启动了LDAP集成
	 * @return
	 */
	public static boolean isStartedAboutLdap(){
		ServerConfig serverConfig = (ServerConfig)serverConfigDao
		           .findUnique("FROM ServerConfig s WHERE s.companyId=?", ContextUtils.getCompanyId());
		if(serverConfig!=null && serverConfig.getLdapInvocation()==true){
			return true;
		}
		return false;
	}

	/**
	 * 修改用户信息.
	 * 
	 * @param attrs
	 *            Attributes 需要修改的用户属性.
	 * @param userDN
	 *            String 用户DN
	 * @return
	 */
	public static String modify(String userDN,String key,String value,String userName) {
		LdapContext ctx = null;
		log.debug("***进入modify方法 ***");
		log.debug("参数为[userDN= "+userDN+";key="+key+";value="+value+";userName="+userName+"]");
		try {
			log.debug("***正在获取LdapContext链接  ***");
			ctx = getConnectionFromPool();
			log.debug("*** 获取LdapContext链接成功  ***");
			Attributes attrs = new BasicAttributes(true);
			attrs.put(key, value);
			ctx.modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, attrs);
			log.debug("***修改LDAP用户成功***");
		} catch (NamingException e) {
			log.debug("***修改LDAP失败:"+e.getMessage()+"****"+e.getStackTrace());
			log.debug("***修改LDAP用户["+e.getRemainingName()+"]失败***");
			return userName;
		}finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		log.debug("***退出modify方法 ***");
		return "";
	}

	/**
	 * 从连接池中获取一个连接.
	 * 
	 * @return LdapContext
	 * @throws NamingException
	 */
	public static LdapContext getConnectionFromPool(){
		Long companyId = ContextUtils.getCompanyId();
		ServerConfig serverConfig = (ServerConfig)serverConfigDao.findUnique("FROM ServerConfig s WHERE s.companyId=?", companyId);
		log.debug("*** 公司Ldap服务配置 *** ServerConfig: [ldapUrl="+serverConfig.getLdapUrl()+";ldapUSerName="+serverConfig.getLdapUsername()+";ldapPassword="+serverConfig.getLdapPassword()+"]");
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, serverConfig.getLdapUrl());
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		if(serverConfig.getLdapType() == LdapType.APACHE){
			env.put(Context.SECURITY_PRINCIPAL, "UID="+serverConfig.getLdapUsername()+",OU=system");
		}else if(serverConfig.getLdapType() == LdapType.DOMINO){
			env.put(Context.SECURITY_PRINCIPAL, "CN="+serverConfig.getLdapUsername());
		}else if(serverConfig.getLdapType() == LdapType.WINDOWS_AD){
			env.put(Context.SECURITY_PRINCIPAL, serverConfig.getLdapUsername());
		}
		env.put(Context.SECURITY_CREDENTIALS, serverConfig.getLdapPassword());
		env.put("com.sun.jndi.ldap.connect.pool", "true");
		env.put("java.naming.referral", "follow");
		try {
			return new InitialLdapContext(env, null);
		} catch (NamingException e) {
			log.debug("*** 初始化LdapContext异常 ***", e);
		}
		return null;
	}

	/**
	 * 校验用户登录.
	 * 
	 * @param userDn
	 *            String
	 * @param password
	 *            String
	 * @return boolean
	 */
	public static boolean authenticate(String userDn, String password) {
		LdapContext ctx = null;
		try {
			Control[] connCtls = new Control[] {};
			ctx = getConnectionFromPool();
			ctx.getRequestControls();
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDn);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(connCtls);
			
			return true;
		} catch (AuthenticationException e) {
			return false;
		} catch (NamingException e) {
			return false;
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 添加&修改部门(dc)
	 * @param department
	 * @param add(true:新建，false:修改)
	 * @return
	 */
	public static String addGroup(Department department,boolean isCreate){
		LdapContext ctx = null;
		try {
			ctx = getConnectionFromPool();
			
			StringBuilder groupDN = new StringBuilder();
			groupDN = getGroupDn(department, groupDN);
			groupDN.append(",o=");
			groupDN.append(department.getCompany().getCode());
			groupDN = groupDN.deleteCharAt(0);
			
			Attributes attrs = new BasicAttributes(true);
			if(isCreate){
				attrs.put("objectClass", "dominoOrganizationalUnit");
				attrs.put("FullName",department.getName());
				ctx.createSubcontext(groupDN.toString(), attrs); 
			}else{
				attrs.put("cn",department.getName());
				ctx.modifyAttributes(groupDN.toString(), DirContext.REPLACE_ATTRIBUTE, attrs);
			}
			
		} catch (NameNotFoundException e) {
			log.debug(" Can't find "+department.getName());
			return department.getName();
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return "";
	}
	
	private static StringBuilder getGroupDn(Department department,StringBuilder groupDN){
		groupDN.append(",ou=");
		groupDN.append(department.getName());
		if(department.getParent() != null){ 
			getGroupDn(department.getParent(),groupDN);
		}
		return groupDN;
	}
	/**
	 * 删除部门.
	 * 
	 */
	public static String delDepartment(Department department) {
		LdapContext ctx = null;
		
		StringBuilder searchBase = new StringBuilder();
		searchBase = getGroupDn(department, searchBase);
		searchBase.append(",o=");
		searchBase.append(department.getCompany().getCode());
		searchBase = searchBase.deleteCharAt(0);
		
		ctx = getConnectionFromPool();
		recursion(searchBase.toString(), ctx, "o="+department.getCompany().getCode(),department.getName());	  
		return message;
	}
	/**
	 * 逐层删除操作
	 * @param rootBase
	 * @param ctx
	 * @param baseCode
	 */
	private static void recursion(String rootBase, LdapContext ctx,String baseCode,String departmentName){
		try {
			String url = rootBase;
			NamingEnumeration<SearchResult>  results = 	ctx.search(url, null);
			String currentUrl = null;
			if(!results.hasMore()){
				ctx.destroySubcontext(url);
			}else{
				while(results.hasMore()){
			    	SearchResult result = results.next();
			    	currentUrl = result.getName()+","+url;
			    	Attributes attrs = result.getAttributes();
			    	if(attrs.get("objectClass").toString().indexOf("dominoPerson") > -1){
			    		String userDN = ("cn="+attrs.get("cn")).replaceAll("cn: ", "");
						ctx.createSubcontext(userDN+","+baseCode, attrs);
						ctx.destroySubcontext(currentUrl);
			    	}else{
			    		recursion(currentUrl,ctx,baseCode,departmentName);
			    	}
				}
				ctx.destroySubcontext(url);
		    }
		}catch (NamingException e) {
			message = departmentName;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * 修改用户名或密码s
	 * @param user
	 * @param companyCode
	 * @param departments
	 * @param flag(true:改名，false：改密码)
	 * @param cn
	 * @return
	 */
	public static String modifyUser(User user, String companyCode, 
			List<Department> departments, boolean flag,String userName){
		String key = null;
		String value = null;
		if(flag){
			key = "uid";
			value = user.getLoginName();
		}else{
			key = "userPassword";
			value = user.getPassword();
		}
		if(departments.isEmpty()){
			log.debug("*** 用户没有部门，直接加入公司 ***");
			StringBuilder userDN = new StringBuilder("cn=");
			userDN.append(userName);
			userDN.append(",o=");
			userDN.append(companyCode);
			message = modify(userDN.toString(), key, value,user.getLoginName());
		}else{
			log.debug("*** 用户已加入[" + departments.size() + "]个部门 ***");
			for(Department d:departments){
				StringBuilder userDN = new StringBuilder("cn=");
				userDN.append(userName);
				userDN = getGroupDn(d, userDN);
				userDN.append(",o=");
				userDN.append(companyCode);
				message = modify(userDN.toString(), key, value,user.getLoginName());
			}
		}
		return message;
	}
	/**
	 *  添加用户 
	 * @param user
	 * @return
	 */
	public static String addUser(UserInfo userInfo,String companyCode,String trueName) {
		User user = userInfo.getUser();
		LdapContext ctx = null;
		StringBuilder userDN = new StringBuilder("cn=");
		userDN.append(user.getLoginName());
		userDN.append(",o=");
		userDN.append(companyCode);
		try {
			ctx = getConnectionFromPool();
			Attributes attrs = new BasicAttributes(true);
			attrs.put("objectClass", "dominoPerson");
			attrs.put("userPassword", user.getPassword());
			attrs.put("sn", trueName);
			attrs.put("uid",user.getLoginName());
			String email = user.getEmail();
			if(!StringUtils.isEmpty(email)){
				attrs.put("mail",email);
			}
			ctx.createSubcontext(userDN.toString(), attrs); 
		} catch(NameNotFoundException e){ 
			log.debug(" Can't find "+user.getLoginName());
			return user.getLoginName();
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return "";
	}

	/**
	 * 删除用户.
	 * 
	 * @param userDN
	 *            String 用户DN
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String delUser(User user,String companyCode) {
		LdapContext ctx = null;
		try {
			ctx = getConnectionFromPool();
			Set<DepartmentUser> sets = user.getDepartmentUsers();
			Iterator it = sets.iterator();
			if(it.hasNext()){
				while(it.hasNext()){
					DepartmentUser departmentToUser = (DepartmentUser)it.next();
					Department department = departmentToUser.getDepartment();
					StringBuilder userDN = new StringBuilder("cn="+user.getLoginName());
					userDN = getGroupDn(department,userDN);
					ctx.destroySubcontext(userDN.toString());
				}
			}else{
				StringBuilder userDN = new StringBuilder("cn="+user.getLoginName());
				userDN.append(",o="+companyCode);
				ctx.destroySubcontext(userDN.toString());
			}
		} catch (NameNotFoundException e) {
			return user.getLoginName();
		} catch (NamingException e) {
			log.debug("delUser has a exception named NamingException");
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return "";
	}
	/**
	 * 部门增加/移除用户
	 * @param department
	 * @param users
	 * @param flag(true:增加,false:移除)
	 * @return
	 */
	public static String addUsersInDepartment(Department department,List<User> users,boolean flag){
		LdapContext ctx = null;
		try {
			ctx = getConnectionFromPool();
			for(User u:users){
				StringBuilder oldUserDN = new StringBuilder("CN="+u.getLoginName());
				oldUserDN.append(",O="+department.getCompany().getCode());
				
				StringBuilder userDN = new StringBuilder("cn="+u.getLoginName());
				userDN = getGroupDn(department,userDN);
				userDN.append(",o="+department.getCompany().getCode());
				
				NamingEnumeration<SearchResult>  results = 	ctx.search("o="+department.getCompany().getCode(), null);
				
				if(flag){
					boolean exsit = false;
					while(results.hasMore()){
				    	SearchResult result = results.next();
				    	Attributes attrs = result.getAttributes();
				    	if(attrs.get("cn")!=null){
				    		String cn = attrs.get("cn").toString().replaceAll("cn: ", "");
					    	if(cn.equals(u.getLoginName())){
								ctx.createSubcontext(userDN.toString(), attrs);
								ctx.destroySubcontext(oldUserDN.toString());
								exsit = true;
					    	}
				    	}
				    	
					}
					if(!exsit){
						Attributes attrs = new BasicAttributes(true);
						attrs.put("objectClass", "dominoPerson");
						attrs.put("userPassword", u.getPassword());
						attrs.put("sn", u.getLoginName());
						attrs.put("uid",u.getLoginName());
						ctx.createSubcontext(userDN.toString(), attrs); 
					}
				}else{
					ctx.destroySubcontext(userDN.toString());
					boolean exsit = false;
					while(results.hasMore()){
				    	SearchResult result = results.next();
				    	Attributes attrs = result.getAttributes();
				    	if(attrs.get("cn")!=null){
				    		String cn = attrs.get("cn").toString().replaceAll("cn: ", "");
					    	if(cn.equals(u.getLoginName())){
								exsit = true;
					    	}
				    	}
				    	
					}
					if(!exsit){
						Attributes attrs = new BasicAttributes(true);
						attrs.put("objectClass", "dominoPerson");
						attrs.put("userPassword", u.getPassword());
						attrs.put("sn", u.getLoginName());
						attrs.put("uid",u.getLoginName());
						ctx.createSubcontext(oldUserDN.toString(), attrs); 
					}
				}
				
			}
		} catch (NameNotFoundException e) {
			return department.getName();
		} catch (NamingException e) {
			log.debug("addUsersInDepartment has a exception named NamingException");
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return "";
	}
}