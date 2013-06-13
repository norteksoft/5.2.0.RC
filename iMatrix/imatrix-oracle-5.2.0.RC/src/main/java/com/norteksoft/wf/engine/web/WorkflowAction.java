package com.norteksoft.wf.engine.web;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.mms.base.FormType;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.base.enumeration.ProcessType;
import com.norteksoft.wf.base.exception.DecisionException;
import com.norteksoft.wf.base.exception.TransactorAssignmentException;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.Opinion;
import com.norteksoft.wf.engine.entity.WorkflowAttachment;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.InstanceHistoryManager;
import com.norteksoft.wf.engine.service.OfficeManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "workflow", type = "redirectAction"),
			@Result(name = "monitor", location = "workflow-definition!monitor", type = "redirectAction"),
			@Result(name = "workflowassign", location = "taskassign", type = "chain"),
			@Result(name = "workflowchoiceTache", location = "taskchoiceTache", type = "chain")
		})
public class WorkflowAction extends CrudActionSupport<WorkflowInstance> {
	private static final long serialVersionUID = 1L;
//	private static final Log log = Log.getLog(WorkflowAction.class.getName());
	private WorkflowDefinitionManager workflowDefinitionManager;
	private WorkflowInstanceManager workflowInstanceManager;
	private WorkflowRightsManager workflowRightsManager;
	private OfficeManager officeManager;
	private FormViewManager formManager;
	private Page<WorkflowInstance> workflowInstances = new Page<WorkflowInstance>(0, true);
	private List<WorkflowDefinition> processDefinitions ;
	private List<WorkflowAttachment> attachments;
	private String processId;// 流程定义的id
	private FormView form;
	private String formHtml;
	private WorkflowInstance workflowInstance;
	private String workflowId;
	private Long taskId;
	private WorkflowTask task;
	private String result;
	private String opinion;
	private boolean viewOpinion,eidtOpinion,mustOpinion;
	private String requiredFields;
	private Long documentId;
	private List<WorkflowType> typeList;
	private Long type= 0l;//流程类型id
	private Long definitionId;
	private Long wfdId;
	private Long formId;
	private Boolean submit;
	private List<FormControl> displayField;
	private Page<Object> formValues = new Page<Object>(Page.EACH_PAGE_TWENTY, true);
	private String fieldPermission; //字段的编辑权限
	private Boolean end = false;
	private List<String[]> transitionNames;
	private String transitionName;
	private String standardUrl;

	private String messageTip;
	private Page<WorkflowDefinition> wfdPage = new Page<WorkflowDefinition>(0, true);
	private WorkflowTypeManager workflowTypeManager;
	private String tree;
	private String firstTreeId;
	private String nullAssignmentException;
    private String newTransactor;
    private TaskService taskService;
    
    private TaskProcessingResult transact;
    private String formType;
    
    
    private Map<String,String> canChoiceTaches;
    private Map<String,String> canChoiceTransactor = new HashMap<String,String>();
    private UserManager userManager;
    
    private String deleteIds;
    
    @Autowired
    public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
    @Required
    public void setWorkflowTypeManager(WorkflowTypeManager workflowTypeManager) {
		this.workflowTypeManager = workflowTypeManager;
	}
	@Override
	public String delete() throws Exception {
		int deleteNum=0,notDeleteNum=0;
		String[] wfIds=deleteIds.split(",");
		for(String wfId:wfIds){
			workflowInstance=workflowInstanceManager.getWorkflowInstance(Long.valueOf(wfId));
//			workflowInstance =  workflowInstanceManager.getWorkflowInstance(wfId);
			if(workflowInstance.getProcessState()==ProcessState.UNSUBMIT||workflowRightsManager.workflowDeleteRight(workflowInstance,workflowInstance.getCurrentActivity())){
				deleteNum++;
				workflowInstanceManager.deleteWorkflowInstance(workflowInstance,true);
			}else{
				notDeleteNum++;
			}
		}
		String message = deleteNum+"个实例被删除";
		if(notDeleteNum!=0)message = message +","+notDeleteNum+"个实例没有权限删除";
		ApiFactory.getBussinessLogService().log("我发起的流程", 
				"删除实例", 
				ContextUtils.getSystemId("wf"));
		this.renderText(message);
		return null;
	}

