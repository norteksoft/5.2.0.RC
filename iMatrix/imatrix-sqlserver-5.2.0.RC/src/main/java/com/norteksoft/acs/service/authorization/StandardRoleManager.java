package com.norteksoft.acs.service.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@SuppressWarnings("deprecation")
@Service
@Transactional
public class StandardRoleManager {
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<FunctionGroup, Long> functionGroupDao;
	private SimpleHibernateTemplate<RoleFunction, Long> roleFunctionDao;
	private SimpleHibernateTemplate<Function, Long> functionDao;
	private Long companyId;
	
	public Long getCompanyId() {
		if (companyId == null) {
			return ContextUtils.getCompanyId();
		} else
			return companyId;
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory, Role.class);
		functionGroupDao = new SimpleHibernateTemplate<FunctionGroup, Long>(sessionFactory, FunctionGroup.class);
		roleFunctionDao = new SimpleHibernateTemplate<RoleFunction, Long>(sessionFactory,RoleFunction.class);
		functionDao = new SimpleHibernateTemplate<Function, Long>(sessionFactory,Function.class);
	}
	
	@Transactional(readOnly = true)
	public Role getStandardRole(Long id){
		return roleDao.get(id);
	}
	
	public Role getStandarRoleByCode(String code, Long systemId){
		return (Role) roleDao.findUnique("from Role sr where sr.code=? and sr.businessSystem.id=? and sr.deleted=?", code, systemId,false);
	}
	/**
	 * 在权限系统中添加的角色带有公司id
	 * @param code
	 * @param systemId
	 * @param companyId
	 * @return
	 */
	public Role getStandarRoleByCode(String code, Long systemId,Long companyId){
		return (Role) roleDao.findUnique("from Role sr where sr.code=? and sr.businessSystem.id=? and sr.deleted=? and sr.companyId=?", code, systemId,false,companyId);
	}
	
	public void deleteStandardRole(Long id){
		Role role = roleDao.get(id);
		role.setDeleted(true);
		roleDao.save(role);
	}

	@Transactional(readOnly = true)
	public List<Role> getAllStandardRole(Long sysId){
		return roleDao.findByCriteria(Restrictions.eq("businessSystem.id", sysId),Restrictions.eq("deleted", false));
	}
	@Transactional(readOnly = true)
	public List<Role> getAllStandardRoleByCompany(Long sysId,Long companyId){
		String hql = "from Role sr where sr.businessSystem.id=? and sr.deleted=? and sr.companyId=null";
		if(companyId!=null){
			hql = "from Role sr where sr.businessSystem.id=? and sr.deleted=? and (sr.companyId!=null and sr.companyId=?)";
			return roleDao.find(hql, sysId,false,companyId);
		}else{
			return roleDao.find(hql, sysId,false);
		}
	}

	@Transactional(readOnly = true)
	public Page<Role> getAllStandardRole(Page<Role> page, Long sysId){
		String hql = "from Role sr where sr.businessSystem.id=? and sr.deleted=? order by sr.weight desc";
		return roleDao.find(page, hql,sysId,false);
	}
	
	public void saveStandardRole(Role role){
		roleDao.save(role);
	}
	
	/**
	 * 角色添加功能 
	 */
	public Page<FunctionGroup> listFunctions(Page<FunctionGroup> functionpage,Long sysId){
		return functionGroupDao.findByCriteria(
				functionpage, Restrictions.eq("businessSystem.id", sysId), Restrictions.eq("deleted", false));
	}
	
	/**
	 * 角色移除功能 
	 */
	public Page<FunctionGroup> canRemoveFunctions(Page<FunctionGroup> functionpage, Long sysId, Long roleId){
		String hql = "select distinct fung from FunctionGroup fung " +
				     "join fung.functions fun join fun.roleFunctions r_f " +
				     "where r_f.role.id=? and fun.deleted=? " +
				     "and r_f.deleted=? and fung. deleted=? and fung.businessSystem.id=?";
		return functionGroupDao.find(functionpage, hql, roleId, false, false, false, sysId);
	}
	
	public List<Long> getFunctionIds(Long roleId,Long sysId) {
		List<Long> FunctionIds = new ArrayList<Long>();
		List<RoleFunction> role_Functions = roleFunctionDao.findByCriteria(
				Restrictions.eq("role.id", roleId), Restrictions.eq("deleted", false));
		for (RoleFunction role_Function : role_Functions) {
			if(role_Function.getFunction()!=null){
				FunctionIds.add(role_Function.getFunction().getId());
			}
		}
		return FunctionIds;
	}

	public void roleAddFunction(Long roleId,List<Long> functionIds,Integer isAdd){
		Role role = roleDao.get(roleId);
		if(isAdd==0){
			RoleFunction role_f = null;
			for (Long funId : functionIds) {
				role_f = new RoleFunction();
				role_f.setRole(role);
				role_f.setFunction(functionDao.get(funId));
				role_f.setCompanyId(getCompanyId());
				roleFunctionDao.save(role_f);
			}
		}
		if(isAdd==1){
			List<RoleFunction> funList = roleFunctionDao.findByCriteria(
					Restrictions.in("function.id", functionIds), Restrictions.eq("role.id", roleId), Restrictions.eq("deleted", false));
			for (RoleFunction role_Function : funList) {
				role_Function.setDeleted(true);
				roleFunctionDao.save(role_Function);
			}
		}
	}
	
	public Long getSystemId(){
		return ContextUtils.getSystemId();
	}
	
	/**
	 * 根据用户ID查询用户所有的角色
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesByUser(Long userId, Long companyId){
		StringBuilder rolesByUserHql = new StringBuilder();
		rolesByUserHql.append("select r from User u join u.roleUsers ru join ru.role r ");
		rolesByUserHql.append("where u.deleted=? and ru.deleted=? and r.deleted=? and r.businessSystem.id=? and u.id=? and (r.companyId is null or r.companyId=?)");
		List<Role> userRoles = roleDao.find(rolesByUserHql.toString(), false, false, false, getSystemId(), userId, companyId);
		
		StringBuilder rolesByDepartmentHql = new StringBuilder();
		rolesByDepartmentHql.append("select r from User u join u.departmentUsers du join du.department d join d.roleDepartments rd join rd.role r ");
		rolesByDepartmentHql.append("where u.deleted=? and du.deleted=? and d.deleted=? and rd.deleted=? and r.deleted=? and r.businessSystem.id=? and u.id=? and (r.companyId is null or r.companyId=?)");
		List<Role> departmentRoles = roleDao.find(rolesByDepartmentHql.toString(), false, false, false,false, false, getSystemId(), userId, companyId);
		
		StringBuilder rolesByWorkgroupHql = new StringBuilder();
		rolesByWorkgroupHql.append("select r from User u join u.workgroupUsers wu join wu.workgroup w join w.roleWorkgroups rw join rw.role r ");
		rolesByWorkgroupHql.append("where u.deleted=? and wu.deleted=? and w.deleted=? and rw.deleted=? and r.deleted=? and r.businessSystem.id=?  and u.id=? and (r.companyId is null or r.companyId=?)");
		List<Role> workgroupRoles = roleDao.find(rolesByWorkgroupHql.toString(), false, false, false,false, false, getSystemId(), userId, companyId);
		
		Set<Role> roles = new HashSet<Role>();
		roles.addAll(userRoles);
		roles.addAll(departmentRoles);
		roles.addAll(workgroupRoles);
		return roles;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Role> getAllRolesByUser(Long userId, Long companyId){
		StringBuilder rolesByUserHql = new StringBuilder();
		rolesByUserHql.append("select r from User u join u.roleUsers ru join ru.role r ");
		rolesByUserHql.append("where u.deleted=? and ru.deleted=? and r.deleted=? and u.id=? and  u.companyId=?");
		List<Role> userRoles = roleDao.find(rolesByUserHql.toString(), false, false, false, userId, companyId);
		
		StringBuilder rolesByDepartmentHql = new StringBuilder();
		rolesByDepartmentHql.append("select r from User u join u.departmentUsers du join du.department d join d.roleDepartments rd join rd.role r ");
		rolesByDepartmentHql.append("where u.deleted=? and du.deleted=? and d.deleted=? and rd.deleted=? and r.deleted=? and u.id=? and  u.companyId=?");
		List<Role> departmentRoles = roleDao.find(rolesByDepartmentHql.toString(), false, false, false,false, false, userId, companyId);
		
		StringBuilder rolesByWorkgroupHql = new StringBuilder();
		rolesByWorkgroupHql.append("select r from User u join u.workgroupUsers wu join wu.workgroup w join w.roleWorkgroups rw join rw.role r ");
		rolesByWorkgroupHql.append("where u.deleted=? and wu.deleted=? and w.deleted=? and rw.deleted=? and r.deleted=?  and u.id=? and u.companyId=?");
		List<Role> workgroupRoles = roleDao.find(rolesByWorkgroupHql.toString(), false, false, false,false, false, userId, companyId);
		
		Set<Role> roles = new HashSet<Role>();
		roles.addAll(userRoles);
		roles.addAll(departmentRoles);
		roles.addAll(workgroupRoles);
		return roles;
	}
	
	/**
	 * 根据角色集合查询所有角色能访问的资源
	 * @param roles
	 * @return
	 */
	public Set<Function> getFunctionsByRoles(Collection<Role> roles){
		Set<Function> functions = new HashSet<Function>();
		for(Role role : roles){
			functions.addAll(getFunctionsByRole(role));
		}
		return functions;
	}
	
	/**
	 * 根据角色查询所有角色能访问的资源
	 * @param role
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Function> getFunctionsByRole(Role role){
		StringBuilder hql = new StringBuilder();
		hql.append("select f from Function f join f.roleFunctions rf join rf.role r where r.id=? and r.deleted=? and rf.deleted=? and f.deleted=? ");
		return functionDao.find(hql.toString(), role.getId(), false, false, false);
	}
	
	
	public RoleFunction getRoleFunction(String roleCode,String funPath,String code){
		List<RoleFunction> roleFuncs=functionDao.find("from RoleFunction rf where (rf.role!=null and rf.role.code=?) and (rf.function!=null and rf.function.path=? and rf.function.code=?) and rf.deleted=?",roleCode,funPath,code,false );
		if(roleFuncs.size()>0)return roleFuncs.get(0);
		return null;
	}
	
	public void saveRoleFunction(RoleFunction roleFun){
		roleFunctionDao.save(roleFun);
	}


	@SuppressWarnings("unchecked")
	public List<Role> getRolesBySystemId(Long bsId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select r from Role r join r.businessSystem bs  where bs.id=? order by r.weight desc");
		return functionDao.find(hql.toString(), bsId);
	}
}
