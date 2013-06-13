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
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.acs.web.eunms.AddOrRomoveState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsTreeUtils;

/**
 *  author 李洪超 version
 *  创建时间：2009-3-11 上午09:51:10 
 *  资源管理Action
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "function?systemId=${systemId}", type = "redirectAction") })
public class FunctionAction extends CRUDActionSupport<Function> {

	private static final long serialVersionUID = 4814560124772644966L;
	private FunctionManager functionManager;
	private Page<Function> page = new Page<Function>(20, true);// 每页5项，自动查询计算总页数.
	private Page<Role> rolePage = new Page<Role>(20, true);// 每页5项，自动查询计算总页数.
	private Function function;
	private Long id;
	private List<Function> allFunction;
	private String functionName;
	private String functionId;
	private List<Long> checkedRoleIds;
	private List<Long> roleIds;
	private Long function_Id;//资源添加角色时传过来的id
	private Long systemId;
	private BusinessSystemManager businessSystemManager;
	private Integer isAddOrRomove;
	private String systemTree;
	
	/**
	 * 删除
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String delete() throws Exception {
			functionManager.deleteFunction(id);
			addActionMessage(getText("common.deleted"));
		    return RELOAD;
	}

	@Override
	public String list() throws Exception {
		//page = functionManager.getAllFunction(page, systemId);
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
			if(systemId == null){
				systemId = businessSystems.get(0).getId();
			}
			page = functionManager.getAllFunction(page, systemId);
		}
		setSystemTree(tree.toString());
	}

	/**
	 * 按条件查询
	 * 
	 * @return
	 */
	public void prepareSearch() throws Exception {
		prepareModel();
	}
	public String search() throws Exception {
		page = functionManager.getSearchFunction(page, function, false);
		return SUCCESS;
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			function = functionManager.getFunction(id);
		} else {
			function = new Function();
			if(systemId != null){
				BusinessSystem bs = businessSystemManager.getBusiness(systemId);
				function.setBusinessSystem(bs);
			}
		}
	}

	@Override
	public String save() throws Exception {
		functionManager.saveFunction(function);
		addActionMessage(getText("common.saved"));
		this.setSystemId(function.getBusinessSystem().getId());
		return RELOAD;
	}

	/**
	 * 资源添加角色
	 */
	public void prepareFunctionToRoleList() throws Exception {
		function = functionManager.getFunction(function_Id);
	}
	public String functionToRoleList() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Role role = new Role();
		role.setName(request.getParameter("roleName"));
		rolePage = functionManager.functionToRoleList(rolePage,role,function.getBusinessSystem().getId());
		//查询资源觉有的角色Id
		checkedRoleIds = functionManager.getRoleIds(function_Id);
		isAddOrRomove=AddOrRomoveState.ADD.code;
		generateTree();
		return "role-list";
	}
	/**
	 * 资源移除角色
	 * @throws Exception
	 */
	public void prepareFunctionRomoveRoleList() throws Exception {
		function = functionManager.getFunction(function_Id);
	}
	public String functionRomoveRoleList() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Role role = new Role();
		role.setName(request.getParameter("roleName"));
		rolePage = functionManager.functionToRomoveRoleList(rolePage,role,function.getBusinessSystem().getId(),function_Id);
		isAddOrRomove=AddOrRomoveState.ROMOVE.code;
		generateTree();
		return "role-list";
	}
	public String functionAddRole() throws Exception {
		function = functionManager.getFunction(function_Id);
		this.setSystemId(function.getBusinessSystem().getId());
		functionManager.functionAddRole(function_Id, roleIds,isAddOrRomove);
		return RELOAD;
	}
	
	
	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	
	public Function getModel() {

		return function;
	}

	public Page<Function> getPage() {
		return page;
	}

	public void setPage(Page<Function> page) {
		this.page = page;
	}

	@Required
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}

	public List<Function> getAllFunction() {
		return allFunction;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Page<Role> getRolePage() {
		return rolePage;
	}

	public void setRolePage(Page<Role> rolePage) {
		this.rolePage = rolePage;
	}

	public List<Long> getCheckedRoleIds() {
		return checkedRoleIds;
	}

	public void setCheckedRoleIds(List<Long> checkedRoleIds) {
		this.checkedRoleIds = checkedRoleIds;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}
	
	public Long getFunction_Id() {
		return function_Id;
	}

	public void setFunction_Id(Long function_Id) {
		this.function_Id = function_Id;
	}
	
	private Long functionGroupId;

	public Long getFunctionGroupId() {
		return functionGroupId;
	}

	public void setFunctionGroupId(Long functionGroupId) {
		this.functionGroupId = functionGroupId;
	}
	
	public String getFuncsByFunctionGroup(){
		if(functionGroupId != null){
			page = functionManager.getFunctionsByFunctionGroup(page, functionGroupId);
		}
		return SUCCESS;
	}
	
	public String getFunctionsBySystem(){
		if(systemId != null){
			page = functionManager.getFunctionsBySystem(page, systemId);
		}
		return SUCCESS;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}
	
	
}
