package com.norteksoft.wf.base.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.wf.base.enumeration.TextOperator;

public class UserUtil {
	
	/**
	 * 判断用户是不是在部门里，如果在返回ture；否则，返回false。
	 * @param companyId 
	 * @param loginName 
	 * @param departmentName
	 * @return 用户在部门里，返回ture；否则，返回false。
	 */
	public static boolean userInDepartment(Long companyId,String loginName,String departmentName){
		if(ContextUtils.getCompanyId()==null){
			ThreadParameters parameters=new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
		}
		List<Department> departments = ApiFactory.getAcsService().getDepartments(loginName);
		for (Department department : departments) {
			if(department.getName().equals(departmentName)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断用户是不是拥有某权限，如果有返回ture；否则，返回false。
	 * @param companyId 
	 * @param loginName 
	 * @param departmentName
	 * @return 用户拥有某权限，返回ture；否则，返回false。
	 */
	public static boolean userHaveRole(Long companyId,String loginName,String roleName){
		//获得子系统的id集合
		BusinessSystemManager businessSystemManager = (BusinessSystemManager)ContextUtils.getBean("businessSystemManager");
		List<Long> subSystemIds=businessSystemManager.getSystemIdsByParentCode(ContextUtils.getSystemCode());
		if(ContextUtils.getCompanyId()==null){
			ThreadParameters parameters=new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
		}
		Set<Role> roles = ApiFactory.getAcsService().getRolesByUser(loginName);
		for (Role role : roles) {
			if(role.getName().equals(roleName)){
				boolean result =  validateRole(role,subSystemIds);
				if(result){
					return result;
				}else{
					continue;
				}
			}
		}
		return false;
	}
	
	private static boolean validateRole(Role role,List<Long> subSystemIds){
		BusinessSystem system = role.getBusinessSystem();
		if(system!=null){
			if(subSystemIds.contains(system.getId())){//但前系统是子系统，且角色所在的系统包含在子系统集合中
				return true;
			}else{
				if(subSystemIds.size()<=0){//当前系统不是子系统
					if(ContextUtils.getSystemId().equals(system.getId())){
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * 判断用户是不是在工作组里，如果在返回ture；否则，返回false。
	 * @param companyId 
	 * @param loginName 
	 * @param departmentName
	 * @return 用户在工作组里，返回ture；否则，返回false。
	 */
	public static boolean userInWorkGroup(Long companyId,String loginName,String workGroupName){
		if(ContextUtils.getCompanyId()==null){
			ThreadParameters parameters=new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
		}
		List<com.norteksoft.product.api.entity.Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroupsByUser(loginName);
		for (com.norteksoft.product.api.entity.Workgroup workGroup : workGroups) {
			if(workGroup.getName().equals(workGroupName)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析用户是否满足条件 ，判断条件的值有3中来源，分别为组织结构中、表单字段中和标准值
	 * ${currentTransactorName} operator.text.et '吴荣[wurong]' 
	 *  ${currentTransactorName} operator.text.et '${documentCreatorName}'
	 * ${currentTransactorName} operator.text.et '${field[姓名[name]]}
	 */
	public static boolean parseUser(String atomicExpress,String loginName){
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			String userLoginName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return !loginName.equals(userLoginName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String userLoginName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return loginName.equals(userLoginName);
		}
		return false;
	}
	
	
	/**
	 * 解析用户是否拥有某角色
	 */
	public static boolean parseRole(String atomicExpress,String loginName){
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			String roleName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return !userHaveRole(ContextUtils.getCompanyId(), loginName, roleName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String roleName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return userHaveRole(ContextUtils.getCompanyId(), loginName, roleName);
		}
		return false;
	}
	
	/**
	 * 解析用户和部门的关系
	 */
	public static boolean parseDepartment(String atomicExpress,String loginName){
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			String departmentName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return !userInDepartment(ContextUtils.getCompanyId(), loginName, departmentName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String departmentName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return userInDepartment(ContextUtils.getCompanyId(), loginName, departmentName);
		}
		return false;
	}
	/**
	 * 解析用户和工作组的关系
	 */
	public static boolean parseWorkGroup(String atomicExpress,String loginName){
		 if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
				String workGroupName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
				return !userInWorkGroup(ContextUtils.getCompanyId(), loginName, workGroupName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String workGroupName = StringUtils.substringBetween(atomicExpress, "'", "'").trim();
			return userInWorkGroup(ContextUtils.getCompanyId(), loginName, workGroupName);
		}
		return false;
	}
	
	public static Set<String> getUserExceptLoginName(String loginName){
		return ApiFactory.getAcsService().getLoginNamesExclude(loginName);
	} 
	
	public static Set<String> getUsersExceptRoleName(Long systemId,String roleName){
		Set<String> userNames = new HashSet<String>();
		for(User user: ApiFactory.getAcsService().getUsersWithoutRoleName(systemId,roleName)){
			userNames.add(user.getLoginName());
		}
		return userNames;
	}
	
	public static Set<String> getUsersExceptRoleName(String roleName,Long systemId,Long companyId){
		Set<String> userNames = new HashSet<String>();
		for(User user: ApiFactory.getAcsService().getUsersWithoutRoleName(systemId,roleName)){
			userNames.add(user.getLoginName());
		}
		return userNames;
	}
	
	public static Set<String> getUsersByRoleName( Long systemId,String roleName){
		Set<String> userNames = new HashSet<String>();
		for(User user: ApiFactory.getAcsService().getUsersByRoleName(systemId,roleName)){
			userNames.add(user.getLoginName());
		}
		return userNames;
	}
	
	public static Set<String> getUsersByRoleName( String roleName,Long systemId,Long companyId){
		Set<String> userNames = new HashSet<String>();
		for(User user: ApiFactory.getAcsService().getUsersByRoleName(systemId,roleName)){
			userNames.add(user.getLoginName());
		}
		return userNames;
	}
	
	public static List<Department> getDepartmentsByUser(String loginName){
		return ApiFactory.getAcsService().getDepartments(loginName);
	}
	
	public static Department getDepartmentByName(String name){
		return ApiFactory.getAcsService().getDepartmentByName(name);
	}
	
	public static Set<String> getUsersNotInDepartment(Set<Department> departmentSet){
		Set<Department> allDepartment = new HashSet<Department>(ApiFactory.getAcsService().getDepartments());
		allDepartment.removeAll(departmentSet);
		return getUsersByDepartment(allDepartment);
	}
	
	public static Set<String> getUsersByDepartment(Set<Department> departmentSet){
		Set<String> userNames = new HashSet<String>();
		for(Department department:departmentSet){
			userNames.addAll(getUserLoginName(ApiFactory.getAcsService().getUsersByDepartmentId(department.getId())));
		}
		return userNames;
	}
	public static Set<String> getUserLoginName(Collection<User> users){
		Set<String> userNames = new HashSet<String>();
		for(User user : users){
			userNames.add(user.getLoginName());
		}
		return userNames;
	}
	public static Set<String> getUsersNotInWorkGroup(Set<Workgroup> workgroupSet){
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		Set<Workgroup> allWorkGroup = new HashSet<Workgroup>(userManager.getWorkgroups());
		allWorkGroup.removeAll(workgroupSet);
		return getUsersByWorkGroup(allWorkGroup);
	}
	
	
	public static Set<String> getUsersByWorkGroup(Set<Workgroup> workgroupSet){
		Set<String> userNames = new HashSet<String>();
		for(Workgroup workGroup:workgroupSet){
			userNames.addAll(getUserLoginName(ApiFactory.getAcsService().getUsersByWorkgroupId(workGroup.getId())));
		}
		return userNames;
	}
	
	public static Workgroup getWorkGroupByName(String workGroupName){
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		return userManager.getWorkgroupByName(workGroupName);
	}
	
	public static List<Workgroup> getWorkGroupsByUser(String loginName){
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		return userManager.getWorkgroupsByUser(loginName);
	}
	
	
}
