package com.norteksoft.acs.base.utils.authHelp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.entity.organization.WorkgroupUser;
import com.norteksoft.acs.entity.sysSetting.ServerConfig;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;



/**
 * 权限API
 * @author Administrator
 */
@Deprecated
@Service
@Transactional
public class AuthHelpAPI {

	public final static String DEPARTMENT = "department";
	public final static String WORKGROUP = "workgroup";
	public final static String DELETED = "deleted";
	protected static SessionFactory sessionFactory;
	private static SimpleHibernateTemplate<Department, Long> departmentDao;
	private static SimpleHibernateTemplate<Workgroup, Long> workGroupDao;
	private static SimpleHibernateTemplate<DepartmentUser, Long> depUserDao;
	private static SimpleHibernateTemplate<WorkgroupUser, Long> workGroupToUserDao;
	private static SimpleHibernateTemplate<User, Long> userDao;
	private static SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	private static SimpleHibernateTemplate<Company, Long> companyDao;
	private static SimpleHibernateTemplate<BusinessSystem, Long> businessSystemDao;
	private static SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private static SimpleHibernateTemplate<Role, Long> roleDao;
	private static SimpleHibernateTemplate<LoginLog, Long> loginUserLogDao;
	private static SimpleHibernateTemplate<ServerConfig, Long> serverConfigDao;
	
	static {
		init();
	}

	private AuthHelpAPI() {
	}

