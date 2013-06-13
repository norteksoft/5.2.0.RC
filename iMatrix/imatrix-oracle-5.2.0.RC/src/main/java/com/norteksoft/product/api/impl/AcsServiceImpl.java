package com.norteksoft.product.api.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.WorkgroupUser;
import com.norteksoft.acs.entity.sysSetting.ServerConfig;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.api.AcsService;
import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.product.api.entity.Department;

import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

@Service
@Transactional
public class AcsServiceImpl implements AcsService{
	public final static String DEPARTMENT = "department";
	public final static String WORKGROUP = "workgroup";
	public final static String DELETED = "deleted";
	protected SessionFactory sessionFactory;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Department, Long> departmentDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> depUserDao;
	private SimpleHibernateTemplate<WorkgroupUser, Long> workGroupToUserDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.User, Long> userDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.BusinessSystem, Long> businessSystemDao;
	private SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.Role, Long> roleDao;
	private SimpleHibernateTemplate<LoginLog, Long> loginUserLogDao;
	private SimpleHibernateTemplate<ServerConfig, Long> serverConfigDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private DepartmentManager departmentManager;
	@Autowired
	private CompanyManager companyManager;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		departmentDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Department, Long>(sessionFactory, com.norteksoft.acs.entity.organization.Department.class);
		workGroupDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.Workgroup, Long>(sessionFactory, com.norteksoft.acs.entity.organization.Workgroup.class);
		depUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(sessionFactory, DepartmentUser.class);
		workGroupToUserDao = new SimpleHibernateTemplate<WorkgroupUser, Long>(sessionFactory, WorkgroupUser.class);
		userDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.User, Long>(sessionFactory,com.norteksoft.acs.entity.organization.User.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory,UserInfo.class);
		businessSystemDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.BusinessSystem, Long>(sessionFactory,com.norteksoft.acs.entity.authorization.BusinessSystem.class);
		roleUserDao = new SimpleHibernateTemplate<RoleUser, Long>(sessionFactory,RoleUser.class);
		roleDao = new SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.Role, Long>(sessionFactory,com.norteksoft.acs.entity.authorization.Role.class);
		loginUserLogDao = new SimpleHibernateTemplate<LoginLog, Long>(sessionFactory, LoginLog.class); 
		serverConfigDao=new SimpleHibernateTemplate<ServerConfig, Long>(sessionFactory, ServerConfig.class);
	}

	protected SessionFactory getSessionFactory() {
		sessionFactory = (SessionFactory)ContextUtils.getBean("sessionFactory");
		return sessionFactory;
	}
	
	private Long getCompanyId(){
		Long id = ContextUtils.getCompanyId();
		if(id == null) throw new RuntimeException("公司ID为空");
		return id;
	}
	/**
	 * 请使用  getOnlineUserCount()
	 */
	@Deprecated
	public Long getOnlineUserCount(Long companyId){
		return getOnlineUserCount();
	}
	
	/**
	 * 查询在线用户数量
	 * @param companyId
	 * @return
	 */
	public Long getOnlineUserCount(){
		return loginUserLogDao.findLong(
				"select count(u) from LoginLog u where u.exitTime is null and u.companyId=? and u.deleted=?", 
				getCompanyId(), false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getOnlineUserIds(){
		return loginUserLogDao.find(
				"select u.userId from LoginLog u where u.exitTime is null and u.companyId=? and u.deleted=?", 
				getCompanyId(), false);
	}

	/**
	 * 请使用  getDepartments()
	 */
	@Deprecated
	public List<Department> getDepartmentList(Long companyId) {
		return getDepartments();
	}
	
	/**
	 * 根据公司ID查询该公司所有的部门
	 * 
	 * @param companyId 公司ID
	 * @return List<Department>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getDepartments() {
		List<com.norteksoft.acs.entity.organization.Department> list =  departmentDao.find(
				"FROM Department d WHERE d.company.id=? AND d.deleted=? and d.parent.id is null ORDER BY d.weight desc", 
				getCompanyId(), false);
		return BeanUtil.turnToModelDepartmentList(list);
	}

	/**
	 *  请使用  getWorkgroups()
	 */
	@Deprecated
	public List<Workgroup> getWorkGroupList(Long companyId) {
		return getWorkgroups();
	}

	/**
	 * 根据公司ID查询该公司所有的工作组
	 * 
	 * @param companyId 公司ID
	 * @return List<WorkGroup>
	 */
	@Transactional(readOnly = true)
	public List<Workgroup> getWorkgroups() {
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroupList = workGroupDao.findByCriteria(
				Restrictions.eq("company.id", getCompanyId()), Restrictions.eq(
						DELETED, false));
		return BeanUtil.turnToModelWorkgroupList(workGroupList);
	}
	
	/**
	 * 请使用  getUsersByDepartmentId
	 */
	@Deprecated
	public List<com.norteksoft.acs.entity.organization.User> getUserListByDepartmentId(Long departmentId) {
		if(departmentId == null) throw new RuntimeException("没有给定查询用户集合的查询条件：部门ID");
		String hql = "select u FROM DepartmentUser d join d.user  u WHERE u.deleted=? and d.department.id=? AND d.deleted=? order by u.weight desc";
		return  depUserDao.find(hql, false,departmentId,false);
	}

	
	/**
	 * 根据部门ID查询该部门所有的人员
	 * 
	 * @param departmentId 部门Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	public List<User> getUsersByDepartmentId(Long departmentId) {
		if(departmentId == null) throw new RuntimeException("没有给定查询用户集合的查询条件：部门ID");
		List<Object[]> list = getUsersByDepartment(departmentId);
		return BeanUtil.turnToModelUserList1(list);
	}
	/**
	 * 根据部门ID查询该部门所有的人员
	 * 
	 * @param departmentId 部门Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Object[]> getUsersByDepartment(Long departmentId) {
		if(departmentId == null) throw new RuntimeException("没有给定查询用户集合的查询条件：部门ID");
		String hql = "select u,ui FROM DepartmentUser d join d.user  u join u.userInfos ui WHERE u.deleted=? and d.department.id=? AND d.deleted=? order by u.weight desc";
		return  depUserDao.find(hql, false,departmentId,false);
	}
	/**
	 * 请使用 getUserLoginNamesByDepartmentName
	 */
	@Deprecated
	public List<String> getUserLoginNameListByDepartmentName(String departmentName,Long companyId) {
		return getUserLoginNamesByDepartmentName(departmentName);
	}
	
	/**
	 * 根据部门名称得到部门下用户的登录名
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<String> getUserLoginNamesByDepartmentName(String departmentName) {
		if(StringUtils.isEmpty(departmentName)) throw new RuntimeException("没有给定查询用户登录名的查询条件：部门名称");
		String dhql ="from Department d where d.name=? and d.deleted=? and d.company.id=?";
		com.norteksoft.acs.entity.organization.Department department=(com.norteksoft.acs.entity.organization.Department)departmentDao.findUnique(dhql, departmentName,false,getCompanyId());
		List<String> userLoginNameList = new ArrayList<String>();
		String hql = "FROM DepartmentUser d WHERE d.department.id=? AND d.deleted=? ORDER BY d.user.weight DESC";
		List<DepartmentUser> list = depUserDao.find(hql, department.getId(),false);
		for (DepartmentUser departmentToUser : list) {
			com.norteksoft.acs.entity.organization.User user = departmentToUser.getUser();
			if(user!=null&&!user.isDeleted()){
				userLoginNameList.add(user.getLoginName());
			}
		}
		return userLoginNameList;
	}

	/**
	 * 请使用 getUsersByWorkgroupId
	 */
	@Deprecated
	public List<com.norteksoft.acs.entity.organization.User> getUserListByWorkGroupId(Long workgroupId) {
		if(workgroupId == null) throw new RuntimeException("没有给定查询用户集合的查询条件： 工作组ID");
		String hql = "select u FROM WorkgroupUser d join d.user u WHERE u.deleted=? and  d.workgroup.id=? AND d.deleted=? ORDER BY d.user.weight DESC";
		return workGroupDao.find(hql,false, workgroupId,false);
	}
	
	/**
	 * 根据工作组ID查询该工作组所有的人员
	 * 
	 * @param workGroupId 工作组Id
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<User> getUsersByWorkgroupId(Long workgroupId) {
		if(workgroupId == null) throw new RuntimeException("没有给定查询用户集合的查询条件： 工作组ID");
		String hql = "select u FROM WorkgroupUser d join d.user u WHERE u.deleted=? and  d.workgroup.id=? AND d.deleted=? ORDER BY d.user.weight DESC";
		List<com.norteksoft.acs.entity.organization.User> list = workGroupDao.find(hql,false, workgroupId,false);
		return BeanUtil.turnToModelUserList(list);
	}

	/**
	 * 根据父部门id查询该父部门下所有子部门
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getSubDepartmentList(Long paternDepartmentId) {
		if(paternDepartmentId == null) throw new RuntimeException("没有给定查询子部门集合的查询条件： 父部门ID");
		List<com.norteksoft.acs.entity.organization.Department> list =  departmentDao.find(
				"FROM Department d WHERE d.parent.id=? AND d.deleted=?  ORDER BY d.weight desc", 
				paternDepartmentId, false);
		return BeanUtil.turnToModelDepartmentList(list);
	}

	/**
	 * 根据用户Id得到用户实体
	 * @return User
	 */
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		if (id instanceof Long)
			return BeanUtil.turnToModelUser(userDao.get(id));
		return null;
	}

	/**
	 * 获取当前用户所有角色的字符串表示形式(即角色编码以逗号隔开)
	 * @return String
	 */
	@Transactional(readOnly = true)
	public String getCurrentUserRoles(){
		Long userId = ContextUtils.getUserId();
		if(userId == null) return "";
		
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return "";
		
		return getRoleCodesFromUser(user);
	}
	
	@Transactional(readOnly = true)
	public String getCurrentUserRoles(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return "";
		
		return getRoleCodesFromUser(user);
	}
	
	@Deprecated
	public Set<Role> getRolesByUserId(Long userId,Long consigner,Long companyId){
		return getTrustedRolesByUserId(userId, consigner);
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
	public Set<Role> getTrustedRolesByUserId(Long trusteeId, Long trustorId){
		if(trusteeId == null) throw new RuntimeException("没有给定查询委托角色的查询条件：受托人ID");
		if(trustorId == null) throw new RuntimeException("没有给定查询委托角色的查询条件：委托人ID");
		String hql="FROM RoleUser ru WHERE ru.consigner=? AND ru.user.id=? AND ru.companyId=?";
		List<RoleUser> roleUsers = roleUserDao.find(hql, trustorId, trusteeId, getCompanyId());
		Set<Role> roles = new HashSet<Role>();
		for(RoleUser ru : roleUsers){
			Role role=BeanUtil.turnToModelRole(roleDao.get(ru.getRole().getId()));
			roles.add(role);
		}
		return roles;
	}
	
	/**
	 * 根据用户获取用户的角色字符串形式（不含委托）
	 */
	@Deprecated
	public String getRoleCodesFromUser(com.norteksoft.acs.entity.organization.User user){
		return getRolesExcludeTrustedRole(user);
	}
	
	@Deprecated
	public String getRolesExcludeTrustedRole(com.norteksoft.acs.entity.organization.User user){
		if(user == null) return "";
		Set<com.norteksoft.acs.entity.authorization.Role> roles = new HashSet<com.norteksoft.acs.entity.authorization.Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner()!= null) continue;
			com.norteksoft.acs.entity.authorization.Role role = ru.getRole();
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
		List< com.norteksoft.acs.entity.authorization.Role> roleList = new ArrayList<com.norteksoft.acs.entity.authorization.Role>();
		roleList.addAll(roles);
		//角色按权重排序
		sortRoles(roleList);
		// 生成字符串形式
		StringBuilder roleStrings = new StringBuilder();
		for(com.norteksoft.acs.entity.authorization.Role role : roleList){
			roleStrings.append(role.getCode()).append(",");
		}
		// 去掉最后一个逗号
		if(roleStrings.lastIndexOf(",") != -1 && roleStrings.lastIndexOf(",") == roleStrings.length()-1){
			roleStrings.replace(roleStrings.length()-1, roleStrings.length(), "");
		}
		return roleStrings.toString();
	}
	
	@SuppressWarnings("unchecked")
	public String getRolesExcludeTrustedRole(User user){
		if(user == null) return "";
		Set<com.norteksoft.acs.entity.authorization.Role> roles = new HashSet<com.norteksoft.acs.entity.authorization.Role>();
		// 用户具有的角色
		String hql = "select r from Role r join r.roleUsers ru where r.deleted=? and ru.deleted=? and ru.user is not null and ru.user.id=? and ru.consigner is not null";
		List<com.norteksoft.acs.entity.authorization.Role> userRoles = roleDao.find(hql, false,false,user.getId());
		roles.addAll(userRoles);
		// 用户具有的部门拥有的角色
		 hql = "select r from Role r join r.roleDepartments rd join rd.department d join d.departmentUsers du where r.deleted=? and rd.deleted=? and d.deleted=? and du.deleted=? and du.user is not null and du.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> deptRoles = roleDao.find(hql, false,false,false,false,user.getId());
		 roles.addAll(deptRoles);
		// 用户具有的工作组拥有的角色
		 hql = "select r from Role r join r.roleWorkgroups rw join rw.workgroup wg join wg.workgroupUsers wu  where r.deleted=? and rw.deleted=? and wg.deleted=? and wu.deleted=?  and wu.user is not null and wu.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> workgroupRoles  = roleDao.find(hql, false,false,false,false,user.getId());
		 roles.addAll(workgroupRoles);
		List< com.norteksoft.acs.entity.authorization.Role> roleList = new ArrayList<com.norteksoft.acs.entity.authorization.Role>();
		roleList.addAll(roles);
		//角色按权重排序
		sortRoles(roleList);
		// 生成字符串形式
		StringBuilder roleStrings = new StringBuilder();
		for(com.norteksoft.acs.entity.authorization.Role role : roleList){
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
	public Set<Role> getRolesByUser(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return null;
		User modeUser = BeanUtil.turnToModelUser(user);
		return getRolesByUser(modeUser);
	}
	
	@Deprecated
	public Set<Role> getRolesByUser(com.norteksoft.acs.entity.organization.User user){
		if(user == null) return null;
		
		Set<Role> roles = new HashSet<Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner() != null) continue;
			Role role = BeanUtil.turnToModelRole(ru.getRole());
			if(!role.isDeleted()) roles.add(role);
		}
		// 用户具有的部门拥有的角色
		Set<DepartmentUser> departmentUsers =  user.getDepartmentUsers();
		for(DepartmentUser du : departmentUsers){
			if(du.isDeleted() || du.getDepartment().isDeleted()) continue;
			for(RoleDepartment rd : du.getDepartment().getRoleDepartments()){
				if(!rd.isDeleted() && !rd.getRole().isDeleted()) roles.add(BeanUtil.turnToModelRole(rd.getRole()));
			}
		}
		// 用户具有的工作组拥有的角色
		Set<WorkgroupUser> workgroupUsers = user.getWorkgroupUsers();
		for(WorkgroupUser wu : workgroupUsers){
			if(wu.isDeleted() || wu.getWorkgroup().isDeleted()) continue;
			for(RoleWorkgroup rw : wu.getWorkgroup().getRoleWorkgroups()){
				if(!rw.isDeleted() && !rw.getRole().isDeleted()) roles.add(BeanUtil.turnToModelRole(rw.getRole()));
			}
		}
		return roles;
	}
	
	/**
	 * 根据用户查询用户的角色（不含委托）
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesByUser(User user){
		if(user == null) return null;
		
		Set<Role> roles = new HashSet<Role>();
		Set<com.norteksoft.acs.entity.authorization.Role> oldRoles = new HashSet<com.norteksoft.acs.entity.authorization.Role>();
		// 用户具有的角色
		String hql = "select r from Role r join r.roleUsers ru where r.deleted=? and ru.deleted=? and ru.user is not null and ru.user.id=? and ru.consigner is null";
		List<com.norteksoft.acs.entity.authorization.Role> userRoles = roleDao.find(hql, false,false,user.getId());
		oldRoles.addAll(userRoles);
		// 用户具有的部门拥有的角色
		 hql = "select r from Role r join r.roleDepartments rd join rd.department d join d.departmentUsers du where r.deleted=? and rd.deleted=? and d.deleted=? and du.deleted=? and du.user is not null and du.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> deptRoles = roleDao.find(hql, false,false,false,false,user.getId());
		 oldRoles.addAll(deptRoles);
		// 用户具有的工作组拥有的角色
		 hql = "select r from Role r join r.roleWorkgroups rw join rw.workgroup wg join wg.workgroupUsers wu  where r.deleted=? and rw.deleted=? and wg.deleted=? and wu.deleted=?  and wu.user is not null and wu.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> workgroupRoles  = roleDao.find(hql, false,false,false,false,user.getId());
		 oldRoles.addAll(workgroupRoles);
		 roles = BeanUtil.turnToModelRoleSet(oldRoles);
		return roles;
	}
	
	
	@Transactional(readOnly = true)
	public List<Role> getRolesListByUser(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return null;
		User modeUser = BeanUtil.turnToModelUser(user);
		return getRolesListByUser(modeUser);
	}
	
	@Deprecated
	public List<Role> getRolesListByUser(com.norteksoft.acs.entity.organization.User user){
		if(user == null) return null;
		
		List<Role> roles = new ArrayList<Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner() != null) continue;
			Role role = BeanUtil.turnToModelRole(ru.getRole());
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
						roles.add(BeanUtil.turnToModelRole(rd.getRole()));
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
						roles.add(BeanUtil.turnToModelRole(rw.getRole()));
					}
				}
			}
		}
		//角色按权重排序
		sortRole(roles);
		return roles;
	}
	
	/**
	 * 根据用户查询用户角色（不含委托）
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getRolesListByUser(User user){
		if(user == null) return null;
		
		List<Role> roles = new ArrayList<Role>();
		Set<com.norteksoft.acs.entity.authorization.Role> oldRoles = new HashSet<com.norteksoft.acs.entity.authorization.Role>();
		// 用户具有的角色
		String hql = "select r from Role r join r.roleUsers ru where r.deleted=? and ru.deleted=? and ru.user is not null and ru.user.id=? and ru.consigner is null";
		List<com.norteksoft.acs.entity.authorization.Role> userRoles = roleDao.find(hql, false,false,user.getId());
		oldRoles.addAll(userRoles);
		// 用户具有的部门拥有的角色
		 hql = "select r from Role r join r.roleDepartments rd join rd.department d join d.departmentUsers du where r.deleted=? and rd.deleted=? and d.deleted=? and du.deleted=? and du.user is not null and du.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> deptRoles = roleDao.find(hql, false,false,false,false,user.getId());
		 oldRoles.addAll(deptRoles);
		// 用户具有的工作组拥有的角色
		 hql = "select r from Role r join r.roleWorkgroups rw join rw.workgroup wg join wg.workgroupUsers wu  where r.deleted=? and rw.deleted=? and wg.deleted=? and wu.deleted=?  and wu.user is not null and wu.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> workgroupRoles  = roleDao.find(hql, false,false,false,false,user.getId());
		 oldRoles.addAll(workgroupRoles);
		 Set<Role> modeRoles = BeanUtil.turnToModelRoleSet(oldRoles);
		 if(modeRoles!=null){
			 roles.addAll(modeRoles);
		 }
		//角色按权重排序
		sortRole(roles);
		return roles;
	}
	
	/**
	 * 获取租户名称
	 * @param businessSystemId
	 * @return
	 */
	public String getBusinessSystemNameById(Long businessSystemId){
		com.norteksoft.acs.entity.authorization.BusinessSystem entity = businessSystemDao.get(businessSystemId);
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
	public List<User> getUsersNotInDepartment(Long companyId){
		return getUsersWithoutDepartment();
	}
	
	public List<User> getUsersWithoutDepartment(){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT u.* FROM ACS_USERINFO ");
		sqlString.append("inner join ACS_USER u on ACS_USERINFO.FK_USER_ID=u.id ");
		sqlString.append("LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USERINFO.ID DESC ");
		
		return BeanUtil.turnToModelUserList(userDao.findByJdbc(sqlString.toString(), ContextUtils.getCompanyId()));
	}
	
	/**
	 * 获取不属于任何部门的用户
	 * @return page
	 */
	public Page<UserInfo> getNoDepartmentUsers(Page<UserInfo> page,Long companyId){
        if(companyId == null) return null;
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT * FROM ACS_USER LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USER.ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USER.DELETED=0 AND ACS_USER.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USER.WEIGHT DESC");
		List<com.norteksoft.acs.entity.organization.User>us =userDao.findByJdbc(sqlString.toString(), companyId);
		List<UserInfo> uiList = new ArrayList<UserInfo>();
		for(com.norteksoft.acs.entity.organization.User u : us){
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
	
	public void assignRolesToSomeone(Long someoneId,String[] roleIds,Long companyId,Long sourceUserId){
		assignTrustedRole(sourceUserId, roleIds, someoneId);
	}
	
	public void assignTrustedRole(Long trustorId, String[]roleIds, Long trusteeId){
		if(trustorId == null) throw new RuntimeException("没有给定委托角色时的委托人");
		if(roleIds == null) throw new RuntimeException("没有给定需要委托的角色集合");
		if(trusteeId == null) throw new RuntimeException("没有给定委托角色时的受托人");
		for(int i=0;i<roleIds.length;i++){
			if(StringUtils.isNotEmpty(roleIds[i])){
				if((getRoleUserBySourceId(trusteeId, Long.parseLong(roleIds[i]), getCompanyId(),trustorId))==null){
					RoleUser roleUser = new RoleUser();
					com.norteksoft.acs.entity.authorization.Role role = roleDao.get(Long.parseLong(roleIds[i]));
					com.norteksoft.acs.entity.organization.User user = userDao.get(trusteeId);
					roleUser.setRole(role);
					roleUser.setUser(user);
					roleUser.setCompanyId(getCompanyId());
					roleUser.setConsigner(trustorId);
					roleUserDao.save(roleUser);
				}else{
					RoleUser roleUser=getRoleUserBySourceId(trusteeId, Long.parseLong(roleIds[i]), getCompanyId(), trustorId);
					roleUser.setDeleted(false);
					roleUserDao.save(roleUser);
				}
			}
		}
	}
	
	public void deleteRoleUsers(Long userId,String[] rIds,Long companyId)	{
		if(rIds==null) return;
		for(int j=0;j<rIds.length;j++){
			RoleUser roleUser=getRoleUserByRelation(userId,Long.parseLong(rIds[j]),companyId);
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
	public void deleteRoleUsers(Long userId,String[] rIds,Long companyId,Long sourceId)	{
		deleteTrustedRole(sourceId, rIds, userId);
	}
	
	public void deleteTrustedRole(Long trustorId, String[]roleIds,Long trusteeId){
		if(trustorId == null) throw new RuntimeException("没有给定解除委托角色时的委托人");
		if(trusteeId == null) throw new RuntimeException("没有给定解除委托角色时的受托人");
		if(roleIds==null) return;
		for(int j=0;j<roleIds.length;j++){
			RoleUser roleUser=getRoleUserBySourceId(trusteeId,Long.parseLong(roleIds[j]),getCompanyId(),trustorId);
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
	public Role getRoleById(Long roleId){
		Role role = BeanUtil.turnToModelRole(roleDao.get(roleId));
		return role;
	}
	/**
	 * 根据userId得到name
	 * @param companyId
	 */
	public String getNameByUserId(){
		String hql="from User u where u.id=? ";
		com.norteksoft.acs.entity.organization.User user=(com.norteksoft.acs.entity.organization.User) userDao.findUnique(hql, ContextUtils.getUserId());
		return user.getName();
	}
	
	
	/**
	 * 删除由别人分配的权限
	 * @param sourceId
	 * @param userId
	 * @param companyId
	 */
	public void deleteAssignedAuthority(Long sourceId,Long userId,Long companyId){
		deleteAllTrustedRole(sourceId, userId);
	}
	
	@SuppressWarnings("unchecked")
	public void deleteAllTrustedRole(Long trustorId, Long trusteeId){
		if(trustorId == null) throw new RuntimeException("没有给定删除角色委托关系时的委托人");
		if(trusteeId == null) throw new RuntimeException("没有给定删除角色委托关系时的受托人");
		String hql = "FROM RoleUser ru WHERE ru.consigner=? AND ru.user.id=? AND ru.companyId=?";
		List<RoleUser> roleUsers = roleUserDao.find(hql, trustorId,trusteeId,getCompanyId());
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
	public RoleUser getRoleUserByRelation(Long userId,Long roleId,Long companyId){
		String hql = "FROM RoleUser ru WHERE ru.role.id=? AND ru.user.id=? AND ru.companyId=?";
		return (RoleUser)roleUserDao.findUnique(hql, roleId,userId,companyId);
	}
	
	/**
	 * 按条件获取角色用户表数据
	 * @param userId
	 * @param roleId
	 * @param companyId
	 */
	public RoleUser getRoleUserBySourceId(Long userId,Long roleId,Long companyId,Long sourceId){
		String hql = "FROM RoleUser ru WHERE ru.role.id=? AND ru.user.id=? AND ru.companyId=? and ru.consigner=?";
		return (RoleUser)roleUserDao.findUnique(hql, roleId,userId,companyId,sourceId);
	}
	/**
	 * 获取所有公司的用户
	 * @return List<User>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> getAllUsers(){
		String hql = "from User u where u.deleted=0 order by u.weight desc";
		List<com.norteksoft.acs.entity.organization.User> list =  userDao.find(hql);
		return BeanUtil.turnToModelUserList(list);
	}
	/**
	 * 通过工作组ID获取工作组实体
	 * @param workGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Workgroup getWorkgroupById(Long workGroupId){
		if(workGroupId == null) 
			return null;
		return BeanUtil.turnToModelWorkgroup(workGroupDao.get(workGroupId));
	}
	
	public Workgroup getWorkGroupByName(String name, Long companyId){
		return getWorkgroupByName(name);
	}
	
	@SuppressWarnings("unchecked")
	public Workgroup getWorkgroupByName(String name){
		if(name == null) throw new RuntimeException("没有给定查询工作组时的查询条件：工作组名称");
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroups = workGroupDao.find("from Workgroup wg where wg.company.id=? and wg.name=? ", getCompanyId(), name);
		if(workGroups.size() == 1){
			return BeanUtil.turnToModelWorkgroup(workGroups.get(0));
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Workgroup getWorkgroupByCode(String code){
		if(code == null) throw new RuntimeException("没有给定查询工作组时的查询条件：工作组编号");
		List<com.norteksoft.acs.entity.organization.Workgroup> workGroups = workGroupDao.find("from Workgroup wg where wg.company.id=? and wg.code=? ", getCompanyId(), code);
		if(workGroups.size() == 1){
			return BeanUtil.turnToModelWorkgroup(workGroups.get(0));
		}
		return null;
	}
	
	/**
	 * 通过部门ID获取部门实体
	 * @param workGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Department getDepartmentById(Long departmentId){
		if(departmentId == null) 
			return null;
		return BeanUtil.turnToModelDepartment(departmentDao.get(departmentId));
	}
	
	/**
	 * 通过部门名称获取部门实体
	 * @param name
	 * @param companyId
	 * @return
	 */
	public Department getDepartmentByName(String name, Long companyId){
		return getDepartmentByName(name);
	}
	
	@SuppressWarnings("unchecked")
	public Department getDepartmentByName(String name){
		if(name == null) throw new RuntimeException("没有给定查询部门时的查询条件：部门名称");
		List<com.norteksoft.acs.entity.organization.Department> depts = departmentDao.find("from Department d where d.company.id=? and d.name=? and d.deleted=?", getCompanyId(), name, false);
		if(depts.size() == 1){
			return BeanUtil.turnToModelDepartment(depts.get(0));
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Department getDepartmentByCode(String code){
		if(code == null) throw new RuntimeException("没有给定查询部门时的查询条件：部门编号");
		List<com.norteksoft.acs.entity.organization.Department> depts = departmentDao.find("from Department d where d.company.id=? and d.code=? and d.deleted=?", getCompanyId(), code, false);
		if(depts.size() == 1){
			return BeanUtil.turnToModelDepartment(depts.get(0));
		}
		return null;
	}
	
	/**
	 * 保存注册用户信息
	 * @param userInfo
	 * @param workGroupId
	 * @param companyId
	 */
	public void saveRegisterUser(UserInfo userInfo,Long workGroupId,Long companyId){
		
		userInfo.getUser().setCompanyId(companyId);
		userInfo.setCompanyId(companyId);
		userInfo.setPasswordUpdatedTime(new Date());
		userInfoDao.save(userInfo);
		
		WorkgroupUser workUser = new WorkgroupUser();
		workUser.setUser(userInfo.getUser());
		workUser.setWorkgroup(BeanUtil.turnToWorkgroup(getWorkgroupById(workGroupId)));
		workUser.setCompanyId(companyId);
		workGroupToUserDao.save(workUser);
		
	}
	
	/**
	 * 根据用户得到电话
	 * @param userInfo
	 * @param workGroupId
	 * @param companyId
	 */
	public String getPhoneByUserId(Long userId,Long companyId){
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
	public List<User> getUsersByCompany(Long companyId){
		if(companyId == null) throw new RuntimeException("没有给定查询用户列表的查询条件：公司ID");
		List<com.norteksoft.acs.entity.organization.User> list = userDao.find("select distinct u FROM User u join u.departmentUsers du join du.department d  WHERE u.companyId=? AND u.deleted=? AND du.deleted=?  AND d.deleted=? ORDER BY u.weight DESC", companyId,false,false,false);
		return BeanUtil.turnToModelUserList(list);
	}
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getAllUsersByCompany(Long companyId){
		if(companyId == null) throw new RuntimeException("没有给定查询用户列表的查询条件：公司ID");
		List<com.norteksoft.acs.entity.organization.User> list = userDao.find("select distinct u FROM User u  WHERE u.companyId=? AND u.deleted=?  ORDER BY u.weight DESC", companyId,false );
		return BeanUtil.turnToModelUserList(list);
	}
	
	public Set<User> getUsersByRoleName(Long systemId, Long companyId, String roleName){
		return getUsersByRoleName(systemId, roleName);
	}
	
	@SuppressWarnings("unchecked")
	public Set<User> getUsersByRoleName(Long systemId, String roleName){
		if(systemId == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：系统ID");
		if(roleName == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：角色名称");
		List<com.norteksoft.acs.entity.authorization.Role> roles = roleDao.find("from Role r where r.businessSystem.id=? and r.name=? and r.deleted=?", systemId, roleName, false);
		if(roles.size() == 1){
			return getUsersByRole(systemId, getCompanyId(), roles.get(0).getCode());
		}
		return new HashSet<User>(0);
	}
	
	public Set<User> getUsersExceptRoleName(Long systemId, Long companyId, String roleName){
		return getUsersWithoutRoleName(systemId, roleName);
	}
	
	@SuppressWarnings("unchecked")
	public Set<User> getUsersWithoutRoleName(Long systemId, String roleName){
		if(systemId == null) throw new RuntimeException("没有给定查询没有某角色的用户列表的查询条件：系统ID");
		if(roleName == null) throw new RuntimeException("没有给定查询没有某角色的用户列表的查询条件：角色名称");
		Set<User> userSet = new HashSet<User>();
		List<com.norteksoft.acs.entity.authorization.Role> roles = roleDao.find("from Role r where r.businessSystem.id=? and r.name<>? and r.deleted=?", systemId, roleName, false);
		for(com.norteksoft.acs.entity.authorization.Role role: roles){
			userSet.addAll(getUsersByRole(systemId, getCompanyId(), role.getCode()));
		}
		return userSet;
	}

	@SuppressWarnings("unchecked")
	public Set<User> getUsersWithoutRoleCode(Long systemId, String roleCode){
		if(systemId == null) throw new RuntimeException("没有给定查询没有某角色的用户列表的查询条件：系统ID");
		if(roleCode == null) throw new RuntimeException("没有给定查询没有某角色的用户列表的查询条件：角色编号");
		Set<User> userSet = new HashSet<User>();
		List<com.norteksoft.acs.entity.authorization.Role> roles = roleDao.find("from Role r where r.businessSystem.id=? and r.code<>? and r.deleted=?", systemId, roleCode, false);
		for(com.norteksoft.acs.entity.authorization.Role role: roles){
			userSet.addAll(getUsersByRole(systemId, getCompanyId(), role.getCode()));
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
	public Set<User> getUsersByRole(Long systemId, Long companyId, String roleCode){
		return getUsersByRoleCodeExceptTrustedRole(systemId, roleCode);
	}
	
	@SuppressWarnings("unchecked")
	public Set<User> getUsersByRoleCodeExceptTrustedRole(Long systemId, String roleCode){
		if(systemId == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：系统ID");
		if(roleCode == null) throw new RuntimeException("没有给定查询拥有某角色的用户列表的查询条件：角色编号");
		Set<com.norteksoft.acs.entity.organization.User> result = new LinkedHashSet<com.norteksoft.acs.entity.organization.User>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u from User u join u.roleUsers ru join ru.role r join r.businessSystem rbs ");
		usersByRole.append("where rbs.id=? and  r.code = ? and u.companyId=? and r.deleted=false and ");
		usersByRole.append("ru.deleted=false and ru.consigner is null and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleUsers = userDao.find(usersByRole.toString(), systemId, roleCode, getCompanyId());
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r join r.businessSystem rbs ");
		usersByDeptRoleHql.append("where rbs.id=? and  r.code = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleDeptUsers = userDao.find(usersByDeptRoleHql.toString(), systemId, roleCode, getCompanyId());
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r join r.businessSystem rbs ");
		usersByWgRoleHql.append("where rbs.id=? and r.code = ? and wg.company.id=? and rbs.deleted=false and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false ");
		List<com.norteksoft.acs.entity.organization.User> roleWgUsers = userDao.find(usersByWgRoleHql.toString(), systemId, roleCode, getCompanyId());
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleWgUsers);
		return BeanUtil.turnToModelUserSet(result);
	}
	
	
	public String getRtxUrl(Long companyId){
		return getRtxUrl();
	}
	
	public String getRtxUrl(){
		String rtxurl="";
		ServerConfig  serverConfig= serverConfigDao.findUniqueByProperty("companyId", getCompanyId());
		if(serverConfig!=null && serverConfig.getRtxUrl()!=null && isRtxInvocation(getCompanyId())){
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
	public Boolean isRtxInvocation(Long companyId){
		return isRtxEnable();
	}
	
	public Boolean isRtxEnable(){
		ServerConfig  serverConfig= serverConfigDao.findUniqueByProperty("companyId", getCompanyId());
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
	public List<Department> getDepartmentsByUser(Long companyId,Long userId){
		return getDepartmentsByUserId(userId);
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentsByUserId(Long userId){
		if(userId == null) throw new RuntimeException("没有给定查询用户所在部门列表的查询条件：用户ID");
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and d.deleted=? order by d.weight desc");
		List<com.norteksoft.acs.entity.organization.Department> list = departmentDao.find(hql.toString(), getCompanyId(), userId, false, false, false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	
	/**
	 * 根据登录名查询用户信息
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public User getUser(Long companyId, String loginName){
		return getUserByLoginName(loginName);
	}
	
	public User getUserByLoginName(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定查询用户的查询条件：用户登录名");
		com.norteksoft.acs.entity.organization.User oldUser= (com.norteksoft.acs.entity.organization.User)userDao.findUnique("from User u where u.companyId=? and u.loginName=? and u.deleted=? ", getCompanyId(), loginName, false);
		return BeanUtil.turnToModelUser(oldUser);
	}
	
	public com.norteksoft.acs.entity.organization.User getUserByLoginNameOld(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定查询用户的查询条件：用户登录名");
		com.norteksoft.acs.entity.organization.User oldUser= (com.norteksoft.acs.entity.organization.User)userDao.findUnique("from User u where u.companyId=? and u.loginName=? and u.deleted=? ", getCompanyId(), loginName, false);
		return oldUser;
	}
	
	public User getUser(String email){
		return getUserByEmail(email);
	}
	
	/**
	 * 根据邮件地址查询用户信息
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public User getUserByEmail(String email){
		if(StringUtils.isEmpty(email)) throw new RuntimeException("没有给定查询用户的查询条件：用户邮件地址");
		List<com.norteksoft.acs.entity.organization.User> list=userDao.find("from User u where u.email=? and u.deleted=? ",email, false);
		if(list!=null&&!list.isEmpty()){
			return BeanUtil.turnToModelUser(list.get(0));
		}
		return null;
	}
	
	public Set<String> getUserExceptLoginName(Long companyId,String loginName){
		return getLoginNamesExclude(loginName);
	}
	
	/**
	 * 查询出该登录名外的其他用户的登录名
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getLoginNamesExclude(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定查询不含某登录名用户列表的查询条件：用户登录名");
		return new HashSet<String>(userDao.find("select u.loginName from User u where u.companyId=? and u.loginName<>? and u.deleted=? ", getCompanyId(), loginName, false));
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Department> getDepartmentsByUser(Long companyId, String loginName){
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		return getDepartments(loginName);
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> getDepartments(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定用户所在部门列表的查询条件：用户登录名");
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.loginName =? and u.deleted=? and du.deleted=? and d.deleted=?");
		List<com.norteksoft.acs.entity.organization.Department> list = departmentDao.find(hql.toString(), getCompanyId(), loginName, false, false, false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentsByUserLike(Long companyId, String name){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.name like ? and u.deleted=? and du.deleted=? and d.deleted=?");
		List<com.norteksoft.acs.entity.organization.Department> list =  departmentDao.find(hql.toString(), companyId, "%"+name+"%", false, false, false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	
	/**
	 * 根据公司ID和用户的登录名查询该用户所具有的角色的字符串表示
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public Set<Role> getRolesByUser(Long companyId, String loginName){
		return getRolesByUser(loginName);
	}
	
	public Set<Role> getRolesByUser(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给定用户角色列表的查询条件：用户登录名");
		com.norteksoft.acs.entity.organization.User user = getUserByLoginNameOld(loginName);
		if(user == null) throw new RuntimeException("用户登录名为["+loginName+"]的用户不存在");
		return getRolesByUser(user.getId());
	}
	
	/**
	 * 根据公司ID和用户登录名查询该用户所在的工作组
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Workgroup> getWorkGroupsByUser(Long companyId, String loginName){
		return getWorkgroupsByUser(loginName);
	}
	
	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkgroupsByUser(String loginName){
		if(StringUtils.isEmpty(loginName)) throw new RuntimeException("没有给出查询用户所在工作组列表的查询条件：用户登录名");
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.loginName=? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		List<com.norteksoft.acs.entity.organization.Workgroup> list =  workGroupDao.find(hql.toString(), getCompanyId(), loginName, false, false, false);
		return BeanUtil.turnToModelWorkgroupList(list);
	}
	
	/**
	 * 根据公司ID和用户登录名查询该用户所在的工作组
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkGroupsByUserLike(Long companyId, String name){
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.name like ? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		List<com.norteksoft.acs.entity.organization.Workgroup> list = workGroupDao.find(hql.toString(), companyId, "%"+name+"%", false, false, false);
		return BeanUtil.turnToModelWorkgroupList(list);
	}
	
	/**
	 * 查询所有的系统并排序
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BusinessSystem> getSystems(){
		List<com.norteksoft.acs.entity.authorization.BusinessSystem> list = businessSystemDao.find("from BusinessSystem bs where bs.deleted=? order by bs.id", false);
		return BeanUtil.turnToModelBusinessSystemList(list);
	}
	
	public BusinessSystem getSystemByCode(String code){
		if(StringUtils.isEmpty(code)) throw new RuntimeException("没有查询业务系统的查询条件：系统编号");
		return BeanUtil.turnToModelBusinessSystem(businessSystemDao.findUniqueByProperty("code", code));
	}
	public BusinessSystem getSystemById(Long id){
		if(id == null) throw new RuntimeException("没有查询业务系统的查询条件：系统ID");
		return BeanUtil.turnToModelBusinessSystem(businessSystemDao.findUniqueByProperty("id", id));
	}
	
	public List<User> getUsersByLoginNames(Long companyId, List<String> loginNames){
		if(companyId == null) throw new RuntimeException("没有给定根据用户登录名集合查询用户列表的查询条件：公司ID");
		if(loginNames == null) throw new RuntimeException("没有给定根据用户登录名集合查询用户列表的查询条件：用户登录名集合");
		ThreadParameters parameters=new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		return getUsersByLoginNames(loginNames);
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getUsersByLoginNames(List<String> loginNames){
		if(loginNames == null) throw new RuntimeException("没有给定根据用户登录名集合查询用户列表的查询条件：用户登录名集合");
		StringBuilder hql = new StringBuilder("from User u where u.companyId=? and (");
		Object[] parameters = new Object[loginNames.size()+1];
		parameters[0] = getCompanyId();
		int index = 1;
		for(String loginName : loginNames){
			parameters[index++] = loginName;
			hql.append(" u.loginName=? or");
		}
		hql.replace(hql.length()-2, hql.length(), "");
		hql.append(") and u.deleted=false order by u.weight desc");
		List<com.norteksoft.acs.entity.organization.User> list =  userDao.find(hql.toString(), parameters);
		return BeanUtil.turnToModelUserList(list);
	}
	
	
	public List<Role> getRolesListByUserExceptDelegateMain(Long userId){
		return getRolesExcludeTrustedRole(userId);
	}
	
	public List<Role> getRolesExcludeTrustedRole(Long userId){
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user == null) return null;
		User modeUser = BeanUtil.turnToModelUser(user);
		return getRolesListByUserExceptDelegateMain(modeUser);
	}
	
	@Deprecated
	@Transactional(readOnly = true)
	public List<Role> getRolesListByUserExceptDelegateMain(com.norteksoft.acs.entity.organization.User user){
		if(user == null) return null;
		
		List<Role> roles = new ArrayList<Role>();
		// 用户具有的角色
		Set<RoleUser> roleUsers = user.getRoleUsers();
		for(RoleUser ru : roleUsers){
			if(ru.isDeleted()) continue;
			if(ru.getConsigner()!=null)continue;
			Role role = BeanUtil.turnToModelRole(ru.getRole());
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
						roles.add(BeanUtil.turnToModelRole(rd.getRole()));
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
						roles.add(BeanUtil.turnToModelRole(rw.getRole()));
					}
				}
			}
		}
		//角色按权重排序
		sortRole(roles);
		return roles;
	}
	/**
	 * 根据用户查询角色(不含委托)
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Role> getRolesListByUserExceptDelegateMain(User user){
		if(user == null) return null;
		
		List<Role> roles = new ArrayList<Role>();
		Set<com.norteksoft.acs.entity.authorization.Role> oldRoles = new HashSet<com.norteksoft.acs.entity.authorization.Role>();
		// 用户具有的角色
		String hql = "select r from Role r join r.roleUsers ru where r.deleted=? and ru.deleted=? and ru.user is not null and ru.user.id=? and ru.consigner is  null";
		List<com.norteksoft.acs.entity.authorization.Role> userRoles = roleDao.find(hql, false,false,user.getId());
		oldRoles.addAll(userRoles);
		// 用户具有的部门拥有的角色
		 hql = "select r from Role r join r.roleDepartments rd join rd.department d join d.departmentUsers du where r.deleted=? and rd.deleted=? and d.deleted=? and du.deleted=? and du.user is not null and du.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> deptRoles = roleDao.find(hql, false,false,false,false,user.getId());
		 oldRoles.addAll(deptRoles);
		// 用户具有的工作组拥有的角色
		 hql = "select r from Role r join r.roleWorkgroups rw join rw.workgroup wg join wg.workgroupUsers wu  where r.deleted=? and rw.deleted=? and wg.deleted=? and wu.deleted=?  and wu.user is not null and wu.user.id=?";
		 List<com.norteksoft.acs.entity.authorization.Role> workgroupRoles  = roleDao.find(hql, false,false,false,false,user.getId());
		 oldRoles.addAll(workgroupRoles);
		 Set<Role> modeRoles = BeanUtil.turnToModelRoleSet(oldRoles);
		if(modeRoles!=null){
			roles.addAll(modeRoles);
		}
		//角色按权重排序
		sortRole(roles);
		return roles;
	}
	//角色按权重排序
	private void sortRole(List<Role> roles){
		Collections.sort(roles, new Comparator<Role>() {
			public int compare(Role role1, Role role2) {
				if(role1.getWeight()==null&&role2.getWeight()!=null)return 1;
				if(role1.getWeight()!=null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()==null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()<role2.getWeight()){
					return 1;
				}
				return 0;
			}
		});
	}
	@Deprecated
	private void sortRoles(List<com.norteksoft.acs.entity.authorization.Role> roles){
		Collections.sort(roles, new Comparator<com.norteksoft.acs.entity.authorization.Role>() {
			public int compare(com.norteksoft.acs.entity.authorization.Role role1, com.norteksoft.acs.entity.authorization.Role role2) {
				if(role1.getWeight()==null&&role2.getWeight()!=null)return 1;
				if(role1.getWeight()!=null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()==null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()<role2.getWeight()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	public List<Department> getSuperiorDepartmentsByUser(Long companyId, String loginName){
		return getParentDepartmentsByUser(loginName);
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门的上级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getParentDepartmentsByUser(String loginName){
		if(loginName == null) throw new RuntimeException("没有查询用户所在的部门的上级部门的查询条件：用户登录名");
		StringBuilder hql = new StringBuilder(" select d from Department d join d.children sd join sd.departmentUsers du join du.user u ");
		hql.append(" where u.companyId=? and u.loginName=? and u.deleted=? and du.deleted=? and sd.deleted=? and d.deleted=?");
		List<com.norteksoft.acs.entity.organization.Department> list = departmentDao.find(hql.toString(), getCompanyId(), loginName, false, false, false,false);
		return BeanUtil.turnToModelDepartmentList(list);
	}
	
	/**
	 * 获得用户的顶级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Department> getUpstageDepartmentsByUser(Long companyId, String loginName){
		return getTopDepartmentsByUser(loginName);
	}
	
	public List<Department> getTopDepartmentsByUser(String loginName){
		if(loginName == null) throw new RuntimeException("没有查询用户所在的部门的顶级部门的查询条件：用户登录名");
		Set<Department> result = new HashSet<Department>();
		List<Department> departments = getDepartmentsByUser(getCompanyId(), loginName);
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
	public List<Department> getUpstageDepartmentsByUserLike(Long companyId, String userName){
		Set<Department> result = new HashSet<Department>();
		List<Department> departments = getDepartmentsByUserLike(companyId, userName);
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
	public Department getFirstDegreeDepartment(Department department){
		return getTopDepartment(department);
	}
	
	public Department getTopDepartment(Department department){
		if(department == null) throw new RuntimeException("没有查询部门的顶级部门的查询条件：部门实体");
		Department parentDept = getParentDepartment(department.getId());
		if(parentDept!=null){
			return getFirstDegreeDepartment(parentDept);
		}else{
			return department;
		}
	}
	
	/**
	 * 员工查询
	 * @param department
	 * @return
	 */
	@Deprecated
	public void userSearch(String userName ,String userDepart,  boolean userSex, Long companyId, Page<com.norteksoft.acs.entity.organization.User> page){
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct u from User u join u.userInfos ui join u.departmentUsers du join du.department d ");
		hql.append("where u.companyId=? and u.deleted=? and ui.deleted=? and du.deleted=? and d.deleted=? ");
		hql.append("and u.name like ? and u.sex=? and d.name like ?");
		userDao.find(page,hql.toString(), companyId,false,false,false,false,"%" + userName + "%", userSex, "%" + userDepart+ "%");
		
	}
	/**
	 * 员工查询所有性别
	 * @param department
	 * @return
	 */
	@Deprecated
	public void userSearchAllSex(String userName ,String userDepart,  Long companyId, Page<com.norteksoft.acs.entity.organization.User> page){
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct u from User u join u.userInfos ui join u.departmentUsers du join du.department d ");
		hql.append("where u.companyId=? and u.deleted=? and ui.deleted=? and du.deleted=? and d.deleted=? ");
		hql.append("and u.name like ? and d.name like ?");
		userDao.find(page,hql.toString(), companyId,false,false,false,false,"%" + userName + "%", "%" + userDepart+ "%");
		
	}
	/**
	 * 获取本公司所有用户的生日
	 * @return
	 */
	 @SuppressWarnings("unchecked")
	public Map<Long,String> getUserBirthdayByCompany(Long companyId){
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
	public List<UserInfo> getNoDepartmentUsers(Long companyId){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT ACS_USERINFO.* FROM ACS_USERINFO LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USERINFO.ID DESC ");
		return userInfoDao.findByJdbc( sqlString.toString(), companyId);
	}

	public void deleteUser(Long userId) {
		if(userId==null)return;
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		if(user!=null){
			user.getUserInfo().setDeleted(true);
			user.getUserInfo().setDr(1);
			userInfoDao.save(user.getUserInfo());
		}
	}

	public void saveUser(com.norteksoft.acs.entity.organization.User user,UserInfo userInfo) {
		if(user.getCompanyId()==null){
			user.setCompanyId(ContextUtils.getCompanyId());
		}
		if(userInfo.getCompanyId()==null){
			userInfo.setCompanyId(user.getCompanyId());
		}
	    userDao.save(user);
		userInfo.setUser(user);
		userInfoDao.save(userInfo);
	}
	

	public void deleteDepartment(Long departmentId) {
		if(departmentId==null)return;
		com.norteksoft.acs.entity.organization.Department department=departmentDao.get(departmentId);
		if(department==null)return;
		
		List<com.norteksoft.acs.entity.organization.User> users=userManager.getUsersByDeptId(departmentId);
		departmentManager.deleteDepart(department,users);
	}

	@Deprecated
	public void saveDepartment(com.norteksoft.acs.entity.organization.Department department,Long companyId) {
		if(companyId==null)throw new RuntimeException("公司id不能为null");
		Company company=companyManager.getCompany(companyId);
		if(company==null)throw new RuntimeException("公司不存在");
		department.setCompany(company);
		departmentDao.save(department);
		
	}
	
	public void saveDepartment(Department department,Long companyId) {
		if(companyId==null)throw new RuntimeException("公司id不能为null");
		Company company=companyManager.getCompany(companyId);
		if(company==null)throw new RuntimeException("公司不存在");
		department.setCompany(company);
		departmentDao.save(BeanUtil.turnToDepartment(department));
		
	}

	@Deprecated
	public void saveDepartmentUser(List<Long> userIds, com.norteksoft.acs.entity.organization.Department department) {
		if(userIds==null||department==null)return;
		departmentManager.departmentToUser(department.getId(), userIds, 0);
	}
	
	public void saveDepartmentUser(List<Long> userIds, Department department) {
		if(userIds==null||department==null)return;
		departmentManager.departmentToUser(department.getId(), userIds, 0);
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsersByName(String userName) {
		List<com.norteksoft.acs.entity.organization.User> list = userDao.find("from User u where u.companyId=? and u.name=? and u.deleted=? ", getCompanyId(), userName, false);
		return BeanUtil.turnToModelUserList(list);
	}

	public String getCurrentUserRolesExcludeTrustedRole() {
		Long userId=ContextUtils.getUserId();
		if(userId==null) return "";
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		return getRolesExcludeTrustedRole(user);
	}

	public String getUserRolesExcludeTrustedRole(Long userId) {
		if(userId==null) return "";
		com.norteksoft.acs.entity.organization.User user = userDao.get(userId);
		return getRolesExcludeTrustedRole(user);
	}
	
	
	public List<User> getTacheUsersByLoginNames(Long companyId, List<String> loginNames){
		if(companyId == null) throw new RuntimeException("没有给定根据用户登录名集合查询用户列表的查询条件：公司ID");
		if(loginNames == null) throw new RuntimeException("没有给定根据用户登录名集合查询用户列表的查询条件：用户登录名集合");
		ThreadParameters parameters=new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		return getTacheUsersByLoginNames(loginNames);
	}

	private List<User> getTacheUsersByLoginNames(List<String> loginNames) {
		if(loginNames == null) throw new RuntimeException("没有给定根据用户登录名集合查询用户列表的查询条件：用户登录名集合");
		List<com.norteksoft.acs.entity.organization.User> users = new ArrayList<com.norteksoft.acs.entity.organization.User>();
		for (String loginName : loginNames) {
			com.norteksoft.acs.entity.organization.User user = (com.norteksoft.acs.entity.organization.User)userDao.findUnique("from User u where u.companyId=? and u.loginName=? and u.deleted = false ",getCompanyId(), loginName);
			if(user!=null)users.add(user);
		}
		return BeanUtil.turnToModelUserList(users);
	}

	@SuppressWarnings("unchecked")
	public String getSystemAdminLoginName() {
		StringBuilder hql = new StringBuilder();
		hql.append("from User u ");
		hql.append("where u.companyId=? and u.deleted=? and u.loginName like ?");
		List<com.norteksoft.acs.entity.organization.User> users = userDao.find(hql.toString(), ContextUtils.getCompanyId(),false,"%.systemAdmin%");
		if(users.size()>0)return users.get(0).getLoginName();
		return null;
	}

	@Deprecated
	public void saveDepartment(com.norteksoft.acs.entity.organization.Department department) {
		saveDepartment(department,ContextUtils.getCompanyId());
	}
	
	public void saveDepartment(Department department) {
		saveDepartment(department,ContextUtils.getCompanyId());
	}
	
	/**
	 * 查询公司中所有人员（不包含无部门人员）
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getLoginNamesByCompany(Long companyId){
		if(companyId == null) throw new RuntimeException("没有给定查询用户列表的查询条件：公司ID");
		return userDao.find("select distinct u.loginName FROM User u join u.departmentUsers du join du.department d  WHERE u.companyId=? AND u.deleted=? AND du.deleted=?  AND d.deleted=? ORDER BY u.weight DESC", companyId,false,false,false);
	}
	/**
	 * 查询工作组所有人员
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getLoginNamesByWorkgroup(Long companyId){
		if(companyId == null) throw new RuntimeException("查询工作组人员时，没有给定查询用户列表的查询条件：公司ID");
		return userDao.find("select distinct u.loginName FROM User u join u.workgroupUsers du join du.workgroup d  WHERE u.companyId=? AND u.deleted=? AND du.deleted=?  AND d.deleted=? ORDER BY u.weight DESC", companyId,false,false,false);
	}

	public void saveUser(User user) {
		com.norteksoft.acs.entity.organization.User oldUser = BeanUtil.turnToUser(user);
		userDao.save(oldUser);
	}
	@SuppressWarnings("unchecked")
	public Department getParentDepartment(Long departmentId){
		String hql = "select d.parent from Department d where d.parent is not null and d.id=? and d.deleted=? and d.parent.deleted=?";
		List<com.norteksoft.acs.entity.organization.Department> parents =  departmentDao.find(hql, departmentId,false,false);
		if(parents.size()>0)return BeanUtil.turnToModelDepartment(parents.get(0));
		return null;
	}

}
