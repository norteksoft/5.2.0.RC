package com.norteksoft.acs.service.authorization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

/**
*资源管理
*@author 陈成虎
*2009-3-2上午11:40:38
*/
@Service
@Transactional
public class FunctionManager{
	
	private static String DELETED = "deleted";
	private static String BUSINESSSYSTEM_ID ="businessSystem.id";
	private static String CODE ="code";
	private static String ROLE_ID ="role.id";
	private static String FUNCTION_ID = "function.id";
	private static String COMPANYID = "companyId";
	private static String NAME ="name";
	private static String COMPANY_ID ="company.id";
	private static String hql = "from Function f where f.deleted=?";
	private static String customRolehql = "select role from CustomRole role join role.roleFunctions r_f where r_f.function.id=? and r_f.companyId=? and role.deleted=? and r_f.deleted=? ";
	
	private SimpleHibernateTemplate<Function, Long> functionDao;	
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<RoleFunction, Long> role_fDao;
	
	
	private Long companyId;
	
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
		functionDao = new SimpleHibernateTemplate<Function, Long>(sessionFactory, Function.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory, Role.class);
		role_fDao = new SimpleHibernateTemplate<RoleFunction, Long>(sessionFactory,RoleFunction.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory,Role.class);
	}
	/**
	 * 查询所有资源信息
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Function> getAllFunction(){
		return functionDao.findByCriteria(Restrictions.eq(DELETED, false));

	}
	
	/**
	 * 获取单条资源信息
	 */
	@Transactional(readOnly = true)
	public Function getFunction(Long id) {
		return functionDao.get(id);
	}
	
	/**
	 * 分页查询所有资源信息
	 */
	@Transactional(readOnly = true)
	public Page<Function> getAllFunction(Page<Function> page, Long sysId) {
			return functionDao.findByCriteria(
					page,Restrictions.eq(DELETED, false),Restrictions.eq(BUSINESSSYSTEM_ID, sysId));
	}
	
	/**
	  * 保存资源信息
	  */	
	public void saveFunction(Function function){
		
		functionDao.save(function);
	}
	
	/**
	 * 删除资源信息
	 */
	public void deleteFunction(Long id) {
		Function function = functionDao.get(id);
		function.setDeleted(true);
		functionDao.save(function);
	}	
	/**
	 * 按条件检索资源
	 */
	@Transactional(readOnly = true)
	public Page<Function> getSearchFunction(Page<Function> page,Function function,boolean deleted) {
         StringBuilder functionHql = new StringBuilder(hql);
		
		 if (function != null) {
			
			String functionId = function.getCode();
			String functionName = function.getName();
			
			if (!"".equals(functionId) && !"".equals(functionName)) {
				functionHql.append(" and f.code like ?");
				functionHql.append(" and f.name like ?");
				return functionDao.find(page, functionHql.toString(),
						                false,
						                "%" + functionId+ "%", "%" + functionName + "%");
			}
			
			if (!"".equals(functionId)) {
				functionHql.append(" and f.code like ?");
				return functionDao.find(page, functionHql.toString(),
						                false,
						                "%" + functionId+ "%");
			}
			
			if (!"".equals(functionName)) {
				functionHql.append(" and f.name like ?");
				return functionDao.find(page, functionHql.toString(),
						                false,
						                "%" + functionName + "%");
			}
		}
        
		return functionDao.find(page, hql, false);
	}
	  
    /**
	 *查询资源添加的角色
	 */
	public Page<Role> functionToRoleList(Page<Role> page, Role entity,Long sysId) {

		if (entity != null) {

			String roleName = entity.getName();
			if (roleName != null && !"".equals(roleName)) {

				return roleDao.findByCriteria(page, 
											  Restrictions.eq(BUSINESSSYSTEM_ID, sysId), 
											  Restrictions.like(NAME, "%" + roleName + "%"),
											  Restrictions.eq(DELETED, false));

			}

		}

		return roleDao.findByCriteria(page, 
				                      Restrictions.eq(BUSINESSSYSTEM_ID, sysId), 
				                      Restrictions.eq(COMPANY_ID, getCompanyId()),
				                      Restrictions.eq(DELETED, false));

	}
	  /**
	   * 
	   * 查询资源要移除的角色
	   */
	  public Page<Role> functionToRomoveRoleList(Page<Role> page,Role entity,Long sysId,Long funId){
		 
		  if(entity!=null){
	    	  
			  String roleName = entity.getName();
	    	  if(roleName!=null&&!"".equals(roleName)){
	    		  StringBuilder hqL = new StringBuilder(customRolehql);
	    		  hqL.append(" and role.name like ? ");
	    		  return roleDao.find(page, hqL.toString(), funId,getCompanyId(),false,false,"%"+roleName+"%");
                                               
	    	 }
	    	
	     }
	    
		  return roleDao.find(page, customRolehql, funId,getCompanyId(),false,false);
	
	  }
	  
	  
	 
	//查处资源拥有的角色
	@SuppressWarnings("unchecked")
	public List<Long> getRoleIds(Long function_Id){
		  List<Long> functionIds = new ArrayList<Long>();
		  List<RoleFunction> role_f = role_fDao.findByCriteria(Restrictions.eq(FUNCTION_ID, function_Id),
										                        Restrictions.eq(COMPANYID, getCompanyId()),
											                    Restrictions.eq(DELETED, false));
		  for (Iterator iterator = role_f.iterator(); iterator.hasNext();) {
			RoleFunction role_Function = (RoleFunction) iterator.next();
			functionIds.add(role_Function.getRole().getId());
			
		}
		  return functionIds;
	  }
	  //保存资源和角色的关系
	  public void functionAddRole(Long function_Id,List<Long> roleIds,Integer isAdd){
		//查出要加入角色的资源
		  Function function = getFunction(function_Id);
		 StringBuilder roleName = new StringBuilder();
		// 资源添加角色
		 if(isAdd==0){
			 RoleFunction role_Function;
			 Role role = null;
			  for (Long rId : roleIds) {
				  role_Function = new RoleFunction();
				  role = roleDao.get(rId);
				  role_Function.setRole(role);
				  role_Function.setFunction(function);
 
				  role_Function.setCompanyId(getCompanyId());
				  role_fDao.save(role_Function);
				  roleName.append(role.getName());
				  roleName.append(",");
			} 
			  roleName.deleteCharAt(roleName.length()-1);
		 }
		// 资源移除角色
		 if(isAdd==1){
			 
			 List<RoleFunction>  list = role_fDao.findByCriteria(Restrictions.in(ROLE_ID, roleIds),
											                      Restrictions.eq(FUNCTION_ID, function_Id),
											                      Restrictions.eq(COMPANYID, getCompanyId()),
											                      Restrictions.eq(DELETED, false));
											                     
			 for (RoleFunction role_Function : list) {
				// 根据选中的角色ID查处资源具有的角色
				
				 //改变删除标志字段
				 role_Function.setDeleted(true);
				 role_fDao.save(role_Function);
				 roleName.append(role_Function.getRole().getName());
				 roleName.append(",");
			}
		    roleName.deleteCharAt(roleName.length()-1);
		 }
		  
	  }
	  
	public Page<Function> getFunctionsByFunctionGroup(Page<Function> page, Long functionGroupId) {
		return functionDao.findByCriteria(page, Restrictions.eq(CODE, functionGroupId));
	}
	
	public List<Function> getFunctionsByFunctionGroup(Long groupId) {
		return functionDao.findList("from Function f where f.functionGroup.id=? and f.deleted=?", groupId, false);
	}
	
	public SimpleHibernateTemplate<Function, Long> getFunctionDao() {
		return functionDao;
	}

	public SimpleHibernateTemplate<RoleFunction, Long> getRole_fDao() {
		return role_fDao;
	}
	
	/**
	 * 获取系统下所有的资源(分页)
	 */
	public Page<Function> getFunctionsBySystem(Page<Function> page, Long systemId){
		return functionDao.findByCriteria(page, Restrictions.eq(BUSINESSSYSTEM_ID, systemId),
				Restrictions.eq(DELETED, false));
	}
	
	/**
	 * 获取系统下所有的资源
	 */
	public List<Function> getFunctionsBySystem(Long systemId){
		return functionDao.findByCriteria(Restrictions.eq(BUSINESSSYSTEM_ID, systemId),
				Restrictions.eq(DELETED, false));
	}
	
	/**
	 * 获取资源组中能移除的的资源
	 */
	public Page<Function> getFunctionsCanRemoveFromFunctionGroup(Page<Function> page, Long functionGroupId){
		return functionDao.findByCriteria(page, Restrictions.eq(CODE, functionGroupId),
				Restrictions.eq(DELETED, false));
	}
	
	public List<Function> getFunctionsByGroup(Long functionGroupId) {
		return functionDao.find("from Function f where f.functionGroup.id=?", functionGroupId);
	}
	public List<Function> getUnGroupFunctions(Long systemId) {
		return functionDao.find("from Function f where f.functionGroup=null and (f.businessSystem!=null and f.businessSystem.id=?)", systemId);
	}
	
	public Function getFunctionByPath(String path,Long systemId,String funId){
		List<Function> funs= functionDao.find("from Function f where f.path=? and f.functionId=? and (f.businessSystem!=null and f.businessSystem.id=?)", path,funId,systemId);
		if(funs.size()>0)return funs.get(0);
		return null;
	}
	@Transactional(readOnly = true)
	public Function getFunctionByPath(String path,Long systemId){
		List<Function> funs= functionDao.find("from Function f where f.path=? and (f.businessSystem!=null and f.businessSystem.id=?)", path,systemId);
		if(funs.size()>0)return funs.get(0);
		return null;
	}
	@Transactional(readOnly = true)
	public String getFunctionPathByCode(String code,String systemCode){
		List<Function> funs= functionDao.find("from Function f where f.code=? and (f.businessSystem!=null and f.businessSystem.code=?)", code,systemCode);
		if(funs.size()>0)return funs.get(0).getPath();
		return null;
	}
}
