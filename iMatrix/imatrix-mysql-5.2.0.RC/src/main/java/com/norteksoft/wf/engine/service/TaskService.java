package com.norteksoft.wf.engine.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.Execution;
import org.jbpm.api.JbpmException;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.model.Activity;
import org.jbpm.api.model.OpenExecution;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.jbpm.pvm.internal.task.ParticipationImpl;
import org.jbpm.pvm.internal.task.TaskImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.acs.api.AcsApi;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.mms.base.utils.FreeMarkertUtils;
import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.AutomaticallyFilledField;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.impl.WorkflowClientManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.AsyncMailUtils;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.base.enumeration.TaskSource;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.TaskMark;
import com.norteksoft.task.entity.TaskSetting;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.webservice.WorkflowTaskService;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.base.enumeration.LogicOperator;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.base.enumeration.TaskTransactorCondition;
import com.norteksoft.wf.base.enumeration.WorkflowTacheType;
import com.norteksoft.wf.base.exception.TransactorAssignmentException;
import com.norteksoft.wf.base.utils.BeanShellUtil;
import com.norteksoft.wf.base.utils.WebUtil;
import com.norteksoft.wf.engine.client.AfterTaskCompleted;
import com.norteksoft.wf.engine.client.BeforeTaskSubmit;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.wf.engine.client.ReturnTaskInterface;
import com.norteksoft.wf.engine.client.OnStartingSubProcess;
import com.norteksoft.wf.engine.client.RetrieveTaskInterface;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.core.ExecutionVariableCommand;
import com.norteksoft.wf.engine.core.GetBackCommand;
import com.norteksoft.wf.engine.core.SubProcessParse;
import com.norteksoft.wf.engine.core.TaskAssigneeCommand;
import com.norteksoft.wf.engine.core.TransactorConditionHandler;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;
import com.norteksoft.wf.engine.dao.OpinionDao;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.TrustRecord;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.entity.WorkflowType;

