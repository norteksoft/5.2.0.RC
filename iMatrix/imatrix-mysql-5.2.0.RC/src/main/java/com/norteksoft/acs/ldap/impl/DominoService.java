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

public class DominoService extends LdapService{

	private static String returnedAtts[] = { 
		"uid", // 登录名
		"cn",   // 用户名
		"mail",   // email
		"objectClass"  // 
	};

	public DominoService(String adminName, String password, String ldapUrl) {
		super(adminName, password, ldapUrl);
	}
	
	public static void main(String[] args) {
		DominoService ad = new DominoService("cn=admin", "123456", "ldap://192.168.1.238:389");
		List<LdapUser> users = ad.getAllUser();
		System.out.println(users);
	}

	@Override
	public List<LdapUser> getAllUser() {
		LdapContext ldatCtx = initialLdap();
		SearchControls searchCtls = new SearchControls(); // Create the search controls
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Specify the search scope
		String searchBase = "o=norteksoft"; // Specify the Base for the search//搜索域节点
		searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集
		
		List<LdapUser> users = new ArrayList<LdapUser>();
		try {
			search(ldatCtx, searchBase, searchCtls, users);
		} catch (NamingException e) {
			e.printStackTrace();
		} finally{
			closeLdap(ldatCtx);
		}
		return users;
	}
	
	public void search(LdapContext ldatCtx, String searchBase, SearchControls searchCtls, List<LdapUser> users) throws NamingException{
		NamingEnumeration<SearchResult> answer = ldatCtx.search(searchBase, null);
		LdapUser user = null;
		
		while (answer.hasMoreElements()) {
			SearchResult sr = answer.next();
			Attributes attrs = sr.getAttributes();
			if(attrs.get("objectClass").contains("dominoPerson")){ // person
				user = new LdapUser();
				user.setUserDn(sr.getNameInNamespace());
				
				user.setUsername(getAttributeValue(attrs, "uid"));
				user.setName(getAttributeValue(attrs, "cn"));
				if(user.getUsername() == null) user.setUsername(user.getName());
				user.setEmail(getAttributeValue(attrs, "mail"));
				users.add(user);
				
			}else if(attrs.get("objectClass").contains("dominoOrganizationalUnit")){ // organizationalUnit
				search(ldatCtx, sr.getNameInNamespace(), searchCtls, users);
			}
		}
	}
	
}
