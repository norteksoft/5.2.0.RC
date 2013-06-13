package com.norteksoft.acs.service.syssetting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.sysSetting.SecuritySetting;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.product.util.ContextUtils;

/**
 *系统参数设置接口
 * 
 * @author 陈成虎 2009-3-2上午11:52:40
 */
@SuppressWarnings("deprecation")
@Service
@Transactional
public class SecuritySetManager {

	private static String SYSTEMADMIN = "SystemAdmin";
	private static String SECURITYADMIN = "SecurityAdmin";
	private static String AUDITADMIN = "AuditAdmin";
	private static String COMPANYID = "companyId";
	private static String DELETED = "deleted";
	private static String SECURITYNAME ="name";
	private static String LOGINTIMEOUTS = "loginTimeouts";
	private static String LOGIN_SECURITY = "login-security";
	private static String  MINUTE = "分钟]";
	
	private SimpleHibernateTemplate<SecuritySetting, Long> securitySetDao;
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<User, Long> userDao;
	
	private SecuritySetting adminPassOver;
	private SecuritySetting userPassOver;
	private SecuritySetting passNotice;
	private List<Role> roleList;
	private LogUtilDao logUtilDao;
	private Long companyId;
	private Integer defaultAdminOverdueDays;
	private Integer defaultGeneralOverdueDays;
	private static String ACS = "acs";

	@Autowired
	private AcsUtils acsUtils;

	public Long getSystemIdByCode(String code) {
	   return acsUtils.getSystemsByCode(code).getId();
	}

