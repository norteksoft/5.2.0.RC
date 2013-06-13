package com.norteksoft.acs.service.organization;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.norteksoft.acs.base.enumeration.SecretGrade;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.Ldaper;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.sysSetting.SecuritySetting;
import com.norteksoft.acs.entity.sysSetting.ServerConfig;
import com.norteksoft.acs.ldap.LdapFactory;
import com.norteksoft.acs.ldap.LdapService;
import com.norteksoft.acs.ldap.LdapService.LdapUser;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.syssetting.SecuritySetManager;
import com.norteksoft.product.enumeration.QueryConditionProperty;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.SearchUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Service
@Transactional
public class UserInfoManager {

	protected final Log logger = LogFactory.getLog(UserInfoManager.class);
	
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	
	private SimpleHibernateTemplate<User, Long> userDao;
	
	private SimpleHibernateTemplate<Department, Long> departmentfoDao;
	
	private SimpleHibernateTemplate<SecuritySetting, Long> securitySetDao;
	
	private SimpleHibernateTemplate<DepartmentUser, Long> depUserDao;
	
	private SimpleHibernateTemplate<ServerConfig, Long> serverConfigDao;
	
	private SecuritySetManager securitySetManager;
	
	private CompanyManager companyManager;
	
	private UserManager userManager;
	
	private DepartmentManager departmentManager;
	
	private static String hql = "select user from User user join user.userInfos ui where ui.companyId=? and ui.dr=? and  ui.deleted=? and user.deleted=? order by user.weight ,user.loginName desc";
	
	//private static String hql1 = "from UserInfo userInfo where userInfo.companyId=? and userInfo.dr=? and  userInfo.deleted=? and userInfo.user.deleted=? ";
	
	private static String hql2="from UserInfo userInfo where userInfo.companyId=? and userInfo.dr=?  order by userInfo.user.weight ,userInfo.user.loginName desc";

    private static int TOTAL_PERSON_COUNT = 0;
    private static int SYNCHRONOUS_PERSON_COUNT = 0;
    private static int TOTAL_DEPARTMENT_COUNT = 0;
    private static int SYNCHRONOUS_DEPARTMENT_COUNT = 0;
    private static String PORTAL_COMMON_ROLE_CODE="portalCommonUser";//portal普通用户角色code
    private static String ACS_COMMON_ROLE_CODE="acsCommonUser";
    
    
	private Long companyId;
	
	private Date newDate;
	
	@Autowired
	private AcsUtils acsUtils;
	
	@Autowired
	private RoleManager roleManager;
	
	public Long getSystemIdByCode(String code) {
		return acsUtils.getSystemsByCode(code).getId();
    }
	
	public Long getCompanyId() {
		if(companyId == null){
			return ContextUtils.getCompanyId();
		}else 
			return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(
				sessionFactory, UserInfo.class);
		userDao = new SimpleHibernateTemplate<User, Long>(
				sessionFactory, User.class);
		
		departmentfoDao = new SimpleHibernateTemplate<Department, Long>(
				sessionFactory, Department.class);
		securitySetDao = new SimpleHibernateTemplate<SecuritySetting, Long>(
				sessionFactory, SecuritySetting.class);
		depUserDao= new SimpleHibernateTemplate<DepartmentUser, Long>(
				sessionFactory, DepartmentUser.class);
		serverConfigDao = new SimpleHibernateTemplate<ServerConfig, Long>(
				sessionFactory, ServerConfig.class);
	}

	@Transactional(readOnly = true)
	public List<UserInfo> getAllUser() {
		return userInfoDao.findAll();
	}

	
	@Transactional(readOnly = true)
	public Page<UserInfo> getSearchUserToDep(Page<UserInfo> page,Long companyId,Integer dr) {
		return userInfoDao.findByCriteria(page, Restrictions.eq("companyId", companyId),Restrictions.eq("dr", 0));
	}
	

	//@Transactional(readOnly = true)
	public Page<User> getSearchUser(Page<User> page,UserInfo userInfo,Integer dr, boolean deleted) {
		return userDao.searchPageByHql(page, hql, getCompanyId(),dr,deleted,deleted);
	}
	


