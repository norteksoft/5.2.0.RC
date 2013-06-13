package com.norteksoft.acs.service.authorization;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.query.QueryManager;

/**
 * 供权限API使用的Manager
 * @author xiaoj
 */
@Service
@Transactional
public class AcsApiManager {

	public final static String DELETED = "deleted";
	private static final String TRUE_STRING = "true";
	private static final String FALSE_STRING = "false";
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<User, Long> userDao;
	private QueryManager queryManager;
	private SimpleHibernateTemplate<BusinessSystem, Long> businessDao;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		workGroupDao = new SimpleHibernateTemplate<Workgroup, Long>(sessionFactory, Workgroup.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory, Role.class);
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory, User.class);
		businessDao=new SimpleHibernateTemplate<BusinessSystem, Long>(sessionFactory, BusinessSystem.class);
	}
	
	/**
	 * 查询公司所有的部门
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Department, String> getAllDepts(Long companyId){
		List<Department> depts =  departmentDao.findByCriteria(
				Restrictions.eq("company.id", companyId), 
				Restrictions.eq(DELETED, false), 
				Restrictions.isNull("parent"));
		return getDeptsHasSubDept(depts);
	}
	
    public List<Department> getAllDeptsInOrder(Long companyId){
		return departmentDao.find("FROM Department d WHERE (d.company.id=? AND d.deleted=? and d.parent is null) ORDER BY d.weight desc", companyId, false);
	}
	
	@Autowired
	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}
	
	/**
	 * 查询在线用户数
	 * @return
	 */
	public Long getOnlineUserCount(){
		return queryManager.getOnlineUserCount();
	}
	
	/**
	 * 查询一个部门的所有子部门
	 * @param companyId
	 * @param parentDeptName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Department, String> getSubDeptsByParentDept(Long companyId, String parentDeptName){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.parent pd ");
		hql.append("where pd.company.id = ? and pd.name = ? and d.deleted = false and pd.deleted = false");
		List<Department> depts = departmentDao.find(hql.toString(), companyId, parentDeptName);
		return getDeptsHasSubDept(depts);
	}
	
	private Map<Department, String> getDeptsHasSubDept(List<Department> depts){
		Map<Department, String> result = new HashMap<Department, String>();
		String hasSubDept = FALSE_STRING;
		for(Department dept : depts){
			Set<Department> subDepts = dept.getChildren();
			for(Department subDept : subDepts){
				if(!subDept.isDeleted()){
					hasSubDept = TRUE_STRING;
					break;
				}
			}
			result.put(dept, hasSubDept);
			hasSubDept = FALSE_STRING;
		}
		return result;
	}
	
	public String hasSubDepartment(Department dept){
			Set<Department> subDepts = dept.getChildren();
			for(Department subDept : subDepts){
				if(!subDept.isDeleted()){
					return TRUE_STRING;
				}
			}
			return FALSE_STRING;
	}

	/**
	 * 查询所有的工作中
	 * @param companyId
	 * @return
	 */
	public List<Workgroup> getAllWorkGroups(Long companyId){
		return workGroupDao.findByCriteria(
				Restrictions.eq("company.id", companyId), 
				Restrictions.eq(DELETED, false));
	}

	/**
	 * 查询系统所有的角色
	 * @param systemId
	 * @return
	 */
	public List<Role> getAllRoles(Long systemId){
		String hql = "from Role sr where sr.businessSystem.id=? and sr.deleted=? order by sr.weight desc";
		return roleDao.find( hql,systemId, false);
	}

	/**
	 * 查询公司所有的用户
	 * @param companyId
	 * @return
	 */
	public List<User> getAllUsers(Long companyId){
		return userDao.findByCriteria(
				Restrictions.eq("companyId", companyId), 
				Restrictions.eq(DELETED, false));
	}

	/**
	 * 查询公司某部门下所有的用户
	 * @param companyId
	 * @param departmentName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByDept(Long companyId, String departmentName){
		StringBuilder hql = new StringBuilder();
		hql.append("select u from User u join u.departmentUsers du join du.department d ");
		hql.append("where d.company.id=? and d.name = ? and u.deleted=false and ");
		hql.append("du.deleted=false and d.deleted=false");
		return userDao.find(hql.toString(), companyId, departmentName);
	}

	/**
	 * 查询公司某工作组下所有的用户
	 * @param companyId
	 * @param workGroupName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByWorkGroup(Long companyId, String workGroupName){
		StringBuilder hql = new StringBuilder();
		hql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		hql.append("where wg.company.id=? and wg.name = ? and u.deleted=false and ");
		hql.append("wgu.deleted=false and wg.deleted=false");
		return userDao.find(hql.toString(), companyId, workGroupName);
	}

	/**
	 * 根据某系统的角色查询公司所有的用户
	 * @param systemId
	 * @param companyId
	 * @param roleName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<User> getUsersByRole(Long systemId, Long companyId, String roleName){
		Set<User> result = new HashSet<User>();
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u from User u join u.roleUsers ru join ru.role r ");
		usersByRole.append("where r.name = ? and u.companyId=? and r.deleted=false and ");
		usersByRole.append("ru.deleted=false and u.deleted=false");
		List<User> roleUsers = userDao.find(usersByRole.toString(), roleName, companyId);
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r ");
		usersByDeptRoleHql.append("where r.name = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false");
		List<User> roleDeptUsers = userDao.find(usersByDeptRoleHql.toString(), roleName, companyId);
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r join r.businessSystem rbs ");
		usersByWgRoleHql.append("where rbs.id=? and r.name = ? and wg.company.id=? and rbs.deleted=false and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false");
		List<User> roleWgUsers = userDao.find(usersByWgRoleHql.toString(), systemId, roleName, companyId);
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleWgUsers);
		return result;
	}
	
	/**
	 * 查询在同一部门的所有用户
	 * @param companyId
	 * @param userLoginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersInSameDept(Long companyId, String userLoginName){
		StringBuilder hql = new StringBuilder();
		hql.append("select DISTINCT u from User u join u.departmentUsers du join du.department d ");
		hql.append("join d.departmentUsers du_ join du_.user u_ ");
		hql.append("where d.company.id=? and u_.loginName = ? and u.deleted=false and ");
		hql.append("du.deleted=false and d.deleted=false and u_.deleted=false and du_.deleted=false");
		return userDao.find(hql.toString(), companyId, userLoginName);
	}
	
	/**
	 * 根据特定条件查询用户(WF使用)
	 * @param companyId
	 * @param conditions
	 * @return
	 */
	public List<User> getUsersByCondition(Long companyId, String conditions){
		StringBuilder sql = getQuerySql();
		if(StringUtils.isNotEmpty(conditions)){
			sql.append(" and ").append(conditions);
		}
		return userDao.findByJdbc(sql.toString(), companyId);
	}
	
	private StringBuilder getQuerySql(){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT u.* FROM acs_user u ");
		sql.append("LEFT JOIN acs_department_user du ON du.fk_user_id = u.id and du.deleted = 0 ");
		sql.append("LEFT JOIN acs_department d ON d.id = du.fk_department_id and d.deleted = 0 ");
		sql.append("LEFT JOIN acs_workgroup_user wgu ON wgu.fk_user_id = u.id and wgu.deleted = 0 ");
		sql.append("LEFT JOIN acs_workgroup wg ON wg.id = wgu.fk_workgroup_id and wg.deleted = 0 ");
		sql.append("LEFT JOIN acs_role_user ru ON ru.fk_user_id = u.id and ru.deleted = 0 ");
		sql.append("LEFT JOIN acs_role r ON r.id = ru.fk_role_id and r.deleted = 0 ");
		sql.append("LEFT JOIN acs_role_department rd ON rd.fk_role_id = r.id AND rd.fk_department_id = d.id and rd.deleted = 0 ");
		sql.append("LEFT JOIN acs_role_workgroup rwg ON rwg.fk_role_id = r.id AND rwg.fk_workgroup_id = wg.id and rwg.deleted = 0 ");
		sql.append("WHERE u.deleted = 0 and u.fk_company_id = ?  ");
		return sql;
	}
	
	/**
	 * 查询不再任何部门的用户
	 * @param companyId
	 * @return
	 */
	public List<User> getUsersNotInDept(Long companyId){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT ACS_USER.* FROM ACS_USER LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USER.ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USER.DELETED=0 AND ACS_USER.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ");
		return userDao.findByJdbc(sqlString.toString(), companyId);
	}
	
	@SuppressWarnings("unchecked")
	public List<BusinessSystem> getAllBusiness(Long companyId) {
		String hql = "select si.product.systemId from SubscriberItem si join si.subsciber s where s.tenantId=? and si.invalidDate>?";
		List<Long> idList = businessDao.find(hql, companyId, new Date());
		if(idList.isEmpty()){
			return new ArrayList<BusinessSystem>();
		}
		return businessDao.findByCriteria(Restrictions.in("id",idList),Restrictions.eq("deleted",false));
	}
	public BusinessSystem getSystemBySystemCode(String code) {
		BusinessSystem bs = (BusinessSystem) businessDao.findUnique(
				"from BusinessSystem bs where bs.code=? and bs.deleted=?", code, false);
		return bs;
	}
	
}