	@Autowired
	public void setWorkflowRightsManager(
			WorkflowRightsManager workflowRightsManager) {
		this.workflowRightsManager = workflowRightsManager;
	}
	
	@Autowired
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	@Override
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log("流程汇编", 
				"发起流程页面", 
				ContextUtils.getSystemId("wf"));
		if(workflowId!=null){
			workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		}
		if(taskId!=null){
			WorkflowTask workflowTask=taskService.getTask(taskId);
			workflowId=workflowTask.getProcessInstanceId();
			workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append( "<input type=\"hidden\" name=\"dataId\" value=\"").append(workflowInstance.getDataId()).append("\"/>");
		builder.append("<input type=\"hidden\" name=\"formId\" value=\"").append(workflowInstance.getFormId()).append("\"/>");
		FormView form = formManager.getFormView(workflowInstance.getFormId());
		
		Map<String,String> parameterSettingMap = DefinitionXmlParse.getParameterSetting(workflowInstance.getProcessDefinitionId());
		String formViewUrl = parameterSettingMap.get(DefinitionXmlParse.FORM_VIEW_URL);
		
		String joinSign = StringUtils.contains(formViewUrl, "?") ? "&" : "?";
		Long systemId = workflowInstance.getSystemId();
		String code = ApiFactory.getAcsService().getSystemById(systemId).getCode();
		standardUrl = SystemUrls.getSystemUrl(code)+""+formViewUrl + joinSign + parameterSettingMap.get(DefinitionXmlParse.FORM_VIEW_URL_PARAMETER_NAME)
		+ "=" + workflowInstance.getDataId()+"&_r=1";
		if(!form.isStandardForm()){//当是自定义表单时，【任务委托监控】和swing客户端处理一致
			standardUrl = SystemUrls.getSystemUrl(code)+""+formViewUrl + joinSign + parameterSettingMap.get(DefinitionXmlParse.FORM_VIEW_URL_PARAMETER_NAME)
			+ "=" + workflowInstance.getDataId()+"&instanceId="+workflowInstance.getProcessInstanceId()+"&_r=1";
		}
		return "viewStandardForm";
		
	}
	
	/**
	 * 流程监控查看流转历史
	 * @return
	 * @throws Exception
	 */
	public String flowHistory() throws Exception {
		workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		ApiFactory.getBussinessLogService().log("流转历史标签", 
				"查看流转历史图", 
				ContextUtils.getSystemId("wf"));
		return "flowHistory";
	}
	
	public String textHistory() throws Exception {
		ApiFactory.getBussinessLogService().log("流转历史标签", 
				"查看文本流转历史", 
				ContextUtils.getSystemId("wf"));
		HttpServletResponse response = Struts2Utils.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(readScriptTemplet());
		return null;
	}
	
	//from com.norteksoft.wf.base.tags.WorkflowHistoryTag
	private String readScriptTemplet() throws Exception{
		InstanceHistoryManager instanceHistoryManager=(InstanceHistoryManager)ContextUtils.getBean("instanceHistoryManager");
		List<InstanceHistory> ihs=instanceHistoryManager.getHistorysByWorkflowId(ContextUtils.getCompanyId(), workflowId);
		for(int i=0;i<ihs.size();i++){
			InstanceHistory ih=ihs.get(i);
			Long taskId=ih.getTaskId();
			StringBuilder sb=new StringBuilder();
			if(taskId!=null){
				WorkflowTask task=taskService.getTask(ih.getTaskId());
				List<Opinion> opinions=workflowInstanceManager.getOpinions(task.getId(),task.getCompanyId());
				for(Opinion opinion:opinions){
					sb.append(opinion.getOpinion()).append(";");
				}
				if(sb.length()>0&&sb.charAt(sb.length()-1)==';'){
					sb.deleteCharAt(sb.length()-1);
				}
			}
			ih.setTransactorOpinion(sb.toString());
			String result=ih.getTransactionResult();
			if(result.contains("[")){
				String temp=result.substring(result.indexOf("[")+1,result.indexOf("]"));
				if(temp.equals("transition.approval.result.agree")){
					result=result.substring(0, result.indexOf("[")) + "[同意]" + result.substring(result.lastIndexOf("]") + 1, result.length());
					ih.setTransactionResult(result);
					ihs.set(i, ih);
				}else if (temp .equals( "transition.approval.result.disagree")){
					result=result.substring(0, result.indexOf("[")) + "[不同意]" + result.substring(result.lastIndexOf("]") + 1, result.length());
					ih.setTransactionResult(result);
					ihs.set(i, ih);
				}else if(temp.contains("_")){
					WorkflowInstance workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
					result=result.substring(0, result.indexOf("[")) + "["+workflowInstance.getProcessName()+"]" + result.substring(result.lastIndexOf("]") + 1, result.length());
					ih.setTransactionResult(result);
					ihs.set(i, ih);
				}
			}
		}
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("instanceHistory",ihs);
		root.put("sequence","序号");
		root.put("name","名称");
		root.put("history","流转操作");
		root.put("start","流程开始");
		root.put("end","流程结束");
		root.put("opinion","办理意见");
		String result =TagUtil.getContent(root, "workflow/textHistory.ftl");
		return result;
	}
	