	public void save(UserInfo entity){
		entity.getUser().setCompanyId(getCompanyId());
		entity.setCompanyId(getCompanyId());
		userInfoDao.save(entity);
	}
	
	@SuppressWarnings("unchecked")
	public Integer getCompanyIsUser(){
		List<User> userList =userInfoDao.find(hql, getCompanyId(),0,false,false);
		return new Integer(userList.size());
	}
	
	@SuppressWarnings("unchecked")
	public Integer getCompanyIsUsers(){
		List<UserInfo> userList =userInfoDao.find(hql2, getCompanyId(),0);
		return new Integer(userList.size());
	}
	
		
	public void delete(String ids){
		User entity = null;
		String[] arr=ids.split(",");
		for (String id : arr) {
			entity = userDao.get(Long.valueOf(id));
			entity.getUserInfo().setDr(1);
			userInfoDao.save(entity.getUserInfo());
		}
	}

	@Transactional(readOnly = true)
	public UserInfo getUserInfoById(Long id) {
		return userInfoDao.get(id);
	}

	public void falseDelete(Long id,List<Long> departmentIds){
		User user=userManager.getUserById(id);
		UserInfo userInfo = user.getUserInfo();
		userInfo.setDeleted(true);
		int i =0;
		if(!userInfo.getUser().getDepartmentUsers().isEmpty()){
			for (DepartmentUser depUser : userInfo.getUser().getDepartmentUsers()) {
				if(departmentIds.get(0)!=null){
					if(departmentIds.get(0).equals(depUser.getDepartment().getId())){
						if(departmentIds.get(0).equals(userInfo.getUser().getMainDepartmentId())){
							userInfo.getUser().setMainDepartmentId(null);
						}
						i++;
				        depUserDao.delete(depUser);
					}
				}else{
					userInfo.getUser().setMainDepartmentId(null);
					depUserDao.delete(depUser);
				}
			}
		}
		if((userInfo.getUser().getDepartmentUsers().size()==i)||departmentIds.get(0)==null){
		   userInfo.getUser().setDeleted(true);
		   userInfo.setDeleted(true);
		}else{
			userInfo.getUser().setDeleted(false);
			userInfo.setDeleted(false);
		}
		//userInfo.getUser().setDeleted(true);
		userInfoDao.save(userInfo);
		userDao.save(user);
	}

	public void savePassWord(UserInfo userInfo){
		userInfoDao.save(userInfo);
	}
	
	public void forbidden(Long id) {
		UserInfo userInfo = userInfoDao.get(id);
		userInfo.getUser().setEnabled(false);
		userInfoDao.save(userInfo);
	}
	
	public void invocation(Long id) {
		UserInfo userInfo = userInfoDao.get(id);
		userInfo.getUser().setEnabled(true);
		userInfoDao.save(userInfo);
	}
	
	public void unblock(Long id) {
		UserInfo userInfo = userInfoDao.get(id);
		userInfo.getUser().setAccountExpired(true);
		userInfoDao.save(userInfo);
	}
	
	public void lock(Long id) {
		UserInfo userInfo = userInfoDao.get(id);
		if(!userInfo.getUser().getAccountLocked()){
		userInfo.getUser().setAccountExpired(false);
		}
		userInfoDao.save(userInfo);
	}
	public void overdueUnblock(Long id){
		UserInfo userInfo = userInfoDao.get(id);
		userInfo.getUser().setAccountExpired(true);
		userInfo.setPasswordUpdatedTime(new Date());
		userInfoDao.save(userInfo);
	}
	public void overdueblock(Long id){
		UserInfo userInfo = userInfoDao.get(id);
		userInfo.getUser().setAccountExpired(false);
		userInfo.setPasswordUpdatedTime(new Date());
		userInfoDao.save(userInfo);
	}
	public List<Department> getDepartmentAll(){
		return departmentfoDao.findAll();
	}
	
