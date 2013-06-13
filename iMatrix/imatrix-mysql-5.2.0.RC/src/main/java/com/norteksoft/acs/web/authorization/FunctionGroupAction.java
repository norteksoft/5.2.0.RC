package com.norteksoft.acs.web.authorization;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionGroupManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.web.eunms.AddOrRomoveState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsTreeUtils;

/**
 *  author 李洪超 version 
 *  创建时间：2009-3-11 上午09:51:10
 *  部门管理Action
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "function-group?systemId=${systemId}", type = "redirectAction") })
public class FunctionGroupAction extends CRUDActionSupport<FunctionGroup> {

	private static final long serialVersionUID = 4814560124772644966L;
	private FunctionGroupManager functionGroupManager;
	private BusinessSystemManager businessSystemManager;
	private FunctionManager functionManager;
	private RoleManager roleManager;
	private Page<FunctionGroup> page = new Page<FunctionGroup>(20, true);
	private Page<Function> pageFunction = new Page<Function>(20, true);
	private FunctionGroup functionGroup;
	private Long id;
	private List<FunctionGroup> allFunctionGroup;
	private String function_Name;
	private String function_Id;
	private Long paternId;
	private List<Long> functionIds;
	private Long systemId;
	private Integer isAddOrRomove;
	private String systemTree;
	private String nodeId;
	private Long roleId;

	public String loadFunctionTree() throws Exception{
		StringBuilder tree = new StringBuilder("[");
		if("INIT".equals(nodeId)){
			BusinessSystem system = roleManager.getRole(roleId).getBusinessSystem();
			tree.append(JsTreeUtils.generateJsTreeNodeNew("SYSTEM_", "open", system.getName(), functionGroupNode(system.getId()), ""));
		}else if(nodeId.startsWith("GROUP_")){
			tree.append(functionNode(Long.parseLong(nodeId.split("_")[1])));
		}
		tree.append("]") ;
		this.renderText(tree.toString());
		return null;
	}
	
	private String functionGroupNode(Long systemId){
		StringBuilder node = new StringBuilder();
		List<FunctionGroup> functionGroups = functionGroupManager.getFuncGroupsBySystem(systemId);
		for(FunctionGroup group : functionGroups){
			node.append(JsTreeUtils.generateJsTreeNodeNew("GROUP_"+group.getId(), "closed", group.getName(), "folder")).append(",");
		}
		return deleteLastComma(node.toString());
	}
	
	private String functionNode(Long groupId){
		List<Function> functions = functionManager.getFunctionsByFunctionGroup(groupId);
		Role role = roleManager.getRole(roleId);
		List<Long> checkedIds = roleManager.getFunctionIds(roleId, role.getBusinessSystem().getId());
		if(0==isAddOrRomove){
			return unCheckedNodes(functions, checkedIds);
		}else if(1==isAddOrRomove){
			return checkedNodes(functions, checkedIds);
		}
		return "";
	}
	
	private String unCheckedNodes(List<Function> functions, List<Long> checkedIds){
		StringBuilder nodes = new StringBuilder();
		for(Function fun : functions){
			if(!checkedIds.contains(fun.getId())){
				nodes.append(JsTreeUtils.generateJsTreeNodeNew("FUN_"+fun.getId(), "", fun.getName(), "")).append(",");
			}
		}
		return deleteLastComma(nodes.toString());
	}
	
	private String checkedNodes(List<Function> functions, List<Long> checkedIds){
		StringBuilder nodes = new StringBuilder();
		for(Function fun : functions){
			if(checkedIds.contains(fun.getId())){
				nodes.append(JsTreeUtils.generateJsTreeNodeNew("FUN_"+fun.getId(), "", fun.getName(), "")).append(",");
			}
		}
		return deleteLastComma(nodes.toString());
	}
	
	private String deleteLastComma(String str){
		if(StringUtils.endsWith(str, ","))str= str.substring(0,str.length() - 1);
		return str;
	}
	
	@Override
	public String delete() throws Exception {
			functionGroupManager.deleteFunGroup(id);
			addActionMessage(getText("common.deleted"));
		    return RELOAD;
	}

	@Override
	public String list() throws Exception {
		//page = functionGroupManager.getSearchFunctionGroup(page, functionGroup, false);
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
			page = functionGroupManager.getFuncGroupsBySystem(page, systemId);
		}
		setSystemTree(tree.toString());
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			functionGroup = functionGroupManager.getFunctionGroup(id);
		} else {
			functionGroup = new FunctionGroup();
			if(systemId != null){
				BusinessSystem businessSystem = businessSystemManager.getBusiness(systemId);
				functionGroup.setBusinessSystem(businessSystem);
			}
		}
	}

	@Override
	public String save() throws Exception {
		functionGroupManager.saveFunGroup(functionGroup);
		addActionMessage(getText("common.saved"));
		systemId = functionGroup.getBusinessSystem().getId();
		return RELOAD;
	}

	/**
	 * 功能组添加功能跳转页面
	 * 
	 * @return
	 * @throws Exception
	 */
	public String inputFunction() throws Exception {
		functionGroup = functionGroupManager.getFunctionGroup(paternId);
		pageFunction = functionGroupManager.getAllFunction(pageFunction,function_Id,function_Name,functionGroup.getBusinessSystem().getId());
		systemId = functionGroup.getBusinessSystem().getId();
		isAddOrRomove=AddOrRomoveState.ADD.code;
		generateTree();
		return "function-list";
	}
	
	/**
	 * 功能组移除功能跳转页面
	 * 
	 * @return
	 * @throws Exception
	 */
	public String romoveFunction() throws Exception {
		functionGroup = functionGroupManager.getFunctionGroup(paternId);
		pageFunction = functionGroupManager.getAllRomoveFunction(pageFunction,function_Id,function_Name,functionGroup.getBusinessSystem().getId(),paternId);
		systemId = functionGroup.getBusinessSystem().getId();
		isAddOrRomove=AddOrRomoveState.ROMOVE.code;
		generateTree();
		return "function-list";
	}
	
  /**
   * 保存功能组和功能的关系
   * @return
   * @throws Exception
   */
	public String saveFunction() throws Exception {
		functionGroup = functionGroupManager.getFunctionGroup(paternId);
		systemId = functionGroup.getBusinessSystem().getId();
		functionGroupManager.saveFunction(paternId, functionIds,isAddOrRomove);
		return RELOAD;
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

		page = functionGroupManager.getSearchFunctionGroup(page, functionGroup, false);
		return SUCCESS;
	}

	public FunctionGroup getModel() {

		return functionGroup;
	}

	public Page<FunctionGroup> getPage() {
		return page;
	}

	public void setPage(Page<FunctionGroup> page) {
		this.page = page;
	}

	@Required
	public void setFunctionGroupManager(
			FunctionGroupManager functionGroupManager) {
		this.functionGroupManager = functionGroupManager;
	}
	
	@Autowired
	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public List<FunctionGroup> getAllFunGroup() {
		return allFunctionGroup;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Page<Function> getPageFunction() {
		return pageFunction;
	}

	public void setPageFunction(Page<Function> pageFunction) {
		this.pageFunction = pageFunction;
	}
	
	public FunctionManager getFunctionManager() {
		return functionManager;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}
	
	@Required
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}
	
	public Long getPaternId() {
		return paternId;
	}

	public void setPaternId(Long paternId) {
		this.paternId = paternId;
	}
	
	public List<Long> getFunctionIds() {
		return functionIds;
	}

	public void setFunctionIds(List<Long> functionIds) {
		this.functionIds = functionIds;
	}
	
	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public String getFuncGroupsBySystem(){
		if(systemId != null){
			page = functionGroupManager.getFuncGroupsBySystem(page, systemId);
		}
		return SUCCESS;
	}

	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	public String getFunction_Name() {
		return function_Name;
	}

	public void setFunction_Name(String function_Name) {
		this.function_Name = function_Name;
	}

	public String getFunction_Id() {
		return function_Id;
	}

	public void setFunction_Id(String function_Id) {
		this.function_Id = function_Id;
	}

	public void setFunctionGroup(FunctionGroup functionGroup) {
		this.functionGroup = functionGroup;
	}

	public FunctionGroup getFunctionGroup() {
		return functionGroup;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
}
