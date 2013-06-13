package com.norteksoft.acs.web.authorization;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.utils.ExportRole;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 标准角色Action
 * @author Administrator
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "standard-role?businessSystemId=${businessSystemId}", type="redirectAction") })
public class StandardRoleAction extends CRUDActionSupport<Role> {
	private static final long serialVersionUID = 1L;
	private static String ACS_SYSTEM_ADMIN="acsSystemAdmin";//系统管理员角色编码
	private static String ACS_SECURITY_ADMIN="acsSecurityAdmin";//安全管理员角色编码
	private static String ACS_AUDIT_ADMIN="acsAuditAdmin";//审计管理员角色编码
	
	private Page<Role> page = new Page<Role>(20, true);
	private Role entity;
	private Long businessSystemId;
	private StandardRoleManager roleManager;
	private Long id;
	private Long roleId;
	private BusinessSystemManager businessSystemManager;
	private String systemTree;
	private List<User> users;
	private List<Department> departments;
	private List<Workgroup> workgroups;
	private RoleManager manager;
	private DepartmentManager departmentManager;
	private Boolean isAdminRole=false;//是否是管理员角色
	private List<String> defaultAdmin;//是否是系统默认管理员
	
	@Autowired
	private CompanyManager companyManager;

	/**
	 * 删除标准角色
	 */
	@Override
	public String delete() throws Exception {
		roleManager.deleteStandardRole(id);
		return RELOAD;
	}
	/**
	 * 导出标准角色
	 */
	public String exportRole() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("角色信息.xls","UTF-8"));
		List<BusinessSystem> businessSystems = businessSystemManager.getAllBusiness();
		ExportRole.exportRole(response.getOutputStream(), businessSystems, ContextUtils.getCompanyId());
		ApiFactory.getBussinessLogService().log("授权管理", 
				"导出角色",ContextUtils.getSystemId("acs"));
		return null;
	}

	/**
	 * 分页显示标准角色
	 */
	@Override
	public String list() throws Exception {
		List<BusinessSystem> businessSystems = businessSystemManager.getAllBusiness();
		if(businessSystemId == null && businessSystems.size() > 0){
			businessSystemId = businessSystems.get(0).getId();
		}
		BusinessSystem bs=businessSystemManager.getBusiness(businessSystemId);
		Set<Role> roles = bs.getRoles();
		for(Role r : roles){
			if(r.isDeleted()) continue;
			//if("acsSystemAdmin".equals(r.getRoleCode())||"acsSecurityAdmin".equals(r.getRoleCode())|| //三个管理员不能重新分配用户
					//"acsAuditAdmin".equals(r.getRoleCode())) continue;
			if(roleId == null){
				roleId = r.getId();
				break;
			}else{
				break;
			}
		}
		ApiFactory.getBussinessLogService().log("授权管理", 
				"查看不同角色授权列表",ContextUtils.getSystemId("acs"));
		return SUCCESS;
	}
	@Action("standard-role-data")
	public String data(){
		return "standard-role-data";
	}
	/*
	 * 生成系统JSON树
	 */
	@Action("standard-role-tree")
	public String tree()throws Exception {
		String currentId = Struts2Utils.getParameter("currentId");
		if(currentId!=null&&currentId.startsWith("BUSINESSSYSTEM_")){
			this.renderText("[]");
			return null;
		}
		StringBuilder tree = new StringBuilder("[ ");
		List<BusinessSystem> businessSystems = businessSystemManager.getAllBusiness();
		if(businessSystemId == null && businessSystems.size() > 0){
			businessSystemId = businessSystems.get(0).getId();
		}
		for(BusinessSystem bs : businessSystems){
			if(bs.getId().equals(businessSystemId)){
				tree.append(JsTreeUtils.generateJsTreeNodeNew("BUSINESSSYSTEM_"+bs.getId(), "open", bs.getName(), getRolesNodes(bs, true), ""));
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeNew("BUSINESSSYSTEM_"+bs.getId(), "closed", bs.getName(), getRolesNodes(bs, false), ""));
			}
			tree.append(",");
		}
		if(tree.lastIndexOf(",") != -1 && tree.lastIndexOf(",") == tree.length()-1){
			tree.replace(tree.length()-1, tree.length(), "");
		}
		tree.append(" ]") ;
		if(roleId != null){
			users = manager.getCheckedUsersByRole(roleId);
			departments = departmentManager.getDepartmentsInRole(roleId);
			workgroups = manager.getCheckedWorkgroupByRole(roleId);
		}
		//setSystemTree(tree.toString());
		this.renderText(tree.toString());
		return null;
	}

	private String getRolesNodes(BusinessSystem bs, boolean isOpen){
		StringBuilder nodes = new StringBuilder();
		//Set<Role> roles = bs.getRoles();
		List<Role> roles = roleManager.getRolesBySystemId(bs.getId());
		boolean isNull = true;
		for(Role r : roles){
			if(r.isDeleted()) continue;
			if(r.getCompanyId()!=null && !r.getCompanyId().equals(ContextUtils.getCompanyId())) continue;
			//if("acsSystemAdmin".equals(r.getRoleCode())||"acsSecurityAdmin".equals(r.getRoleCode())|| //三个管理员不能重新分配用户
			//		"acsAuditAdmin".equals(r.getRoleCode())) continue;
			if(isNull && isOpen){
				if(roleId == null) roleId = r.getId();
				isNull = false;
			}
			nodes.append(JsTreeUtils.generateJsTreeNodeNew("ROLE_"+r.getId().toString(), "", r.getName(), ""));
			nodes.append(",");
		}
		//去掉最后一个逗号
		if(nodes.length()>0&&nodes.charAt(nodes.length()-1)==','){
			nodes.delete(nodes.length()-1, nodes.length());
		}
		return nodes.toString();
	}
	
	public String authoritys(){
		if(id != null){
			users = manager.getCheckedUsersByRole(id);
			departments = departmentManager.getDepartmentsInRole(id);
			workgroups = manager.getCheckedWorkgroupByRole(id);
			Role role=roleManager.getStandardRole(id);
			isAdminRole=hasAdminRole(role);
		}else if(roleId != null){
			users = manager.getCheckedUsersByRole(roleId);
			departments = departmentManager.getDepartmentsInRole(roleId);
			workgroups = manager.getCheckedWorkgroupByRole(roleId);
			Role role=roleManager.getStandardRole(roleId);
			isAdminRole=hasAdminRole(role);
		}
		defaultAdmin = getSystemDefaultAdmin();
		return "data";
	}
	
	private List<String> getSystemDefaultAdmin() {
		List<String> result = new ArrayList<String>();
		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
		String systemAdmin = company.getCode()+".systemAdmin";
		String securityAdmin = company.getCode()+".securityAdmin";
		String auditAdmin = company.getCode()+".auditAdmin";
		result.add(systemAdmin);
		result.add(securityAdmin);
		result.add(auditAdmin);
		return result;
	}

	private boolean hasAdminRole(Role role){
		if(ACS_SYSTEM_ADMIN.equals(role.getCode())||ACS_AUDIT_ADMIN.equals(role.getCode())||ACS_SECURITY_ADMIN.equals(role.getCode())){
			return true;
		}
		return false;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id != null){
			entity = roleManager.getStandardRole(id);
		}else{
			entity = new Role();
		}
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	/**
	 * 保存标准角色
	 */
	@Override
	public String save() throws Exception {
		businessSystemId = entity.getBusinessSystem().getId();
		roleManager.saveStandardRole(entity);
		return RELOAD;
	}

	public Role getModel() {
		return entity;
	}

	public Long getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(Long businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	public Page<Role> getPage() {
		return page;
	}

	public void setPage(Page<Role> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}

	@Required
	public void setRoleManager(RoleManager manager) {
		this.manager = manager;
	}

	@Required
	public void setStandardRoleManager(StandardRoleManager roleManager) {
		this.roleManager = roleManager;
	}	
	
	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public List<Workgroup> getWorkgroups() {
		return workgroups;
	}

	public void setWorkgroups(List<Workgroup> workgroups) {
		this.workgroups = workgroups;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Boolean getIsAdminRole() {
		return isAdminRole;
	}

	public void setIsAdminRole(Boolean isAdminRole) {
		this.isAdminRole = isAdminRole;
	}

	public List<String> getDefaultAdmin() {
		return defaultAdmin;
	}

	public void setDefaultAdmin(List<String> defaultAdmin) {
		this.defaultAdmin = defaultAdmin;
	}
}