	public boolean checkLoginPassword(String orgPassword){
		List<SecuritySetting> list = securitySetDao.findByCriteria(Restrictions.eq("name", "password-complexity")
				                                               ,Restrictions.eq("companyId", getCompanyId()));
		if(list.isEmpty()){
			return true;
		}
		SecuritySetting seyset = list.get(0);
		Integer len = getPassWordLength(seyset.getValue());
		boolean istrue = false;
		istrue = passWordValidator(seyset.getValue(), len, orgPassword);
		return istrue;
	}
	
	
	/**
	 * 验证密码规则
	 */
	public boolean passWordValidator(String regx,Integer length,String orgPassWord){
		if(regx==null||"".equals(regx))
			return true;
		String[] rule=regx.split(",");
		StringBuilder validator = new StringBuilder(); 
		for (int i = 0; i < rule.length; i++) {
			if(rule[i].indexOf(')')!=-1){
				validator.append(rule[i].trim());
				if(rule[i].indexOf('{')!=-1){
					validator.append(",");
				}
			}
			
		}
		validator.append("(?!.*\\s)");//不允许有空格
		validator.append(".*");
		boolean istrue = orgPassWord.matches(validator.toString());
		boolean isLength = length==null||length==0 ? true: (orgPassWord.length()>=length?true:false);
		if(istrue&&isLength){
			return true;
		}
		
		return false;
	}
	
	public Integer getPassWordLength(String securityValue){
		if(securityValue==null||"".equals(securityValue))
			return null;
		String[] rule=securityValue.split(",");
		Pattern p=Pattern.compile("\\d");
		Matcher m=p.matcher(rule[rule.length-1].trim());
		if(m.find()){
			
			return Integer.valueOf(rule[rule.length-1].trim());
		}
		return null;
	}

	public UserInfo checkUserName(String userName){
		//User user = (User) userDao.findUnique("select user from User user where user.deleted=false and user.loginName=? ", userName);
		UserInfo ui=(UserInfo)userInfoDao.findUnique("select ui from UserInfo ui where ui.user.loginName=? and ui.dr=?", userName,0);
		return ui;
	}
	public Page<User> queryUsersByDepartment(Page<User> page, Long departmentId) {
		return userDao.searchPageByHql(page,
				"select distinct user from User user inner join user.departmentUsers  du where du.department.id=? and user.deleted=? and du.deleted = ? and user.companyId=? order by user.weight desc",
				//"select ui.user from UserInfo ui inner join ui.user.departmentUsers du where du.department.id=? and ui.dr =? and ui.deleted=? and du.deleted = ? and ui.companyId=? order by ui.user.weight desc", 
				departmentId,false,false,getCompanyId());
	}
	
	public Page<User> queryUsersByCompany(Page<User> page, Long companyId) {
		userDao.find(page, "select ui.user from UserInfo ui where ui.companyId=? and ui.dr=? and ui.deleted=? and ui.user.deleted=? order by ui.user.weight desc, ui.user.loginName ", companyId, 0, false,false);
		return page;
	}

	public Page<User> queryUsersByWorkGroup(Page<User> page, Long workGroupId) {
		return userDao.searchPageByHql(page,"select user from User user inner join user.workgroupUsers wu where wu.workgroup.id=? and wu.deleted = ? and user.deleted=?", workGroupId,false,false);
	}

	public SimpleHibernateTemplate<UserInfo, Long> getUserInfoDao() {
		return userInfoDao;
	}
	/**
	 * 查询密码过期的用户
	 */
	
	public List<Long> getPassWordOverdueId(List<User> userList){
		List<Long> passWordOverdueIds = new ArrayList<Long>();
		Integer temp =0;
		for (User userInfo : userList) {
			temp = securitySetManager.getPasswordIsOverdue(userInfo.getId(), getCompanyId());
			if(temp!=null&&temp==0)
				passWordOverdueIds.add(userInfo.getId());
			
		}
		return passWordOverdueIds;
	}
	
