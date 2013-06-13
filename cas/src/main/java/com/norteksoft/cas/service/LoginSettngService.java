package com.norteksoft.cas.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.jasig.cas.util.PropUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class LoginSettngService {
	
	private final static String SETTING_SQL = "select s.value,s.fail_set_type,s.locked_time " +
			"from acs_security_setting s join acs_user u on s.fk_company_id=u.fk_company_id " +
			"where s.name='login-security' and s.deleted=0 and u.login_name=?";
	
	private final static String LOCK_USER_SQL = "update acs_user set account_locked=? where login_name=?";
	private final static String IS_LOCKED_SQL = "select account_locked from acs_user where login_name=? and deleted=0";
	private final static String COMPANY_ID_SQL = "select fk_company_id from acs_user where login_name=? and deleted=0";
	private final static String USER_ENABLE_SQL = "select enabled from acs_user where login_name=? and deleted=0";
	
	protected final Logger log = LoggerFactory.getLogger(LoginSettngService.class);
	
	@NotNull
    private SimpleJdbcTemplate jdbcTemplate;
    
    @NotNull
    private DataSource dataSource;
    
    /**
     * 锁定用户
     * @param username
     */
    public void lockUser(String username){
    	jdbcTemplate.update(LOCK_USER_SQL, 1, username);
    }
    
    /**
     * 解锁用户
     * @param username
     */
    public void unlockUser(String username){
    	jdbcTemplate.update(LOCK_USER_SQL, 0, username);
    }
    
    /**
     * 根据用户名查询公司id
     * @param username
     * @return
     */
    public Long getCompanyId(String username){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(COMPANY_ID_SQL, username);
    	if(list.isEmpty()) return null;
    	Object obj = list.get(0).get("fk_company_id");
    	return Long.valueOf(obj.toString());
    }
    /**
     * 根据用户名查询用户是否被禁用
     * @param username
     * @return
     */
    public Boolean getUserEnabled(String username){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(USER_ENABLE_SQL, username);
    	if(list.isEmpty()) return null;
    	Object obj = list.get(0).get("enabled");
    	String database = PropUtils.getDataBase();
    	if(PropUtils.DATABASE_ORACLE.equals(database)||PropUtils.DATABASE_SQLSERVER.equals(database)){//oracle和sqlserver时
    		if(obj.toString().equals("1")){
    			return true;
    		}else{
    			return false;
    		}
    	}else{//mysql时
    		return Boolean.valueOf(obj.toString());
    	}
    }
    
    public Date getLastTime(Long companyId) {
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(
    			"select max(login_time) maxdate from acs_login_log where fk_company_id=?", companyId);
    	if(list.isEmpty()) return new Date();
        return (Date) list.get(0).get("maxdate");
    }
    
    /**
     * 用户是否已经锁定
     * @param username
     * @return null,不存在
     */
    public Boolean isUserLocked(String username){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(IS_LOCKED_SQL, username);
    	if(list.isEmpty()) return null;
    	Object obj = list.get(0).get("account_locked");
    	return getBoolean(obj);
    }
    
    private boolean getBoolean(Object obj){
    	if(obj instanceof Number){
    		return ((Number)obj).intValue()==1;
    	}else if(obj instanceof Boolean){
    		return (Boolean)obj;
    	}
    	return false;
    }
    
    /**
     * 用户是否已经解锁
     * @return
     */
    public boolean isUserUnlock(String username){
    	return !jdbcTemplate.queryForObject(IS_LOCKED_SQL, Boolean.class, username);
    }
    
    /**
     * 查询登陆设置
     * @param username
     * @return KEY:[value: 允许失败次数; fail_set_type: 失败后设置(0,验证码; 1,锁定用户); locked_time:锁定时间(分钟);]
     */
    public Map<String, Object> getSecuritySetting(String username){
    	Map<String, Object> result = new HashMap<String, Object>();
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(SETTING_SQL, username);
    	if(list.isEmpty()){
        	result.put("value", 3);
        	result.put("fail_set_type", 0);
        	result.put("locked_time", 30);
    	}else{
        	result.put("value", list.get(0).get("value"));
        	result.put("fail_set_type", list.get(0).get("fail_set_type"));
        	result.put("locked_time", list.get(0).get("locked_time"));
    	}
    	return result;
    }
    
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
    
    /**
     * 记录用户登录日志
     * @param companyId
     * @param username
     */
    public void loginLog(Long companyId, String username, String ip){
    	Object[] user = getUser(username);
    	Long id = null;
    	try {
			String rawUrl = getJdbcUrl();
			if(rawUrl.startsWith("jdbc:oracle:")){
				id = jdbcTemplate.queryForLong("select hibernate_sequence.nextval from dual");
			}
			if(id == null){
				jdbcTemplate.update(LOGIN_LOG_SQL, false, new Date(),false, companyId, ip, new Date(), user[0], user[1], getUserType(username));
			}else{
				jdbcTemplate.update(LOGIN_LOG_SQL_CONTAINS_ID, id, false, new Date(),false, companyId, ip, new Date(), user[0], user[1], getUserType(username));
			}
    	} catch (Exception e) {
			log.error("get datasource metadata error or query oracle sequence error.", e);
		}
    }
    
    /**
     * 返回值根据角色参考ACS系统中的  com.norteksoft.acs.base.enumeration.OperatorType
     * @param username
     * @return
     */
    private Integer getUserType(String username){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(USER_ROLES_SQL, username, false);
    	for(Map<String, Object> map : list){
    		if(SYSTEM_ADMIN.equals(map.get("code"))) return 1;
    		if(SECURITY_ADMIN.equals(map.get("code"))) return 2;
    		if(AUDIT_ADMIN.equals(map.get("code"))) return 3;
    	}
    	return 0;
    }
    
    /**
     * 查询用户ID和用户姓名
     * @param username
     * @return
     */
    private Object[] getUser(String username){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(USER_SQL, username);
    	Object[] result = new Object[2];
    	result[0] = list.get(0).get("id");
    	result[1] = list.get(0).get("name");
    	return result;
    }
    
	private final static String SYSTEM_ADMIN = "acsSystemAdmin";
	private final static String SECURITY_ADMIN = "acsSecurityAdmin";
	private final static String AUDIT_ADMIN = "acsAuditAdmin";
	private final static String USER_SQL = "select id, name from acs_user where login_name=? and deleted=0";
	private final static String LOGIN_LOG_SQL = 
		"insert into acs_login_log(deleted, ts, admin_log, fk_company_id, ip_address, login_time," +
		"user_id, user_name, operator_type) values(?,?,?,?,?,?,?,?,?)";
	private final static String LOGIN_LOG_SQL_CONTAINS_ID = 
		"insert into acs_login_log(id, deleted, ts, admin_log, fk_company_id, ip_address, login_time," +
		"user_id, user_name, operator_type) values(?,?,?,?,?,?,?,?,?,?)";
	private final static String USER_ROLES_SQL = "SELECT acs_role.code FROM acs_role " +
			"join acs_role_user on acs_role.id=acs_role_user.fk_role_id " +
			"join acs_user on acs_role_user.fk_user_id=acs_user.id where acs_user.login_name=? and acs_user.deleted=?";
	
	private static String JDBC_URL;
	
	static String getJdbcUrl(){
		if(JDBC_URL == null){
			JDBC_URL = getProp("hibernate.connection.url");
		}
		return JDBC_URL;
	}
	
	private static String getProp(String key){
		Properties propert = new Properties();
		try {
			propert.load(LoginSettngService.class.getClassLoader().getResourceAsStream("cas.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return propert.getProperty(key);
	}
}
