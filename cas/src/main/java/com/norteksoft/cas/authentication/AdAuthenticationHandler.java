package com.norteksoft.cas.authentication;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;

public class AdAuthenticationHandler extends AbstractLdapAuthenticationHandler {

	private AuthenticationPattern authenticationPattern;
	private Hashtable<String, String> environment = new Hashtable<String, String>();
	
	public AdAuthenticationHandler(AuthenticationPattern authenticationPattern){
		this.authenticationPattern = authenticationPattern;
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.REFERRAL, "follow");
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, authenticationPattern.getUrl());
		environment.put(Context.SECURITY_PRINCIPAL, authenticationPattern.getUsername());
		environment.put(Context.SECURITY_CREDENTIALS, authenticationPattern.getPassword());
	}
	
	public boolean authenticate(final String username, final String password){
		LdapContext context = getLdapContext();
		if(context == null) return false;
		return authenticateByAd(context, username, password);
	}

	private boolean authenticateByAd(LdapContext context, final String username, final String password) {
		String searchBase = ",CN=users,DC="+authenticationPattern.getUsername().split("@")[1].replaceAll("\\.", ",DC=");
		try {
			Control[] connCtls = new Control[0];
			context.addToEnvironment(Context.SECURITY_PRINCIPAL, "CN="+username+searchBase);
			context.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			context.reconnect(connCtls);
			return true;
		} catch (NamingException e) {
			log.error(" windows AD authenticate error: ", e);
			return false;
		} finally{
			closeContext(context);
		}
	}

	@Override
	public Hashtable<String, String> getEnvironment() {
		return environment;
	}
	
}
