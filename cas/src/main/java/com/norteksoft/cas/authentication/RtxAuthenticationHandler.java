package com.norteksoft.cas.authentication;

import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

/**
 * RTX 认证方式
 * @author xiao
 *
 * 2012-7-30
 */
public class RtxAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler{

	private AuthenticationPattern authenticationPattern;
	
	public RtxAuthenticationHandler(){super();}
	
	public RtxAuthenticationHandler(AuthenticationPattern authenticationPattern){
		super();
		this.authenticationPattern = authenticationPattern;
	}
	
	@Override
	protected boolean authenticateUsernamePasswordInternal(
			UsernamePasswordCredentials credentials)
			throws AuthenticationException {
		log.error(" not implemented ... ... ... ... rtx ", authenticationPattern.getUrl());
		return false;
	}

}
