package com.norteksoft.acs.web.organization;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.utils.Ldaper;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserInfoManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.TreeUtils;
import com.norteksoft.tags.tree.DepartmentDisplayType;

/**
 * author 李洪超
 * version 创建时间：2009-3-11 上午09:51:10
 *  部门管理Action
 *  
 *  2010-07-27 xiaoj
 */
@SuppressWarnings("deprecation")
@Namespace("/organization")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "department!list?departmentId=${departmentId}", type="redirectAction") })
public class DepartmentAction extends CRUDActionSupport<Department>{
	
	private static final long serialVersionUID = 4814560124772644966L;
	private DepartmentManager departmentManager;
	private CompanyManager companyManager;
	private Page<Department> page = new Page<Department>(20, true);
	private Page<User> userPage = new Page<User>(0, true);
	private Page<UserInfo> pageUserInfo = new Page<UserInfo>(20, true);
	private Department department;
	private Long id;
	private Long parentId;
	private Long companyId;
	private List<Long> checkedUserIds;
	private String departmentName;
	private String departmentCode;
	private Long departmentId;
	private List<Role> allRoles;
	private List<Long> roleIds;
	private List<Long> checkedRoleIds;
	private Integer isAddOrRomove;
	private List<Long> departmentIds;
	private User user;
	private List<BusinessSystem> systems;
	private BusinessSystemManager businessSystemManager;
	private UserManager userManager;
	private UserInfoManager userInfoManager;
	private String message = "";
	private String currentId;
	private List<Long> userIds;
	private String ids;
	private String treeSelectedNode;
	/**
	 * 分页查询所有不在任何部门的用户
	 */
	@Override
	@Action("department")
	public String list() throws Exception {
		if(userPage.getPageSize() <= 1){
			return SUCCESS; 
		}else{
			if(departmentId != null){
				userInfoManager.queryUsersByDepartment(userPage, departmentId);
			}else{
				userInfoManager.getAllDepartmentUsers(userPage);
			}
			renderHtml(PageUtils.pageToJson(userPage));
			ApiFactory.getBussinessLogService().log("部门管理", 
					"查看部门列表",ContextUtils.getSystemId("acs"));
			return null;
		}
	}
	
