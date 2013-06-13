package com.norteksoft.wf.engine.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.SearchUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.ProcessType;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionTemplate;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "workflow-definition?wfdId=${wfdId}&wfdFile=${wfdFile}", type = "redirectAction")})
public class WorkflowDefinitionAction extends CrudActionSupport<WorkflowDefinition>{
	
	private static final long serialVersionUID = 1L;
	private Long wfdId;
	private String workflowId;//流程实例ID
	private Set<Long> workflowIds;
	private Set<WorkflowInstance> workflowInstances;
	private WorkflowInstance workflowInstance;
	private WorkflowDefinitionManager workflowDefinitionManager;
	private WorkflowInstanceManager workflowInstanceManager;
	private WorkflowTypeManager workflowTypeManager;
	private FormViewManager formViewManager;
	private ListViewManager listViewManager;
	private Page<WorkflowDefinition> wfdPage = new Page<WorkflowDefinition>(0, true);
	private Page<WorkflowTask> tasks=new Page<WorkflowTask>(0, true);
	private Page<Object> wiPage = new Page<Object>(0,true);
	private String xmlFile;
	private Long defCompanyId;
	private String defCreator;
	private List<WorkflowType> typeList;
	private String searchCdn;
	private Long type = 0l;//流程类型id
	private Long sysId = 0l ;//系统id
	private List<String> titleList;
	private WorkflowDefinition workflowDefinition;
	private List<ListColumn> displayField = new ArrayList<ListColumn>();
	private String tree;
	private String firstTreeId;
	private String wfDefinitionId;
	private String formHtml;
	private List<WorkflowDefinitionTemplate> templates;
	private Long templateId;
	private Long defSystemId;
	private String option;
	private String formType;
	private String processId;// 流程定义的id
	private Long formId;
	private String fieldPermission; //字段的编辑权限
	private List<Long> wfdIds;
	private List<String> operates;
	private List<String> searchValues;
	private String vertionType="ENABLE";
	
	private List<FormView> forms;
	
	private String formCode;
	private Integer version;
	private List<WorkflowDefinition> definitions;
	private String definitionCode;
	private List<String> enNames;
	private List<String> chNames;
	private List<String> dataTypes;
	private String position;
	private Long instanceId;//流程实例的记录id
	private String url;
	private String operationName;//流程监控中做的什么操作:查看流程实例（view）/应急处理(urgenDone)
	private String transactorName;//批量移除任务页面传来的办理人登录名
	private List<Long> taskIds;//需批量移除的任务id
	private List<BusinessSystem> systems;//所有系统
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private TaskService taskService;
	private String systemCode;
	private Map<String, List<WorkflowTask>> chooseTasks;
	/**
	 * 模版列表
	 * @return
	 * @throws Exception
	 */
	public String template() throws Exception {
		typeList = workflowTypeManager.getAllWorkflowType();
		if(type.equals(0l)){
			if(typeList!=null && typeList.size()>0){
				templates = workflowDefinitionManager.getWorkflowDefinitionTemplates(typeList.get(0).getId());
			}
		}else{
			templates = workflowDefinitionManager.getWorkflowDefinitionTemplates(type);
		}
		return "template";
	}

	public String templateList() throws Exception {
		templates = workflowDefinitionManager.getWorkflowDefinitionTemplates(type);
		return "template";
	}
	
	public String getActiveDefinition() throws Exception{
		workflowDefinitionManager.getActiveDefinition(wfdPage);
		return SUCCESS;
	}
	
	/**
	 * 流程启用与禁用
	 * @return
	 * @throws Exception
	 */
	public String deploy() throws Exception{
		ApiFactory.getBussinessLogService().log("流程定义", 
				"启用与禁用流程定义", 
				ContextUtils.getSystemId("wf"));
		this.renderText(SUCCESS_MESSAGE_LEFT+workflowDefinitionManager.deployProcess(wfdId)+MESSAGE_RIGHT);
		return null;
	}
	
	
	public String getFirstTreeId() {
		return firstTreeId;
	}
	