	/**
	 * 流程监控查看任务
	 * @return
	 */
	public String monitorView(){
		workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		taskId = workflowInstance.getFirstTaskId();
		formHtml = workflowInstanceManager.getHtml(workflowInstance, null);
		//添加"列表控件"/"宏控件"等的script处理代码
		form=formManager.getFormView(workflowInstance.getFormId());
		formHtml=formManager.getFormHtml(form, formHtml,workflowInstance.getDataId(),false,false);
		fieldPermission = workflowRightsManager.getFieldPermission(false);
		ApiFactory.getBussinessLogService().log("流程监控", 
				"查看任务", 
				ContextUtils.getSystemId("wf"));
		return "monitorView";
	}
	
	/**
	 * 已办理
	 * @return
	 * @throws Exception
	 */
	public String completed() throws Exception {
		return "completed";
	}

	@Action("list")
	public String listIndex(){
		return SUCCESS;
	}
	
	/**
	 * 处理中的流程
	 */
	@Override
	@Action("workflow")
	public String list() throws Exception {
		if(workflowInstances.getPageSize()>1){
			if(definitionId != null){
				getFormDatasByDefinition(definitionId, end);
			}else if(type!=null&&type.longValue()!=0){
				if(end){
					workflowInstanceManager.listEndWorkflowInstance(workflowInstances,type,
							ContextUtils.getLoginName(),ContextUtils.getCompanyId());//
				}else{
					workflowInstanceManager.listNotEndWorkflowInstance(workflowInstances,type,
							ContextUtils.getLoginName(),ContextUtils.getCompanyId());//
				}
			}else{
				if(end){
					workflowInstanceManager.listEndWorkflowInstance(workflowInstances,
							ContextUtils.getLoginName(),ContextUtils.getCompanyId());//
				}else{
					workflowInstanceManager.listNotEndWorkflowInstance(workflowInstances,
							ContextUtils.getLoginName(),ContextUtils.getCompanyId());//
				}
			}
			ApiFactory.getBussinessLogService().log("我发起的流程", 
					"处理中的流程", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(workflowInstances));
			return null;
		}
		return SUCCESS;
	}
	
	/**
	 * 查询表单显示字段并排序、查询表单数据
	 * @param defId
	 */
	private void getFormDatasByDefinition(Long defId, boolean isEnd){
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(defId);
		FormView form=formManager.getCurrentFormViewByCodeAndVersion(definition.getFormCode(), definition.getFromVersion());
		if(!form.isStandardForm()){
			formType=FormType.DEFAULT;
//			displayField = workflowInstanceManager.getFormDatas(formValues, definition, isEnd);
			if(isEnd){
				workflowInstanceManager.listEndWorkflowInstanceByDefinitionId(workflowInstances, definitionId, ContextUtils.getCompanyId(),ContextUtils.getLoginName());
			}else{
				workflowInstanceManager.listNotEndWorkflowInstanceByDefinitionId(workflowInstances, definitionId, ContextUtils.getCompanyId(),ContextUtils.getLoginName());
			}
		}else if(form.isStandardForm()){
			formType=FormType.STANDARD;
			if(isEnd){
				workflowInstanceManager.listEndWorkflowInstanceByDefinitionId(workflowInstances, definitionId, ContextUtils.getCompanyId(),ContextUtils.getLoginName());
			}else{
				workflowInstanceManager.listNotEndWorkflowInstanceByDefinitionId(workflowInstances, definitionId, ContextUtils.getCompanyId(),ContextUtils.getLoginName());
			}
		}
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}