	public String getpasswordOverdueDay(UserInfo userInfo,SecuritySetting adminSeyset,SecuritySetting usertSeyset,List<Role> roleList){
		int temp = 0;
		
		 for (Role role : roleList) {
				if(userInfo.getUser().getRoleUsers().size()>0){
				    for (RoleUser roleU : userInfo.getUser().getRoleUsers()) {
						  if(role.getId().equals(roleU.getRole().getId())){//判断当前用户是否是管理员
							  temp=1;
						  }
					}
			}				  
			
		}
		 
		 if(temp==1){
			 boolean istrue = adminSeyset!=null?(!adminSeyset.getValue().trim().equals("")?true:false):false;
			 if(istrue)
				  return adminSeyset.getValue();
		 }if(temp==0){
			 boolean istrue = usertSeyset!=null?(!usertSeyset.getValue().trim().equals("")?true:false):false;
			 if(istrue)
				  return usertSeyset.getValue();
		 }
		 
		return null;
	}
	
	/**
	 * 查询密码提前通知的用户
	 */
	
	public Map<Long,Integer> passwordOverNotice(List<User> userList){
		Map<Long,Integer> passwordOverNotice = new LinkedHashMap<Long, Integer>();
		Integer temp =0;
		for (User userInfo : userList) {
			temp = securitySetManager.getPasswordIsOverdue(userInfo.getId(), getCompanyId());
			if(temp !=null&&temp>0){
				passwordOverNotice.put(userInfo.getId(), temp);
			}
		}
		return passwordOverNotice;
	}

