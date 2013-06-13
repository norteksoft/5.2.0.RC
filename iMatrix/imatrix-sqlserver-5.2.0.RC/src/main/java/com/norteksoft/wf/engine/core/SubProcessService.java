package com.norteksoft.wf.engine.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.activity.ExternalActivityBehaviour;
import org.jbpm.internal.log.Log;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.TaskTransactorCondition;
import com.norteksoft.wf.engine.client.AfterSubProcessEnd;
import com.norteksoft.wf.engine.client.BeforeStartSubProcess;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.InstanceHistoryManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
/**
 * 该类处理子流程节点的相关业务
 * 初始化变量
 * 获得子流程环节开始前的事件处理类，如果该类不空并且方法isIntoSubProcess(Long id)返回了false,将不发起子流程，直接跳过子流程。否则，发起子流程。
 * 如果需要发起子流程，先判断是否共用表单
 * 如果共用表单，判断表单类型，根据不同表单流程进行启动流程
 * 如果不公用表单，判断父子表单类型，然后赋值，启动子流程
 * 在启动子流程时，还需要分析子流程环节的办理人设置
 * 如果子流程环节设置的是多人办理，那么将给根据条件选出的每一个人发起一个子流程实例。
 * 如果子流程环节设置的是单人办理，将只发起一个流程实例
 * 如果子流程环节没有选择办理人，子流程实例的文档创建人将用父流程的文档创建人。
 * @author wurong
 */
@Transactional
public class SubProcessService implements ExternalActivityBehaviour{

	private static final long serialVersionUID = 1L;
	
	private static final Log log = Log.getLog(SubProcessService.class.getName());
	
	private SubProcessParse subprocessParse;
	
	private WorkflowInstance parentWorkflow;
	
	//-------
	private static final String PROCESS_ENTER = "流程进入";
	private static final String PROCESS_LEAVE = "流程离开";
	
	//流转历史常量
	private static final String COMMA = ", ";
	private static final String DELTA_START = "[ ";
	private static final String DELTA_END = " ]";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private void init(ActivityExecution execution){
		 WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
		 ProcessEngine processEngine = (ProcessEngine)ContextUtils.getBean("processEngine");
		log.info("开始变量初始化...");
		subprocessParse=new SubProcessParse();
		subprocessParse.setParentInstanceId(execution.getProcessInstance().getId());
		log.info("parentInstanceId:" + subprocessParse.getParentInstanceId());
		parentWorkflow=workflowInstanceManager.getWorkflowInstance(subprocessParse.getParentInstanceId());
		ActivityExecution activityExecution = (ActivityExecution)execution;
		subprocessParse.setActivityName(activityExecution.getActivityName());
		log.info("当前环节名字：" + subprocessParse.getActivityName());
		
		subprocessParse.setCreator(execution.getVariable("creator").toString());
		log.info("creator:"+subprocessParse.getCreator());
		subprocessParse.setParentExecutionId(execution.getId());
		log.info("parentExecutionId:" + subprocessParse.getParentExecutionId());
		subprocessParse.setParentDefinitionId(parentWorkflow.getProcessDefinitionId());
		subprocessParse.setSubDefinitionId(DefinitionXmlParse.getSubDefinitionId(parentWorkflow.getProcessDefinitionId(), subprocessParse.getActivityName()));
		log.info("subDefinitionId");
		
		Object priorityObject = processEngine.getExecutionService().getVariable(execution.getId(), CommonStrings.PRIORITY);
		if(priorityObject != null){
			subprocessParse.setPriority(Integer.valueOf(priorityObject.toString()));
		}
		subprocessParse.setExecutionId(execution.getId());
	}
	
