package com.norteksoft.acs.service.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.portal.service.IndexManager;
import com.norteksoft.product.util.ContextUtils;

/**
 * SpringSecurity中接口UserDetailsService的实现类，
 * 根据客户端提供的用户登陆名返回用户的详细信息(含用户所具有的权限)
 * 
 * @author xiaoj
 */
public class UserDetailServiceImpl implements UserDetailsService {
	private Log log = LogFactory.getLog(UserDetailServiceImpl.class);
	private UserManager userManager;
	private CompanyManager companyManager;
	private StandardRoleManager standardRoleManager;
	private IndexManager indexManager;
	private boolean isTenant;
	private String defaultTheme = "black";
	
	public UserDetails loadUserByUsername(String loginName)
			throws UsernameNotFoundException, DataAccessException {
		log.debug("*** Received parameter: loginName:" + loginName);
		
		User user = userManager.getUserByLoginName(loginName);
		
//		try {
//			Map<String, String> licence = License.getLicense();
//			String dateString = licence.get("end_time");
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			Date settingDate = df.parse(dateString);
//			Date date = userManager.getUserTs(user.getCompanyId());
//			if(date != null && settingDate.before(date)){
//				log.error("licence invalidation");
//				throw new UsernameNotFoundException("licence invalidation");
//			}
//		} catch (Exception e) {
//			log.error("licence invalidation");
//			throw new UsernameNotFoundException("licence invalidation");
//		}
		
		if(isTenant){
			isCompanyValidDate(user);
		}
		Company company = companyManager.getCompany(user.getCompanyId());
		List<GrantedAuthority> authsList = getAuthorityByUser(user);
		
		log.debug("*** loadUserByUsername 结束");
		return createUserdetail(user, company, authsList);	
	}
	
	/*
	 * 验证公司订单日期
	 * @param user
	 */
	void isCompanyValidDate(User user){
		if(user == null) throw new UsernameNotFoundException("username does not exist");
		if(!companyManager.isCompanyValidDate(
				user.getCompanyId(), ContextUtils.getSystemId("portal")))
			throw new UsernameNotFoundException("company valid date");
	}
	
	/*
	 * 获取用户权限
	 */
	private List<GrantedAuthority> getAuthorityByUser(User user){
		log.debug("*** getAuthorityByUser 开始");
		
		List<GrantedAuthority> authsList = null;
		Set<Role> userRoles = standardRoleManager.getAllRolesByUser(user.getId(), user.getCompanyId());
		
		StringBuilder roles = new StringBuilder(",");
		for(Role role : userRoles){
			roles.append(role.getCode()).append(",");
		}
		user.setRoleCodes(roles.toString());
		
		Set<Function> functions =  standardRoleManager.getFunctionsByRoles(userRoles);
		if(isTenant){//如果用户属于租户，则只授权用户角色所具有的并且该用户所在的该租户已经购买的功能
			//查询该用户所属租户购买的所有功能
			//如果该角色的权限包含在购买的功能内，则授权该功能
			//FIXME 需要判断是否是租户时的权限分配
			
			authsList = getAuthorityFromFunctions(functions);
			
		}else{//否则授权角色所具有的全部功能
			
			authsList = getAuthorityFromFunctions(functions);
		}
		log.debug("*** getAuthorityByUser 结束");
		
		authsList = DynamicAuthority.getLogAuthority(authsList, roles);
		
		return authsList;
	}
	
	/*
	 * 创建spring security使用的用户
	 */
	private com.norteksoft.acs.entity.security.User createUserdetail(User user, Company company, List<GrantedAuthority> authsList){
		com.norteksoft.acs.entity.security.User userdetail = 
			new com.norteksoft.acs.entity.security.User(user.getId(), user.getLoginName(), user.getPassword(), user.getEmail(),
					user.getEnabled(), !user.getAccountExpired(), true, !user.getAccountLocked(),
					authsList.toArray(new GrantedAuthority[authsList.size()]),
					company.getId(), company.getCode(), company.getName(), user.getSecretGrade());
		userdetail.setHonorificTitle(user.getHonorificName());
		userdetail.setTrueName(user.getName());
		userdetail.setRoleCodes(user.getRoleCodes());
		String theme = indexManager.getThemeByUser(user.getId(), company.getId());
		if(StringUtils.isEmpty(theme)) theme = getDefaultTheme();
		userdetail.setTheme(theme);
		return userdetail;
	}
	
	/*
	 * 从功能集合中封装权限
	 */
	private List<GrantedAuthority> getAuthorityFromFunctions(Collection<Function> functions){
		List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
		for(Function function : functions){
			authorityList.add(new GrantedAuthorityImpl(function.getCode()));
		}
		return authorityList;
	}

	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
	
	@Required
	public void setStandardRoleManager(StandardRoleManager standardRoleManager) {
		this.standardRoleManager = standardRoleManager;
	}
	
	@Required
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public boolean isTenant() {
		return isTenant;
	}

	public void setIsTenant(boolean isTenant) {
		this.isTenant = isTenant;
	}

	public String getDefaultTheme() {
		return defaultTheme;
	}

	public void setDefaultTheme(String defaultTheme) {
		this.defaultTheme = defaultTheme;
	}
}
