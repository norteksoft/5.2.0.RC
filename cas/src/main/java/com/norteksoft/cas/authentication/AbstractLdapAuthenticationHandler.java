package com.norteksoft.cas.authentication;

import java.util.Hashtable;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public abstract class AbstractLdapAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
	
	private static final String COMPANY_CODE_SQL = 
    	"SELECT c.code FROM acs_user u join acs_company c on u.fk_company_id=c.id where u.login_name=?";
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private LdapContext ldapContext;

	@Override
	protected boolean authenticateUsernamePasswordInternal(
			UsernamePasswordCredentials credentials)throws AuthenticationException {
		
		final String username = getPrincipalNameTransformer().transform(credentials.getUsername());
        final String password = credentials.getPassword();
        
        return authenticate(username, password);
	}
	
	public abstract boolean authenticate(final String username, final String password);
	
	protected void closeContext(LdapContext context){
		try { 
			context.close(); 
		} catch (NamingException e) {  }
	}
	
	protected LdapContext getLdapContext(){
		try {
			if(ldapContext == null)
				ldapContext = new InitialLdapContext(getEnvironment(), null);
			return ldapContext;
		} catch (NamingException e) {
			log.error(" init ldap context error: ", e);
		}
		return null;
	}
	
	public String queryFullDn(String username){
		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchCtls.setReturningAttributes(new String[0]);
		String fullName = "";
		try {
			NamingEnumeration<SearchResult> answer = ldapContext.search(getSeachBase(username), getCnFilter(username), searchCtls);
			while (answer.hasMore()) {
				NameClassPair result = (NameClassPair) answer.next();
				fullName = result.getNameInNamespace();
			}
		} catch (NamingException e) {
			log.error(" query user full dn error. ", e);
		}
		return fullName;
	}
	
	public AbstractLdapAuthenticationHandler setJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
		return this;
	}
	
	public String getSeachBase(String username){
		String code = getCompanyCode(username);
		log.debug(" get orgnization code [" + code+"] by user ["+username+"] ");
		return "o="+code;
	}
	
	public String getCnFilter(String username){
		return "cn="+username;
	}
    
    public String getCompanyCode(String loginName){
    	return getJdbcTemplate().queryForObject(COMPANY_CODE_SQL, String.class, loginName);
    }
	
	public SimpleJdbcTemplate getJdbcTemplate() {
		return simpleJdbcTemplate;
	}
	
	public abstract Hashtable<String, String> getEnvironment();
}
