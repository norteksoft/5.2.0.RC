package com.norteksoft.wf.engine.web;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.service.authorization.AcsApiManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;

@SuppressWarnings("unchecked")
@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "tree", type = "redirectAction")})
public class TreeAction extends CrudActionSupport {
	private static final long serialVersionUID = 1L;

	private DepartmentManager departmentManager;
	private String currentId;
	private WorkflowInstanceManager workflowInstanceManager;
	private WorkflowDefinitionManager workflowDefinitionManager;
	private TaskService taskService;
	private AcsApiManager acsApiManager;
	private WorkflowTypeManager workflowTypeManager;
	
	@Autowired  
	public void setAcsApiManager(AcsApiManager acsApiManager) {
		this.acsApiManager = acsApiManager;
	}
	@Autowired
	public void setWorkflowTypeManager(WorkflowTypeManager workflowTypeManager) {
		this.workflowTypeManager = workflowTypeManager;
	}
	
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	public String getCompanyName(){
		return ContextUtils.getCompanyName();
	}
	
	public String getCurrentUser(){
		return ContextUtils.getLoginName();
	}
	
	/**
	 * 我的流程树
	 * @return
	 * @throws Exception
	 */
	@Action("tree-myProcess")
	public String myProcess() throws Exception{
		StringBuilder tree = new StringBuilder("[ ");
		if("INITIALIZED".equals(currentId)){
			List<WorkflowType> wfTypes = workflowTypeManager.getAllWorkflowType();
			StringBuilder subNodes = new StringBuilder();
			//办理中
			for(WorkflowType wft : wfTypes){
				subNodes.append(JsTreeUtils.generateJsTreeNodeDefault("ING_"+wft.getId(), null, 
						wft.getName() + "(" + getInstanceNumByType(wft.getId(), false) + 
						")", myInstanceByType(wft.getId(), false) )).append(",");
			}
			JsTreeUtils.removeLastComma(subNodes);
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("ING", "open", 
					getText("workflow.doing")+"("+getInstanceNumByEnable(false)+")", subNodes.toString())).append(",");
			//已完成
			subNodes = new StringBuilder();
			for(WorkflowType wft : wfTypes){
				subNodes.append(JsTreeUtils.generateJsTreeNodeDefault("END_"+wft.getId(), null, 
						wft.getName() + "(" + getInstanceNumByType(wft.getId(), true) + 
						")", myInstanceByType(wft.getId(), true) )).append(",");
			}
			JsTreeUtils.removeLastComma(subNodes);
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("END", "", 
					getText("workflow.complete")+"("+getInstanceNumByEnable(true)+")", subNodes.toString())).append(",");
			
		}else if(currentId.startsWith("ING_")){
			
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	//我委托的流程
	public String delegateMonitor() throws Exception{
		StringBuilder tree = new StringBuilder("[ ");
		tree.append(JsTreeUtils.generateJsTreeNodeDefault("DEL_ING", null, 
				getText("workflow.doing") + "(" + 
				taskService.getDelegateTasksNum(getCompanyId(), getCurrentUser(), false) + ")" )).append(",");
		tree.append(JsTreeUtils.generateJsTreeNodeDefault("DEL_END", null, 
				getText("workflow.complete") + "(" + 
				taskService.getDelegateTasksNum(getCompanyId(), getCurrentUser(), true) + ")" ));
		
	tree.append(" ]");
	renderText(tree.toString());
	return null;
	}
	
	//我受托的流程
	public String superviseAsTrusteeTree() throws Exception{
		StringBuilder tree = new StringBuilder("[ ");
		tree.append(JsTreeUtils.generateJsTreeNodeDefault("TRUSTEE_ING", null, 
				getText("workflow.doing") + "(" + 
				taskService.getTrusteeTasksNum(getCompanyId(), getCurrentUser(), false) + ")" )).append(",");
		tree.append(JsTreeUtils.generateJsTreeNodeDefault("TRUSTEE_END", null, 
				getText("workflow.complete") + "(" + 
				taskService.getTrusteeTasksNum(getCompanyId(), getCurrentUser(), true) + ")" ));
		
	tree.append(" ]");
	renderText(tree.toString());
	return null;
	}
	
	
	private String myInstanceByType(Long typeId, boolean isEnd){
		StringBuilder subNodes = new StringBuilder();
		List<WorkflowDefinition> definitions = workflowDefinitionManager.getWfDefinitionsByType(getCompanyId(), typeId);
		for(WorkflowDefinition wfd : definitions){
			if(isEnd){
				subNodes.append(JsTreeUtils.generateJsTreeNodeDefault("END_WFD_" + wfd.getId(), "", 
						wfd.getName() + "(" + getInstanceNumByDefinition(wfd, isEnd) + ")")).append(",");
			}else{
				subNodes.append(JsTreeUtils.generateJsTreeNodeDefault("ING_WFD_" + wfd.getId(), "", 
						wfd.getName() + "(" + getInstanceNumByDefinition(wfd, isEnd) + ")")).append(",");
			}
		}
		JsTreeUtils.removeLastComma(subNodes);
		return subNodes.toString();
	}
	
	private Integer getInstanceNumByEnable(boolean isEnd){
		if(isEnd){
			return workflowInstanceManager.getEndInstanceNumByEnable(getCompanyId(), getCurrentUser());
		}else{
			return workflowInstanceManager.getNotEndInstanceNumByEnable(getCompanyId(), getCurrentUser());
		}
		
	}
	
	/*
	 * 根据流程定义查询流程实例个数
	 */
	private Integer getInstanceNumByDefinition(WorkflowDefinition definition, boolean isEnd){
		if(isEnd){
			return workflowInstanceManager.getEndInstanceNumByDefinition(getCompanyId(), getCurrentUser(), definition);
		}else{
			return workflowInstanceManager.getNotEndInstanceNumByDefinition(getCompanyId(), getCurrentUser(), definition);
		}
	}
	
	/*
	 * 根据流程实例类型查询当前用户流程实例个数
	 */
	private Integer getInstanceNumByType(Long typeId, boolean isEnd){
		if(isEnd){
			return workflowInstanceManager.getEndInstanceNumByCreatorAndType(getCompanyId(), getCurrentUser(), typeId);
		}else{
			return workflowInstanceManager.getNotEndInstanceNumByCreatorAndType(getCompanyId(), getCurrentUser(), typeId);
		}
	}
	
	/**
	 * 流程汇编树
	 * @return
	 * @throws Exception
	 */
	public String process() throws Exception{
		StringBuilder tree = new StringBuilder("[ ");
		if("INITIALIZED".equals(currentId)){
			List<WorkflowType> wfTypes = workflowTypeManager.getAllWorkflowType();
			boolean isFirstNode = true;
			for(WorkflowType wft : wfTypes){
				if(isFirstNode){
					tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFDTYPE_" + wft.getId(), "open",wft.getName(), processDefs(wft.getId()))).append(",");
					isFirstNode = false;
				}else{
					tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFDTYPE_" + wft.getId(), "", wft.getName(), processDefs(wft.getId()))).append(",");
				}
			}
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	public String processDefs(Long typeId){
		StringBuilder subNodes = new StringBuilder();
		List<WorkflowDefinition> definitions = workflowDefinitionManager.getWfDefinitionsByType(getCompanyId(), typeId);
		for(WorkflowDefinition wfd : definitions){
			subNodes.append(JsTreeUtils.generateJsTreeNodeDefault("WFDID_" + wfd.getId(), "", wfd.getName())).append(",");
		}
		JsTreeUtils.removeLastComma(subNodes);
		return subNodes.toString();
	}
 	
	/**
	 * 流程及表单类型树
	 * @return
	 * @throws Exception
	 */
	public String wfTypes() throws Exception{
		List<WorkflowType> wfTypes = workflowTypeManager.getAllWorkflowType();
		List<BusinessSystem> businessSystemList = acsApiManager.getAllBusiness(getCompanyId());
		StringBuilder tree = new StringBuilder("[ ");
		if( "INITIALIZED_PROCESS".equals(currentId)){
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("ENABLE_ALL_1", "open", 
					"当前版本",getSecondNodesInWftypeTree(wfTypes,businessSystemList,"ENABLE"))).append(",");
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("UNABLE_ALL_1", "", 
					"历史版本",getSecondNodesInWftypeTree(wfTypes,businessSystemList,"UNABLE"))).append(",");
			
		}else if( "INITIALIZED_MONITOR".equals(currentId)){
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFT_monitor_0", null, 
					"所有流程")).append(",");
			boolean isSuperWf=workflowDefinitionManager.isSuperWf();
			for(WorkflowType wft : wfTypes){
				tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFT_monitor_"+wft.getId(), null, wft.getName(),monitorTree(wft,isSuperWf))).append(",");
			}
		}else if("INITIALIZED_FORM".equals(currentId)){
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("parent_default_0", "open", "自定义表单" ,formTypes(wfTypes,"default"))).append(",");
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("parent_standard_0", null, "标准表单",formTypes(wfTypes,"standard"))).append(",");
		}else if("INITIALIZED_DICT".equals(currentId)){
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFT_myCreate_0", null, "所有数据" )).append(",");
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFT_type_0", null, "类型管理")).append(",");
		}else if("INITIALIZED_TEMPLATE".equals(currentId)){
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFT_0", null, getText("workflow.allTemplate") )).append(",");
			for(WorkflowType wft : wfTypes){
				tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFT_"+wft.getId(), null, wft.getName())).append(",");
			}
		}else if("INITIALIZED_WFD_TEMPLATE".equals(currentId)){
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFDT_0", null, getText("workflow.allTemplate") )).append(",");
			for(WorkflowType wft : wfTypes){
				tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFDT_"+wft.getId(), null, wft.getName())).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	private String monitorTree(WorkflowType type,boolean isSuperWf){
		StringBuilder tree = new StringBuilder();
		List<String> definitionCodes=workflowDefinitionManager.getWfDefinitionCodesByType(getCompanyId(), type.getId());
		for(String def : definitionCodes){
			WorkflowDefinition wf=workflowDefinitionManager.getWorkflowDefinitionByCodeAndVersion(def, 1,ContextUtils.getCompanyId(),isSuperWf);
			if(wf!=null){
				tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFT_monitor_"+type.getId()+"_"+def, null, wf.getName())).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		return tree.toString();
	}
	
	public String getSecondNodesInWftypeTree(List<WorkflowType> wfTypes,List<BusinessSystem> businessSystemList,String belongType){
		StringBuilder secondNodes = new StringBuilder();
		
		//流程类型
		StringBuilder subNodes = new StringBuilder();
		for(WorkflowType wft : wfTypes){
			subNodes.append(JsTreeUtils.generateJsTreeNodeDefault(belongType+"_WFT_"+wft.getId(), null, wft.getName())).append(",");
		}
		JsTreeUtils.removeLastComma(subNodes);
		secondNodes.append(JsTreeUtils.generateJsTreeNodeDefault(belongType+"_WFT_0", "open", 
				"流程类型", subNodes.toString())).append(",");
		
		//所有系统
		 subNodes = new StringBuilder();
		for(BusinessSystem bs : businessSystemList){
			subNodes.append(JsTreeUtils.generateJsTreeNodeDefault(belongType+"_BSYS_"+bs.getId(), null, bs.getName())).append(",");
		}
		JsTreeUtils.removeLastComma(subNodes);
		secondNodes.append(JsTreeUtils.generateJsTreeNodeDefault(belongType+"_BSYS_0", "", 
				"所有系统", subNodes.toString())).append(",");
		
		JsTreeUtils.removeLastComma(secondNodes);
		return secondNodes.toString();
	}
	
	public String formTypes(List<WorkflowType> wfTypes,String formType){
		StringBuilder tree = new StringBuilder();
		tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFT_"+formType+"_0", null, getText("workflow.allForm"))).append(",");
		for(WorkflowType wft : wfTypes){
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFT_"+formType+"_"+wft.getId(), null, wft.getName())).append(",");
		}
		return tree.toString();
	}
	
	public String load() throws Exception{
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
			subNodes.append(JsTreeUtils.generateJsTreeNodeDefault("NODEPARTMENTUS," + getCompanyId(), 
					"closed", getText("user.noDepartment"), ""));
			JsTreeUtils.removeLastComma(subNodes);
			//公司节点
			tree.append(JsTreeUtils.generateJsTreeNodeDefault("", "open", getCompanyName(), subNodes.toString()));
		}else if(currentId.startsWith("DEPARTMENT")){
			tree.append(getUserNodes(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length()))));
		}else if(currentId.startsWith("NODEPARTMENTUS")){
			tree.append(getNoDepartmentUserNodes(Long.valueOf(currentId.substring(currentId.indexOf(',')+1, currentId.length()))));
		}
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	
	/**
	 * 部门节点 
	 */
	private String getDdeptNodes(Department dept){
		StringBuilder nodes = new StringBuilder();
		if(dept.getParent() == null){
			//部门树节点
			nodes.append(JsTreeUtils.generateJsTreeNodeDefault("DEPARTMENT," + dept.getId(), "closed", dept.getName(), ""));
		}
		return nodes.toString();
	}
	
	/**
	 * 用户节点 
	 */
	public String getUserNodes(Long deptId) throws Exception{
		StringBuilder nodes = new StringBuilder();
		Department dept = departmentManager.getDepartment(deptId);
		for(Department d : dept.getChildren()){
			nodes.append(getDdeptNodes(d)).append(",");
		}
		for(DepartmentUser du : dept.getDepartmentUsers()){
			if(du.isDeleted()) continue;
			com.norteksoft.acs.entity.organization.User user = du.getUser();
			if(user.isDeleted()) continue;
			nodes.append(JsTreeUtils.generateJsTreeNodeDefault("USER," + user.getId() + "," + user.getLoginName(), "", 
					user.getName(), "")).append(",");
		}
		JsTreeUtils.removeLastComma(nodes);
		return nodes.toString();
	}
	
	/**
	 * 没有部门的用户的树节点
	 * @param companyId
	 * @return
	 */
	public String getNoDepartmentUserNodes(Long companyId){
		StringBuilder nodes = new StringBuilder();
		ThreadParameters parameters=new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		List<User> users = ApiFactory.getAcsService().getUsersWithoutDepartment();
		for(User user : users){
			if(user.isDeleted()) continue;
			nodes.append(JsTreeUtils.generateJsTreeNodeDefault("USER," + user.getId() + "," + user.getLoginName(), "", 
					user.getName(), "")).append(",");
		}
		JsTreeUtils.removeLastComma(nodes);
		return nodes.toString();
	}
	
	/**
	 * 选择用户、部门和工作组，并将它们分装为json格式。
	 * 格式如：{user:"用户登录名1,用户登录名2,...",department:"部门1,部门2,...",workGroup:"工作组1,工作组2,..."}
	 */
	public String selectUserPackToJson() throws Exception{
		
		return "selectUserPackToJson";
	}
	
	
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	
	@Required
	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}
	
	@Required
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}
	
	@Required
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	@Override
	public String delete() throws Exception {
		return null;
	}

	@Override
	public String input() throws Exception {
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		
	}

	@Override
	public String save() throws Exception {
		return null;
	}

	public Object getModel() {
		return null;
	}

}
