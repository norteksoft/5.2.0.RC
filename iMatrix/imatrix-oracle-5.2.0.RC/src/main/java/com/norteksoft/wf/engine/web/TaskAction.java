package com.norteksoft.wf.engine.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.web.authorization.JsTreeUtil1;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskSource;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.entity.TaskSetting;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.base.exception.DecisionException;
import com.norteksoft.wf.base.exception.TransactorAssignmentException;
import com.norteksoft.wf.engine.entity.Opinion;
import com.norteksoft.wf.engine.entity.Temp;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "task", type = "redirectAction"),
	@Result(name = "toAssignTransactor", location = "task!assignTransactor", type = "redirectAction"),
	@Result(name = "assign", location = "task-assign.jsp", type = "dispatcher"),
	@Result(name = "choiceUrl", location = "task-choiceUrl.jsp", type = "dispatcher")
	})
public class TaskAction extends CrudActionSupport<WorkflowTask>{
	
	private Log log = LogFactory.getLog(TaskAction.class);
	private static final long serialVersionUID = 1L;
	private static final String URGENCY = "urgency";
	private static final String SAVE = "save";
	private WorkflowInstanceManager workflowInstanceManager;
	private WorkflowDefinitionManager workflowDefinitionManager;
	private WorkflowRightsManager workflowRightsManager;
	private FormViewManager formViewManager;
	private AcsUtils acsUtils;
	private Long taskId;
	private String retrieveResult;
	private WorkflowTask  task;
	private TaskProcessingResult transact;
	private String formHtml;
	private String[] targetNames;
	private String[] targetTransactors; //修改的目标办理人
	private Map<String[], List<String[]>> candidates;
	private String requiredFields;
	private String workflowId;
	private WorkflowInstance workflowInstance;
	private List<Opinion> opinions ;
	private String opinion;
	private Long companyId; 
	private List<Temp> temps;
	private List<WorkflowTask> taskList;
	private List<String> transactors = new ArrayList<String>();
	private List<Long> ids = new ArrayList<Long>();
	private String processId;
	private Boolean view=false;  //查看意见的权限 
	
	private Boolean edit=false; //编辑意见的权限
 
	private Boolean must=false; //意见必填
	
	private String username;
	
	private String rtxSessionKey;
	
	private String rtxsIp;
	
	private Long dataId;
	private Long formId;
	
	private String fieldPermission; //字段的编辑权限
	
	private String transactor;
	
	private String backto;
	private List<String> canBackTo;
	
	private boolean moreTransactor;
	
	private Map<String,String> canChoiceTaches;
	
	private Map<String,String> canChoiceTransactor = new HashMap<String,String>();
    private UserManager userManager;
	
	private String transitionName;
	private String newTransactor;
	private String nullAssignmentException;
	 
	private Long wfdId;
	 
	private List<String[]> transitionNames;
	private String messageTip;
	 
	private String canChoiceTransacators="";//登录名,用户名;登录名,用户名;登录名,用户名;...的格式
	private String position;//流程监控或者"流程定义/流程监控"
	private String definitionCode;//流程定义编码
	private Long type = 0l;//流程类型id
	private Integer transactorNum=0;//办理人数目
	private Long instanceId;
	private boolean forkTask;//当前任务是否是并发任务 
	private boolean hasActivitySubProcess;//当前环节是否是子流程
	private String instanceIds;
	private String workflowIds;
	 
	@Autowired
    public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	@Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
	
	public void setBackto(String backto) {
		this.backto = backto;
	}
	public String getBackto() {
		return backto;
	}
	public List<String> getCanBackTo() {
		return canBackTo;
	}
	
	/**
	 * 环节跳转/选择环节
	 * @return
	 * @throws Exception
	 */
	public String backView() throws Exception{
		workflowInstance=workflowInstanceManager.getWorkflowInstance(instanceId);
		if(workflowInstance!=null)wfdId=workflowInstance.getWorkflowDefinitionId();
		workflowId = workflowInstance.getProcessInstanceId();
		boolean isForkTask = taskService.isForkTask(workflowId);
		if(!isForkTask){//当前任务是分支汇聚任务时不让跳转
			canBackTo = taskService.getTaskNames(workflowId);
		}else{
			canBackTo = new ArrayList<String>();
		}
		ApiFactory.getBussinessLogService().log("流程监控", 
				"环节跳转选择环节页面", 
				ContextUtils.getSystemId("wf"));
		return "goback";
	}
	
