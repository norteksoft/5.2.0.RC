package com.norteksoft.cas.authentication;

import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class AuthenticationHandlerFactory {

	public static AuthenticationHandler getAuthenticationHandler(SimpleJdbcTemplate simpleJdbcTemplate, AuthenticationPattern pattern){
		
		switch(pattern.getPattern()){
		case LDAP: 
		case DOMINO : return new LdapAuthenticationHandler(pattern).setJdbcTemplate(simpleJdbcTemplate);
		case WINDOWS_AD: return new AdAuthenticationHandler(pattern).setJdbcTemplate(simpleJdbcTemplate);
		case RTX: return new RtxAuthenticationHandler(pattern);
		case HTTP: return new HttpAuthenticationHandler(pattern);
		case RESTFUL: return new RestAuthenticationHandler(pattern);
		case WEBSERVICE: return new WebserviceAuthenticationHandler(pattern);
		}
		return null;
		
	}
}