	public Date getNewDate() {
		if(newDate==null){
			Calendar cal=Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DATE);
			cal.clear();
			cal.set(year, month, day);
			newDate = cal.getTime();
		}
		return newDate;
	}

	public void setNewDate(Date newDate) {
		this.newDate = newDate;
	}
	@Autowired
	public void setSecuritySetManager(SecuritySetManager securitySetManager) {
		this.securitySetManager = securitySetManager;
	}
	@Autowired
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	@Autowired
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	
	/**
	 * LDAP 同步
	 * @return
	 */
	public String synchronize(){
		Company company = companyManager.getCompany(getCompanyId());
		ServerConfig config = (ServerConfig)serverConfigDao.findUnique("FROM ServerConfig s WHERE s.companyId=?", getCompanyId());
		LdapService ldap = LdapFactory.getLdapService(
				config.getLdapType(), config.getLdapUsername(), 
				config.getLdapPassword(), config.getLdapUrl());
		
		List<LdapUser> ldapUsers = ldap.getAllUser();
		
		Map<String, Long> deptIds = getDepartmentInfo();
		int count = 0;
		Department dept = null;
		for(LdapUser lu : ldapUsers){
			User u = getUserByLoginName(lu.getUsername());
			// 用户已经存在，不作更改
			if(u != null){
				u.setName(lu.getName());
				u.setEmail(lu.getEmail());
				userDao.save(u);
				count++;
				continue;
			}
			dept = getDepartmentId(deptIds, lu.getDepartment(), company);
			User user = new User();
			UserInfo info = new UserInfo();
			info.setUser(user);
			user.setName(lu.getName());
			user.setLoginName(lu.getUsername());
			user.setEmail(lu.getEmail());
			user.setSex(Boolean.FALSE);
			user.setPassword("");
			info.setPasswordUpdatedTime(new Date());
			if(dept != null) user.setMainDepartmentId(dept.getId());
			userDao.save(user);
			this.save(info);
			count++;
			// 建立用户部门关系
			addUserIntoDept(user, dept);
			giveNewUserPortalCommonRole(user);
		}
		return "共同步"+count+"个用户";
	}
	private User getUserByLoginName(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定查询用户的查询条件：用户登录名");
		return (User)userDao.findUnique("from User u where u.companyId=? and u.loginName=? and u.deleted=? ", getCompanyId(), loginName, false);
	}
	private void addUserIntoDept(User user, Department dept){
		if(dept == null) return;
		
		DepartmentUser du = new DepartmentUser();
		du.setCompanyId(getCompanyId());
		du.setDepartment(dept);
		du.setUser(user);
		depUserDao.save(du);
	}
	
	private Map<String, Long> getDepartmentInfo(){
		List<Department> depts = departmentManager.getAllDepartment();
		Map<String, Long> deptIds = new HashMap<String, Long>();
		for(Department dept : depts){
			deptIds.put(dept.getName(), dept.getId());
		}
		return deptIds;
	}
	
	private Department getDepartmentId(Map<String, Long> depts, List<String> deptNames, Company company){
		if(deptNames.isEmpty()) return null;
		
		Long deptId = null;
		Department parentDept = null; // 已存在的部门作为父部门
		Department newParemtDept = null; // 新建的部门作父部门
		Department department = null;
		for(String deptName : deptNames){
			if(newParemtDept == null){
				deptId = depts.get(deptName);
			}
			if(deptId == null){
				department = new Department();
				department.setCode(deptName);
				department.setName(deptName);
				department.setCompany(company);
				department.setParent(parentDept);
				departmentManager.saveDept(department);
				deptId = department.getId();
				newParemtDept = department;
				parentDept = department;
				depts.put(deptName, deptId);
			}
			// 最后一次不需要查了
			if(deptNames.size()>1)
				parentDept = departmentManager.getDepartment(deptId);
		}
		return departmentManager.getDepartment(deptId);
	}
	
	/**
	 * 同步LDAP部门及用户
	 */
	public String synchronousLdap(){
		TOTAL_PERSON_COUNT = 0;
	    SYNCHRONOUS_PERSON_COUNT = 0;
	    TOTAL_DEPARTMENT_COUNT = 0;
	    SYNCHRONOUS_DEPARTMENT_COUNT = 0;
	    
		LdapContext ctx = null;
		String message = "";
		logger.debug("company id is "
		    		 + getCompanyId());
		Company company = companyManager.getCompany(getCompanyId());
		logger.debug("company name is "
		    		 +company.getName());
		String companyCode = company.getCode();
		logger.debug("company code is "
		    		 +companyCode);
		try {
			ctx = Ldaper.getConnectionFromPool();

			StringBuilder searchUrl = new StringBuilder("o=");
			searchUrl.append(companyCode);
			logger.debug("searchUrl is "+searchUrl.toString());
			NamingEnumeration<SearchResult> results = ctx.search(searchUrl.toString(), null);
			while (results.hasMore()) {
				SearchResult result = results.next();
				Attributes attrs = result.getAttributes();
				logger.debug("objectClass is"+attrs.get("objectClass").toString());
				if (attrs.get("objectClass").toString().contains("dominoPerson")) {
					TOTAL_PERSON_COUNT++;
					String userName = null;
					if(attrs.get("uid")==null){
						userName = attrs.get("cn").toString().replaceAll("cn: ", "");
					}else{
						userName = attrs.get("uid").toString().replaceAll("uid: ", "");
					}
					logger.debug("loginname is "+userName);
					User user = userManager.getUserByLoginName(userName);
					if(user == null){
						SYNCHRONOUS_PERSON_COUNT++;
						String password = attrs.get("userPassword")==null?
								null:attrs.get("userPassword").toString().replaceAll("userPassword: ", "");
						String email = attrs.get("mail")==null?"":attrs.get("mail").toString().replaceAll("mail: ", "");
						if(email.indexOf("/")>-1 && email.lastIndexOf("/")<email.length()-1){
							email = email.substring(email.lastIndexOf("/")+1);
						}
						String trueName = attrs.get("cn").toString().replaceAll("cn: ", "");
						user = new User();
						if(!StringUtils.isEmpty(password))
							user.setPassword(password);
						user.setLoginName(userName);
						
						UserInfo userInfo = new UserInfo();
						userInfo.setPasswordUpdatedTime(getNewDate());
						user.setEmail(email);
						userInfo.setUser(user);
						user.setName(trueName);
						save(userInfo);
					}
					
				} else if(attrs.get("objectClass").toString().contains("dominoOrganizationalUnit")) {
					TOTAL_DEPARTMENT_COUNT++;
					String departmentName = attrs.get("ou").toString().replaceAll("ou: ", "");
					logger.debug("department name is "+departmentName);
					Department department = departmentManager.checkDeptNoParent(departmentName);
					if(department == null){
						SYNCHRONOUS_DEPARTMENT_COUNT++;
						department = new Department();
						department.setName(departmentName);
						department.setCode(departmentName);
						department.setCompany(company);
						departmentfoDao.save(department);
					}
					StringBuilder subSearchUrl = new StringBuilder("ou=");
					subSearchUrl.append(departmentName);
					subSearchUrl.append(",");
					subSearchUrl.append(searchUrl);
					subSynchronous(subSearchUrl.toString(), ctx, department,company);
				}
			}
			message = getMessage();
			return message;
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		return null;
	}

	private void subSynchronous(String rootBase, LdapContext ctx,Department department,Company company) {
		logger.debug(" enter subSynchronous...");
		try {
			String url = rootBase;
			Department dept = department;
			NamingEnumeration<SearchResult> results = ctx.search(url, null);
			String currentUrl = null;
			if (results.hasMore()) {
				while (results.hasMore()) {
					SearchResult result = results.next();
					
					Attributes attrs = result.getAttributes();
					if (attrs.get("objectClass").toString().contains("dominoPerson")) {
						TOTAL_PERSON_COUNT++;
						String userName = null;
						if(attrs.get("uid")==null){
							userName = attrs.get("cn").toString().replaceAll("cn: ", "");
						}else{
							userName = attrs.get("uid").toString().replaceAll("uid: ", "");
						}
						logger.debug("loginname is "+userName);
						User user = userManager.getUserByLoginName(userName);
						if(user == null){
							SYNCHRONOUS_PERSON_COUNT++;
							String password = attrs.get("userPassword")==null?
									null:attrs.get("userPassword").toString().replaceAll("userPassword: ", "");
							String email = attrs.get("mail")==null?"":attrs.get("mail").toString().replaceAll("mail: ", "");
							String trueName = attrs.get("cn").toString().replaceAll("cn: ", "");
							user = new User();
							if(!StringUtils.isEmpty(password))
								user.setPassword(password);
							user.setLoginName(userName);
							
							UserInfo userInfo = new UserInfo();
							userInfo.setPasswordUpdatedTime(getNewDate());
							user.setEmail(email);
							userInfo.setUser(user);
							user.setName(trueName);
							save(userInfo);
							//建立部门&人员关系
							List<Long> checkedUserIds = new ArrayList<Long>();
							checkedUserIds.add(userInfo.getId());
							departmentManager.departmentToUser(dept.getId(), checkedUserIds,0);
						}
					} else if(attrs.get("objectClass").toString().contains("dominoOrganizationalUnit")) {
						TOTAL_DEPARTMENT_COUNT++;
						currentUrl = result.getName() + "," + url;
						String departmentName = attrs.get("ou").toString().replaceAll("ou: ", "");
						logger.debug("department name is "+departmentName);
						Department subDepartment = departmentManager.checkDeptHasParent(departmentName,dept.getId());
						if(subDepartment == null){
							SYNCHRONOUS_DEPARTMENT_COUNT++;
							subDepartment = new Department();
							subDepartment.setName(departmentName);
							subDepartment.setCode(departmentName);
							subDepartment.setCompany(company);
							subDepartment.setParent(dept);
							departmentfoDao.save(subDepartment);
						}
						subSynchronous(currentUrl, ctx, subDepartment,company);
					}
				}
			
			} 
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	private String getMessage(){
		StringBuilder message = new StringBuilder();
		message.append(TOTAL_PERSON_COUNT);
		message.append("-");
		message.append(SYNCHRONOUS_PERSON_COUNT);
		message.append("-");
		message.append(TOTAL_DEPARTMENT_COUNT);
		message.append("-");
		message.append(SYNCHRONOUS_DEPARTMENT_COUNT);
		return message.toString();
	}
	public boolean validateLdapStart(){
		Long companyId = ContextUtils.getCompanyId();
		ServerConfig serverConfig = (ServerConfig)serverConfigDao.
		           findUnique("FROM ServerConfig s WHERE s.companyId=?", companyId);
		if(serverConfig!=null){
			if(true == serverConfig.getLdapInvocation()){
				return true;
			}
		}
		return false;
	}
	
	public void getNoDepartmentUsers(Page<User> userInfo){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT u.* FROM ACS_USERINFO ");
		sqlString.append("inner join ACS_USER u on ACS_USERINFO.FK_USER_ID=u.id ");
		sqlString.append("LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ");
		String condition=getCondition();
		if(StringUtils.isNotEmpty(condition)){
			sqlString.append(condition);
		}
		sqlString.append(" ORDER BY ACS_USERINFO.ID DESC ");
		userDao.findPageByJdbc(userInfo, sqlString.toString(), getCompanyId());
	}
	
	
	public void getAllDepartmentUsers(Page<User> userInfo){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT u.* FROM ACS_USERINFO ");
		sqlString.append("inner join ACS_USER u on ACS_USERINFO.FK_USER_ID=u.id ");
		sqlString.append("LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NOT NULL ORDER BY ACS_USERINFO.ID DESC ");
		userDao.findPageByJdbc(userInfo, sqlString.toString(), getCompanyId());
	}
	
	private String getCondition(){
		String searchParameters = Struts2Utils.getParameter("searchParameters");
		String condition="";
		if(StringUtils.isNotEmpty(searchParameters)){
			MapType mt = TypeFactory.defaultInstance().constructMapType(
					HashMap.class, QueryConditionProperty.class, String.class);
			CollectionType ct = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, mt);
			List<Map<QueryConditionProperty,String>> prms = JsonParser.json2Object(ct, searchParameters);
			Map<QueryConditionProperty, String> obj;
			
			for(int i = 0; i < prms.size(); i++){
				condition+=" and "+getCondition(prms.get(i));
			}
		}
		return condition;
	}
	
	private String getCondition(Map<QueryConditionProperty, String> obj){
		String propName=getSqlName(obj.get(QueryConditionProperty.propName));
		String propValue=obj.get(QueryConditionProperty.propValue);
		String dataType=obj.get(QueryConditionProperty.dataType);
		String condition=propName;
		if("BOOLEAN".equals(dataType)){
			condition+=" = ";
			if("1".equals(propValue)||"true".equals(propValue)){
				condition+="true";
			}else if("0".equals(propValue)||"false".equals(propValue)){
				condition+="false";
			}
		}else if("ENUM".equals(dataType)){
			condition+=" = ";
			if("COMMON".equals(propValue)){
				condition+=0;
			}else if("MAJOR".equals(propValue)){
				condition+=1;
			}else {
				condition+=2;
			}
		}else{
			condition+=" like ";
			condition+="'%"+propValue+"%'";
		}
		return condition;
	}
	
	private String getSqlName(String name){
		if("secretGrade".equals(name)){
			return "secret_grade";
		}else if("accountLocked".equals(name)){
			return "account_locked";
		}else{
			return name;
		}
	}
	
	public UserInfo getUserInfoByUser(String loginName){
		return (UserInfo)userInfoDao.findUnique("from UserInfo userInfo where userInfo.companyId=? and userInfo.dr=? and userInfo.user.deleted=? and userInfo.user.loginName=?", getCompanyId(),false,false,loginName);
	}
	
	public User getUserByUserInfoId(Long userInfoId){
		UserInfo userInfo=getUserInfoById(userInfoId);
		if(userInfo==null)return null;
		return userInfo.getUser();
	}
	
	//新建用户是默认给用户portal普通用户权限
	public void giveNewUserPortalCommonRole(User user) {
		List<Role> roles = roleManager.getRolesByCodes(PORTAL_COMMON_ROLE_CODE, ACS_COMMON_ROLE_CODE);
		RoleUser roleUser = null;
		for(Role role : roles){
			if(!roleManager.checkRoleUser(role.getId(),user.getId())){
				roleUser = new RoleUser();
				roleUser.setRole(role);
				roleUser.setUser(user);
				roleUser.setCompanyId(getCompanyId());
				roleManager.saveRoleUser(roleUser);
			}
		}
	}
	
}