/**
 * 工作流任务处理
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class TaskService {
	
	private Log log = LogFactory.getLog(TaskService.class);
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private ProcessEngine processEngine;
	private WorkflowTaskService workflowTaskService;
	private WorkflowInstanceManager workflowInstanceManager;
	private InstanceHistoryManager instanceHistoryManager;
	private WorkflowDefinitionManager workflowDefinitionManager;
	private GeneralDao generalDao;
	private FormViewManager formViewManager;
	private WorkflowClientManager workflowClientManager ;
	private WorkflowTypeManager workflowTypeManager;
	private static final String PROCESS_WENT = "流程走过";
	private static final String PROCESS_ENTER = "流程进入";
	private static final String PROCESS_JUMPTASK = "流程跳转到";
	private static final String PROCESS_GOBACKTASK = "流程退回到";
	private static final String PROCESS_ADDTRANSACTOR = "增加办理人:";
	private static final String PROCESS_REMOVETRANSACTOR = "减少办理人:";
	private static final String PROCESS_LEAVE = "流程离开";
	private static final String PROCESS_END = "流程结束";
	private static final String PROCESS_END_EN = "process_is_end";
	//流转历史常量
	private static final String COMMA = ", ";
	private static final String DELTA_START = "[ ";
	private static final String DELTA_END = " ]";
	private static final String ACTION_START = "点击了[";
	private static final String ACTION_END = "]操作";
	
	private static final String TASK_CANCEL = "的任务被取消";
	private static final String TASK_BACK = "退回任务";
	
	
	public static final String DEFAULT_URL = "/engine/task!assignTransactor.htm";
	public static final String DEFAULT_URL_POP = "/engine/task!assignTransactorPop.htm";
	public static final String DEFAULT_DO_TASK_URL = "/engine/task!input.htm?taskId=";
	public static final String DEFAULT_CHOICE_TACHE_URL = "/engine/task!choiceTache.htm?taskId=";
	public static final String DEFAULT_CHOICE_TACHE_URL_POP = "/engine/task!choiceTachePop.htm?taskId=";
	public static final String PRE_TRANSACTOR_ASSIGN="${previousTransactorAssignment}";
	
	private static final String SQUARE_BRACKETS_LEFT = "[";
	private static final String SQUARE_BRACKETS_RIGHT = "]";
	private static final String TASK_CUSTOM_INFO="$custom";
	private static final String TASK_TITLE_INFO="$title";
	private DelegateMainManager delegateManager;
	
	private UserManager userManager;
	
	private WorkflowRightsManager workflowRightsManager;
	
	private BusinessSystemManager businessSystemManager;
	
	private TableColumnManager tableColumnManager;
	private OpinionDao opinionDao;
	
	private AcsUtils acsUtils;
	@Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
	
	public static Map<String,String> instanceIds=new HashMap<String, String>();
	
	
	@Transactional(readOnly=false)
	public void saveTask(WorkflowTask task){
		workflowTaskService.saveTask(task);
	}
	
	@Transactional(readOnly=false)
	public void saveTasks(List<WorkflowTask> tasks){
		workflowTaskService.saveTasks(tasks);
	}
	@Autowired
	public void setGeneralDao(GeneralDao generalDao) {
		this.generalDao = generalDao;
	}
	
	@Autowired
	public void setWorkflowTypeManager(WorkflowTypeManager workflowTypeManager) {
		this.workflowTypeManager = workflowTypeManager;
	}
	@Autowired
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}
	@Autowired
	public void setTableColumnManager(TableColumnManager tableColumnManager) {
		this.tableColumnManager = tableColumnManager;
	}
	@Autowired
	public void setOpinionDao(OpinionDao opinionDao) {
		this.opinionDao = opinionDao;
	}

	public WorkflowTask getTask(Long taskId){
		Assert.notNull(taskId, "taskId不能为null");
		return workflowTaskService.getTask(taskId);
	}
	

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	public List<WorkflowTask> generateFirstTask(String processId, String instanceId,int priority,boolean visible){
		List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
		OpenExecution execution = (OpenExecution)processEngine.getExecutionService().findExecutionById(instanceId);
		String creator =execution.getVariable(CommonStrings.CREATOR)==null?null:execution.getVariable(CommonStrings.CREATOR).toString();
		if(StringUtils.isNotEmpty(creator)){
			WorkflowTask task = generateFirstTask(processId,instanceId,creator, priority, visible); 
			task.setActive(TaskState.WAIT_TRANSACT.getIndex());
			tasks.add(task);
		}else{
			Set<String> creatorCandidates =  execution.getVariable(CommonStrings.CREATOR_CANDIDATES)==null?null:(Set<String>)execution.getVariable(CommonStrings.CREATOR_CANDIDATES);
			for(String transactor:creatorCandidates){
				WorkflowTask task = generateFirstTask(processId,instanceId,transactor, priority, visible); 
				task.setActive(TaskState.DRAW_WAIT.getIndex());
				tasks.add(task);
			}
		}
		this.saveTasks(tasks);
		return tasks;
	}
	/**
	 * 流程启动时生成第一个任务
	 * @param processId
	 * @param instanceId
	 * @param transactor
	 */
	@Transactional(readOnly=false)
	public WorkflowTask generateFirstTask(String processId, String instanceId, String transactor,int priority,boolean visible){
		log.debug("*** generateFirstTask 方法开始");
		
		String firstTaskName = DefinitionXmlParse.getFirstTaskName(processId);
		log.debug("第一环节名："+ firstTaskName);
		
		WorkflowInstance wi=workflowInstanceManager.getWorkflowInstance(instanceId);
		WorkflowTask task = new WorkflowTask();
		task.setCompanyId(wi.getCompanyId());
		task.setProcessInstanceId(instanceId);
		task.setExecutionId(instanceId);
		task.setName(firstTaskName);
		String code = DefinitionXmlParse.getTacheCode(processId, task.getName());
		task.setCode(code);
		task.setDistributable(false);
		task.setTransactor(transactor);
		task.setUrl(getTaskUrl(processId));
		task.setCreator(transactor);
		User user = userManager.getUserByLoginName(task.getTransactor());
		if(user!=null){
			task.setTransactorName(user.getName());
			task.setCreatorName(user.getName());
    	}
		task.setCreatedTime(new Date());
		task.setTaskMark(TaskMark.valueOf(priority));
		addTitle(task, processId,null);
		//FIXME 增加实例标题，实例中任务标题一样时有用，需要修改
		wi.setInstanceTitle(task.getTitle());
		workflowInstanceManager.saveWorkflowInstance(wi);
		
		String processingMode = DefinitionXmlParse.getTaskProcessingMode(
				processId, task.getName());
		boolean moreTransactor = DefinitionXmlParse.hasMoreTransactor(
				processId, task.getName());
		task.setProcessingMode(TaskProcessingMode.getTaskModeFromStringToEnum(processingMode));
		task.setMoreTransactor(moreTransactor);
		task.setRead(false);
		WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		//流程类型
		WorkflowType type=null;
		if(wfDef!=null){
			task.setGroupName(wfDef.getName());
			task.setCustomType(wfDef.getCustomType());
			//流程类型
			type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
			if(type!=null)task.setCategory(type.getName());
		}
		
		task.setVisible(visible);
		task.setUrl(getTaskUrl(processId));
		
		return task;
	}
	/*
	 * 创建任务 
	 * @param processInstanceId
	 * @param executionId
	 * @param transactor
	 * @param activityName
	 * @param processingMode
	 * @param moreTransactor
	 * @param isSendMsg
	 * @return
	 * createTask(task.getProcessInstanceId(), task.getExecutionId(), user, "title", TaskProcessingMode.TYPE_READ.toString(), true));
	 */
	@Transactional(readOnly=false)
	private WorkflowTask createTask(String processInstanceId, String executionId, String transactor, String taskName,Integer groupNum){
		String definitionId = processEngine.getExecutionService().findProcessInstanceById(processInstanceId).getProcessDefinitionId();
		WorkflowTask newTask = createTask(definitionId,processInstanceId,executionId,taskName,transactor, false,groupNum);
		return newTask;
	}
	@Transactional(readOnly=false)
	private WorkflowTask createDistributeTask(WorkflowTask task, String transactor, String processingMode,Integer groupNum){
		String definitionId = processEngine.getExecutionService().findProcessInstanceById(task.getProcessInstanceId()).getProcessDefinitionId();
		WorkflowTask newTask = createTask(definitionId,task.getProcessInstanceId(),task.getExecutionId(),task.getName(),transactor, true,groupNum);
		newTask.setProcessingMode(TaskProcessingMode.getTaskModeFromStringToEnum(processingMode));
		return newTask;
	}
	/*
	 * 创建任务
	 * @param processId 流程定义id
	 * @param workflowId 流程实例id
	 * @param executionId 
	 * @param taskName 任务名
	 * @param transactor 任务办理人
	 * @param isSendMsg 是否发送消息
	 * @return
	 */
	@Transactional(readOnly=false)
	private WorkflowTask createTask(String processId,String workflowId,String executionId,String taskName,String transactor,boolean isDistribute,Integer groupNum){
		log.debug(new StringBuilder("*** createTask parameter:[")
		.append("processId:").append(processId)
		.append(", workflowId:").append(workflowId)
		.append(", executionId:").append(executionId)
		.append(", taskName:").append(taskName)
		.append(", transactor:").append(transactor)
		.append("]").toString());
		
			WorkflowTask task = new WorkflowTask();
			WorkflowInstance  wi=workflowInstanceManager.getWorkflowInstance(workflowId,ContextUtils.getCompanyId());
			if(wi.getProcessState()!=ProcessState.END&&wi.getProcessState()!=ProcessState.MANUAL_END){//流程实例没有结束或强制结束则执行以下语句，因为jbpm4.4中当实例结束后删除该实例，执行processEngine.getExecutionService().getVariable时报异常
				Object creator = processEngine.getExecutionService().getVariable(executionId, CommonStrings.CREATOR);
				if(creator != null){
					User user = userManager.getUserByLoginName(creator.toString());
					task.setCreator(user.getLoginName());
					task.setCreatorName(user.getName());
				}
			}
			wi=workflowInstanceManager.getWorkflowInstance(workflowId,ContextUtils.getCompanyId());
			task.setCompanyId(ContextUtils.getCompanyId());
			task.setProcessInstanceId(workflowId);
			task.setExecutionId(executionId);
			task.setDistributable(isDistribute);
			log.info("taskService 设置办理人"+transactor);
			String titleInfo=setTaskTransactorInfo(transactor, task);
			 String delegateTransactor = delegateManager.getDelegateMainName(
					 wi.getCompanyId(), transactor,wi.getProcessDefinitionId() , taskName);
			 boolean moreTransactor = DefinitionXmlParse.hasMoreTransactor(
					 processId, taskName);
			 boolean shouldSaveTask =  shouldSaveTask(wi.getCompanyId(),task.getTransactor(),null,wi.getProcessInstanceId(),delegateTransactor,moreTransactor);
			 User user = userManager.getUserByLoginName(task.getTransactor());
			 if(user!=null){//如果办理人transactor存在才生成任务
				 task.setTransactorName(user.getName());
			 }else{
				 return null;
			 }
			 task.setName(taskName);
			 String code = DefinitionXmlParse.getTacheCode(processId, taskName);
			 task.setCode(code);
			 task.setCreatedTime(new Date());
			 
			 if(wi.getProcessState()!=ProcessState.END&&wi.getProcessState()!=ProcessState.MANUAL_END){//流程实例没有结束或强制结束则执行以下语句，因为jbpm4.4中当实例结束后删除该实例，执行processEngine.getExecutionService().getVariable时报异常
				 Object priorityObject = processEngine.getExecutionService().getVariable(executionId, CommonStrings.PRIORITY);
				 if(priorityObject != null){
					 int priority = Integer.valueOf(priorityObject.toString());
					 task.setTaskMark(TaskMark.valueOf(priority));
				 }
			 }
			 addTitle(task, processId,titleInfo);
			 //FIXME 增加实例标题,实例中任务标题一样时有用，需要修改
			 wi.setInstanceTitle(task.getTitle());
			 workflowInstanceManager.saveWorkflowInstance(wi);
			 
			 String processingMode = DefinitionXmlParse.getTaskProcessingMode(
					 processId, taskName);
			 log.debug("办理模式："+processingMode);
			 log.debug("是否多人办理："+moreTransactor);
			 task.setProcessingMode(TaskProcessingMode.getTaskModeFromStringToEnum(processingMode));
			 task.setMoreTransactor(moreTransactor);
			 task.setActive(TaskState.WAIT_TRANSACT.getIndex());
			 task.setUrl(getTaskUrl(processId));
			 task.setRead(false);
			 Map<String,String > buttonNameSetting = DefinitionXmlParse.getButtonNameByProcessMode(processId, task.getName(),TaskProcessingMode.getTaskModeFromStringToEnum(processingMode));
			 setButtonName(TaskProcessingMode.getTaskModeFromStringToEnum(processingMode),task,buttonNameSetting);
			 Map<String,String > reminderSetting = DefinitionXmlParse.getReminderSetting(processId, task.getName());
			 task.setReminderStyle(reminderSetting.get(DefinitionXmlParse.REMIND_STYLE));
			 if(reminderSetting.get(DefinitionXmlParse.REMIND_REPEAT)!=null)task.setRepeat(Long.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_REPEAT)));
			 if(reminderSetting.get(DefinitionXmlParse.REMIND_DUEDATE)!=null)task.setDuedate(Long.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_DUEDATE)));
			 if(reminderSetting.get(DefinitionXmlParse.REMIND_TIME)!=null)task.setReminderLimitTimes(Integer.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_TIME)));
			 if(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_TYPE)!=null)task.setReminderNoticeStyle(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_TYPE));
			 if(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_USER_CONDITION)!=null)task.setReminderNoticeUser(parseUserCondition( task,reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_USER_CONDITION)));
			 task.setSendingMessage(ApiFactory.getAcsService().isRtxEnable());
			 WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
			 
			 //流程类型
			 WorkflowType type=null;
			 if(wfDef!=null){
				 task.setGroupName(wfDef.getName());
				 task.setCustomType(wfDef.getCustomType());
				 //流程类型
				 type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
				 if(type!=null)task.setCategory(type.getName());
			 }
			 if(delegateTransactor != null){
				 task.setTrustor(task.getTransactor());
				 user = userManager.getUserByLoginName(task.getTransactor());
				 if(user!=null)task.setTrustorName(user.getName());
				 task.setTransactor(delegateTransactor);
				 user = userManager.getUserByLoginName(task.getTransactor());
				 if(user!=null){
					 task.setTransactorName(user.getName());
				 }
				 //生成委托流转历史
				 generateDelegateHistory(task);
			 }
			 //设置"任务组编号"
			 task.setGroupNum(groupNum);
			//是否显示，bkyoa特制功能
			task.setVisible(shouldSaveTask);
			 //*******************消息提醒*************************
			 sendMessage(task,type);
			 //*******************邮件通知*************************
			 sendMail(task,processId);
			 return task;
	}
	//委托人和受托人是否是同一部门
	private boolean trustorTransactorSameDept(String trustor,String delegateTransactor){
		String bkySpecial=PropUtils.getProp("bky.special");
		boolean isSameDept=false;
		if("true".equals(bkySpecial)){//是否是bky特制的功能,当受托人和委托人是同一部门时不生成委托任务
			//trustor和delegateTransactor是否是同一部门
			//委托人部门列表
			List<Department> trustorDepts = ApiFactory.getAcsService().getDepartments(trustor);
			//受托人部门列表
			List<Department> delegateDepts = ApiFactory.getAcsService().getDepartments(delegateTransactor);
			for(Department dept:delegateDepts){
				for(Department trustorDept:trustorDepts){
					if(dept.getName().equals(trustorDept.getName())){
						isSameDept=true;
						break;
					}
				}
				if(isSameDept)break;
			}
		}
		return isSameDept;
	}
	
	/**
	 * 生成委托流转历史
	 * @param task
	 * @param type
	 */
	private void generateDelegateHistory(WorkflowTask task) {
		StringBuilder historyMessage = new StringBuilder();
		historyMessage.append(dateFormat.format(new Date())).append(COMMA)
		.append(acsUtils.getUserByLoginName(task.getTrustor()).getName())
		.append("已把任务委托给了")
		.append(task.getTransactorName()).append("。\n");
		
		InstanceHistory history = new InstanceHistory(task.getCompanyId(), task.getProcessInstanceId(), InstanceHistory.TYPE_TASK, historyMessage.toString(), task.getName());
		history.setEffective(false);
		history.setCreatedTime(new Date());
		history.setExecutionId(task.getProcessInstanceId());
		
        instanceHistoryManager.saveHistory(history);
		
	}

	/**
	 * 消息提醒
	 * @param task
	 * @param type
	 */
	private void sendMessage(WorkflowTask task,WorkflowType type){
		saveTask(task);
		try {
			ApiFactory.getPortalService().addMessage("task", ContextUtils.getUserName(), ContextUtils.getLoginName(), task.getTransactor(),type==null?"待办任务":type.getName(), task.getTitle(), "/task/message-task.htm?id="+task.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendMail(WorkflowTask task,String processId){
	    try{
	        boolean isMailNotice=DefinitionXmlParse.isMailNotice(processId, task.getName());
	        if(isMailNotice){
	            String mailContent=PropUtils.getProp("mail.properties", "task.notice.content");
	            if(StringUtils.isNotEmpty(mailContent)){
	                mailContent=mailContent.replace("${url}", workflowTaskService.getTaskUrl(task));
	            }
	            com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserByLoginName(task.getTransactor());
	            if(user==null){
	                throw new RuntimeException("用户不存在："+task.getTransactor());
	            }else if(StringUtils.isEmpty(user.getEmail())){
	                throw new RuntimeException("用户邮件地址没有输入："+task.getTransactor());
	            }else{
	                AsyncMailUtils.sendMail(user.getEmail(), task.getTitle(),mailContent );
	            }
	        }
	    }catch (Exception e) {
	        e.printStackTrace();
            log.error(PropUtils.getExceptionInfo(e));
        }
	}
	
	private void setButtonName(TaskProcessingMode processModel,WorkflowTask task,Map<String,String > buttonNameSetting){
		if(TaskProcessingMode.TYPE_EDIT.equals(processModel)){
			task.setSubmitButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.SUBMIT_NAME),"提交"));
		}else if(TaskProcessingMode.TYPE_APPROVAL.equals(processModel)){
			task.setAgreeButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.APPROVE_NAME),"同意"));
			task.setDisagreeButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.REFUSE_NAME),"不同意"));
		}else if(TaskProcessingMode.TYPE_COUNTERSIGNATURE.equals(processModel)){
			task.setAgreeButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.APPROVE_NAME),"同意"));
			task.setDisagreeButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.REFUSE_NAME),"不同意"));
			task.setAddSignerButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.ADD_COUNTER_NAME),"加签"));
			task.setRemoveSignerButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.DEL_COUNTER_NAME),"减签"));
		}else if(TaskProcessingMode.TYPE_SIGNOFF.equals(processModel)){
			task.setSignForButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.SIGNOFF_NAME),"签收"));
		}else if(TaskProcessingMode.TYPE_VOTE.equals(processModel)){
			task.setApproveButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.AGREEMENT_NAME),"赞成"));
			task.setOpposeButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.OPPOSE_NAME),"反对"));
			task.setAbstainButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.KIKEN_NAME),"弃权"));
		}else if(TaskProcessingMode.TYPE_ASSIGN.equals(processModel)){
			task.setAbstainButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.ASSIGN_NAME),"交办"));
		}else if(TaskProcessingMode.TYPE_DISTRIBUTE.equals(processModel)){
			task.setSubmitButton(getButtonValue(buttonNameSetting.get(DefinitionXmlParse.SUBMIT_NAME),"提交"));
		}
	}
	/*
	 * 处理按钮名称，当是空时用默认的名字
	 */
	private String getButtonValue(String buttonValue,String initValue){
		if(StringUtils.isNotEmpty(buttonValue)){
			return buttonValue;
		}
		return initValue;
	}
	
	public String parseUserCondition(WorkflowTask task,String userCondition){
		Set<String> set = new HashSet<String>();
		StringBuilder builder = new StringBuilder();
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(instance.getWorkflowDefinitionId());
		FormView form = formViewManager.getFormView(instance.getFormId());
		UserParseCalculator upc = new UserParseCalculator();
		upc.setDataId(instance.getDataId());
		upc.setFormView(form);
		upc.setDocumentCreator(instance.getCreator());
		upc.setCurrentTransactor(task.getTransactor());
		upc.setProcessAdmin(definition.getAdminLoginName());
		set.addAll(upc.getUsers(userCondition,instance.getSystemId(),instance.getCompanyId()));
		for(String str:set){
			builder.append(str).append(",");
		}
		return  StringUtils.removeEnd(builder.toString(), ",");
	}
	/*
	 *创建特事特办任务
	 */
	@Transactional(readOnly=false)
	private WorkflowTask createSpecialTask(WorkflowInstance workflow,String executionId,String transactor,String taskName,String title){
		String delegateTransactor = delegateManager.getDelegateMainName(
				workflow.getCompanyId(), transactor,workflow.getProcessDefinitionId() , taskName);
		boolean moreTransactor = DefinitionXmlParse.hasMoreTransactor(
				workflow.getProcessDefinitionId(), taskName);
		 boolean shouldSaveTask =  shouldSaveTask(workflow.getCompanyId(),transactor,null,workflow.getProcessInstanceId(),delegateTransactor,moreTransactor);
		 if(title==null) title = taskName;
		 final String processId = workflow.getProcessDefinitionId();
		 WorkflowTask task = new WorkflowTask();
		 task.setCompanyId(workflow.getCompanyId());
		 task.setProcessInstanceId(workflow.getProcessInstanceId());
		 task.setExecutionId(executionId);
		 log.info("taskService 设置办理人"+transactor);
		 task.setTransactor(transactor);
		 User user = userManager.getUserByLoginName(task.getTransactor());
		 if(user!=null){
			 task.setTransactorName(user.getName());
		 }
		 task.setName(taskName);
		 String code = DefinitionXmlParse.getTacheCode(processId, task.getName());
		 task.setCode(code);
		 task.setCreatedTime(new Date());
		 Object obj = processEngine.getExecutionService().getVariable(executionId, "creator");
		 if(obj != null){
			 user = userManager.getUserByLoginName(obj.toString());
			 task.setCreator(obj.toString());
			 task.setCreatorName(user.getName());
		 }
		 task.setTitle(title);
		 String processingMode = DefinitionXmlParse.getTaskProcessingMode(
				 processId, taskName);
		 log.debug("办理模式："+processingMode);
		 log.debug("是否多人办理："+moreTransactor);
		 task.setProcessingMode(TaskProcessingMode.getTaskModeFromStringToEnum(processingMode));
		 task.setMoreTransactor(moreTransactor);
		 task.setActive(TaskState.WAIT_TRANSACT.getIndex());
		 task.setUrl(getTaskUrl(processId));
		 task.setRead(false);
		 task.setSendingMessage(true);
		 task.setSpecialTask(true);
		 WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		 task.setGroupName(wfDef.getName());
		 if(delegateTransactor != null){
			 task.setTrustor(task.getTransactor());
			 task.setTransactor(delegateTransactor);
			 user = userManager.getUserByLoginName(task.getTransactor());
			 if(user!=null){
				 task.setTransactorName(user.getName());
			 }
			 //生成委托流转历史
			 generateDelegateHistory(task);
		 }
		//是否显示，bkyoa特制功能
		 task.setVisible(shouldSaveTask);
		 return task;
	}
	
	/*
	 * 解析标题表达式
	 */
	@SuppressWarnings("unchecked")
	private String parseTitleExpression(String expression ,WorkflowInstance workflow,String titleInfo){
		if(StringUtils.isEmpty(expression)) return null;
		Long dataId=null;
		if(workflow==null) return null;
		FormView form = formViewManager.getFormView(workflow.getFormId());
		if(form==null){log.debug("解析标题表达式时，表单不能为null");throw new RuntimeException("解析标题表达式时，表单不能为null");}
		Map dataMap = null;
		Object entity = null;
		if(workflow.getDataId()!=null){
			dataId=workflow.getDataId();
		}else{
			dataId=processEngine.getExecutionService().getVariable(workflow.getProcessInstanceId(), CommonStrings.FORM_DATA_ID)==null?null:(Long)processEngine.getExecutionService().getVariable(workflow.getProcessInstanceId(), CommonStrings.FORM_DATA_ID);
			//去变量
		}
		if(dataId==null)return null;
		boolean isSql=true;
		if(form.getDataTable()==null){log.debug("解析标题表达式时，表单对应的数据表不能为null");}
		if(!form.isStandardForm()){
			//自定义表单
			dataMap = formViewManager.getDataMap(form.getDataTable().getName(), dataId);
		}else if(form.isStandardForm()){
			//标准表单
			try{
				Class.forName(form.getDataTable().getEntityName());//判断是否
				entity = this.generalDao.getObject(form.getDataTable().getEntityName(),dataId);
				isSql=false;
			}catch(ClassNotFoundException e){
				dataMap = formViewManager.getDataMap(form.getDataTable().getName(), dataId);
			}
		}
		
		StringBuilder title = new StringBuilder();
		expression = StringUtils.removeStart(expression, "${");
		expression = StringUtils.removeEnd(expression, "}");
		String[] subExpressions = expression.split("\\}\\$\\{");
		for(String subExpression:subExpressions){
			if(subExpression.startsWith("writeTitle")){
				title.append(subExpression.substring( subExpression.indexOf("[")+1, subExpression.lastIndexOf("]")));
			}else if(subExpression.startsWith("titleInfo")){
				if(StringUtils.isNotEmpty(titleInfo))title.append(titleInfo);
			}else if(subExpression.startsWith("field")){
				String field=subExpression.substring(subExpression.lastIndexOf("[")+1, subExpression.indexOf("]"));
				String enName = field;
				String dataType=null;
				String dbName=null;
				if(enName.contains(":")){
					enName=field.substring(0,field.indexOf(":"));
					String dataTypeAndDbName=field.substring(field.indexOf(":")+1);
					if(dataTypeAndDbName.contains(":")){
						dataType=dataTypeAndDbName.substring(0,dataTypeAndDbName.indexOf(":"));
						dbName=dataTypeAndDbName.substring(dataTypeAndDbName.indexOf(":")+1);
					}else{//兼容bkyoa xml
						dataType=dataTypeAndDbName;
						TableColumn column=tableColumnManager.getTableColumnByColName(form.getDataTable().getId(), enName);
						if(column!=null){
							dbName=column.getDbColumnName();
						}
					}
				}else{//兼容bkyoa xml
					TableColumn column=tableColumnManager.getTableColumnByColName(form.getDataTable().getId(), enName);
					if(column!=null){
						dataType=column.getDataType().getCode();
						dbName=column.getDbColumnName();
					}
				}
				if(isSql){
					Object value = null;
					if(!form.isStandardForm()){
						//自定义表单
						value = dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+enName);
					}else{
						//标准表单
						if(StringUtils.isNotEmpty(dbName))value = dataMap.get(dbName);
					}
					if(value==null)continue;
					if(dataType==null){
						title.append(value.toString());
						continue;
					}
					title.append(getSqlFormFieldValue(value.toString(),dataType));
				}else{
					//标准表单
					try {
						Object value=PropertyUtils.getProperty(entity, enName);
						if(value==null)continue;
						if(dataType==null){
							title.append(value.toString());
							continue;
						}
						title.append(getFormFieldValue(value,dataType));
					} catch (IllegalAccessException e) {
						log.debug(e.getMessage());
						throw new RuntimeException(e);
					} catch (InvocationTargetException e) {
						log.debug(e.getMessage());
						throw new RuntimeException(e);
					} catch (NoSuchMethodException e) {
						log.debug(e.getMessage());
						throw new RuntimeException(e);
					}
				}
				
			}
		}
		return title.toString();
	}
	
	private String getSqlFormFieldValue(String value,String dataType){
		if("DATE".equals(dataType)){
			value=value.split(" ")[0];
			return value;
		}else if("TIME".equals(dataType)){
			value=value.split(":")[0]+":"+value.split(":")[1];
			return value;
		}else{
			return value.toString();
		}
	}
	
	private String getFormFieldValue(Object value,String dataType){
		if("DATE".equals(dataType)){
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			return format.format(value);
		}else if("TIME".equals(dataType)){
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return format.format(value);
		}else{
			return value.toString();
		}
	}
	
	/*
	 * 设置任务标题
	 */
	@Transactional(readOnly=false)
	public void addTitle(WorkflowTask task, String definitionId,String titleInfo){
		String value = DefinitionXmlParse.getTaskTitle(definitionId, task.getName());
		
		String title = "";
		if(StringUtils.isNotBlank(value)){
			WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),task.getCompanyId());
			title = parseTitleExpression(value,workflow,titleInfo);
		}
		if(StringUtils.isNotBlank(title)){
			task.setTitle(title);
		}else{
			task.setTitle(task.getName());
		}
	}
	
	
	public String getTaskUrl(String processId){
		Map<String,String> parameterSetting = DefinitionXmlParse.getParameterSetting(processId);
		String doTaskUrl = parameterSetting.get(DefinitionXmlParse.DO_TASK_URL);
		Map<String, String> defBasicInfo=DefinitionXmlParse.getWorkFlowBaseInfo(processId);
		String parameterName = parameterSetting.get(DefinitionXmlParse.DO_TASK_URL_PARAMETER_NAME);
		if(StringUtils.isEmpty(doTaskUrl)){
			doTaskUrl = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.PROCESS_TASK_URL);
		}
		if(StringUtils.isEmpty(parameterName)){
			parameterName = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.PROCESS_TASK_PARAMTER_NAME);
		}
		if(StringUtils.isEmpty(doTaskUrl)){
			doTaskUrl = DEFAULT_DO_TASK_URL;
		}else{
			String joinSign = StringUtils.contains(doTaskUrl, "?") ? "&" : "?";
			doTaskUrl = doTaskUrl + joinSign + parameterName + "=";
		}
		String systemCode=defBasicInfo.get(DefinitionXmlParse.SYSTEM_CODE);
		String code=ContextUtils.getSystemCode();
		if(StringUtils.isNotEmpty(systemCode)){
			code=systemCode;
		}
		
		return code+doTaskUrl;
	}
	
	
	public List<WorkflowTask> getCompletedTasks(String workflowId){
		WorkflowInstance workflowInstance=workflowInstanceManager.getWorkflowInstance(workflowId);
		return workflowTaskService.getCompletedTasks( workflowId,workflowInstance.getCompanyId());
	}
	
	/**
	 * 流程实例能退回到的环节名称
	 * @param instanceId
	 * @return
	 */
	public List<String> canBackNames(String workflowId){
		List<String> result = new ArrayList<String>();
		WorkflowInstance workflowInstance=workflowInstanceManager.getWorkflowInstance(workflowId);
		if(workflowInstance==null){log.debug("获得流程实例能退回到的环节名称时,流程实例不能为null");throw new RuntimeException("获得流程实例能退回到的环节名称时,流程实例不能为null");}
		ProcessInstance instance = processEngine.getExecutionService().findProcessInstanceById(workflowId);
		//有并发不能退回
		if(instance==null || ((ExecutionImpl)instance).getSubProcessInstance() != null) 
			return result;
		List<WorkflowTask> tasks = workflowTaskService.getActivityTasks(workflowId, workflowInstance.getCompanyId());
		List<WorkflowTask> completedTasks = getCompletedTasks(workflowId);
		if((tasks != null && !tasks.isEmpty())||(tasks.size()==0&&workflowInstance.getProcessState()==ProcessState.END)){//当前不为并发
			for(WorkflowTask task: completedTasks){
				//排除同名和和当前环节的任务
				if(!result.contains(task.getName())&&!containTheSameTaskName(tasks,task.getName())) {
					result.add(task.getName());
				}
			}
		}
		return result;
	}
	private boolean containTheSameTaskName(List<WorkflowTask> tasks,String taskName){
		for(WorkflowTask task:tasks){
			if(task.getName().trim().equals(taskName.trim())) return true;
		}
		return false;
	}
	
	/**
	 * 退回到某环节
	 * @param instanceId
	 * @param backTo
	 */
	@Transactional(readOnly=false)
	public String goBack(String workflowId, String backTo){
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(workflowId);
		if(instance==null){log.debug("退回到某环节时，流程实例不能为null");throw new RuntimeException("退回到某环节时，流程实例不能为null");}
		List<WorkflowTask> needSaveTasks = new ArrayList<WorkflowTask>();
		List<WorkflowTask> tasks = workflowTaskService.getActivityTasks(workflowId, instance.getCompanyId());
		//取消所有当前的环节
		for(WorkflowTask task :tasks){
			if(!task.isSpecialTask()) {
				task.setActive(TaskState.CANCELLED.getIndex());
				task.setEffective(false);      //设置任务失效
				needSaveTasks.add(task);
				taskJumpHistoryProcess(workflowId, task, backTo);
			}
		}
		
		//得到所有已完成的任务
		List<WorkflowTask> completedTasks = getCompletedTasks(workflowId);
		List<WorkflowTask> backToTasks = new ArrayList<WorkflowTask>();
		for(WorkflowTask task:completedTasks){
			if(task.getName().trim().equals(backTo.trim())){
				backToTasks.add(task);
			}
		}
		//生成退回环节任务
		for(WorkflowTask task :getTaskNameFilter(backToTasks)){
			needSaveTasks.add(cloneTask(instance,task));
		}
		//将jbpm退回到backto环节
		processEngine.execute(new GetBackCommand(instance.getProcessInstanceId(), backTo));
		if(instance.getProcessState()==ProcessState.END){
			instance.setProcessState(ProcessState.SUBMIT);
			instance.setEndTime(null);
		}
		instance.setCurrentActivity(backTo);
		FormView form = formViewManager.getFormView(instance.getFormId());
		if(form==null){log.debug("退回到某环节时，流程对应的表单不能为null");throw new RuntimeException("退回到某环节时，流程对应的表单不能为null");}
		//如果表单是实体表单，同步实体的当前环节
		if(form.isStandardForm()){
			try {
				if(form.getDataTable()==null){log.debug("退回到某环节时，表单对应的数据表不能为null");throw new RuntimeException("退回到某环节时，表单对应的数据表不能为null");}
				if(form.getDataTable().getEntityName()==null){log.debug("退回到某环节时，表单对应的数据表的实体类名不能为null");throw new RuntimeException("退回到某环节时，表单对应的数据表的实体类名不能为null");}
				Object entity = generalDao.getObject(form.getDataTable().getEntityName(), instance.getDataId());
				BeanUtils.setProperty(entity, "workflowInfo.currentActivityName", backTo);
				generalDao.save(entity);
			} catch (IllegalAccessException e) {
				log.error("为bean设置属性异常:" + e.getMessage());
			} catch (InvocationTargetException e) {
				log.error("为bean设置属性异常:" +e.getMessage());
			}
		}
		workflowInstanceManager.saveWorkflowInstance(instance);
		this.saveTasks(needSaveTasks);
		return null;
	}
	private static final String LINK_SIGN = "-";
	private List<WorkflowTask> getTaskNameFilter(List<WorkflowTask> tasks){
		List<String> targetTasks = new ArrayList<String>(); 
		List<WorkflowTask> result = new ArrayList<WorkflowTask>();
		StringBuilder temp = new StringBuilder(); 
		for(WorkflowTask task :tasks){
			temp.append(task.getName()).append(LINK_SIGN).append(task.getTransactor());
			if(!task.isSpecialTask()&&!targetTasks.contains(temp.toString())){
				result.add(task);
				targetTasks.add(temp.toString());
			}
			temp.delete(0, temp.length());
		}
		return result;
	}
	private WorkflowTask cloneTask(WorkflowInstance instance,WorkflowTask task){
		WorkflowTask targetTask = task.clone();
		targetTask.setCreatedTime(new Date());
		targetTask.setId(null);
		targetTask.setActive(TaskState.WAIT_TRANSACT.getIndex());
		targetTask.setRead(false);
		targetTask.setTransactDate(null);
		targetTask.setTaskProcessingResult(null);
		targetTask.setSendingMessage(true);
		targetTask.setVisible(true);
		targetTask.setUrl(getTaskUrl(instance.getProcessDefinitionId()));
		return targetTask;
	}
	/*
	 * 退回流转历史处理
	 * @param instanceId
	 * @param currentTaskName
	 * @param historyNames
	 */
	@Transactional(readOnly=false)
	private void taskJumpHistoryProcess(String instanceId, WorkflowTask task, String currentTaskName){
		List<InstanceHistory> resultHistories = new ArrayList<InstanceHistory>(); 
		String historyMessage = new StringBuilder(dateFormat.format(new Date()))
			.append(COMMA).append(PROCESS_JUMPTASK).append(DELTA_START)
			.append(currentTaskName).append(DELTA_END).append("\n")
			.append(dateFormat.format(new Date())).append(COMMA).append(task.getTransactorName()).append(TASK_CANCEL).toString();
		//流程退回到
		InstanceHistory history = new InstanceHistory(task.getCompanyId(), instanceId, InstanceHistory.TYPE_TASK, historyMessage, task.getName());
		history.setEffective(false);
		history.setCreatedTime(new Date());
		history.setExecutionId(instanceId);
		resultHistories.add(history);
		
		historyMessage = new StringBuilder(dateFormat.format(new Date()))
			.append(COMMA).append(PROCESS_LEAVE).append(DELTA_START)
			.append(task.getName()).append(DELTA_END).toString();
		//流程离开
		history = new InstanceHistory(task.getCompanyId(), instanceId, InstanceHistory.TYPE_FLOW_LEAVE, historyMessage, task.getName());
		history.setCreatedTime(new Date());
		history.setExecutionId(instanceId);
		resultHistories.add(history);
		instanceHistoryManager.saveHistories(resultHistories);
		//流程进入
		generateFlowHistory(instanceId, instanceId, currentTaskName, PROCESS_ENTER);
	}
	
	/*
	 * 退回流转历史处理
	 * @param instanceId
	 * @param currentTaskName
	 * @param historyNames
	 */
	private void gobackTaskHistoryProcess(String instanceId, WorkflowTask task, String currentTaskName){
		List<InstanceHistory> resultHistories = new ArrayList<InstanceHistory>(); 
		String historyMessage = new StringBuilder(dateFormat.format(new Date()))
			.append(COMMA).append(PROCESS_GOBACKTASK).append(DELTA_START)
			.append(currentTaskName).append(DELTA_END).append("\n")
			.append(dateFormat.format(new Date())).append(COMMA).append(task.getTransactorName()).append(TASK_BACK).toString();
		//流程退回到
		InstanceHistory history = new InstanceHistory(task.getCompanyId(), instanceId, InstanceHistory.TYPE_TASK, historyMessage, task.getName());
		history.setEffective(false);
		history.setCreatedTime(new Date());
		history.setExecutionId(instanceId);
		resultHistories.add(history);
		
		historyMessage = new StringBuilder(dateFormat.format(new Date()))
			.append(COMMA).append(PROCESS_LEAVE).append(DELTA_START)
			.append(task.getName()).append(DELTA_END).toString();
		//流程离开
		history = new InstanceHistory(task.getCompanyId(), instanceId, InstanceHistory.TYPE_FLOW_LEAVE, historyMessage, task.getName());
		history.setCreatedTime(new Date());
		history.setExecutionId(instanceId);
		resultHistories.add(history);
		instanceHistoryManager.saveHistories(resultHistories);
		
		//流程进入
		generateFlowHistory(instanceId, instanceId, currentTaskName, PROCESS_ENTER);
	}
	
	
	/**
	 * 完成任务
	 * @param taskId
	 * @param operation
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeWorkflowTask(Long taskId, TaskProcessingResult operation,TaskSetting setting){
		WorkflowTask task = getTask(taskId);
		return completeWorkflowTask(task, operation,setting);
	}
	/**
	 * 完成任务
	 * @param task
	 * @param operation
	 * @param isCreateSpecialTask
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeWorkflowTask(WorkflowTask task, TaskProcessingResult operation,TaskSetting setting){
		return complete(task,operation,setting);
	}
	
	/**
	 * 完成任务
	 * @param task
	 * @param operation
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeWorkflowTaskAndSaveData(WorkflowTask task, TaskProcessingResult operation,TaskSetting setting){
		workflowInstanceManager.saveData(task, null);
		return completeWorkflowTask(task,operation,setting);
	}
	
	/**
	 * 完成任务
	 * @param task
	 * @param operation
	 * @param isCreateSpecialTask
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeWorkflowTask(WorkflowTask task, TaskProcessingResult operation){
		Assert.notNull(task,"task任务不能为null");
		return complete(task,operation,TaskSetting.getTaskSettingInstance());
	}
	/**
	 * 完成交互的任务
	 * @param task
	 * @param operation
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeInteractiveWorkflowTask(Long taskId){
		WorkflowTask task = getTask(taskId);
		return completeInteractiveWorkflowTask(task);
	}
	
	/**
	 * 完成交互的任务
	 * @param task
	 * @param operation
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task ){
		return completeInteractiveWorkflowTask(task,TaskSetting.getTaskSettingInstance());
	}
	/**
	 * 完成交互的任务
	 * @param task
	 * @param operation
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task, TaskSetting setting){
		return complete(task,task.getTaskProcessingResult(),setting.setReturnUrl(false));
	}
	
	
	
	
	/**
	 * 完成交互的任务
	 * @param task
	 * @param operation
	 * @param transcators 下一环节办理人
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeInteractiveWorkflowTask(Long taskId, String allOriginalUsers,String... transcators){
		WorkflowTask task = getTask(taskId);
		return completeInteractiveWorkflowTask(task,allOriginalUsers,transcators);
	}
	/**
	 * 完成交互的任务
	 * @param task
	 * @param allOriginalUsers 原办理人
	 * @param transcators 下一环节办理人
	 * @return
	 */

	@Transactional(readOnly=false)
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task,String allOriginalUsers, String... transcators){
		return completeInteractiveWorkflowTask(task,Arrays.asList(transcators),allOriginalUsers);
	}
	
	
	/**
	 * 完成交互的任务
	 * @param task
	 * @param operation
	 * @param transcators 下一环节办理人
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeInteractiveWorkflowTask(Long taskId, Collection<String> transcators,String allOriginalUsers ){
		WorkflowTask task = getTask(taskId);
		return completeInteractiveWorkflowTask(task,transcators,allOriginalUsers);
	}
	
		
	/**
	 * 完成交互的任务(调用)
	 * @param task
	 * @param operation
	 * @param transcators 下一环节办理人
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task, Collection<String> transcators,String allOriginalUsers){
		return completeInteractiveWorkflowTask(task, transcators,TaskSetting.getTaskSettingInstance().setAllOriginalUsers(allOriginalUsers));
	}
	
	/**
	 * 完成交互的任务(调用)
	 * @param task
	 * @param operation
	 * @param transcators 下一环节办理人
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task,TaskSetting setting, String... transcators){
		return completeInteractiveWorkflowTask(task,Arrays.asList(transcators),setting);
	}
	/**
	 * 完成交互的任务
	 * @param task
	 * @param operation
	 * @param transcators 下一环节办理人
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeInteractiveWorkflowTask(WorkflowTask task, Collection<String> transcators,TaskSetting setting){
		log.debug(PropUtils.LOG_METHOD_BEGIN+"完成任务,TaskService+completeInteractiveWorkflowTask(WorkflowTask task, Collection<String> transcators,TaskSetting setting)"+PropUtils.LOG_FLAG);
		//当线程1执行到put，线程2执行到get判断是否为null时，会出现value值不为同一对象的问题，如果此处不加锁，会影响下面的锁【synchronized (instanceIds.get(task.getProcessInstanceId()))】失败，所以要在此处以TaskService对象加锁。
		synchronized(this){
			//将实例id放入map中，用于同步处理时调用
			if(instanceIds.get(task.getProcessInstanceId())==null){
				instanceIds.put(task.getProcessInstanceId(), task.getProcessInstanceId());
			}
		}
		//以实例id为锁，只锁定完成同一个实例的不同任务的线程，如果锁定整个方法会导致所有的任务均变慢（包括不同实例的任务）
		synchronized (instanceIds.get(task.getProcessInstanceId())) {
			String bkySpecial=PropUtils.getProp("bky.special");
			if("true".equals(bkySpecial)){//是否是bky特制的功能,当受托人和委托人是同一部门时不生成委托任务
				if(task.getMoreTransactor()){//是多人办理环节才需自动完成该办理人的其他未显示的委托任务
					//根据办理人查待办理的委托任务
					List<WorkflowTask> tasks = workflowTaskService.getActivityTrustorTasksByTransactor(task.getProcessInstanceId(), task.getTransactor(),task.getId());
					for(WorkflowTask wt:tasks){
						complete(wt,task.getTaskProcessingResult(),setting.setReturnUrl(false));
					}
				}
			}
			//完成自己当前的任务
			CompleteTaskTipType completeTaskTipType = complete(task,task.getTaskProcessingResult(),setting.setReturnUrl(false));
			Execution exec=processEngine.getExecutionService().findExecutionById(task.getExecutionId());
			log.debug(PropUtils.LOG_CONTENT+"processEngine.getExecutionService().findExecutionById(task.getExecutionId())"+PropUtils.LOG_FLAG+exec);
			if(exec!=null){
				Object assign=processEngine.getExecutionService().getVariable(task.getExecutionId(), CommonStrings.TRANSACTOR_ASSIGNMENT);
				log.debug(PropUtils.LOG_CONTENT+"指定的办理人"+PropUtils.LOG_FLAG+assign);
				if(assign!=null && PRE_TRANSACTOR_ASSIGN.equals(assign.toString())){//子流程/上一环节办理人指定时
					SubProcessParse subprocessParse=(SubProcessParse)processEngine.getExecutionService().getVariable(task.getExecutionId(),CommonStrings.SUBPROCESS_PARSE);
					ActivityExecution execution=(ActivityExecution)processEngine.getExecutionService().findExecutionById(subprocessParse.getExecutionId());
					execution.removeVariable(CommonStrings.TRANSACTOR_ASSIGNMENT);
					execution.removeVariable(CommonStrings.SUBPROCESS_PARSE);
					completeTask(task);
					Map<TaskTransactorCondition, String> transactor = DefinitionXmlParse.getTaskTransactor(subprocessParse.getParentDefinitionId(),subprocessParse.getActivityName());
					log.debug(PropUtils.LOG_CONTENT+"发起子流程开始"+PropUtils.LOG_FLAG);
					this.startSubProcessWorkflow(transactor, subprocessParse,transcators);
					log.debug(PropUtils.LOG_CONTENT+"发起子流程结束"+PropUtils.LOG_FLAG);
					log.debug(PropUtils.LOG_CONTENT+"完成任务返回值"+PropUtils.LOG_FLAG+completeTaskTipType);
					log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completeInteractiveWorkflowTask(WorkflowTask task, Collection<String> transcators,TaskSetting setting)"+PropUtils.LOG_FLAG);
					return CompleteTaskTipType.OK.setContent("子流程已创建");
				}else{
					if(completeTaskTipType==CompleteTaskTipType.RETURN_URL||completeTaskTipType==CompleteTaskTipType.SINGLE_TRANSACTOR_CHOICE) {
						this.setTasksTransactor(task, transcators);
						return CompleteTaskTipType.OK.setContent("办理人已指定");
					}
					log.debug(PropUtils.LOG_CONTENT+"完成任务返回值"+PropUtils.LOG_FLAG+completeTaskTipType);
					log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completeInteractiveWorkflowTask(WorkflowTask task, Collection<String> transcators,TaskSetting setting)"+PropUtils.LOG_FLAG);
					return completeTaskTipType;
				}
			}else{
				log.debug(PropUtils.LOG_CONTENT+"完成任务返回值"+PropUtils.LOG_FLAG+completeTaskTipType);
				log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completeInteractiveWorkflowTask(WorkflowTask task, Collection<String> transcators,TaskSetting setting)"+PropUtils.LOG_FLAG);
				return completeTaskTipType;
			}
		}
	}
	
	@Transactional(readOnly=false)
	  public CompleteTaskTipType assignTask(Long taskId, String transcator)
	  {
		WorkflowTask task = getTask(taskId);
    	//被指派人的新任务
    	WorkflowTask targetTask = task.clone();
    	//设置指派人任务已完成
    	task.setTaskProcessingResult(null);
    	task.setActive(TaskState.ASSIGNED.getIndex());
    	task.setTransactDate(new Date());
    	task.setNextTasks("assign to:" + transcator);
    	//新任务办理人
    	targetTask.setId(null);
		targetTask.setTransactor(transcator);
		User user = userManager.getUserByLoginName(transcator);
		if(user!=null){
			targetTask.setTransactorName(user.getName());
		}
    	targetTask.setRead(false);
    	List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
    	tasks.add(task);
    	tasks.add(targetTask);
    	saveTasks(tasks);
	    generateAssignTaskHistory(task, targetTask.getTransactorName());
	    this.log.debug("*** assignTask 方法结束");
	    return CompleteTaskTipType.ASSIGN_TASK;
	  }
	@Deprecated
	@Transactional(readOnly=false)
	  public CompleteTaskTipType assignTask(Long taskId, Collection<String> transactors)
	  {
		WorkflowTask task = getTask(taskId);
    	//被指派人的新任务
    	WorkflowTask targetTask = task.clone();
    	//设置指派人任务已完成
    	task.setTaskProcessingResult(null);
    	task.setActive(TaskState.ASSIGNED.getIndex());
    	task.setTransactDate(new Date());
    	task.setNextTasks("assign to:" + transactors);
    	//新任务办理人
    	targetTask.setId(null);
    	for(String transactor:transactors){
    		targetTask.setTransactor(transactor);
    		User user = userManager.getUserByLoginName(transactor);
    		if(user!=null){
    			targetTask.setTransactorName(user.getName());
    		}
    	}
    	targetTask.setRead(false);
    	List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
    	tasks.add(task);
    	tasks.add(targetTask);
    	saveTasks(tasks);
	      generateAssignTaskHistory(task, targetTask.getTransactorName());
	      this.log.debug("*** assignTask 方法结束");
	      return CompleteTaskTipType.ASSIGN_TASK;
	  }

	@Transactional(readOnly=false)
	  private void generateAssignTaskHistory(WorkflowTask task,String transcator)
	  {
	    InstanceHistory ih = new InstanceHistory();
	    ih.setCompanyId(task.getCompanyId());
	    ih.setType(InstanceHistory.TYPE_TASK);
	    ih.setInstanceId(task.getProcessInstanceId());
	    ih.setExecutionId(task.getExecutionId());
	    ih.setTaskName(task.getName());
	    ih.setTaskId(task.getId());
	    ih.setCreatedTime(new Date());
	    ih.setSpecialTask(task.isSpecialTask());
	    StringBuilder msg = new StringBuilder();
	    msg.append(dateFormat.format(ih.getCreatedTime())).append
	      (", ").append(ContextUtils.getUserName()).append("指派给了[").append
	      (transcator.replace("[", "").replace("]", "")).append("]");
	    ih.setTransactionResult(msg.toString());
	    ih.setTransactor(ContextUtils.getUserName() + "[ " + task.getTransactor() + " ]");
	    instanceHistoryManager.saveHistory(ih);
	  }
	private static final String AND = "&&";
	private static final String OR = "||";
	private boolean parseCondition(String express,TaskProcessingResult operation,UserParseCalculator upc){
		//${user} operator.text.et '吴荣[wurong]' condition.operator.and ${department} operator.text.et 'EIT业务部' condition.operator.and ${role} operator.text.et '普通员工'
			if(StringUtils.isEmpty(express)) return false;
			if(express.trim().equalsIgnoreCase("true")) return true;
			String temp = express;
			String[] strs = BeanShellUtil.splitExpression(express);
			log.info("分割后的原子表达式为：" + Arrays.toString(strs));
			
			Boolean result = false;
			for(int i=0;i<strs.length;i++){
				log.info("开始分析原子表达式：" + strs[i]);
				result = computeAtomicExpression(strs[i],operation,upc);
				log.info("原子表达式：" + strs[i] + "的分析结果为 " + result);
				temp = StringUtils.replace(temp, strs[i].trim(), result.toString());
				log.info("将原子表达式替换为它的结果后：" + temp );
			}
			temp = temp.replaceAll(LogicOperator.AND.getCode(), AND);
			temp = temp.replaceAll(LogicOperator.OR.getCode(), OR);
			log.info("最终该流向的表达式为：" + temp);
			boolean expressResult = BeanShellUtil.evel(temp);
			return expressResult;
	}
	private boolean computeAtomicExpression(String atomicExpress,TaskProcessingResult operation,UserParseCalculator upc){
		if(StringUtils.isEmpty(atomicExpress)) return false;
		boolean result = false;
		atomicExpress = atomicExpress.trim();
		if(StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_NAME)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_ROLE)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_SUPERIOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.DOCUMENT_CREATOR_WORKGROUP)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_NAME)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_ROLE)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_SUPERIOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_UPSTAGE_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.CURRENT_TRANSACTOR_WORKGROUP)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_NAME)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_ROLE)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_SUPERIOR_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_UPSTAGE_DEPARTMENT)
			||StringUtils.contains(atomicExpress, CommonStrings.PREVIOUS_TRANSACTOR_WORKGROUP)){
			result = upc.execute(atomicExpress);
		}else if(StringUtils.contains(atomicExpress, CommonStrings.APPROVAL_RESULT)){
			result = BeanShellUtil.execute(atomicExpress,DataType.TEXT,CommonStrings.APPROVAL_RESULT,operation.getName());
		}else{
			if(upc.getFormView().isStandardForm()){
				log.info("标准表单");
				//标准表单的处理
				
				//根据表单id获得对应的类
				String className = upc.getFormView().getDataTable().getEntityName();
				log.info("实体类名：" + className);
				//根据表名和id获得实体
				Object entity = generalDao.getObject(className, upc.getDataId());
				log.info("查询得到的实体:" + entity);
				String name = StringUtils.substringBetween(atomicExpress, SQUARE_BRACKETS_LEFT, SQUARE_BRACKETS_RIGHT);
				log.info("字段名：" + name);
				FormControl field = getFormControl(name,upc.getFormView());
				log.info("对应字段为：" + field);
				try {
					Object value = BeanUtils.getProperty(entity, name);
					if(value==null) throw new RuntimeException("Field:"+field.getTitle()+" no value.");
					log.info("自动对应的值" + value.toString());
					result = BeanShellUtil.execute(atomicExpress,field.getDataType(),field.getTitle()+SQUARE_BRACKETS_LEFT+name+SQUARE_BRACKETS_RIGHT,value.toString());
					log.info("判断结果为：" + result);
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
				
			}else if(!upc.getFormView().isStandardForm()){
				log.info("自定义表单处理");
			//自定义表单的处理
				//根据表单id获得对应的表名
				String tableName = upc.getFormView().getDataTable().getName();
				log.info("表名：" + tableName);
				log.info("数据ID：" + upc.getDataId());
				//根据表名和id获得对应记录数据封装的MAP
				Map dataMap = formViewManager.getDataMap(tableName,upc.getDataId());
				log.info("数据map：" + dataMap);
				String ch_name = StringUtils.substringBefore(atomicExpress, SQUARE_BRACKETS_LEFT);
				String name =StringUtils.substringBetween(atomicExpress, SQUARE_BRACKETS_LEFT, SQUARE_BRACKETS_RIGHT);
				FormControl field = getFormControl(name,upc.getFormView());
				String value = "";
				if(dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+name)==null){
						if(field.getDataType()==DataType.AMOUNT||field.getDataType()==DataType.NUMBER){
							value = "0";
						}else if(field.getDataType().equals(DataType.DATE.toString())||field.getDataType().equals(DataType.TIME.toString())){
							if(dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+name)==null) throw new RuntimeException("Field:"+ch_name+" no value.");
						}
				}else{
					value = dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+name).toString();
				}
				log.info("对应值为：" + value);
				result = BeanShellUtil.execute(atomicExpress,field.getDataType(),ch_name + SQUARE_BRACKETS_LEFT+name+SQUARE_BRACKETS_RIGHT,value);
				log.info("判断结果为：" + result);
			}
		}
		return result;
	}
	
	private FormControl getFormControl(String name,FormView form){
		List<FormControl> fields = formViewManager.getControls(form.getId());
		for(FormControl formControl : fields){
			if(formControl.getName().equals(name)) return formControl;
		}
		return null;
	}
	
	/**
	 * 完成自由流的任务
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeCustomProcess(Long taskId,TaskProcessingResult operation,TaskSetting setting){
		return this.completeCustomProcess(this.getTask(taskId), operation, setting);
	}
	/**
	 * 完成自由流的任务
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeCustomProcess(WorkflowTask task,TaskProcessingResult operation,TaskSetting setting){
		task.setActive(TaskState.COMPLETED.getIndex());
		task.setTaskProcessingResult(operation);
		task.setTransactDate(new Date());
		this.saveTask(task);
		return CompleteTaskTipType.OK.setContent("已完成");
	}
	
	/*
	 *@param isReturnUrl 是否执行返回url
	 */
	@Transactional(readOnly=false)
	private CompleteTaskTipType complete(WorkflowTask task, TaskProcessingResult operation,TaskSetting setting){
		if(task==null){log.debug("complete中，task任务不能为null");throw new RuntimeException("complete中，task任务不能为null");}
		log.debug(PropUtils.LOG_METHOD_BEGIN+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
		log.debug(PropUtils.LOG_CONTENT+"当前任务"+PropUtils.LOG_FLAG+task);
		log.debug(PropUtils.LOG_CONTENT+"当前操作"+PropUtils.LOG_FLAG+operation);
		log.debug(PropUtils.LOG_CONTENT+"任务设置的信息TaskSetting"+PropUtils.LOG_FLAG+setting);
		if(!task.getTransactor().equals(ContextUtils.getLoginName())){
			log.debug(PropUtils.LOG_CONTENT+"当前登录名和当前任务办理人不同。当前登录名为"+ContextUtils.getLoginName()+",当前任务办理人为"+task.getTransactor()+PropUtils.LOG_FLAG);
			log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
			return CompleteTaskTipType.MESSAGE.setContent("当前登录名和当前任务办理人不同");
		}
		CompleteTaskTipType result = null;
		if(task.getActive().equals(TaskState.CANCELLED.getIndex())){
			log.debug(PropUtils.LOG_CONTENT+"该任务已失效"+PropUtils.LOG_FLAG);
			log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
			return CompleteTaskTipType.MESSAGE.setContent("该任务已失效！");
		}
		if(task.getActive().equals(TaskState.COMPLETED.getIndex())){
			log.debug(PropUtils.LOG_CONTENT+"该任务已完成"+PropUtils.LOG_FLAG);
			log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
			return CompleteTaskTipType.MESSAGE.setContent("该任务已完成！");
		}
		if(task.getActive().equals(TaskState.ASSIGNED.getIndex())){
			log.debug(PropUtils.LOG_CONTENT+"该任务已指派"+PropUtils.LOG_FLAG);
			log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
			return CompleteTaskTipType.MESSAGE.setContent("该任务已指派！");
		}
		if(task.getActive().equals(TaskState.HAS_DRAW_OTHER.getIndex())){
			log.debug(PropUtils.LOG_CONTENT+"该任务他人已领取"+PropUtils.LOG_FLAG);
			log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
			return CompleteTaskTipType.MESSAGE.setContent("该任务他人已领取！");
		}
		if(operation!=null){
			task.setTaskProcessingResult(operation);
			this.saveTask(task);
		}
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		if(instance==null){
			log.debug("流程实例不能为null");
			throw new RuntimeException("流程实例不能为null");
		}
		//阅办环节,不影响流程流转
		if(TaskProcessingMode.TYPE_READ.equals(task.getProcessingMode())){
			log.debug(PropUtils.LOG_CONTENT+"任务为阅办环节"+PropUtils.LOG_FLAG);
			log.debug(PropUtils.LOG_CONTENT+"任务信息"+PropUtils.LOG_FLAG+task.getTransactor()+"的任务："+task.getName()+"为阅办环节,当前已阅完");
			task.setTaskProcessingResult(operation);
			task.setTransactDate(new Date());
			task.setActive(TaskState.COMPLETED.getIndex());
			this.saveTask(task);
			//人工环节历史记录
    		generateTaskHistory(task, operation);
    		log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
			return CompleteTaskTipType.OK.setContent("已阅完");
		}
		if(instance!=null){
			log.debug(PropUtils.LOG_CONTENT+"当前任务对应的实例ID"+PropUtils.LOG_FLAG+instance.getProcessInstanceId());
			log.debug(PropUtils.LOG_CONTENT+"当前任务对应的实例"+PropUtils.LOG_FLAG+instance);
			processEngine.getExecutionService().setVariable(task.getExecutionId(), CommonStrings.PRIORITY, instance.getPriority());
		}
		if( instance.getFirstTaskId()==null){
			//主要用于第一环节任务需要领取时，一般出现在子流程中
			instance.setCreator(task.getCreator());
			instance.setCreatorName(task.getCreatorName());
			instance.setFirstTaskId(task.getId());
			workflowInstanceManager.saveWorkflowInstance(instance);
			executionVariableCommand(new ExecutionVariableCommand(task.getExecutionId(),CommonStrings.CREATOR,task.getCreator()));
		}
		
		
		if((result=isNeedChoiceTache(task))!=null){
			log.debug(PropUtils.LOG_CONTENT+"需要选择环节result"+PropUtils.LOG_FLAG+task.getTransactor()+"的任务："+task.getName()+"需要选择环节。可选择的环节个数为:"+result.getCanChoiceTaches().size());
			log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
			return result;
		}
		//办理前是否需要指定办理人
		result = getBeforeTaskSubmitUrl(task,operation,setting);
		log.debug(PropUtils.LOG_CONTENT+"办理前是否需要指定办理人result"+PropUtils.LOG_FLAG+result);
		if(result!=null){
			log.debug(PropUtils.LOG_CONTENT+"办理前需要指定办理人的信息"+PropUtils.LOG_FLAG+task.getTransactor()+"的任务："+task.getName()+"在提交前需要返回url。方法返回url为:"+result.getContent());
			log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
			return result;
		}
		result=isNeedAssigningTransactor(instance,task);
		log.debug(PropUtils.LOG_CONTENT+"环节是否需要指定办理人result"+PropUtils.LOG_FLAG+result);
		if(result!=null){
			log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
    		return result;
		}
		//办理前执行事件处理
		result = beforSubmit(task,operation);
		log.debug(PropUtils.LOG_CONTENT+"办理前执行事件处理result"+PropUtils.LOG_FLAG+result);
		if(result!=null){
			log.debug(PropUtils.LOG_CONTENT+"办理前执行事件处理信息"+PropUtils.LOG_FLAG+task.getTransactor()+"的任务："+task.getName()+"在提交前执行出错。方法返回信息为:"+result.getContent());
			log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
			return result;
		}
		//人工环节任务历史 
    	if(operation != null){
    		executionVariableCommand(new ExecutionVariableCommand(task.getExecutionId(),CommonStrings.ALL_ORIGINAL_USERS,setting.getAllOriginalUsers()));
    		task.setTaskProcessingResult(operation);
    		//自动填写域
			log.debug(PropUtils.LOG_CONTENT+"办理后自动填写域开始..."+PropUtils.LOG_FLAG);
			saveAutomaticallyFilledField(task.getProcessInstanceId(),task.getName(), Struts2Utils.getText(operation.getName()));
			log.debug(PropUtils.LOG_CONTENT+"办理后自动填写域结束..."+PropUtils.LOG_FLAG);
			result=executionCompanyTask(task, operation,setting);
			return result;
    	}else{
    		log.debug(PropUtils.LOG_CONTENT+"请在完成任务时，传入办理任务时执行的操作"+PropUtils.LOG_FLAG);
    		log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+completecomplete(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
    		return CompleteTaskTipType.MESSAGE.setContent("请在完成任务时，传入办理任务时执行的操作");
    	}
    	
	}
	
	/*
	 *执行任务提交前事件 
	 */
	@Transactional(readOnly=false)
	private CompleteTaskTipType beforSubmit(WorkflowTask task, TaskProcessingResult operation){
		Assert.notNull(task,"task任务不能为null");
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		if(wi==null){
			log.debug("流程实例不能为null");
			throw new RuntimeException("流程实例不能为null");
		}
		String beanName = DefinitionXmlParse.getBeforeTaskSubmitImpClassName(wi.getProcessDefinitionId(), task.getName());
		if(StringUtils.isNotEmpty(beanName)){
			BeforeTaskSubmit obj = (BeforeTaskSubmit) ContextUtils.getBean(beanName);
			if(obj==null){log.debug("执行任务提交前事件 时,bean不能为null"); throw new RuntimeException("执行任务提交前事件 时,bean不能为null");}
			boolean result = obj.execute(wi.getDataId(),operation);
			if(!result){
				return CompleteTaskTipType.MESSAGE.setContent(DefinitionXmlParse.getBeforeTaskSubmitResultMessage(wi.getProcessDefinitionId(), task.getName()));
			}
		}
		return null;
	}
	/*
	 * 检查办理任务前，是否需要返回用户设定的url
	 */
	@Transactional(readOnly=false)
	private CompleteTaskTipType getBeforeTaskSubmitUrl(WorkflowTask task,TaskProcessingResult operation,TaskSetting setting){
		Assert.notNull(task,"任务不能为null");
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		if(setting.isReturnUrl()){
			List<String[]> urls = DefinitionXmlParse.getBeforeTaskSubmitUrl(wi.getProcessDefinitionId(), task.getName());
			if(urls!=null){
				Assert.notNull(wi.getFormId(),"表单id不能为null");
				FormView form = formViewManager.getFormView(wi.getFormId());
				if(form==null){
					log.debug("FormView表单不能为null");
					throw new RuntimeException("FormView表单不能为null");
				}
				UserParseCalculator upc = new UserParseCalculator();
				upc.setDataId(wi.getDataId());
				upc.setFormView(form);
				upc.setDocumentCreator(wi.getCreator());
				if(StringUtils.isEmpty(task.getTrustor())){
					upc.setCurrentTransactor(task.getTransactor());
				}else{//当是委托任务时，当前办理人为委托人
					String delegateTransactor = delegateManager.getDelegateMainName(
							task.getCompanyId(), task.getTrustor(),wi.getProcessDefinitionId() , task.getName());
					if(StringUtils.isNotEmpty(delegateTransactor)&&delegateTransactor.equals(task.getTransactor())){
						upc.setCurrentTransactor(task.getTrustor());
					}else{
						task.setTrustor(null);
						task.setTrustorName(null);
						upc.setCurrentTransactor(task.getTransactor());
					}
				}
				for(String[] urlArray:urls){
					if(parseCondition(urlArray[0],operation,upc)){
						task.setTaskProcessingResult(operation);
						this.saveTask(task);
						return CompleteTaskTipType.RETURN_URL.setContent(urlArray[1]);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 完成环节选择
	 * @param taskId
	 * @param map
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeTacheChoice(Long taskId, String transitionName) {
		Assert.notNull(taskId,"完成环节选择任务时，任务id不能为null");
		CompleteTaskTipType completeTaskTipType = null;
		WorkflowTask task = this.getTask(taskId);
		Assert.notNull(task,"完成环节选择任务时，任务不能为null");
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		Assert.notNull(instance,"完成环节选择任务时，流程实例不能为null");
		String originalActivityName=instance.getCurrentActivity();
		processEngine.getExecutionService().signalExecutionById(task.getExecutionId(), transitionName);
		String parentExecutionId = null;
		Execution execution = processEngine.getExecutionService().findExecutionById(task.getExecutionId());
		Assert.notNull(execution,"完成环节选择任务时，execution不能为null");
		Execution parentExecution = execution.getParent();
		if(parentExecution != null){
			parentExecutionId = parentExecution.getId();
		}
		task.setNextTasks(((ActivityExecution)execution).getActivityName());
		completeTaskTipType  = completeWfTask(instance,task, parentExecutionId);
		saveTask(task);
		if(completeTaskTipType==null){
			//影响setInstanceCurrentActivity方法中获得activityNames的值
			instance.setCurrentActivity(originalActivityName);
			setInstanceCurrentActivity(instance, task);
			completeTaskTipType = CompleteTaskTipType.OK.setContent("你已完成了任务");
			if(instance.getProcessState()==ProcessState.UNSUBMIT){
				instance.setProcessState(ProcessState.SUBMIT);
				instance.setSubmitTime(new Timestamp(System.currentTimeMillis()));
				this.workflowInstanceManager.saveWorkflowInstance(instance);
				FormView form = formViewManager.getFormView(instance.getFormId());
				Assert.notNull(form,"完成环节选择任务时，流程对应的表单不能为null");
				if(form.isStandardForm()){
					Assert.notNull(form.getDataTable(),"完成环节选择任务时，表单对应的数据表不能为null");
					Assert.notNull(form.getDataTable().getEntityName(),"完成环节选择任务时，表单对应的数据表实体类名不能为null");
					Object entity = generalDao.getObject(form.getDataTable().getEntityName(), instance.getDataId());
					try {
						BeanUtils.setProperty(entity, "workflowInfo.processState", ProcessState.SUBMIT);
						generalDao.save(entity);
					} catch (IllegalAccessException e) {
						log.debug(e);
						throw new RuntimeException();
					} catch (InvocationTargetException e) {
						log.debug(e);
						throw new RuntimeException();
					}
				}else{
					jdbcDao.updateTable("UPDATE "+form.getDataTable().getName()+" SET PROCESS_STATE="+ProcessState.SUBMIT.getIndex()+" WHERE  ID="+instance.getDataId());
				}
			}
		}
    	
    	return completeTaskTipType;
	}
	
	@Transactional(readOnly=false)
	private CompleteTaskTipType executionCompanyTask(WorkflowTask task, TaskProcessingResult operation,TaskSetting setting){
		log.debug(PropUtils.LOG_METHOD_BEGIN+"完成任务,TaskService+executionCompanyTask(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
		/* 如果是多人办理环节，最后一个人办理完，任务才算完成。
		 * 如果是单人办理环节，第一个办理的人领取任务。
		 * 
		 */
		if(task==null){log.debug("executionCompanyTask中，task任务不能为null");throw new RuntimeException("executionCompanyTask中，task任务不能为null");}
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		if(instance==null){log.debug("executionCompanyTask中，流程实例不能为null");throw new RuntimeException("executionCompanyTask中，流程实例不能为null");}
		log.debug(PropUtils.LOG_CONTENT+"该任务对应的实例"+PropUtils.LOG_FLAG+instance);
		CompleteTaskTipType completeTaskTipType = null;
    	String parentExecutionId = null;
    	if(task.getMoreTransactor()){
    		log.debug(PropUtils.LOG_CONTENT+"任务信息"+PropUtils.LOG_FLAG+task.getTransactor()+"的任务："+task.getName()+"为多人办理环节");
        	if(isCompleteJbpmTask(task,instance)){
        		log.debug(PropUtils.LOG_CONTENT+"可以完成JBPM任务"+PropUtils.LOG_FLAG);
        		log.debug(PropUtils.LOG_CONTENT+"完成JBPM任务开始"+PropUtils.LOG_FLAG);
        		parentExecutionId = completeJbpmTask(task, CommonStrings.COLLECTIVE_OPERATION,setting);
        		log.debug(PropUtils.LOG_CONTENT+"完成JBPM任务结束"+PropUtils.LOG_FLAG);
        		completeTaskTipType = isNeedChoiceTache(task);
        		log.debug(PropUtils.LOG_CONTENT+"是否需要选择环节"+PropUtils.LOG_FLAG+completeTaskTipType);
    			if(completeTaskTipType!=null){
    				log.debug(PropUtils.LOG_CONTENT+"需要选择环节"+PropUtils.LOG_FLAG+"需要选择环节。可选择的环节个数为:"+completeTaskTipType.getCanChoiceTaches().size());
    				log.debug(PropUtils.LOG_METHOD_BEGIN+"完成任务,TaskService+executionCompanyTask(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
    				return completeTaskTipType;
    			}
    			completeTaskTipType = completeWfTask(instance,task, parentExecutionId);
        	}else{
        		if(task.getActive()!=1){// 不是 “等待设置办理人”
        			generateTaskHistory(task);
        		}
        	}
    	}else{
    		log.debug(PropUtils.LOG_CONTENT+"任务信息"+PropUtils.LOG_FLAG+task.getTransactor()+"的任务："+task.getName()+"为单人办理环节");
    		//完成JBPM所对应的任务
    		if(isOneCompleteJbpmTask(task,instance)){
    			log.debug(PropUtils.LOG_CONTENT+"可以完成JBPM任务"+PropUtils.LOG_FLAG);
        		log.debug(PropUtils.LOG_CONTENT+"完成JBPM任务开始"+PropUtils.LOG_FLAG);
    			parentExecutionId = completeJbpmTask(task, operation.toString(),setting);
    			log.debug(PropUtils.LOG_CONTENT+"完成JBPM任务结束"+PropUtils.LOG_FLAG);
    			completeTaskTipType = isNeedChoiceTache(task);
    			log.debug(PropUtils.LOG_CONTENT+"是否需要选择环节"+PropUtils.LOG_FLAG+completeTaskTipType);
    			if(completeTaskTipType!=null){
    				log.debug(PropUtils.LOG_CONTENT+"需要选择环节"+PropUtils.LOG_FLAG+"需要选择环节。可选择的环节个数为:"+completeTaskTipType.getCanChoiceTaches().size());
    				log.debug(PropUtils.LOG_METHOD_BEGIN+"完成任务,TaskService+executionCompanyTask(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
    				return completeTaskTipType;
    			}
    			log.debug(PropUtils.LOG_CONTENT+"完成任务开始"+PropUtils.LOG_FLAG);
    			completeTaskTipType = completeWfTask(instance,task, parentExecutionId);
    			log.debug(PropUtils.LOG_CONTENT+"完成任务返回值"+PropUtils.LOG_FLAG+completeTaskTipType);
    			log.debug(PropUtils.LOG_CONTENT+"完成任务结束"+PropUtils.LOG_FLAG);
    		}else{
        		if(task.getActive()!=1){// 不是 “等待设置办理人”
        			generateTaskHistory(task);
        		}
        	}
    	}
    	
    	saveTask(task);
    	if(completeTaskTipType==null){
    		setInstanceCurrentActivity(instance, task);
    		completeTaskTipType = CompleteTaskTipType.OK.setContent("你已完成了任务");
    		if(instance.getProcessState()==ProcessState.UNSUBMIT){
    			instance.setProcessState(ProcessState.SUBMIT);
    			instance.setSubmitTime(new Timestamp(System.currentTimeMillis()));
    			
        		this.workflowInstanceManager.saveWorkflowInstance(instance);
        		FormView form = formViewManager.getFormView(instance.getFormId());
        		if(form.isStandardForm()){
        			Object entity = generalDao.getObject(form.getDataTable().getEntityName(), instance.getDataId());
        			try {
    					BeanUtils.setProperty(entity, "workflowInfo.processState", ProcessState.SUBMIT);
    					generalDao.save(entity);
    				} catch (IllegalAccessException e) {
    					log.debug(e);
    					throw new RuntimeException();
    				} catch (InvocationTargetException e) {
    					log.debug(e);
    					throw new RuntimeException();
    				}
        		}else{
        			jdbcDao.updateTable("UPDATE "+form.getDataTable().getName()+" SET PROCESS_STATE="+ProcessState.SUBMIT.getIndex()+" WHERE  ID="+instance.getDataId());
        		}
        	}
    	}
    	log.debug(PropUtils.LOG_CONTENT+"完成任务返回值"+PropUtils.LOG_FLAG+completeTaskTipType);
    	log.debug(PropUtils.LOG_METHOD_END+"完成任务,TaskService+executionCompanyTask(WorkflowTask task, TaskTransact operation,TaskSetting setting)"+PropUtils.LOG_FLAG);
    	return completeTaskTipType;
	}
	
	/*
	 * 是否需要选择环节
	 */
	public CompleteTaskTipType isNeedChoiceTache(WorkflowTask task){
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		if(instance==null){
			log.debug("流程实例不能为null");
			throw new RuntimeException("流程实例不能为null");
		}
		Assert.notNull(instance.getFormId(),"表单id不能为null");
		FormView form = formViewManager.getFormView(instance.getFormId());
		Execution execution = processEngine.getExecutionService().findExecutionById(task.getExecutionId());
		if(execution!=null){
			for(String activityName:execution.findActiveActivityNames()){
				String tacheType = DefinitionXmlParse.getCurrentTacheType(instance.getProcessDefinitionId(),activityName);
				if(StringUtils.isNotEmpty(tacheType)&&tacheType.equals(WorkflowTacheType.CHOICE_TACHE.getCode())){
					task.setActive(TaskState.WAIT_CHOICE_TACHE.getIndex());
					this.saveTask(task);
					return  CompleteTaskTipType.TACHE_CHOICE_URL.setContent(!form.isStandardForm()?DEFAULT_CHOICE_TACHE_URL:DEFAULT_CHOICE_TACHE_URL_POP)
					.setCanChoiceTaches(DefinitionXmlParse.getChoiceTaches(instance.getProcessDefinitionId(),activityName));
				}
			}
		}
		return null;
	}
	/*
	 * 子流程是否设置了上一环节办理人设置
	 */
	public CompleteTaskTipType isSubProcessNeedChoiceTransactor(WorkflowTask task ){
		CompleteTaskTipType result = null;
		Execution execution=processEngine.getExecutionService().findExecutionById(task.getExecutionId());
		//判断是子流程
		if(execution!=null ){
			ProcessInstance jbpmInstance=processEngine.getExecutionService().findProcessInstanceById(task.getProcessInstanceId());
			//判断子流程没有结束
			if(jbpmInstance!=null){
				Object assign=processEngine.getExecutionService().getVariable(task.getExecutionId(), CommonStrings.TRANSACTOR_ASSIGNMENT);
				if(assign!=null && "${previousTransactorAssignment}".equals(assign.toString()) ){
					WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
					String url = DefinitionXmlParse.getPreviousTransactorAssignmentUrl(instance.getProcessDefinitionId(),task.getNextTasks());
					url = StringUtils.isEmpty(url)?DEFAULT_URL:url;
		    		result = CompleteTaskTipType.RETURN_URL.setContent(url);
				}
			}
		}
		return result;
	}
	
	
	/*
	 * 单人办理环节是否可以完成jbpm任务 
	 */
	private boolean isOneCompleteJbpmTask(WorkflowTask task , WorkflowInstance instance){
		List<WorkflowTask> parallelTasks = workflowTaskService.getTasksByActivity(
    			task.getCompanyId(), task.getExecutionId(), task.getName());
		return parallelTasks.size() == 1&&parallelTasks.get(0).getTransactor().equals(ContextUtils.getLoginName())&&(!parallelTasks.get(0).isSpecialTask()||parallelTasks.get(0).getName().equals(instance.getCurrentActivity()));
	}
	
	//如果任务只有一个而且办理人是自己的时候，完成任务。主要防止调用两次完成任务导致把别人的任务完成的情况
	private boolean isCompleteJbpmTask(WorkflowTask task , WorkflowInstance instance){
		if(task==null){log.debug("isCompleteJbpmTask方法中，任务参数不能为null");throw new RuntimeException("isCompleteJbpmTask方法中，任务参数不能为null");}
		if(instance==null){log.debug("isCompleteJbpmTask方法中，流程实例参数不能为null");throw new RuntimeException("isCompleteJbpmTask方法中，流程实例参数不能为null");}
		List<WorkflowTask> parallelTasks = workflowTaskService.getTasksByActivity(
    			task.getCompanyId(), task.getExecutionId(), task.getName());
		List<WorkflowTask> allTask = workflowTaskService.getWorkflowTasks(instance.getProcessInstanceId(),task.getName());
		double passNum = 1,totle=0;
		WorkflowTask tempTask;
		if(task.getProcessingMode()==TaskProcessingMode.TYPE_COUNTERSIGNATURE&&task.getTaskProcessingResult()==TaskProcessingResult.APPROVE){
			int passSetting = DefinitionXmlParse.getTransactPassRate(instance.getProcessDefinitionId(), task.getName());
			if(passSetting!=0){
				for(int i=0;i<allTask.size();i++){
					tempTask = allTask.get(i);
					if(!tempTask.isSpecialTask()&&(tempTask.getActive().equals(TaskState.WAIT_TRANSACT.getIndex())||tempTask.getActive().equals(TaskState.COMPLETED.getIndex())))totle++;
					if(!tempTask.isSpecialTask()&&tempTask.getActive().equals(TaskState.COMPLETED.getIndex())&&tempTask.getTaskProcessingResult()==TaskProcessingResult.APPROVE){
						passNum++;
					}
				}
				
				if(Math.round(passNum/totle*100)>=passSetting){
					cancelOtherTask(parallelTasks,task);
					return true;
				}
			}
		}else if(task.getProcessingMode()==TaskProcessingMode.TYPE_VOTE&&task.getTaskProcessingResult()==TaskProcessingResult.AGREEMENT){
			int passSetting = DefinitionXmlParse.getTransactPassRate(instance.getProcessDefinitionId(), task.getName());
			if(passSetting!=0){
				for(int i=0;i<allTask.size();i++){
					tempTask = allTask.get(i);
					if(!tempTask.isSpecialTask()&&(tempTask.getActive().equals(TaskState.WAIT_TRANSACT.getIndex())||tempTask.getActive().equals(TaskState.COMPLETED.getIndex())))totle++;
					if(!tempTask.isSpecialTask()&&tempTask.getActive().equals(TaskState.COMPLETED.getIndex())&&tempTask.getTaskProcessingResult()==TaskProcessingResult.AGREEMENT){
						passNum++;
					}
				}
				if(Math.round(passNum/totle*100)>=passSetting){
					cancelOtherTask(parallelTasks,task);
					return true;
				}
			}
		}
		return parallelTasks.size() == 1&&parallelTasks.get(0).getTransactor().equals(ContextUtils.getLoginName())&&(!parallelTasks.get(0).isSpecialTask()||parallelTasks.get(0).getName().equals(instance.getCurrentActivity()));
	}
	
	@Transactional(readOnly=false)
	private void cancelOtherTask(List<WorkflowTask> activityTasks ,WorkflowTask task){
		for(WorkflowTask tsk:activityTasks){
			if(!tsk.equals(task)){
				tsk.setActive(TaskState.CANCELLED.getIndex());
				this.saveTask(tsk);
			} 
		}
	}
	
	/**
	 * 完成分发任务
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeDistributeTask(Long taskId, List<String> receivers){
		Assert.notNull(taskId,"完成分发任务时，任务id不能为null");
		WorkflowTask task = getTask(taskId);
		Assert.notNull(task,"完成分发任务时，任务不能为null");
		
		List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
		if(receivers.size()>0 && receivers.get(0).equals(CommonStrings.ALL_USER)){
			receivers.remove(0);
			List<com.norteksoft.product.api.entity.User> users = ApiFactory.getAcsService().getUsersByCompany(task.getCompanyId());
			for(com.norteksoft.product.api.entity.User user:users){
				receivers.add(user.getLoginName());
			}
		}
		Set<String> receiverSet = new HashSet<String>(receivers);
		Integer groupNum=getTaskMaxGroupNum(task.getProcessInstanceId(), task.getName(),task.getCompanyId());
		groupNum++;
		for(String user : receiverSet){
			tasks.add(createDistributeTask(task, user, TaskProcessingMode.TYPE_READ.toString(),groupNum));
		}
		saveTasks(tasks);
		return complete(task,TaskProcessingResult.DISTRIBUTE,TaskSetting.getTaskSettingInstance().setReturnUrl(false));
	}
	
	/**
	 * 设置任务是否已阅
	 * @param task
	 */
	@Transactional(readOnly=false)
	public void updateTaskIsRead(WorkflowTask task){
		boolean isNeedSave = false;
		if(!task.getRead()){
			task.setRead(true);
			isNeedSave = true;
		}
		if(isNeedSave){
			saveTask(task);
		}
	}

	/*
	 * 设置流程实例当前环境 
	 */
	@Transactional(readOnly=false)
	private void setInstanceCurrentActivity(WorkflowInstance instance, WorkflowTask task){
		if(task==null){log.debug("设置流程实例当前环节时，任务参数不能为null");throw new RuntimeException("设置流程实例当前环节时，任务参数不能为null");}
		if(instance==null){log.debug("设置流程实例当前环节时，流程实例参数不能为null");throw new RuntimeException("设置流程实例当前环节时，流程实例参数不能为null");}
		//if(task.isSpecialTask()) return ;
		String activityNames = instance.getCurrentActivity();
		if(StringUtils.isNotEmpty(activityNames)){
			if(task.getNextTasks() != null){
				activityNames = activityNames.replace(task.getName(), task.getNextTasks());
			}
		}else{
			activityNames = task.getNextTasks();
		}
		instance.setCurrentActivity(activityNames);
		FormView form =formViewManager.getFormView(instance.getFormId());
		if(form==null){log.debug("设置流程实例当前环节时，表单不能为null");throw new RuntimeException("设置流程实例当前环节时，表单不能为null");}
		if(form.isStandardForm()){
			log.info("标准表单");
			//标准表单的处理
			//根据表单id获得对应的类
			try {
				if(form.getDataTable()==null){log.debug("设置流程实例当前环节时，表单对应的数据表不能为null");throw new RuntimeException("设置流程实例当前环节时，表单对应的数据表不能为null");}
				String className = form.getDataTable().getEntityName();
				log.info("实体类名：" + className);
				//根据表名和id获得实体
				Object entity = generalDao.getObject(className, instance.getDataId());
				log.info("查询得到的实体:" + entity);
				BeanUtils.setProperty(entity, "workflowInfo.currentActivityName", activityNames);
				generalDao.save(entity);
			} catch (Exception e) {
				new RuntimeException(e);
			}
		}
	}
	
	/**
	 * 领取
	 * @param taskId
	 */
	@Transactional(readOnly=false)
	public String receive(Long taskId){
		return workflowTaskService.receive(taskId);
	}
	
	/**
	 * 放弃领取
	 * @param taskId
	 */
	@Transactional(readOnly=false)
	public String abandonReceive(Long taskId){
		return workflowTaskService.abandonReceive(taskId);
	}
	
	
	/*
	 * 生成任务历史记录
	 * @param instanceId
	 * @param activityName
	 */
	@Transactional(readOnly=false)
	private void generateTaskHistory(WorkflowTask task,TaskProcessingResult result){
		InstanceHistory ih = new InstanceHistory();
		ih.setCompanyId(task.getCompanyId());
		ih.setType(InstanceHistory.TYPE_TASK);
		ih.setInstanceId(task.getProcessInstanceId());
		ih.setExecutionId(task.getExecutionId());
		ih.setTaskName(task.getName());
		ih.setTaskId(task.getId());
		ih.setCreatedTime(new Date());
		ih.setSpecialTask(task.isSpecialTask());
		StringBuilder msg = new StringBuilder();
		msg.append(dateFormat.format(ih.getCreatedTime()))
			.append(COMMA).append(ContextUtils.getUserName()).append(ACTION_START)
			.append(result.getName()).append(ACTION_END);
		ih.setTransactionResult(msg.toString());
		ih.setTransactor(new StringBuilder(ContextUtils.getUserName()).append(DELTA_START).append(task.getTransactor()).append(DELTA_END).toString());
		instanceHistoryManager.saveHistory(ih);
	}
	
	/*
	 * 完成Workflow任务
	 * @param task
	 * @param parentExecutionId
	 * @return
	 */
	@Transactional(readOnly=false)
	private CompleteTaskTipType completeWfTask(WorkflowInstance instance,WorkflowTask task, String parentExecutionId){
		log.debug("*** completeWfTask 方法开始");
		CompleteTaskTipType isNeedAssign = isNeedAssigningTransactor(instance, task);
		if(isNeedAssign==null){
			//判断子流程是否需要设置办理人
			isNeedAssign=isSubProcessNeedChoiceTransactor(task);
		}
		
		if(isNeedAssign==null){
			//生成新任务
			generateTask(instance, task.getExecutionId(), parentExecutionId);
			
			//特事特办
			doSpecialTask(task);
			//完成任务，生成流转历史
			completeTask(task);
			generateHistory(task, parentExecutionId);
			//设置流程实例上一环节相关信息
			setWorkflowPreviousAcitivity(instance,task);
		}else{
			task.setTransactDate(new Date());
        	task.setActive(TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex());
		}
		return isNeedAssign;
	}
	
	private void setWorkflowPreviousAcitivity(WorkflowInstance instance,WorkflowTask task){
		instance.setPreviousActivity(task.getName());
		instance.setPreviousActivityTitle(task.getTitle());
		workflowInstanceManager.saveWorkflowInstance(instance);
	}
	
	private void generateTaskHistory(WorkflowTask task){
		// 完成任务
		task.setTransactDate(new Date());
    	task.setActive(TaskState.COMPLETED.getIndex());
    	//人工环节流转历史
    	generateTaskHistory(task, task.getTaskProcessingResult());
		saveTask(task);
	}
	
	private void completeTask(WorkflowTask task){
		generateTaskHistory(task);
		executeAfterTaskCompleted(task);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	private void doSpecialTask(WorkflowTask task){
		//判断是否特事特办
		WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		FormView form = formViewManager.getFormView(workflow.getFormId());
		String titleExpression = DefinitionXmlParse.getSpecialTaskTitle(workflow.getProcessDefinitionId(), task.getName());
		String title = this.parseTitleExpression(titleExpression, workflow,null);
		String specialTaskName = DefinitionXmlParse.getSpecialTaskProperties(workflow.getProcessDefinitionId(), task.getName());
		if(!form.isStandardForm()){
			//自定义表单
			Map map = formViewManager.getDataMap(form.getDataTable().getName(), workflow.getDataId());
			Object value = map.get(JdbcSupport.FILED_NAME_IS_CREATE_SPECIAL_TASK);
			if(value!=null&& "1".equals(value.toString()) && DefinitionXmlParse.isHaveSpecialTask(workflow.getProcessDefinitionId(), task.getName())){
				//获得特事特办任务 并创建
				Object specialTaskTransactor = map.get(JdbcSupport.FILED_NAME_IS_SPECIAL_TASK_TRANSACTOR);
				String json;
				if(specialTaskTransactor!=null){
					json = specialTaskTransactor.toString();
					createSpecialTask(workflow, task.getExecutionId(), json.split(","), specialTaskName,title);
				}else{
					//JPDL定义扩展参数
					Map<TaskTransactorCondition, String> conditions = 
						DefinitionXmlParse.getTaskTransactor(workflow.getProcessDefinitionId(), specialTaskName);
					
					Execution execution = processEngine.getExecutionService().findExecutionById(task.getExecutionId());
					OpenExecution openExeccution = ((OpenExecution)execution);
					String processInstanceId = ((ActivityExecution)execution).getProcessInstance().getId();
					String creator = openExeccution.getVariable("creator").toString();
					//根据条件选定办理人
					log.info("办理人设置条件为:"+conditions);
					Map<String,String> paramMap = new HashMap<String,String>();
					paramMap.put(TransactorConditionHandler.DOCUMENT_CREATOR, creator);
					paramMap.put(TransactorConditionHandler.PROCESS_INSTANCEID, processInstanceId);
					Object obj = openExeccution.getVariable(CommonStrings.PREVIOUS_TASK_TRANSACTOR);
					if(obj!=null){
						paramMap.put(TransactorConditionHandler.PREVIOUS_TRANSACTOR, obj.toString());
					}
					
					Set<String> candidates = TransactorConditionHandler.processCondition(conditions, openExeccution,paramMap);
					if(candidates.isEmpty() || (candidates.size()==1 && candidates.iterator().next().equals(CommonStrings.TRANSACTOR_ASSIGNMENT)))return;//如果没有办理人。将不特办
					createSpecialTask(workflow, task.getExecutionId(), candidates, specialTaskName,title);
				} 
					
			}
		}else if(form.isStandardForm()){
			//标准表单
			FormFlowable entity = (FormFlowable)generalDao.getObject(form.getDataTable().getEntityName(), workflow.getDataId());
			boolean isCreateSpecialTask = entity.getWorkflowInfo().getCreateSpecialTask();
			if(isCreateSpecialTask&& DefinitionXmlParse.isHaveSpecialTask(workflow.getProcessDefinitionId(), task.getName())){
				//获得特事特办任务 并创建
				String specialTaskTransactor = entity.getWorkflowInfo().getSpecialTaskTransactor();
				if(specialTaskTransactor!=null){
					//TODO 如果特事特办流向没有设定办理人，就用那个环节条件选的办理人
					
					createSpecialTask(workflow, task.getExecutionId(), specialTaskTransactor.split(","), specialTaskName,title);
				}else{
					//JPDL定义扩展参数
					Map<TaskTransactorCondition, String> conditions = 
						DefinitionXmlParse.getTaskTransactor(workflow.getProcessDefinitionId(), specialTaskName);
					
					Execution execution = processEngine.getExecutionService().findExecutionById(task.getExecutionId());
					OpenExecution openExeccution = ((OpenExecution)execution);
					String processInstanceId = ((ActivityExecution)execution).getProcessInstance().getId();
					String creator = openExeccution.getVariable("creator").toString();
					//根据条件选定办理人
					log.info("办理人设置条件为:"+conditions);
					Map<String,String> paramMap = new HashMap<String,String>();
					paramMap.put(TransactorConditionHandler.DOCUMENT_CREATOR, creator);
					paramMap.put(TransactorConditionHandler.PROCESS_INSTANCEID, processInstanceId);
					Object obj = openExeccution.getVariable(CommonStrings.PREVIOUS_TASK_TRANSACTOR);
					if(obj!=null){
						paramMap.put(TransactorConditionHandler.PREVIOUS_TRANSACTOR, obj.toString());
					}
					
					Set<String> candidates = TransactorConditionHandler.processCondition(conditions, openExeccution,paramMap);
					if(candidates.isEmpty())return;//如果没有办理人。将不特办
					createSpecialTask(workflow, task.getExecutionId(), candidates, specialTaskName,title);
				} 
			}
		}
	}
	
	@Transactional(readOnly=false)
	private void createSpecialTask(WorkflowInstance workflow,String executionId,Set<String> users,String taskName,String title){
		for(String user:users){
			WorkflowTask specialTask = createSpecialTask(workflow, executionId, user, taskName,title);
			this.saveTask(specialTask);
		}
	}
	
	@Transactional(readOnly=false)
	private void createSpecialTask(WorkflowInstance workflow,String executionId,String[] users,String taskName,String title){
		for(String user:users){
			WorkflowTask specialTask = createSpecialTask(workflow, executionId, user, taskName,title);
			this.saveTask(specialTask);
		}
	}
	
	/**
	 * 任务完成后执行
	 */
	@Transactional(readOnly=false)
	private void executeAfterTaskCompleted(WorkflowTask task){
		if(task==null){log.debug("任务完成后执行bean时，任务参数不能为null");throw new RuntimeException("任务完成后执行bean时，任务参数不能为null");}
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		if(instance==null){log.debug("任务完成后执行bean时，流程实例不能为null");throw new RuntimeException("任务完成后执行bean时，流程实例不能为null");}
		String beanName = DefinitionXmlParse.getAfterTaskCompletedBean(
				instance.getProcessDefinitionId(), task.getName());
		if(!StringUtils.isEmpty(beanName)){
			AfterTaskCompleted bean = (AfterTaskCompleted) ContextUtils.getBean(beanName);
			if(bean==null){log.debug("任务完成后执行bean时，bean不能为null");throw new RuntimeException("任务完成后执行bean时，bean不能为null");}
			bean.execute(instance.getDataId(), task.getTaskProcessingResult());
		}
	}
	
    /*
     * 生成流转历史
     * @param task
     */
	@Transactional(readOnly=false)
    private void generateHistory(WorkflowTask task, String parentExecutionId){
    	log.debug("*** generateHistory 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("task:").append(task)
			.append(", parentExecutionId:").append(parentExecutionId)
			.append("]").toString());
		
    	generateFlowHistory(task.getProcessInstanceId(), task.getExecutionId(), task.getName(), PROCESS_LEAVE);
		Execution execution = processEngine.getExecutionService().findExecutionById(task.getExecutionId());
		String nextTasks = "";
		if(execution == null){
			if(parentExecutionId != null){
				// 并发join时取主流程的任务  (不考虑合并后还需要选择办理人)
				log.debug("parentExecutionId:"+parentExecutionId);
				Execution parentExecution = processEngine.getExecutionService().findExecutionById(parentExecutionId);
				log.debug("parentExecution:" + parentExecution);
				if(parentExecution == null){ //汇聚后流程直接结束
					InstanceHistory ih = new InstanceHistory(task.getCompanyId(), task.getProcessInstanceId(), 
							InstanceHistory.TYPE_FLOW_END, PROCESS_END);
					instanceHistoryManager.saveHistory(ih);
					workflowInstanceManager.setWorkflowInstanceEnd(task.getProcessInstanceId());
					nextTasks = PROCESS_END_EN;
				}else{
					String activityName = ((ActivityExecution)parentExecution).getActivityName();
	        		generateFlowHistory(task.getProcessInstanceId(), parentExecution.getId(), activityName, PROCESS_ENTER);
	        		nextTasks = activityName;
				}
			}else{
				ProcessInstance instance = processEngine.getExecutionService().findProcessInstanceById(task.getProcessInstanceId());
				// 当 execution 和 instance 同时为null时，流程结束
				if(instance == null){
					InstanceHistory ih = new InstanceHistory(task.getCompanyId(), task.getProcessInstanceId(), 
							InstanceHistory.TYPE_FLOW_END, PROCESS_END);
					instanceHistoryManager.saveHistory(ih);
					//设置流程结束
					workflowInstanceManager.setWorkflowInstanceEnd(task.getProcessInstanceId());
					nextTasks = PROCESS_END_EN;
				}
			}
		}else{
			Collection<? extends Execution> subExecutions = execution.getExecutions();
			List<Execution> executions = new ArrayList<Execution>();
			if(subExecutions == null || subExecutions.size() < 1){
				if(Execution.STATE_INACTIVE_JOIN.equals(execution.getState())){//并发汇聚
					//String activityName = ((ActivityExecution)execution).getActivityName();
	        		//generateFlowHistory(task.getProcessInstanceId(), activityName, PROCESS_TRANSACT);
				}else{
					ExecutionImpl subInstance = ((ExecutionImpl)execution.getProcessInstance()).getSubProcessInstance();
					if(subInstance != null){//子流程
						String activityName = subInstance.getActivityName();
		        		generateFlowHistory(subInstance.getProcessInstance().getId(), subInstance.getId(), activityName, PROCESS_ENTER);
		        		//executions.add(subInstance);
		        		nextTasks = activityName;
					}else{
		    			String activityName = ((ActivityExecution)execution).getActivityName();
		    			WorkflowInstance wi=workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),task.getCompanyId());
		    			if(wi.getProcessState()==ProcessState.END){
		    				InstanceHistory ih = new InstanceHistory(task.getCompanyId(), task.getProcessInstanceId(), 
		    						InstanceHistory.TYPE_FLOW_END, PROCESS_END);
		    				instanceHistoryManager.saveHistory(ih);
		    				//设置流程结束
		    				workflowInstanceManager.setWorkflowInstanceEnd(task.getProcessInstanceId());
		    			}else{
		    				generateFlowHistory(task.getProcessInstanceId(), execution.getId(), activityName, PROCESS_ENTER);
		    			}
		    			executions.add(execution);
		        		nextTasks = activityName;
					}
				}
			}else{
				// 并发流程的流转历史
				for(Execution e : subExecutions){
					String activityName = ((ActivityExecution)e).getActivityName();
            		generateFlowHistory(task.getProcessInstanceId(), e.getId(), activityName, PROCESS_ENTER);
            		executions.add(e);
            		nextTasks = nextTasks + "," + activityName;
				}
			}
			
		}
		//任务完成时修改任务下一环节的任务(含并发)
		nextTasks = nextTasks.replaceFirst(",", "");
		task.setNextTasks(nextTasks);
		
		log.debug("*** generateHistory 方法结束");
    }

	/*
	 * 生成流转历史记录
	 * @param instanceId
	 * @param activityName
	 */
	@Transactional(readOnly=false)
	private void generateFlowHistory(String instanceId, String executionId, String activityName, String state){
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(instanceId);
		InstanceHistory ih = new InstanceHistory();
		ih.setCompanyId(instance.getCompanyId());
		if(PROCESS_ENTER.equals(state)){
			ih.setType(InstanceHistory.TYPE_FLOW_INTO);
		}else if(PROCESS_LEAVE.equals(state)){
			ih.setType(InstanceHistory.TYPE_FLOW_LEAVE);
		}
		ih.setInstanceId(instanceId);
		ih.setExecutionId(executionId);
		ih.setCreatedTime(new Date());
		ih.setTransactionResult(new StringBuilder(dateFormat.format(new Date()))
			.append(COMMA).append(state).append(DELTA_START)
			.append(activityName).append(DELTA_END).toString());
		ih.setTaskName(activityName);
		instanceHistoryManager.saveHistory(ih);
	}
	
	/*
	 * 提取需要生成新任务的JBPM task
	 * @param jbpmTasks
	 * @param taskNames
	 */
	@Transactional(readOnly=false)
	private List<org.jbpm.api.task.Task> extractTask(List<org.jbpm.api.task.Task> jbpmTasks, List<String> taskNames){
		if(taskNames != null){
			List<org.jbpm.api.task.Task> targetTasks = new ArrayList<org.jbpm.api.task.Task>();
			for(org.jbpm.api.task.Task t : jbpmTasks){
				if(!taskNames.contains(t.getName())){
					targetTasks.add(t);
				}
			}
			return targetTasks;
		}
		return jbpmTasks;
	}
	
	/**
	 * 生成任务
	 * @param processId
	 * @param instanceId
	 * @param transactor
	 */
	@Transactional(readOnly=false)
	public void generateTask(WorkflowInstance instance, String executionId, String parentExecutionId){
		log.debug("*** generateTask 方法开始");

		String instanceId=instance.getProcessInstanceId();
		List<org.jbpm.api.task.Task> jbpmTasks = processEngine.getTaskService().createTaskQuery()
			.processInstanceId(instanceId).list();
		ActivityExecution execution = (ActivityExecution) processEngine.getExecutionService().findExecutionById(executionId);
		if(execution != null){
			if(jbpmTasks.isEmpty() && !Execution.STATE_INACTIVE_CONCURRENT_ROOT.equals(execution.getState())){
				return;
			}
//			if(jbpmTasks.isEmpty() && !Execution.STATE_ACTIVE_ROOT.equals(execution.getState())){//流程结束,流程并发
//				WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(execution.getProcessInstance().getId());
//				instanceId = instance.getParentProcessId();
//				jbpmTasks = processEngine.getTaskService().createTaskQuery().processInstanceId(instanceId).list();
//			}
			if(jbpmTasks.isEmpty()&&Execution.STATE_INACTIVE_JOIN.equals(execution.getState())){
				return ;
			}
			List<String> taskNames = workflowTaskService.getTaskNamesByInstance(instance.getCompanyId(), instanceId);// 查询流程实例所有已经生成的任务
			jbpmTasks = extractTask(jbpmTasks, taskNames);
			List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
			boolean isSameGroup=false;
			for(org.jbpm.api.task.Task task : jbpmTasks){
				Integer groupNum=getTaskMaxGroupNum(instanceId,task.getActivityName(),instance.getCompanyId());
				if(!isSameGroup)groupNum++;
				isSameGroup=true;
				if(task.getAssignee() == null){//任务有多个办理人
					Iterator<ParticipationImpl> it = ((TaskImpl)task).getParticipations().iterator();
					WorkflowTask wfTask = null;
					while(it.hasNext()){
						wfTask = createTask(instanceId, task.getExecutionId(), it.next().getUserId(), task.getActivityName(),groupNum);
						//审批任务有多个办理人时设置为待领取
						if(!wfTask.getMoreTransactor()){
							wfTask.setActive(TaskState.DRAW_WAIT.getIndex());
						}
						tasks.add(wfTask);
					}
				}else{//任务只有一个办理人
					tasks.add(createTask(instanceId, task.getExecutionId(), task.getAssignee(), task.getActivityName(),groupNum));
				}
			}
			if(!tasks.isEmpty()) saveTasks(tasks);
		}else if(execution == null && StringUtils.isNotEmpty(parentExecutionId)){//并发Execution结束，进入parent Execution
			generateTask(instance, parentExecutionId, "");
		}
		log.debug("*** generateTask 方法结束");
	}
	
	/*
	 * 获取任务完成后流程的Execution
	 * @param execution
	 * @param parentExecutionId
	 * @return
	 */
	private Map<ActivityExecution, String> getCurrentExecutions(ActivityExecution execution, String parentExecutionId){
		Map<ActivityExecution, String> result = new HashMap<ActivityExecution,String>();
		if(execution == null){
			if(parentExecutionId != null){
				ActivityExecution parentExecution = (ActivityExecution)processEngine.getExecutionService().findExecutionById(parentExecutionId);
				String activityName = parentExecution.getActivityName();
				result.put(parentExecution, activityName);
			}
		}else{
			Collection<? extends Execution> executions = execution.getExecutions();
			if(executions.isEmpty()){
				String activityName = execution.getActivityName();
				result.put(execution, activityName);
			}else{
				for(Execution ex : executions){
					String activityName = ((ActivityExecution)ex).getActivityName();
					result.put((ActivityExecution)ex, activityName);
				}
			}
		}
		return result;
	}
    
    /**
     * 判断环节是否需要指定办理人
     * @param taskId
     * @return
     */
	public CompleteTaskTipType isNeedAssigningTransactor(WorkflowInstance instance, WorkflowTask task){
		Assert.notNull(instance,"流程实例不能为null");
		Assert.notNull(task,"task任务不能为null");
    	log.debug("*** isNeedAssigningTransactor 方法开始");
    	CompleteTaskTipType result = null;
		//直接为上一环节办理人指定
    	List<org.jbpm.api.task.Task> assignmentList = processEngine.getTaskService().createTaskQuery()
    		.assignee(CommonStrings.TRANSACTOR_ASSIGNMENT)
    		.processInstanceId(instance.getProcessInstanceId()).list();
    	
    	if(assignmentList.size() > 0){
    		String url = DefinitionXmlParse.getPreviousTransactorAssignmentUrl(instance.getProcessDefinitionId(),task.getNextTasks());
    		url = StringUtils.isEmpty(url)?DEFAULT_URL:url;
    		result = CompleteTaskTipType.RETURN_URL.setContent(url);
    	}else{
    		//附加条件中需要唯一指定办理人
    		//直流候选人
    		OpenExecution execution = (OpenExecution)processEngine.getExecutionService()
							.findExecutionById(task.getExecutionId());
    		Object condidates = null;
    		if(execution != null){
    			condidates = processEngine.getExecutionService().getVariable(task.getExecutionId(), CommonStrings.TRANSACTOR_SINGLE_CANDIDATES);
    			boolean moreTransactor = DefinitionXmlParse.hasMoreTransactor(
    					instance.getProcessDefinitionId(), task.getNextTasks());
	    		if(condidates != null){
	        		result = CompleteTaskTipType.SINGLE_TRANSACTOR_CHOICE.setCanChoiceTransactor((Set<String>)condidates).setContent(moreTransactor+"");
	    		}else{
	    			//并发流程候选人
	    			Collection<? extends Execution> executions = execution.getExecutions();
	    			for(Execution e : executions){
	    				condidates = ((OpenExecution)e).getVariable(CommonStrings.TRANSACTOR_SINGLE_CANDIDATES);
	    				if(condidates != null){
	    					result = CompleteTaskTipType.SINGLE_TRANSACTOR_CHOICE.setCanChoiceTransactor((Set<String>)condidates).setContent(moreTransactor+"");
	    	    			break;
	    	    		}
	    			}
	    		}
    		}
    	}
    	return result;
    }

    /**
     * 完成JBPM所对应的任务
     * @param task
     */
    @Transactional(readOnly=false)
    private String completeJbpmTask(WorkflowTask task, String operation,TaskSetting setting){
    	log.debug("*** completeJbpmTask 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("task:").append(task)
			.append(", operation:").append(operation)
			.append("]").toString());
		
    	//if(task.isSpecialTask()) return null;
    	
		if(task == null){log.debug("completeJbpmTask方法中，task任务不能为null");throw new RuntimeException("completeJbpmTask方法中，task任务不能为null");}
    	// 个人任务
    	org.jbpm.api.task.Task jbpmTask = processEngine.getTaskService().createTaskQuery()
			.processInstanceId(task.getProcessInstanceId())
    		.activityName(task.getName()).uniqueResult();
    	// 组任务
    	if(jbpmTask == null){
    		jbpmTask = processEngine.getTaskService().createTaskQuery()
				.processInstanceId(task.getProcessInstanceId())
				.candidate(ContextUtils.getLoginName())
				.activityName(task.getName()).uniqueResult();
    	}
    	if(jbpmTask == null){log.debug("completeJbpmTask方法中，jbpmTask任务不能为null");throw new RuntimeException("completeJbpmTask方法中，jbpmTask任务不能为null");}
    	String parentExecutionId = null;
    	Execution execution = processEngine.getExecutionService().findExecutionById(jbpmTask.getExecutionId());
    	if(execution == null){log.debug("completeJbpmTask方法中，execution不能为null");throw new RuntimeException("completeJbpmTask方法中，jexecution不能为null");}
    	Execution parentExecution = execution.getParent();
    	if(parentExecution != null){
    		parentExecutionId = parentExecution.getId();
    	}
    	
    	// execution变量，供后面的Decision使用
    	Map<String, String> variables = new HashMap<String, String>();
    	variables.put(CommonStrings.CURRENT_OPERATTION_STRING, operation);//本环节办理人执行的操作
    	variables.put(CommonStrings.PREVIOUS_TASK_NAME, task.getName());//本环节任务名
    	variables.put(CommonStrings.PREVIOUS_TASK_TRANSACTOR, task.getTransactor());//本环节办理人（受托人）
    	variables.put(CommonStrings.PREVIOUS_TASK_PRINCI_TRANSACTOR, task.getTrustor());//本环节办理人委托人
    	variables.put(CommonStrings.TRANSITION_NAME, setting.getTransitionName());//指定的流向名
    	variables.put(CommonStrings.NEW_TRANSACTOR, setting.getAssignmentTransactors());//指定的办理人
    	executionVariableCommand(new ExecutionVariableCommand(execution.getId(),variables));
		try {
			processEngine.getTaskService().completeTask(jbpmTask.getId());
			WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),task.getCompanyId());
			//以下执行两遍completeAutoTache，completeCopyTache的原因是因为流程图中连续的【自动、抄送】环节。先【自动】再【抄送】或先【抄送】再【自动】均包括在内。
			completeAutoTache(instance, execution);//完成自动环节
			completeCopyTache(instance, execution);//完成抄送环节
			completeAutoTache(instance, execution);//完成自动环节
			completeCopyTache(instance, execution);//完成抄送环节
			task.setNextTasks(((ActivityExecution)execution).getActivityName());
		} catch (JbpmException e) {
			if(e.getMessage().endsWith(TransactorAssignmentException.NO_TRANSACTOR)){
				throw new TransactorAssignmentException(TransactorAssignmentException.NO_TRANSACTOR);
			}
		}
		log.debug("*** completeJbpmTask 方法结束");
		
    	return parentExecutionId;
    }
    
    
    /*
     * 递归完成抄送环节。可以完成过个抄送环节串联的情况
     */
    private void completeCopyTache(WorkflowInstance instance ,Execution execution){
    	if(execution!=null){
    		if(execution.getState().equals(Execution.STATE_ENDED)&&!execution.getProcessInstance().getState().equals(Execution.STATE_ENDED)){
				execution = processEngine.getExecutionService().findExecutionById(execution.getProcessInstance().getId());
			}
			for(String activityName:execution.findActiveActivityNames()){
				String tacheType = DefinitionXmlParse.getCurrentTacheType(instance.getProcessDefinitionId(),activityName);
				if(StringUtils.isNotEmpty(tacheType)&&tacheType.equals(WorkflowTacheType.COPY_TACHE.getCode())){
					Execution tempExecution = execution.findActiveExecutionIn(activityName);
					createCopyTask(instance,tempExecution,activityName);//创建抄送任务
					processEngine.getExecutionService().signalExecutionById(tempExecution.getId());
					generateCopyTaskHistory(instance.getProcessInstanceId(),tempExecution.getId(),activityName);
					completeCopyTache(instance,execution);
				}
			}
		}
    }
    
    @Transactional(readOnly=false)
	private void createCopyTask(WorkflowInstance instance,Execution execution,String taskName){
    	Object creatorObj = processEngine.getExecutionService().getVariable(execution.getId(), "creator");
    	User creator = userManager.getUserByLoginName(creatorObj.toString());
    	if(creator == null){log.debug("createCopyTask方法中，文档创建人不能为null");throw new RuntimeException("createCopyTask方法中，文档创建人不能为null");}
    	//分析本环节办理人的
    	//JPDL定义扩展参数
		Map<TaskTransactorCondition, String> conditions = 
			DefinitionXmlParse.getTaskTransactor(instance.getProcessDefinitionId(), taskName);
		
		
		//根据条件选定办理人
		log.info("办理人设置条件为:"+conditions);
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put(TransactorConditionHandler.DOCUMENT_CREATOR, creator.getLoginName());
		paramMap.put(TransactorConditionHandler.PROCESS_INSTANCEID, instance.getProcessInstanceId());
		Object previousTaskTransactorObj = ((OpenExecution) execution).getVariable(CommonStrings.PREVIOUS_TASK_TRANSACTOR);
		if(previousTaskTransactorObj!=null){
			paramMap.put(TransactorConditionHandler.PREVIOUS_TRANSACTOR, previousTaskTransactorObj.toString());
		}
		
		Set<String> candidates = TransactorConditionHandler.processCondition(conditions, (OpenExecution) execution,paramMap);
    	List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
    	for(String transactor:candidates){
        	WorkflowTask task = createTask(instance.getProcessDefinitionId(),instance.getProcessInstanceId(),execution.getId(),taskName,transactor,false,1);
        	if(task!=null){
        		task.setProcessingMode(TaskProcessingMode.TYPE_READ);
        		task.setActive(TaskState.WAIT_TRANSACT.getIndex());
        		task.setMoreTransactor(true);
        		task.setRead(false);
        		task.setSendingMessage(true);
        		tasks.add(task);
        	}
    	}
    	this.saveTasks(tasks);
	}
    /*
     * 递归完成自动环节。可以完成多个自动环节串连的情况
     */
    @Transactional(readOnly=false)
    private void completeAutoTache(WorkflowInstance instance ,Execution execution){
		if(execution!=null){
			if(execution.getState().equals(Execution.STATE_ENDED)&&!execution.getProcessInstance().getState().equals(Execution.STATE_ENDED)){
				//如果参数的execution已经结束，那么重新从实例中查找当前的execution
				execution = processEngine.getExecutionService().findExecutionById(execution.getProcessInstance().getId());
			}
			for(String activityName:execution.findActiveActivityNames()){
				String tacheType = DefinitionXmlParse.getCurrentTacheType(instance.getProcessDefinitionId(),activityName);
				if(StringUtils.isNotEmpty(tacheType)&&tacheType.equals(WorkflowTacheType.AUTO_TACHE.getCode())){
					Execution tempExecution = execution.findActiveExecutionIn(activityName);
					processEngine.getExecutionService().signalExecutionById(tempExecution.getId());
					generateTaskHistory(instance.getProcessInstanceId(),tempExecution.getId(),activityName);
					completeAutoTache(instance,execution);
				}
			}
		}
    }
    
    /*
	 * 生成自动环节的历史记录
	 * @param instanceId
	 * @param activityName
	 */
	@Transactional(readOnly=false)
	private void generateTaskHistory(String instanceId,String excuteId,String tacheName){
		WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(instanceId);
		InstanceHistory ih = new InstanceHistory();
		ih.setCompanyId(instance.getCompanyId());
		ih.setType(InstanceHistory.TYPE_TASK);
		ih.setInstanceId(instanceId);
		ih.setExecutionId(excuteId);
		ih.setTaskName(tacheName);
		ih.setCreatedTime(new Date());
		StringBuilder msg = new StringBuilder();
		msg.append(dateFormat.format(ih.getCreatedTime()))
			.append(COMMA).append(PROCESS_WENT).append(tacheName);
		ih.setTransactionResult(msg.toString());
		instanceHistoryManager.saveHistory(ih);
	}
	
	/*
	 * 生成自动环节的历史记录
	 * @param instanceId
	 * @param activityName
	 */
	@Transactional(readOnly=false)
	private void generateCopyTaskHistory(String instanceId,String excuteId,String tacheName){
		WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(instanceId);
		InstanceHistory ih = new InstanceHistory();
		ih.setCompanyId(instance.getCompanyId());
		ih.setType(InstanceHistory.TYPE_AUTO);
		ih.setInstanceId(instanceId);
		ih.setExecutionId(excuteId);
		ih.setTaskName(tacheName);
		ih.setCreatedTime(new Date());
		StringBuilder msg = new StringBuilder();
		msg.append(dateFormat.format(ih.getCreatedTime()))
			.append(COMMA).append(PROCESS_WENT).append(tacheName);
		ih.setTransactionResult(msg.toString());
		instanceHistoryManager.saveHistory(ih);
	}
    
    
    @Transactional(readOnly=false)
    public void executionVariableCommand(ExecutionVariableCommand executionVariableCommand){
    	processEngine.execute(executionVariableCommand);
    }
    
    /**
     * 根据前一任务的ID查询需要指定任务的办理人的任务
     * 说明：暂时不支持并发汇聚后需要指定办理人
     * @param taskId
     * @return
     */
	public Map<String[], List<String[]>> getNextTasksCandidates(WorkflowTask task){
		if(task==null){log.debug("getNextTasksCandidates中，任务不能为null");throw new RuntimeException("getNextTasksCandidates中，任务不能为null");}
		log.debug("*** getNextTasksCandidates 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("task:").append(task)
			.append("]").toString());
		
		
		
    	//直接为上一环节办理人指定//附加条件中需要唯一指定办理人
		Map<String[], List<String[]>> result = new HashMap<String[], List<String[]>>();
		ActivityExecution execution = (ActivityExecution) processEngine.getExecutionService().findExecutionById(task.getExecutionId());
		if(execution==null){log.debug("getNextTasksCandidates中，execution不能为null");throw new RuntimeException("getNextTasksCandidates中，execution不能为null");}
		Collection<? extends Execution> executions = execution.getExecutions();
		if(executions.isEmpty()){//同级Execution
			String activityName = execution.getActivityName();
			getCondidates(result, execution, task.getProcessInstanceId(), activityName);
		}else{//下级Execution
			for(Execution ex : executions){
				String activityName = ((ActivityExecution)ex).getActivityName();
				getCondidates(result, ex, task.getProcessInstanceId(), activityName);
			}
		}
		
		log.debug("*** getNextTasksCandidates 方法结束");
    	return result;
    }
    
	/*
	 * 根据Task查询Task的办理人
	 * @param task
	 * @param result
	 */
    @SuppressWarnings("unchecked")
	private void getCondidates(Map<String[], List<String[]>> result, Execution ex, String instanceId, String activityName){
    	log.debug("*** getCondidates 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("execution:").append(ex)
			.append(", instanceId:").append(instanceId)
			.append(", activityName:").append(activityName)
			.append("]").toString());
		WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(instanceId);
		if(instance==null){log.debug("getCondidates中，流程实例不能为null");throw new RuntimeException("getCondidates中，流程实例不能为null");}
    	//上一环节指定
    	org.jbpm.api.task.Task jbpmTask = processEngine.getTaskService().createTaskQuery()
			.processInstanceId(instanceId).activityName(activityName).uniqueResult();
    	String isMoreTransactor = ""+DefinitionXmlParse.hasMoreTransactor(
    			ex.getProcessDefinitionId(), activityName);
    	Object assign=processEngine.getExecutionService().getVariable(ex.getId(), CommonStrings.TRANSACTOR_ASSIGNMENT);
		if(assign!=null && PRE_TRANSACTOR_ASSIGN.equals(assign.toString())){
			result.put(new String[]{isMoreTransactor, activityName},  AcsApi.getAllUsers(instance.getCompanyId()));
		}else{
			if(jbpmTask==null){log.debug("getCondidates中，jbpmTask不能为null");throw new RuntimeException("getCondidates中，jbpmTask不能为null");}
			if(CommonStrings.TRANSACTOR_ASSIGNMENT.equals(jbpmTask.getAssignee())){
				result.put(new String[]{isMoreTransactor, activityName},  AcsApi.getAllUsers(instance.getCompanyId()));
			}else if(CommonStrings.TRANSACTOR_SINGLE.equals(jbpmTask.getAssignee())){
				//附加条件指定唯一
				List<String[]> condidates = (List<String[]>) ((OpenExecution)ex).getVariable(CommonStrings.TRANSACTOR_SINGLE_CANDIDATES);
				result.put(new String[]{isMoreTransactor, activityName}, condidates );
			}
		}
    	
		
		log.debug("*** getCondidates 方法结束");
    }
    
    @Transactional(readOnly=false)
    public void setTasksTransactor(Long taskId,List<String> transcators){
    	WorkflowTask task = getTask(taskId);
    	Object assign=processEngine.getExecutionService().getVariable(task.getExecutionId(), CommonStrings.TRANSACTOR_ASSIGNMENT);
		if(assign!=null && PRE_TRANSACTOR_ASSIGN.equals(assign.toString())){
			SubProcessParse subprocessParse=(SubProcessParse)processEngine.getExecutionService().getVariable(task.getExecutionId(),CommonStrings.SUBPROCESS_PARSE);
			ActivityExecution execution=(ActivityExecution)processEngine.getExecutionService().findExecutionById(subprocessParse.getExecutionId());
			execution.removeVariable(CommonStrings.TRANSACTOR_ASSIGNMENT);
			execution.removeVariable(CommonStrings.SUBPROCESS_PARSE);
			Map<TaskTransactorCondition, String> transactor = DefinitionXmlParse.getTaskTransactor(subprocessParse.getParentDefinitionId(),subprocessParse.getActivityName());
			completeTask(task);
			this.startSubProcessWorkflow(transactor, subprocessParse,transcators);
		}else{
			setTasksTransactor(task,transcators);
		}
    }
    
    
    /**
     * 为某个环节设置办理人
     * @param task 任务
     * @param transcators 办理人列表
     */
    @Transactional(readOnly=false)
    public void setTasksTransactor(WorkflowTask task,Collection<String> transcators){
    	log.debug("*** setTasksTransactor 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("task:").append(task)
			.append("]").toString());
		if(task==null){log.debug("为某环节设置办理人时，任务不能为null");throw new RuntimeException("为某环节设置办理人时，任务不能为null");}
		if(transcators==null){log.debug("为某环节设置办理人时，办理人集合不能为null");throw new RuntimeException("为某环节设置办理人时，办理人集合不能为null");}
		Set<String> transcatorsSet = new HashSet<String>();
		transcatorsSet.addAll(transcators);
    	ActivityExecution execution = (ActivityExecution) processEngine.getExecutionService().findExecutionById(task.getExecutionId());
    	org.jbpm.api.task.Task jbpmTask = processEngine.getTaskService().createTaskQuery()
		.processInstanceId(task.getProcessInstanceId())
		.activityName(execution.getActivityName()).uniqueResult();   
    	if(jbpmTask==null){log.debug("为某环节设置办理人时，jbpmTask不能为null");throw new RuntimeException("为某环节设置办理人时，jbpmTask不能为null");}
    	if(transcatorsSet.size()>0)((OpenExecution)processEngine.getExecutionService()
				.findExecutionById(jbpmTask.getExecutionId())).removeVariable(CommonStrings.TRANSACTOR_SINGLE_CANDIDATES);
    	
    	Integer groupNum=getTaskMaxGroupNum(task.getProcessInstanceId(),jbpmTask.getActivityName(),task.getCompanyId());
    	groupNum++;
    	for(String transcator:transcatorsSet){
    		if(transcator.contains(":")){
    			String[] transacts=transcator.split(":");
    			WorkflowTask wfTask=createTask(task.getProcessInstanceId(), jbpmTask.getExecutionId(), transacts[0] ,jbpmTask.getActivityName(),groupNum);
    			String titleInfo=setTaskTransactorInfo(transcator, wfTask);
    			String processId= processEngine.getExecutionService().findProcessInstanceById(wfTask.getProcessInstanceId()).getProcessDefinitionId();
    			addTitle(wfTask, processId, titleInfo);
    			saveTask(wfTask);
    		}else{
    			saveTask(createTask(task.getProcessInstanceId(), jbpmTask.getExecutionId(), transcator ,jbpmTask.getActivityName(),groupNum));
    		}
    		if(jbpmTask.getAssignee().equals(CommonStrings.TRANSACTOR_ASSIGNMENT)||jbpmTask.getAssignee().equals(CommonStrings.TRANSACTOR_SINGLE)){
        		processEngine.getTaskService().assignTask(jbpmTask.getId(),transcator);
        	}
    	}
    	
    	task.setActive(TaskState.COMPLETED.getIndex());// 任务完成状态
    	saveTask(task);
    	//特事特办
		doSpecialTask(task);
		//完成任务并生成wf任务流转历史
		completeTask(task);
    	// 保存流转历史
    	generateHistory(task, null);
    	WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),task.getCompanyId());
    	if(wi==null){log.debug("为某环节设置办理人时，流程实例不能为null");throw new RuntimeException("为某环节设置办理人时，流程实例不能为null");}
    	//设置流程实例当前环节
    	
    	if(wi.getProcessState()==ProcessState.UNSUBMIT){
    		wi.setProcessState(ProcessState.SUBMIT);
    		this.workflowInstanceManager.saveWorkflowInstance(wi);
    		FormView form = formViewManager.getFormView(wi.getFormId());
    		if(form==null){log.debug("为某环节设置办理人时，表单不能为null");throw new RuntimeException("为某环节设置办理人时，表单不能为null");}
    		if(form.isStandardForm()){
    			if(form.getDataTable()==null){log.debug("为某环节设置办理人时，表单对应的数据表不能为null");throw new RuntimeException("为某环节设置办理人时，表单对应的数据表不能为null");}
    			Object entity = generalDao.getObject(form.getDataTable().getEntityName(), wi.getDataId());
    			try {
					BeanUtils.setProperty(entity, "workflowInfo.processState", ProcessState.SUBMIT);
					generalDao.save(entity);
				} catch (IllegalAccessException e) {
					log.debug(e);
					throw new RuntimeException();
				} catch (InvocationTargetException e) {
					log.debug(e);
					throw new RuntimeException();
				}
    		}else{
    			jdbcDao.updateTable("UPDATE "+form.getDataTable().getName()+" SET PROCESS_STATE="+ProcessState.SUBMIT.getIndex()+" WHERE  ID="+wi.getDataId());
    		}
    	}
    	
    	setInstanceCurrentActivity(wi, task);
    	//设置流程实例上一环节相关信息
		setWorkflowPreviousAcitivity(wi,task);
    	executeAfterTaskCompleted(task);
    	
    	log.debug("*** setTasksTransactor 方法结束");
    } 
    
    /**
     * 当办理人为”上一环节办理人指定“时，调用此方法修改办理人
     * @param task
     * @param transactor
     */
    @Transactional(readOnly=false)
    public void setTasksTransactor(Long preTaskId, Map<String, List<String>> taskCondidates){
    	WorkflowTask task = getTask(preTaskId);
    	completeTask(task);
    	Object assign=processEngine.getExecutionService().getVariable(task.getExecutionId(), CommonStrings.TRANSACTOR_ASSIGNMENT);
		if(assign!=null && PRE_TRANSACTOR_ASSIGN.equals(assign.toString())){
			SubProcessParse subprocessParse=(SubProcessParse)processEngine.getExecutionService().getVariable(task.getExecutionId(),CommonStrings.SUBPROCESS_PARSE);
			ActivityExecution execution=(ActivityExecution)processEngine.getExecutionService().findExecutionById(subprocessParse.getExecutionId());
			execution.removeVariable(CommonStrings.TRANSACTOR_ASSIGNMENT);
			execution.removeVariable(CommonStrings.SUBPROCESS_PARSE);
			Map<TaskTransactorCondition, String> transactor = DefinitionXmlParse.getTaskTransactor(subprocessParse.getParentDefinitionId(),subprocessParse.getActivityName());
			Collection<String> transactors=new ArrayList<String>();
			for(Map.Entry<String, List<String>> candidates : taskCondidates.entrySet()){
				if(subprocessParse.getActivityName().equals(candidates.getKey().toString()))transactors=candidates.getValue();
	    	}
			this.startSubProcessWorkflow(transactor, subprocessParse,transactors);
		}else{
			setTasksTransactors(preTaskId,taskCondidates);
		}
    }
    
    /**
     * 当办理人为”上一环节办理人指定“时，调用此方法修改办理人
     * @param task
     * @param transactor
     */
    @Transactional(readOnly=false)
    public void setTasksTransactors(Long preTaskId, Map<String, List<String>> taskCondidates){
    	log.debug("*** setTasksTransactor 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("preTaskId:").append(preTaskId)
			.append("]").toString());
    	
    	WorkflowTask task = getTask(preTaskId);
    	ActivityExecution execution = (ActivityExecution) processEngine.getExecutionService().findExecutionById(task.getExecutionId());
    	List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
    	StringBuilder taskNames = new StringBuilder();
    	for(Map.Entry<String, List<String>> candidates : taskCondidates.entrySet()){
    		taskNames.append(candidates.getKey()).append(",");
    		org.jbpm.api.task.Task jbpmTask = processEngine.getTaskService().createTaskQuery()
				.processInstanceId(task.getProcessInstanceId())
	    		.activityName(candidates.getKey()).uniqueResult();
    		generateTask(tasks, task.getProcessInstanceId(), jbpmTask.getExecutionId(), candidates.getKey(), candidates.getValue(),task.getCompanyId());
			processEngine.execute(new TaskAssigneeCommand(jbpmTask, ""));
	    	//清除execution变量
	    	((OpenExecution)processEngine.getExecutionService()
				.findExecutionById(jbpmTask.getExecutionId())).removeVariable(CommonStrings.TRANSACTOR_SINGLE_CANDIDATES);
    	}
    	boolean isSameGroup=false;
    	//并发任务的其他任务
    	Map<ActivityExecution, String> map = getCurrentExecutions(execution, null);
    	for(String taskName : map.values()){
    		Integer groupNum=getTaskMaxGroupNum(task.getProcessInstanceId(), taskName,task.getCompanyId());
    		if(!isSameGroup)groupNum++;
    		isSameGroup=true;
    		if(!taskNames.toString().contains(taskName+',')){
    			List<org.jbpm.api.task.Task> jbpmList = processEngine.getTaskService().createTaskQuery()
	        		.activityName(taskName).processInstanceId(execution.getProcessInstance().getId()).list();
    			String processingMode = DefinitionXmlParse.getTaskProcessingMode(
    					execution.getProcessDefinitionId(), taskName);
    			for(org.jbpm.api.task.Task t: jbpmList){
    				WorkflowTask newtask = createTask(task.getProcessInstanceId(), t.getExecutionId(), t.getAssignee(), taskName, processingMode, false,groupNum);
    	    		if(newtask!=null) tasks.add(newtask);
    			}
    		}
    	}
    	saveTasks(tasks);
    	task.setActive(TaskState.COMPLETED.getIndex());// 任务完成状态
    	saveTask(task);
    	
    	// 保存流转历史
    	generateHistory(task, null);
    	WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),task.getCompanyId());
    	//设置流程实例当前环节
    	
    	if(wi.getProcessState()==ProcessState.UNSUBMIT){
    		wi.setProcessState(ProcessState.SUBMIT);
    		this.workflowInstanceManager.saveWorkflowInstance(wi);
    		FormView form = formViewManager.getFormView(wi.getFormId());
    		if(form.isStandardForm()){
    			Object entity = generalDao.getObject(form.getDataTable().getEntityName(), wi.getDataId());
    			try {
					BeanUtils.setProperty(entity, "workflowInfo.processState", ProcessState.SUBMIT);
					generalDao.save(entity);
				} catch (IllegalAccessException e) {
					log.debug(e);
					throw new RuntimeException();
				} catch (InvocationTargetException e) {
					log.debug(e);
					throw new RuntimeException();
				}
    		}else{
    			jdbcDao.updateTable("UPDATE "+form.getDataTable().getName()+" SET PROCESS_STATE="+ProcessState.SUBMIT.getIndex()+" WHERE  ID="+wi.getDataId());
    		}
    	}
    	
    	setInstanceCurrentActivity(wi, task);
    	//设置流程实例上一环节相关信息
		setWorkflowPreviousAcitivity(wi,task);
    	executeAfterTaskCompleted(task);
    	
    	log.debug("*** setTasksTransactor 方法结束");
    }
    
    @Transactional(readOnly=false)
    private void generateTask(List<WorkflowTask> tasks, String instanceId, 
    		String executionId, String activityName, List<String> taskCondidates,Long companyId){
    	WorkflowTask targetTask = null;
    	Integer groupNum=getTaskMaxGroupNum(instanceId, activityName,companyId);
    	groupNum++;
    	for(String condidate : taskCondidates){
    		targetTask = createTask(instanceId, executionId, condidate, activityName,groupNum);
    		tasks.add(targetTask);
    	}
    }
    
    
    /**
     * 办理人更改
     * @param taskId
     * @param transactor
     */
    @Transactional(readOnly=false)
    public void changeTransactor(Long taskId, String transactor){
    	WorkflowTask task = getTask(taskId);
		//被指派人的新任务
    	WorkflowTask targetTask = task.clone();
    	//设置指派人任务已完成
    	task.setActive(TaskState.CANCELLED.getIndex());
    	//新任务办理人
    	targetTask.setId(null);
    	targetTask.setRead(false);
    	targetTask.setCreatedTime(new Date());
    	targetTask.setTransactor(transactor);
    	User user = userManager.getUserByLoginName(transactor);
		if(user!=null){
			targetTask.setTransactorName(user.getName());
    	}
    	//任务委托设置
		String processId= processEngine.getExecutionService().findProcessInstanceById(task.getProcessInstanceId()).getProcessDefinitionId();
		String delegateTransactor = delegateManager.getDelegateMainName(
				task.getCompanyId(), transactor, processId, task.getName());
		if(delegateTransactor != null){
			targetTask.setTrustor(transactor);
			targetTask.setTrustorName(ApiFactory.getAcsService().getUserByLoginName(transactor).getName());
			targetTask.setTransactor(delegateTransactor);
			User targetUser = userManager.getUserByLoginName(delegateTransactor);
			if(targetUser!=null){
				targetTask.setTransactorName(targetUser.getName());
	    	}
		}
    	targetTask.setSendingMessage(true);
    	saveTask(task);
    	saveTask(targetTask);
    	
    	WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
    	WorkflowType type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
    	//消息提醒
		sendMessage(targetTask, type);
		//*******************邮件通知*************************
		sendMail(task,processId);
		//*******************生成更改办理人和委托流转历史*************************
		generateChangeTransactorHistory(task,transactor,targetTask,delegateTransactor);
    }
    
    /**
     * 生成更改办理人流转历史
     * @param taskId
     * @param transactor
     */
    private void generateChangeTransactorHistory(WorkflowTask task,
			String transactor,WorkflowTask delegateTask,String trustee) {
	        StringBuilder historyMessage = new StringBuilder();
			if(StringUtils.isNotEmpty(trustee)){//生成更改办理人和委托的流转历史
				historyMessage.append(acsUtils.getUserByLoginName(delegateTask.getTrustor()).getName())
				.append("已把任务委托给了")
				.append(delegateTask.getTransactorName()).append("。\n")
				.append(dateFormat.format(new Date()))
				.append(COMMA).append(ContextUtils.getUserName()).append("把办理人")
			    .append(task.getTransactorName()).append("更改成")
			    .append(acsUtils.getUserByLoginName(transactor).getName()).append(DELTA_START)
			    .append(task.getName()).append(DELTA_END).append("\n");
			}else{//生成指派流转历史
				historyMessage.append(dateFormat.format(new Date()))
				.append(COMMA).append(ContextUtils.getUserName()).append("把办理人")
			    .append(task.getTransactorName()).append("更改成")
			    .append(acsUtils.getUserByLoginName(transactor).getName()).append(DELTA_START)
			    .append(task.getName()).append(DELTA_END).append("\n");
			}
			
			InstanceHistory history = new InstanceHistory(task.getCompanyId(), task.getProcessInstanceId(), InstanceHistory.TYPE_TASK, historyMessage.toString(), task.getName());
			history.setEffective(false);
			history.setCreatedTime(new Date());
			history.setExecutionId(task.getProcessInstanceId());
			
	        instanceHistoryManager.saveHistory(history);
		
	}

	/**
     * 交办任务
     * @param taskId
     * @param transactor
     */
    @Transactional(readOnly=false)
    public void assign(Long taskId, String transactor){
    	workflowTaskService.assign(taskId, transactor);
    }
    
    /**
     * 交办任务
     * @param taskId
     * @param transactor
     */
    @Transactional(readOnly=false)
    public void assign(Long taskId, List<String> transactors){
    	WorkflowTask task = getTask(taskId);
    	List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
    	for(String transactor:transactors){
			//被指派人的新任务
        	WorkflowTask targetTask = task.clone();
        	//新任务办理人
        	targetTask.setId(null);
        	targetTask.setRead(false);
        	targetTask.setProcessingMode(TaskProcessingMode.TYPE_EDIT);
        	targetTask.setCreatedTime(new Date());
        	targetTask.setTransactor(transactor);
        	User targetUser = userManager.getUserByLoginName(targetTask.getTransactor());
        	if(targetUser!=null){
        		targetTask.setTransactorName(targetUser.getName());
        	}
        	tasks.add(targetTask);
    	}
    	saveTasks(tasks);
    	//设置指派人任务已完成
    	task.setTaskProcessingResult(TaskProcessingResult.ASSIGN);
    	task.setActive(TaskState.ASSIGNED.getIndex());
    	task.setTransactDate(new Date());
    	StringBuilder result=new StringBuilder(CommonStrings.ASSIGN_TO);
    	for(WorkflowTask wfTask:tasks){
    		result.append(wfTask.getId()).append(",");
    	}
    	if(result.lastIndexOf(",")>=0){
    		task.setNextTasks(result.substring(0, result.lastIndexOf(",")));
    	}
    	saveTask(task);
    }
    
    /**
     * 通过自定义实体获取填写了数据的HTML片段
     * @param entity extends FormFlowable
     * @param htmlParameterName html片段中变量的key eg: obj.name,则htmlParameterName为obj
     * @return
     */
    public String getHtmlByData(FormFlowable entity){
    	WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(entity.getWorkflowInfo().getWorkflowId(),entity.getCompanyId());
    	FormView form = formViewManager.getFormView(wi.getFormId());
    	
    	String html = form.getHtml();
    	return FreeMarkertUtils.render(entity, html);
    }
    
    /**
	 * 根据流程名称查询最新的流程定义表单
	 * @param workflowDefinitionName
	 * @return
	 */
    public String getLastHtmlByWorkflowDefinitionName(FormFlowable entity, String processDefinitionName){
    	WorkflowDefinition defintion = workflowDefinitionManager.getLatestVersion(processDefinitionName, entity.getCompanyId());
    	String html = formViewManager.getCurrentFormViewByCodeAndVersion(defintion.getFormCode(), defintion.getVersion()).getHtml();
    	return FreeMarkertUtils.render(entity, html);
    }
    
    /**
     * 环节办理过程中办理人是否可以删除流程实例
     * @return
     */
    public boolean canDeleteByTask(WorkflowInstance workflow, String taskName){
    	return workflowRightsManager.workflowDeleteRight(workflow, taskName);
    }
    
    /**
	 * 加签
	 */
    @Transactional(readOnly=false)
	public void generateTask(Long taskId,Collection<String> transacts,TaskSource taskSource){
		WorkflowTask task = getTask(taskId);
		if(task==null){log.debug("加签时，任务不能为null");throw new RuntimeException("加签时，任务不能为null");}
		Set<String> transactors=new HashSet<String>();
		if(transacts!=null)transactors.addAll(transacts);
		//得到当前办理人集合
		List<String[] > currentTransactors=workflowTaskService.getActivityTaskTransactors(task.getProcessInstanceId(), task.getCompanyId());
		//得到当前委托人集合
		List<String> currentPrincipals=workflowTaskService.getActivityTaskPrincipals(task.getProcessInstanceId(), task.getCompanyId());
		
		//得到所有当前办理人和委托人集合
		Set<String> currentTransacts=new HashSet<String>();
		for(int i=0;i<currentTransactors.size();i++){
			Object[] transactor1=currentTransactors.get(i);
			currentTransacts.add(transactor1[0].toString());
		}
		if(currentPrincipals!=null)currentTransacts.addAll(currentPrincipals);
		
		List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
		Integer groupNum=getTaskMaxGroupNum(task.getProcessInstanceId(), task.getName(),task.getCompanyId());
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		String processId= instance.getProcessDefinitionId();
		Assert.notNull( ContextUtils.getCompanyId(),"加签时，公司id不能为null");
		//根据受托人获得委托人集合
		List<String> consigners=delegateManager.getConsignerByTrustee(task.getTransactor(), ContextUtils.getCompanyId(), processId, task.getName());
		//得到受托人姓名的集合
		List<String> historyTransactors = new ArrayList<String>();
		
		for(String transactor:transactors){
			if(StringUtils.isNotEmpty(transactor)){
				if(CommonStrings.ALL_USER.equals(transactor)){//加签所有人
					allUsersGenerateTask(task,tasks,groupNum,currentTransacts,consigners,historyTransactors,taskSource);
				}else{
					//如果当前环节办理人包括当前用户，并且任务未完成，则不生成加签任务！0:等待处理  1:等待设置办理人  4:待领取  6：待选择环节
					if(currentTransacts.contains(transactor)&&ContextUtils.getLoginName().equals(transactor) && (task.getActive()==0 || task.getActive()==1 || task.getActive()==4 || task.getActive()==6)){
						continue;
					}
					//如果是委托任务，且委托人等于选择的人transactor，则不生成任务（bkyoa要求的）
					if(transactor.equals(task.getTrustor()))continue;
					//判断用户是否已有任务，若没有则生成任务,或者当前办理人加签给自己并且任务状态为已完成时也可生成任务
					if(!currentTransacts.contains(transactor)||ContextUtils.getLoginName().equals(transactor)){
						//判断用户是否已有任务，若没有则生成任务
						if(!consigners.contains(transactor)||StringUtils.isEmpty(task.getTrustor())){//源任务不是委托任务或transactor不是当前用户的委托人则生成任务
							//生成任务
							usersGenerateTask(task,tasks,transactor,groupNum,historyTransactors,taskSource,transactors);
						}
					}
				}
			}
		}
		saveTasks(tasks);
		//生成加签流转历史
		generateAddSignerHistory(task,historyTransactors);
	}
    
    
    /**
	 *生成加签流转历史
	 * @param task
	 * @param transactor
	 */
	private void generateAddSignerHistory(WorkflowTask task, 
			List<String> historyTransactors) {
		
		StringBuilder historyMessage = new StringBuilder();
		historyMessage.append(dateFormat.format(new Date()))
		.append(COMMA).append(ContextUtils.getUserName()).append("给:")
		.append(getTansactorNames(historyTransactors)).append("加签。")
		.append(DELTA_START)
		.append(task.getName()).append(DELTA_END).append("\n");
		
		InstanceHistory history = new InstanceHistory(task.getCompanyId(), task.getProcessInstanceId(), InstanceHistory.TYPE_TASK, historyMessage.toString(), task.getName());
		history.setEffective(false);
		history.setCreatedTime(new Date());
		history.setExecutionId(task.getProcessInstanceId());
		
        instanceHistoryManager.saveHistory(history);
		
	}
    
    private String getTansactorNames(List<String> historyTransactors) {
		String result = "";
		for(String name : historyTransactors ){
			result+=name+",";
		}
		return StringUtils.isEmpty(result)?"":result.substring(0,result.length()-1);
	}

	private void allUsersGenerateTask(WorkflowTask task,
    		List<WorkflowTask> tasks,
    		Integer groupNum,
    		Set<String> currentTransacts,
    		List<String> consigners,
    		List<String> historyTransactors,TaskSource taskSource){
    	WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
    	String processId= instance.getProcessDefinitionId();
		List<com.norteksoft.product.api.entity.User> users=ApiFactory.getAcsService().getUsersByCompany(task.getCompanyId());
    	for(com.norteksoft.product.api.entity.User user:users){
    		//如果用户是当前办理人，并且任务未完成，则不生成加签任务！0:等待处理  1:等待设置办理人  4:待领取  6：待选择环节
    		if(currentTransacts.contains(user.getLoginName())&&ContextUtils.getLoginName().equals(user.getLoginName()) && (task.getActive()==0 || task.getActive()==1 || task.getActive()==4 || task.getActive()==6)){
    			continue;
    		}
    		//判断用户是否已有任务，若没有则生成任务,或者当前办理人加签给自己并且任务状态为已完成时也可生成任务
    		if(!currentTransacts.contains(user.getLoginName())||ContextUtils.getLoginName().equals(user.getLoginName())){
    			if(!consigners.contains(user.getLoginName())||StringUtils.isEmpty(task.getTrustor())){//源任务不是委托任务或user不是当前用户的委托人则生成任务
    				//任务委托设置
    				String delegateTransactor = delegateManager.getDelegateMainName(
    						task.getCompanyId(), user.getLoginName(), processId, task.getName());
    				boolean shouldSaveTask = true;
    				WorkflowTask targetTask = task.clone();
    				targetTask.setId(null);
    				String titleInfo=setTaskTransactorInfo(user.getLoginName(), targetTask);
    				if(delegateTransactor != null){
    					boolean isSameDept=trustorTransactorSameDept(targetTask.getTransactor(),delegateTransactor);
    					if(isSameDept){//如果委托人、受托人是同一部门，且当前传过来的人员列表包含受托人时，则委托人不再生成任务（当然受托人也不用生成了）,因为是所有人，所以只需判断委托人、受托人是否是同一部门
							shouldSaveTask=false;
    					}
    				}
					User targetUser = userManager.getUserByLoginName(targetTask.getTransactor());
					if(targetUser!=null){
						String userName = targetUser.getName();
						targetTask.setTransactorName(userName);
						historyTransactors.add(userName);
					}
					targetTask.setCreatedTime(new Date());
					targetTask.setSendingMessage(true);
					//设置标题
					addTitle(targetTask, processId,titleInfo);
					WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
					//流程类型
					WorkflowType type=null;
					if(wfDef!=null){
						targetTask.setGroupName(wfDef.getName());
						targetTask.setCustomType(wfDef.getCustomType());
						//流程类型
						type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
						if(type!=null)targetTask.setCategory(type.getName());
					}
					targetTask.setTrustor(null);
					targetTask.setTrustorName(null);
					if(delegateTransactor != null){
						targetTask.setTrustor(targetTask.getTransactor());
						User targetTaskUser = userManager.getUserByLoginName(targetTask.getTransactor());
						if(targetTaskUser!=null)targetTask.setTrustorName(targetTaskUser.getName());
						targetTask.setTransactor(delegateTransactor);
						User transacorUser=userManager.getUserByLoginName(targetTask.getTransactor());
						if(transacorUser!=null){
							targetTask.setTransactorName(transacorUser.getName());
						}
						//生成委托流转历史
						generateDelegateHistory(targetTask);
					}else{
						targetTask.setTrustor(null);
						targetTask.setTrustorName(null);
					}
					targetTask.setGroupNum(groupNum);
					targetTask.setRead(false);
					targetTask.setTaskSource(taskSource);
					//是否显示，bkyoa特制功能
					targetTask.setVisible(shouldSaveTask);
					
					//*******************消息提醒*************************
					sendMessage(targetTask,type);
					//*******************邮件通知*************************
					sendMail(task,processId);
					tasks.add(targetTask);
    			}
    		}
    	}
    }
    
    private void usersGenerateTask(WorkflowTask task,
    		List<WorkflowTask> tasks,
    		String transactor,
    		Integer groupNum,
    		List<String> historyTransactors,TaskSource taskSource,Set<String> transactors){
    	WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
    	String processId= instance.getProcessDefinitionId();
    		WorkflowTask targetTask = task.clone();
    		//transactor的值“办理人登录名”或“办理人登录名:用户自定义信息”或“办理人登录名:$custom:customInfo$title:titleInfo”
			targetTask.setId(null);
			String titleInfo=setTaskTransactorInfo(transactor, targetTask);
			//任务委托设置
			String delegateTransactor = delegateManager.getDelegateMainName(
					task.getCompanyId(), targetTask.getTransactor(), processId, task.getName());
			boolean shouldSaveTask = shouldSaveTask(task.getCompanyId(),targetTask.getTransactor(),transactors,task.getProcessInstanceId(),delegateTransactor,true);
			User user=userManager.getUserByLoginName(targetTask.getTransactor());
			if(user!=null){
				targetTask.setTransactorName(user.getName());
				historyTransactors.add(user.getName());
			}
			targetTask.setCreatedTime(new Date());
			targetTask.setSendingMessage(true);
			//设置标题
			addTitle(targetTask, processId,titleInfo);
			WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
			//流程类型
			WorkflowType type=null;
			if(wfDef!=null){
				targetTask.setGroupName(wfDef.getName());
				targetTask.setCustomType(wfDef.getCustomType());
				//流程类型
				type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
				if(type!=null)targetTask.setCategory(type.getName());
			}
			targetTask.setTrustor(null);
			targetTask.setTrustorName(null);
			if(delegateTransactor != null){
				targetTask.setTrustor(targetTask.getTransactor());
				user =  userManager.getUserByLoginName(targetTask.getTransactor());
				if(user!=null)targetTask.setTrustorName(user.getName());
				targetTask.setTransactor(delegateTransactor);
				user=userManager.getUserByLoginName(targetTask.getTransactor());
				if(user!=null){
					targetTask.setTransactorName(user.getName());
				}
				//生成委托流转历史
				generateDelegateHistory(targetTask);
			}else{
				targetTask.setTrustor(null);
				targetTask.setTrustorName(null);
			}
			targetTask.setGroupNum(groupNum);
			targetTask.setRead(false);
			targetTask.setTaskSource(taskSource);
			//是否显示，bkyoa特制功能
			targetTask.setVisible(shouldSaveTask);
			
			//*******************消息提醒*************************
			sendMessage(targetTask,type);
			//*******************邮件通知*************************
			sendMail(task,processId);
			tasks.add(targetTask);
    }
    private boolean shouldSaveTask(Long companyId,String taskName,Set<String> transactors,String processInstanceId,String delegateTransactor,boolean moreTransactor){
		boolean shouldSaveTask = true;
		//bkyoa特制功能 ：当是多人办理环节时，如果委托人、受托人是同一部门，且当前传过来的人员列表包含受托人时或受托人已有任务时，则委托人不再生成任务（当然受托人也不用生成了）
		if(moreTransactor){//多人办理环节才需要该判断
			if(delegateTransactor != null){
				boolean isSameDept=trustorTransactorSameDept(taskName,delegateTransactor);
				if(isSameDept){//如果委托人、受托人是同一部门，且当前传过来的人员列表包含受托人时或受托人已有任务时，则委托人不再生成任务（当然受托人也不用生成了）
					if(transactors!=null&&transactors.contains(delegateTransactor)){//当前传过来的人员列表包含受托人时,不生成任务
						shouldSaveTask=false;
					}else{
						WorkflowTask mytask=workflowTaskService.getMyTask(processInstanceId, companyId, delegateTransactor);
						if(mytask!=null){//如果受托人已有任务
							shouldSaveTask=false;
						}
					}
				}
			}
		}
		return shouldSaveTask;
    }
    private String setTaskTransactorInfo(String transactor, WorkflowTask targetTask){
    	//transactor的值“办理人登录名”或“办理人登录名:xxx”或“办理人登录名:$custom:xxx$title:xxx”
    	String titleInfo=null;
    	if(transactor.contains(":")){
			String[] transacts=transactor.split(":");
			targetTask.setTransactor(transacts[0]);
			boolean containCustom=transactor.contains(TASK_CUSTOM_INFO);
			boolean containTitle=transactor.contains(TASK_TITLE_INFO);
			if(containCustom||containTitle){//办理人登录名:$custom:xxx$title:xxx
				if(containCustom){//$custom(用户自定义信息，如部门名称、角色名称等等任何信息)
					String remark=transactor.split("\\"+TASK_CUSTOM_INFO+":")[1];
					if(containTitle){
						titleInfo=remark.split("\\"+TASK_TITLE_INFO+":")[1];
						remark=remark.split("\\"+TASK_TITLE_INFO+":")[0];
					}
					targetTask.setRemark(remark);//将$custom:的信息放入任务的扩展字段remark中
				}else if(containTitle){
					titleInfo=transactor.split("\\"+TASK_TITLE_INFO+":")[1];
				}
			}else{//办理人登录名:xxx
				targetTask.setRemark(transacts[1]);
			}
		}else{//办理人登录名
			targetTask.setTransactor(transactor);
		}
    	return titleInfo;
    }
	/**
	 * 获得与该任务是同一环节的所有未办理任务
	 */
	public List<WorkflowTask> getCountersigns(Long id){
		return workflowTaskService.getCountersigns(id);
	}
	
	/**
	 * 获得与该任务是同一环节的所有未办理任务
	 */
	public List<WorkflowTask> getProcessCountersigns(Long id){
		return workflowTaskService.getProcessCountersigns(id);
	}
	
	/**
	 * 获得该会签环节的所有办理人
	 */
	public List<String> getCountersignsHandler(Long id,Integer handlingState){
		return workflowTaskService.getCountersignsHandler(id,handlingState);
	}
	
	@Transactional(readOnly=false)
	public void deleteCountersignHandler(Long taskId, Collection<String> users) {
		workflowTaskService.deleteCountersignHandler(taskId, users);
	}
	
	/**
	 * 删除任务
	 */
	@Transactional(readOnly=false)
	public void deleteWorkflowTask(List<Long> ids){
		workflowTaskService.deleteWorkflowTask(ids);
	}

    /**
     * 取回任务
     * @param taskId
     * @return 是否成功
     */
	@Transactional(readOnly=false)
    public String retrieve(Long taskId){
    	log.debug("*** retrieve 方法开始");
    	
    	Assert.notNull(taskId,"取回任务时任务id不能为null");
    	//FIXME 使用国际化key替换中文字符
    	WorkflowTask task = workflowTaskService.getTask(taskId);
    	Assert.notNull(task,"取回任务时任务不能为null");
    	// 判断后面的环节是否已经办理//a.流程已经结束，b.后面的环节已阅
    	WorkflowInstance wfi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),task.getCompanyId());
    	if(wfi==null){log.debug("取回任务时，流程实例不能为null");throw new RuntimeException("取回任务时，流程实例不能为null");}
    	if(wfi.getProcessState()==ProcessState.END || wfi.getProcessState()==ProcessState.MANUAL_END || wfi.getProcessState()==ProcessState.PAUSE){
    		if(wfi.getParentProcessId()==null){
    			return "流程已经结束或者被取消或者被暂停，不能取回";
    		}else{
    			return "子流程已经结束或者被取消或者被暂停，不能取回";
    		}
    	}
    	if(isSubProcessTask(task)){
    		return "下一环节是子流程环节，不能取回";
    	}
    	// 会签、投票特殊处理
    	if(TaskProcessingMode.TYPE_COUNTERSIGNATURE.toString().equals(task.getProcessingMode()) ||
    			TaskProcessingMode.TYPE_VOTE.toString().equals(task.getProcessingMode())){
    		return "会签、投票环节不能取回";
    	}
    	if(task.getMoreTransactor()){
    		return "多人办理环节不能取回";
    	}
    	Execution execut=processEngine.getExecutionService().findExecutionById(task.getExecutionId());
		if((execut!=null && task.getNextTasks()==null)||(execut==null && task.getNextTasks()!=null)){
			return "并发流程的分支已结束不能取回";
		}
    	String result = "任务已取回";
    	if(isNextTasksRead(task)){
    		// 已阅：用户不能取回
    		result = "下环节任务已阅，取回失败";
    	}else{
    		//取回任务
    		// 1. 使JBPM环节回到该任务
    		Activity activity = processEngine.execute(new GetBackCommand(task.getExecutionId(), task.getName()));
    		if(activity == null){
    			//不支持并发取回
    			result = "并发流程不能取回";
    		}else{
	    		// 2. 修改后面的环节为被取回(或删掉)
	    		String[] taskNames = task.getNextTasks().split(",");
	    		workflowTaskService.deleteTasksByName(task.getCompanyId(), task.getProcessInstanceId(), taskNames);
	    			
	    		//环节跳转后task.getNextTasks()获得的环节名不对了
	    		List<String> names=workflowTaskService.getActiveTaskNameWithoutSpecial(task.getProcessInstanceId());
	    			taskNames=names.toArray(new String[names.size()]);
	    			workflowTaskService.deleteTasksByName(task.getCompanyId(), task.getProcessInstanceId(), taskNames);
	    			
	    		//FIXME 判断任务是否需要指定办理人？
	    		// 3 设置办理为当前取回的任务的办理人
	    		ActivityExecution execution = (ActivityExecution) processEngine.getExecutionService().findExecutionById(task.getExecutionId());
	    		if(execution==null){log.debug("取回任务时，execution不能为null");throw new RuntimeException("取回任务时，execution不能为null");}
	    		org.jbpm.api.task.Task jbpmTask = processEngine.getTaskService().createTaskQuery()
	    		.processInstanceId(task.getProcessInstanceId())
	    		.activityName(execution.getActivityName()).uniqueResult();   
	    		
	    		if(jbpmTask==null){log.debug("取回任务时，jbpmTask不能为null");throw new RuntimeException("取回任务时，jbpmTask不能为null");}
        		processEngine.getTaskService().assignTask(jbpmTask.getId(),task.getTransactor());
	    		// 4. 修改本任务为未完成(删除任务)
	    		task.setActive(TaskState.WAIT_TRANSACT.getIndex());
	    		wfi.setInstanceTitle(task.getTitle());
	    		//取回的的任务,不能退回
	    		wfi.setPreviousActivity(null);
	    		wfi.setPreviousActivityTitle(null);
	    		// 5 删除流转历史
	    		instanceHistoryManager.deleteHistoryByTask(task.getCompanyId(), task.getProcessInstanceId(), task.getId(), taskNames);
	    		// 5 删除流转历史
	    		opinionDao.deleteOpinionsByTask(task.getCompanyId(), task.getProcessInstanceId(), task.getId());
	    		// 6 修改实体数据中的流程实例的一些信息
	    		WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),task.getCompanyId());
	    		if(instance==null){log.debug("取回任务时，流程实例不能为null");throw new RuntimeException("取回任务时，流程实例不能为null");}
	    		WorkflowDefinition wfDef=workflowDefinitionManager.getWfDefinition(instance.getWorkflowDefinitionId());
	    		if(wfDef==null){log.debug("取回任务时，流程定义实体不能为null");throw new RuntimeException("取回任务时，流程定义实体不能为null");}
	    		FormView formView=formViewManager.getCurrentFormViewByCodeAndVersion(wfDef.getFormCode(),wfDef.getFromVersion());
	    		if(formView==null){log.debug("取回任务时，表单不能为null");throw new RuntimeException("取回任务时，表单不能为null");}
	    		if(formView.isStandardForm()){
	    			try{
	    				if(formView.getDataTable()==null){log.debug("取回任务时，表单对应的数据表不能为null");throw new RuntimeException("取回任务时，表单对应的数据表不能为null");}
	    				Object entity = generalDao.getObject(formView.getDataTable().getEntityName(), instance.getDataId());
	    				BeanUtils.setProperty(entity, "workflowInfo.currentActivityName", task.getName());
	    				generalDao.save(entity);
	    			}catch (Exception e) {
						throw new RuntimeException(e);
					} 
	    		}
	    		task.setNextTasks(null);
	    		task.setVisible(true);
	    		saveTask(task);
	    		WorkflowType type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
	    		//发送消息
	    		sendMessage(task, type);
	    		//*******************邮件通知*************************
				sendMail(task,wfDef.getProcessId());
	    		//业务补偿
	    		retrieveTaskSet(instance,task);
	    		workflowInstanceManager.saveWorkflowInstance(wfi);
	    		execution.removeVariable(CommonStrings.TRANSACTOR_SINGLE_CANDIDATES);
    		}
    	}
    	
    	log.debug("*** retrieve 方法结束");
    	return result;
    }
	
	/**
	 * 当是取回任务时业务补偿
	 * @param instance
	 * @param form
	 */
	private void retrieveTaskSet(WorkflowInstance instance,WorkflowTask task){
		String retrieveTaskSet = DefinitionXmlParse.getRetrieveTaskSet(instance.getProcessDefinitionId());
		if(StringUtils.isNotEmpty(retrieveTaskSet)){
			RetrieveTaskInterface retrieveTaskBean=(RetrieveTaskInterface)ContextUtils.getBean(retrieveTaskSet);
			retrieveTaskBean.retrieveTaskExecute(instance.getDataId(),task.getId());
		}
	}
    
    /*
     * 查询Task后面的环节是否已阅
     * @param task
     * @return
     */
	private boolean isNextTasksRead(WorkflowTask task){
    	log.debug("*** isNextTasksRead 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("task:").append(task)
			.append("]").toString());
    	
    	
    	String nextTasks = task.getNextTasks();
    	if(nextTasks == null) return false;
		String[] taskNames = nextTasks.split(",");
		if(taskNames.length == 1 && PROCESS_END_EN.equals(taskNames[0])){
			//流程已经结束
			return true;
		}
		for(String name : taskNames){
			List<WorkflowTask> tasks = workflowTaskService.getTasksByName(task.getCompanyId(), task.getProcessInstanceId(), name);
			if(tasks.size() == 1 &&tasks.get(0).getRead()){
				return true;
			}else if(tasks.size() > 1 &&!tasks.get(0).getMoreTransactor()){//单人办理已有多条任务
				for(WorkflowTask t : tasks){
					if(!t.getRead()) return false;
				}
				return true;
			}
		}
		
		log.debug("*** isNextTasksRead 方法结束");
    	return false;
    }
    
    private boolean isSubProcessTask(WorkflowTask task){
    	log.debug("*** isSubProcessTask 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
			.append("task:").append(task)
			.append("]").toString());
    	String nextTasks = task.getNextTasks();
    	WorkflowInstance wfi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),task.getCompanyId());
    	if(wfi==null){log.debug("isSubProcessTask中，流程实例不能为null");throw new RuntimeException("isSubProcessTask中，流程实例不能为null");}
    	if(nextTasks != null){
    		String[] taskNames = nextTasks.split(",");
    		for(String name:taskNames){
    			return DefinitionXmlParse.isSubProcessTask(wfi.getProcessDefinitionId(), name);
    		}
    	}
    	log.debug("*** isSubProcessTask 方法结束");
    	return false;
    }
    
    /**
     * 当前实例的所有有效的办理人
     * @param instanceId
     * @return
     */
    public List<String> getParticipantsTransactor(String instanceId){
    	WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(instanceId);
    	return workflowTaskService.getParticipantsTransactor(instance.getCompanyId(), instanceId);
    }
    
    /**
     * 根据任务查询投票的结果
     * @param instanceId
     * @param taskName
     * @return int[同意票，不同意票，弃权票]
     */
    public long[] getVoteResults(String instanceId, String taskName,Long companyId){
    	log.debug("*** getVoteResults 方法开始");
    	
    	Integer groupNum=getTaskMaxGroupNum(instanceId,taskName,companyId);
    	List<WorkflowTask> tasks = workflowTaskService.getNoAssignTasksByName(companyId, instanceId, taskName,groupNum);
    	int agreement = 0;
    	int oppose = 0;
    	int kiken = 0;
    	for(WorkflowTask t : tasks){
    		if(TaskProcessingResult.AGREEMENT==t.getTaskProcessingResult()){
    			agreement++;
    		}else if(TaskProcessingResult.OPPOSE==t.getTaskProcessingResult()){
    			oppose++;
    		}else if(TaskProcessingResult.KIKEN==t.getTaskProcessingResult()){
    			kiken++;
    		}
    	}
    	
    	log.debug("*** getVoteResults 方法结束");
    	return new long[]{agreement, oppose, kiken};
    }
    
    /**
     * 根据任务查询任务会签的结果
     * @param instanceId
     * @param taskName
     * @return int[同意票，不同意票]
     */
    public long[] getCountersignatureResult(String instanceId, String taskName,Long companyId){
    	log.debug("*** getCountersignatureResult 方法开始");
    	Integer groupNum=getTaskMaxGroupNum(instanceId,taskName,companyId);
    	List<WorkflowTask> tasks = workflowTaskService.getNoAssignTasksByName(companyId, instanceId, taskName,groupNum);
    	int approve = 0;
    	int refuse = 0;
    	for(WorkflowTask t : tasks){
    		if(TaskProcessingResult.APPROVE==t.getTaskProcessingResult()){
    			approve++;
    		}else if(TaskProcessingResult.REFUSE==t.getTaskProcessingResult()){
    			refuse++;
    		}
    	}
    	
    	log.debug("*** getCountersignatureResult 方法结束");
    	return new long[]{approve, refuse};
    }
    
    /**
     * 我委托的所有流程
     * @param companyId
     * @param tasks
     * @param loginName
     * @return
     */
	public Page<WorkflowTask> getDelegateTasks(Long companyId, Page<WorkflowTask> tasks, String loginName){
		tasks = workflowTaskService.getDelegateTasks(companyId, loginName, tasks);
    	return tasks;
    }

	/**
	 * 按是否结束查询我委托的流程
	 * @param companyId
	 * @param tasks
	 * @param longinName
	 * @param isEnd
	 * @return
	 */
	public Page<WorkflowTask> getDelegateTasksByActive(Long companyId,
			Page<WorkflowTask> tasks, String loginName, Boolean isEnd) {
		return workflowTaskService.getDelegateTasksByActive(companyId, loginName, tasks, isEnd);
	}
	
	public Page<WorkflowTask> getTaskAsTrustee(Long companyId,
			Page<WorkflowTask> tasks, String loginName, Boolean isEnd) {
		return workflowTaskService.getTaskAsTrustee(companyId, loginName, tasks, isEnd);
	}
	
	public Integer getDelegateTasksNum(Long companyId, String loginName){
		return workflowTaskService.getDelegateTasksNum(companyId, loginName);
	}
	
	public Integer getDelegateTasksNum(Long companyId, String loginName, Boolean isEnd){
		return workflowTaskService.getDelegateTasksNumByActive(companyId, loginName, isEnd);
	}
	
	public Integer getTrusteeTasksNum(Long companyId, String loginName, Boolean isEnd){
		return workflowTaskService.getTrusteeTasksNum(companyId, loginName, isEnd);
	}
    
	/**
	 * 查询流程实例的第一个任务
	 * @return
	 */
	public WorkflowTask getFirstTask(String instanceId, String transactor){
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(instanceId);
		String firstTaskName = DefinitionXmlParse.getFirstTaskName(instance.getProcessDefinitionId());
		List<WorkflowTask> tasks = this.getWorkflowTasks(instanceId, firstTaskName);
		WorkflowTask resultTask = null;
		for(WorkflowTask task:tasks){
			if(task.getTransactor().equals(transactor)){
				resultTask = task;
				break;
			}
		}
		if(!resultTask.getMoreTransactor()&&resultTask.getActive().equals(TaskState.DRAW_WAIT.getIndex())){
				for(WorkflowTask task : tasks){
					if(task.equals(resultTask)){
						resultTask.setActive(TaskState.WAIT_TRANSACT.getIndex());
					}else{
						task.setActive(TaskState.CANCELLED.getIndex());
					}
				}
		}
		return resultTask;
	}
	
	
	
	/**
	 * 查询任务
	 * @param id
	 * @return
	 */
	public WorkflowTask getWorkflowTask(Long id){
		Assert.notNull(id, "任务Id不能为null");
		return workflowTaskService.getTask(id);
	}
	
	@Autowired
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

	/**
	 * 删除任务
	 * @param processInstanceId
	 * @param companyId
	 */
	@Transactional(readOnly=false)
	public void deleteTaskByProcessId(String processInstanceId, Long companyId) {
		workflowTaskService.deleteTaskByProcessId(processInstanceId,companyId);
	}

	/**
	 * 手动结束流程
	 */
	@Transactional(readOnly=false)
	public void endTasks(String processInstanceId, Long companyId) {
		Assert.notNull(companyId,"手动结束流程时companyId不能为空");
		workflowTaskService.endTasks(processInstanceId,companyId);
	}
	/**
	 * 强制结束流程
	 */
	@Transactional(readOnly=false)
	public void compelEndTasks(String processInstanceId, Long companyId) {
		Assert.notNull(companyId,"手动结束流程时companyId不能为空");
		workflowTaskService.endTasks(processInstanceId,companyId);
	}

	/**
	 * 得到当前活动的任务
	 */
	public List<WorkflowTask> getActivityTasks(String processInstanceId, Long companyId) {
		return workflowTaskService.getActivityTasks(processInstanceId,companyId);
	}
	
	public WorkflowTask getMyTask(String processInsatnceId,Long companyId ,String loginName){
		Assert.notNull(companyId,"查询用户当前办理人的当前任务时，公司id不能为null");
		return workflowTaskService.getMyTask(processInsatnceId, companyId, loginName);
	}
	
	/**
	 * 保存自动填写域数据(办理后)
	 * @param task
	 */
	@Transactional(readOnly=false)
	public void saveAutomaticallyFilledField(String processInstanceId,String taskName, String currentOperation){
		log.debug(PropUtils.LOG_METHOD_BEGIN+"TaskService+saveAutomaticallyFilledField(String processInstanceId,String taskName, String currentOperation)"+PropUtils.LOG_FLAG);
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(processInstanceId);
		if(wi==null){
			log.debug("环节自动填写字段时，流程实例不能为null");
			throw new RuntimeException("环节自动填写字段时，流程实例不能为null");
		}
		log.debug(PropUtils.LOG_CONTENT+"实例workflowInstance"+PropUtils.LOG_FLAG + wi);
		Assert.notNull(wi.getFormId(),"流程对应的表单id不能为null");
		FormView form = formViewManager.getFormView(wi.getFormId());
		if(form==null){
			log.debug("环节自动填写字段时，流程对应的表单不能为null");
			throw new RuntimeException("环节自动填写字段时，流程对应的表单不能为null");
		}
		log.debug(PropUtils.LOG_CONTENT+"对应的表单formView"+PropUtils.LOG_FLAG + form );
		log.debug(PropUtils.LOG_CONTENT+"对应的表单是否是标准字段,true表示是标准表单"+PropUtils.LOG_FLAG + form.isStandardForm() );
		if(form.isStandardForm()){
			saveAutomaticallyFilledFieldEntity(wi,form,taskName, currentOperation);
		}else if(!form.isStandardForm()){
			Map<String,String[]> automaticallyFilledFieldMap  = getAutomaticallyFilledFields(wi,taskName, currentOperation);
			log.debug(PropUtils.LOG_CONTENT+"需要自动填写的字段为:"+PropUtils.LOG_FLAG + automaticallyFilledFieldMap.toString());
			if(!automaticallyFilledFieldMap.isEmpty()) formViewManager.saveFormContentToTable(automaticallyFilledFieldMap,wi.getFormId(),wi.getDataId());
		}
		log.debug(PropUtils.LOG_METHOD_END+"TaskService+saveAutomaticallyFilledField(String processInstanceId,String taskName, String currentOperation)"+PropUtils.LOG_FLAG);
	}
	
	/**
	 * 得到定义文件中需要自动填写的设置
	 */
	public List<AutomaticallyFilledField> getAutomaticallyFilledFields(String processDefinitionId,String taskName){
		return DefinitionXmlParse.getAfterFilledFields(processDefinitionId, taskName);
	}
	
	@Transactional(readOnly=false)
	private void saveAutomaticallyFilledFieldEntity(WorkflowInstance wi,FormView form,String taskName, String currentOperation){
		try {
			Assert.notNull(wi,"流程实例不能为null");
			Assert.notNull(form,"流程对应的表单不能为null");
			Assert.notNull(form.getDataTable(),"表单对应的数据表不能为null");
			Assert.notNull(wi.getDataId(),"流程对应的实体id不能为null");
			Object entity = generalDao.getObject(form.getDataTable().getEntityName(), wi.getDataId());
			List<AutomaticallyFilledField> autoFilledFields = getAutomaticallyFilledFields(wi.getProcessDefinitionId(),taskName);
			log.debug("需要自动填写的字段为:" + autoFilledFields);
			for(AutomaticallyFilledField aff : autoFilledFields){
				Object value = getValueEntity(wi,   currentOperation,aff);
				log.debug("需要自动填写的字段值为:" + value);
				if(value!=null&&StringUtils.isEmpty(value.toString()))value=null;
				PropertyUtils.setProperty(entity, aff.getName(), value);
			}
			 generalDao.save(entity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	/*
	 * 实体类型的自动填写值
	 * @param wi
	 * @param taskTransactor
	 * @param currentOperation
	 * @param aff
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private Object getValueEntity(WorkflowInstance wi, String currentOperation,AutomaticallyFilledField aff) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Assert.notNull(wi.getFormId(),"自动填写时流程对应的表单id不能为null");
		FormView form = formViewManager.getFormView(wi.getFormId());
		Assert.notNull(form,"自动填写时流程对应的表单不能为null");
		Assert.notNull(form.getDataTable(),"自动填写时表单对应的数据表不能为null");
		Assert.notNull(wi.getDataId(),"自动填写时实例对应的实体id不能为null");
		Object entity = generalDao.getObject(form.getDataTable().getEntityName(), wi.getDataId());
		List<FormControl> fieldsList = this.formViewManager.getControls(wi.getFormId());
		FormControl field = getFieldbyName(fieldsList,aff.getName() );
		Object value ;
		if(field.getDataType()==DataType.TIME || field.getDataType()==DataType.DATE){
			log.debug("需要自动填写的字段为DATE或TIME类型" );
			value = StringUtils.contains(aff.getValue(), CommonStrings.CURRENTTIME) ? new Date() :null;
		}else if(field.getDataType()==DataType.TEXT){
			log.debug("需要自动填写的字段为TEXT类型" );
			value = getValue(aff, currentOperation);
			if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDITIONAL)){
				String originalValue = BeanUtils.getProperty(entity, aff.getName());
				if(value!=null&&StringUtils.isNotEmpty(originalValue)){//如果配的值不为空且数据库中的值也不为空时，则其值为追加后的值
					value = originalValue+","+value;
				}else if(value==null&&StringUtils.isNotEmpty(originalValue)){//如果配的值为空且数据库的值不为空时，则其值为数据库中的值不作修改
					value= originalValue;
				}
			}else if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDED_TO_THE_BEGINNING)){
				String originalValue = BeanUtils.getProperty(entity, aff.getName());
				if(value!=null&&StringUtils.isNotEmpty(originalValue)){
					value = value + ","+originalValue;
				}else if(value==null&&StringUtils.isNotEmpty(originalValue)){
					value= originalValue;
				}
			}
		}else if(field.getDataType()==DataType.NUMBER){
			value = Integer.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.AMOUNT){
			value = Float.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.INTEGER){
			log.debug("需要自动填写的字段为INTEGER类型" );
			value = Integer.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.LONG){
			log.debug("需要自动填写的字段为LONG类型" );
			value = Long.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.DOUBLE){
			log.debug("需要自动填写的字段为DOUBLE类型" );
			value = Float.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.FLOAT){
			log.debug("需要自动填写的字段为FLOAT类型" );
			value = Float.valueOf(aff.getValue());
		}else if(field.getDataType()==DataType.BOOLEAN){
			log.debug("需要自动填写的字段为BOOLEAN类型" );
			value = Boolean.parseBoolean(aff.getValue());
		}else{
			log.debug("需要自动填写的字段为不是指定的类型" );
			value = aff.getValue();
		}
		return value;
	}
	
	
	/**
	 * 得到需字段填写的字段
	 */
	public Map<String,String[]> getAutomaticallyFilledFields(WorkflowInstance wi,String taskName,String currentOperation){
		Map<String,String[]> automaticallyFilledFieldMap = new HashMap<String,String[]>();
		List<AutomaticallyFilledField> autoFilledFields = DefinitionXmlParse.getAfterFilledFields(wi.getProcessDefinitionId(), taskName);
			for(AutomaticallyFilledField aff : autoFilledFields){
				String value = getAutoFilledFieldValue(wi, currentOperation,aff);
					automaticallyFilledFieldMap.put(aff.getName(),new String[]{value });
			}
		return automaticallyFilledFieldMap;
	}
	
	
	/**
	 * 返回自动填写域表示的值
	 * @param condition
	 * @param currentOperation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private  String getAutoFilledFieldValue(WorkflowInstance wi, String currentOperation,AutomaticallyFilledField aff){
		log.debug("*** getAutoFilledFieldValue 方法开始");
		
		FormView form = formViewManager.getFormView(wi.getFormId());
		Map map = formViewManager.getDataMap(form.getDataTable().getName(), wi.getDataId());
		List<FormControl> fieldsList = this.formViewManager.getControls(wi.getFormId());
		FormControl field = getFieldbyName(fieldsList,aff.getName() );
		String value ;
		if(field.getDataType()==DataType.TIME){
			value = getFormatCurrentTime(aff,DataType.TIME);
		}else if(field.getDataType()==DataType.DATE){
			value = getFormatCurrentTime(aff,DataType.DATE);
		}else if(field.getDataType()==DataType.TEXT||field.getDataType()==DataType.CLOB){
			value = getValue(aff, currentOperation);
			if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDITIONAL)){//追加
				String originalValue = (map.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName())==null?"":map.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName()))+"";
				if(StringUtils.isNotEmpty(value)&&StringUtils.isNotEmpty(originalValue)){//如果配的值不为空且数据库中的值也不为空时，则其值为追加后的值
					value = originalValue+","+value;
				}else if(StringUtils.isEmpty(value)&&StringUtils.isNotEmpty(originalValue)){//如果配的值为空且数据库的值不为空时，则其值为数据库中的值不作修改
					value= originalValue;
				}
			}else if(aff.getFillType().equals(AutomaticallyFilledField.AUTO_FILLED_FILL_TYPE_ADDED_TO_THE_BEGINNING)){//添加
				String originalValue = (map.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName())==null?"":map.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+aff.getName()))+"";
				if(StringUtils.isNotEmpty(value)&&StringUtils.isNotEmpty(originalValue)){
					value = value + ","+originalValue;
				}else if(StringUtils.isEmpty(value)&&StringUtils.isNotEmpty(originalValue)){
					value= originalValue;
				}
			}
		}else{
			value = aff.getValue();
		}
		
		log.debug("*** getAutoFilledFieldValue 方法结束");
		return value;
	}
	
	/*
	 *如果字段类型是text 调用该方法来解析要填写的值 
	 * @param aff
	 * @param taskTransactor
	 * @param currentOperation
	 * @return
	 */
	public String getValue(AutomaticallyFilledField aff, String currentOperation){
		StringBuilder builder = new StringBuilder();
		String[] strs = null;
		String condition = aff.getValue();
		if(condition.indexOf('+')==-1){
			strs = new String[]{condition};
		}else{
			strs = condition.split("\\+");
		}
		for(int i=0;i<strs.length;i++){
			if(i!=0) builder.append(aff.getSeparate());
			if( CommonStrings.CURRENTTRANSACTOR.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前登录名时，当前登录名不能为null");throw new RuntimeException("自动填写当前登录名时，当前登录名不能为null");}
				builder.append(ContextUtils.getLoginName());
			}else if(CommonStrings.CURRENT_TRANSACTOR_NAME.equals(strs[i])){
				if(ContextUtils.getUserName()==null){log.debug("自动填写当前用户姓名时，当前用户姓名不能为null");throw new RuntimeException("自动填写当前用户姓名时，当前用户姓名不能为null");}
				builder.append(ContextUtils.getUserName());
			}else if(CommonStrings.CURRENTTIME.equals(strs[i])){
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				builder.append(simpleDateFormat.format(new Date()));
			}else if(CommonStrings.CURRENTOPERATION.equals(strs[i])){
				builder.append(currentOperation);
			}else if(CommonStrings.CURRENT_TRANSACTOR_DEPARTMENT.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人部门时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人部门时，当前登录名不能为null");}
				builder.append(getDepartementNames(ApiFactory.getAcsService().getDepartments(ContextUtils.getLoginName())));
			}else if(CommonStrings.CURRENT_TRANSACTOR_MAIN_DEPARTMENT.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人正职部门时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人正职部门时，当前登录名不能为null");}
				com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserByLoginName(ContextUtils.getLoginName());
				if(user!=null){
					if(user.getMainDepartmentId()==null){log.debug("自动填写当前办理人正职部门时，当前办理人的正职部门id不能为null");throw new RuntimeException("自动填写当前办理人正职部门时，当前办理人的正职部门id不能为nul");}
					com.norteksoft.product.api.entity.Department dept=ApiFactory.getAcsService().getDepartmentById(user.getMainDepartmentId());
					builder.append(dept.getName());
				}
			}else if(CommonStrings.CURRENT_TRANSACTOR_SUPERIOR_DEPARTMENT.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人的上级部门时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人直属上级所在部门时，当前登录名不能为null");}
				builder.append(getDepartementNames(ApiFactory.getAcsService().getParentDepartmentsByUser(ContextUtils.getLoginName())));
			}else if(CommonStrings.CURRENT_TRANSACTOR_UPSTAGE_DEPARTMENT.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人顶级部门时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人顶级部门时，当前登录名不能为null");}
				builder.append(getDepartementNames( ApiFactory.getAcsService().getTopDepartmentsByUser(ContextUtils.getLoginName())));
			}else if(CommonStrings.CURRENT_TRANSACTOR_ROLE.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人角色时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人角色时，当前登录名不能为null");}
				builder.append(getRoleNames(ApiFactory.getAcsService().getRolesByUser(ContextUtils.getLoginName())));
			}else if(CommonStrings.CURRENT_TRANSACTOR_WORKGROUP.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人工作组时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人工作组时，当前登录名不能为null");}
				builder.append(getWorkgroupNames(ApiFactory.getAcsService().getWorkgroupsByUser(ContextUtils.getLoginName())));
			}else if(CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_NAME.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人直属上级名称时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人直属上级名称时，当前登录名不能为null");}
				com.norteksoft.product.api.entity.User user=ApiFactory.getDataDictService().getDirectLeader(ContextUtils.getLoginName());
				if(user!=null)builder.append(user.getName());
			}else if(CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_LOGIN_NAME.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人直属上级登录名时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人直属上级登录名时，当前登录名不能为null");}
				com.norteksoft.product.api.entity.User user=ApiFactory.getDataDictService().getDirectLeader(ContextUtils.getLoginName());
				if(user!=null)builder.append(user.getLoginName());
			}else if(CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人直属上级部门时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人直属上级部门时，当前登录名不能为null");}
				com.norteksoft.product.api.entity.User user=ApiFactory.getDataDictService().getDirectLeader(ContextUtils.getLoginName());
				if(user!=null) builder.append(getDepartementNames(ApiFactory.getAcsService().getParentDepartmentsByUser(user.getLoginName())));
			}else if(CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_MAIN_DEPARTMENT.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人直属上级正职部门时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人直属上级正职部门时，当前登录名不能为null");}
				com.norteksoft.product.api.entity.User user=ApiFactory.getDataDictService().getDirectLeader(ContextUtils.getLoginName());
				if(user!=null){
					user=ApiFactory.getAcsService().getUserByLoginName(user.getLoginName());
					if(user!=null){
						if(user.getMainDepartmentId()==null){log.debug("自动填写当前办理人直属上级正职部门时，当前办理人直属上级的正职部门id不能为null");throw new RuntimeException("自动填写当前办理人直属上级正职部门时，当前办理人直属上级的正职部门id不能为null");}
						com.norteksoft.product.api.entity.Department dept=ApiFactory.getAcsService().getDepartmentById(user.getMainDepartmentId());
						builder.append(dept.getName());
					}
				}
			}else if(CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_ROLE.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人直属上级角色时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人直属上级角色时，当前登录名不能为null");}
				com.norteksoft.product.api.entity.User user=ApiFactory.getDataDictService().getDirectLeader(ContextUtils.getLoginName());
				if(user!=null){
					builder.append(getRoleNames(ApiFactory.getAcsService().getRolesByUser(user.getLoginName())));
				}
			}else if(CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP.equals(strs[i])){
				if(ContextUtils.getLoginName()==null){log.debug("自动填写当前办理人直属上级工作组时，当前登录名不能为null");throw new RuntimeException("自动填写当前办理人直属上级工作组时，当前登录名不能为null");}
				com.norteksoft.product.api.entity.User user=ApiFactory.getDataDictService().getDirectLeader(ContextUtils.getLoginName());
				if(user!=null){
					builder.append(getWorkgroupNames(ApiFactory.getAcsService().getWorkgroupsByUser(user.getLoginName())));
				}
			}else{
				builder.append(strs[i]);
			}
		}
		return builder.toString();
	}
	
	private String getDepartementNames(List<com.norteksoft.product.api.entity.Department> departments){
		StringBuilder sb=new StringBuilder();
		if(departments!=null){
			int i=0;
			for(com.norteksoft.product.api.entity.Department dept:departments){
				i++;
				sb.append(dept.getName());
				if(i<departments.size()){
					sb.append(",");
				}
			}
		}
		return sb.toString();
	}
	
	private String getRoleNames(Set<com.norteksoft.product.api.entity.Role> roles){
		StringBuilder sb=new StringBuilder();
		if(roles!=null){
			int i=0;
			for(com.norteksoft.product.api.entity.Role role:roles){
				i++;
				sb.append(role.getName());
				if(i<roles.size()){
					sb.append(",");
				}
			}
		}
		return sb.toString();
	}
	
	private String getWorkgroupNames(List<com.norteksoft.product.api.entity.Workgroup> workGroups){
		StringBuilder sb=new StringBuilder();
		if(workGroups!=null){
			int i=0;
			for(com.norteksoft.product.api.entity.Workgroup wg:workGroups){
				i++;
				sb.append(wg.getName());
				if(i<workGroups.size()){
					sb.append(",");
				}
			}
		}
		return sb.toString();
	}
	
	
	private String getFormatCurrentTime(AutomaticallyFilledField aff,DataType dataType){
		String format ;
		switch(dataType){
		  	case TIME: format = "yyyy-MM-dd HH:mm" ;break;
		  	case DATE: format = "yyyy-MM-dd";break;
		  	default: return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return StringUtils.contains(aff.getValue(), CommonStrings.CURRENTTIME) ? simpleDateFormat.format(new Date()) :"";
	}
	
	/*
	 * 从List中取出英文名为enName的Field
	 */
	private FormControl getFieldbyName(List<FormControl> fields , String enName){
		for(FormControl field:fields){
			if(field.getName().equals(enName)) return field;
		}
		return null;
	}
	
	/**
	 * 通过流程实例ID及方式
	 * @param page
	 * @param processInstanceId
	 * @return
	 */
	public List<WorkflowTask> getCountersignByProcessInstanceIdResult(String processInstanceId,TaskProcessingMode processingMode,String taskName,TaskProcessingResult result){
		return workflowTaskService.getCountersignByProcessInstanceIdResult(processInstanceId, taskName, result);
	}
	
	public List<String> getCountersignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return workflowTaskService.getCountersignByProcessInstanceId(processInstanceId, processingMode);
	}
	
	/**
	 * 自定义流程中取得会签名字列表
	 * @param processInstanceId
	 * @param processingMode
	 * @return
	 */
	public List<String> getSignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return workflowTaskService.getSignByProcessInstanceId(processInstanceId, processingMode);
	}

	/**
	 * 根据流程名字和实例id查询workflowTask
	 * @param instanceId
	 * @param taskName
	 * @return
	 */
	public List<WorkflowTask> getWorkflowTasks(String instanceId, String taskName) {
		return workflowTaskService.getWorkflowTasks(instanceId, taskName);
	}

	public Set<String> getHandledTransactors(WorkflowInstance instance){
		Assert.notNull(instance,"流程实例不能为null");
		return workflowTaskService.getHandledTransactors(instance.getProcessInstanceId());
	}
	
	public Set<String> getAllHandleTransactors(WorkflowInstance instance){
		return workflowTaskService.getAllHandleTransactors(instance.getProcessInstanceId());
	}
	
	/**
	 * 得到所有需要催办的task
	 */
	public List<WorkflowTask> getNeedReminderTasks(){
		return workflowTaskService.getNeedReminderTasks();
	}
	
	@Autowired
	public void setWorkflowRightsManager(
			WorkflowRightsManager workflowRightsManager) {
		this.workflowRightsManager = workflowRightsManager;
	}
	
	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	@Autowired
	public void setWorkflowClientManager(
			WorkflowClientManager workflowClientManager) {
		this.workflowClientManager = workflowClientManager;
	}
	
//	@Autowired
//	public void setGeneralDao(GeneralDao generalDao) {
//		this.generalDao = generalDao;
//	}

	@Autowired
	public void setWorkflowTaskService(WorkflowTaskService workflowTaskService) {
		this.workflowTaskService = workflowTaskService;
	}
	

	@Autowired
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}

	@Autowired
	public void setInstanceHistoryManager(
			InstanceHistoryManager instanceHistoryManager) {
		this.instanceHistoryManager = instanceHistoryManager;
	}
	
	@Autowired
	public void setDelegateManager(DelegateMainManager delegateManager) {
		this.delegateManager = delegateManager;
	}
	
	@Autowired
	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}
	
	@Autowired
	public void setFormViewManager(FormViewManager formManager) {
		this.formViewManager = formManager;
	}

	/**
	 * 设置新的办理人到工作流引擎中
	 * @param taskId 任务id
	 * @param newTransactor 新的办理人
	 */
	public void setNewTransactor(Long taskId, String newTransactor) {
		WorkflowTask task = this.getTask(taskId);
		processEngine.execute(new ExecutionVariableCommand(task.getExecutionId(), CommonStrings.NEW_TRANSACTOR, newTransactor));
	}
	/**
	 * 设置新的流向名到工作流引擎中
	 * @param taskId 任务id
	 * @param transitionName 流向名
	 */
	public void setTransitionName(Long taskId, String transitionName) {
		WorkflowTask task = this.getTask(taskId);
		processEngine.execute(new ExecutionVariableCommand(task.getExecutionId(), CommonStrings.TRANSITION_NAME, transitionName));
	}

	public List<WorkflowTask> getWorkflowTasksByDefinitonName(
			String definitionName, String loginName) {
		return workflowTaskService.getTasksOrderByWdfName(definitionName,loginName);
	}
	/**
	 * 完成选择具体办理人任务
	 * @param taskId
	 * @param newTransactor
	 */
	@Transactional(readOnly=false)
	public void completeChoiceTransactor(Long taskId, List<String> transactors) {
		for(String transactor:transactors){
			completeChoiceTransactor(taskId,transactor);
		}
	}

	/**
	 * 完成选择具体办理人任务
	 * @param taskId
	 * @param newTransactor
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType completeChoiceTransactor(Long taskId, String newTransactor) {
		if(StringUtils.isEmpty(newTransactor)) return CompleteTaskTipType.MESSAGE.setContent("请选择具体办理人！");
		WorkflowTask task = this.getTask(taskId);
    	ActivityExecution execution = (ActivityExecution) processEngine.getExecutionService().findExecutionById(task.getExecutionId());
    	org.jbpm.api.task.Task jbpmTask = processEngine.getTaskService().createTaskQuery()
		.processInstanceId(task.getProcessInstanceId())
		.activityName(execution.getActivityName()).uniqueResult();   
    	
    	((OpenExecution)processEngine.getExecutionService()
				.findExecutionById(jbpmTask.getExecutionId())).removeVariable(CommonStrings.TRANSACTOR_SINGLE_CANDIDATES);
    	
    	Integer groupNum=getTaskMaxGroupNum(task.getProcessInstanceId(), jbpmTask.getActivityName(),task.getCompanyId());
    	groupNum++;
		saveTask(createTask(task.getProcessInstanceId(), jbpmTask.getExecutionId(), newTransactor ,jbpmTask.getActivityName(),groupNum));
		if(jbpmTask.getAssignee().equals(CommonStrings.TRANSACTOR_ASSIGNMENT)||jbpmTask.getAssignee().equals(CommonStrings.TRANSACTOR_SINGLE)){
    		processEngine.getTaskService().assignTask(jbpmTask.getId(),newTransactor);
    	}
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId(),task.getCompanyId());
		if(instance.getProcessState()==ProcessState.UNSUBMIT){
			instance.setProcessState(ProcessState.SUBMIT);
			instance.setSubmitTime(new Timestamp(System.currentTimeMillis()));
			Map<String,String > reminderSetting = DefinitionXmlParse.getReminderSetting(instance.getProcessDefinitionId());
			instance.setReminderStyle(reminderSetting.get(DefinitionXmlParse.REMIND_STYLE));
			if(reminderSetting.get(DefinitionXmlParse.REMIND_REPEAT)!=null)instance.setRepeat(Long.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_REPEAT)));
			if(reminderSetting.get(DefinitionXmlParse.REMIND_DUEDATE)!=null)instance.setDuedate(Long.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_DUEDATE)));
			if(reminderSetting.get(DefinitionXmlParse.REMIND_TIME)!=null)instance.setReminderLimitTimes(Integer.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_TIME)));
			if(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_TYPE)!=null)instance.setReminderNoticeStyle(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_TYPE));
			if(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_USER_CONDITION)!=null)instance.setReminderNoticeUserCondition(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_USER_CONDITION));
		}
		workflowInstanceManager.saveWorkflowInstance(instance);
    	task.setActive(TaskState.COMPLETED.getIndex());// 任务完成状态
    	saveTask(task);
    	return CompleteTaskTipType.OK.setContent("办理人已指定");
	}
	
	
	/**
	 *  生成抄送任务。该任务不会影响任务流转，只是给用户看一下。
	 * @param taskId
	 * @param transactors
	 * @param title
	 * @param url
	 */
	
	@Transactional(readOnly=false)
	public void createCopyTaches(Long taskId, List<String> trans,String title,String url) {
		if(taskId==null){log.debug("生成抄送任务时，任务id不能为null");throw new RuntimeException("生成抄送任务时，任务id不能为null");}
		WorkflowTask currentTask=workflowTaskService.getTask(taskId);
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(currentTask.getProcessInstanceId());
		if(currentTask==null){log.debug("生成抄送任务时，任务不能为null");throw new RuntimeException("生成抄送任务时，任务不能为null");}
    	Set<String> transactors= new HashSet<String>();
    	if(trans!=null)transactors.addAll(trans);
		List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
    	for(String transactor:transactors){
    		WorkflowTask task = createTask(wi.getProcessDefinitionId(),currentTask.getProcessInstanceId(),currentTask.getExecutionId(),currentTask.getName(),transactor,false,currentTask.getGroupNum());
    		if(task!=null){
    			task.setName("(抄送)"+currentTask.getName());
    			task.setTitle("(抄送)"+currentTask.getTitle());
    			task.setProcessingMode(TaskProcessingMode.TYPE_READ);
    			task.setActive(TaskState.WAIT_TRANSACT.getIndex());
    			task.setRead(false);
    			task.setSendingMessage(true);
    			if(transactors.size()>0){
    				task.setMoreTransactor(true);
    			}else{
    				task.setMoreTransactor(false);
    			}
    			if(StringUtils.isNotEmpty(title))task.setTitle(title);
    			if(StringUtils.isNotEmpty(url))task.setUrl(ContextUtils.getSystemCode()+url);
    			
    			tasks.add(task);
    		}
    	}
    	this.saveTasks(tasks);
	}
	/**
	 * 发起子流程
	 * @param subprocessParse 封装子流程初始化变量
	 */
	public void startSubProcessWorkflow(Map<TaskTransactorCondition, String> transactor,SubProcessParse subprocessParse,Collection<String> transcators){
		ActivityExecution execution=(ActivityExecution)processEngine.getExecutionService().findExecutionById(subprocessParse.getExecutionId());
		WorkflowInstance parentWorkflow=workflowInstanceManager.getWorkflowInstance(subprocessParse.getParentInstanceId());
		FormView parentForm=formViewManager.getFormView(parentWorkflow.getFormId());
		boolean isMoreTransator = DefinitionXmlParse.hasMoreTransactor(subprocessParse.getParentDefinitionId(),subprocessParse.getActivityName());
		Set<String> subProcessCreator = new HashSet<String>();
		if(transactor.isEmpty() || StringUtils.isEmpty(transactor.get(TaskTransactorCondition.USER_CONDITION))){
			subProcessCreator.add(subprocessParse.getCreator());
		}else{
			Object originalUser=processEngine.getExecutionService().getVariable(execution.getId(),CommonStrings.IS_ORIGINAL_USER); 
			execution.removeVariable(CommonStrings.IS_ORIGINAL_USER);
			Object allOriginalUsers=processEngine.getExecutionService().getVariable(execution.getId(),CommonStrings.ALL_ORIGINAL_USERS);
			execution.removeVariable(CommonStrings.ALL_ORIGINAL_USERS);
			Set<String> candidates = null;
			if("true".equals(originalUser)){
				TaskService  taskService = (TaskService)ContextUtils.getBean("taskService");
				String subFirstTaskName=DefinitionXmlParse.getFirstTaskName(subprocessParse.getSubDefinitionId());
				List<WorkflowInstance> ins=workflowInstanceManager.getSubWorkflowInstances(subprocessParse.getParentInstanceId(),ContextUtils.getSystemId());
				List<WorkflowTask> tasks=new ArrayList<WorkflowTask>();
				for(WorkflowInstance wi:ins){
					String subFirstName=DefinitionXmlParse.getFirstTaskName(wi.getProcessDefinitionId());
					if(subFirstTaskName.equals(subFirstName)){
						tasks.addAll(taskService.getCompletedTasksByTaskName(subprocessParse.getParentInstanceId(), wi.getCompanyId(), subFirstTaskName));
					}
				}
				
				candidates = new HashSet<String>();
				if(allOriginalUsers!=null && !"".equals(allOriginalUsers)){
					String[] aous=allOriginalUsers.toString().split(",");
					for(String s:aous){
						for(WorkflowTask task:tasks){
							if(s.equals(task.getTransactor())){
								candidates.add(task.getTransactor());
								break;
							}
						}
					}
				}
				//当没有传入该环节上次办理人的登录名，则将所有已办理该环节的人加入候选人集合中
				if(allOriginalUsers==null ||(allOriginalUsers!=null && "".equals(allOriginalUsers))){
					for(WorkflowTask task:tasks){
						candidates.add(task.getTransactor());
					}
				}
			}
			if(originalUser==null || "false".equals(originalUser) || ("true".equals(originalUser)&&candidates.size()==0)){
				Map<String,String> paramMap = new HashMap<String,String>();
				paramMap.put(TransactorConditionHandler.DOCUMENT_CREATOR, subprocessParse.getCreator());
				paramMap.put(TransactorConditionHandler.PROCESS_INSTANCEID, subprocessParse.getParentInstanceId());
				Object previousTaskTransactorObj = ((OpenExecution) execution).getVariable(CommonStrings.PREVIOUS_TASK_TRANSACTOR);
				if(previousTaskTransactorObj!=null){
					paramMap.put(TransactorConditionHandler.PREVIOUS_TRANSACTOR, previousTaskTransactorObj.toString());
				}
				
				candidates = TransactorConditionHandler.processCondition(transactor, (OpenExecution) execution,paramMap);
			}
			if(transcators!=null)subProcessCreator.addAll(transcators);
			if(subProcessCreator.isEmpty()){//如果指定办理人为空时，继续指定子流程创建人
				if(candidates.isEmpty()){
					subProcessCreator.add(subprocessParse.getCreator());
				}else{
					subProcessCreator.addAll(candidates);
				}
			}
		}
		Map<String,Object> subNeedVariableMap = new HashMap<String,Object>();// mainVariableTosub(execution);
		subNeedVariableMap.put(CommonStrings.PRIORITY, subprocessParse.getPriority());//本环节任务名
		subNeedVariableMap.put(CommonStrings.PARENT_WORKFLOW_ID, subprocessParse.getParentInstanceId());
		subNeedVariableMap.put(CommonStrings.PARENT_EXECUTION_ID, execution.getId());
		subNeedVariableMap.put(CommonStrings.PARENT_TACHE_NAME, subprocessParse.getActivityName());
	if(DefinitionXmlParse.isSharedForm(subprocessParse.getParentDefinitionId(),subprocessParse.getSubDefinitionId())){
		subNeedVariableMap.put(CommonStrings.FORM_DATA_ID, parentWorkflow.getDataId());
		//共用表单
		log.info("主子流程公用表单");
		if(parentForm.isStandardForm()){
			log.info("父流程表单为标准表单");
			FormFlowable parentEntity = (FormFlowable)generalDao.get(parentWorkflow.getDataId());
			log.info("parentEntity:"+parentEntity);
			log.info("开始启动子流程...");
			String subprocessInstanceId="";
			if(isMoreTransator){
				//需要发起多个子流程
				Iterator< String> it=subProcessCreator.iterator();
				while(it.hasNext()){
					subNeedVariableMap.put(CommonStrings.CREATOR, it.next());
					subprocessInstanceId = workflowClientManager.startSubProcess(subprocessParse.getSubDefinitionId(), parentEntity,  subNeedVariableMap);
					WorkflowInstance subWorkflow = workflowInstanceManager.getWorkflowInstance(subprocessInstanceId);
					//设置表单查看url formUrl和应急处理url urgenUrl
					setInstanceUrl(subWorkflow);	
				}
			}else{
				if(subProcessCreator.size()==1){
					subNeedVariableMap.put(CommonStrings.CREATOR, subProcessCreator.iterator().next());
				}else{
					subNeedVariableMap.put(CommonStrings.CREATOR_CANDIDATES, subProcessCreator);
				}
				subprocessInstanceId = workflowClientManager.startSubProcess(subprocessParse.getSubDefinitionId(), parentEntity,  subNeedVariableMap);
				WorkflowInstance subWorkflow = workflowInstanceManager.getWorkflowInstance(subprocessInstanceId);
				//设置表单查看url formUrl和应急处理url urgenUrl
				setInstanceUrl(subWorkflow);	
			}
			log.info("子流程启动结束...");
		}else if(!parentForm.isStandardForm()){
			log.info("开始启动子流程...");
			if(isMoreTransator){
				//需要发起多个子流程
				for(String creator:subProcessCreator){
					subNeedVariableMap.put(CommonStrings.CREATOR, creator);
					WorkflowInstance subWorkflow = startWorkflow(subNeedVariableMap,subprocessParse);
					subWorkflow.setDataId(parentWorkflow.getDataId());
					//设置表单查看url formUrl和应急处理url urgenUrl
					setInstanceUrl(subWorkflow);
					workflowInstanceManager.saveWorkflowInstance(subWorkflow);
				}
			}else{
				if(subProcessCreator.size()==1){
					subNeedVariableMap.put(CommonStrings.CREATOR, subProcessCreator.iterator().next());
				}else{
					subNeedVariableMap.put(CommonStrings.CREATOR_CANDIDATES, subProcessCreator);
				}
				WorkflowInstance subWorkflow = startWorkflow(subNeedVariableMap,subprocessParse);
				subWorkflow.setDataId(parentWorkflow.getDataId());
				//设置表单查看url formUrl和应急处理url urgenUrl
				setInstanceUrl(subWorkflow);
				workflowInstanceManager.saveWorkflowInstance(subWorkflow);
			}
			log.info("子流程启动结束...");
		}
	}else{
		Map<String,Object> param = new HashMap<String,Object>();
		log.info("主子流程不同表单");
		WorkflowDefinition subDefinition =  workflowDefinitionManager.getWorkflowDefinitionByProcessId(subprocessParse.getSubDefinitionId());
		log.info("subDefinition"+subDefinition.toString());
		FormView subForm=formViewManager.getCurrentFormViewByCodeAndVersion(subDefinition.getFormCode(), subDefinition.getFromVersion());
		log.info("subForm:"+subForm);
		if(subForm.isStandardForm()){
			String beanName = DefinitionXmlParse.getSubProcessBeginning(subprocessParse.getParentDefinitionId(),subprocessParse.getActivityName());
			log.info("实现类的beanname:"+beanName);
			OnStartingSubProcess beginning = (OnStartingSubProcess)ContextUtils.getBean(beanName);
			Assert.notNull(beginning,"实现类不能为空");
			log.info("开始启动子流程...");
			String subprocessInstanceId="";
			
			if(isMoreTransator){
				//需要发起多个子流程
				Iterator< String> it=subProcessCreator.iterator();
				while(it.hasNext()){
					String creator=it.next();
					subNeedVariableMap.put(CommonStrings.CREATOR, creator);
					param.put(OnStartingSubProcess.PARENT_ENTITY_ID, parentWorkflow.getDataId());
					param.put(OnStartingSubProcess.SUB_DOCUMENT_CREATOR, creator);
					FormFlowable subFormEntity = beginning.getRequiredSubEntity(param);
					Assert.notNull(subFormEntity+"返回子流程实体不能为空");
					Assert.notNull(subFormEntity.getId(),"返回子流程实体的id不能为空");
					log.info("返回实体："+subFormEntity);
					fillSubEntity(subFormEntity,subprocessParse);
					subprocessInstanceId = workflowClientManager.startSubProcess(subprocessParse.getSubDefinitionId(), subFormEntity,  subNeedVariableMap);
					WorkflowInstance subWorkflow = workflowInstanceManager.getWorkflowInstance(subprocessInstanceId);
					//设置实体中表示流程实例状态字段的值
					Object entity = generalDao.getObject(subForm.getDataTable().getEntityName(), subWorkflow.getDataId());
	    			try {
						BeanUtils.setProperty(entity, "workflowInfo.processState", ProcessState.SUBMIT);
						generalDao.save(entity);
					} catch (IllegalAccessException e) {
						log.debug(e);
						throw new RuntimeException();
					} catch (InvocationTargetException e) {
						log.debug(e);
						throw new RuntimeException();
					}
					//设置表单查看url formUrl和应急处理url urgenUrl
					setInstanceUrl(subWorkflow);	
				}
			}else{
				if(subProcessCreator.size()==1){
					subNeedVariableMap.put(CommonStrings.CREATOR, subProcessCreator.iterator().next());
					param.put(OnStartingSubProcess.SUB_DOCUMENT_CREATOR, subprocessParse.getCreator());
				}else{
					subNeedVariableMap.put(CommonStrings.CREATOR_CANDIDATES, subProcessCreator);
				}
				param.put(OnStartingSubProcess.PARENT_ENTITY_ID, parentWorkflow.getDataId());
				FormFlowable subFormEntity = beginning.getRequiredSubEntity(param);
				Assert.notNull(subFormEntity+"返回子流程实体不能为空");
				Assert.notNull(subFormEntity.getId(),"返回子流程实体的id不能为空");
				log.info("返回实体："+subFormEntity);
				fillSubEntity(subFormEntity,subprocessParse);
				subprocessInstanceId = workflowClientManager.startSubProcess(subprocessParse.getSubDefinitionId(), subFormEntity,  subNeedVariableMap);
				WorkflowInstance subWorkflow = workflowInstanceManager.getWorkflowInstance(subprocessInstanceId);
				//设置实体中表示流程实例状态字段的值
				Object entity = generalDao.getObject(subForm.getDataTable().getEntityName(), subWorkflow.getDataId());
    			try {
					BeanUtils.setProperty(entity, "workflowInfo.processState", ProcessState.SUBMIT);
					generalDao.save(entity);
				} catch (IllegalAccessException e) {
					log.debug(e);
					throw new RuntimeException();
				} catch (InvocationTargetException e) {
					log.debug(e);
					throw new RuntimeException();
				}
				//设置表单查看url formUrl和应急处理url urgenUrl
				setInstanceUrl(subWorkflow);	
			}
			log.info("子流程启动结束...");
			
		}else if(!subForm.isStandardForm()){
			log.info("开始启动子流程...");
			if(isMoreTransator){
				//需要发起多个子流程
				for(String creator:subProcessCreator){
					subNeedVariableMap.put(CommonStrings.CREATOR, creator);
					WorkflowInstance subWorkflow = startWorkflow(subNeedVariableMap,subprocessParse);
					Long dataId = fillSubDefaultForm(subWorkflow,subprocessParse);
					subWorkflow.setDataId(dataId);
					//设置表单查看url formUrl和应急处理url urgenUrl
					setInstanceUrl(subWorkflow);	
					subNeedVariableMap.put(CommonStrings.FORM_DATA_ID, dataId);
					workflowInstanceManager.saveWorkflowInstance(subWorkflow);
				}
			}else{
				if(subProcessCreator.size()==1){
					subNeedVariableMap.put(CommonStrings.CREATOR, subProcessCreator.iterator().next());
				}else{
					subNeedVariableMap.put(CommonStrings.CREATOR_CANDIDATES, subProcessCreator);
				}
				WorkflowInstance subWorkflow = startWorkflow(subNeedVariableMap,subprocessParse);
				Long dataId = fillSubDefaultForm(subWorkflow,subprocessParse);
				subWorkflow.setDataId(dataId);
				//设置表单查看url formUrl和应急处理url urgenUrl
				setInstanceUrl(subWorkflow);
				subNeedVariableMap.put(CommonStrings.FORM_DATA_ID, dataId);
				workflowInstanceManager.saveWorkflowInstance(subWorkflow);
			}
			log.info("子流程启动结束...");
		}
	}
	log.info("主流程开始等待...");
	generateFirstTask(execution,subprocessParse);
	execution.removeVariable(CommonStrings.FORM_DATA_ID);
	//generateFlowHistory( execution, PROCESS_ENTER);
	}
	
	private void setInstanceUrl(WorkflowInstance workflow){
		String processId = workflow.getProcessDefinitionId();
		Map<String,String> parameterSetting=DefinitionXmlParse.getParameterSetting(workflow.getProcessDefinitionId());
		String formViewUrl = parameterSetting.get(DefinitionXmlParse.FORM_VIEW_URL);
		if(StringUtils.isEmpty(formViewUrl)){
			formViewUrl = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_VIEW_URL);
		}
		String parameterName = parameterSetting.get(DefinitionXmlParse.FORM_VIEW_URL_PARAMETER_NAME);
		if(StringUtils.isEmpty(parameterName)){
			parameterName = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_VIEW_PARAMTER_NAME);
		}
		if(StringUtils.isNotEmpty(formViewUrl)){
			String joinSign = StringUtils.contains(formViewUrl, "?") ? "&" : "?";
			String systemCode=ContextUtils.getSystemCode();
			WorkflowDefinition definition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
			if(definition!=null){
				com.norteksoft.product.api.entity.BusinessSystem system=ApiFactory.getAcsService().getSystemById(definition.getSystemId());
				if(system!=null)systemCode=system.getCode();
			}
			formViewUrl = systemCode+formViewUrl + joinSign + parameterName + "=";
		}
		workflow.setFormUrl(formViewUrl);
		String urgenUrl = parameterSetting.get(DefinitionXmlParse.URGEN_URL);
		if(StringUtils.isEmpty(urgenUrl)){
			urgenUrl = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_URGEN_URL);
		}
		String urgenParameterName = parameterSetting.get(DefinitionXmlParse.URGEN_URL_PARAMETER_NAME);
		if(StringUtils.isEmpty(urgenParameterName)){
			urgenParameterName = PropUtils.getProp(CommonStrings.WORKFLOW_PARAMETER_URL, CommonStrings.FORM_URGEN_PARAMTER_NAME);
		}
		if(StringUtils.isNotEmpty(urgenUrl)){
			String joinSign = StringUtils.contains(urgenUrl, "?") ? "&" : "?";
			String systemCode=ContextUtils.getSystemCode();
			WorkflowDefinition definition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
			if(definition!=null){
				com.norteksoft.product.api.entity.BusinessSystem system=ApiFactory.getAcsService().getSystemById(definition.getSystemId());
				if(system!=null)systemCode=system.getCode();
			}
			urgenUrl = systemCode+urgenUrl + joinSign + urgenParameterName + "=";
		}
		workflow.setEmergencyUrl(urgenUrl);
	}
	
	public void  generateFirstTask(ActivityExecution execution,SubProcessParse subprocessParse){
		WorkflowInstance parentWorkflow=workflowInstanceManager.getWorkflowInstance(subprocessParse.getParentInstanceId());
		WorkflowTask task = new WorkflowTask();
		task.setCompanyId(parentWorkflow.getCompanyId());
		task.setProcessInstanceId(subprocessParse.getParentInstanceId());
		task.setExecutionId(execution.getId());
		task.setName(subprocessParse.getActivityName());
		String code = DefinitionXmlParse.getTacheCode(parentWorkflow.getProcessDefinitionId(), task.getName());
		task.setCode(code);
		task.setTitle(subprocessParse.getActivityName());
		task.setActive(TaskState.WAIT_TRANSACT.getIndex());
		task.setVisible(false);
		saveTask(task);
		executionVariableCommand(new ExecutionVariableCommand(execution.getId(), CommonStrings.SUBPROCESS_TASK_ID, task.getId()));
	}
	
	@SuppressWarnings("unchecked")
	private void fillSubEntity(FormFlowable subFormEntity,SubProcessParse subprocessParse){
		try {
			WorkflowInstance parentWorkflow=workflowInstanceManager.getWorkflowInstance(subprocessParse.getParentInstanceId());
			FormView parentForm=formViewManager.getFormView(parentWorkflow.getFormId());
			log.info("subFormEntity:"+subFormEntity);
			Map<String,String> mainToSubMap = DefinitionXmlParse.getMainToSub(subprocessParse.getParentDefinitionId(), subprocessParse.getActivityName());
			log.info("mainToSubMap"+mainToSubMap);
			Map<String,Object> valueMap = new HashMap<String,Object>();
			if(parentForm.isStandardForm()){
				Object parentEntity = generalDao.getObject(parentForm.getDataTable().getEntityName(), parentWorkflow.getDataId());
				log.info("parentObject:"+ parentEntity);
				for(String mainFieldName:mainToSubMap.keySet()){
					Object mainFieldValue = BeanUtils.getProperty(parentEntity, mainFieldName);
					if(mainFieldValue!=null){
						log.info("mainFieldValue:"+mainFieldValue.toString());
						valueMap.put(mainToSubMap.get(mainFieldName),mainFieldValue);
					}
				}
			}else if(!parentForm.isStandardForm()){
				Map dataMap = formViewManager.getDataMap(parentForm.getDataTable().getName(), parentWorkflow.getDataId());
				log.info("datamap:" + dataMap);
				for(String mainFieldName:mainToSubMap.keySet()){
					Object mainFieldValue = dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+mainFieldName);
					if(mainFieldValue!=null){
						log.info("mainFieldValue:" + mainFieldValue);
						valueMap.put(mainToSubMap.get(mainFieldName),mainFieldValue);
					}
				}
			}
			log.info("ValueMap:"+ valueMap.toString());
			BeanUtils.populate(subFormEntity, valueMap);
			log.info("subFormEntity:" + subFormEntity.toString());
			generalDao.save(subFormEntity);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
		
	}
	
	private WorkflowInstance startWorkflow(Map<String ,Object> subNeedVariableMap,SubProcessParse subprocessParse){
		ProcessInstance pi = processEngine.getExecutionService()
				.startProcessInstanceById(subprocessParse.getSubDefinitionId(),subNeedVariableMap);
		String subInstanceId = pi.getId();
		WorkflowInstance subWorkflow = workflowInstanceManager.getWorkflowInstance(subInstanceId);
		return subWorkflow;
	}
	
	@SuppressWarnings("unchecked")
	private Long fillSubDefaultForm(WorkflowInstance subWorkflow,SubProcessParse subprocessParse){
		Map<String,String> mainToSubMap = DefinitionXmlParse.getMainToSub(subprocessParse.getParentDefinitionId(), subprocessParse.getActivityName());
		log.info("mainToSubMap:"+mainToSubMap);
		 Map<String,String[]> valueMap = new HashMap<String,String[]>();
		 valueMap.put(WorkflowInstanceManager.INSTANCE_ID, new String[]{subWorkflow.getProcessInstanceId()});
		 WorkflowDefinition subDefinition =  workflowDefinitionManager.getWorkflowDefinitionByProcessId(subprocessParse.getSubDefinitionId());
		 FormView subForm=formViewManager.getCurrentFormViewByCodeAndVersion(subDefinition.getFormCode(), subDefinition.getFromVersion());
		try {
			WorkflowInstance parentWorkflow=workflowInstanceManager.getWorkflowInstance(subprocessParse.getParentInstanceId());
			FormView parentForm=formViewManager.getFormView(parentWorkflow.getFormId());
			if(parentForm.isStandardForm()){
				Object parentEntity = generalDao.getObject(parentForm.getDataTable().getEntityName(), parentWorkflow.getDataId());
				log.info("parentEntity:"+parentEntity);
				for(String mainFieldName:mainToSubMap.keySet()){
					Object mainFieldValue = BeanUtils.getProperty(parentEntity, mainFieldName);
					if(mainFieldValue!=null){
						log.info("mainFieldValue"+mainFieldValue);
						valueMap.put(mainToSubMap.get(mainFieldName), new String[]{mainFieldValue.toString()});
					}
				}
			}else if(!parentForm.isStandardForm()){
				Map dataMap = formViewManager.getDataMap(parentForm.getDataTable().getName(), parentWorkflow.getDataId());
				log.info("dataMap"+dataMap);
				for(String mainFieldName:mainToSubMap.keySet()){
					Object mainFieldValue = dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+mainFieldName);
					if(mainFieldValue!=null){
						log.info("mainFieldValue"+mainFieldValue.toString());
						valueMap.put(mainToSubMap.get(mainFieldName),new String[]{mainFieldValue.toString()});
					}
				}
			}
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
		log.info("开始保存子流程的表单数据...");
		
		Long subDataId = formViewManager.saveFormContentToTable(valueMap,subForm.getId(),null);
		log.info("返回id为"+subDataId);
		return subDataId;
	}
	public List<WorkflowTask> getCompletedTasksByTaskName(String workflowId,
			Long companyId, String taskName) {
		return workflowTaskService.getCompletedTasksByTaskName(workflowId, companyId, taskName);
	}
	@Deprecated
	public List<WorkflowTask> getOverdueTasks(Long companyId) {
		return workflowTaskService.getOverdueTasks(companyId);
	}
	public List<WorkflowTask> getOverdueTasks() {
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不能为null");
		return workflowTaskService.getOverdueTasks(ContextUtils.getCompanyId());
	}
	@Deprecated
	public Map<String, Integer> getOverdueTasksNumByTransactor(Long companyId) {
		return workflowTaskService.getOverdueTasksNumByTransactor(companyId);
	}
	public Map<String, Integer> getOverdueTasksNumByTransactor() {
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不能为null");
		return workflowTaskService.getOverdueTasksNumByTransactor(ContextUtils.getCompanyId());
	}
	@Deprecated
	public Integer getTasksNumByTransactor(Long companyId, String loginName) {
		return workflowTaskService.getTasksNumByTransactor(companyId, loginName);
	}
	
	public Integer getTasksNumByTransactor(String loginName) {
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不能为null");
		return workflowTaskService.getTasksNumByTransactor(ContextUtils.getCompanyId(),loginName);
	}
	@Deprecated
	public List<WorkflowTask> getTotalOverdueTasks(Long companyId) {
		return workflowTaskService.getTotalOverdueTasks(companyId);
	}
	
	public List<WorkflowTask> getTotalOverdueTasks() {
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不能为null");
		return workflowTaskService.getTotalOverdueTasks(ContextUtils.getCompanyId());
	}
	@Deprecated
	public Map<String, Integer> getTotalOverdueTasksNumByTransactor(
			Long companyId) {
		return workflowTaskService.getTotalOverdueTasksNumByTransactor(companyId);
	}
	
	public Map<String, Integer> getTotalOverdueTasksNumByTransactor() {
		Assert.notNull(ContextUtils.getCompanyId(),"公司id不能为null");
		return workflowTaskService.getTotalOverdueTasksNumByTransactor(ContextUtils.getCompanyId());
	}
	
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByUser(Long companyId, String loginName, Page<WorkflowTask> page){
		workflowTaskService.getAllTasksByUser(companyId,loginName,page);
	}
	
	/**
	 * 查询用户所有未完成任务(不是分页)
	 * @param page
	 */
	public List<WorkflowTask> getAllTasksByUser(Long companyId, String loginName){
		return workflowTaskService.getAllTasksByUser(companyId,loginName);
	}
	
	public List<String> getTransactorsExceptTask(Long taskId){
		return workflowTaskService.getTransactorsExceptTask(taskId);
	}
	
	/**
	 * 获得“任务组编号”最大的任务
	 * @param workflowId
	 * @param taskName
	 * @return
	 */
	private Integer getTaskMaxGroupNum(String workflowId,String taskName,Long companyId){
		List<WorkflowTask> tasks=workflowTaskService.getTaskOrderByGroupNum(companyId, workflowId, taskName);
		if(tasks.size()>0){
			WorkflowTask task1=tasks.get(0);
			if(task1.getGroupNum()==null||task1.getGroupNum().equals(0)){
				return 0;
			}else{
				return task1.getGroupNum();
			}
		}
		return 0;
	}
	
	/**
	 * 流程实例所有的环节名称的集合
	 * @param instanceId
	 * @return
	 */
	public List<String> getTaskNames(String workflowId){
		WorkflowInstance workflowInstance=workflowInstanceManager.getWorkflowInstance(workflowId);
		List<String> names=new ArrayList<String>();
		ProcessInstance instance = processEngine.getExecutionService().findProcessInstanceById(workflowId);
		//有并发或流程已结束不能退回
		if(instance==null || ((ExecutionImpl)instance).getSubProcessInstance() != null) 
			return names;
		//获得环节属性中“办理人设置”不是“字段中指定”的环节名称
		List<String> unFieldNames=DefinitionXmlParse.getUnFieldTaskNames(workflowInstance.getProcessDefinitionId());
		names.addAll(unFieldNames);
		return names;
	}
	/**
	 * 环节跳转处理
	 * @param workflowId
	 * @param backTo
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType taskJump(String workflowId, String backTo,Long companyId){
		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(workflowId,companyId);
		OpenExecution execution=(OpenExecution)processEngine.getExecutionService().findProcessInstanceById(instance.getProcessInstanceId()).findActiveExecutionIn(instance.getCurrentActivity());
		Object compIdStr=execution.getVariable(CommonStrings.COMPANY_ID);
		if(compIdStr==null){
			processEngine.getExecutionService().setVariable(execution.getId(), CommonStrings.COMPANY_ID, companyId);
		}
		return taskJump(instance,backTo,null,null);
	}
	
	@Autowired
	private JdbcSupport jdbcDao;
	
	/**
	 * 环节跳转处理
	 * @param workflowId
	 * @param backTo
	 * @return
	 */
	@Transactional(readOnly=false)
	public CompleteTaskTipType taskJump(WorkflowInstance instance, String backTo,List<String> transactors,String type){
		CompleteTaskTipType result=null;
		
//		WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(workflowId);
		String workflowId=instance.getProcessInstanceId();
		
		boolean isSubprocess=getActivetySubProcess(workflowId);
		if(isSubprocess){
			return CompleteTaskTipType.MESSAGE.setContent("当前环节为【子流程】环节,环节跳转时不支持该功能");
		}
		// 环节跳转时生成任务
		if(transactors!=null && transactors.size()>0){//当是"上一环节办理人指定"时生成任务
			generateTask(instance,backTo,transactors);
		}else{//当不是上一环节办理人指定时
			//判断backTo环节是否需要指定办理人
			Map<TaskTransactorCondition, String> conditions = DefinitionXmlParse.getTaskTransactor(instance.getProcessDefinitionId(),backTo);
			String userCondition = conditions.get(TaskTransactorCondition.USER_CONDITION);
			if(PRE_TRANSACTOR_ASSIGN.equals(userCondition)){
				result = CompleteTaskTipType.RETURN_URL;
			}else if(conditions.get(TaskTransactorCondition.SELECT_ONE_FROM_MULTIPLE).equals("true")&&conditions.get(TaskTransactorCondition.SELECT_TYPE).equals(TaskTransactorCondition.SELECT_TYPE_CUSTOM)){
				if("volumeBack".equals(type)){
					result = CompleteTaskTipType.MESSAGE.setContent("跳转到的环节为【选择具体办理人】,批量环节跳转时不支持该功能");
				}else{
					OpenExecution execution=(OpenExecution)processEngine.getExecutionService().findProcessInstanceById(instance.getProcessInstanceId()).findActiveExecutionIn(instance.getCurrentActivity());
					FormView form = formViewManager.getFormView(instance.getFormId());
					UserParseCalculator upc = new UserParseCalculator();
					upc.setDataId(instance.getDataId());
					upc.setFormView(form);
					String creator = execution.getVariable(CommonStrings.CREATOR)==null?null:execution.getVariable(CommonStrings.CREATOR).toString();
					upc.setDocumentCreator(creator);
					Object obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_PRINCI_TRANSACTOR);
					if(obj==null){//上一环节办理人委托人为空，取办理人
						obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_TRANSACTOR);
					}
					if(obj!=null){
						upc.setPreviousTransactor(obj.toString());
					}
					result = CompleteTaskTipType.SINGLE_TRANSACTOR_CHOICE.setCanChoiceTransactor(upc.getUsers(userCondition,instance.getSystemId(),instance.getCompanyId()));
				}
			}
			if(result!=null)return result;
			
			List<WorkflowTask> needSaveTasks = new ArrayList<WorkflowTask>();
			List<WorkflowTask> tasks = workflowTaskService.getActivityTasks(workflowId, instance.getCompanyId());
			//取消所有当前的环节
			for(WorkflowTask task :tasks){
//				if(!task.isSpecialTask()) {
					task.setActive(TaskState.CANCELLED.getIndex());
					task.setEffective(false);      //设置任务失效
					needSaveTasks.add(task);
					taskJumpHistoryProcess(workflowId, task, backTo);
//				}
			}
			//将jbpm退回到backto环节
			processEngine.execute(new GetBackCommand(instance.getProcessInstanceId(), backTo));
			//判断backTo节点是否是子流程节点
			if(!DefinitionXmlParse.isSubProcessTask(instance.getProcessDefinitionId(), backTo)){
				generateTask(instance,backTo);
				FormView form = formViewManager.getFormView(instance.getFormId());
				//如果表单是实体表单，同步实体的当前环节
				if(form.isStandardForm()){
					try {
						
						
						StringBuilder sql = new StringBuilder("UPDATE ").append(form.getDataTable().getName())
							.append(" SET current_activity_name='").append(backTo).append("' ")
							.append("where id=").append(instance.getDataId());
						jdbcDao.updateTable(sql.toString());
						
					} catch (Exception e) {
						log.error("为bean设置属性异常:" + e.getMessage());
					} 
				}
				this.saveTasks(needSaveTasks);
			}
		}
		//设置实例的相关属性
		if(instance.getProcessState()==ProcessState.END){
			instance.setProcessState(ProcessState.SUBMIT);
			instance.setEndTime(null);
		}
		instance.setCurrentActivity(backTo);
		//跳转后，不能再退回
		instance.setPreviousActivity(null);
		instance.setPreviousActivityTitle(null);
		workflowInstanceManager.saveWorkflowInstance(instance);
		//业务补偿
		taskJumpSet(instance);
		
		return CompleteTaskTipType.OK;
	}
	
	/**
	 * 流程监控/环节跳转的的业务补偿
	 * @param instance
	 * @param form
	 */
	private void taskJumpSet(WorkflowInstance instance){
		Map<String,String> taskJumpSet = DefinitionXmlParse.getMonitorTaskJumpSet(instance.getProcessDefinitionId());
		String setType=taskJumpSet.get(DefinitionXmlParse.SET_TYPE);
		String taskJumpSetUrl=taskJumpSet.get(DefinitionXmlParse.TASK_JUMP_MONITOR);
		if(StringUtils.isNotEmpty(taskJumpSetUrl)){
			String systemCode=WebUtil.getSystemCodeByDef(instance.getProcessDefinitionId());
			if(setType.equals("http")){
				WebUtil.getHttpConnection(taskJumpSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode);
			}else if(setType.equals("RESTful")){
				WebUtil.restful(taskJumpSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode);
			}
		}
	}
	/**
	 * 环节跳转时生成任务
	 * 当是"上一环节办理人指定"时生成任务
	 * @param instance
	 * @param taskName
	 */
	@Transactional(readOnly=false)
	private void generateTask(WorkflowInstance instance,String taskName,List<String> transactors){
		List<WorkflowTask> needSaveTasks = new ArrayList<WorkflowTask>();
		List<WorkflowTask> tasks = workflowTaskService.getActivityTasks(instance.getProcessInstanceId(), instance.getCompanyId());
		//取消所有当前的环节
		for(WorkflowTask task :tasks){
			if(!task.isSpecialTask()) {
				task.setActive(TaskState.CANCELLED.getIndex());
				task.setEffective(false);      //设置任务失效
				needSaveTasks.add(task);
				taskJumpHistoryProcess(instance.getProcessInstanceId(), task, taskName);
			}
		}
		this.saveTasks(needSaveTasks);
		//将jbpm跳转到taskName环节
		processEngine.execute(new GetBackCommand(instance.getProcessInstanceId(), taskName));
		
		Execution exec=processEngine.getExecutionService().findProcessInstanceById(instance.getProcessInstanceId()).findActiveExecutionIn(taskName);
		Object assign=processEngine.getExecutionService().getVariable(exec.getId(), CommonStrings.TRANSACTOR_ASSIGNMENT);
		if(assign!=null && PRE_TRANSACTOR_ASSIGN.equals(assign.toString())){
			SubProcessParse subprocessParse=(SubProcessParse)processEngine.getExecutionService().getVariable(exec.getId(),CommonStrings.SUBPROCESS_PARSE);
			ActivityExecution execution=(ActivityExecution)processEngine.getExecutionService().findExecutionById(subprocessParse.getExecutionId());
			execution.removeVariable(CommonStrings.TRANSACTOR_ASSIGNMENT);
			execution.removeVariable(CommonStrings.SUBPROCESS_PARSE);
			Map<TaskTransactorCondition, String> transactor = DefinitionXmlParse.getTaskTransactor(subprocessParse.getParentDefinitionId(),subprocessParse.getActivityName());
			this.startSubProcessWorkflow(transactor, subprocessParse,transactors);
		}else{
			setTaskJumpTransactor(instance,taskName,transactors);
		}
	}
	/**
	 * 环节跳转时生成任务
	 * 当是"上一环节办理人指定"时生成任务
	 * @param instance
	 * @param taskName
	 */
	@Transactional(readOnly=false)
	private void setTaskJumpTransactor(WorkflowInstance instance,String taskName,List<String> transactors){
		List<String> trans=new ArrayList<String>();
		Set<String> transcatorsSet = new HashSet<String>();
		if(CommonStrings.ALL_USER.equals(transactors.get(0))){//当时所有人时
			List<com.norteksoft.product.api.entity.User> users=ApiFactory.getAcsService().getUsersByCompany(instance.getCompanyId());
			for(com.norteksoft.product.api.entity.User u:users){
				trans.add(u.getLoginName());
			}
			transcatorsSet.addAll(trans);
		}else{
			transcatorsSet.addAll(transactors);
		}
		org.jbpm.api.task.Task jbpmTask = processEngine.getTaskService().createTaskQuery()
		.processInstanceId(instance.getProcessInstanceId())
		.activityName(taskName).uniqueResult();
    	
    	Integer groupNum=getTaskMaxGroupNum(instance.getProcessInstanceId(),jbpmTask.getActivityName(),instance.getCompanyId());
    	groupNum++;
    	for(String transcator:transcatorsSet){
    		if(transcator.contains(":")){
    			String[] transacts=transcator.split(":");
    			WorkflowTask wfTask=createTask(instance.getProcessInstanceId(), jbpmTask.getExecutionId(), transacts[0] ,jbpmTask.getActivityName(),groupNum);
    			//审批任务有多个办理人时设置为待领取
				if(!wfTask.getMoreTransactor()&&transcatorsSet.size()>1){
					wfTask.setActive(TaskState.DRAW_WAIT.getIndex());
				}
    			wfTask.setRemark(transacts[1]);
    			saveTask(wfTask);
    		}else{
    			WorkflowTask wfTask = createTask(instance.getProcessInstanceId(), jbpmTask.getExecutionId(), transcator ,jbpmTask.getActivityName(),groupNum);
    			//审批任务有多个办理人时设置为待领取
				if(!wfTask.getMoreTransactor()&&transcatorsSet.size()>1){
					wfTask.setActive(TaskState.DRAW_WAIT.getIndex());
				}
    			saveTask(wfTask);
    		}
    		
        	((OpenExecution)processEngine.getExecutionService()
    				.findExecutionById(jbpmTask.getExecutionId())).removeVariable(CommonStrings.TRANSACTOR_SINGLE_CANDIDATES);
        	//"上一环节指定办理人"或"办理人设置"/"选择具体办理人"/“人工选择”
    		if(jbpmTask.getAssignee().equals(CommonStrings.TRANSACTOR_ASSIGNMENT)||jbpmTask.getAssignee().equals(CommonStrings.TRANSACTOR_SINGLE)){
        		processEngine.getTaskService().assignTask(jbpmTask.getId(),transcator);
        	}
    	}
	}
	/**
	 * 环节跳转时生成任务
	 * 当不是"上一环节办理人指定"时生成任务
	 * @param instance
	 * @param taskName
	 */
	@Transactional(readOnly=false)
	private void generateTask(WorkflowInstance instance,String taskName){
		org.jbpm.api.task.Task jbpmTask = processEngine.getTaskService().createTaskQuery()
		.processInstanceId(instance.getProcessInstanceId())
		.activityName(taskName).uniqueResult();
		// 组任务
		if(jbpmTask == null){
			jbpmTask = processEngine.getTaskService().createTaskQuery()
				.processInstanceId(instance.getProcessInstanceId())
				.candidate(ContextUtils.getLoginName())
				.activityName(taskName).uniqueResult();
		}
		String parentExecutionId = null;
    	Execution execution = processEngine.getExecutionService().findExecutionById(jbpmTask.getExecutionId());
    	Execution parentExecution = execution.getParent();
    	if(parentExecution != null){
    		parentExecutionId = parentExecution.getId();
    	}
		generateTask(instance,execution.getId(),parentExecutionId);
	}
	/**
	 * 流程监控/增加办理人
	 * @param instance
	 * @param taskName
	 */
	@Transactional(readOnly=false)
	public void addTransactor(String workflowId,List<String> transactors){
		WorkflowInstance wfi = workflowInstanceManager.getWorkflowInstance(workflowId);
		List<WorkflowTask> tasks = workflowTaskService.getActivitySignTasks(workflowId, wfi.getCompanyId());
		WorkflowTask task=null;
		if(tasks!=null&&tasks.size()>0)task=tasks.get(0);
		if(task==null)return;
		
		//生成任务
		generateTask(task.getId(), transactors,TaskSource.ADD_TRANSACTOR);
		
		List<WorkflowTask> currentTasks=workflowTaskService.getActivitySignTasks(task.getProcessInstanceId(), task.getCompanyId());
		//任务为单人办理，且任务有多个办理人时设置为待领取
		if(currentTasks!=null&&currentTasks.size()>1){
			List<WorkflowTask> result=new ArrayList<WorkflowTask>();
			boolean isMoreTransactor=DefinitionXmlParse.hasMoreTransactor(wfi.getProcessDefinitionId(), task.getName());
			if(!isMoreTransactor){
				for(WorkflowTask currentTask:currentTasks){
					//如果任务是会签式或投票式默认为多人办理模式,所以当任务不是会签式和投票式时才有必要设置任务状态为待领取
					if(TaskProcessingMode.TYPE_COUNTERSIGNATURE!=currentTask.getProcessingMode()&&TaskProcessingMode.TYPE_VOTE!=currentTask.getProcessingMode()){
						if(TaskState.WAIT_TRANSACT.getIndex().equals(currentTask.getActive())){
							currentTask.setActive(TaskState.DRAW_WAIT.getIndex());
							currentTask.setTitle(task.getTitle());//增加办理人时将任务的标题设为源任务的标题，因为流程监控中无法取字段的值
							result.add(currentTask);
						}
					}
				}
				saveTasks(result);
			}
		}
		
		//生成流传历史
		generateAddTransactorHistory(task,wfi,transactors);
	}
	/**
	 * 生成增加办理人流转历史
	 * @param instance
	 * @param taskName
	 */
	private void generateAddTransactorHistory(WorkflowTask task,
			WorkflowInstance wfi,List<String> transactors) {
		String historyMessage = new StringBuilder(dateFormat.format(new Date()))
		.append(COMMA).append(ContextUtils.getUserName()).append(PROCESS_ADDTRANSACTOR)
		.append(getTransactorName(transactors)).append(DELTA_START)
		.append(task.getName()).append(DELTA_END).append("\n").toString();
		
		InstanceHistory history = new InstanceHistory(task.getCompanyId(), wfi.getProcessInstanceId(), InstanceHistory.TYPE_TASK, historyMessage, task.getName());
		history.setEffective(false);
		history.setCreatedTime(new Date());
		history.setExecutionId(wfi.getProcessInstanceId());
		
        instanceHistoryManager.saveHistory(history);
		
	}

	
	/**
	 * 根据
	 * @param instance
	 * @param taskName
	 */
	private Object getTransactorName(List<String> transactors) {
		String result = "";
		for(String transactor:transactors){
			result+=acsUtils.getUserByLoginName(transactor).getName()+",";
		}
		return result.substring(0, result.length()-1);
	}

	/**
	 * 流程监控/减少办理人
	 * @param instance
	 * @param taskName
	 */
	@Transactional(readOnly=false)
	public void delTransactor(String workflowId,List<String> transactors){
		WorkflowInstance wfi = workflowInstanceManager.getWorkflowInstance(workflowId);
		List<WorkflowTask> tasks = workflowTaskService.getActivitySignTasks(workflowId, wfi.getCompanyId());
		WorkflowTask task=null;
		if(tasks!=null&&tasks.size()>0)task=tasks.get(0);
		if(task==null)return;
		deleteCountersignHandler(task.getId(), transactors);
		
		List<WorkflowTask> currentTasks=workflowTaskService.getActivitySignTasks(task.getProcessInstanceId(), task.getCompanyId());
		//任务为单人办理，且任务只有一个办理人时且任务的状态为“待领取”时，设置任务为”待办理“状态
		if(currentTasks!=null&&currentTasks.size()==1){
			
			boolean isMoreTransactor=DefinitionXmlParse.hasMoreTransactor(wfi.getProcessDefinitionId(), task.getName());
			if(!isMoreTransactor){
				WorkflowTask currentTask=currentTasks.get(0);
				if(TaskState.DRAW_WAIT.getIndex().equals(currentTask.getActive()))currentTask.setActive(TaskState.WAIT_TRANSACT.getIndex());
				saveTask(currentTask);
			}
		}
		//生成流转历史
		generateRemoveTransactorHistory(task,wfi,transactors);
	}
	
	private void generateRemoveTransactorHistory(WorkflowTask task,
			WorkflowInstance wfi, List<String> transactors) {
		String historyMessage = new StringBuilder(dateFormat.format(new Date()))
		.append(COMMA).append(ContextUtils.getUserName()).append(PROCESS_REMOVETRANSACTOR)
		.append(getTransactorName(transactors)).append(DELTA_START)
		.append(task.getName()).append(DELTA_END).append("\n").toString();
		
		InstanceHistory history = new InstanceHistory(task.getCompanyId(), wfi.getProcessInstanceId(), InstanceHistory.TYPE_TASK, historyMessage, task.getName());
		history.setEffective(false);
		history.setCreatedTime(new Date());
		history.setExecutionId(wfi.getProcessInstanceId());
		
        instanceHistoryManager.saveHistory(history);
		
	}

	public List<String[]> getActivityTaskTransactors(String workflowId){
		WorkflowInstance wfi = workflowInstanceManager.getWorkflowInstance(workflowId);
		if(wfi==null){log.debug("获得当前任务和办理时，流程实例不能为null");throw new RuntimeException("获得当前任务和办理时，流程实例不能为null");}
		return workflowTaskService.getActivityTaskTransactors(workflowId,wfi.getCompanyId());
	}
	
	public List<String> getActivityTaskPrincipals(String workflowId){
		WorkflowInstance wfi = workflowInstanceManager.getWorkflowInstance(workflowId);
		return workflowTaskService.getActivityTaskPrincipals(workflowId, wfi.getCompanyId());
	}
	public List<String[]> getActivityTaskPrincipalsDetail(String workflowId){
		WorkflowInstance wfi = workflowInstanceManager.getWorkflowInstance(workflowId);
		return workflowTaskService.getActivityTaskPrincipalsDetail(workflowId, wfi.getCompanyId());
	}
	/**
	 * 暂停任务
	 */
	@Transactional(readOnly=false)
	public void pauseTasks(String processInstanceId, Long companyId) {
		workflowTaskService.pauseTasks(processInstanceId,companyId);
	}
	
	/**
	 * 继续被暂停的任务
	 */
	@Transactional(readOnly=false)
	public void continueTasks(String processInstanceId, Long companyId) {
		workflowTaskService.continueTasks(processInstanceId,companyId);
	}
	/**
	 * 批量移除任务中根据办理人查询当前任务列表
	 * @param tasks
	 * @param transactorName
	 * @param typeId
	 * @param defCode
	 * @param wfdId
	 */
	
	public void getActivityTasksByTransactorName(Page<WorkflowTask> tasks,Long typeId, String defCode,Long wfdId){
		workflowTaskService.getActivityTasksByTransactorName(tasks,  typeId, defCode, wfdId);
	}
	@Transactional(readOnly=false)
	public Map<String, List<WorkflowTask>> deleteTasks(List<Long> taskIds){
		// 将所有需要删除的任务按实例分组
		Map<String, List<WorkflowTask>> instanceTasks = new HashMap<String, List<WorkflowTask>>();
		
		WorkflowTask task = null;
		for(Long id:taskIds){
			task = getWorkflowTask(id);
			if(instanceTasks.get(task.getProcessInstanceId())==null){
				instanceTasks.put(task.getProcessInstanceId(), new ArrayList<WorkflowTask>());
			}
			instanceTasks.get(task.getProcessInstanceId()).add(task);
		}
		Map<String, List<WorkflowTask>> result = new HashMap<String, List<WorkflowTask>>();
		// 判断是否可以删除任务
		for(Map.Entry<String, List<WorkflowTask>> en : instanceTasks.entrySet()){
			// 查询实例当前活动的任务
			List<WorkflowTask> tasks = getActivityTasks(en.getKey(), en.getValue().get(0).getCompanyId());
			if(tasks.size()==1){ // 只有一个活动任务不允许删除
				if(result.get("JUST_ONE") == null) result.put("JUST_ONE", new ArrayList<WorkflowTask>());
				result.get("JUST_ONE").add(en.getValue().get(0));
			}else if(tasks.size() == en.getValue().size()){ // 实例活动任务多于一个，但不允许删除所有活动的任务
				result.put(en.getKey(), en.getValue());
			}else{
				deleteTaskInList(en.getValue());
			}
		}
		return result;
	}
	private void deleteTaskInList(List<WorkflowTask> tasks){
		String instanceId=null;
		Long companyId=null;
		if(tasks.size()>0){
			instanceId=tasks.get(0).getProcessInstanceId();
			companyId=tasks.get(0).getCompanyId();
		}
		for(WorkflowTask task : tasks){
			workflowTaskService.deleteTask(task);
		}
		if(StringUtils.isNotEmpty(instanceId)&&companyId!=null){
			List<WorkflowTask> activeTasks = getActivityTasks(instanceId, companyId);
			if(activeTasks!=null&&activeTasks.size()==1){
				WorkflowTask task=activeTasks.get(0);
				if(task.getActive().equals(TaskState.DRAW_WAIT.getIndex())){//待领取改为待办理
					task.setActive(TaskState.WAIT_TRANSACT.getIndex());
					saveTask(task);
				}
			}
		}
	}
	/**
	 * 批量移除任务
	 * @param taskIds
	 * @return
	 */
	@Transactional(readOnly=false)
	public String deleteTasksBatch(List<Long> taskIds){
		int sucessNum=0;
		int failNum=0;
		for(Long id:taskIds){
			WorkflowTask task=getWorkflowTask(id);
			List<WorkflowTask> tasks=getActivityTasks(task.getProcessInstanceId(), task.getCompanyId());
			if(tasks!=null&&tasks.size()>1){
				ArrayList<Long> idList=new ArrayList<Long>();
				idList.add(id);
				sucessNum++;
				deleteWorkflowTask(idList);
				//设置流程状态
				tasks=getActivityTasks(task.getProcessInstanceId(), task.getCompanyId());
				if(tasks!=null&&tasks.size()==1){
					task=tasks.get(0);
					if(task.getActive()==4){//待领取改为待办理
						task.setActive(0);
						saveTask(task);
					}
				}
			}
		}
		failNum=taskIds.size()-sucessNum;
		StringBuilder sb=new StringBuilder();
		sb.append("成功移除")
		.append(sucessNum).append("个;")
		.append("移除失败").append(failNum).append("个");
		return sb.toString();
	}
	
	/**
	 * 根据委托查询任务计划
	 * @param instanceIds 实例id的集合
	 * @param taskName 任务名称
	 * @param recieveUser 委托的受托人登录名
	 * @param consignor 委托的委托人登录名
	 * @return
	 */
	public List<WorkflowTask> getTasksByInstance(List<String> instanceIds,String taskName,String recieveUser,String consignor,Long companyId){
		List<WorkflowTask> result = new ArrayList<WorkflowTask>();
		if(StringUtils.isNotEmpty(taskName)){
			String[] taskNames = taskName.split(",");
			for(String t : taskNames){
				result.addAll(workflowTaskService.getTasksByInstance(instanceIds, t,recieveUser,consignor,companyId));
			}
		}else{
			result.addAll(workflowTaskService.getTasksByInstance(instanceIds, null,recieveUser,consignor,companyId));
		}
		return result;
	}
	
	/**
	 * 取消委托或委托过期时取回委托的任务
	 */
	public void recieveDelegateTask(TrustRecord delegateMain){
		List<String> instanceIds=new ArrayList<String>();
		if(delegateMain.getStyle()==1){
			instanceIds=workflowInstanceManager.getInstanceIdByDelegate(delegateMain);
		}
		List<WorkflowTask> tasks=getTasksByInstance(instanceIds,delegateMain.getActivityName(),delegateMain.getTrustee(),delegateMain.getTrustor(),delegateMain.getCompanyId());
		WorkflowTask targetTask =null;
		List<WorkflowTask> cancelTasks = new ArrayList<WorkflowTask>();
		List<WorkflowTask> targetTasks=new ArrayList<WorkflowTask>();
		for(WorkflowTask task:tasks){
			//流程类型
			WorkflowType type=getType(task);
			
			//得到当前办理人集合
			List<String[] > currentTransactors=workflowTaskService.getActivityTaskTransactors(task.getProcessInstanceId(), ContextUtils.getCompanyId());
			if(!currentTransactors.contains(delegateMain.getTrustor())){
				targetTask = task.clone();
				targetTask.setId(null);
				targetTask.setTrustor(null);//将该任务的委托人设为空，即使该任务不再是委托任务
				targetTask.setTrustorName(null);//将该任务的委托人设为空，即使该任务不再是委托任务
				targetTask.setTransactor(delegateMain.getTrustor());
				User user= userManager.getUserByLoginName(delegateMain.getTrustor());
				if(user!=null){
					targetTask.setTransactorName(user.getName());
				}
				targetTask.setRead(false);
				targetTask.setVisible(true);
				
				//*******************消息提醒*************************
				sendMessage(targetTask,type);
				//******************发送邮件*************************
				WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
				sendMail(targetTask, instance.getProcessDefinitionId());
				targetTasks.add(targetTask);
			}
			task.setTaskProcessingResult(null);
	    	task.setActive(TaskState.CANCELLED.getIndex());
	    	cancelTasks.add(task);
		}
		saveTasks(targetTasks);
		saveTasks(cancelTasks);
	}
	private WorkflowType getType(WorkflowTask task){
		String processId= processEngine.getExecutionService().findProcessInstanceById(task.getProcessInstanceId()).getProcessDefinitionId();
		WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		//流程类型
		WorkflowType type=null;
		if(wfDef!=null){
			//流程类型
			type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
		}
		return type;
	}
	public List<Integer> getGroupNumByTaskName(String processInstanceId,String taskName){
		return workflowTaskService.getGroupNumByTaskName(processInstanceId, taskName);
	}
	//是否是分支汇聚任务，true表示是分支任务，false表示不是
	public boolean isForkTask(String workflowId){
		List<WorkflowTask> tasks = workflowTaskService.getActivityTasks(workflowId, ContextUtils.getCompanyId());
		for(WorkflowTask task:tasks){
			if(!workflowId.equals(task.getExecutionId())){//表示是分支汇聚任务
				return true;
			}
		}
		return false;
	}
	
	public String goBackTask(Long taskId){
		Assert.notNull("流程实例workflow不能为null");
		WorkflowTask task = workflowTaskService.getTask(taskId);
		Assert.notNull("当前任务task不能为null");
		if(!(TaskState.WAIT_TRANSACT.getIndex().equals(task.getActive())))return "当前任务不是待办理任务，不能退回";
		WorkflowInstance workflow = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		if(workflow.getProcessState()==ProcessState.END || workflow.getProcessState()==ProcessState.MANUAL_END|| workflow.getProcessState()==ProcessState.PAUSE){
    		if(workflow.getParentProcessId()==null){
    			return "流程已经结束或者被取消或已暂停，不能退回";
    		}else{
    			return "子流程已经结束或者被取消或已暂停，不能退回";
    		}
    	}
		if(!workflow.getProcessInstanceId().equals(task.getExecutionId())){
			return "当前环节为分支环节不能退回";
		}
		
		if(task.getMoreTransactor()){
    		List<WorkflowTask> activeTasks = workflowTaskService.getActivityTasksByNameWithout(workflow.getProcessInstanceId(), taskId, task.getName());
    		if(activeTasks.size()>0){//有多个人,将当前任务减签
    			deleteCountersignHandler(taskId,Arrays.asList(task.getTransactor()));
    			return "退回成功";
    		}
    	}
		String backTo =  workflow.getPreviousActivity();
		if(StringUtils.isEmpty(backTo))return "无法退回。可能是历史任务或是被退回、取回、跳转的任务。";
		WorkflowTask previousTask=workflowTaskService.getLastCompletedTaskByTaskName(task.getProcessInstanceId(), task.getCompanyId(), backTo);
		if(previousTask!=null){
			if(!workflow.getProcessInstanceId().equals(previousTask.getExecutionId())){
				return "上一环节为分支环节不能退回";
			}
			if(previousTask.getMoreTransactor()){
				return "上一环节是多人办理不能退回";
			}else{
				//退回成功
				List<WorkflowTask> needSaveTasks = new ArrayList<WorkflowTask>();
				//取消当前环节任务
				task.setActive(TaskState.CANCELLED.getIndex());
				task.setEffective(false);      //设置任务失效
				needSaveTasks.add(task);
				gobackTaskHistoryProcess(workflow.getProcessInstanceId(), task, backTo);
				
				//将jbpm退回到backto环节
				processEngine.execute(new GetBackCommand(workflow.getProcessInstanceId(), backTo));
				//如果退回到的环节办理人为"上一环节办理人指定"或“条件筛选/选择具体办理人/人工选择”，则设置jbpmTask的办理人为previousTask的办理人
				ActivityExecution execution = (ActivityExecution) processEngine.getExecutionService().findExecutionById(task.getExecutionId());
				org.jbpm.api.task.Task jbpmTask = processEngine.getTaskService().createTaskQuery()
				.processInstanceId(task.getProcessInstanceId())
				.activityName(execution.getActivityName()).uniqueResult();   
				if(jbpmTask!=null && (CommonStrings.TRANSACTOR_ASSIGNMENT.equals(jbpmTask.getAssignee())||CommonStrings.TRANSACTOR_SINGLE.equals(jbpmTask.getAssignee()))){
	        		processEngine.getTaskService().assignTask(jbpmTask.getId(),previousTask.getTransactor());
	        	}
				//判断backTo节点是否是子流程节点
				if(!DefinitionXmlParse.isSubProcessTask(workflow.getProcessDefinitionId(), backTo)){
					FormView form = formViewManager.getFormView(workflow.getFormId());
					//如果表单是实体表单，同步实体的当前环节
					if(form.isStandardForm()){
						try {
							StringBuilder sql = new StringBuilder("UPDATE ").append(form.getDataTable().getName())
							.append(" SET current_activity_name='").append(backTo).append("' ")
							.append("where id=").append(workflow.getDataId());
							jdbcDao.updateTable(sql.toString());
							
						} catch (Exception e) {
							log.error("为bean设置属性异常:" + e.getMessage());
						} 
					}
					this.saveTasks(needSaveTasks);
				}
				//设置实例的相关属性
				if(workflow.getProcessState()==ProcessState.END){
					workflow.setProcessState(ProcessState.SUBMIT);
					workflow.setEndTime(null);
				}
				workflow.setCurrentActivity(backTo);
				//退回生成的任务，不能再退回
				workflow.setPreviousActivity(null);
				workflow.setPreviousActivityTitle(null);
				
				//生成退回的任务
				WorkflowTask backToTask=previousTask.clone();
				backToTask.setId(null);
				backToTask.setActive(TaskState.WAIT_TRANSACT.getIndex());
				backToTask.setRead(false);
				backToTask.setCreatedTime(new Date());
				backToTask.setTransactDate(null);
				saveTask(backToTask);
				
				workflow.setInstanceTitle(backToTask.getTitle());
				workflowInstanceManager.saveWorkflowInstance(workflow);
				
				//退回时回调
				String beanName=DefinitionXmlParse.getGobackTaskBean(workflow.getProcessDefinitionId());
				if(StringUtils.isNotEmpty(beanName)){
					ReturnTaskInterface gobackTaskInterface = (ReturnTaskInterface)ContextUtils.getBean(beanName);
					gobackTaskInterface.goback(workflow, task, backToTask);
				}
				return "退回成功";
			
		}
	  }else{
			return "可能是第一环节或取回的任务,不能退回";
	  }
	}
	
	/**
	 * 判断当前环节是否是子流程
	 * @param processInstanceId
	 * @return
	 */
	
	public boolean getActivetySubProcess(String processInstanceId) {
		List<Object> subProcess = workflowInstanceManager.getActivetySubProcess(processInstanceId);
		if(subProcess.size()>0){
			return true;
		}
		return false;
	}
}
