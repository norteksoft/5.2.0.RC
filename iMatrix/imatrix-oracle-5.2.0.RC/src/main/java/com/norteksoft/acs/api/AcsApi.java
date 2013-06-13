package com.norteksoft.acs.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.AcsApiManager;
import com.norteksoft.product.util.ContextUtils;

/**
 * 权限API
 * @author xiaoj
 */
@Service
@Transactional
public class AcsApi {

	private static Log log = LogFactory.getLog(AcsApi.class);
	public static final String DEPARTMENT_NAME_CONDITION = "d.department_name";
	public static final String WORKGROUP_NAME_CONDITION = "wg.work_group_name";
	public static final String ROLE_NAME_CONDITION = "r.role_name";
	public static final String USER_NAME_CONDITION = "u.user_name";
	
	private static AcsApiManager getAcsApiManager(){
		return (AcsApiManager)ContextUtils.getBean("acsApiManager");
	}
	
	/**
	 * 查询公司所有的部门
	 * @param companyId   公司ID
	 * @return List       [部门名称列表, 是否有子部门(true,false),是否有人员(true,false)]
	 */
	public static List<String[]> getAllDepts(Long companyId){
		log.debug("*** getAllDepts 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append("]").toString());
		
 		//Map<Department, String> depts = getAcsApiManager().getAllDepts(companyId);
		List<Department> depts = getAcsApiManager().getAllDeptsInOrder(companyId);
		List<String[]> results = new ArrayList<String[]>();
		for(Department dept : depts){
			String isHasUsersInDept=getAcsApiManager().getUsersByDept(companyId, dept.getName()).size()>0?"true":"false";
			results.add(new String[]{dept.getName(), getAcsApiManager().hasSubDepartment(dept),isHasUsersInDept});
		}
		
		log.debug("*** getAllDepts 方法结束");
		return results;
	}
	
	
	
	/**
	 * 根据部门名称查询该部门的所有子部门
	 * @param companyId
	 * @param parentDeptName
	 * @return List  [部门名称列表, 是否有子部门(true,false),是否有人员(true,false)]
	 */
	public static List<String[]> getSubDeptsByParentDept(Long companyId, String parentDeptName){
		log.debug("*** getSubDeptsByParentDept 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", parentDeptName:").append(parentDeptName)
			.append("]").toString());
		
		Map<Department, String> depts = getAcsApiManager().getSubDeptsByParentDept(companyId, parentDeptName);
		List<String[]> results = new ArrayList<String[]>();
		for(Department dept : depts.keySet()){
			String isHasUsersInDept=getAcsApiManager().getUsersByDept(companyId, dept.getName()).size()>0?"true":"false";
			results.add(new String[]{dept.getName(), depts.get(dept),isHasUsersInDept});
		}
		
		log.debug("*** getSubDeptsByParentDept 方法结束");
		return results;
	}

	/**
	 * 查询公司所有的工作组
	 * @param companyId   公司ID
	 * @return List       工作组名称列表
	 */
	public static List<String> getAllWorkGroups(Long companyId){
		log.debug("*** getAllWorkGroups 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append("]").toString());
		
		
		List<Workgroup> wgs = getAcsApiManager().getAllWorkGroups(companyId);
		List<String> results = new ArrayList<String>();
		for(Workgroup wg : wgs){
			results.add(wg.getName());
		}
		
		log.debug("*** getAllWorkGroups 方法结束");
		return results;
	}

	/**
	 * 查询公司所有的角色
	 * @param companyId   公司ID
	 * @return List       角色名称列表
	 */
	public static List<String> getAllRolesBySystemId(Long systemId){
		log.debug("*** getAllRolesBySystemId 方法开始");
		List<Role> rs = getAcsApiManager().getAllRoles(systemId);
		List<String> results = new ArrayList<String>();
		for(Role r : rs){
			results.add(r.getName());
		}
		
		log.debug("*** getAllRolesBySystemId 方法结束");
		return results;
	}
	/**
	 * 查询公司所有的角色
	 * @param companyId   公司ID
	 * @return List       角色名称列表
	 */
	public static List<String> getAllRoles(String systemCode){
		log.debug("*** getAllRoles 方法开始");
		BusinessSystem system=getAcsApiManager().getSystemBySystemCode(systemCode);
		if(system!=null)return getAllRolesBySystemId(system.getId());
		log.debug("*** getAllRoles 方法结束");
		return new ArrayList<String>();
	}

	/**
	 * 查询公司所有的人员
	 * @param companyId
	 * @return String[用户名称，用户登录名称] 
	 */
	public static List<String[]> getAllUsers(Long companyId){
		log.debug("*** getAllUsers 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append("]").toString());
		
		List<User> us = getAcsApiManager().getAllUsers(companyId);
		
		log.debug("*** getAllUsers 方法结束");
		return getUserNamesFromList(us);
	}