	public String getTree() {
		return tree;
	}
	
	public String getWfDefinitionId() {
		return wfDefinitionId;
	}
	
	public void setFormHtml(String formHtml) {
		this.formHtml = formHtml;
	}
	
	public String getFormHtml() {
		return formHtml;
	}
	
	@Action("workflow-definition-view")
	public String view() throws Exception{
		ApiFactory.getBussinessLogService().log("流程定义", 
				"查看流程定义", 
				ContextUtils.getSystemId("wf"));
		workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
		FormView form=formViewManager.getCurrentFormViewByCodeAndVersion(workflowDefinition.getFormCode(), workflowDefinition.getFromVersion());
		if(form==null){
			return "viewFaild";
		}else{
			formHtml = form.getHtml();
			wfDefinitionId = workflowDefinitionManager.getWfDefinition(wfdId).getProcessId();
			return "workflow-definition-view";
		}
	}
	
	/**
	 * 删除还没有部署的流程定义
	 */
	@Override
	public String delete() throws Exception {
		if(wfdIds!=null){
			int num = workflowDefinitionManager.deleteWfDefinitions(wfdIds);
			ApiFactory.getBussinessLogService().log("流程定义", 
					"删除流程定义", 
					ContextUtils.getSystemId("wf"));
			this.renderText(num+"个已删除；"+(wfdIds.size()-num)+"个已启用或有相应的实例存在，不能删除");
		}
		return null;
	}
	
	/**
	 * 管理员删除流程实例
	 */
	public String deleteWorkflow() throws Exception {
		ApiFactory.getBussinessLogService().log("流程监控", 
				"删除流程实例", 
				ContextUtils.getSystemId("wf"));
		this.renderText(workflowInstanceManager.deleteWorkflowInstances(workflowInstances));
		return null;
	}

	@Override
	public String input() throws Exception {
		if(templateId!=null&& WorkflowDefinitionTemplate.CUSTOM_PROCESS_TEMPLATE.equals(workflowDefinitionManager.getWorkflowDefinitionTemplate(templateId).getTemplateType())){
			getFormInfo();
			return "customProcess";
		}else{
			defCreator = ContextUtils.getLoginName();
			defSystemId = ContextUtils.getSystemId();
			defCompanyId = ContextUtils.getCompanyId();
			return INPUT;
		}
	}
	
	private void getFormInfo(){
		forms = formViewManager.getFormViewsByCompany();
		FormView temp = new FormView();
		temp.setName("请选择表单");
		forms.add(0, temp);
	}
	public void prepareUpdate() throws Exception{
		prepareModel();
	}
	
	public String update() throws Exception {
		if(ProcessType.CUSTOM_PROCESS.equals(workflowDefinition.getProcessType())){
			type = workflowDefinitionManager.getWfDefinition(wfdId).getTypeId();
			getFormInfo();
			return "customProcess";
		}else{
			if(type==null || type.intValue() == 0){
				type=0l;
			}else{
				type = workflowDefinitionManager.getWfDefinition(wfdId).getTypeId();
			}
			defCreator = ContextUtils.getLoginName();
			defCompanyId = ContextUtils.getCompanyId();
			xmlFile=workflowDefinitionManager.getXmlByDefinitionId(wfdId, defCompanyId);
			defSystemId = ContextUtils.getSystemId();
			return "update";
		}
	}

	@Override
	public String save() throws Exception {
		workflowDefinitionManager.saveWorkflowDefinition(wfdId, ContextUtils.getCompanyId(), xmlFile,type,ContextUtils.getSystemId());
		return RELOAD;
	}
	public String saveCustomProcess() throws Exception {
		FormView form = formViewManager.getFormView(formId);
		workflowDefinition.setFormName(form.getName());
		workflowDefinition.setFromVersion(form.getVersion());
		workflowDefinition.setVersion(workflowDefinitionManager.generateWorkflowDefinitionVersion(workflowDefinition.getName()));
		workflowDefinitionManager.saveWorkflowDefinition(workflowDefinition);
		wfdId = workflowDefinition.getId();
		getFormInfo();
		return "customProcess";
	}
	
