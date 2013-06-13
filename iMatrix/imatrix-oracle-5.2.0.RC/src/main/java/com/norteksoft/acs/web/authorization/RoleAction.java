package com.norteksoft.acs.web.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.acs.service.syssetting.SecuritySetManager;
import com.norteksoft.acs.web.eunms.AddOrRomoveState;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;

@SuppressWarnings("deprecation")
@ParentPackage("default")
@Results( { 
	@Result(name = CRUDActionSupport.RELOAD, location = "role?businessSystemId=${businessSystemId}", type="redirectAction") 
	,@Result(name="RELOAD_CUSTOM_ROLE", location="custom-role?businessSystemId=${businessSystemId}", type="redirectAction")
	,@Result(name="RELOAD_STANDARD_ROLE", location="standard-role!authoritys?businessSystemId=${businessSystemId}&roleId=${roleId}", type="redirectAction")
})
public class RoleAction extends CRUDActionSupport<Role> {
	private static final long serialVersionUID = -5473169092158238538L;
	private static String ACS_SYSTEM_ADMIN="acsSystemAdmin";//系统管理员角色编码
	private static String ACS_SECURITY_ADMIN="acsSecurityAdmin";//安全管理员角色编码
	private static String ACS_AUDIT_ADMIN="acsAuditAdmin";//审计管理员角色编码
	
