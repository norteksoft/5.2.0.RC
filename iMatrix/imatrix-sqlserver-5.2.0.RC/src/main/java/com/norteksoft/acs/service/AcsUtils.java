package com.norteksoft.acs.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.product.util.Md5;

/**
 * 权限API
 * 
 * @author xiao
 *
 * 2010-9-26
 */
@Service
@Transactional
public class AcsUtils {

	private SimpleHibernateTemplate<User, Long> userDao;
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private StandardRoleManager standardRoleManager;
	private BusinessSystemManager businessSystemManager;
	
	private static SimpleHibernateTemplate<BusinessSystem, Long> businessSystemDao;
	
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory,User.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		workGroupDao = new SimpleHibernateTemplate<Workgroup, Long>(sessionFactory, Workgroup.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory,Role.class);
		companyDao = new SimpleHibernateTemplate<Company, Long>(sessionFactory,Company.class);
		businessSystemDao = new SimpleHibernateTemplate<BusinessSystem, Long>(sessionFactory,BusinessSystem.class);
	}
	
	@Autowired
	public void setStandardRoleManager(StandardRoleManager standardRoleManager) {
		this.standardRoleManager = standardRoleManager;
	}
	
	@Autowired
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	/**
	 * 根据公司ID查询所有顶级部门
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Department> getDepartments(Long companyId) {
		return departmentDao.findList(
				"FROM Department d WHERE d.company.id=? AND d.deleted=? and d.parent.id is null ORDER BY d.weight desc", 
				companyId, false);
	}
	
	@SuppressWarnings("unchecked")
	public Department getManDepartment(String loginName, Long companyId){
		List<Department> depts = departmentDao.find("select d from Department d,User u where d.id=u.mainDepartmentId and u.companyId=? and u.loginName=? and u.deleted=false", companyId, loginName);
		if(depts.size() == 1){
			return depts.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Department getManDepartment(Long userId, Long companyId){
		List<Department> depts = departmentDao.find("select d from Department d,User u where d.id=u.mainDepartmentId and u.companyId=? and u.id=?", companyId, userId);
		if(depts.size() == 1){
			return depts.get(0);
		}
		return null;
	}
	
	/**
	 * 根据公司ID查询所有工作组
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Workgroup> getWorkGroups(Long companyId) {
		return workGroupDao.findList(
				"from Workgroup wg where wg.company.id=? and wg.deleted=? ORDER BY wg.weight desc"
				,companyId, false);
	}
	
	/**
	 * 根据部门ID查询该部门所有的用户
	 * @param departmentId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<User> getUsersByDepartmentId(Long companyId, Long departmentId) {
		return userDao.findList(
				"select u from User u join u.departmentUsers du join du.department d " +
				"where d.company.id=? and d.id=? and d.deleted=? and du.deleted=? and u.deleted=? ORDER BY u.weight desc", 
				companyId, departmentId, false, false, false);
	}
	
	/**
	 * 根据工作组ID查询该组下所有的用户
	 * @param companyId
	 * @param workGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<User> getUsersByWorkGroupId(Long companyId, Long workGroupId) {
		return userDao.findList(
				"select u from User u join u.workgroupUsers wu join wu.workgroup wg " +
				"where wg.company.id=? and wg.id=? and wg.deleted=? and wu.deleted=? and u.deleted=? ORDER BY u.weight desc", 
				companyId, workGroupId, false, false, false);
	}
	
	/**
	 * 根据父部门id查询该父部门下所有子部门
	 * @param paternDepartmentId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Department> getSubDepartmentList(Long paternDepartmentId) {
		return departmentDao.findList(
				"FROM Department d WHERE d.parent.id=? AND d.deleted=?  ORDER BY d.weight desc", 
				paternDepartmentId, false);
	}
	
	/**
	 * 根据用户Id得到用户
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		if (id == null) return null;
		return userDao.get(id);
	}
	
	/**
	 * 根据用户Id得到用户
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public User getUserByLoginName(String loginName) {
		if (loginName == null) return null;
		return (User) userDao.findUnique("select user from User user where user.deleted=false and user.loginName=?", loginName);
	}
	
	/**
	 * 根据用户Id得到用户
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public User getUserByLikeLoginName(String loginName,Long companyId) {
		if (loginName == null) return null;
		List<User> users=userDao.find("select user from User user where user.deleted=false and user.loginName like ? and user.companyId=? ", "%"+loginName+"%",companyId);
		if(users.size()>0)return users.get(0);
		return null;
	}
	
	/**
	 * 根据用户Id得到公司Id
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long getCompanyIdByUserId(Long userId) {
		if (userId == null) return null;
		User user=getUserById(userId);
		if(user==null)return null;
		return user.getCompanyId();
	}
	
	/**
	 * 根据用户Id得到公司Id
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long getCompanyIdLoginName(String loginName) {
		if (loginName == null) return null;
		User user=getUserByLoginName(loginName);
		if(user==null)return null;
		return user.getCompanyId();
	}
	
	/**
	 * 根据登录名查询用户
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Transactional(readOnly = true)
	public User getUser(Long companyId, String loginName){
		List<User> users = userDao.findList("from User u where u.companyId=? and u.loginName=? and u.deleted=? ", companyId, loginName, false);
		User user = null;
		if(users.size() == 1){
			user = users.get(0);
		}
		return user;
	}
	
	/**
	 * 获取不属于任何部门的用户
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<User> getUsersNotInDepartment(Long companyId){
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
	 * 通过部门ID获取部门实体
	 * @param workGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Department getDepartmentById(Long departmentId){
		if(departmentId == null) return null;
		return departmentDao.get(departmentId);
	}
	
	/**
	 * 通过部门名称获取部门实体
	 * @param name
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Department getDepartmentByName(String name, Long companyId){
		List<Department> depts = departmentDao.findList("from Department d where d.company.id=? and d.name=? and d.deleted=?", companyId, name, false);
		Department dept = null;
		if(depts.size() == 1){
			dept = depts.get(0);
		}
		return dept;
	}
	
	/**
	 * 根据用户ID查询用户所在的部门
	 * @param companyId
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Department> getDepartmentsByUser(Long companyId, Long userId){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and d.deleted=?  ORDER BY d.weight desc");
		return departmentDao.findList(hql.toString(), companyId, userId, false, false, false);
	}
	
	/**
	 * 根据用户ID查询用户所在的工作组
	 * @param companyId
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Workgroup> getWorkGroupByUser(Long companyId, Long userId){
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and wgu.deleted=? and wg.deleted=? order by wg.weight desc");
		return workGroupDao.findList(hql.toString(), companyId, userId, false, false, false);
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Department> getDepartmentsByUser(Long companyId, String loginName){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.loginName=? and u.deleted=? and du.deleted=? and d.deleted=?");
		return departmentDao.findList(hql.toString(), companyId, loginName, false, false, false);
	}

	/**
	 * 通过角色编号查询所有的用户
	 * @param systemId
	 * @param companyId
	 * @param roleCode
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<User> getUsersByRole(Long systemId, Long companyId, String roleCode){
		Set<User> result = new LinkedHashSet<User>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u from User u join u.roleUsers ru join ru.role r ");
		usersByRole.append("where r.code = ? and u.companyId=? and r.deleted=false and ru.consigner is null and ");
		usersByRole.append("ru.deleted=false and u.deleted=false order by u.weight desc");
		List<User> roleUsers = userDao.findList(usersByRole.toString(), roleCode, companyId);
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r ");
		usersByDeptRoleHql.append("where r.code = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false order by u.weight desc");
		List<User> roleDeptUsers = userDao.findList(usersByDeptRoleHql.toString(), roleCode, companyId);
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r join r.businessSystem rbs ");
		usersByWgRoleHql.append("where rbs.id=? and r.code = ? and wg.company.id=? and rbs.deleted=false and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false order by u.weight desc");
		List<User> roleWgUsers = userDao.findList(usersByWgRoleHql.toString(), systemId, roleCode, companyId);
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleWgUsers);
		return result;
	}
	
	public List<Department> getDepartmentsByRole(Long companyId, String roleCode){
		StringBuilder deptRoleHql = new StringBuilder();
		deptRoleHql.append("select d from Department d ");
		deptRoleHql.append("join d.roleDepartments rd join rd.role r ");
		deptRoleHql.append("where r.code = ? and d.company.id=? and r.deleted=false and ");
		deptRoleHql.append("rd.deleted=false and d.deleted=false order by d.weight desc");
		return departmentDao.findList(deptRoleHql.toString(), roleCode, companyId);
	}
	
	/**
	 * 根据用户ID查询用户所有的角色
	 * @param systemId
	 * @param companyId
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesByUser(Long systemId, Long companyId, Long userId){
		StringBuilder rolesByUserHql = new StringBuilder();
		rolesByUserHql.append("select r from User u join u.roleUsers ru join ru.role r ");
		rolesByUserHql.append("where u.deleted=? and ru.deleted=? and ru.consigner is null and r.deleted=? and r.businessSystem.id=? and u.id=? and u.companyId=? and (r.companyId is null or r.companyId=?)");
		List<Role> userRoles = roleDao.find(rolesByUserHql.toString(), false, false, false, systemId, userId, companyId, companyId);
		
		StringBuilder rolesByDepartmentHql = new StringBuilder();
		rolesByDepartmentHql.append("select r from User u join u.departmentUsers du join du.department d join d.roleDepartments rd join rd.role r ");
		rolesByDepartmentHql.append("where u.deleted=? and du.deleted=? and d.deleted=? and rd.deleted=? and r.deleted=? and r.businessSystem.id=? and u.id=? and u.companyId=? and (r.companyId is null or r.companyId=?)");
		List<Role> departmentRoles = roleDao.find(rolesByDepartmentHql.toString(), false, false, false,false, false, systemId, userId, companyId, companyId);
		
		StringBuilder rolesByWorkgroupHql = new StringBuilder();
		rolesByWorkgroupHql.append("select r from User u join u.workgroupUsers wu join wu.workgroup w join w.roleWorkgroups rw join rw.role r ");
		rolesByWorkgroupHql.append("where u.deleted=? and wu.deleted=? and w.deleted=? and rw.deleted=? and r.deleted=? and r.businessSystem.id=?  and u.id=? and u.companyId=? and (r.companyId is null or r.companyId=?)");
		List<Role> workgroupRoles = roleDao.find(rolesByWorkgroupHql.toString(), false, false, false,false, false, systemId, userId, companyId, companyId);
		
		Set<Role> roles = new HashSet<Role>();
		roles.addAll(userRoles);
		roles.addAll(departmentRoles);
		roles.addAll(workgroupRoles);
		return roles;
	}
	
	/**
	 * 通过url的key查询用户是否具有该权限
	 * @param urlKey
	 * @param userId
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean isAuthority(String urlKey, Long userId, Long companyId){
		Set<Role> userRoles = standardRoleManager.getAllRolesByUser(userId, companyId);
		Set<Function> functions =  standardRoleManager.getFunctionsByRoles(userRoles);
		boolean result = false;
		for(Function function : functions){
			if(urlKey.equals(function.getCode())){
				result = true;
				break;
			}
		}
		return result;
	}
	
	/**
	 * 通过用户ID查询用户的角色
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<Role> getRolesByUser(Long userId, Long companyId){
		return standardRoleManager.getRolesByUser(userId, companyId);
	}
	
	/**
	 * 判断用户是否具有给定角色编码的角色
	 * @param userId
	 * @param companyId
	 * @param roleCode
	 */
	@Transactional(readOnly = true)
	public boolean hasRole(Long userId, Long companyId, String roleCode){
		Set<Role> roles = standardRoleManager.getRolesByUser(userId, companyId);
		for(Role r : roles){
			if(r.getCode().equals(roleCode)){
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> getUsersByCompany(Long companyId){
		return userDao.find("FROM User u WHERE u.companyId=? AND u.deleted=? ORDER BY u.weight DESC", companyId,false);
	}
	
	
	/**
	 * 查询所有的系统并排序
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BusinessSystem> getSystems(){
		return businessSystemDao.find("from BusinessSystem bs where bs.deleted=? order by id", false);
	}
	/**
	 * 查询所有的系统并排序
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public BusinessSystem getSystemsByCode(String systemCode){
		return businessSystemManager.getSystemBySystemCode(systemCode);
	}
	@Transactional(readOnly = true)
	public Workgroup getWorkGroup(Long workGroupId){
		if(workGroupId == null) 
			return null;
		return workGroupDao.get(workGroupId);
	}
	
	/**
	 * 根据工作组编号查询工作组
	 * @return
	 */
	@Transactional(readOnly = true)
	public Workgroup getWorkGroupByCode(String code, Long companyId){
		if(code == null)  return null;
		List<Workgroup> groups =  workGroupDao.findList("from Workgroup w where w.company.id=? and w.code=? and w.deleted=? ", 
				companyId, code, false);
		if(groups.size() == 1) return groups.get(0);
		return null;
	}
	
	/**
	 * 查询所有业务系统信息
	 */
	@SuppressWarnings("unchecked")
	public List<BusinessSystem> getAllBusiness(Long companyId){
		String hql = "select si.product.systemId from SubscriberItem si join si.subsciber s where s.tenantId=? and si.invalidDate>?";
		List<Long> idList = businessSystemDao.find(hql, companyId, new Date());
		if(idList.isEmpty()){
			return new ArrayList<BusinessSystem>();
		}
		return businessSystemDao.findByCriteria(Restrictions.in("id",idList),Restrictions.eq("deleted",false));
	}
	/**
	 * 验证当前用户是否存在且密码是否正确
	 * @param loginName
	 * @param password
	 * @return
	 */
	public boolean validateUserAccess(String loginName,String password){
		User user=getUserByLoginName(loginName);
		if(user==null)return false;
		String userPassword=user.getPassword();
		if(userPassword.length()<32){
			userPassword=Md5.toMessageDigest(userPassword);
		}
		if(userPassword==null&&password==null)return true;
		if(userPassword!=null&&userPassword.equals(password))return true;
		return false;
	}
	/**
	 * 返回加密后的密码(Md5)
	 * @param loginName
	 * @param password
	 * @return
	 */
	public String validateUserAccess(String password){
		return Md5.toMessageDigest(password);
	}
	
	public User getUserByCardNo(String cardNo){
		if (cardNo == null) return null;
		List<User> users=userDao.find("select user from User user where user.deleted=false and user.cardNo=?", cardNo);
		if(users==null||users.size()<=0)return null;
		return (User) users.get(0);
	}
	
	/**
	 * 通过部门名称获取部门实体
	 * @param name
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Department getDepartmentByCode(String code, Long companyId){
		List<Department> depts = departmentDao.findList("from Department d where d.company.id=? and d.code=? and d.deleted=?", companyId, code, false);
		Department dept = null;
		if(depts.size() == 1){
			dept = depts.get(0);
		}
		return dept;
	}
	/**
	 * 获得所有公司
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Company> getAllCompanys(){
		return companyDao.findList("from Company c where c.deleted=?", false);
	}
	
	/**
	 * 通过角色编号查询所有的用户
	 * @param systemId
	 * @param companyId
	 * @param roleName
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<User> getUsersByRoleName(Long systemId, Long companyId, String roleName){
		Set<User> result = new LinkedHashSet<User>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u from User u join u.roleUsers ru join ru.role r ");
		usersByRole.append("where r.name = ? and u.companyId=? and r.deleted=false and ru.consigner is null and ");
		usersByRole.append("ru.deleted=false and u.deleted=false order by u.weight desc");
		List<User> roleUsers = userDao.findList(usersByRole.toString(), roleName, companyId);
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r ");
		usersByDeptRoleHql.append("where r.name = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false order by u.weight desc");
		List<User> roleDeptUsers = userDao.findList(usersByDeptRoleHql.toString(), roleName, companyId);
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r join r.businessSystem rbs ");
		usersByWgRoleHql.append("where rbs.id=? and r.name = ? and wg.company.id=? and rbs.deleted=false and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false order by u.weight desc");
		List<User> roleWgUsers = userDao.findList(usersByWgRoleHql.toString(), systemId, roleName, companyId);
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleWgUsers);
		return result;
	}

	/**
	 * 通过公司code获取公司Id
	 * @param companyCode
	 * @return Long
	 */
	@Transactional(readOnly = true)
	public  Long getCompanyIdByCompanycode(String companyCode) {
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
	@SuppressWarnings("unchecked")
	public  Workgroup getWorkGroupByName(String name, Long companyId){
		List<Workgroup> workGroups = workGroupDao.find("from Workgroup wg where wg.company.id=? and wg.name=? ", companyId, name);
		if(workGroups.size() == 1){
			return workGroups.get(0);
		}
		return null;
	}
	/**
	 * 根据邮件地址查询用户信息
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public User getUser(String email){
		List<User> list=userDao.find("from User u where u.email=? and u.deleted=? ",email, false);
		if(list!=null&&!list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<User> getUserByName(Long companyId,String trueName){
		return userDao.find("from User u where u.companyId=? and u.name=? and u.deleted=? ",companyId,trueName, false);
	}
	
	/**
	 * 获得平台系统
	 * @return
	 */
	public List<BusinessSystem> getParentSystem(){
		return businessSystemManager.getParentSystem();
	}
}
