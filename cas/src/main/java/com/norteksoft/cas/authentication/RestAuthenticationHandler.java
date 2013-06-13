package com.norteksoft.cas.authentication;

import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * RESTful 认证方式
 * @author xiao
 *
 * 2012-8-14
 */
public class RestAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler{

	private AuthenticationPattern authenticationPattern;
	
	public RestAuthenticationHandler(){ super();}
	
	public RestAuthenticationHandler(AuthenticationPattern authenticationPattern){
		super();
		this.authenticationPattern = authenticationPattern;
	}
	
	@Override
	protected boolean authenticateUsernamePasswordInternal(
			UsernamePasswordCredentials credentials)
			throws AuthenticationException {
		
		final String username = getPrincipalNameTransformer().transform(credentials.getUsername());
        final String password = credentials.getPassword();
        
        return authenticate(authenticationPattern.getUrl(), username, password);
	}
	
	private boolean authenticate(String url,String username, String password) throws AuthenticationException{
		log.debug(" RESTful url:  ", url);
		
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(url); 
		ClientResponse cr = service.entity("username="+username+"&password="+password, "text/html;charset=UTF-8")
				.accept("text/html").post(ClientResponse.class);
		String result = cr.getEntity(String.class);
		
		log.debug(" RESTful result:  ", result);
		
		if("1".equals(result)){
			return true;
		}else if("2".equals(result)){ // 用户锁定
			throw new BadCredentialsAuthenticationException("error.authentication.user.locked");
		}else if("3".equals(result)){ // 用户名或密码错误
			throw new BadCredentialsAuthenticationException("error.authentication.username.or.password.error");
		}
		
		cr.close();
		client.destroy();
		
		return false;
	}

}
