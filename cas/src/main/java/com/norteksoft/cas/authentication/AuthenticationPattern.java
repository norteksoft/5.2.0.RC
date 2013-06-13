package com.norteksoft.cas.authentication;

public class AuthenticationPattern {

	private Pattern pattern;
	private String url;
	private String username;
	private String password;
	
	public AuthenticationPattern(Pattern pattern){
		this.pattern = pattern;
	}
	
	public AuthenticationPattern(Pattern pattern, String url){
		this.pattern = pattern;
		this.url = url;
	}
	
	public AuthenticationPattern(Pattern pattern, String url, String username, String password){
		this(pattern, url);
		this.username = username;
		this.password = password;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	static enum Pattern {
		DATABASE, LDAP, WINDOWS_AD, DOMINO, RTX, HTTP, RESTFUL, WEBSERVICE
	}

}
