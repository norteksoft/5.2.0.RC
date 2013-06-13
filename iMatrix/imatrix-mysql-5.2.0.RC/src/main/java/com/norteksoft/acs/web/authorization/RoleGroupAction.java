package com.norteksoft.acs.web.authorization;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleGroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.RoleGroupManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsTreeUtils;

@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "role-group?businessSystemId=${businessSystemId}", type = "redirectAction") })
public class RoleGroupAction extends CRUDActionSupport<RoleGroup> {
	private static final long serialVersionUID = -8606451048205552471L;
	private Page<RoleGroup> page = new Page<RoleGroup>(20, true);
	private Page<Role> pageRole = new Page<Role>(20, true);
	private RoleGroupManager roleGroupManager;
	private RoleManager roleManager;
	private BusinessSystemManager businessSystemManager;
	private RoleGroup entity;
	private Long businessSystemId;
	private Long id;
	private Long paternId;
	private List<Long> roleIds;
	private Integer isAddOrremove;
	private String roleGroupName;
	private String systemTree;

	@Override
	public String delete() throws Exception {
		entity = roleGroupManager.getRoleGroup(id);
		this.setBusinessSystemId(entity.getBusinessSystem().getId());
		roleGroupManager.deleteRoleGroup(id);
		return RELOAD;
	}

	@Override
	public String list() throws Exception {
		generateTree();
		return SUCCESS;
	}
	
	/*
	 * 生成系统JSON树
	 */
	private void generateTree(){
		StringBuilder tree = new StringBuilder("[ ");
		List<BusinessSystem> businessSystems = businessSystemManager.getAllBusiness();
		for(BusinessSystem bs : businessSystems){
			tree.append(JsTreeUtils.generateJsTreeNode("BUSINESSSYSTEM_"+bs.getId(), "", bs.getName()));
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
			page = roleGroupManager.getRoleGroupsBySystem(page, businessSystemId);
		}
		setSystemTree(tree.toString());
	}

	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			entity = roleGroupManager.getRoleGroup(id);
		} else {
			entity = new RoleGroup();
			if(businessSystemId != null){
				BusinessSystem businessSystem = businessSystemManager.getBusiness(businessSystemId);
				entity.setBusinessSystem(businessSystem);
			}
		}
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	@Override
	public String save() throws Exception {
		roleGroupManager.saveRoleGroup(entity);
		this.setBusinessSystemId(entity.getBusinessSystem().getId());
		return RELOAD;
	}

	/**
	 * 角色组名称唯一性校验 
	 */
	public String checkRoleGroupName() {
		//FIXME 角色组名唯一性校验
		//roleGroupManager.isRoleGroupNameUnique(roleGroupName, roleGroupName);
		return renderText("true");
	}

	/**
	 * 角色组添加角色跳转页面
	 * 
	 * @return
	 * @throws Exception
	 */
	public void prepareInputRole()throws Exception{
		entity = roleGroupManager.getRoleGroup(paternId); 
	}
	
	public String inputRole() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String roleName = request.getParameter("roleName");
		
		pageRole = roleGroupManager.inputRole(pageRole,roleName,entity.getBusinessSystem().getId());
		isAddOrremove=0;
		generateTree();
		return "role-list";
	}

	/**
	 * 保存角色组和角色关系
	 * @return
	 * @throws Exception
	 */
	public String saveRole() throws Exception {
		entity = roleGroupManager.getRoleGroup(paternId);
		this.setBusinessSystemId(entity.getBusinessSystem().getId());
		roleGroupManager.saveRole(paternId, roleIds,isAddOrremove);
		return RELOAD;
	}

	/**
	 * 角色组移除角色跳转页面
	 * 
	 * @return
	 * @throws Exception
	 */
	public void prepareRemoveRole()throws Exception{
		entity = roleGroupManager.getRoleGroup(paternId); 
	}
	
	public String removeRole() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String roleName = request.getParameter("roleName");
		pageRole = roleGroupManager.romoveRole(pageRole,roleName,entity.getBusinessSystem().getId(),entity.getId());
		isAddOrremove=1;
		generateTree();
		return "role-list";
	}


	public RoleGroup getModel() {
		return entity;
	}

	public Page<RoleGroup> getPage() {
		return page;
	}

	public void setPage(Page<RoleGroup> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Required
	public void setRoleGroupManager(RoleGroupManager roleGroupManager) {
		this.roleGroupManager = roleGroupManager;
	}

	public RoleManager getRoleManager() {
		return roleManager;
	}

	@Required
	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public Page<Role> getPageRole() {
		return pageRole;
	}

	public void setPageRole(Page<Role> pageRole) {
		this.pageRole = pageRole;
	}

	public Long getPaternId() {
		return paternId;
	}

	public void setPaternId(Long paternId) {
		this.paternId = paternId;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public Long getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(Long businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	public String getRoleGroupsBySystem(){
		if(businessSystemId != null){
			page = roleGroupManager.getRoleGroupsBySystem(page, businessSystemId);
		}
		return SUCCESS;
	}

	public Integer getIsAddOrremove() {
		return isAddOrremove;
	}

	public void setIsAddOrremove(Integer isAddOrremove) {
		this.isAddOrremove = isAddOrremove;
	}

	public String getRoleGroupName() {
		return roleGroupName;
	}

	public void setRoleGroupName(String roleGroupName) {
		this.roleGroupName = roleGroupName;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}
	
	
}