	public void prepareSaveCustomProcess() throws Exception{
		if(wfdId==null){
			createWorkflowDefinition();
		}else{
			workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
		}
	}
	private WorkflowDefinition createWorkflowDefinition(){
		workflowDefinition = new WorkflowDefinition();
		workflowDefinition.setProcessType(ProcessType.CUSTOM_PROCESS);
		workflowDefinition.setSystemId(ContextUtils.getSystemId());
		workflowDefinition.setCompanyId(ContextUtils.getCompanyId());
		workflowDefinition.setCreator(ContextUtils.getLoginName());
		workflowDefinition.setCreatedTime(new Date());
		workflowDefinition.setTypeId(type);
		return workflowDefinition;
	}

	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	@Action("workflow-definition-data")
	public String data(){
		if(wfdPage.getPageSize()>1){
			ApiFactory.getBussinessLogService().log("流程定义", 
					"流程定义列表", 
					ContextUtils.getSystemId("wf"));
			typeList = workflowTypeManager.getAllWorkflowType();
			if(type==null || type.intValue() == 0||sysId==null||sysId.intValue()==0){
				workflowDefinitionManager.getWfDefinitions(wfdPage,vertionType,ContextUtils.getLoginName());
				this.renderText(PageUtils.pageToJson(wfdPage));
				return null;
			}else if(type!=null&&type.intValue() != -1){
				workflowDefinitionManager.getWfDefinitions(wfdPage,type,vertionType,ContextUtils.getLoginName());
				this.renderText(PageUtils.pageToJson(wfdPage));
				return null;
			}else if(sysId!=null&&sysId.intValue() != -1){
				workflowDefinitionManager.getWfDefinitionsBySystemId(wfdPage,sysId,vertionType,ContextUtils.getLoginName());
				this.renderText(PageUtils.pageToJson(wfdPage));
				return null;
			}
		}
		return "workflow-definition-data";
	}
	/**
	 * 结束流程
	 */
	public String endWorkflow() throws Exception {
		String msg = workflowInstanceManager.endWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log("流程定义", 
				"取消流程", 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 结束流程
	 */
	public String endWorkflowDef() throws Exception {
		String msg = workflowInstanceManager.endWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log("流程监控模块", 
				"取消流程", 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 强制结束流程
	 */
	@Action("workflow-definition-compelEndWorkflow")
	public String compelEndWorkflow() throws Exception {
		String msg = workflowInstanceManager.compelEndWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log("流程定义", 
				"强制结束流程", 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	

	/**
	 * 流程监控
	 */
	public String monitor() throws Exception {
		if(wiPage.getPageSize() > 1){
			workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
			
			workflowDefinitionManager.monitor(wiPage,workflowDefinition);
			ApiFactory.getBussinessLogService().log("流程定义/流程监控", 
					"流程实例列表", 
					ContextUtils.getSystemId("wf"));
			renderText(PageUtils.pageToJson(wiPage));
			return null;
		}
		return "monitor";
	}
	
	/**
	 * 流程监控管理
	 */
	public String monitorDefintion() throws Exception {
		if(wiPage.getPageSize()>1){
			workflowDefinitionManager.monitorDefinition(wiPage,type,definitionCode);
			ApiFactory.getBussinessLogService().log("流程监控模块", 
					"流程实例列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(wiPage));
			return null;
		}
		return "monitorStandardManager";
	}
	
	
	/**
	 * 查询流程实例
	 */
	public String search() throws Exception {
		workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
		List<WorkflowInstance> wiList = workflowInstanceManager.getAllWorkflowInstances(wfdId,workflowDefinition.getSystemId());//流程定义所有的实例
		List<WorkflowInstance> wiEndList = workflowInstanceManager.getAllEndWorkflowInstances(wfdId,workflowDefinition.getSystemId());//流程定义所有结束的实例
		if(wiList!=null)workflowDefinition.setInstanceCount(wiList.size());
		if(wiEndList!=null)workflowDefinition.setEndCount(wiEndList.size());
		FormView form = formViewManager.getCurrentFormViewByCodeAndVersion(workflowDefinition.getFormCode(), workflowDefinition.getFromVersion());
		ListView listView=listViewManager.getDefaultDisplay(form.getDataTable().getId());
		if(listView!=null){
			for(ListColumn column:listView.getColumns()){
				if(column.getVisible()){
					displayField.add(column);
				}
			}
		}
		workflowDefinitionManager.searchMonitor(wiPage,workflowDefinition,getSearchManagerFields());
		ApiFactory.getBussinessLogService().log("流程监控", 
				"查询流程实例", 
				ContextUtils.getSystemId("wf"));
//		if(form.isStandardForm()){
//			return "monitorStandardForm";
//		}else{
			return "monitor";
//		}
	}
	
	public String searchManager() throws Exception{
		workflowDefinitionManager.searchManagerMonitor(wiPage,type,definitionCode,getSearchManagerFields());
		return "monitorStandardManager";
	}
	
	private List<ListColumn> getSearchManagerFields(){
		if(enNames==null)return null;
		List<ListColumn> fields = new ArrayList<ListColumn>();
		ListColumn field = null;
		for(int i=0;i<enNames.size();i++){
			field =new ListColumn();
			TableColumn tb=new TableColumn();
			field.setTableColumn(tb);
			field.getTableColumn().setName(enNames.get(i));
			field.getTableColumn().setAlias(chNames.get(i));
			field.getTableColumn().setDataType(DataType.valueOf(dataTypes.get(i).toUpperCase()));
			field.getTableColumn().setOperate(operates.get(i));
			field.getTableColumn().setSearchValue(searchValues.get(i));
			fields.add(field);
		}
		return fields;
	}
	/**
	 * 根据系统编码获得系统url
	 * @return
	 * @throws Exception
	 */
	@Action("obtain-system-url")
	public String obtainSystemUrl() throws Exception{
		BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
		if(system==null||StringUtils.isEmpty(system.getPath())){
			this.renderText("");
		}else{
			this.renderText(system.getPath());
		}
		return null;
	}
	/**
	 * 流程监控/查看表单和应急处理入口
	 * @return
	 * @throws Exception
	 */
	@Action("monitor-view")
	public String monitorView() throws Exception{
		WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(instanceId);
		if(instance!=null){
			if("view".equals(operationName)){
				url=instance.getFormUrl();
			}else if("urgenDone".equals(operationName)){
				url=instance.getEmergencyUrl();
			}
			if(StringUtils.isNotEmpty(url)){
				if(url.indexOf("?")!=-1){
					url = url+instance.getDataId()+"&instanceId="+instance.getProcessInstanceId();
				}else{
					url = url + "?id="+instance.getDataId()+"&instanceId="+instance.getProcessInstanceId();
				}
				if(!url.startsWith("http")){
					int index = url.indexOf("/");
					String code = url.substring(0, index);
					String systemUrl=SystemUrls.getBusinessPath(code);
					if(StringUtils.isNotEmpty(systemUrl))
						url = systemUrl + url.substring(index, url.length());
				}
				url = url+"&_r=1";
			}
		}
		ApiFactory.getBussinessLogService().log("流程监控", 
				"查看表单或应急处理", 
				ContextUtils.getSystemId("wf"));
		return "monitor-view";
	}
	
	
	/**
	 * 流程定义/流程监控/暂停流程实例
	 * @return
	 * @throws Exception
	 */
	public String pauseWorkflows() throws Exception{
		String msg =  workflowInstanceManager.pauseWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log("流程定义/流程监控", 
				"暂停流程", 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 流程监控/暂停流程实例
	 */
	public String pauseWorkflowDef() throws Exception {
		String msg = workflowInstanceManager.pauseWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log("流程监控模块", 
				"暂停流程", 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 流程定义/流程监控/继续流程
	 * @return
	 * @throws Exception
	 */
	public String continueWorkflows() throws Exception{
		String msg = workflowInstanceManager.continueWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log("流程定义/流程监控", 
				"继续流程", 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 流程监控/继续流程
	 */
	public String continueWorkflowDef() throws Exception {
		String msg = workflowInstanceManager.continueWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log("流程监控模块", 
				"继续流程", 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 流程监控/根据办理人姓名查询任务
	 * @return
	 * @throws Exception
	 */
	public String searchTasks() throws Exception{
		return "tasks";
	}
	/**
	 * 流程监控/根据办理人姓名查询任务
	 * @return
	 * @throws Exception
	 */
	public String searchTaskDatas() throws Exception{
		if(tasks.getPageSize()>1 && !SearchUtils.getQueryParameter().isEmpty()){
			taskService.getActivityTasksByTransactorName(tasks,type,definitionCode,wfdId);
			ApiFactory.getBussinessLogService().log("流程监控", 
					"查询任务", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(tasks));
			return null;
		}
		return "tasks";
	}
	
	/**
	 * 流程监控/批量移除任务
	 * @return
	 * @throws Exception
	 */
	public String delTasksBatch() throws Exception{
		chooseTasks = taskService.deleteTasks(taskIds);
		if(chooseTasks.isEmpty()){
			ApiFactory.getBussinessLogService().log("流程监控", 
					"批量移除任务", 
					ContextUtils.getSystemId("wf"));
			addActionSuccessMessage("成功移除"+taskIds.size()+"个");
			return searchTaskDatas();
		}
		return "choose-task";
	}
	
	public Map<String, List<WorkflowTask>> getChooseTasks() {
		return chooseTasks;
	}
	
	public void prepareDeleteWorkflow() throws Exception{
		if(workflowIds!=null&&!workflowIds.isEmpty()){
			workflowInstances = workflowInstanceManager.getWorkflowInstances(workflowIds);
		}
	}
	
	public void prepareDeleteConfirm() throws Exception{
		
	}
	
	public String deleteConfirm() throws Exception{
		return "deleteConfirm";
	}
	
	public String chooseDefinition() throws Exception {
		return "choose";
	}
	
	public String getGoldDefinitions() throws Exception {
		StringBuilder notes = new StringBuilder("[");
		definitions = workflowDefinitionManager.getAllEnableDefinitionsByformCodeAndVersion(formCode, version);
		for (WorkflowDefinition definition : definitions) {
			notes.append(JsTreeUtils.generateJsTreeNodeDefault(Long.toString(definition.getId()), "closed", definition.getName()));
			notes.append(",");
		}
		JsTreeUtils.removeLastComma(notes);
		notes.append("]");
		renderText(notes.toString());
		return null;
	}
	public void prepareBasicInput() throws Exception {
		prepareModel();
	}
	@Action("workflow-definition-basic-input")
	public String basicInput() throws Exception {
		typeList = workflowTypeManager.getAllWorkflowType();
		systems=businessSystemManager.getAllSystems();
		return "workflow-definition-basic-input";
	}
	
	public void prepareSaveBasic() throws Exception {
		prepareModel();
	}
	@Action("workflow-definition-save-basic")
	public String saveBasic() throws Exception {
		typeList = workflowTypeManager.getAllWorkflowType();
		systems=businessSystemManager.getAllSystems();
		workflowDefinitionManager.saveWfBasic(workflowDefinition);
		addActionSuccessMessage("保存成功");
		return "workflow-definition-basic-input";
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(wfdId==null){
			workflowDefinition = new WorkflowDefinition();
		}else{
			workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
		}
	}

	public WorkflowDefinition getModel() {
		return workflowDefinition;
	}

	public Long getWfdId() {
		return wfdId;
	}

	public void setWfdId(Long wfdId) {
		this.wfdId = wfdId;
	}
	
	@Required
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}

	@Autowired
	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}
	
	@Required
	public void setWorkflowTypeManager(WorkflowTypeManager workflowTypeManager) {
		this.workflowTypeManager = workflowTypeManager;
	}
	
	@Required
	public void setFormViewManager(FormViewManager formManager) {
		this.formViewManager = formManager;
	}
	@Autowired
	public void setListViewManager(ListViewManager listViewManager) {
		this.listViewManager = listViewManager;
	}

	public Page<Object> getWiPage() {
		return wiPage;
	}
	
	public void setWiPage(Page<Object> wiPage) {
		this.wiPage = wiPage;
	}

	public Page<WorkflowDefinition> getWfdPage() {
		return wfdPage;
	}

	public void setWfdPage(Page<WorkflowDefinition> wfdPage) {
		this.wfdPage = wfdPage;
	}

	public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}
	
	public Long getDefCompanyId() {
		return defCompanyId;
	}

	public String getDefCreator() {
		return defCreator;
	}


	public String getSearchCdn() {
		return searchCdn;
	}

	public void setSearchCdn(String searchCdn) {
		this.searchCdn = searchCdn;
	}

	public List<WorkflowType> getTypeList() {
		return typeList;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public List<String> getTitleList() {
		return titleList;
	}

	public List<ListColumn> getDisplayField() {
		return displayField;
	}

	public WorkflowDefinition getWorkflowDefinition() {
		return workflowDefinition;
	}

	public List<WorkflowDefinitionTemplate> getTemplates() {
		return templates;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getDefSystemId() {
		return defSystemId;
	}

	public void setDefSystemId(Long defSystemId) {
		this.defSystemId = defSystemId;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getFormType() {
		return formType;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getFieldPermission() {
		return fieldPermission;
	}

	public void setFieldPermission(String fieldPermission) {
		this.fieldPermission = fieldPermission;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public WorkflowInstance getWorkflowInstance() {
		return workflowInstance;
	}

	public void setWorkflowInstance(WorkflowInstance workflowInstance) {
		this.workflowInstance = workflowInstance;
	}

	public Collection<Long> getWorkflowIds() {
		return workflowIds;
	}

	public void setWorkflowIds(Set<Long> workflowIds) {
		this.workflowIds = workflowIds;
	}

	public void setWorkflowInstances(Set<WorkflowInstance> workflowInstances) {
		this.workflowInstances = workflowInstances;
	}

	public void setWfdIds(List<Long> wfdIds) {
		this.wfdIds = wfdIds;
	}

	public void setOperates(List<String> operates) {
		this.operates = operates;
	}

	public void setSearchValues(List<String> searchValues) {
		this.searchValues = searchValues;
	}

	public List<FormView> getForms() {
		return forms;
	}
	private void addActionSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}

	public String getFormCode() {
		return formCode;
	}
	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<WorkflowDefinition> getDefinitions() {
		return definitions;
	}

	public Long getSysId() {
		return sysId;
	}

	public void setSysId(Long sysId) {
		this.sysId = sysId;
	}

	public String getVertionType() {
		return vertionType;
	}

	public void setVertionType(String vertionType) {
		this.vertionType = vertionType;
	}
	public void setDefinitionCode(String definitionCode) {
		this.definitionCode = definitionCode;
	}
	
	public String getDefinitionCode() {
		return definitionCode;
	}

	public List<String> getEnNames() {
		return enNames;
	}

	public void setEnNames(List<String> enNames) {
		this.enNames = enNames;
	}

	public List<String> getChNames() {
		return chNames;
	}

	public void setChNames(List<String> chNames) {
		this.chNames = chNames;
	}

	public List<String> getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(List<String> dataTypes) {
		this.dataTypes = dataTypes;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public String getUrl() {
		return url;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public void setTransactorName(String transactorName) {
		this.transactorName = transactorName;
	}
	public Page<WorkflowTask> getTasks() {
		return tasks;
	}

	public void setTaskIds(List<Long> taskIds) {
		this.taskIds = taskIds;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	public List<BusinessSystem> getSystems() {
		return systems;
	}
	
}