	/**
	 * 环节跳转功能
	 * @return
	 * @throws Exception
	 */
	public String goback() throws Exception{
		String msg = "任务跳转成功";
		try {
			CompleteTaskTipType result=null;
			workflowInstance=workflowInstanceManager.getWorkflowInstance(workflowId);
			result=taskService.taskJump(workflowInstance, backto,transactors,null);
			switch(result){
			case OK:	
				msg="OK";
				break;
			case RETURN_URL:
				msg="RETURN_URL";
				break;
			case SINGLE_TRANSACTOR_CHOICE:
				User temp;
				for(String tran : result.getCanChoiceTransactor()){
					temp = userManager.getUserByLoginName(tran);
					if(temp!=null)canChoiceTransacators=canChoiceTransacators+tran+","+temp.getName()+";";
				}
				msg="SINGLE_TRANSACTOR_CHOICE";
			case MESSAGE:
				msg=result.getContent();
			}
		}catch (Exception e) {
			PropUtils.getExceptionInfo(e);
			msg="环节跳转失败";
		}
		ApiFactory.getBussinessLogService().log("流程监控", 
				"执行环节跳转", 
				ContextUtils.getSystemId("wf"));
		renderText(msg+"="+canChoiceTransacators);
		return null;
	}
	/**
	 * 批量环节跳转/选择环节
	 * @return
	 * @throws Exception
	 */
	@Action("task-volumeBackView")
	public String volumeBackView() throws Exception{
		String[] str=instanceIds.split(",");
		workflowIds="";
		for(String s:str){
			workflowInstance=workflowInstanceManager.getWorkflowInstance(Long.valueOf(s));
			if(workflowInstance!=null)wfdId=workflowInstance.getWorkflowDefinitionId();
			workflowId = workflowInstance.getProcessInstanceId();
			boolean isForkTask = taskService.isForkTask(workflowId);
			if(!isForkTask){//当前任务是分支汇聚任务时不让跳转
				List<String> temp = taskService.getTaskNames(workflowId);
				if(temp!=null&&temp.size()>0){
					if(StringUtils.isNotEmpty(workflowIds)){
						workflowIds+=",";
					}
					canBackTo=temp;
					workflowIds+=workflowId;
				}
			}else{
				break;
			}
		}
		if(canBackTo==null){
			canBackTo = new ArrayList<String>();
		}
		ApiFactory.getBussinessLogService().log("流程监控", 
				"批量环节跳转选择环节页面", 
				ContextUtils.getSystemId("wf"));
		return SUCCESS;
	}
	/**
	 *  批量环节跳转功能
	 * @return
	 * @throws Exception
	 */
	@Action("task-volumeBack")
	public String volumeBack() throws Exception{
		String msg = "任务跳转成功";
		try {
			CompleteTaskTipType result=null;
			String[] wfids=workflowIds.split(",");
			for(String workflowId:wfids){
				workflowInstance=workflowInstanceManager.getWorkflowInstance(workflowId);
				result=taskService.taskJump(workflowInstance, backto,transactors,"volumeBack");
			}
			switch(result){
			case OK:	
				msg="OK";
				break;
			case RETURN_URL:
				msg="RETURN_URL";
				break;
			case MESSAGE:
				msg=result.getContent();
			}
		}catch (Exception e) {
			PropUtils.getExceptionInfo(e);
			msg="环节跳转失败";
		}
		ApiFactory.getBussinessLogService().log("流程监控", 
				"执行批量环节跳转", 
				ContextUtils.getSystemId("wf"));
		renderText(msg);
		return null;
	}
	/**
	 * 环节跳转选择办理人页面
	 * 上一环节办理人指定
	 * @return
	 * @throws Exception
	 */
	public String taskJumpAssignTransactor()throws Exception {
		return "assignTransactor";
	}
	/**
	 * 环节跳转选择办理人页面
	 * 选择办理人/条件选择/人工选择办理人页面
	 * @return
	 * @throws Exception
	 */
	public String taskJumpChoiceTransactor()throws Exception {
		String[] infos=canChoiceTransacators.split(";");
		for(String info:infos){
			String[] strs=info.split(",");
			canChoiceTransactor.put(strs[0], strs[1]);
		}
		return "taskJumpChoiceTransactor";
	}
	
	/**
	 * 批量环节跳转选择办理人页面
	 * 上一环节办理人指定
	 * @return
	 * @throws Exception
	 */
	@Action("task-taskJumpAssignTransactorVolume")
	public String taskJumpAssignTransactorVolume()throws Exception {
		return "task-assignTransactorVolume";
	}
	
	/**
	 * 意见
	 * @return
	 * @throws Exception
	 */
	public String opinion() throws Exception {
		//workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		
		//authority(workflowInstance.getProcessDefinitionId(),task.getName());
		
		//if(view){
			//opinions = workflowInstanceManager.getOpinionsByInstanceId(workflowInstance.getProcessInstanceId(),companyId);
		//}
		if(taskId==null){
			view = true;
		}else{
			task = taskService.getWorkflowTask(taskId);
			if(task.getActive()==2){
				edit = false;
			}
		}
		workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		opinions=workflowInstanceManager.getOpinions(taskId, task.getCompanyId());
		if(workflowRightsManager.mustOpinionRight(task) && (opinions==null || opinions.size()<=0) ){
			must=true;
		}else{
			must=false;
		}
		companyId = ContextUtils.getCompanyId();	
		ApiFactory.getBussinessLogService().log("流程汇编", 
				"意见列表", 
				ContextUtils.getSystemId("wf"));
		return "opinion";
	}
	
	/**
	 * 保存审批结果
	 */
	public String saveOpinion() throws Exception { 
		workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		Opinion opi = new Opinion();
		opi.setTransactor(ContextUtils.getUserName());
		opi.setCreatedTime(new Date());
		opi.setOpinion(opinion);
		opi.setWorkflowId(workflowInstance.getProcessInstanceId());
		opi.setCompanyId(ContextUtils.getCompanyId());
		opi.setTaskId(taskId);
		workflowInstanceManager.saveOpinion(opi);
		ApiFactory.getBussinessLogService().log("流程汇编", 
				"保存意见", 
				ContextUtils.getSystemId("wf"));
		return opinion();
	}
	
