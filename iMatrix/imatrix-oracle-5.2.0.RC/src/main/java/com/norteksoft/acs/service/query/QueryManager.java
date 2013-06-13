package com.norteksoft.acs.service.query;


import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

/**
 *综合查询接口
 * 
 * @author 陈成虎 2009-3-2上午11:43:36
 */

@Service
@Transactional
@SuppressWarnings({ "unused" })
public class QueryManager {

	private static String userHql = "select new list(user.loginName,userInfo.trueName,userInfo.email,userInfo.telephone,dep_user.department.departmentName,role_u.role.name)  "
			+ " from UserInfo userInfo join userInfo.user user  join user.departmentUsers dep_user  "
			+ " join dep_user.user.roleUsers role_u "
			+ " where userInfo.companyId=? and userInfo.dr=? and userInfo.deleted=? and dep_user.deleted=? and role_u.deleted=? and dep_user.department.deleted=? and role_u.role.deleted=?";

	private static String departmentHql = "select new list(user.loginName,dep.name,user.userInfo.trueName,user.userInfo.email,user.userInfo.telephone) from DepartmentUser department_u join department_u.department  dep "
			+ "join department_u.user user where department_u.deleted=? and dep.deleted=? and user.deleted=? "
			+ "and department_u.companyId=?";

	private static String roleHql = "select new list(role.name,user.loginName,user.userInfo.trueName,user.userInfo.email,user.userInfo.telephone) from RoleUser role_u join role_u.role  role join role_u.user user "
			+ "where 1=1 and role_u.deleted=? and role.deleted=? and user.deleted=? and role_u.companyId=?";

	private static String functionHql = "select new list(user.loginName,user.userInfo.trueName,user.userInfo.email,user.userInfo.telephone,depUser.department.name,function.name) from RoleFunction role_f  join role_f.function function "
			+ "join role_f.role role join role.roleUsers role_u "
			+ "join role_u.user user join user.departmentUsers as depUser "
			+ "where role_f.deleted=? and function.deleted=? and role.deleted=? and role_u.deleted=? "
			+ "and user.deleted=? and depUser.deleted=? "
			+ "and  role_f.companyId=?";

	private static String functionHql2 = "select user from User user  join user.departmentUsers dep_user "
			+ "join dep_user.user.roleUsers role_user join role_user.role role "
			+ "join role.roleFunctions role_fun join role_fun.function function "
			+ "where user.deleted=? and dep_user.deleted=? and role_user.deleted=? and role.deleted=? "
			+ "and role_fun.deleted=? and function.deleted=?"
			+ "and  user.companyId=?";

	private static String loginUserLogHql = "from LoginLog as loginLog where loginLog.exitTime is null and loginLog.companyId=? and loginLog.deleted=?";

	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	
	private SimpleHibernateTemplate<DepartmentUser, Long> department_uDao;
	private SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private SimpleHibernateTemplate<RoleFunction, Long> role_fDao;
	private SimpleHibernateTemplate<LoginLog, Long> loginUserLogDao;
	private LogUtilDao logUtilDao;