	/**
	 * 查询部门下所有的人员
	 * @param companyId
	 * @param name
	 * @return String[用户名称，用户登录名称] 
	 */
	public static List<String[]> getUsersByDept(Long companyId, String name){
		log.debug("*** getUsersByDept 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", name:").append(name)
			.append("]").toString());
		
		List<User> us = getAcsApiManager().getUsersByDept(companyId, name);

		log.debug("*** getUsersByDept 方法结束");
		return getUserNamesFromList(us);
	}

	/**
	 * 查询工作组下所有的人员
	 * @param companyId
	 * @param workGroupName
	 * @return String[用户名称，用户登录名称] 
	 */
	public static List<String[]> getUsersByWorkGroup(Long companyId, String workGroupName){
		log.debug("*** getUsersByWorkGroup 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", name:").append(workGroupName)
			.append("]").toString());
		
		List<User> us = getAcsApiManager().getUsersByWorkGroup(companyId, workGroupName);

		log.debug("*** getUsersByWorkGroup 方法结束");
		return getUserNamesFromList(us);
	}

	/**
	 * 查询拥有该角色的所有人员
	 * @param companyId
	 * @param roleName
	 * @return String[用户名称，用户登录名称] 
	 */
	public static List<String[]> getUsersByRole(Long companyId, String roleName){
		log.debug("*** getUsersByRole 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", roleName:").append(roleName)
			.append("]").toString());
		
		Set<User> us = getAcsApiManager().getUsersByRole(ContextUtils.getSystemId(), companyId, roleName);
		
		log.debug("*** getUsersByRole 方法结束");
		return getUserNamesFromList(us);
	}
	
	/**
	 * 查询与给定用户名在同一部门的用户
	 * @param companyId
	 * @param userLoginName 用户登录名
	 * @return
	 */
	public static List<String[]> getUsersInSameDept(Long companyId, String userLoginName){
		log.debug("*** getUsersInSameDept 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", userLoginName:").append(userLoginName)
			.append("]").toString());
		
		List<User> us = getAcsApiManager().getUsersInSameDept(companyId, userLoginName);

		log.debug("*** getUsersInSameDept 方法结束");
		return getUserNamesFromList(us);
	}
	
	/**
	 * 根据条件查询用户
	 * @param companyId
	 * @param conditions
	 * @return
	 */
	public static List<String[]> getUsersByCondition(Long companyId, String conditions){
		log.debug("*** getUsersByCondition 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append(", conditions:").append(conditions)
			.append("]").toString());
		
		List<User> us = getAcsApiManager().getUsersByCondition(companyId, conditions);

		log.debug("*** getUsersByCondition 方法结束");
		return getUserNamesFromList(us);
	}
	
	/**
	 * 根据条件查询用户
	 * @param companyId
	 * @param conditions
	 * @return List<[用户名, 登录名, 邮件地址]>
	 */
	public static List<String[]> getUserEmailByCondition(Long companyId, String conditions){
		
		List<User> users = getAcsApiManager().getUsersByCondition(companyId, conditions);

		List<String[]> results = new ArrayList<String[]>();
		String[] names = null;
		if(users != null){
			for(User u : users){
				names = new String[3];
				names[0] = u.getName();
				names[1] = u.getLoginName();
				names[2] = u.getEmail();
				results.add(names);
			}
		}
		return results;
	}
	
	/**
	 * 查询没有在任何部门的用户
	 * @param companyId
	 * @return
	 */
	public static List<String[]> getUsersNotInDept(Long companyId){
		log.debug("*** getUsersNotInDept 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("companyId:").append(companyId)
			.append("]").toString());
		
		List<User> us = getAcsApiManager().getUsersNotInDept(companyId);

		log.debug("*** getUsersNotInDept 方法结束");
		return getUserNamesFromList(us);
	}
	
	private static List<String[]> getUserNamesFromList(Collection<User> users){
		log.debug("*** getUserNamesFromList 方法开始");
		
		List<String[]> results = new ArrayList<String[]>();
		String[] names = null;
		if(users != null){
			for(User u : users){
				names = new String[2];
				names[0] = u.getName();
				names[1] = u.getLoginName();
				results.add(names);
			}
		}
		
		log.debug("*** getUserNamesFromList 方法结束");
		return results;
	}
	
	/**
	 * 查询所有业务系统信息
	 */
	public static List<String[]> getAllBusiness(Long companyId){
		List<BusinessSystem> list=getAcsApiManager().getAllBusiness(companyId);
		List<String[]> results = new ArrayList<String[]>();
		String[] bs = null;
		if(list != null){
			for(BusinessSystem u : list){
				bs = new String[3];
				bs[0] = u.getId().toString();
				bs[1] = u.getName();
				bs[2] = u.getCode();
				results.add(bs);
			}
		}
		return results;
	}
	
}