	private Page<Role> page = new Page<Role>(0, true);
	private Page<FunctionGroup> functionpage = new Page<FunctionGroup>(20, true);
	private Page<Workgroup> workGroupPage = new Page<Workgroup>(20, true);
	private Page<Department> departmentPage = new Page<Department>(20, true);
	private RoleManager roleManager;
	private BusinessSystemManager businessSystemManager;
	private SecuritySetManager securitySetManager;
	private List<Role> roles;
	private Role entity;
	private Long id;
	private Long paternId;
	private Long roleId;
	private Long businessSystemId;
	private DepartmentManager departmentManager;
	private List<Long> userIds;
	private List<User> allUsers;
	private List<Long> departmentsIds;
	private List<Long> functionIds;
	private List<Long> checkedFunctionIds;
	private List<Long> checkedWorkGroupIds;
	private List<Long> workGroupIds;
	private Integer isAddOrRomove;
	private String departmentTree;
	private String usersTree;
	private String currentId;
	private Long roleGroupId;
	private String systemTree;
	private String workgroupTree;
	private CompanyManager companyManager;
	private WorkGroupManager workGroupManager;
	private String queryType;
	private String queryName;
	private String queryTitle;
	private List<BusinessSystem> systems;
	private List<List<Role>> allRoles;
	private Map<User, List<List<Role>>> userRoles;
	private UserManager userManager;
	private String isHave;
	private List<Long> ids;
	private List<Long> roleIds;
	private String allInfos;
	private Boolean isAdminRole=false;//是否是管理员角色
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}

	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	
	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@Required
	public void setSecuritySetManager(SecuritySetManager securitySetManager) {
		this.securitySetManager = securitySetManager;
	}

    public void prepareListUsers() throws Exception {
    	entity = roleManager.getRole(roleId);
	}

  
	public void prepareRemoveUsers() throws Exception {
    	entity = roleManager.getRole(roleId);
    	isAdminRole=hasAdminRole(entity);
	}
	
    /**
     * 给角色添加用户列表
     */
	public String listUsers() throws Exception{
		isAddOrRomove = AddOrRomoveState.ADD.code;
		//generateJsTree();
		return "user";
	}
	private boolean hasAdminRole(Role role){
		if(ACS_SYSTEM_ADMIN.equals(role.getCode())||ACS_AUDIT_ADMIN.equals(role.getCode())||ACS_SECURITY_ADMIN.equals(role.getCode())){
			return true;
		}
		return false;
	}
	
	public String loadWorkgroupTree(){
		StringBuilder tree = new StringBuilder("[ ");
		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
		tree.append(JsTreeUtils.generateJsTreeNode("", "open", company.getName(), getWorkGroupNodes(company.getId())));
		tree.append(" ]") ;
		renderText(tree.toString());
		return null;
	}
	
	private String getWorkGroupNodes(Long companyId){
		List<Workgroup> workGroups = workGroupManager.queryWorkGroupByCompany(ContextUtils.getCompanyId());
		List<Long> wgIds = roleManager.getWorkGroupIds(roleId);
		StringBuilder nodes = new StringBuilder();
		for(Workgroup wg: workGroups){
			if(wg.isDeleted() || wgIds.contains(wg.getId())) continue;
			nodes.append(JsTreeUtils.generateJsTreeNode("USERSBYWORKGROUP,"+wg.getId().toString(), "", wg.getName()));
			nodes.append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}

	/**
	 * 给角色移除用户列表
	 */
	public String removeUsers() throws Exception{
		//isAddOrRomove = 1;
		//return "user";
		Role role = roleManager.getRole(roleId);
		businessSystemId = role.getBusinessSystem().getId();
		roleManager.removeUDWFromRoel(roleId, userIds, departmentsIds, workGroupIds);
		return "RELOAD_STANDARD_ROLE";
	}
	
	/**
	 *  角色添加用户时的树
	 */
	public String getCompanyNodes() throws Exception{
		StringBuilder tree = new StringBuilder("[ ");
		if("INITIALIZED".equals(currentId)){
			//公司里的部门节点
			StringBuilder subNodes = new StringBuilder();
			List<Department> departments = departmentManager.getAllDepartment();
			for(Department d : departments){
				String nodeString = getDdeptNodes(d);
				if(nodeString.length() > 0)
					subNodes.append(nodeString).append(",");
			}
			subNodes.append(generateJsTreeNode("NODEPARTMENTUS," + ContextUtils.getCompanyId(), 
					"closed", getText("user.noDepartment"), ""));
			if(subNodes.lastIndexOf(",") != -1 && subNodes.lastIndexOf(",") == subNodes.length()-1){
				subNodes.replace(subNodes.length()-1, subNodes.length(), "");
			}
			//公司节点
			tree.append(generateJsTreeNode("", "open", ContextUtils.getCompanyName(), subNodes.toString()));
		}else if(currentId.startsWith("DEPARTMENT")){
			tree.append(getUserNodes(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length()))));
		}else if(currentId.startsWith("NODEPARTMENTUS")){
			tree.append(getNoDepartmentUserNodes(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length()))));
		}
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	public String removeFromRole(){
		roleManager.removeUDWFromRoel(roleId, userIds, departmentsIds, workGroupIds);
		return "RELOAD_STANDARD_ROLE";
	}
	
	/**
	 * 角色添加用户时的部门节点 
	 */
	private String getDdeptNodes(Department dept){
		StringBuilder nodes = new StringBuilder();
		if(dept.getParent() == null){
			//部门树节点
			nodes.append(generateJsTreeNode("DEPARTMENT," + dept.getId(), "closed", dept.getName(), ""));
		}
		return nodes.toString();
	}
	
	/**
	 * 角色添加用户时的用户节点 
	 */
	public String getUserNodes(Long deptId) throws Exception{
		StringBuilder nodes = new StringBuilder();
		
		List<User> users = userManager.getUsersByDeptId(deptId);
		
		List<Department> subDepts = departmentManager.getSubDeptments(deptId);
		for(Department subDept : subDepts){
			nodes.append(generateJsTreeNode("DEPARTMENT," + subDept.getId(), "closed", subDept.getName(), ""));
			nodes.append(",");
		}
		List<Long> checkedUsers = roleManager.getCheckedUserByRole(roleId);
		if(isAddOrRomove == 0){
			for(User user : users){
				if(checkedUsers.contains(user.getId())) continue;
				nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getName(), "")).append(",");
			}
		}else if(isAddOrRomove == 1){
			for(User user : users){
				if(checkedUsers.contains(user.getId()))
					nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getName(), "")).append(",");
			}
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 * 没有部门的用户的树节点
	 * @param companyId
	 * @return
	 */
	public String getNoDepartmentUserNodes(Long companyId){
		StringBuilder nodes = new StringBuilder();
		List<com.norteksoft.product.api.entity.User> users = ApiFactory.getAcsService().getUsersNotInDepartment(companyId);
		List<Long> checkedUsers = roleManager.getCheckedUserByRole(roleId);
		if(isAddOrRomove == 0){
			for(com.norteksoft.product.api.entity.User user : users){
				if(checkedUsers.contains(user.getId())) continue;
				nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getLoginName(), "")).append(",");
			}
		}else if(isAddOrRomove == 1){
			for(com.norteksoft.product.api.entity.User user : users){
				if(!checkedUsers.contains(user.getId())) continue;
				nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getLoginName(), "")).append(",");
			}
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 * 给角色添加用户
	 * @return
	 * @throws Exception
	 */
	public String addUsersToRole() throws Exception{
		entity = roleManager.getRole(roleId);
		businessSystemId = entity.getBusinessSystem().getId();
		addSuccessMessage(roleManager.addUDWFromRoel(entity, 
				userIds == null ? new ArrayList<Long>() : userIds, 
				departmentsIds == null ? new ArrayList<Long>() : departmentsIds, 
				workGroupIds == null ? new ArrayList<Long>() : workGroupIds,
						allInfos==null?"":allInfos));
		
		return "RELOAD_STANDARD_ROLE";
	}
	
