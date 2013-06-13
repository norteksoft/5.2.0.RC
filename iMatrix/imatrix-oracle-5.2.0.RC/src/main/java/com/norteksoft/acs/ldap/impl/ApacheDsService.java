package com.norteksoft.acs.ldap.impl;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import com.norteksoft.acs.ldap.LdapService;

public class ApacheDsService extends LdapService{
	
	private static String returnedAtts[] = { 
		"uid", // 登录名
		"cn",   // 用户名
		"mail",   // email
		"telephoneNumber"  // 电话
	};

	public ApacheDsService(String adminName, String password, String ldapUrl) {
		super(adminName, password, ldapUrl);
	}
	
	public List<LdapUser> getAllUser(){
		LdapContext ldatCtx = initialLdap();
		SearchControls searchCtls = new SearchControls(); 
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE); 
		String searchFilter="objectClass=person";
		String searchBase = "o=nortek";
		searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集
		
		try {
			NamingEnumeration<SearchResult> answer = ldatCtx.search(searchBase, searchFilter, searchCtls);
			List<LdapUser> users = new ArrayList<LdapUser>();
			LdapUser user = null;
			while (answer.hasMoreElements()) {
				SearchResult sr = answer.next();
				Attributes attrs = sr.getAttributes();
				user = new LdapUser();
				user.setUserDn(sr.getName());
				users.add(user);
				
				
				user.setUsername(getAttributeValue(attrs, "uid"));
				user.setName(getAttributeValue(attrs, "cn"));
				if(user.getUsername() == null) user.setUsername(user.getName());
				
				user.setEmail(getAttributeValue(attrs, "mail"));
				user.setTelephone(getAttributeValue(attrs, "telephoneNumber"));
			}
			return users;
		} catch (NamingException e) {
			logger.error(" get all user error. ", e);
			return null;
		} finally{
			closeLdap(ldatCtx);
		}
	}
	
	public static void main(String[] args) {
		ApacheDsService ad = new ApacheDsService("uid=admin,ou=system", "12345", "ldap://192.168.1.134:389");
		List<LdapUser> users = ad.getAllUser();
		System.out.println(users);
		System.exit(0);
	}
}