	private static void init() {
		sessionFactory = getSessionFactory();
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		workGroupDao = new SimpleHibernateTemplate<Workgroup, Long>(sessionFactory, Workgroup.class);
		depUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(sessionFactory, DepartmentUser.class);
		workGroupToUserDao = new SimpleHibernateTemplate<WorkgroupUser, Long>(sessionFactory, WorkgroupUser.class);
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory,User.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory,UserInfo.class);
		companyDao = new SimpleHibernateTemplate<Company, Long>(sessionFactory,Company.class);
		businessSystemDao = new SimpleHibernateTemplate<BusinessSystem, Long>(sessionFactory,BusinessSystem.class);
		roleUserDao = new SimpleHibernateTemplate<RoleUser, Long>(sessionFactory,RoleUser.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory,Role.class);
		loginUserLogDao = new SimpleHibernateTemplate<LoginLog, Long>(sessionFactory, LoginLog.class); 
		serverConfigDao=new SimpleHibernateTemplate<ServerConfig, Long>(sessionFactory, ServerConfig.class);
	}
	
	/**
	 * 查询在线用户数量
	 * @param companyId
	 * @return
	 */
	public static Long getOnlineUserCount(Long companyId){
		return loginUserLogDao.findLong(
				"select count(u) from LoginLog u where u.exitTime is null and u.companyId=? and u.deleted=?", 
				 companyId, false);
	}

	protected static SessionFactory getSessionFactory() {
		sessionFactory = (SessionFactory)ContextUtils.getBean("sessionFactory");
		return sessionFactory;
	}

	/**
	 * 根据公司ID查询该公司所有的部门和工作组
	 * @param companyId
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	@Transactional(readOnly = true)
//	public static Map<String, List> getOrganization(Long companyId) {
//		Map<String, List> map = new HashMap<String, List>();
//		map.put(DEPARTMENT, getDepartmentList(companyId));
//		map.put(WORKGROUP, getWorkGroupList(companyId));
//		return map;
//	}

	/**
	 * 根据公司ID查询该公司所有的部门
	 * 
	 * @param companyId 公司ID
	 * @return List<Department>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public static List<Department> getDepartmentList(Long companyId) {
		return departmentDao.find(
				"FROM Department d WHERE d.company.id=? AND d.deleted=? and d.parent.id is null ORDER BY d.weight desc", 
				companyId, false);
	}

	/**
	 * 根据公司ID查询该公司所有的工作组
	 * 
	 * @param companyId 公司ID
	 * @return List<WorkGroup>
	 */
	@Transactional(readOnly = true)
	public static List<Workgroup> getWorkGroupList(Long companyId) {
		List<Workgroup> workGroupList = workGroupDao.findByCriteria(
				Restrictions.eq("company.id", companyId), Restrictions.eq(
						DELETED, false));
		return workGroupList;
	}

	/**
	 * 根据部门ID查询该部门所有的人员
	 * 
	 * @param departmentId 部门Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public static List<User> getUserListByDepartmentId(Long departmentId) {
		List<User> userList = new ArrayList<User>();
		String hql = "FROM DepartmentUser d WHERE d.department.id=? AND d.deleted=? ORDER BY d.user.weight DESC";
		List<DepartmentUser> list = depUserDao.find(hql, departmentId,false);
		UserInfo userif = null;
		for (DepartmentUser departmentToUser : list) {
			userif = departmentToUser.getUser().getUserInfo();
			if (userif != null && userif.getDr() == 0)
				userList.add(departmentToUser.getUser());

		}
		return userList;
	}
	/**
	 * 根据部门名称得到部门下用户的登录名
	 * @param companyId
	 * @param loginName
	 * @return
	 */	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public static List<String> getUserLoginNameListByDepartmentName(String departmentName,Long companyId) {
		String dhql ="from Department d where d.departmentName=? and d.deleted=? and d.company.id=?";
		Department department=(Department)departmentDao.findUnique(dhql, departmentName,false,companyId);
		List<String> userLoginNameList = new ArrayList<String>();
		String hql = "FROM DepartmentUser d WHERE d.department.id=? AND d.deleted=? ORDER BY d.user.weight DESC";
		List<DepartmentUser> list = depUserDao.find(hql, department.getId(),false);
		for (DepartmentUser departmentToUser : list) {
			userLoginNameList.add(departmentToUser.getUser().getLoginName());
		}
		return userLoginNameList;
	}
	

	/**
	 * 根据工作组ID查询该工作组所有的人员
	 * 
	 * @param workGroupId 工作组Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public static List<User> getUserListByWorkGroupId(Long workGroupId) {
		List<User> userList = new ArrayList<User>();
		String hql = "FROM WorkGroupUser d WHERE d.workgroup.id=? AND d.deleted=? ORDER BY d.user.weight DESC";
		List<WorkgroupUser> list = workGroupDao.find(hql, workGroupId,false);
		UserInfo userif = null;
		for (WorkgroupUser workGroupToUser : list) {
			userif = workGroupToUser.getUser().getUserInfo();
			if (userif != null && userif.getDr() == 0)
				userList.add(workGroupToUser.getUser());
		}
		userList = doSort(userList);
		return userList;
	}

	/**
	 * 根据父部门id查询该父部门下所有子部门
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public static List<Department> getSubDepartmentList(Long paternDepartmentId) {
		return departmentDao.find(
				"FROM Department d WHERE d.parent.id=? AND d.deleted=?  ORDER BY d.weight desc", 
				paternDepartmentId, false);
	}

	/**
	 * 根据用户Id得到用户实体
	 * @return User
	 */
	@Transactional(readOnly = true)
	public static User getUserById(Long id) {
		if (id instanceof Long)
			return userDao.get(id);
		return null;
	}

	/**
	 * 通过公司code获取公司Id
	 * @param companyCode
	 * @return Long
	 */
	@Transactional(readOnly = true)
	public static Long getCompanyIdByCompanycode(String companyCode) {
		if (companyCode == null || companyCode.trim().length() <= 0)
			return null;
		Object obj = companyDao.findUnique(
						"from Company company where company.code=? and company.deleted=?",
						companyCode, false);
		if (obj instanceof Company) {
			return ((Company) obj).getId();
		}
		return null;
	}
	
	/**
	 * 获取当前用户所有角色的字符串表示形式(即角色编码以逗号隔开)
	 * @return String
	 */
	@Transactional(readOnly = true)
	public static String getCurrentUserRoles(){
		Long userId = ContextUtils.getUserId();
		if(userId == null) return "";
		
		User user = userDao.get(userId);
		if(user == null) return "";
		
		return getRoleCodesFromUser(user);
	}
	
	@Transactional(readOnly = true)
	public static String getCurrentUserRoles(Long userId){
		User user = userDao.get(userId);
		if(user == null) return "";
		
		return getRoleCodesFromUser(user);
	}
	
	/**
	 * 查询用户委托的角色。 
	 * @param userId
	 * @param sourceId
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public static Set<Role> getRolesByUserId(Long userId,Long sourceId,Long companyId){
		String hql="FROM RoleUser ru WHERE ru.consigner=? AND ru.user.id=? AND ru.companyId=?";
		List<RoleUser> roleUsers = roleUserDao.find(hql, sourceId,userId,companyId);
		Set<Role> roles = new HashSet<Role>();
		for(RoleUser ru : roleUsers){
			Role role=roleDao.get(ru.getRole().getId());
			roles.add(role);
		}
		return roles;
	}
	
	/**
	 * 根据用户获取用户的角色字符串形式（不含委托）
	 */
	public static String getRoleCodesFromUser(User user){
		Set<Role> roles = new HashSet<Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner()!= null) continue;
			Role role = ru.getRole();
			if(!role.isDeleted()) roles.add(role);
		}
		// 用户具有的部门拥有的角色
		Set<DepartmentUser> departmentUsers =  user.getDepartmentUsers();
		for(DepartmentUser du : departmentUsers){
			if(du.isDeleted() || du.getDepartment().isDeleted()) continue;
			for(RoleDepartment rd : du.getDepartment().getRoleDepartments()){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) roles.add(rd.getRole());
			}
		}
		// 用户具有的工作组拥有的角色
		Set<WorkgroupUser> workgroupUsers = user.getWorkgroupUsers();
		for(WorkgroupUser wu : workgroupUsers){
			if(wu.isDeleted() || wu.getWorkgroup().isDeleted()) continue;
			for(RoleWorkgroup rw : wu.getWorkgroup().getRoleWorkgroups()){
				if(!rw.isDeleted() && !rw.getRole().isDeleted()) roles.add(rw.getRole());
			}
		}
		// 生成字符串形式
		StringBuilder roleStrings = new StringBuilder();
		for(Role role : roles){
			roleStrings.append(role.getCode()).append(",");
		}
		// 去掉最后一个逗号
		if(roleStrings.lastIndexOf(",") != -1 && roleStrings.lastIndexOf(",") == roleStrings.length()-1){
			roleStrings.replace(roleStrings.length()-1, roleStrings.length(), "");
		}
		return roleStrings.toString();
	}
	/**
	 * 根据用户获取用户的角色
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Set<Role> getRolesByUser(Long userId){
		User user = userDao.get(userId);
		if(user == null) return null;
		return getRolesByUser(user);
	}
	
	/**
	 * 根据用户查询用户的角色（不含委托）
	 * @param user
	 * @return
	 */
	public static Set<Role> getRolesByUser(User user){
		if(user == null) return null;
		
		Set<Role> roles = new HashSet<Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner() != null) continue;
			Role role = ru.getRole();
			if(!role.isDeleted()) roles.add(role);
		}
		// 用户具有的部门拥有的角色
		Set<DepartmentUser> departmentUsers =  user.getDepartmentUsers();
		for(DepartmentUser du : departmentUsers){
			if(du.isDeleted() || du.getDepartment().isDeleted()) continue;
			for(RoleDepartment rd : du.getDepartment().getRoleDepartments()){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) roles.add(rd.getRole());
			}
		}
		// 用户具有的工作组拥有的角色
		Set<WorkgroupUser> workgroupUsers = user.getWorkgroupUsers();
		for(WorkgroupUser wu : workgroupUsers){
			if(wu.isDeleted() || wu.getWorkgroup().isDeleted()) continue;
			for(RoleWorkgroup rw : wu.getWorkgroup().getRoleWorkgroups()){
				if(!rw.isDeleted() && !rw.getRole().isDeleted()) roles.add(rw.getRole());
			}
		}
		return roles;
	}
	
	
	@Transactional(readOnly = true)
	public static List<Role> getRolesListByUser(Long userId){
		User user = userDao.get(userId);
		if(user == null) return null;
		return getRolesListByUser(user);
	}
	
	/**
	 * 根据用户查询用户角色（不含委托）
	 * @param user
	 * @return
	 */
	public static List<Role> getRolesListByUser(User user){
		if(user == null) return null;
		
		List<Role> roles = new ArrayList<Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner() != null) continue;
			Role role = ru.getRole();
			if(!role.isDeleted()) {
				if(!roles.contains(role)){
					roles.add(role);
				}
			}
		}
		// 用户具有的部门拥有的角色
		Set<DepartmentUser> departmentUsers =  user.getDepartmentUsers();
		for(DepartmentUser du : departmentUsers){
			if(du.isDeleted() || du.getDepartment().isDeleted()) continue;
			for(RoleDepartment rd : du.getDepartment().getRoleDepartments()){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) {
					if(!roles.contains(rd.getRole())){
						roles.add(rd.getRole());
					}
				}
			}
		}
		// 用户具有的工作组拥有的角色
		Set<WorkgroupUser> workgroupUsers = user.getWorkgroupUsers();
		for(WorkgroupUser wu : workgroupUsers){
			if(wu.isDeleted() || wu.getWorkgroup().isDeleted()) continue;
			for(RoleWorkgroup rw : wu.getWorkgroup().getRoleWorkgroups()){
				if(!rw.isDeleted() && !rw.getRole().isDeleted()) {
					if(!roles.contains(rw.getRole())){
						roles.add(rw.getRole());
					}
				}
			}
		}
		return roles;
	}
	
	/**
	 * 获取租户名称
	 * @param businessSystemId
	 * @return
	 */
	public String getBusinessSystemNameById(Long businessSystemId){
		BusinessSystem entity = businessSystemDao.get(businessSystemId);
		if(entity==null){
			return "";
		}else{
			return entity.getName();
		}
	}
	/**
	 * 获取不属于任何部门的用户
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	public static List<User> getUsersNotInDepartment(Long companyId){
		if(companyId == null) return null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT ACS_USER.* FROM ACS_USER LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USER.ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USER.DELETED=0 AND ACS_USER.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USER.WEIGHING DESC");
		
		return userDao.findByJdbc(sqlString.toString(), companyId);
	}
	/**
	 * 获取不属于任何部门的用户
	 * @return page
	 */
	public static Page<UserInfo> getNoDepartmentUsers(Page<UserInfo> page,Long companyId){
        if(companyId == null) return null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT * FROM ACS_USER LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USER.ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USER.DELETED=0 AND ACS_USER.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USER.WEIGHING DESC");
		List<User>us =userDao.findByJdbc(sqlString.toString(), companyId);
		List<UserInfo> uiList = new ArrayList<UserInfo>();
		for(User u : us){
			UserInfo ui= (UserInfo)userInfoDao.findUnique("from UserInfo ui where ui.user.id=? and ui.companyId=? ",u.getId() ,companyId);
			uiList.add(ui);
		}
		page.setResult(uiList);
		page.setPageSize(15);
		return page;
	}
	
	
	/**
	 * 将角色授权给别人，自己还保留该角色
	 * @param someoneId 受权人
	 * @param roleIds 角色id数组
	 * @param companyId
	 * @param sourceUserId //授权人
	 */
	
	public static void assignRolesToSomeone(Long someoneId,String[] roleIds,Long companyId,Long sourceUserId){
//		deleteAssignedAuthority(sourceUserId,someoneId,companyId);
		for(int i=0;i<roleIds.length;i++){
			if(StringUtils.isNotEmpty(roleIds[i])){
				if((getRoleUserBySourceId(someoneId, Long.parseLong(roleIds[i]), companyId,sourceUserId))==null){
					RoleUser roleUser = new RoleUser();
					Role role = roleDao.get(Long.parseLong(roleIds[i]));
					User user = userDao.get(someoneId);
					roleUser.setRole(role);
					roleUser.setUser(user);
					roleUser.setCompanyId(companyId);
					roleUser.setConsigner(sourceUserId);
					roleUserDao.save(roleUser);
				}else{
					RoleUser roleUser=getRoleUserBySourceId(someoneId, Long.parseLong(roleIds[i]), companyId,sourceUserId);
					roleUser.setDeleted(false);
					roleUserDao.save(roleUser);
				}
			}
		}
	}
	
	public static void deleteRoleUsers(Long userId,String[] rIds,Long companyId)	{
		if(rIds==null) return;
		for(int j=0;j<rIds.length;j++){
			RoleUser roleUser=AuthHelpAPI.getRoleUserByRelation(userId,Long.parseLong(rIds[j]),companyId);
			if(roleUser!=null){
			roleUser.setDeleted(true);
			roleUserDao.save(roleUser);
			}
		}
	}
	/**
	 * 删除委托人委托出去的角色
	 * @param userId 受委托人的id
	 * @param rIds 角色id数组
	 * @param companyId 公司id
	 * @param sourceId 委托人id
	 */
	public static void deleteRoleUsers(Long userId,String[] rIds,Long companyId,Long sourceId)	{
		if(rIds==null) return;
		for(int j=0;j<rIds.length;j++){
			RoleUser roleUser=AuthHelpAPI.getRoleUserBySourceId(userId,Long.parseLong(rIds[j]),companyId,sourceId);
			if(roleUser!=null){
				roleUser.setDeleted(true);
				roleUserDao.save(roleUser);
			}
		}
	}
	
	
	/**
	 * 根据roleId得到role
	 * @param sourceId
	 * @param userId
	 * @param companyId
	 */
	public static Role getRoleById(Long roleId){
		Role role = roleDao.get(roleId);
		return role;
	}
	/**
	 * 根据userId得到name
	 * @param companyId
	 */
	public static String getNameByUserId(){
		String hql="from User u where u.id=? ";
		User user=(User) userDao.findUnique(hql, ContextUtils.getUserId());
		return user.getName();
	}
	
	
	/**
	 * 删除由别人分配的权限
	 * @param sourceId
	 * @param userId
	 * @param companyId
	 */
	@SuppressWarnings("unchecked")
	public static void deleteAssignedAuthority(Long sourceId,Long userId,Long companyId){
		String hql = "FROM RoleUser ru WHERE ru.consigner=? AND ru.user.id=? AND ru.companyId=?";
		List<RoleUser> roleUsers = roleUserDao.find(hql, sourceId,userId,companyId);
		for(RoleUser ru:roleUsers){
			roleUserDao.delete(ru);
		}
	}
	/**
	 * 按条件获取角色用户表数据
	 * @param userId
	 * @param roleId
	 * @param companyId
	 */
	public static RoleUser getRoleUserByRelation(Long userId,Long roleId,Long companyId){
		String hql = "FROM RoleUser ru WHERE ru.role.id=? AND ru.user.id=? AND ru.companyId=?";
		return (RoleUser)roleUserDao.findUnique(hql, roleId,userId,companyId);
	}
	
	/**
	 * 按条件获取角色用户表数据
	 * @param userId
	 * @param roleId
	 * @param companyId
	 */
	public static RoleUser getRoleUserBySourceId(Long userId,Long roleId,Long companyId,Long sourceId){
		String hql = "FROM RoleUser ru WHERE ru.role.id=? AND ru.user.id=? AND ru.companyId=? and ru.consigner=?";
		return (RoleUser)roleUserDao.findUnique(hql, roleId,userId,companyId,sourceId);
	}
	/**
	 * 获取所有公司的用户
	 * @return List<User>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public static List<User> getAllUsers(){
		String hql = "from User u where u.deleted=0 order by u.weight desc";
		return userDao.find(hql);
	}
	/**
	 * 通过工作组ID获取工作组实体
	 * @param workGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Workgroup getWorkGroupById(Long workGroupId){
		if(workGroupId == null) 
			return null;
		return workGroupDao.get(workGroupId);
	}
	
	@SuppressWarnings("unchecked")
	public static Workgroup getWorkGroupByName(String name, Long companyId){
		List<Workgroup> workGroups = workGroupDao.find("from Workgroup wg where wg.company.id=? and wg.name=? ", companyId, name);
		if(workGroups.size() == 1){
			return workGroups.get(0);
		}
		return null;
	}
	
	/**
	 * 通过部门ID获取部门实体
	 * @param workGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Department getDepartmentById(Long departmentId){
		if(departmentId == null) 
			return null;
		return departmentDao.get(departmentId);
	}
	
	/**
	 * 通过部门名称获取部门实体
	 * @param name
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Department getDepartmentByName(String name, Long companyId){
		List<Department> depts = departmentDao.find("from Department d where d.company.id=? and d.departmentName=? and d.deleted=?", companyId, name, false);
		if(depts.size() == 1){
			return depts.get(0);
		}
		return null;
	}
	
	/**
	 * 保存注册用户信息
	 * @param userInfo
	 * @param workGroupId
	 * @param companyId
	 */
	public static void saveRegisterUser(UserInfo userInfo,Long workGroupId,Long companyId){
		
		userInfo.getUser().setCompanyId(companyId);
		userInfo.setCompanyId(companyId);
		userInfo.setPasswordUpdatedTime(new Date());
		userInfoDao.save(userInfo);
		
		WorkgroupUser workUser = new WorkgroupUser();
		workUser.setUser(userInfo.getUser());
		workUser.setWorkgroup(getWorkGroupById(workGroupId));
		workUser.setCompanyId(companyId);
		workGroupToUserDao.save(workUser);
		
	}
	
	/**
	 * 根据用户得到电话
	 * @param userInfo
	 * @param workGroupId
	 * @param companyId
	 */
	public static String getPhoneByUserId(Long userId,Long companyId){
		UserInfo userInfo=(UserInfo)userInfoDao.findUnique("from UserInfo ui where ui.user.id=? and ui.companyId=? ",userId ,companyId);
		if(userInfo.getTelephone()==null){
			return "";
		}else{
			return userInfo.getTelephone();
		}
	}
	
	
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<User> getUsersByCompany(Long companyId){
		return userDao.find("select distinct u FROM User u join u.departmentUsers du join du.department d  WHERE u.companyId=? AND u.deleted=? AND du.deleted=?  AND d.deleted=? ORDER BY u.weight DESC", companyId,false,false,false);
	}

	public static void setSessionFactory(SessionFactory sessionFactory) {
		AuthHelpAPI.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	public static Set<User> getUsersByRoleName(Long systemId, Long companyId, String roleName){
		List<Role> roles = roleDao.find("from Role r where r.businessSystem.id=? and r.name=? and r.deleted=?", systemId, roleName, false);
		if(roles.size() == 1){
			return getUsersByRole(systemId, companyId, roles.get(0).getCode());
		}
		return new HashSet<User>(0);
	}
	
	@SuppressWarnings("unchecked")
	public static Set<User> getUsersExceptRoleName(Long systemId, Long companyId, String roleName){
		Set<User> userSet = new HashSet<User>();
		List<Role> roles = roleDao.find("from Role r where r.businessSystem.id=? and r.name<>? and r.deleted=?", systemId, roleName, false);
		for(Role role: roles){
			userSet.addAll(getUsersByRole(systemId, companyId, role.getCode()));
		}
		return userSet;
	}

	/**
	 * 通过角色编号查询所有的用户（不含委托）
	 * @param systemId
	 * @param companyId
	 * @param roleCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set<User> getUsersByRole(Long systemId, Long companyId, String roleCode){
		Set<User> result = new LinkedHashSet<User>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u from User u join u.roleUsers ru join ru.role r join r.businessSystem rbs ");
		usersByRole.append("where rbs.id=? and  r.code = ? and u.companyId=? and r.deleted=false and ");
		usersByRole.append("ru.deleted=false and ru.consigner is null and u.deleted=false ");
		List<User> roleUsers = userDao.find(usersByRole.toString(), systemId, roleCode, companyId);
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r join r.businessSystem rbs ");
		usersByDeptRoleHql.append("where rbs.id=? and  r.code = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false ");
		List<User> roleDeptUsers = userDao.find(usersByDeptRoleHql.toString(), systemId, roleCode, companyId);
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r join r.businessSystem rbs ");
		usersByWgRoleHql.append("where rbs.id=? and r.code = ? and wg.company.id=? and rbs.deleted=false and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false ");
		List<User> roleWgUsers = userDao.find(usersByWgRoleHql.toString(), systemId, roleCode, companyId);
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleWgUsers);
		return result;
	}
	private static List<User> doSort(List<User> departments){
		for(int i=0;i<departments.size()-1;i++){
			for(int j=departments.size()-1;j>i;j--){
				if(!(departments.get(j).getWeight() instanceof Integer)){
					departments.get(j).setWeight(1);
				}
				if(!(departments.get(j-1).getWeight() instanceof Integer)){
					departments.get(j-1).setWeight(1);
				}
				if(departments.get(j).getWeight() > departments.get(j-1).getWeight()){
					User dt = departments.get(j);
					departments.set(j,departments.get(j-1));
					departments.set(j-1,dt);
				}
			}
		}
		return departments;
	}
	
	public static String getRtxUrl(Long companyId){
		String rtxurl="";
		ServerConfig  serverConfig= serverConfigDao.findUniqueByProperty("companyId", companyId);
		if(serverConfig!=null && serverConfig.getRtxUrl()!=null && isRtxInvocation(companyId)){
			rtxurl=serverConfig.getRtxUrl();
			if(rtxurl.endsWith("/")){
				rtxurl=rtxurl.substring(0,rtxurl.lastIndexOf("/"));
			}
		}
		return rtxurl;
	}
	
	/**
	 * 是否启用了rtx集成
	 * @param companyId
	 * */
	public static Boolean isRtxInvocation(Long companyId){
		ServerConfig  serverConfig= serverConfigDao.findUniqueByProperty("companyId", companyId);
		if(serverConfig!=null){
			return serverConfig.getRtxInvocation();
		}else{
			return false;
		}
	}
	
	/**
	 * 根据用户ID查询用户所在的部门
	 * @param companyId
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Department> getDepartmentsByUser(Long companyId,Long userId){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and d.deleted=? order by d.weight desc");
		return departmentDao.find(hql.toString(), companyId, userId, false, false, false);
	}
	
	/**
	 * 根据登录名查询用户信息
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public static User getUser(Long companyId, String loginName){
		return (User)userDao.findUnique("from User u where u.companyId=? and u.loginName=? and u.deleted=? ", companyId, loginName, false);
	}
	
	/**
	 * 根据邮件地址查询用户信息
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static User getUser(String email){
		List<User> list=userDao.find("from User u where u.email=? and u.deleted=? ",email, false);
		if(list!=null&&!list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 查询出该登录名外的其他用户的登录名
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set<String> getUserExceptLoginName(Long companyId,String loginName){
		return new HashSet<String>(userDao.find("select u.loginName from User u where u.companyId=? and u.loginName<>? and u.deleted=? ", companyId, loginName, false));
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Department> getDepartmentsByUser(Long companyId, String loginName){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.loginName =? and u.deleted=? and du.deleted=? and d.deleted=?");
		return departmentDao.find(hql.toString(), companyId, loginName, false, false, false);
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Department> getDepartmentsByUserLike(Long companyId, String name){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.name like ? and u.deleted=? and du.deleted=? and d.deleted=?");
		return departmentDao.find(hql.toString(), companyId, "%"+name+"%", false, false, false);
	}
	
	/**
	 * 根据公司ID和用户的登录名查询该用户所具有的角色的字符串表示
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public static Set<Role> getRolesByUser(Long companyId, String loginName){
		User user = getUser(companyId, loginName);
		return getRolesByUser(user.getId());
	}
	
	/**
	 * 根据公司ID和用户登录名查询该用户所在的工作组
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Workgroup> getWorkGroupsByUser(Long companyId, String loginName){
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.loginName=? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		return workGroupDao.find(hql.toString(), companyId, loginName, false, false, false);
	}
	
	/**
	 * 根据公司ID和用户登录名查询该用户所在的工作组
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Workgroup> getWorkGroupsByUserLike(Long companyId, String name){
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.name like ? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		return workGroupDao.find(hql.toString(), companyId, "%"+name+"%", false, false, false);
	}
	
	/**
	 * 查询所有的系统并排序
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<BusinessSystem> getSystems(){
		return businessSystemDao.find("from BusinessSystem bs where bs.deleted=? order by bs.id", false);
	}
	
	public static BusinessSystem getSystemByCode(String code){
		return businessSystemDao.findUniqueByProperty("code", code);
	}
	
	@SuppressWarnings("unchecked")
	public static List<User> getUsersByLoginNames(Long companyId, List<String> loginNames){
		StringBuilder hql = new StringBuilder("from User u where u.companyId=? and (");
		Object[] parameters = new Object[loginNames.size()+1];
		parameters[0] = companyId;
		int index = 1;
		for(String loginName : loginNames){
			parameters[index++] = loginName;
			hql.append(" u.loginName=? or");
		}
		hql.replace(hql.length()-2, hql.length(), "");
		hql.append(") and u.deleted=false order by u.weight desc");
		return userDao.find(hql.toString(), parameters);
	}
	
	
	@Transactional(readOnly = true)
	public static List<Role> getRolesListByUserExceptDelegateMain(Long userId){
		User user = userDao.get(userId);
		if(user == null) return null;
		return getRolesListByUserExceptDelegateMain(user);
	}
	
	/**
	 * 根据用户查询角色(不含委托)
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = true)
	public static List<Role> getRolesListByUserExceptDelegateMain(User user){
		if(user == null) return null;
		
		List<Role> roles = new ArrayList<Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner()!=null)continue;
			Role role = ru.getRole();
			if(!role.isDeleted()) {
				if(!roles.contains(role)){
					roles.add(role);
				}
			}
		}
		// 用户具有的部门拥有的角色
		Set<DepartmentUser> departmentUsers =  user.getDepartmentUsers();
		for(DepartmentUser du : departmentUsers){
			if(du.isDeleted() || du.getDepartment().isDeleted()) continue;
			for(RoleDepartment rd : du.getDepartment().getRoleDepartments()){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) {
					if(!roles.contains(rd.getRole())){
						roles.add(rd.getRole());
					}
				}
			}
		}
		// 用户具有的工作组拥有的角色
		Set<WorkgroupUser> workgroupUsers = user.getWorkgroupUsers();
		for(WorkgroupUser wu : workgroupUsers){
			if(wu.isDeleted() || wu.getWorkgroup().isDeleted()) continue;
			for(RoleWorkgroup rw : wu.getWorkgroup().getRoleWorkgroups()){
				if(!rw.isDeleted() && !rw.getRole().isDeleted()) {
					if(!roles.contains(rw.getRole())){
						roles.add(rw.getRole());
					}
				}
			}
		}
		return roles;
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门的上级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public static List<Department> getSuperiorDepartmentsByUser(Long companyId, String loginName){
		StringBuilder hql = new StringBuilder(" select d from Department d join d.children sd join sd.departmentUsers du join du.user u ");
		hql.append(" where u.companyId=? and u.loginName=? and u.deleted=? and du.deleted=? and sd.deleted=? and d.deleted=?");
		return departmentDao.find(hql.toString(), companyId, loginName, false, false, false,false);
	}
	/**
	 * 获得用户的顶级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public static List<Department> getUpstageDepartmentsByUser(Long companyId, String loginName){
		Set<Department> result = new HashSet<Department>();
		List<Department> departments = getDepartmentsByUser(companyId, loginName);
		for(Department department:departments){
			result.add(getFirstDegreeDepartment(department));
		}
		return new ArrayList<Department>(result);
	}
	
	/**
	 * 获得用户的顶级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public static List<Department> getUpstageDepartmentsByUserLike(Long companyId, String loginName){
		Set<Department> result = new HashSet<Department>();
		List<Department> departments = getDepartmentsByUserLike(companyId, loginName);
		for(Department department:departments){
			result.add(getFirstDegreeDepartment(department));
		}
		return new ArrayList<Department>(result);
	}

	/**
	 * 返回该部门的一级部门
	 * @param department
	 * @return
	 */
	public static Department getFirstDegreeDepartment(Department department){
		if(department.getParent()!=null){
			return getFirstDegreeDepartment(department.getParent());
		}else{
			return department;
		}
	}
	/**
	 * 员工查询
	 * @param department
	 * @return
	 */
	public static void userSearch(String userName ,String userDepart,  boolean userSex, Long companyId, Page<User> page){
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct u from User u join u.userInfos ui join u.departmentUsers du join du.department d ");
		hql.append("where u.companyId=? and u.deleted=? and ui.deleted=? and du.deleted=? and d.deleted=? ");
		hql.append("and u.name like ? and ui.sex=? and d.departmentName like ?");
		userDao.find(page,hql.toString(), companyId,false,false,false,false,"%" + userName + "%", userSex, "%" + userDepart+ "%");
		
	}
	/**
	 * 员工查询所有性别
	 * @param department
	 * @return
	 */
	public static void userSearchAllSex(String userName ,String userDepart,  Long companyId, Page<User> page){
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct u from User u join u.userInfos ui join u.departmentUsers du join du.department d ");
		hql.append("where u.companyId=? and u.deleted=? and ui.deleted=? and du.deleted=? and d.deleted=? ");
		hql.append("and u.name like ? and d.departmentName like ?");
		userDao.find(page,hql.toString(), companyId,false,false,false,false,"%" + userName + "%", "%" + userDepart+ "%");
		
	}
	/**
	 * 获取本公司所有用户的生日
	 * @return
	 */
	 @SuppressWarnings("unchecked")
	public static Map<Long,String> getUserBirthdayByCompany(Long companyId){
		List<UserInfo> userInfoList=userInfoDao.find("from UserInfo ui where ui.companyId=?  and ui.deleted=?",companyId,false);
		Map<Long,String> birthdayMap=new HashMap();
		for(int i=0;i<userInfoList.size();i++){
			if(StringUtils.isNotEmpty(userInfoList.get(i).getBirthday())){
			birthdayMap.put(userInfoList.get(i).getUser().getId(), userInfoList.get(i).getBirthday());
			}
		}
		return birthdayMap;
	}
    /**
	 * 得到无部门人员
	 * @return
	 */
	public static List<UserInfo> getNoDepartmentUsers(Long companyId){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT ACS_USERINFO.* FROM ACS_USERINFO LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USERINFO.ID DESC ");
		return userInfoDao.findByJdbc( sqlString.toString(), companyId);
	}
}