	private Long companyId;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(
				sessionFactory, UserInfo.class);
		loginUserLogDao = new SimpleHibernateTemplate<LoginLog, Long>(
				sessionFactory, LoginLog.class);
		roleUserDao = new SimpleHibernateTemplate<RoleUser, Long>(
				sessionFactory, RoleUser.class);
		department_uDao = new SimpleHibernateTemplate<DepartmentUser, Long>(
				sessionFactory, DepartmentUser.class);
		role_fDao = new SimpleHibernateTemplate<RoleFunction, Long>(
				sessionFactory, RoleFunction.class);
		logUtilDao = new LogUtilDao(sessionFactory);

	}

	/**
	 * 用户查询
	 * 
	 * @return
	 */

	@Transactional(readOnly = true)
	public Page<UserInfo> getListByUser(Page<UserInfo> page, UserInfo userInfo,
			String initialListView) {

		StringBuilder userInfoHql = new StringBuilder(userHql);

		if ("yes".equals(initialListView)) {
			return userInfoDao.find(page, userInfoHql.toString(),
					ContextUtils.getCompanyId(), 0, false, false, false,
					false, false);

		} else if (userInfo != null) {

			String userName = userInfo.getUser() != null ? userInfo.getUser().getLoginName() : null;
			String trueName = userInfo.getUser().getName();

			if (userName != null && !"".equals(userName) && trueName != null
					&& !"".equals(trueName)) {
				userInfoHql.append(" and user.loginName like ?");
				userInfoHql.append(" and userInfo.trueName like ?");
				return userInfoDao.find(page, userInfoHql.toString(),
						ContextUtils.getCompanyId(), 0, false, false,
						false, false, false, "%" + userName + "%", "%"
								+ trueName + "%");
			}

			if (userName != null && !"".equals(userName)) {
				userInfoHql.append(" and user.loginName like ?");
				return userInfoDao.find(page, userInfoHql.toString(),
						ContextUtils.getCompanyId(), 0, false, false,
						false, false, false, "%" + userName + "%");
			}

			if (trueName != null && !"".equals(trueName)) {
				userInfoHql.append(" and userInfo.trueName like ?");
				return userInfoDao.find(page, userInfoHql.toString(),
						ContextUtils.getCompanyId(), 0, false, false,
						false, false, false, "%" + trueName + "%");
			}
		}
		return userInfoDao.find(page, userInfoHql.toString(),
				ContextUtils.getCompanyId(), 0, false, false, false,
				false, false);

	}

	/**
	 * 部门查询
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<DepartmentUser> getListByDepartment(
			Page<DepartmentUser> page, Department department) {

		StringBuffer hql = new StringBuffer(departmentHql);

		if (department != null && department.getName() != null
				&& !department.getName().equals("")) {
			hql.append(" and dep.name like ? ");
			return department_uDao.find(page, hql.toString(), false, false,
					false, getCompanyId(), "%" + department.getName()
							+ "%");
		}
		return department_uDao.find(page, hql.toString(), false, false, false,
				getCompanyId());
	}

	/**
	 * 角色查询
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<RoleUser> getListByRole(Page<RoleUser> page, Role role) {

		StringBuffer hql = new StringBuffer(roleHql);

		if (role != null && role.getName() != null
				&& !role.getName().equals("")) {
			hql.append(" and role.name like ? ");
			return roleUserDao.find(page, hql.toString(), false, false, false,
					getCompanyId(), "%" + role.getName() + "%");
		}
		return roleUserDao.find(page, hql.toString(), false, false, false,
				getCompanyId());
	}

	/**
	 * 权限查询
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<RoleFunction> getListByFunction(Page<RoleFunction> page,
			Function function) {

		StringBuffer hql = new StringBuffer(functionHql);

		if (function != null && function.getName() != null
				&& !function.getName().equals("")) {
			hql.append(" and function.name like ? ");
			return role_fDao.find(page, hql.toString(), false, false, false,
					false, false, false, getCompanyId(), "%"
							+ function.getName() + "%");
		}
		return role_fDao.find(page, hql.toString(), false, false, false, false,
				false, false, getCompanyId());
	}

	/**
	 * 在线用户查询
	 */
	public Page<LoginLog> getListByLoginUserLog(Page<LoginLog> page,
			LoginLog loginUserLog) {
		loginUserLogDao.searchPageByHql(page, loginUserLogHql,
				ContextUtils.getCompanyId(), false);
		return page;
	}
	
	/**
	 * 查询在线用户数量
	 * @return
	 */
	public Long getOnlineUserCount(){
		return loginUserLogDao.findLong(
				"select count(u) from LoginLog u where u.exitTime is null and u.companyId=? and u.deleted=?", 
			     getCompanyId(), false);
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

}
