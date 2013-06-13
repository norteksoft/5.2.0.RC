package com.norteksoft.acs.service.organization;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@SuppressWarnings("deprecation")
@Service
@Transactional
public class DepartmentManager {

	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<User, Long> userDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> departmentToUserDao;
	private SimpleHibernateTemplate<RoleDepartment, Long> roleDepartmentDao;
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private LogUtilDao logUtilDao;
	private static String DELETED = "deleted";
	private static String COMPANYID = "companyId";
	private static String DEPARTMENTID = "department.id";
	private static String hql = "from Department d where d.company.id=? and d.deleted=?";
	private static final String SBU_DEPT_HQL = "from Department d where d.company.id=? and d.parent.id=? and d.deleted=? order by d.weight desc";
	
	@Autowired
	private AcsUtils acsUtils;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {

		companyDao = new SimpleHibernateTemplate<Company, Long>(
				sessionFactory, Company.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(
				sessionFactory, Department.class);
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory,
				User.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(
				sessionFactory, UserInfo.class);
		departmentToUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(
				sessionFactory, DepartmentUser.class);
		roleDepartmentDao = new SimpleHibernateTemplate<RoleDepartment, Long>(
				sessionFactory, RoleDepartment.class);
		logUtilDao = new LogUtilDao(sessionFactory);
	}
  
	public LogUtilDao getLogUtilDao() {
		return logUtilDao;
	}

	public void setLogUtilDao(LogUtilDao logUtilDao) {
		this.logUtilDao = logUtilDao;
	}

	private Long companyId;

	public Long getCompanyId() {
		if (companyId == null) {
			return ContextUtils.getCompanyId();
		} else{
			return companyId;
		}
	}
	public Long getSystemIdByCode(String code) {
		return acsUtils.getSystemsByCode(code).getId();
    }
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	/**
	 *根据userId得到DepartmentToUser
	 */
	@SuppressWarnings("unchecked")
	public List<DepartmentUser> getDepartmentToUserByuserId(Long userId,Long departmentId){
		String hql="from DepartmentUser d where d.user.id=? and d.department.id=?";
		return departmentToUserDao.find(hql, userId,departmentId);
	}
	
	
	
	/**
	 * 验证部门名称唯一性
	 */
	public boolean checkDeptName(String name,Long id){
		String hql = "FROM Department d WHERE d.name=? AND d.id<>? AND d.company.id=? AND d.deleted=0";
		Object obj = departmentDao.findUnique(hql, name,id,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 验证部门名称唯一性
	 */
	public boolean checkDeptName(String name){
		String hql = "FROM Department d WHERE d.name=? AND d.company.id=? AND d.deleted=0";
		Object obj = departmentDao.findUnique(hql, name,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 验证部门编码唯一性
	 * liudongxia
	 */
	public boolean checkDeptCode(String code,Long id){
		String hql = "FROM Department d WHERE d.code=? AND d.id<>? AND d.company.id=? AND d.deleted=0";
		Object obj = departmentDao.findUnique(hql, code,id,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 验证部门编码唯一性
	 * liudongxia
	 */
	public boolean checkDeptCode(String code){
		String hql = "FROM Department d WHERE d.code=? AND d.company.id=? AND d.deleted=0";
		Object obj = departmentDao.findUnique(hql, code,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}

	
	/**
	 * 检测公司根目录下是否存在此部门
	 * @param name
	 * @return
	 */
	public Department checkDeptNoParent(String name){
		String hql = "FROM Department d WHERE d.name=? AND d.company.id=? AND d.deleted=0 AND d.parent is null";
		return (Department)departmentDao.findUnique(hql, name,ContextUtils.getCompanyId());
	}
	/**
	 * 检测部门下是否存在此子部门
	 * @param name
	 * @return
	 */
	public Department checkDeptHasParent(String name,Long parentId){
		String hql = "FROM Department d WHERE d.name=? AND d.company.id=? AND d.deleted=0 AND d.parent.id=?";
		return (Department)departmentDao.findUnique(hql, name,ContextUtils.getCompanyId(),parentId);
	}
	/**
	 * 查询所有部门信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Department> getAllDepartment() {
		return departmentDao.find("FROM Department d WHERE d.company.id=? AND d.deleted=? ORDER BY d.weight desc", getCompanyId(), false);
	}

	/**
	 * 获取单条部门信息
	 */
	@Transactional(readOnly = true)
	public Department getDepartment(Long id) {
		return (Department)departmentDao.findUnique("from Department d where d.company.id=? and d.id=? ",ContextUtils.getCompanyId(), id);
	}
	/**
	 * 获取单条部门信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Department getDepartmentById(Long id) {
		List<Department> depts=departmentDao.find("FROM Department d WHERE d.id=? AND d.deleted=?", id, false);
		if(depts.size()>0)return depts.get(0);
		return null;
	}

	/**
	 * 分页查询所有部门信息
	 */
	@Transactional(readOnly = true)
	public Page<Department> getAllDepartment(Page<Department> page) {
		page.setOrderBy("weight");
		page.setOrder("desc");
		return departmentDao.findByCriteria(page, Restrictions.eq("company.id",
				getCompanyId()), Restrictions.eq(DELETED, false));

	}

	/**
	 * 保存部门信息
	 */
	public void saveDept(Department department) {
		departmentDao.save(department);
	}

	/**
	 * 保存子部门信息
	 */
	public void saveSubDepartment(Long departmentId, Department subDepartment) {
		Department Parentdepartment = departmentDao.get(departmentId);
		subDepartment.setParent(Parentdepartment);
		subDepartment.setCompany(Parentdepartment.getCompany());
		if(subDepartment.getId()==null){
		}
		else{
		}
		    departmentDao.save(subDepartment);
	}
	
	/**
	 * 删除部门信息
	 */
	public void deleteDepartmet(Long id) {
		Department department = departmentDao.get(id);
		deleteDepartmet(department);
	}
	public void deleteDepartmet(Department department) {
		department.setDeleted(true);
		departmentDao.save(department);
	}

	public void deleteDepart(Department department,List<User> users) {
		department.setDeleted(true);
		departmentDao.save(department);
		List<DepartmentUser> dtus=null;
		for(User user:users){
			if(department.getId().equals(user.getMainDepartmentId())) user.setMainDepartmentId(null);
			dtus=getDepartmentToUserByuserId(user.getId(),department.getId());
			if(dtus.size()!=0){
			DepartmentUser dtu= dtus.get(0);
			dtu.setDeleted(true);
			departmentToUserDao.save(dtu);
			}
		}
	}
	public Page<UserInfo> departmentToUsers(Page<UserInfo> userPage,
			boolean deleted, Long companyId, Integer dr) {
		return userInfoDao.findByCriteria(userPage, Restrictions.eq(DELETED,
				false), Restrictions.eq(COMPANYID, getCompanyId()),
				Restrictions.eq("dr", 0));

	}

	/**
	 *查询部门已经添加的用户
	 */
	public List<Long> getUserIds(Long departmentId) throws Exception {
		List<Long> userIds = new ArrayList<Long>();
		List<DepartmentUser> departmnetToUsers = departmentToUserDao
				.findByCriteria(Restrictions.eq(DEPARTMENTID, departmentId),
						Restrictions.eq(COMPANYID, getCompanyId()),
						Restrictions.eq(DELETED, false));
		for (DepartmentUser departmentToUser : departmnetToUsers) {
			userIds.add(departmentToUser.getUser().getUserInfo().getId());
		}
		return userIds;
	}

	/**
	 * 查询部门要移除的用户
	 */
	public Page<UserInfo> departmentToRomoveUserList(Page<UserInfo> page,
			User user, Long departmentId) {

		String hql = "select userInfo from UserInfo userInfo join userInfo.user.departmentUsers du where du.department.id=? and du.companyId=? and userInfo.deleted=? and du.deleted=?";
		if (user != null) {

			String userName = user.getLoginName();
			if (userName != null && !"".equals(userName)) {
				StringBuilder hqL = new StringBuilder(hql);
				hqL.append(" and userInfo.user.userName like ? ");
				return userInfoDao.find(page, hqL.toString(), departmentId,
						getCompanyId(), false, false, "%" + userName + "%");
			}
		}
		return userInfoDao.find(page, hql, departmentId, getCompanyId(), false,
				false);
	}
             
	/**
	 * 部门添加人员
	 */
	public void departmentToUser(Long departmentId, List<Long> userIds,
			Integer isAdd ) {

		if(userIds==null){
			return;
		}
		Department department = departmentDao.get(departmentId);
		/**
		 * 添加人员
		 */
		if (isAdd == 0) {
			for (Long userId : userIds) {
				if(userId.equals(0L)){//全公司时
					List<com.norteksoft.product.api.entity.User> users = ApiFactory.getAcsService().getAllUsersByCompany(ContextUtils.getCompanyId());
					for(com.norteksoft.product.api.entity.User u:users){
						departmentToUserSingle(u.getId(),department);
					}
				}else{
					departmentToUserSingle(userId,department);
				}
			}
		}
		/**
		 *移除人员
		 */
		if (isAdd == 1) {
			List<User> uif = userDao.findByCriteria(Restrictions.in(
					"id", userIds));
			List<Long> ids = new ArrayList<Long>();
			for (User user : uif) {
				ids.add(user.getId());
				if(departmentId.equals(user.getMainDepartmentId())){
					user.setMainDepartmentId(null);
				}
			}
			List<DepartmentUser> list = departmentToUserDao.findByCriteria(
					Restrictions.in("user.id", ids), Restrictions.eq(
							DEPARTMENTID, departmentId), Restrictions.eq(
									COMPANYID, getCompanyId()), Restrictions.eq(
									DELETED, false));

			for (DepartmentUser departmentToUser : list) {
				departmentToUser.setDeleted(true);
				departmentToUserDao.save(departmentToUser);
			}
		}

	}
	
	private void departmentToUserSingle(Long userId,Department department){
		DepartmentUser departmentToUser;
		User user = null;
		List<DepartmentUser> dtu=getDepartmentToUserByuserId(userId,department.getId());
		user = userDao.get(userId);
		if(dtu.size()==0){
			departmentToUser = new DepartmentUser();
			departmentToUser.setUser(user);
			departmentToUser.setDepartment(department);
			departmentToUser.setCompanyId(user.getCompanyId());
			departmentToUserDao.save(departmentToUser);
		}else{
			DepartmentUser d=dtu.get(0);
			d.setDeleted(false);
			departmentToUserDao.save(d);
		}
		if(user.getMainDepartmentId()==null){
			user.setMainDepartmentId(department.getId());
		}
	}

	/**
	 * 按条件检索部门
	 */
	@Transactional(readOnly = true)
	public Page<Department> getSearchDepartment(Page<Department> page,
			Department department, boolean deleted) {
		    StringBuilder departmentHql = new StringBuilder(hql);

		if (department != null) {

			String departmentCode = department.getCode();
			String name = department.getName();

			if (!"".equals(departmentCode) && !"".equals(name)) {
				departmentHql.append(" and d.code like ?");
				departmentHql.append(" and d.name like ?");
				return departmentDao.find(page, departmentHql.toString(),
						getCompanyId(), false, "%" + departmentCode + "%", "%"
								+ name + "%");
			}

			if (!"".equals(departmentCode)) {
				departmentHql.append(" and d.departmentCode like ?");
				return departmentDao.find(page, departmentHql.toString(),
						getCompanyId(), false, "%" + departmentCode + "%");
			}

			if (!"".equals(name)) {
				departmentHql.append(" and d.name like ?");
				return departmentDao.find(page, departmentHql.toString(),
						getCompanyId(), false, "%" + name + "%");
			}
		}
		return departmentDao.find(page, hql, getCompanyId(), false);
	}

	/**
	 * 查询己分配给公司的部门
	 */
	public List<Department> saveDepart(List<Long> departmentId) {

		return departmentDao
				.findByCriteria(Restrictions.in("id", departmentId));
	}

	/**
	 * 给部门分配角色
	 * 
	 * @param departmentId
	 * @param roleIds
	 */
	public void addRolesToDepartments(Long departmentId, List<Long> roleIds,
			Integer isAdd) {
		Department department = departmentDao.get(departmentId);
		RoleDepartment roleDepartment = null;
		if (isAdd == 0) {
			Role role = null;
			for (Long id : roleIds) {
				role = new Role();
				role.setId(id);
				roleDepartment = new RoleDepartment();
				roleDepartment.setRole(role);
				roleDepartment.setDepartment(department);
				roleDepartment.setCompanyId(getCompanyId());
				roleDepartmentDao.save(roleDepartment);
			}
		} else if (isAdd == 1) {
			List<RoleDepartment> roleDepartments = roleDepartmentDao
					.findByCriteria(Restrictions.eq(DEPARTMENTID,
							departmentId), Restrictions.in("role.id", roleIds),
							Restrictions.eq(COMPANYID, getCompanyId()),
							Restrictions.eq(DELETED, false));
			for (RoleDepartment rd : roleDepartments) {
				rd.setDeleted(true);
				roleDepartmentDao.save(rd);
			}
		}
	}

	/**
	 * 查询部门已经分配的角色
	 */
	public List<Long> getCheckedRoleIdsByDepartment(Long departmentId) {
		List<RoleDepartment> roleDepartment = roleDepartmentDao.findByCriteria(
				Restrictions.eq(DEPARTMENTID, departmentId), Restrictions
						.eq(COMPANYID, getCompanyId()), Restrictions.eq(
								DELETED, false));
		List<Long> checkedRoleIds = new ArrayList<Long>();
		for (RoleDepartment rd : roleDepartment) {
			checkedRoleIds.add(rd.getRole().getId());
		}
		return checkedRoleIds;
	}

	public Page<Department> queryDepartmentByCompany(Page<Department> page,
			Long company) {
		return departmentDao.findByCriteria(page, Restrictions.eq("company.id",
				company), Restrictions.eq(DELETED, false));
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.User, Long> getUserDao() {
		return userDao;
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.DepartmentUser, Long> getDepartmentToUserDao() {
		return departmentToUserDao;
	}

	public SimpleHibernateTemplate<Department, Long> getDepartmentDao() {
		return departmentDao;
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> getSubDeptments(Long deptId){
		return departmentDao.find(SBU_DEPT_HQL, getCompanyId(), deptId, false);
	}

	public Page<Department> getDepartmentsCanAddToRoel(Page<Department> page,
			Long roleId) {

		return getDepartmentDao()
				.find(
						page,
						"select d from Department d where d not in (select d from Department d join d.roleDepartments rd where rd.role.id=? and rd.companyId=? and d.deleted=? and rd.deleted=? )",
						roleId, getCompanyId(), false, false);
		 
	}

	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentsInRole(Long roleId) {
		return departmentDao.find(
						"select d from Department d join d.roleDepartments rd where rd.role.id=? and rd.companyId=? and d.deleted=? and rd.deleted=? order by d.weight desc",
						roleId, getCompanyId(), false, false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> getDepartmentsByUser(Long companyId,Long userId){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and d.deleted=?");
		return departmentDao.find(hql.toString(), companyId, userId, false, false, false);
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.UserInfo, Long> getUserInfoDao() {
		return userInfoDao;
	}

	public SimpleHibernateTemplate<Company, Long> getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(SimpleHibernateTemplate<Company, Long> companyDao) {
		this.companyDao = companyDao;
	}
	

}
