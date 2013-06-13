package com.norteksoft.cas.authentication;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

/**
 * webservice 认证方式
 * @author xiao
 *
 * 2012-8-14
 */
public class WebserviceAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler{

	private AuthenticationPattern authenticationPattern;
	
	public WebserviceAuthenticationHandler(){ super();}
	
	public WebserviceAuthenticationHandler(AuthenticationPattern authenticationPattern){
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
	
	private boolean authenticate(String url, String username, String password) throws AuthenticationException {
		log.debug(" http authenticate url:  ", url);
		if(!url.contains("?wsdl")) url = url+"?wsdl";
		JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
		Client client = factory.createClient(url);
		Object[] result = null;
		try {
			result = client.invoke("authenticate", username, password);
			if(result != null && result.length == 1){
				Object auth = result[0];
				if("1".equals(auth.toString())){
					return true;
				}else if("2".equals(auth.toString())){ // 用户锁定
					throw new BadCredentialsAuthenticationException("error.authentication.user.locked");
				}else if("3".equals(auth.toString())){ // 用户名或密码错误
					throw new BadCredentialsAuthenticationException("error.authentication.username.or.password.error");
				}
			}
		} catch (Exception e) {
			log.error(" webservice authenticate faild ", e);
		} finally {
			client.destroy();
		}
		return false;
	}

}