	/**
	 * 已完成的流程
	 */
	public String listEnd() throws Exception {
		if(type==null){
			workflowInstanceManager.listNotEndWorkflowInstance(workflowInstances,
					ContextUtils.getLoginName(),ContextUtils.getCompanyId());//
		}else{
			workflowInstanceManager.listNotEndWorkflowInstance(workflowInstances,type,
					ContextUtils.getLoginName(),ContextUtils.getCompanyId());//
		}
		ApiFactory.getBussinessLogService().log("我发起的流程", 
				"已完成的流程", 
				ContextUtils.getSystemId("wf"));
		return "listEnd";
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(StringUtils.isNotEmpty(workflowId)){
			workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		}else{
			workflowInstance = new WorkflowInstance();
		}
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public String save() throws Exception {
		Map<String,String[]> parameterMap=Struts2Utils.getRequest().getParameterMap();
		Map<String,String> resultMap  = workflowInstanceManager.save(parameterMap);
		workflowId = resultMap.get(WorkflowInstanceManager.INSTANCEID);
		task = taskService.getTask(Long.valueOf(resultMap.get(WorkflowInstanceManager.TASKID)));
		taskId = task.getId();
		workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		StringBuilder builder = new StringBuilder();
		builder.append( "<input type=\"hidden\" name=\"dataId\" value=\"").append(resultMap.get(WorkflowInstanceManager.DATAID)).append("\"/>");
		builder.append("<input type=\"hidden\" name=\"formId\" value=\"").append(formId).append("\"/>");
		formHtml = workflowInstanceManager.getHtml(workflowInstance, task);
		formHtml = builder.toString() + formHtml;
		form=formManager.getFormView(formId);
		//添加"列表控件"/"宏控件"等的script处理代码
		formHtml=formManager.getFormHtml(form, formHtml,Long.parseLong(resultMap.get(WorkflowInstanceManager.DATAID)),true,false);
		fieldPermission = workflowRightsManager.getFieldPermission(task);	
		ApiFactory.getBussinessLogService().log("我发起的流程", 
				"保存流程", 
				ContextUtils.getSystemId("wf"));
		addActionMessage("任务已保存");
		return "startProcess";
	}
	
	public String saveCustomProcess(){
		Map<String,String> resultMap  = workflowInstanceManager.saveCustomProcess(Struts2Utils.getRequest().getParameterMap());
		workflowId = resultMap.get(WorkflowInstanceManager.INSTANCEID);
		workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		taskId = workflowInstance.getFirstTaskId();
		StringBuilder builder = new StringBuilder();
		builder.append( "<input type=\"hidden\" name=\"dataId\" value=\"").append(resultMap.get(WorkflowInstanceManager.DATAID)).append("\"/>");
		builder.append("<input type=\"hidden\" name=\"formId\" value=\"").append(formId).append("\"/>");
		formHtml = workflowInstanceManager.getHtml(workflowInstance, task);
		formHtml = builder.toString() + formHtml;
		return "startCustomProcess";
	}
	
	public String submitCustomProcess() throws Exception {
//		String url = ContextUtils.getSystemCode()+"/engine/task!input.htm";
//		String url = "http://" + Struts2Utils.getRequest().getHeader("Host") +
//		Struts2Utils.getRequest().getContextPath() +
//		"/engine/task!input.htm";
		Map<String, String[]> parameterMap =  Struts2Utils.getRequest().getParameterMap();
		//捕获异常
		Map<String,String> resultMap=null;
		resultMap= workflowInstanceManager.submitCustomProcess(parameterMap);
		workflowId = resultMap.get(WorkflowInstanceManager.INSTANCEID);
		workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		task = taskService.getTask(Long.valueOf(resultMap.get(WorkflowInstanceManager.TASKID)));
		StringBuilder builder = new StringBuilder();
		builder.append( "<input type=\"hidden\" name=\"dataId\" value=\"").append(resultMap.get(WorkflowInstanceManager.DATAID)).append("\"/>");
		formHtml = workflowInstanceManager.getHtml(workflowInstance, task);
		formHtml = builder.toString() + formHtml;
		String to = null;
		result = resultMap.get(WorkflowInstanceManager.RESULT);
		ApiFactory.getBussinessLogService().log("我发起的流程", 
				"保存自由流", 
				ContextUtils.getSystemId("wf"));
		return "startCustomProcess";
	}
	
	public String completeCustomProcess() throws Exception {
		Map<String,String> resultMap  = workflowInstanceManager.saveCustomProcess(Struts2Utils.getRequest().getParameterMap());
		workflowId = resultMap.get(WorkflowInstanceManager.INSTANCEID);
		workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		taskId = workflowInstance.getFirstTaskId();
		workflowInstanceManager.completeCustomProcess(taskId,TaskProcessingResult.SUBMIT);
		ApiFactory.getBussinessLogService().log("我发起的流程", 
				"完成自由流", 
				ContextUtils.getSystemId("wf"));
		return "startCustomProcess";
	}
	
	
	@SuppressWarnings("unchecked")
	public String submit() throws Exception {
		String url = ContextUtils.getSystemCode()+"/engine/task!input.htm";
		Map<String, String[]> parameterMap =  Struts2Utils.getRequest().getParameterMap();
		//捕获异常
		Map<String,Object> resultMap=null;
		try{
			resultMap= workflowInstanceManager.submit(parameterMap,url,transitionName,newTransactor);
		}catch(DecisionException de){
			transitionNames=de.getTransitionNames();
			messageTip=de.getMessage();
			messageTip=getText(messageTip);
			return this.startProcess();
		}catch(TransactorAssignmentException je){
			nullAssignmentException="下一环节没有办理人，请指定！";
			return  this.startProcess();
		}
		workflowId = resultMap.get(WorkflowInstanceManager.INSTANCEID).toString();
		workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		task = taskService.getTask(Long.valueOf(resultMap.get(WorkflowInstanceManager.TASKID).toString()));
		taskId = task.getId();
		StringBuilder builder = new StringBuilder();
		builder.append( "<input type=\"hidden\" name=\"dataId\" value=\"").append(resultMap.get(WorkflowInstanceManager.DATAID)).append("\"/>");
		formHtml = workflowInstanceManager.getHtml(workflowInstance, task);
		formHtml = builder.toString() + formHtml;
		form=formManager.getFormView(formId);
		//添加"列表控件"/"宏控件"等的script处理代码
		formHtml=formManager.getFormHtml(form, formHtml,Long.parseLong(resultMap.get(WorkflowInstanceManager.DATAID).toString()),false,false);
		fieldPermission = workflowRightsManager.getFieldPermission(false);	
		String to = null;
		CompleteTaskTipType completeTaskTipType = (CompleteTaskTipType)resultMap.get(WorkflowInstanceManager.RESULT);
		if(completeTaskTipType.equals(CompleteTaskTipType.OK)){
			addActionMessage(completeTaskTipType.getContent());
			submit = true;
			to = "startProcess";
		}else if(completeTaskTipType==CompleteTaskTipType.RETURN_URL){//如果需要指定办理人
			addActionMessage("请选择办理人");
			to = "workflowassign";
		}else if(completeTaskTipType==CompleteTaskTipType.TACHE_CHOICE_URL){//如果需要选择环节
			addActionMessage("请选择环节");
			canChoiceTaches = completeTaskTipType.getCanChoiceTaches();
			to = "workflowchoiceTache";
		}else if(completeTaskTipType==CompleteTaskTipType.SINGLE_TRANSACTOR_CHOICE){
			User temp;
			for(String tran : completeTaskTipType.getCanChoiceTransactor()){
				temp = userManager.getUserByLoginName(tran);
				if(temp!=null)canChoiceTransactor.put(tran, temp.getName());
			}
			to = "singleTransactorChoice";
		}else if(completeTaskTipType.equals(CompleteTaskTipType.MESSAGE)){
			to = null;
			addActionMessage(completeTaskTipType.getContent());
		}
		ApiFactory.getBussinessLogService().log("我发起的流程", 
				"提交任务", 
				ContextUtils.getSystemId("wf"));
		return to;
	}
	public String completeChoiceTransactor() throws Exception {
		taskService.completeChoiceTransactor(taskId,newTransactor);	
		ApiFactory.getBussinessLogService().log("我发起的流程", 
				"指定办理人", 
				ContextUtils.getSystemId("wf"));
		addActionMessage("办理人指定成功！");
		return "singleTransactorChoice";
	}
	
	/**
	 * 选择办理人
	 */
	public String choseTransactor() throws Exception {
		nullAssignmentException="下一环节没有办理人，请指定！";
		return "assignmentTree";
	}
	
	/**
	 * 查询最新流程定义
	 */
	public String listProcessDefinition() throws Exception {
		processDefinitions = workflowDefinitionManager.getActiveDefinition();
		return "processList";
	}

	
	public void prepareInputForm() throws Exception {
		
	}
	
	/**
	 * 转向需我办理的流程
	 */
	public String needDo() throws Exception {
//		taskManager.getTasksByUser(tasks, 
//				ContextUtils.getCompanyId(), 
//				ContextUtils.getLoginName());
		return "needdo";
	} 

	/**
	 * 转向办理页面
	 */
	public String toApprove() throws Exception {
//		task = taskService.getWorkflowTask(taskId);
//		workflowInstance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
//		workflowId = workflowInstance.getId();
//		formHtml = taskService.getFormHtml(task);
//		viewOpinion = PermissionsParse.viewOpinion(workflowInstance.getProcessDefinitionId(),task.getName(),ContextUtils.getLoginName());
//		eidtOpinion = PermissionsParse.editOpinion(workflowInstance.getProcessDefinitionId(),task.getName(),ContextUtils.getLoginName());
//		if(eidtOpinion){
//			mustOpinion = PermissionsParse.mustOpinion(workflowInstance.getProcessDefinitionId(),task.getName(),ContextUtils.getLoginName());
//		}
		return "approve";
	}
	
	/**
	 * 删除正文
	 */
	public String deleteText() throws Exception{
		officeManager.deleteText(documentId);
		ApiFactory.getBussinessLogService().log("我发起的流程", 
				"删除正文", 
				ContextUtils.getSystemId("wf"));
		renderText("ok");
		return null;
	}
	
	/**
	 * 流程汇编
	 * @return
	 * @throws Exception
	 */
	public String collection() throws Exception{
		if(wfdPage.getPageSize()>1){
			if(type==0l){
				workflowDefinitionManager.getEnableWfDefinitions(wfdPage);
			}else{
				workflowDefinitionManager.getEnableWfDefinitions(wfdPage,type);
			}
			ApiFactory.getBussinessLogService().log("流程汇编", 
					"流程汇编列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(wfdPage));
			return null;
		}
		return "collection";
	}
	
	public String createTree() throws Exception{
		String[] rs = processTree();
		tree = rs[0];
		firstTreeId = rs[1];
		renderText(tree.toString());
		return null;
	}
	
	private String[] processTree(){
		StringBuilder tree = new StringBuilder("[ ");
		List<WorkflowType> wfTypes = workflowTypeManager.getAllWorkflowType();
		boolean isFirstNode = true;
		String firstId = "";
		for(WorkflowType wft : wfTypes){
			List<WorkflowDefinition> definitions = workflowDefinitionManager.getWfDefinitionsByType(getCompanyId(), wft.getId());
			if(isFirstNode){
				if(definitions==null || definitions.isEmpty()){
					tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFDTYPE_" + wft.getId(), "",wft.getName())).append(",");
				}else{
					tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFDTYPE_" + wft.getId(), "open",wft.getName(), processDefs(definitions))).append(",");
				}
				isFirstNode = false;
				firstId = String.valueOf(wft.getId());
			}else{
				tree.append(JsTreeUtils.generateJsTreeNodeDefault("WFDTYPE_" + wft.getId(), "", wft.getName(), processDefs(definitions))).append(",");
			}
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		return new String[]{tree.toString(), firstId};
	}
	
	public String processDefs(List<WorkflowDefinition> definitions){
		StringBuilder subNodes = new StringBuilder();
		
		for(WorkflowDefinition wfd : definitions){
			subNodes.append(JsTreeUtils.generateJsTreeNodeDefault("WFDID_" + wfd.getId(), "", wfd.getName())).append(",");
		}
		JsTreeUtils.removeLastComma(subNodes);
		return subNodes.toString();
	}
	
	
	/**
	 * 发起流程
	 * 
	 * @return
	 * @throws Exception
	 * @author liudongxia
	 */
	public String startProcess() throws Exception {
		ApiFactory.getBussinessLogService().log("流程汇编", 
				"发起流程", 
				ContextUtils.getSystemId("wf"));
		WorkflowDefinition wfd = workflowDefinitionManager.getWfDefinition(wfdId);
		processId=wfd.getProcessId();
		if(ProcessType.PREDEFINED_PROCESS.equals(wfd.getProcessType())){
			if(workflowInstanceManager.canStartTask(ContextUtils.getLoginName(), wfd)){
				form= this.formManager.getCurrentFormViewByCodeAndVersion(wfd.getFormCode(), wfd.getFromVersion());
				if(!form.isStandardForm()){
					formId = form.getId();
					definitionId=wfd.getId();
					StringBuilder builder = new StringBuilder();
					builder.append("<input type=\"hidden\" name=\"formId\" value=\"").append(formId).append("\"/>")
					.append("<input type=\"hidden\"  name=\"processId\" value=\"").append(processId).append("\"/>");
					String firstTaskName = workflowInstanceManager.getFirstTaskName(processId);
					formHtml = workflowInstanceManager.initHtml(form,firstTaskName,wfd.getProcessId(),form.getHtml());
					formHtml = builder.toString() + formHtml;
					//添加"列表控件"/"宏控件"等的script处理代码
					formHtml=formManager.getFormHtml(form, formHtml,null,true,false);
					fieldPermission = workflowRightsManager.getFieldPermissionNotStarted(wfd);
					
					return "startProcess";
				}else if(form.isStandardForm()){
					Map<String,String> parameterMap = DefinitionXmlParse.getParameterSetting(wfd.getProcessId());
					String processStartUrl = parameterMap.get(DefinitionXmlParse.PROCESS_START_URL);
					
					String joinSign = StringUtils.contains(processStartUrl, "?") ? "&" : "?";
					
					standardUrl = processStartUrl + joinSign + parameterMap.get(DefinitionXmlParse.PROCESS_START_URL_PARAMETER_NAME)
								+ "=" + parameterMap.get(DefinitionXmlParse.PROCESS_START_URL_PARAMETER_VALUE);///
					return "standardFormStart";
				}else{
					addActionMessage("表单类型错误");
					return collection();
				}
			}else{
				addActionMessage("您不能发起该流程");
				return collection();
			}
		}else{
			FormView form= this.formManager.getCurrentFormViewByCodeAndVersion(wfd.getFormCode(), wfd.getFromVersion());
			formId = form.getId();
			StringBuilder builder = new StringBuilder();
			builder.append("<input type=\"hidden\" name=\"formId\" value=\"").append(formId).append("\"/>")
			.append("<input type=\"hidden\"  name=\"processId\" value=\"").append(processId).append("\"/>");
			formHtml = builder.toString() + form.getHtml();
			return "startCustomProcess";
		}
	}
	
	
	public WorkflowInstance getModel() {
		return workflowInstance;
	}
	

	@Required
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}

	@Required
	public void setFormViewManager(FormViewManager formManager) {
		this.formManager = formManager;
	}
	@Required
	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}
	@Required
	public void setOfficeManager(OfficeManager officeManager) {
		this.officeManager = officeManager;
	}

	public List<WorkflowDefinition> getProcessDefinitions() {
		return processDefinitions;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public FormView getFormView() {
		return form;
	}

	public void setFormView(FormView form) {
		this.form = form;
	}


	public Page<WorkflowInstance> getWorkflowInstances() {
		return workflowInstances;
	}

	public void setWorkflowInstances(Page<WorkflowInstance> workflowInstances) {
		this.workflowInstances = workflowInstances;
	}

	public WorkflowInstance getWorkflowInstance() {
		return workflowInstance;
	}

	public void setWorkflowInstance(WorkflowInstance workflowInstance) {
		this.workflowInstance = workflowInstance;
	}


	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getFormHtml() {
		return formHtml;
	}

	public void setFormHtml(String formHtml) {
		this.formHtml = formHtml;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public WorkflowTask getTask() {
		return task;
	}

	public void setTask(WorkflowTask task) {
		this.task = task;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public boolean isViewOpinion() {
		return viewOpinion;
	}

	public void setViewOpinion(boolean viewOpinion) {
		this.viewOpinion = viewOpinion;
	}

	public boolean isEidtOpinion() {
		return eidtOpinion;
	}

	public void setEidtOpinion(boolean eidtOpinion) {
		this.eidtOpinion = eidtOpinion;
	}

	public boolean isMustOpinion() {
		return mustOpinion;
	}

	public void setMustOpinion(boolean mustOpinion) {
		this.mustOpinion = mustOpinion;
	}

	public String getRequiredFields() {
		return requiredFields;
	}

	public List<WorkflowAttachment> getAttachments() {
		return attachments;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}
	
	public Long getSystemId(){
		return ContextUtils.getSystemId();
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public List<WorkflowType> getTypeList() {
		return typeList;
	}

	public Long getDefinitionId() {
		return definitionId;
	}

	public Long getWfdId() {
		return wfdId;
	}

	public void setWfdId(Long wfdId) {
		this.wfdId = wfdId;
	}

	public List<FormControl> getDisplayField() {
		return displayField;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public Page<Object> getFormValues() {
		return formValues;
	}

	public void setFormValues(Page<Object> formValues) {
		this.formValues = formValues;
	}

	public Boolean getSubmit() {
		return submit;
	}

	public String getFieldPermission() {
		return fieldPermission;
	}

	public void setEnd(Boolean end) {
		this.end = end;
	}

	public Boolean getEnd() {
		return end;
	}

	public List<String[]> getTransitionNames() {
		return transitionNames;
	}

	public String getTransitionName() {
		return transitionName;
	}

	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;
	}


	public String getMessageTip() {
		return messageTip;
	}

	public void setMessageTip(String messageTip) {
		this.messageTip = messageTip;
	}


	public Page<WorkflowDefinition> getWfdPage() {
		return wfdPage;
	}

	public void setWfdPage(Page<WorkflowDefinition> wfdPage) {
		this.wfdPage = wfdPage;
	}

	public String getTree() {
		return tree;
	}

	public void setTree(String tree) {
		this.tree = tree;
	}

	public String getNullAssignmentException() {
		return nullAssignmentException;
	}

	public void setNullAssignmentException(String nullAssignmentException) {
		this.nullAssignmentException = nullAssignmentException;
	}

	public String getNewTransactor() {
		return newTransactor;
	}

	public void setNewTransactor(String newTransactor) {
		this.newTransactor = newTransactor;
	}

	public String getStandardUrl() {
		return standardUrl;
	}

	public void setTransact(TaskProcessingResult transact) {
		this.transact = transact;
	}

	public String getFormType() {
		return formType;
	}
	public void setCanChoiceTaches(Map<String, String> canChoiceTaches) {
		this.canChoiceTaches = canChoiceTaches;
	}

	public Map<String, String> getCanChoiceTaches() {
		return canChoiceTaches;
	}
	public FormView getForm() {
		return form;
	}
	public Map<String, String> getCanChoiceTransactor() {
		return canChoiceTransactor;
	}
	public String getDeleteIds() {
		return deleteIds;
	}
	public void setDeleteIds(String deleteIds) {
		this.deleteIds = deleteIds;
	}
}	
