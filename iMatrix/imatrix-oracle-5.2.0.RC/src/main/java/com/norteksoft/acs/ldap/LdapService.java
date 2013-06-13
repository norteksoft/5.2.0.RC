package com.norteksoft.acs.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class LdapService {

	protected static Log logger = LogFactory.getLog(LdapService.class);
	private String adminName;
	private String password;
	private String ldapUrl;
	private boolean ssl = false;
	
	public LdapService(String adminName, String password, String ldapUrl){
		this.adminName = adminName;
		this.password = password;
		this.ldapUrl = ldapUrl;
	}
	
	public LdapService(String adminName, String password, String ldapUrl, boolean isSsl){
		this(adminName, password, ldapUrl);
		this.password = password;
		this.ldapUrl = ldapUrl;
	}
	
	public abstract List<LdapUser> getAllUser();
	
	public LdapContext initialLdap() {
		
		Hashtable<String, String> hashEnv = new Hashtable<String, String>();
		hashEnv.put(Context.SECURITY_AUTHENTICATION, "simple");  // LDAP访问安全级别
		hashEnv.put(Context.SECURITY_PRINCIPAL, adminName);  // AD User
		hashEnv.put(Context.SECURITY_CREDENTIALS, password);  // AD Password
		hashEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); // LDAP工厂类
		hashEnv.put(Context.PROVIDER_URL, ldapUrl);
		hashEnv.put("com.sun.jndi.ldap.connect.pool", "true");
		hashEnv.put(Context.REFERRAL, "follow");
		if(ssl) hashEnv.put(Context.SECURITY_PROTOCOL, "ssl");
		
		logger.debug(" start init ldap context ... ");
		try {
			LdapContext ldatCtx = new InitialLdapContext(hashEnv, null);
			logger.debug(" ldap context init success ... ");
			return ldatCtx;
		}catch (Exception e) {
			logger.error(" windows ad init error ", e);
			return null;
		}
	}
	
	public boolean closeLdap(LdapContext ldatCtx) {
		try {
			logger.debug(" start close ldap context ... ");
			ldatCtx.close();
			logger.debug(" close ldap context success. ");
			return true;
		} catch (NamingException e) {
			logger.error(" close ldap context failed. ", e);
			return false;
		}
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public void setSsl(boolean isSsl) {
		this.ssl = isSsl;
	}
	
	public String getAttributeValue(Attributes attrs, String key) throws NamingException{
		Attribute attr = attrs.get(key);
		if(attr != null) return  getString(attr.get());
		else return null;
	}
	
	public static String getString(Object obj){
		if(obj == null) return null;
		else return obj.toString();
	}
	
	public static class LdapUser{
		private String userDn;
		private String username;
		private String name;
		private String email;
		private String telephone;
		private List<String> departments = new ArrayList<String>();
		
		public String getUserDn() {
			return userDn;
		}
		public void setUserDn(String userDn) {
			this.userDn = userDn;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		@Override
		public String toString() {
			return "User [name=" + name + 
				", username=" + username +
				", email=" + email +  
				", telephone=" + telephone + 
				//", userDn=" + userDn +  
				"] depts: "+getDepartment()+"\n";
		}
		public List<String> getDepartment(){
			String[] dn = userDn.split(",");
			for(int i=dn.length-1; i>0; i--){
				if(dn[i].startsWith("OU=")||dn[i].startsWith("ou=")){
					departments.add(dn[i].replace("OU=", "").replace("ou=", ""));
				}
			}
			return departments;
		}
	}
}
