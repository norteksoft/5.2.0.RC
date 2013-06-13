package com.norteksoft.product.api;

import java.util.List;
import java.util.Set;

import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.product.api.entity.Workgroup;

public interface AcsService {
	
	@Deprecated
	public Long getOnlineUserCount(Long companyId);
	
	/**
	 * 查询公司在线用户数量
	 * @return
	 */
	public Long getOnlineUserCount();
	
	/**
	 * 查询在线用户人员ID
	 * @return
	 */
	public List<Long> getOnlineUserIds();
	
	@Deprecated
	public List<Department> getDepartmentList(Long companyId);
	
	/**
	 * 查询公司所有的部门
	 * @return List<Department>
	 */
	public List<Department> getDepartments();

	@Deprecated
	public List<Workgroup> getWorkGroupList(Long companyId);
	
	/**
	 * 查询公司所有的工作组
	 * @return List<WorkGroup>
	 */
	public List<Workgroup> getWorkgroups();

	@Deprecated
	public List<com.norteksoft.acs.entity.organization.User> getUserListByDepartmentId(Long departmentId);
	
	/**
	 * 根据部门ID查询该部门的人员
	 * @param departmentId 部门Id
	 * @return List<User>
	 */
	public List<User> getUsersByDepartmentId(Long departmentId);
	
	/**
	 * 根据部门名称得到部门下用户的登录名
	 * @param loginName
	 * @return
	 */	
	public List<String> getUserLoginNamesByDepartmentName(String departmentName);
	
	/**
	 * 根据工作组ID查询工作组的人员
	 * @param workgroupId 工作组Id
	 * @return List<User>
	 */
	public List<User> getUsersByWorkgroupId(Long workgroupId);

	/**
	 * 根据父部门id查询该父部门下所有子部门
	 * @param paternDepartmentId 父部门Id
	 * @return List<Department>
	 */
	public List<Department> getSubDepartmentList(Long paternDepartmentId);

	/**
	 * 根据用户Id得到用户实体
	 * @return User
	 */
	public User getUserById(Long id);

	/**
	 * 查询用户委托的角色。 
	 * @param trusteeId 受托人ID
	 * @param trustorId 委托人ID
	 * @return
	 */
	public Set<Role> getTrustedRolesByUserId(Long trusteeId, Long trustorId);
	
	/**
	 * 根据用户获取用户的角色字符串形式（不含委托）
	 */
	@Deprecated
	public String getRoleCodesFromUser(com.norteksoft.acs.entity.organization.User user);
	
	/**
	 * 根据用户查询用户的角色（不含委托）
	 * @param userId
	 * @return Set<Role>
	 */
	public String getRolesExcludeTrustedRole(User user);
	
	@Deprecated
	public String getRolesExcludeTrustedRole(com.norteksoft.acs.entity.organization.User user);
	
	/**
	 * 查询用户所有的角色
	 * @param userId
	 * @return
	 */
	public Set<Role> getRolesByUser(Long userId);
	
	/**
	 * 查询用户所有的角色
	 * @param userId
	 * @return
	 */
	public Set<Role> getRolesByUser(User user);
	
	@Deprecated
	public Set<Role> getRolesByUser(com.norteksoft.acs.entity.organization.User user);
	
	@Deprecated
	public List<User> getUsersNotInDepartment(Long companyId);
	
	/**
	 * 获取不属于任何部门的用户
	 * @return List<User>
	 */
	public List<User> getUsersWithoutDepartment();
	
	@Deprecated
	public void assignRolesToSomeone(Long someoneId,String[] roleIds,Long companyId,Long sourceUserId);
	
	/**
	 * 将角色授权给别人，自己还保留该角色
	 * @param someoneId 受权人
	 * @param roleIds 角色id数组
	 * @param companyId
	 * @param sourceUserId //授权人
	 */
	public void assignTrustedRole(Long trustorId, String[]roleIds, Long trusteeId);
	
	@Deprecated
	public void deleteRoleUsers(Long userId,String[] rIds,Long companyId,Long sourceId)	;
	
	/**
	 * 删除委托人委托出去的角色
	 * @param userId 受委托人的id
	 * @param rIds 角色id数组
	 * @param companyId 公司id
	 * @param sourceId 委托人id
	 */
	public void deleteTrustedRole(Long trustorId, String[]roleIds,Long trusteeId);
	
	/**
	 * 根据roleId得到role
	 * @param sourceId
	 * @param userId
	 * @param companyId
	 */
	public Role getRoleById(Long roleId);
	
	@Deprecated
	public void deleteAssignedAuthority(Long sourceId,Long userId,Long companyId);
	