	public String editOpinion(){
		return "editOpinion";
	}
	
	/**
	 * 我的任务列表(未完成)
	 */
	@Override
	public String list() throws Exception {
		ApiFactory.getBussinessLogService().log("工作流平台", 
				"我的任务列表(未完成)列表", 
				ContextUtils.getSystemId("wf"));
		return SUCCESS;
	}
	
	/**
	 * 我的任务列表(已完成)
	 */
	public String completed() throws Exception{
		ApiFactory.getBussinessLogService().log("工作流平台", 
				"我的任务列表(已完成)列表", 
				ContextUtils.getSystemId("wf"));
		return "completed";
	}

	private TaskService taskService;
	
	@Required
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	/**
	 * 处理任务
	 */
	@Override
	public String input() throws Exception {
		log.debug("*** input 处理任务方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("task:").append(task)
			.append("]").toString());
		taskService.updateTaskIsRead(task);
		workflowId = task.getProcessInstanceId();
		workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		opinions=workflowInstanceManager.getOpinions(taskId, task.getCompanyId());
		if(workflowRightsManager.mustOpinionRight(task) && (opinions==null || opinions.size()<=0) ){
			must=true;
		}
		StringBuilder builder = new StringBuilder();
		builder.append( "<input type=\"hidden\" name=\"dataId\" value=\"").append(workflowInstance.getDataId()).append("\"/>");
		formHtml = workflowInstanceManager.getHtml(workflowInstance, task);
		formHtml = builder.toString() + formHtml;
		if(TaskState.COMPLETED.getIndex().equals(task.getActive())||TaskState.CANCELLED.getIndex().equals(task.getActive())){
			//列表控件控制
			formHtml= workflowInstanceManager.getFormHtml(workflowInstance, formHtml,workflowInstance.getDataId(),false,false);
			fieldPermission = workflowRightsManager.getFieldPermission(false);
		}else if(TaskState.WAIT_CHOICE_TACHE.getIndex().equals(task.getActive())){
			canChoiceTaches = taskService.isNeedChoiceTache(task).getCanChoiceTaches();
			return choiceTache();
		}else{
			//列表控件控制
			formHtml= workflowInstanceManager.getFormHtml(workflowInstance, formHtml,workflowInstance.getDataId(),true,false);
			fieldPermission = workflowRightsManager.getFieldPermission(task);
		}
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"任务办理页面", 
				ContextUtils.getSystemId("wf"));
		log.debug("*** input 处理任务方法结束");
		return INPUT;
	}
	
	/*
	 * 领取任务
	 * 
	 */
	public String receive() throws Exception {
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"领取任务", 
				ContextUtils.getSystemId("wf"));
		return  this.renderText(taskService.receive(taskId));
	}
	
	/*
	 * 放弃领取的任务
	 */
	public String abandonReceive() throws Exception {
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"放弃领取的任务", 
				ContextUtils.getSystemId("wf"));
		return  this.renderText(taskService.abandonReceive(taskId));
	}

	/**
	 * 查看已完成任务
	 */
	public String view() throws Exception { 
		log.debug("*** view 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("task:").append(task)
			.append("]").toString());
		return preView("view");
	}
	
	private String preView(String operate){
		log.debug("*** preView 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("task:").append(task)
			.append("]").toString());
		workflowInstance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		workflowId = task.getProcessInstanceId();
		StringBuilder builder = new StringBuilder();
		builder.append( "<input type=\"hidden\" name=\"dataId\" value=\"").append(workflowInstance.getDataId()).append("\"/>");
		FormView form = formViewManager.getFormView(workflowInstance.getFormId());
		formHtml =workflowInstanceManager.setValueForHtml(workflowInstance, form,form.getHtml()); 
		formHtml = builder.toString() + formHtml;
		if(SAVE.equals(operate)){
			formHtml= workflowInstanceManager.getFormHtml(workflowInstance, formHtml,workflowInstance.getDataId(),true,false);
			fieldPermission = workflowRightsManager.getFieldPermission(task);
		}else{
			formHtml= workflowInstanceManager.getFormHtml(workflowInstance, formHtml,workflowInstance.getDataId(),false,false);
			fieldPermission = workflowRightsManager.getFieldPermission(false);
		}
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"查看已完成任务", 
				ContextUtils.getSystemId("wf"));
		log.debug("*** preView 方法结束");
		return "view";
	}
	
	public void prepareSaveForm() throws Exception {
		if(taskId != null){
			task = taskService.getWorkflowTask(taskId);
		}else {
			task = new WorkflowTask();
		}
	}
	
	/**
	 * 保存表单
	 */
	public String saveForm() throws Exception {
		workflowInstanceManager.saveFormData(taskId);
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"保存表单", 
				ContextUtils.getSystemId("wf"));
		this.addSuccessMessage("任务已保存");
		return input();
	}
	
	/**
	 * 只保存数据，与流程无关
	 * @return
	 * @throws Exception
	 */
	public String savaData() throws Exception {
		dataId = workflowInstanceManager.savaData();
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"保存数据", 
				ContextUtils.getSystemId("wf"));
		return null;
	}
	

	/**
	 * 提交表单
	 */
	public String submit() throws Exception{
		log.debug("*** submit 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("taskId:").append(taskId)
			.append("]").toString());
		
		task = taskService.getWorkflowTask(taskId);
		String to = "";
		CompleteTaskTipType result = workflowInstanceManager.submitForm(task);
		switch(result){
			case OK:	
				this.addSuccessMessage(result.getContent());
				to = input();
				break;
			case MESSAGE:	
				this.addErrorMessage(result.getContent());
				to = INPUT;
				break;
			case RETURN_URL:
				to = assignTransactor();
				break;
			case TACHE_CHOICE_URL:
				canChoiceTaches = result.getCanChoiceTaches();
				to = choiceTache();
				break;
		}
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"提交表单", 
				ContextUtils.getSystemId("wf"));
		return to;
	}
	public void prepareChoiceTache() throws Exception {
		this.prepareModel();
	}
	@Action(value = "taskchoiceTache")
	public String choiceTache() throws Exception{
		if(canChoiceTaches==null){
			canChoiceTaches = taskService.isNeedChoiceTache(task).getCanChoiceTaches();
		}
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"选择环节页面", 
				ContextUtils.getSystemId("wf"));
		return "choiceUrl";
	}
	
	public void prepareSaveChoice() throws Exception {
		this.prepareModel();
	}
	
	public String saveChoice() throws Exception{
		CompleteTaskTipType completeTaskTipType = taskService.completeTacheChoice(taskId,transitionName);
		this.addSuccessMessage(completeTaskTipType.getContent());
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"完成选择环节", 
				ContextUtils.getSystemId("wf"));
		return input();
	}
	
	
	public void prepareChoiceTachePop() throws Exception {
		this.prepareModel();
	}
	public String choiceTachePop() throws Exception{
		if(canChoiceTaches==null){
			canChoiceTaches = taskService.isNeedChoiceTache(task).getCanChoiceTaches();
		}
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"弹出选择环节页面", 
				ContextUtils.getSystemId("wf"));
		return "choiceUrlPop";
	}
	public void prepareSaveChoicePop() throws Exception {
		this.prepareModel();
	}
	
	public String saveChoicePop() throws Exception{
		CompleteTaskTipType completeTaskTipType = taskService.completeTacheChoice(taskId,transitionName);
		String to = "";
		switch(completeTaskTipType){
		case OK:	
			to = "OK:"+completeTaskTipType.getContent();
			break;
		case MESSAGE:	
			to = "MS:"+completeTaskTipType.getContent();
			break;
		case RETURN_URL:
			String url = completeTaskTipType.getContent();
			if(url.equals(TaskService.DEFAULT_URL)) url = url + "?taskId="+task.getId();
			to = "RU:"+url;
			break;
		case TACHE_CHOICE_URL:
			to = "TC:"+completeTaskTipType.getContent();
			break;
		}
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"完成选择环节任务", 
				ContextUtils.getSystemId("wf"));
		this.renderText(to);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String start() throws Exception{
		log.debug("*** start 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("processId:").append(processId)
			.append("]").toString());
		
		WorkflowDefinition wdf = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		String wfdName = wdf.getName();
		String url = Struts2Utils.getRequest().getRequestURL().toString().replaceFirst("task!start", "task!input");
		Map<String,String> urlMap = new HashMap<String,String>();
		urlMap.put(wfdName, url);
		Integer priority = 6;
		Map<String, String[]> parameterMap =  Struts2Utils.getRequest().getParameterMap();
		String[] urgency = parameterMap.get(URGENCY);
		if(urgency!=null&&urgency.length>0&&StringUtils.isNotEmpty(urgency[0])) priority = Integer.valueOf(urgency[0]);
		String to = "";
		CompleteTaskTipType result = workflowInstanceManager.startAndSubmitWorkflow(processId, urlMap,priority,null);
		switch(result){
		case OK:	
			this.addSuccessMessage(result.getContent());
			to = null;
			break;
		case MESSAGE:	
			this.addErrorMessage(result.getContent());
			to = null;
			break;
		case RETURN_URL:
			to = assignTransactor();
			break;
		}
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"发起流程", 
				ContextUtils.getSystemId("wf"));
		return to;
	}
	
	
	/**
	 * 保存任务
	 */
	@Override
	public String save() throws Exception {
		log.debug("*** save 办理任务方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("task:").append(task)
			.append("]").toString());
			ApiFactory.getBussinessLogService().log("工作流管理", 
					"保存任务", 
					ContextUtils.getSystemId("wf"));
			//如果为分发环节,指定被分发人
			if(TaskProcessingMode.TYPE_DISTRIBUTE.toString().equals(task.getProcessingMode().toString())){
				return "distribute";
			}
			//如果为交办环节,指定被交办人
			if(task.getActive()==0 && TaskProcessingMode.TYPE_ASSIGN.toString().equals(task.getProcessingMode().toString())){
				moreTransactor = true;
				workflowInstanceManager.saveData(task, null);
				return "assignTree";
			}
			//如果已经办理但没有指定办理人
			if(task.getActive()==1&& task.getProcessingMode()!=TaskProcessingMode.TYPE_ASSIGN){
				return assignTransactor();
			}
			String to = "";
			try{
				CompleteTaskTipType result = taskService.completeWorkflowTaskAndSaveData(task, transact,TaskSetting.getTaskSettingInstance().setTransitionName(transitionName).setAssignmentTransactors(newTransactor));
				switch(result){
				case OK:	
					this.addSuccessMessage(result.getContent());
					to = input();
					break;
				case MESSAGE:	
					this.addErrorMessage(result.getContent());
					to = INPUT;
					break;
				case RETURN_URL:
					to = assignTransactor();
					break;
				case TACHE_CHOICE_URL:
					addActionMessage("请选择环节");
					canChoiceTaches = result.getCanChoiceTaches();
					to = choiceTache();
					break;
				case SINGLE_TRANSACTOR_CHOICE:
					User temp;
					for(String tran : result.getCanChoiceTransactor()){
						temp = userManager.getUserByLoginName(tran);
						if(temp!=null)canChoiceTransactor.put(tran, temp.getName());
					}
					moreTransactor=CompleteTaskTipType.SINGLE_TRANSACTOR_CHOICE.getContent().equals("true");
					to = "singleTransactorChoice";
				}
			}catch (TransactorAssignmentException e) {
				nullAssignmentException="下一环节没有办理人，请指定！";
				return  "assignmentTree";
			}catch(DecisionException de){
				transitionNames=de.getTransitionNames();
				messageTip=de.getMessage();
				messageTip=getText(messageTip);
				return input();
			}
			return to;

	}
	
	public void prepareCompleteChoiceTransactor() throws Exception{
		prepareModel();
	}
	
	public String completeChoiceTransactor() throws Exception {
		taskService.completeChoiceTransactor(taskId,transactors);	
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"指定办理人", 
				ContextUtils.getSystemId("wf"));
		addActionMessage("办理人指定成功！");
		return input();
	}
	/**
	 * 完成分发
	 * @return
	 * @throws Exception
	 */
	public String distribute() throws Exception{
		CompleteTaskTipType completeTaskTipType = taskService.completeDistributeTask(taskId, transactors);
		if(CompleteTaskTipType.RETURN_URL==completeTaskTipType){
			return assignTransactor();
		}else{
			this.addSuccessMessage(completeTaskTipType.getContent());
			task = taskService.getWorkflowTask(taskId);
			ApiFactory.getBussinessLogService().log("工作流管理", 
					"完成分发任务", 
					ContextUtils.getSystemId("wf"));
			return input();
		}
	}
	
	
	/**
	 * 指定办理人
	 */
	@Action(value = "taskassign")
	public String assignTransactor() throws Exception{
		String to = INPUT;
		if(task.getId()==null){
			this.addErrorMessage("非法的任务");
		}else{
			WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
			CompleteTaskTipType result =  taskService.isNeedAssigningTransactor(instance, task);
			if(result==null){
				result=taskService.isSubProcessNeedChoiceTransactor(task);
			}
			switch(result){
			case OK:	
				this.addSuccessMessage(result.getContent());
				to = input();
				break;
			case MESSAGE:	
				this.addErrorMessage(result.getContent());
				to = INPUT;
				break;
			case RETURN_URL:
				candidates = taskService.getNextTasksCandidates(task);
				this.addErrorMessage("请选择办理人");
				to = "assign";
				break;
			case TACHE_CHOICE_URL:
				addActionMessage("请选择环节");
				canChoiceTaches = result.getCanChoiceTaches();
				to = choiceTache();
			case SINGLE_TRANSACTOR_CHOICE:
				User temp;
				for(String tran : result.getCanChoiceTransactor()){
					temp = userManager.getUserByLoginName(tran);
					if(temp!=null)canChoiceTransactor.put(tran, temp.getName());
				}
				moreTransactor=CompleteTaskTipType.SINGLE_TRANSACTOR_CHOICE.getContent().equals("true");
				to = "singleTransactorChoice";
			}
		}
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"指定办理人", 
				ContextUtils.getSystemId("wf"));
		return to;
	}
	public void prepareAssignTransactorPop() throws Exception {
		prepareModel();
	}
	public String assignTransactorPop() throws Exception{
		if(task.getId()==null){
			this.addErrorMessage("非法的任务");
		}else{
			candidates = taskService.getNextTasksCandidates(task);
			this.addErrorMessage("请选择办理人");
		}
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"弹出指定办理人", 
				ContextUtils.getSystemId("wf"));
		return "assignPop";
	}
	
	/**
	 * 设置办理人
	 * @return
	 * @throws Exception
	 */
	public String setTransactor() throws Exception{
		log.debug("*** setTransactor 办理任务方法开始");
		
		Map<String, List<String>> taskCondidates = new HashMap<String, List<String>>();
		for(String taskId : targetNames){
			String[] condidates = Struts2Utils.getRequest().getParameterValues("targetTransactors_" + taskId);
			List<String> condidateList = new ArrayList<String>();
			for(String condidate : condidates){
				condidateList.add(condidate.split("_")[0]);
			}
			taskCondidates.put(taskId, condidateList);
		}
		taskService.setTasksTransactor(taskId, taskCondidates);
		task = taskService.getTask(taskId);
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"设置办理人", 
				ContextUtils.getSystemId("wf"));
		this.addSuccessMessage("任务已完成");
		log.debug("*** setTransactor 方法结束");
		return view();
	}
	
	public void prepareGetBack() throws Exception{
		prepareModel();
	}
	/**
	 * 取回任务
	 * @return
	 * @throws Exception
	 */
	public String getBack() throws Exception{
		retrieveResult = taskService.retrieve(taskId);
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"取回任务", 
				ContextUtils.getSystemId("wf"));
		return input();
	}
	
	/**
	 * 完成交办
	 * @return
	 * @throws Exception
	 */
	public String assignTo() throws Exception{
		task = taskService.getWorkflowTask(taskId);
		newTransactor=transactors.toString().replace(" ", "").replace("[", "").replace("]","");
		taskService.completeWorkflowTask(task, TaskProcessingResult.ASSIGN,TaskSetting.getTaskSettingInstance().setAssignmentTransactors(newTransactor));
		ApiFactory.getBussinessLogService().log("工作流管理", 
				"完成交办任务", 
				ContextUtils.getSystemId("wf"));
		this.addSuccessMessage("任务交办成功");
		return input();
	}
	
	
	/**
	 * 流程监控/保存更改的办理人
	 * @return
	 * @throws Exception
	 */
	public String changeTransactorSave() throws Exception{
		taskService.changeTransactor(taskId, transactor);
		task = taskService.getWorkflowTask(taskId);
		workflowId = task.getProcessInstanceId();
		ApiFactory.getBussinessLogService().log("流程监控", 
				"更改办理人", 
				ContextUtils.getSystemId("wf"));
		this.addSuccessMessage("办理人已更改");
		return changeTransactor();
	}
	
	/**
	 * 流程监控 更改办理人
	 */
	public String changeTransactor() throws Exception { 
		if(instanceId != null) workflowInstance = workflowInstanceManager.getWorkflowInstance(instanceId);
		else workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		taskList = taskService.getActivityTasks(workflowInstance.getProcessInstanceId(),ContextUtils.getCompanyId());
		//判断当前环节是否是子流程
		hasActivitySubProcess = taskService.getActivetySubProcess(workflowInstance.getProcessInstanceId());
		return "changeTransactor";
	}
	
	
	/**
	 * 转向流转历史
	 * @return
	 * @throws Exception
	 */
	public String flowHistory() throws Exception {
		companyId = ContextUtils.getCompanyId();
		ApiFactory.getBussinessLogService().log("工作流平台", 
				"查看流转历史", 
				ContextUtils.getSystemId("wf"));
		return "flowHistory";
	}
	
	/**
	 * 转向加签页面
	 */
	public String addCountersign() throws Exception {
		return "addCountersign";
	}
	/**
	 * 加签
	 */
	public String addTask() throws Exception {
		taskService.generateTask(taskId,transactors,TaskSource.ADD_SIGN);
		ApiFactory.getBussinessLogService().log("工作流平台", 
				"加签", 
				ContextUtils.getSystemId("wf"));
		return null;
	}
	/**
	 * 转向减签页面
	 */
	public String deleteCountersign() throws Exception {
		taskList = taskService.getProcessCountersigns(taskId);
		return "deleteCountersign";
	}
	
	/**
	 * 减签 
	 */
	public String deleteTask() throws Exception {
		taskService.deleteWorkflowTask(ids);
		ApiFactory.getBussinessLogService().log("工作流平台", 
				"减签", 
				ContextUtils.getSystemId("wf"));
		return deleteCountersign();
	}
	
	/**
	 * 会签结果
	 * @return
	 * @throws Exception
	 */
	public String countersign()throws Exception{
		return "countersign";
	}
	
	/**
	 * 投票结果
	 * @return
	 * @throws Exception
	 */
	public String vote()throws Exception{
		return "vote";
	}
	/**
	 * 点击抄送按钮
	 * @return
	 * @throws Exception
	 */
	public String copyTache() throws Exception {
		
		return "copyTache";
	}
	
	public void prepareCompleteCopyTache() throws Exception{
		prepareModel();
	}
	/**
	 * 完成抄送
	 * @return
	 * @throws Exception
	 */
	public String completeCopyTache() throws Exception {
		taskService.createCopyTaches(taskId,transactors,"","/engine/task!input.htm");	
		ApiFactory.getBussinessLogService().log("工作流平台", 
				"完成抄送任务", 
				ContextUtils.getSystemId("wf"));
		this.addSuccessMessage("抄送成功");
		return input();
	}
	
	public String retrieve() throws Exception {
		retrieveResult=taskService.retrieve(taskId);
		ApiFactory.getBussinessLogService().log("工作流平台", 
				"取回任务", 
				ContextUtils.getSystemId("wf"));
		return input();
	}
	
	/**
	 * 流程监控/保存增加的办理人
	 * @return
	 * @throws Exception
	 */
	public String addTransactorSave() throws Exception{
		taskService.addTransactor(workflowId, transactors);
		ApiFactory.getBussinessLogService().log("流程监控", 
				"增加办理人", 
				ContextUtils.getSystemId("wf"));
		this.addSuccessMessage("办理人已增加");
		return addTransactor();
	}
	
	/**
	 * 流程监控  增加办理人
	 */
	public String addTransactor() throws Exception { 
		if(instanceId != null) workflowInstance = workflowInstanceManager.getWorkflowInstance(instanceId);
		else workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
		workflowId = workflowInstance.getProcessInstanceId();
		forkTask = taskService.isForkTask(workflowId);
		//判断当前环节是否是子流程
		hasActivitySubProcess = taskService.getActivetySubProcess(workflowInstance.getProcessInstanceId());
		return "addTransactor";
	}
	
	/**
	 * 流程监控/保存减少的办理人
	 * @return
	 * @throws Exception
	 */
	public String delTransactorSave() throws Exception{
		taskService.delTransactor(workflowId, transactors);
		ApiFactory.getBussinessLogService().log("流程监控", 
				"减少办理人", 
				ContextUtils.getSystemId("wf"));
		this.addSuccessMessage("办理人已减少");
		return delTransactor();
	}
	
	/**
	 * 流程监控  减少办理人
	 */
	public String delTransactor() throws Exception {
		WorkflowInstance instance = null;
		if(instanceId != null) instance = workflowInstanceManager.getWorkflowInstance(instanceId);
		else instance = workflowInstanceManager.getWorkflowInstance(workflowId);
		workflowId = instance.getProcessInstanceId();
		boolean isForkTask = taskService.isForkTask(workflowId);
		if(!isForkTask){//表示是分支汇聚任务时不让减少办理人
			List<String[]> currentTransactors=taskService.getActivityTaskTransactors(workflowId);
			List<String> currentPrincipals=taskService.getActivityTaskPrincipals(workflowId);
			if(currentTransactors!=null){
				if(currentTransactors.size()==1){
					Object[] transactors=currentTransactors.get(0);
					transactor=(transactors[1]).toString();
				}
				transactorNum=currentTransactors.size();
			}
			if(currentPrincipals!=null){
				if(transactorNum==0&&currentPrincipals.size()==1){
					String loginName=currentPrincipals.get(0);
					User user=acsUtils.getUserByLoginName(loginName);
					if(user!=null){
						transactor=user.getName();
					}
				}
				transactorNum=transactorNum+currentPrincipals.size();
			}
		}
		//判断当前环节是否是子流程
		hasActivitySubProcess = taskService.getActivetySubProcess(instance.getProcessInstanceId());
		return "delTransactor";
	}
	
	public String delTransactorTree() throws Exception{
		StringBuilder tempTree = new StringBuilder();
		StringBuilder tree=new StringBuilder();
		tree.append("[");
		tree.append("{\"attr\":{").append(JsTreeUtil1.treeAttrBefore).append("company_company-company").append(JsTreeUtil1.treeAttrMiddle).append("company").append(JsTreeUtil1.treeAttrAfter).append("},\"state\":\"open\",\"data\":\""+ContextUtils.getCompanyName() + "\"");
		List<String[]> currentTransactors=taskService.getActivityTaskTransactors(workflowId);
		List<String> currentPrincipals=taskService.getActivityTaskPrincipals(workflowId);
		int transactNum=currentTransactors.size()+currentPrincipals.size();
//		if((currentTransactors!=null&&currentTransactors.size()>1)||(currentPrincipals!=null&&currentPrincipals.size()>1)){
		if(transactNum>1){
			tempTree.append(",\"children\":");
			tempTree.append("[");
			for(int i=0;i<currentTransactors.size();i++){
				Object[] transactor=currentTransactors.get(i);
				Object userName = transactor[1];
				if(userName==null){
					User user = userManager.getUserByLoginName(transactor[0]+"");
					if(user!=null)userName = user.getName();
				}
				tempTree.append(JsTreeUtil1.generateJsTreeNodeNew("user_"+ userName+"-"+transactor[0], "",userName.toString(),"user")).append(",");
			}
			for(String str:currentPrincipals){
				String userName=userManager.getUserByLoginName(str).getName();
				tempTree.append(JsTreeUtil1.generateJsTreeNodeNew("user_"+ userName+"-"+str, "", userName,"user")).append(",");
			}
			tree.append(delComma(tempTree.toString()));
			tree.append("]");
		}
		tree.append("}");
		tree.append("]");
		renderText(tree.toString());
		return null;
	}
	
	/**
	 * 去逗号
	 * @param str
	 * @return
	 */
	private String delComma(String str){
		if(StringUtils.endsWith(str, ","))str= str.substring(0,str.length() - 1);
		return str;
	}	
	
	@Override
	public String delete() throws Exception {
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(taskId != null){
			task = taskService.getWorkflowTask(taskId);
		}else {
			task = new WorkflowTask();
		}
	}
	
	public void prepareAssignTransactor() throws Exception {
		prepareModel();
	}
	
	public void prepareView() throws Exception {
		prepareModel();
	}
	
	public void prepareSetTransactor() throws Exception {
		prepareModel();
	}

	public void prepareSaveOpinion() throws Exception {
		prepareModel();
	}
	
	public WorkflowTask  getModel() {
		return null;
	}
	
	@Autowired
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}
	@Autowired
	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}
	@Autowired
	public void setWorkflowRightsManager(
			WorkflowRightsManager workflowRightsManager) {
		this.workflowRightsManager = workflowRightsManager;
	}
	@Autowired
	public void setFormViewManager(FormViewManager formManager) {
		this.formViewManager = formManager;
	}
	
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		if(taskId==null)return ;
		this.taskId = taskId;
	}

	public String getRetrieveResult() {
		return retrieveResult;
	}

	public TaskProcessingResult getTransact() {
		return transact;
	}
	public void setTransact(TaskProcessingResult transact) {
		this.transact = transact;
	}
	public String getFormHtml() {
		return formHtml;
	}

	public void setFormHtml(String formHtml) {
		this.formHtml = formHtml;
	}

	public Map<String[], List<String[]>> getCandidates() {
		return candidates;
	}

	public void setCandidates(Map<String[], List<String[]>> candidates) {
		this.candidates = candidates;
	}

	public String[] getTargetTransactors() {
		return targetTransactors;
	}

	public void setTargetTransactors(String[] targetTransactors) {
		this.targetTransactors = targetTransactors;
	}

	public String[] getTargetNames() {
		return targetNames;
	}

	public void setTargetNames(String[] targetNames) {
		this.targetNames = targetNames;
	}

	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getRequiredFields() {
		return requiredFields;
	}

	public void setRequiredFields(String requiredFields) {
		this.requiredFields = requiredFields;
	}

	public WorkflowInstance getWorkflowInstance() {
		return workflowInstance;
	}

	public void setWorkflowInstance(WorkflowInstance workflowInstance) {
		this.workflowInstance = workflowInstance;
	}

	public List<Opinion> getOpinions() {
		return opinions;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public WorkflowTask  getTask() {
		return task;
	}

	public void setTask(WorkflowTask  task) {
		this.task = task;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public List<Temp> getTemps() {
		return temps;
	}

	public void setTemps(List<Temp> temps) {
		this.temps = temps;
	}

	public List<WorkflowTask> getTaskList() {
		return taskList;
	}

	public Boolean getView() {
		return view;
	}
	
	public Boolean getEdit() {
		return edit;
	}

	public Boolean getMust() {
		return must;
	}

	public void setTransactors(List<String> transactors) {
		this.transactors = transactors;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRtxSessionKey() {
		return rtxSessionKey;
	}
	public void setRtxSessionKey(String rtxSessionKey) {
		this.rtxSessionKey = rtxSessionKey;
	}
	public String getRtxsIp() {
		return rtxsIp;
	}
	public void setRtxsIp(String rtxsIp) {
		this.rtxsIp = rtxsIp;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public Long getDataId() {
		return dataId;
	}
	public void setDataId(Long dataId) {
		this.dataId = dataId;
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
	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}
	public boolean isMoreTransactor() {
		return moreTransactor;
	}
	public void setMoreTransactor(boolean moreTransactor) {
		this.moreTransactor = moreTransactor;
	}
	
	public String getTransitionName() {
		return transitionName;
	}
	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;
	}
	public Map<String, String> getCanChoiceTaches() {
		return canChoiceTaches;
	}
	public void setCanChoiceTaches(Map<String, String> canChoiceTaches) {
		this.canChoiceTaches = canChoiceTaches;
	}
	private void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	public Map<String, String> getCanChoiceTransactor() {
		return canChoiceTransactor;
	}

	public void setNewTransactor(String newTransactor) {
		this.newTransactor = newTransactor;
	}
	
	public void setNullAssignmentException(String nullAssignmentException) {
		this.nullAssignmentException = nullAssignmentException;
	}
	public String getNullAssignmentException() {
		return nullAssignmentException;
	}
	public Long getWfdId() {
		return wfdId;
	}
	public void setWfdId(Long wfdId) {
		this.wfdId = wfdId;
	}
	public List<String[]> getTransitionNames() {
		return transitionNames;
	}
	public String getMessageTip() {
		return messageTip;
	}

	public String getCanChoiceTransacators() {
		return canChoiceTransacators;
	}

	public void setCanChoiceTransacators(String canChoiceTransacators) {
		this.canChoiceTransacators = canChoiceTransacators;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getDefinitionCode() {
		return definitionCode;
	}

	public void setDefinitionCode(String definitionCode) {
		this.definitionCode = definitionCode;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public Integer getTransactorNum() {
		return transactorNum;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}
	public String getTransactor() {
		return transactor;
	}
	public boolean isForkTask() {
		return forkTask;
	}

	public String getInstanceIds() {
		return instanceIds;
	}
	public void setInstanceIds(String instanceIds) {
		this.instanceIds = instanceIds;
	}
	public String getWorkflowIds() {
		return workflowIds;
	}
	public void setWorkflowIds(String workflowIds) {
		this.workflowIds = workflowIds;
	}

	public boolean isHasActivitySubProcess() {
		return hasActivitySubProcess;
	}
	public void setHasActivitySubProcess(boolean hasActivitySubProcess) {
		this.hasActivitySubProcess = hasActivitySubProcess;
	}

	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
}
