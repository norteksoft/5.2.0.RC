package com.norteksoft.cas.authentication;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jasig.cas.authentication.AbstractAuthenticationManager;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.UnsupportedCredentialsException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public final class AuthenticationManagerImpl extends AbstractAuthenticationManager {

	@NotNull
    @Size(min=1)
    private List<AuthenticationHandler> authenticationHandlers;
	
	@NotNull
    @Size(min=1)
    private List<CredentialsToPrincipalResolver> credentialsToPrincipalResolvers;
	
	@NotNull
	private AuthenticationPatternHandler authenticationPatternHandler;
	
	@Override
	protected Pair<AuthenticationHandler, Principal> authenticateAndObtainPrincipal(
			Credentials credentials) throws AuthenticationException {
		boolean foundSupported = false;
        boolean authenticated = false;
        AuthenticationHandler authenticatedClass = null;
        
        String username = "";
        if(credentials instanceof UsernamePasswordCredentials){
        	username = ((UsernamePasswordCredentials)credentials).getUsername();
        }
        
        // List<AuthenticationHandler> handlers = authenticationHandlers;
        List<AuthenticationHandler> handlers = null;
        if(username == null){
        	handlers = authenticationHandlers;
        }else{
        	handlers = updateAuthenticationHandlers(username);
        }
        if(handlers == null){ // 用户名不存在
        	throw new BadCredentialsAuthenticationException("error.authentication.username.not.found");
        }
        
        for (final AuthenticationHandler authenticationHandler : handlers) {
            if (authenticationHandler.supports(credentials)) {
                foundSupported = true;
                if (!authenticationHandler.authenticate(credentials)) {
                    if (log.isInfoEnabled()) {
                        log.info("AuthenticationHandler: "
                                + authenticationHandler.getClass().getName()
                                + " failed to authenticate the user which provided the following credentials: "
                                + credentials.toString());
                    }
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("AuthenticationHandler: "
                                + authenticationHandler.getClass().getName()
                                + " successfully authenticated the user which provided the following credentials: "
                                + credentials.toString());
                    }
                    authenticatedClass = authenticationHandler;
                    authenticated = true;
                    break;
                }
            }
        }

        if (!authenticated) {
            if (foundSupported) {
                throw BadCredentialsAuthenticationException.ERROR;
            }

            throw UnsupportedCredentialsException.ERROR;
        }

        foundSupported = false;

        for (final CredentialsToPrincipalResolver credentialsToPrincipalResolver : this.credentialsToPrincipalResolvers) {
            if (credentialsToPrincipalResolver.supports(credentials)) {
                final Principal principal = credentialsToPrincipalResolver
                    .resolvePrincipal(credentials);
                foundSupported = true;
                if (principal != null) {
                    return new Pair<AuthenticationHandler,Principal>(authenticatedClass, principal);
                }
            }
        }

        if (foundSupported) {
            if (log.isDebugEnabled()) {
                log.debug("CredentialsToPrincipalResolver found but no principal returned.");
            }

            throw BadCredentialsAuthenticationException.ERROR;
        }

        log.error("CredentialsToPrincipalResolver not found for " + credentials.getClass().getName());
        throw UnsupportedCredentialsException.ERROR;
	}
	
	private List<AuthenticationHandler> updateAuthenticationHandlers(String loginName){
		AuthenticationPattern pattern = authenticationPatternHandler.getAuthenticationPattern(loginName);
		if(pattern == null) return null;
		AuthenticationHandler handler = AuthenticationHandlerFactory.getAuthenticationHandler(
				authenticationPatternHandler.getJdbcTemplate(), pattern);
		if(handler == null){
			return authenticationHandlers;
		}else{
			List<AuthenticationHandler> handlers = new ArrayList<AuthenticationHandler>();
			handlers.add(authenticationHandlers.get(0));
			handlers.add(handler);
			return handlers;
		}
	}
	
	public void setAuthenticationPatternHandler(
			AuthenticationPatternHandler authenticationPatternHandler) {
		this.authenticationPatternHandler = authenticationPatternHandler;
	}
	
	public void setAuthenticationHandlers(
        final List<AuthenticationHandler> authenticationHandlers) {
        this.authenticationHandlers = authenticationHandlers;
    }

    public void setCredentialsToPrincipalResolvers(
        final List<CredentialsToPrincipalResolver> credentialsToPrincipalResolvers) {
        this.credentialsToPrincipalResolvers = credentialsToPrincipalResolvers;
    }
    
}