	/**
	 * 修改部门
	 */
	@Override
	public String input() throws Exception {
		if(id == null){
			ApiFactory.getBussinessLogService().log("部门管理", 
					"新建部门",ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log("部门管理", 
					"修改部门",ContextUtils.getSystemId("acs"));
		}
		return INPUT;
	}
	
	/**
	 * 保存部门信息
	 */
	public String saveDepartment() throws Exception{
		boolean logSign=true;//该字段只是为了标识日志信息：true表示新建部门、false表示修改部门
		if(id==null){
			Company company = companyManager.getCompany(ContextUtils.getCompanyId());
			department.setCompany(company);
			departmentManager.saveDept(department);
			logSign=true;
		}else{
			departmentManager.saveDept(department);
			if(Ldaper.isStartedAboutLdap()){
				message = Ldaper.addGroup(department,false);
			}
			logSign=false;
		}
		addActionMessage(getText("common.saved"));
		
		if(logSign){
			ApiFactory.getBussinessLogService().log("部门管理", 
					"新建部门："+department.getName(),ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log("部门管理", 
					"修改部门："+department.getName(),ContextUtils.getSystemId("acs"));
		}
		return null;
	}
	
	/**
	 * 删除部门
	 */
	@Override
	public String delete() throws Exception {
		String logSign="";//该字段只是为了标识日志信息：部门名称
		Department dept = departmentManager.getDepartment(departmentId);
		logSign=dept.getName();
		Department parentDept = dept.getParent();
		List<User> users=userManager.getUsersByDeptId(departmentId);
		departmentManager.deleteDepart(dept,users);
		if(parentDept != null)
			departmentId = dept.getParent().getId();
		else
			departmentId=null;
		addActionMessage(getText("common.deleted"));
		ApiFactory.getBussinessLogService().log("部门管理", 
				"删除部门:"+logSign,ContextUtils.getSystemId("acs"));
	    return RELOAD;
	}
	
	public void prepareSaveDepartment() throws Exception {
		prepareModel();
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			department = departmentManager.getDepartment(id);
		} else {
			department = new Department();
		}
		if(parentId != null){
			department.setParent(departmentManager.getDepartment(parentId));
		}
	}

    /**
     * 部门添加人员
     */
    public String addDepartmentToUsers()throws Exception{
    	return "user-tree";
    }
    
    /**
     * 部门树
     * @return
     * @throws Exception
     */
    public String tree() throws Exception{
    	return "tree";
    }
	
    /**
     * 人员树
     * @return
     * @throws Exception
     */
	public String getCompanyNodes() throws Exception{
		this.renderText(TreeUtils.getCreateManDepartmentTree(ContextUtils.getCompanyId(),currentId,false,DepartmentDisplayType.NAME,false));
		return null;
		
	}
	
	public String getUserNodes(Long deptId) throws Exception{
		StringBuilder nodes = new StringBuilder();
		List<User> users = userManager.getUsersByDeptId(deptId);
		
		List<Department> subDepts = departmentManager.getSubDeptments(deptId);
		for(Department subDept : subDepts){
			nodes.append(JsTreeUtils.generateJsTreeNode("DEPARTMENT," + subDept.getId(), "closed", subDept.getName(), ""));
			nodes.append(",");
		}
		for(User user : users){
			nodes.append(JsTreeUtils.generateJsTreeNode("USER," + user.getId(), "", user.getName(), "")).append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	public String getNoDepartmentUserNodes(Long companyId){
		StringBuilder nodes = new StringBuilder();
		List<com.norteksoft.product.api.entity.User> users =ApiFactory.getAcsService().getUsersNotInDepartment(companyId);
		for(com.norteksoft.product.api.entity.User user : users){
			nodes.append(JsTreeUtils.generateJsTreeNode("USER," + user.getId(), "", user.getName(), "")).append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 * 保存部门添加用户
	 * @return
	 * @throws Exception
	 */
    public String departmentAddUser()throws Exception{
    	departmentManager.departmentToUser(departmentId, userIds, 0);
    	
    	String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
    	department = departmentManager.getDepartment(departmentId);
		for(Long userId:userIds){
			if(userId.equals(0L)){//全公司时
				logSign+="公司所有人";
				break;
			}else{
				user=userManager.getUserById(Long.valueOf(userId));
				if(StringUtils.isNotEmpty(logSign)){
					logSign+=",";
				}
				logSign+=user.getName();
			}
		}
    	ApiFactory.getBussinessLogService().log("部门管理", 
    			department.getName()+"添加兼职人员:"+logSign,ContextUtils.getSystemId("acs"));
    	return RELOAD;
    }
    
    /**
     * 保存部门移除用户
     * @return
     * @throws Exception
     */
    public String removeDepartmentToUsers() throws Exception{
    	departmentManager.departmentToUser(departmentId, userIds, 1);
    	
    	String logSign="";//该字段只是为了标识日志信息：用户1，用户2，...
    	department = departmentManager.getDepartment(departmentId);
		for(Long userId:userIds){
			user=userManager.getUserById(Long.valueOf(userId));
			if(StringUtils.isNotEmpty(logSign)){
				logSign+=",";
			}
			logSign+=user.getName();
		}
    	ApiFactory.getBussinessLogService().log("部门管理", 
    			department.getName()+"移除兼职人员:"+logSign,ContextUtils.getSystemId("acs"));
    	return RELOAD;
    }
    
    @Autowired
    public void setUserInfoManager(UserInfoManager userInfoManager) {
		this.userInfoManager = userInfoManager;
	}
    
	
	/**
	 * 验证部门名称唯一性
	 */
	public String checkDeptName() throws Exception{
		boolean department =false;
		if(id!=null){
			department = departmentManager.checkDeptName(departmentName,id);
		}else{
			department = departmentManager.checkDeptName(departmentName);
		}
		if(department==false){
			this.renderText("true");
		}else{
			this.renderText(departmentName);
		}
		return null;
	}
	
	/**
	 * 验证部门编号唯一性
	 * liudongxia
	 */
	public String checkDeptCode() throws Exception{
		boolean department =false;
		if(id!=null){
			department = departmentManager.checkDeptCode(departmentCode,id);
		}else{
			department = departmentManager.checkDeptCode(departmentCode);
		}
		if(department==false){
			this.renderText("true");
		}else{
			this.renderText(departmentCode);
		}
		return null;
	}
	
	/**
	 * 按条件查询
	 * @return
	 */
	public void prepareSearch() throws Exception {
		prepareModel();
	}
	
	public String search() throws Exception {
		page = departmentManager.getSearchDepartment(page, department, false);
		return SUCCESS;

	}
    
    
    
    
    
	public void prepareSaveUser() throws Exception {
		prepareModel();
	}
	

	public String saveUser() throws Exception{
		Department department = departmentManager.getDepartment(parentId);
		this.department.setParent(department);
		departmentManager.saveDept(this.department);
		addActionMessage(getText("common.saved"));
		 return RELOAD;
	}
	    
	/**
	 * 新建部门
	 */
	public String inputDepartment() throws Exception{
		companyId = companyManager.getCompanyId();
		return "input";
	}
	
	public void prepareListRoles() throws Exception {
		department = departmentManager.getDepartment(departmentId);
	}
	
	/**
	 * 部门添加角色 
	 */
	public String listRoles()throws Exception{
		isAddOrRomove = 0;
		systems = businessSystemManager.getAllBusiness();
		checkedRoleIds = departmentManager.getCheckedRoleIdsByDepartment(departmentId);
		return "role";
	}
	
	/**
	 * 部门移除角色
	 */
	public String removeRoles() throws Exception{
		isAddOrRomove = 1;
		systems = businessSystemManager.getAllBusiness();
		checkedRoleIds = departmentManager.getCheckedRoleIdsByDepartment(departmentId);
		return "role";
	}
	
	/**
	 * 给部门分配角色
	 */
	public String addRolesToDepartment(){
		departmentManager.addRolesToDepartments(departmentId, roleIds, isAddOrRomove);
		if(isAddOrRomove == 0){
			addActionMessage(getText("department.addRolesSuccess"));
		}else if(isAddOrRomove == 1){
			addActionMessage(getText("department.removeRolesSuccess"));
		}
		ApiFactory.getBussinessLogService().log("部门管理", 
				"部门添加或移除角色",ContextUtils.getSystemId("acs"));
		return RELOAD;
	}
	
	/**
	 * 保存
	 */
	@Override
	public String save() throws Exception {
		departmentManager.saveDept(department);
		addActionMessage(getText("common.saved"));
		ApiFactory.getBussinessLogService().log("部门管理", 
				"保存部门信息",ContextUtils.getSystemId("acs"));
		return RELOAD;
	}
	
	public Department getModel() {
		return department;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}
	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}
	public Page<UserInfo> getPageUserInfo() {
		return pageUserInfo;
	}
	public void setPageUserInfo(Page<UserInfo> pageUserInfo) {
		this.pageUserInfo = pageUserInfo;
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	public List<Long> getCheckedUserIds() {
		return checkedUserIds;
	}

	public void setCheckedUserIds(List<Long> checkedUserIds) {
		this.checkedUserIds = checkedUserIds;
	}
	
	public Page<User> getUserPage() {
		return userPage;
	}
	public void setUserPage(Page<User> userPage) {
		this.userPage = userPage;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	
	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public Page<Department> getPage() {
		return page;
	}

	public void setPage(Page<Department> page) {
		this.page = page;
	}

	public String temp()throws Exception{
		return SUCCESS;
	}
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	
	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public List<Role> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(List<Role> allRoles) {
		this.allRoles = allRoles;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	public List<Long> getCheckedRoleIds() {
		return checkedRoleIds;
	}

	public void setCheckedRoleIds(List<Long> checkedRoleIds) {
		this.checkedRoleIds = checkedRoleIds;
	}
	
	public String getDepartmentByCompany(){
		if(companyId != null){
			page = departmentManager.queryDepartmentByCompany(page, companyId);
		}
		return SUCCESS;
	}
	
	public List<BusinessSystem> getSystems() {
		return systems;
	}
	
	public void setSystems(List<BusinessSystem> systems) {
		this.systems = systems;
	}
	
	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setDepartmentIds(List<Long> departmentIds) {
		this.departmentIds = departmentIds;
	}

	public List<Long> getDepartmentIds() {
		return departmentIds;
	}
	
	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getTreeSelectedNode() {
		return treeSelectedNode;
	}

	public void setTreeSelectedNode(String treeSelectedNode) {
		this.treeSelectedNode = treeSelectedNode;
	}
}