//	private PermissionsWebservice permissionsWebservice;
//	private static final String TEACHER_CODE = "LMS_TEACHER";
//	@Required
//	public void setPermissionsWebservice(PermissionsWebservice permissionsWebservice) {
//		this.permissionsWebservice = permissionsWebservice;
//	}
	
    public void prepareListDepartments() throws Exception {
    	entity = roleManager.getRole(roleId);
    	isAdminRole=hasAdminRole(entity);
	}
	
    /**
     * 角色可添加部门列表 
     */
	public String listDepartments() throws Exception{
		isAddOrRomove = AddOrRomoveState.ADD.code;
		return "department";
	}
	
	public String loadDepartmentTree() throws Exception{
		List<Long> checkedDepts = roleManager.getCheckedDepartmentByRole(roleId);
		StringBuilder tree = new StringBuilder("[ ");
		StringBuilder subNodes = new StringBuilder();
		List<Department> departments = departmentManager.getAllDepartment();
		for(Department d : departments){
			if(checkedDepts.contains(d.getId())) continue;
			String nodeString = getDepartmentsNodes(d, false);
			if(nodeString.length() > 0)
				subNodes.append(nodeString).append(",");
		}
		if(subNodes.lastIndexOf(",") != -1 && subNodes.lastIndexOf(",") == subNodes.length()-1){
			subNodes.replace(subNodes.length()-1, subNodes.length(), "");
		}
		tree.append(generateJsTreeNode("company", "open", ContextUtils.getCompanyName(), subNodes.toString()));
		tree.append(" ]") ;
		renderText(tree.toString());
		return null;
	}
	
	/** 
	 * 角色可移除部门列表 
	 */
	public String removeDepartments() throws Exception{
		isAddOrRomove = 1;
		StringBuilder tree = new StringBuilder("[ ");
		StringBuilder nodes = new StringBuilder();
		List<Department> departments = departmentManager.getDepartmentsInRole(roleId);
		for(Department dept : departments){
			String nodeString = getDepartmentsNodes(dept, false);
			if(nodeString.length() > 0)
				nodes.append(nodeString).append(",");
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		tree.append(generateJsTreeNode("", "open", ContextUtils.getCompanyName(), nodes.toString()));
		tree.append(" ]") ;
		departmentTree = tree.toString();
		return "department";
	}
	
	/**
	 * 根据给定的部门生成树的部门节点
	 */
	private String getDepartmentsNodes(Department dept, boolean isSubDept){
		StringBuilder nodes = new StringBuilder();
		if(dept.getParent() != null && !isSubDept) return "";
		List<Department> subDept = departmentManager.getSubDeptments(dept.getId());
		if(subDept.size() > 0){
			StringBuilder subNodes = new StringBuilder();
			//子部门树节点列表
			for(Department d : subDept){
				if(d.isDeleted()) continue;
				subNodes.append(getDepartmentsNodes(d, true));
				subNodes.append(",");
			}
			//去掉最后一个逗号
			if(subNodes.lastIndexOf(",") == subNodes.length()-1){
				subNodes.replace(subNodes.length()-1, subNodes.length(), "");
			}
			//部门树节点
			nodes.append(generateJsTreeNode(dept.getId().toString(), "closed", dept.getName(), subNodes.toString()));
		}else{
			nodes.append(generateJsTreeNode(dept.getId().toString(), "", dept.getName(), ""));
		}
		return nodes.toString();
	}
	
	/**
	 *  生成树的一个NODE
	 * @param id        NODE的id
	 * @param state     NODE的状态   open || closed || ""
	 * @param data      NODE的显示数据
	 * @param children  NODE的子NODE 
	 * @return
	 */
	protected String generateJsTreeNode(String id, String state, String data, String children){
		StringBuilder node = new StringBuilder();
		node.append("{ attributes: { id : \"").append(id).append("\" }");
		if(state != null && !"".equals(state.trim())){
			node.append(",state : \"").append(state).append("\"");
		}
		node.append(", data: \"").append(data).append("\" ");
		if(children != null && !"".equals(children.trim())){
			node.append(", children : [").append(children).append("]");
		}
		node.append("}");
		return node.toString();
	}
	
	/**
	 * 给角色添加或移除部门
	 * @return
	 * @throws Exception
	 */
	public String addDepartmentsToRole() throws Exception{
		Role role = roleManager.getRole(roleId);
		this.setBusinessSystemId(role.getBusinessSystem().getId());
		roleManager.addDepartmentsToRole(roleId, departmentsIds, isAddOrRomove);
		if(isAddOrRomove == 0){
			addActionMessage(getText("common.saved"));
		}else if(isAddOrRomove == 1){
			addActionMessage(getText("common.saved"));
		}
		return "RELOAD_STANDARD_ROLE";
	}
	
	 public String forward(Object obj){
//		Object target = null;
//		if(obj instanceof HibernateProxy){
//	        HibernateProxy proxy = (HibernateProxy)obj;
//	        target = proxy.getHibernateLazyInitializer().getImplementation();
//	    }
		return "RELOAD_STANDARD_ROLE";
	}
	 
	@Override
	public String delete() throws Exception {
		if(roleIds != null) roleManager.deleteRoles(roleIds);
		return list();
	}

	@Override
	public String list() throws Exception {
		if(page.getPageSize()>1){
			page = roleManager.getAllRoles(page, businessSystemId);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "role";
	}
	
	public String input() throws Exception {
		
		return "input";
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			entity = roleManager.getRole(id);
		} else {
			entity = new Role();
			if(businessSystemId != null){
				BusinessSystem businessSystem = businessSystemManager.getBusiness(businessSystemId);
				entity.setBusinessSystem(businessSystem);
			}
			//控制在acs中建角色时，保存公司id
			entity.setCompanyId(ContextUtils.getCompanyId());
		}
	}

	@Override
	public String save() throws Exception {
		boolean logSign=true;//该字段只是为了标识日志信息：true表示新建角色、false表示修改角色
		if(id!=null)logSign=false;
		if(entity.getId()==null){//只有在权限系统中新建角色时才需加公司id
			entity.setCompanyId(ContextUtils.getCompanyId());
		}
		if(entity.getWeight()==null){
			entity.setWeight(0);
		}
		roleManager.saveRole(entity);
		this.setBusinessSystemId(entity.getBusinessSystem().getId());
		addActionMessage(getText("common.saved"));
		
		if(logSign){
			ApiFactory.getBussinessLogService().log("角色管理", 
					"新建角色:"+entity.getName(),ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log("角色管理", 
					"修改角色:"+entity.getName(),ContextUtils.getSystemId("acs"));
		}
		return INPUT;
	}
	
	/**
	 * 跳转的添加子角色的页面
	 */
	public String inputSubRole() throws Exception {
		entity = roleManager.getRole(paternId);
		this.setBusinessSystemId(entity.getBusinessSystem().getId());
		//generateTree();
		return "subrole";
	}
	
	/*
	 * 生成系统JSON树
	 */
	@Action("role-systemTree")
	public String systemTree(){
		StringBuilder tree = new StringBuilder("[ ");
		List<BusinessSystem> businessSystems = businessSystemManager.getAllBusiness();
		for(BusinessSystem bs : businessSystems){
			tree.append(JsTreeUtils.generateJsTreeNodeNew("BUSINESSSYSTEM_"+bs.getId(), "", bs.getName(), ""));
			tree.append(",");
		}
		if(tree.lastIndexOf(",") != -1 && tree.lastIndexOf(",") == tree.length()-1){
			tree.replace(tree.length()-1, tree.length(), "");
		}
		tree.append(" ]") ;
		if(businessSystems.size() > 0){
			if(businessSystemId == null){
				businessSystemId = businessSystems.get(0).getId();
			}
			
		}
		this.renderText(tree.toString());
		return null;
		//setSystemTree(tree.toString());
	}

    public String roleToFunctionList()throws Exception{
    	isAddOrRomove=AddOrRomoveState.ADD.code;
    	return "function-list";
    }
    
    public String roleRomoveFunctionList()throws Exception{
    	isAddOrRomove=AddOrRomoveState.ROMOVE.code;
    	return "function-list";
    }
    
    public String roleAddFunction()throws Exception{
    	Role role = roleManager.getRole(roleId);
    	this.setBusinessSystemId(role.getBusinessSystem().getId());
    	roleManager.roleAddFunction(roleId, functionIds,isAddOrRomove);
    	return null;
    }
    
    /**
     * 角色添加工作组
     */
    public void prepareRoleToWorkGroupList() throws Exception {
    	entity = roleManager.getRole(roleId);
    	isAdminRole=hasAdminRole(entity);
	}
    
    public String roleToWorkGroupList()throws Exception{
    	isAddOrRomove=AddOrRomoveState.ADD.code;
    	return "work-group-list";
    }
    
    public void prepareRoleRomoveWorkGroupList() throws Exception {
    	entity = roleManager.getRole(roleId);
	}
    
    public String roleRomoveWorkGroupList()throws Exception{
    	HttpServletRequest request = ServletActionContext.getRequest();
    	Workgroup wgp = new Workgroup();
    	wgp.setCode(request.getParameter("workGroupCode"));
    	wgp.setName(request.getParameter("workGroupName"));
    	workGroupPage = roleManager.roleRomoveWorkGroupList(workGroupPage,wgp,roleId);
    	isAddOrRomove=AddOrRomoveState.ROMOVE.code;
//    	generateTree();
    	return "work-group-list";
    }
   
    public String roleAddWorkGroup()throws Exception{
    	entity = roleManager.getRole(roleId);
    	this.setBusinessSystemId(entity.getBusinessSystem().getId());
    	roleManager.roleAddWorkGroup(roleId, workGroupIds,isAddOrRomove);
    	return forward(entity);
    }
	
	public String getRolesByRoleGroup(){
		if(roleGroupId != null){
			page = roleManager.getRolesByRoleGroup(page, roleGroupId);
		}
		return SUCCESS;
	}
   
	public Role getModel() {
		return entity;
	}

	public Page<Role> getPage() {
		return page;
	}

	public void setPage(Page<Role> page) {
		this.page = page;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPaternId() {
		return paternId;
	}

	public void setPaternId(Long paternId) {
		this.paternId = paternId;
	}
	
	public Long getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(Long businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	@Required
	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public Page<Department> getDepartmentPage() {
		return departmentPage;
	}

	public void setDepartmentPage(Page<Department> departmentPage) {
		this.departmentPage = departmentPage;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(List<User> allUsers) {
		this.allUsers = allUsers;
	}

	public List<Long> getDepartmentsIds() {
		return departmentsIds;
	}

	public void setDepartmentsIds(List<Long> departmentsIds) {
		this.departmentsIds = departmentsIds;
	}
	
	public List<Long> getFunctionIds() {
		return functionIds;
	}

	public void setFunctionIds(List<Long> functionIds) {
		this.functionIds = functionIds;
	}
	
	public Page<FunctionGroup> getFunctionpage() {
		return functionpage;
	}

	public void setFunctionpage(Page<FunctionGroup> functionpage) {
		this.functionpage = functionpage;
	}
	
	public List<Long> getCheckedFunctionIds() {
		return checkedFunctionIds;
	}

	public void setCheckedFunctionIds(List<Long> checkedFunctionIds) {
		this.checkedFunctionIds = checkedFunctionIds;
	}

	public Page<Workgroup> getWorkGroupPage() {
		return workGroupPage;
	}

	public void setWorkGroupPage(Page<Workgroup> workGroupPage) {
		this.workGroupPage = workGroupPage;
	}

	public List<Long> getCheckedWorkGroupIds() {
		return checkedWorkGroupIds;
	}

	public void setCheckedWorkGroupIds(List<Long> checkedWorkGroupIds) {
		this.checkedWorkGroupIds = checkedWorkGroupIds;
	}

	public List<Long> getWorkGroupIds() {
		return workGroupIds;
	}

	public void setWorkGroupIds(List<Long> workGroupIds) {
		this.workGroupIds = workGroupIds;
	}

	public Long getRoleGroupId() {
		return roleGroupId;
	}

	public void setRoleGroupId(Long roleGroupId) {
		this.roleGroupId = roleGroupId;
	}
	
	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	public String getDepartmentTree() {
		return departmentTree;
	}

	public void setDepartmentTree(String departmentTree) {
		this.departmentTree = departmentTree;
	}

	public String getUsersTree() {
		return usersTree;
	}

	public void setUsersTree(String usersTree) {
		this.usersTree = usersTree;
	}

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}

	public String getWorkgroupTree() {
		return workgroupTree;
	}

	public void setWorkgroupTree(String workgroupTree) {
		this.workgroupTree = workgroupTree;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}

	@Required
	public void setWorkGroupManager(WorkGroupManager workGroupManager) {
		this.workGroupManager = workGroupManager;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	
	public String query(){
		if(queryType == null || "".equals(queryType)){queryTitle="用户名称"; return "query"; }
		List<Role> sysRoles =null;
		systems = businessSystemManager.getAllBusiness();
		if("ROLE_USER".equals(queryType)){
			User us=userManager.getCompanyUserByLoginName(queryName);
			if(us==null)isHave="false";
			allUsers = roleManager.queryUserByTrueName(queryName);
			userRoles = new HashMap<User, List<List<Role>>>();
			for(User user : allUsers){
				allRoles = new ArrayList<List<Role>>();
				userRoles.put(user, allRoles);
				for(BusinessSystem bs : systems){
					//sysRoles = roleManager.queryRolesByUserName(user.getId(), bs.getId());
					sysRoles=securitySetManager.getRolesByUserAndBussinessId(user.getId(),bs.getId());
					allRoles.add(sysRoles);
				}
			}
			if(us!=null)
				ApiFactory.getBussinessLogService().log("权限查询", 
						"查询"+us.getName()+"用户权限",ContextUtils.getSystemId("acs"));
		}else if("ROLE_DEPARTMENT".equals(queryType)){
			allRoles = new ArrayList<List<Role>>();
			isHave=departmentManager.checkDeptName(queryName)+"";
			if("false".equals(isHave)){
				systems.clear();
				return "query";
				};
			for(BusinessSystem bs : systems){
				sysRoles = roleManager.queryRolesByDepartmentName(queryName, bs.getId());
				allRoles.add(sysRoles);
			}
			ApiFactory.getBussinessLogService().log("权限查询", 
						"查询"+queryName+"部门权限",ContextUtils.getSystemId("acs"));
		}else if("ROLE_WORKGROUP".equals(queryType)){
			allRoles = new ArrayList<List<Role>>();
			isHave=workGroupManager.checkWorkName(queryName)+"";
			if("false".equals(isHave)){
				systems.clear();
				return "query";
				};
			for(BusinessSystem bs : systems){
				sysRoles = roleManager.queryRolesByWorkgroupName(queryName, bs.getId());
				allRoles.add(sysRoles);
			}
			ApiFactory.getBussinessLogService().log("权限查询", 
					"查询"+queryName+"工作组权限",ContextUtils.getSystemId("acs"));
		}
		return "query";
	}

	public List<BusinessSystem> getSystems() {
		return systems;
	}

	public void setSystems(List<BusinessSystem> systems) {
		this.systems = systems;
	}

	public List<List<Role>> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(List<List<Role>> allRoles) {
		this.allRoles = allRoles;
	}

	public Map<User, List<List<Role>>> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Map<User, List<List<Role>>> userRoles) {
		this.userRoles = userRoles;
	}

	public String getAllInfos() {
		return allInfos;
	}

	public void setAllInfos(String allInfos) {
		this.allInfos = allInfos;
	}

	public String getIsHave() {
		return isHave;
	}

	public void setIsHave(String isHave) {
		this.isHave = isHave;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public String getQueryTitle() {
		return queryTitle;
	}

	public void setQueryTitle(String queryTitle) {
		this.queryTitle = queryTitle;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}
	public Boolean getIsAdminRole() {
		return isAdminRole;
	}
	public void setIsAdminRole(Boolean isAdminRole) {
		this.isAdminRole = isAdminRole;
	}
	
}
