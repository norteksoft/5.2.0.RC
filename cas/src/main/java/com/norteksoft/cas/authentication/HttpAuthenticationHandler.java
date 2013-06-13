package com.norteksoft.cas.authentication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

/**
 * HTTP 认证方式
 * @author xiao
 *
 * 2012-7-30
 */
public class HttpAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler{

	private AuthenticationPattern authenticationPattern;
	
	public HttpAuthenticationHandler(){ super();}
	
	public HttpAuthenticationHandler(AuthenticationPattern authenticationPattern){
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
		DefaultHttpClient httpClient = new DefaultHttpClient();
		
		log.debug(" http authenticate url:  ", url);
		
		try {
			HttpPost postRequest = new HttpPost(url);
			postRequest.addHeader("Charset", "UTF-8");
			postRequest.addHeader("Content-Type", "text/plain");
			
			AbstractHttpEntity input = new StringEntity("username="+username+"&password="+password);
			input.setContentType("text/plain;charset=UTF-8");
			postRequest.setEntity(input);
			
			HttpResponse response = httpClient.execute(postRequest);
			
			if (response.getStatusLine().getStatusCode() != 200) {
				return false;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			String output = br.readLine();
			log.debug(" http authentication result: " + output);
			if("1".equals(output)){
				return true;
			}else if("2".equals(output)){ // 用户锁定
				throw new BadCredentialsAuthenticationException("error.authentication.user.locked");
			}else if("3".equals(output)){ // 用户名或密码错误
				throw new BadCredentialsAuthenticationException("error.authentication.username.or.password.error");
			}
			return false;
			
		} catch (BadCredentialsAuthenticationException e) {
			throw e;
		} catch (Exception e) {
			log.error(" http authenticate faild ", e);
		} finally{
			httpClient.getConnectionManager().shutdown();
		}
		return false;
	}

}
