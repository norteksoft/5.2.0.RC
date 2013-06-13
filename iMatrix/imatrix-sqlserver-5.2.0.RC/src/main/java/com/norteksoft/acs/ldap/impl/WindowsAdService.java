package com.norteksoft.acs.ldap.impl;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import com.norteksoft.acs.ldap.LdapService;

public class WindowsAdService extends LdapService{
	
	private static String returnedAtts[] = { 
		"sAMAccountName", // 登录名
		"name",   // 用户名
		"mail",   // email
		"telephoneNumber" // 电话
	};

	public WindowsAdService(String adminName, String password, String ldapUrl) {
		super(adminName, password, ldapUrl);
	}
	
	public List<LdapUser> getAllUser(){
		LdapContext ldatCtx = initialLdap();
		SearchControls searchCtls = new SearchControls(); // Create the search controls
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Specify the search scope
		String searchFilter = "objectClass=User"; // specify the LDAP search filter
		searchFilter="objectClass=Group";
		searchFilter="objectClass=Person";
		// searchFilter="objectClass=Domain"; // Domain = DC=norteksoft,DC=com
		
		String searchBase = "DC=nortek,DC=com"; // Specify the Base for the search//搜索域节点
		searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集
		
		// Search for objects using the filter
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
				
				if (attrs != null) {
					NamingEnumeration<? extends Attribute> ae = attrs.getAll();
					while(ae.hasMore()){
						Attribute attr = (Attribute) ae.next();
						NamingEnumeration<?> e = attr.getAll();
						if(e.hasMore()){
							if("sAMAccountName".equals(attr.getID())){
								user.setUsername(getString(e.next()));
							}else if("name".equals(attr.getID())){
								user.setName(getString(e.next()));
							}else if("mail".equals(attr.getID())){
								user.setEmail(getString(e.next()));
							}else if("telephoneNumber".equals(attr.getID())){
								user.setTelephone(getString(e.next()));
							}
						}
					}
				}
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
		WindowsAdService ad = new WindowsAdService("administrator@nortek.com", "123abc,.", "ldap://192.168.1.5:389");
		List<LdapUser> users = ad.getAllUser();
		System.out.println(users);
		System.exit(0);
	}
}