	/**
	 * 删除由别人分配的权限
	 * @param sourceId
	 * @param userId
	 * @param companyId
	 */
	public void deleteAllTrustedRole(Long trustorId, Long trusteeId);

	/**
	 * 通过工作组ID获取工作组
	 * @param workgroupId
	 * @return Workgroup
	 */
	public Workgroup getWorkgroupById(Long workgroupId);
	
	@Deprecated
	public Workgroup getWorkGroupByName(String name, Long companyId);
	
	/**
	 * 通过工作组名称获取工作组
	 * @param workgroupName
	 * @return Workgroup
	 */
	public Workgroup getWorkgroupByName(String name);
	
	/**
	 * 通过工作组编号获取工作组
	 * @param workgroupCode
	 * @return Workgroup
	 */
	public Workgroup getWorkgroupByCode(String code);
	
	/**
	 * 通过部门ID获取部门实体
	 * @param departmentId
	 * @return Department
	 */
	public Department getDepartmentById(Long departmentId);
	
	@Deprecated
	public Department getDepartmentByName(String name, Long companyId);
	
	/**
	 * 通过部门名称获取部门实体
	 * @param name
	 * @return Department
	 */
	public Department getDepartmentByName(String name);
	
	/**
	 * 通过部门编号获取部门实体
	 * @param code
	 * @return Department
	 */
	public Department getDepartmentByCode(String code);
	/**
	 * 查询所有人员（不包含无部门人员）
	 * @param companyId
	 * @return
	 */
	public List<User> getUsersByCompany(Long companyId);
	/**
	 * 查询所有人员（包含无部门人员）
	 * @param companyId
	 * @return
	 */
	public List<User> getAllUsersByCompany(Long companyId);
	
	@Deprecated
	public Set<User> getUsersByRoleName(Long systemId, Long companyId, String roleName);
	
	/**
	 * 通过角色名称查询拥有该角色的用户
	 * @param systemId
	 * @param roleName
	 * @return
	 */
	public Set<User> getUsersByRoleName(Long systemId, String roleName);
	
	@Deprecated
	public Set<User> getUsersExceptRoleName(Long systemId, Long companyId, String roleName);

	public Set<User> getUsersWithoutRoleName(Long systemId, String roleName);
	
	public Set<User> getUsersWithoutRoleCode(Long systemId, String roleCode);
	
	@Deprecated
	public Set<User> getUsersByRole(Long systemId, Long companyId, String roleCode);
	
	/**
	 * 通过角色编号查询所有的用户（不含委托）
	 * @param systemId
	 * @param roleCode
	 * @return
	 */
	public Set<User> getUsersByRoleCodeExceptTrustedRole(Long systemId, String roleCode);
	
	@Deprecated
	public String getRtxUrl(Long companyId);
	
	public String getRtxUrl();
	
	@Deprecated
	public Boolean isRtxInvocation(Long companyId);
	
	/**
	 * 是否启用了rtx集成
	 * */
	public Boolean isRtxEnable();
	
	@Deprecated
	public List<Department> getDepartmentsByUser(Long companyId,Long userId);
	
	/**
	 * 根据用户ID查询用户所在的部门
	 * @param userId
	 * @return
	 */
	public List<Department> getDepartmentsByUserId(Long userId);
	
	@Deprecated
	public User getUser(Long companyId, String loginName);
	
	/**
	 * 根据登录名查询用户信息
	 * @param loginName
	 * @return
	 */
	public User getUserByLoginName(String loginName);
	
	@Deprecated
	public User getUser(String email);
	
	/**
	 * 根据邮件地址查询用户信息
	 * @param email
	 * @return
	 */
	public User getUserByEmail(String email);
	
	/**
	 * 根据用户姓名查询用户
	 * @param userName
	 * @return
	 */
	public List<User> getUsersByName(String userName);
	
	@Deprecated
	public Set<String> getUserExceptLoginName(Long companyId, String loginName);
	
	/**
	 * 查询出该登录名外的其他用户的登录名
	 * @param loginName
	 * @return
	 */
	public Set<String> getLoginNamesExclude(String loginName);
	
	/**
	 * 根据用户登录名查询用户所在的部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Deprecated
	public List<Department> getDepartmentsByUser(Long companyId, String loginName);
	
	public List<Department> getDepartments(String loginName);
	
	@Deprecated
	public Set<Role> getRolesByUser(Long companyId, String loginName);
	
	/**
	 * 根据公司ID和用户的登录名查询该用户所具有的角色的字符串表示
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public Set<Role> getRolesByUser(String loginName);
	
	@Deprecated
	public List<Workgroup> getWorkGroupsByUser(Long companyId, String loginName);
	
	/**
	 * 根据用户登录名查询该用户所在的工作组
	 * @param loginName
	 * @return
	 */
	public List<Workgroup> getWorkgroupsByUser(String loginName);
	