	public void execute(ActivityExecution execution) throws Exception {
		 TaskService taskService = (TaskService) ContextUtils.getBean("taskService");
		 ProcessEngine processEngine = (ProcessEngine)ContextUtils.getBean("processEngine");
		log.info("子流程开始执行...");
		init(execution);
		String beforeStartSubProcessName = DefinitionXmlParse.getBeforeStartSubProcess(subprocessParse.getParentDefinitionId(),subprocessParse.getActivityName());
		log.info("实现类的beforeStartSubProcessName:"+beforeStartSubProcessName);
		BeforeStartSubProcess beforeStartSubProcess = null;
		if(StringUtils.isNotEmpty(beforeStartSubProcessName)){
			beforeStartSubProcess = (BeforeStartSubProcess)ContextUtils.getBean(beforeStartSubProcessName);
			log.debug("beforeStartSubProcess:"+beforeStartSubProcess);
		}
		if(beforeStartSubProcess==null || beforeStartSubProcess.isIntoSubProcess(parentWorkflow.getDataId())){
			Object originalUser=processEngine.getExecutionService().getVariable(execution.getId(),CommonStrings.IS_ORIGINAL_USER); 
			Map<TaskTransactorCondition, String> transactor = DefinitionXmlParse.getTaskTransactor(subprocessParse.getParentDefinitionId(),this.subprocessParse.getActivityName());
			String userCondition = transactor.get(TaskTransactorCondition.USER_CONDITION);
			if("${previousTransactorAssignment}".equals(userCondition)){
				if("true".equals(originalUser)){//是否使用原办理人
					taskService.startSubProcessWorkflow(transactor,subprocessParse,null);
				}else{
					taskService.executionVariableCommand(new ExecutionVariableCommand(execution.getId(), CommonStrings.TRANSACTOR_ASSIGNMENT, "${previousTransactorAssignment}"));
					taskService.executionVariableCommand(new ExecutionVariableCommand(execution.getId(), CommonStrings.SUBPROCESS_PARSE, subprocessParse));
				}
			}else{
				taskService.startSubProcessWorkflow(transactor,subprocessParse,null);
			}
			
		execution.waitForSignal();
		}
	}
	
	
	public void signal(ActivityExecution execution, String signalName, Map<String, ?> parameters) throws Exception {
		 TaskService taskService = (TaskService) ContextUtils.getBean("taskService");
		init(execution);
		log.info("父流程得到继续执行的信号。");
		String subProcessEndName = DefinitionXmlParse.getSubProcessEnd(subprocessParse.getParentDefinitionId(),subprocessParse.getActivityName());
		log.info("实现类的subProcessEndName:"+subProcessEndName);
		if(StringUtils.isNotEmpty(subProcessEndName)){
			AfterSubProcessEnd afterSubProcessEnd = (AfterSubProcessEnd)ContextUtils.getBean(subProcessEndName);
			log.info("subProcessEnd:"+afterSubProcessEnd);
			if(afterSubProcessEnd!=null) afterSubProcessEnd.execute(parentWorkflow.getDataId());
		}
		Long taskId = (Long)execution.getVariable(CommonStrings.SUBPROCESS_TASK_ID);
		WorkflowTask task = taskService.getTask(taskId);
		task.setActive(TaskState.COMPLETED.getIndex());
		taskService.saveTask(task);
		generateFlowHistory( execution, PROCESS_LEAVE);
		execution.take(signalName);
		execution.setVariable(CommonStrings.NEED_GENERATE_TASK, true);
		execution.setVariable(CommonStrings.PARENT_INSTANCE_ID, subprocessParse.getParentInstanceId());
	}
	
	private void generateFlowHistory(ActivityExecution execution, String state){
		 InstanceHistoryManager instanceHistoryManager = (InstanceHistoryManager)ContextUtils.getBean("instanceHistoryManager");
		InstanceHistory ih = new InstanceHistory();
		ih.setCompanyId(parentWorkflow.getCompanyId());
		if(PROCESS_ENTER.equals(state)){
			ih.setType(InstanceHistory.TYPE_FLOW_INTO);
		}else if(PROCESS_LEAVE.equals(state)){
			ih.setType(InstanceHistory.TYPE_TASK);
		}
		ih.setInstanceId(subprocessParse.getParentInstanceId());
		ih.setExecutionId(execution.getId());
		ih.setCreatedTime(new Date());
		ih.setTransactionResult(new StringBuilder(dateFormat.format(new Date()))
			.append(COMMA).append(state).append(DELTA_START)
			.append(subprocessParse.getActivityName()).append(DELTA_END).toString());
		ih.setTaskName(subprocessParse.getActivityName());
		instanceHistoryManager.saveHistory(ih);
	}
	
	
}