	public Long getCompanyId() {
		if (companyId == null) {
			return ContextUtils.getCompanyId();
		} else
			return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		securitySetDao = new SimpleHibernateTemplate<SecuritySetting, Long>(
				sessionFactory, SecuritySetting.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory,
				Role.class);
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory,
				User.class);
		logUtilDao = new LogUtilDao(sessionFactory);
	}

	public void save(SecuritySetting entity) {
		securitySetDao.save(entity);
	}
	
	public void save(List<SecuritySetting> entitys) {
		for (SecuritySetting entity : entitys) {
			securitySetDao.save(entity);
		}
	}
	
	public List<SecuritySetting> getSecuritySetList(){
		List<SecuritySetting> list  = securitySetDao.findByCriteria(Restrictions.eq(COMPANYID,
				getCompanyId()));
		return list;
	}

	public String getPassWorkRule(SecuritySetting entity) {
		// (?=(.*[A-Z]){1,}) (?=(.*[a-z]){1,}) (?=(.*\d){1,}) (?=(.*\W){1,})
		StringBuilder magess = new StringBuilder();
		String value = entity.getValue();
		if (value != null && !"".equals(value.trim())) {

			if (value.indexOf("(?=(.*[A-Z]){1,})") > -1) {
				magess.append("大写字母");
				magess.append(",");
			}
			if (value.indexOf("(?=(.*[a-z]){1,})") > -1) {
				magess.append("小写字母");
				magess.append(",");
			}
			if (value.indexOf("(?=(.*\\d){1,})") > -1) {
				magess.append("数字");
				magess.append(",");
			}
			if (value.indexOf("(?=(.*\\W){1,})") > -1) {
				magess.append("特殊符号");
				magess.append(",");
			}
			if (getPassWordLength(entity) != null) {
				magess.append("密码长度是" + getPassWordLength(entity));
				magess.append(",");
			}
			magess.deleteCharAt(magess.length() - 1);
		}

		return magess.toString();
	}

	@Transactional(readOnly = true)
	public SecuritySetting getSecuritySetById(Long id) {
		return securitySetDao.get(id);
	}

	public SecuritySetting getSecuritySetByName(String securityName, String regex) {

		List<SecuritySetting> list = securitySetDao.findByCriteria(Restrictions.eq(
				SECURITYNAME, securityName), Restrictions.eq(COMPANYID,
				getCompanyId()));
		if (list.isEmpty()) {
			insert(securityName, regex);
			list = securitySetDao.findByCriteria(Restrictions.eq(
					SECURITYNAME, securityName), Restrictions.eq(COMPANYID,
					getCompanyId()));
			return list.get(0);
		} else {
			return list.get(0);
		}

	}

	public void insert(String securityName, String regex) {
		SecuritySetting entity = new SecuritySetting();
		entity.setName(securityName);
		if (regex != null) {
			entity.setValue(regex);
			entity.setCompanyId(getCompanyId());
			save(entity);
		}else{
			entity.setValue("10");
			entity.setCompanyId(getCompanyId());
			save(entity);
		}
		
	}

	public String getPassWordLength(SecuritySetting security) {
		//if (security == null || "".equals(security))
		if (security == null)
			return null;
		if (security.getValue() == null
				|| "".equals(security.getValue()))
			return null;
		String[] rule = security.getValue().split(",");
		Pattern p = Pattern.compile("\\d");
		Matcher m = p.matcher(rule[rule.length - 1].trim());
		if (m.find()) {

			return rule[rule.length - 1].trim();
		}
		return null;
	}

	public void writeLog(String name) {
	}

	/**
	 * 读取登陆时间
	 * 
	 * @param companyId
	 * @return
	 */
	public Integer getLoginTimeoutValues(Long companyId) {
		List<SecuritySetting> list = securitySetDao.findByCriteria(Restrictions.eq(
				COMPANYID, companyId), Restrictions.eq(SECURITYNAME,
				LOGINTIMEOUTS));
		SecuritySetting entity;
		if (!list.isEmpty()) {
			entity = list.get(0);
			if (entity.getValue() != null
					&& entity.getValue().trim().length() > 0)
				return Integer.valueOf(entity.getValue());
		}

		return null;
	}

	/**
	 * 读取系统参数 登陆安全设置
	 * 
	 * @param companyId
	 * @return SecuritySet的securityValue属性是登陆失败次数
	 *         SecuritySet的unblockTime属性是自动解锁时间
	 */
	protected SecuritySetting getSystemValues(Long companyId) {
		return getSystemValues(companyId, LOGIN_SECURITY);
	}
	
	
	protected SecuritySetting getSystemValues(Long companyId, String name) {
		List<SecuritySetting> list = securitySetDao.findByCriteria(
				Restrictions.eq(COMPANYID, companyId), 
				Restrictions.eq(SECURITYNAME, name),
				Restrictions.eq(DELETED, false));
		SecuritySetting entity;
		if (!list.isEmpty()) {
			entity = list.get(0);
			return entity;
		}
		return null;
	}
	
	/**
	 * 读取密码是否过期
	 * 
	 * @return null 为密码没有过期 Integer 密码还有几天过期 0 密码过期
	 */
	public Integer getPasswordIsOverdue(Long userId, Long companyId){
		User user = userDao.get(userId);
		return getPasswordIsOverdue(user.getUserInfo(), companyId);
	}

	public Integer getPasswordIsOverdue(UserInfo ui, Long companyId) {

		searchSystemValues(companyId);
		List<Role> roleList = getAdminRole();
		//User user = userDao.get(userId);

		boolean isAdminOrUser = isAdminRole(ui.getUser(), roleList);
		Calendar cal = Calendar.getInstance();
		Date date = getNewDate(cal);// 当前时间没有秒的
		Date validityDate = null;// 密码有效时间
		Date passNoticeDate = null;// 密码通知时间
		cal.clear();
		cal.setTime(ui.getPasswordUpdatedTime());
		cal.setTime(getNewDate(cal));// 密码创建时间

		if (isAdminOrUser) {// 如果是管理员角色
			if (adminPassOver != null
					&& !"".equals(adminPassOver.getValue().trim())) {
				cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(adminPassOver
						.getValue().trim()));// 管理员密码过期时间
				validityDate = cal.getTime();
				if (date.compareTo(validityDate) >= 0) {// 判断密码是否过期
					return 0;
				} else {// 没有过期
					cal.clear();
					cal.setTime(ui.getPasswordUpdatedTime());
					cal.setTime(getNewDate(cal));
					if (passNotice != null
							&& !"".equals(passNotice.getValue().trim()))
						cal.add(Calendar.DAY_OF_MONTH, Integer
								.parseInt(adminPassOver.getValue()
										.trim())
								- Integer.parseInt(passNotice
										.getValue().trim()));
					passNoticeDate = cal.getTime();// 密码通知日期
					if (date.compareTo(passNoticeDate) >= 0) {// 判断密码是否提前通知
						Long day = (validityDate.getTime() - date.getTime())
								/ (24 * 60 * 60 * 1000);// 密码通知过期天数
						return Integer.valueOf(String.valueOf(day));
					}
				}
			}
		} else {// 如果是普通用户
			if (userPassOver != null
					&& !"".equals(userPassOver.getValue().trim())) {
				cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(userPassOver
						.getValue().trim()));
				validityDate = cal.getTime();
				if (date.compareTo(validityDate) >= 0) {
					return 0;
				} else {
					cal.clear();
					cal.setTime(ui.getPasswordUpdatedTime());
					cal.setTime(getNewDate(cal));
					if (passNotice != null
							&& !"".equals(passNotice.getValue().trim()))
						cal.add(Calendar.DAY_OF_MONTH, Integer
								.parseInt(userPassOver.getValue()
										.trim())
								- Integer.parseInt(passNotice
										.getValue().trim()));
					passNoticeDate = cal.getTime();
					if (date.compareTo(passNoticeDate) >= 0) {// 判断密码是否提前通知
						Long day = (validityDate.getTime() - date.getTime())
								/ (24 * 60 * 60 * 1000);
						return Integer.valueOf(String.valueOf(day));
					}
				}
			}
		}

		return null;
	}

	public void searchSystemValues(Long companyId) {
			adminPassOver = getSystemValues(companyId, "admin-password-overdue");
			userPassOver = getSystemValues(companyId, "user-password-overdue");
			passNotice = getSystemValues(companyId, "password-over-notice");
	}

	/**
	 * 返回没有秒的时间
	 * 
	 * @return
	 */
	public Date getNewDate(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		cal.clear();
		cal.set(year, month, day);
		return cal.getTime();
	}

	/**
	 * 查出管理员角色的数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getAdminRole() {
		if (roleList == null) {
			String hql = " from Role role where role.code like ? or role.code like ? or role.code like ? and deleted=?";
			roleList = roleDao.find(hql, "%" + SYSTEMADMIN,
					"%" + SECURITYADMIN, "%" + AUDITADMIN, false);
			return roleList;
		}
		return roleList;
	}

	/**
	 * 判断用户是否是管理员角色
	 * 
	 * @param user
	 * @param roleList
	 * @return
	 */
	public boolean isAdminRole(User user, List<Role> roleList) {
		boolean temp = false;
		Set<Role> userRoles=getRolesByUserNew(user.getId());
		for (Role role : roleList) {
			/*for (RoleUser roleU : user.getRoleUser()) {
				if(!roleU.isDeleted()){
				if (!roleU.getRole().isDeleted()&&role.getId().equals(roleU.getRole().getId())) {// 判断当前用户是否是管理员
					temp = true;
					return temp;
				}
				}
			}*/ 
			for(Role r:userRoles){
				if(role.getId().equals(r.getId())){
					temp = true;
					return temp;
				}
		    }
		}

		return temp;
	}

	/**
	 * 根据用户ID查询用户所有的角色
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesByUser(Long userId){
		StringBuilder rolesByUserHql = new StringBuilder();
		rolesByUserHql.append("select r from User u join u.roleUsers ru join ru.role r ");
		rolesByUserHql.append("where u.deleted=? and ru.deleted=? and r.deleted=?  and u.id=?");
		List<Role> userRoles = roleDao.find(rolesByUserHql.toString(), false, false, false, ContextUtils.getSystemId(), userId);
		
		StringBuilder rolesByDepartmentHql = new StringBuilder();
		rolesByDepartmentHql.append("select r from User u join u.departmentUsers du join du.department d join d.roleDepartments rd join rd.role r ");
		rolesByDepartmentHql.append("where u.deleted=? and du.deleted=? and d.deleted=? and rd.deleted=? and r.deleted=? and r.businessSystem.id=? and u.id=?");
		List<Role> departmentRoles = roleDao.find(rolesByDepartmentHql.toString(), false, false, false,false, false, ContextUtils.getSystemId(), userId);
		
		StringBuilder rolesByWorkgroupHql = new StringBuilder();
		rolesByWorkgroupHql.append("select r from User u join u.workgroupUsers wu join wu.workgroup w join w.roleWorkgroups rw join rw.role r ");
		rolesByWorkgroupHql.append("where u.deleted=? and wu.deleted=? and w.deleted=? and rw.deleted=? and r.deleted=? and r.businessSystem.id=?  and u.id=?");
		List<Role> workgroupRoles = roleDao.find(rolesByWorkgroupHql.toString(), false, false, false,false, false, ContextUtils.getSystemId(), userId);
		
		Set<Role> roles = new HashSet<Role>();
		roles.addAll(userRoles);
		roles.addAll(departmentRoles);
		roles.addAll(workgroupRoles);
 		return roles;
	}
	/**
	 * 根据用户ID查询用户所有的角色
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesByUserNew(Long userId){
		StringBuilder rolesByUserHql = new StringBuilder();
		rolesByUserHql.append("select r from User u join u.roleUsers ru join ru.role r ");
		rolesByUserHql.append("where u.deleted=? and ru.deleted=? and r.deleted=?  and u.id=?");
		List<Role> userRoles = roleDao.find(rolesByUserHql.toString(), false, false, false,  userId);
		
		StringBuilder rolesByDepartmentHql = new StringBuilder();
		rolesByDepartmentHql.append("select r from User u join u.departmentUsers du join du.department d join d.roleDepartments rd join rd.role r ");
		rolesByDepartmentHql.append("where u.deleted=? and du.deleted=? and d.deleted=? and rd.deleted=? and r.deleted=? and u.id=?");
		List<Role> departmentRoles = roleDao.find(rolesByDepartmentHql.toString(), false, false, false,false, false, userId);
		
		StringBuilder rolesByWorkgroupHql = new StringBuilder();
		rolesByWorkgroupHql.append("select r from User u join u.workgroupUsers wu join wu.workgroup w join w.roleWorkgroups rw join rw.role r ");
		rolesByWorkgroupHql.append("where u.deleted=? and wu.deleted=? and w.deleted=? and rw.deleted=? and r.deleted=?  and u.id=?");
		List<Role> workgroupRoles = roleDao.find(rolesByWorkgroupHql.toString(), false, false, false,false, false, userId);
		
		Set<Role> roles = new HashSet<Role>();
		roles.addAll(userRoles);
		roles.addAll(departmentRoles);
		roles.addAll(workgroupRoles);
 		return roles;
	}
	
	/**
	 * 根据用户ID查询用户所有的角色
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getRolesByUserAndBussinessId(Long userId,Long bussinessId){
		StringBuilder rolesByUserHql = new StringBuilder();
		rolesByUserHql.append("select r from User u join u.roleUsers ru join ru.role r ");
		rolesByUserHql.append("where u.deleted=? and ru.deleted=? and r.deleted=? and r.businessSystem.id=? and u.id=?");
		List<Role> userRoles = roleDao.find(rolesByUserHql.toString(), false, false, false, bussinessId, userId);
		
		StringBuilder rolesByDepartmentHql = new StringBuilder();
		rolesByDepartmentHql.append("select r from User u join u.departmentUsers du join du.department d join d.roleDepartments rd join rd.role r ");
		rolesByDepartmentHql.append("where u.deleted=? and du.deleted=? and d.deleted=? and rd.deleted=? and r.deleted=? and r.businessSystem.id=? and u.id=?");
		List<Role> departmentRoles = roleDao.find(rolesByDepartmentHql.toString(), false, false, false,false, false, bussinessId, userId);
		
		StringBuilder rolesByWorkgroupHql = new StringBuilder();
		rolesByWorkgroupHql.append("select r from User u join u.workgroupUsers wu join wu.workgroup w join w.roleWorkgroups rw join rw.role r ");
		rolesByWorkgroupHql.append("where u.deleted=? and wu.deleted=? and w.deleted=? and rw.deleted=? and r.deleted=? and r.businessSystem.id=?  and u.id=?");
		List<Role> workgroupRoles = roleDao.find(rolesByWorkgroupHql.toString(), false, false, false,false, false, bussinessId, userId);
		
		Set<Role> roles = new HashSet<Role>();
		roles.addAll(userRoles);
		roles.addAll(departmentRoles);
		roles.addAll(workgroupRoles);
		List<Role> r=new ArrayList<Role>();
		r.addAll(roles);
 		return r;
	}
	public SimpleHibernateTemplate<SecuritySetting, Long> getSecuritySetDao() {
		return securitySetDao;
	};
	

	//========================================================================================
	//========================================================================================
	
	
	/**
	 * 获取用户允许失败登陆的次数，若没有设置返回null
	 * @param 公司ID
	 */
	@Transactional(readOnly = true)
	public Integer getLoginFailedCounts(Long companyId) {
		SecuritySetting securitySet = getSystemValues(companyId, LOGIN_SECURITY);
		if (securitySet != null && securitySet.getValue() != null) {
			boolean ietrue = securitySet.getValue().trim().length() > 0;
			if (ietrue) {
				return Integer.valueOf(securitySet.getValue());
			}
		}
		return null;
	}

	/**
	 * 获取管理员密码失效时间
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Integer getAdmainPasswordAgeingDays(Long companyId){
		SecuritySetting securitySet = getSystemValues(companyId, "admin-password-overdue");
		if (securitySet != null && securitySet.getValue() != null) {
			boolean istrue = securitySet.getValue().trim().length() > 0;
			if (istrue) {
				return Integer.valueOf(securitySet.getValue());
			}
		}
		return null;
	}
	
	/**
	 * 获取一般用户密码失效时间
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Integer getGeneralPasswordAgeingDays(Long companyId){
		SecuritySetting securitySet = getSystemValues(companyId, "user-password-overdue");
		if (securitySet != null && securitySet.getValue() != null) {
			boolean istrue = securitySet.getValue().trim().length() > 0;
			if (istrue) {
				return Integer.valueOf(securitySet.getValue());
			}
		}
		return null;
	}
	
	
	/**
	 * 根据公司检查所有可以解锁的用户账户为其解锁
	 * 注意：此方法未作是否已到达解锁时间的判断
	 * @param unclockTime
	 */
	public void unclockUserAccount(Long companyId){
		//解锁条件：用户的锁定标识accountNonLocked=false;登陆失败次数  failedCounts=0;
		StringBuilder hql = new StringBuilder();
		hql.append("update User u set u.accountNonLocked=? ");
		hql.append("where u.deleted=? and u.accountNonLocked=? and u.companyId=? and u.failedCounts=? ");
		userDao.executeUpdate(hql.toString(), true, false, false, companyId, 0);
	}
	
	/**
	 * 根据公司检查所有过期的账户将其设置为过期
	 * @param companyId
	 */
	@SuppressWarnings("unchecked")
	public void expiredUserAccount(Long companyId){
		//用户过期： 将用户过期标识设置为：accountNonExpired=false
		//用户的密码创建时间：passWordCreateTime
		//判断为过期的条件：用户密码的创建时间   + 密码过期的天数 < 当前时间
		StringBuilder queryHql = new StringBuilder();
		queryHql.append("select user from User user ");
		queryHql.append("where user.companyId=? and user.deleted=? and  user.userInfo.passWordCreateTime < ?");
		
		
		//管理员密码过期时间，缺省为90天
		Integer adminDays = getAdmainPasswordAgeingDays(companyId);
		if(adminDays == null) adminDays = this.getDefaultAdminOverdueDays();
		Date adminNow = new Date((new Date()).getTime() - adminDays * 24 * 60 * 60 * 1000L);
		
		
		//普通用户密码过期时间，缺省为90天
		Integer generalDays = getGeneralPasswordAgeingDays(companyId);
		if(generalDays == null) generalDays = this.getDefaultGeneralOverdueDays();
		Date generalNow = new Date((new Date()).getTime() - generalDays * 24 * 60 * 60 * 1000L);
		
		
		//查询所有过期的用户，用已过期天数较小的时间
		Date queryDate = adminNow;
		if(adminDays > generalDays){
			queryDate = generalNow;
		}
		List<User> users = userDao.find(queryHql.toString(), companyId, false, queryDate);
		executeExpired(users, adminNow, generalNow);
	}
	
	/**
	 * @param users
	 * @param adminOverdue
	 * @param generalOverdue
	 * @param byAdmin        是否按照管理员级别方式查询的用户，若果是，则说明管理员密码过期时间短
	 */
	private void executeExpired(List<User> users, Date adminOverdue, Date generalOverdue){
		for(User user : users){
			Date passWordCreateTime = user.getUserInfo().getPasswordUpdatedTime();
			if(isAdmin(user)){
				if(adminOverdue.after(passWordCreateTime)){
					user.setAccountExpired(false);
					userDao.save(user);
				}
			}else{
				if(generalOverdue.after(passWordCreateTime)){
					user.setAccountExpired(false);
					userDao.save(user);
				}
			}
		}
	}
	
	/**
	 * 根据角色编码判断是否为管理员
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = true)
	protected boolean isAdmin(User user){
		for(RoleUser ru: user.getRoleUsers()){
			if(ru.getRole().isDeleted()) continue;
			Role role = ru.getRole();
			if(role.getCode().endsWith(SYSTEMADMIN)|| 
					role.getCode().endsWith(SECURITYADMIN) ||
					role.getCode().endsWith(AUDITADMIN)){
				return true;
			}
		}
		return false;
	}

	public Integer getDefaultAdminOverdueDays() {
		if(defaultAdminOverdueDays == null) defaultAdminOverdueDays = 90;
		return defaultAdminOverdueDays;
	}

	public void setDefaultAdminOverdueDays(Integer defaultAdminOverdueDays) {
		this.defaultAdminOverdueDays = defaultAdminOverdueDays;
	}

	public Integer getDefaultGeneralOverdueDays() {
		if(defaultGeneralOverdueDays == null) defaultGeneralOverdueDays = 90;
		return defaultGeneralOverdueDays;
	}

	public void setDefaultGeneralOverdueDays(Integer defaultGeneralOverdueDays) {
		this.defaultGeneralOverdueDays = defaultGeneralOverdueDays;
	}

}