	/**
	 * 根据公司ID和用户登录名查询该用户所在的工作组
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Deprecated
	public List<Workgroup> getWorkGroupsByUserLike(Long companyId, String name);
	
	/**
	 * 根据登录名获得用户列表
	 * 如：flex中查询流转历史需要使用
	 */
	public List<User> getUsersByLoginNames(Long companyId, List<String> loginNames);
	
	public List<User> getTacheUsersByLoginNames(Long companyId, List<String> loginNames);
	
	public List<User> getUsersByLoginNames(List<String> loginNames);
	
	@Deprecated
	public List<Role> getRolesListByUserExceptDelegateMain(Long userId);
	
	
	@Deprecated
	public List<Role> getRolesListByUserExceptDelegateMain(com.norteksoft.acs.entity.organization.User user);
	
	public List<Role> getRolesListByUserExceptDelegateMain(User user);
	
	@Deprecated
	public List<Role> getRolesListByUser(com.norteksoft.acs.entity.organization.User user);
	
	public List<Role> getRolesListByUser(User user);
	
	/**
	 * 根据用户查询角色(不含委托)
	 * @param user
	 * @return
	 */
	public List<Role> getRolesExcludeTrustedRole(Long userId);
	
	@Deprecated
	public List<Department> getSuperiorDepartmentsByUser(Long companyId, String loginName);
	
	/**
	 * 根据用户登录名查询用户所在的部门的上级部门
	 * @param loginName
	 * @return
	 */
	public List<Department> getParentDepartmentsByUser(String loginName);
	
	@Deprecated
	public List<Department> getUpstageDepartmentsByUser(Long companyId, String loginName);
	
	/**
	 * 获得用户的顶级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Department> getTopDepartmentsByUser(String loginName);
	
	/**
	 * 获得用户的顶级部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Deprecated
	public List<Department> getUpstageDepartmentsByUserLike(Long companyId, String loginName);

	@Deprecated
	public Department getFirstDegreeDepartment(Department department);
	
	/**
	 * 返回该部门的一级部门
	 * @param department
	 * @return
	 */
	public Department getTopDepartment(Department department);
	/**
	 * 根据系统编码获得系统
	 * @param code
	 * @return
	 */
	public BusinessSystem getSystemByCode(String code);
	/**
	 * 根据系统id获得系统
	 * @param code
	 * @return
	 */
	public BusinessSystem getSystemById(Long id);
	/**
	 * 保存用户
	 * @param user
	 */
	@Deprecated
	public void saveUser(com.norteksoft.acs.entity.organization.User user,UserInfo userInfo);
	/**
	 * 保存用户
	 * @param user
	 */
	public void saveUser(User user);
	
	/**
	 * 删除用户
	 * @param userId
	 */
	public void deleteUser(Long userId);
	/**
	 * 保存部门
	 * @param department
	 */
	public void saveDepartment(Department department,Long companyId);
	
	@Deprecated
	public void saveDepartment(com.norteksoft.acs.entity.organization.Department department,Long companyId);
	/**
	 * 保存部门
	 * @param department
	 */
	public void saveDepartment(Department department);
	
	@Deprecated
	public void saveDepartment(com.norteksoft.acs.entity.organization.Department department);
	/**
	 * 删除部门
	 * @param departmentId
	 */
	public void deleteDepartment(Long departmentId);
	/**
	 * 保存用户部门关系
	 * @param userIds 用户User的id的集合
	 * @param department 部门
	 */
	public void saveDepartmentUser(List<Long> userIds,Department department);
	
	@Deprecated
	public void saveDepartmentUser(List<Long> userIds,com.norteksoft.acs.entity.organization.Department department);
	/**
	 * 查询当前用户的角色（不含委托）
	 * @return
	 */
	public String getCurrentUserRolesExcludeTrustedRole();
	/**
	 * 查询指定用户的角色（不含委托）
	 * @return
	 */
	public String getUserRolesExcludeTrustedRole(Long userId);
	/**
	 * 获得该公司的系统管理员（默认的系统管理员systemAdmin）
	 * @param companyId
	 * @return
	 */
	public String getSystemAdminLoginName();
	/**
	 * 查询公司中所有人员登录名（不包含无部门人员）
	 * @param companyId
	 * @return
	 */
	public List<String> getLoginNamesByCompany(Long companyId);
	/**
	 * 查询工作组所有人员
	 * @param companyId
	 * @return
	 */
	public List<String> getLoginNamesByWorkgroup(Long companyId);
	/**
	 * 获得父部门
	 * @param departmentId
	 * @return
	 */
	public Department getParentDepartment(Long departmentId);
}
