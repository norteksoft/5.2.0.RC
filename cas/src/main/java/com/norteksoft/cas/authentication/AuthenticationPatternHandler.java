package com.norteksoft.cas.authentication;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class AuthenticationPatternHandler {
	
    private static final String PATTERN_SQL =
    	// sqlserver 使用
    	// "SELECT c.ldap_invocation,c.ldap_type,c.ldap_url,c.ldap_username,c.ldap_password,c.rtx_invocation,c.rtx_url,c.[external], c.external_type,c.external_url " +
    	"SELECT c.ldap_invocation,c.ldap_type,c.ldap_url,c.ldap_username,c.ldap_password,c.rtx_invocation,c.rtx_url,c.extern, c.external_type,c.external_url " +
    	"FROM acs_server_config c join acs_user u on c.company_id=u.fk_company_id where u.deleted=0 and u.login_name=?";
    
	@NotNull
    private SimpleJdbcTemplate jdbcTemplate;
    
    @NotNull
    private DataSource dataSource;
    
    public final void setDataSource(final DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }
	
    protected final SimpleJdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    
    protected final DataSource getDataSource() {
        return this.dataSource;
    }
    
    public AuthenticationPattern getAuthenticationPattern(String loginName){
        try {
        	if(isAdmin(loginName)) return new AuthenticationPattern(AuthenticationPattern.Pattern.DATABASE);
        	List<Map<String, Object>> setting = getJdbcTemplate().queryForList(PATTERN_SQL, loginName);
        	if(setting.isEmpty()){
        		return null;
        	}
        	Map<String, Object> prmt = setting.get(0);
        	Object ldapInvocation = prmt.get("ldap_invocation");
        	Object rtxInvocation = prmt.get("rtx_invocation");
        	Object external = prmt.get("extern");
        	if(getBoolean(ldapInvocation)){ // ldap 认证
        		return createLdapPattern(prmt);
        	}else if(getBoolean(rtxInvocation)){ // rtx 认证
        		return new AuthenticationPattern(
        				AuthenticationPattern.Pattern.RTX,
        				objToString(prmt.get("rtx_url")));
        	}else if(getBoolean(external)){ // 其他方式认证
        		return createExternalPattern(prmt);
        	}
        	return new AuthenticationPattern(AuthenticationPattern.Pattern.DATABASE);
        } catch (final IncorrectResultSizeDataAccessException e) {
        	return new AuthenticationPattern(AuthenticationPattern.Pattern.DATABASE);
        }
    }
    
    private boolean isAdmin(String loginName){
    	return loginName!=null && (
    			loginName.endsWith(".systemAdmin") || 
    			loginName.endsWith(".securityAdmin") || 
    			loginName.endsWith(".auditAdmin"));
    }
    
    private AuthenticationPattern createLdapPattern(Map<String, Object> prmt){
    	AuthenticationPattern.Pattern pattern = null;
    	String type = objToString(prmt.get("ldap_type"));
    	String cn = objToString(prmt.get("ldap_username"));
    	if("APACHE".equals(type)){
    		pattern = AuthenticationPattern.Pattern.LDAP;
    		cn = "UID="+cn+",OU=system";
    	}else if("DOMINO".equals(type)){
    		pattern = AuthenticationPattern.Pattern.DOMINO;
    		cn = "cn="+cn;
    	}else if("WINDOWS_AD".equals(type)){
    		pattern = AuthenticationPattern.Pattern.WINDOWS_AD;
    	}
		return new AuthenticationPattern(pattern,
				objToString(prmt.get("ldap_url")), 
				cn, objToString(prmt.get("ldap_password")));
    }
    
    private AuthenticationPattern createExternalPattern(Map<String, Object> prmt){
    	String type = objToString(prmt.get("external_type"));
    	String url = objToString(prmt.get("external_url"));
    	if("HTTP".equals(type)){
    		return new AuthenticationPattern(AuthenticationPattern.Pattern.HTTP, url);
    	}else if("RESTFUL".equals(type)){
    		return new AuthenticationPattern(AuthenticationPattern.Pattern.RESTFUL, url);
    	}else {
    		return new AuthenticationPattern(AuthenticationPattern.Pattern.WEBSERVICE, url);
    	}
    }
    
    private boolean getBoolean(Object obj){
    	if(obj instanceof Number){
    		return ((Number)obj).intValue()==1;
    	}else if(obj instanceof Boolean){
    		return (Boolean)obj;
    	}
    	return false;
    }
    
    private String objToString(Object obj){
    	if(obj == null) return "";
    	return obj.toString();
    }
}
