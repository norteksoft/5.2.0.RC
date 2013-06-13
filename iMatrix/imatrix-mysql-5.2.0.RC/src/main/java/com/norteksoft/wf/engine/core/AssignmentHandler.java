package com.norteksoft.wf.engine.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.model.OpenExecution;
import org.jbpm.api.task.Assignable;
import org.jbpm.internal.log.Log;

import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.TaskTransactorCondition;
import com.norteksoft.wf.base.exception.WorkflowException;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

/**
 * 办理人指定处理类
 * @author Administrator
 *
 */
public class AssignmentHandler implements org.jbpm.api.task.AssignmentHandler{

	private static final long serialVersionUID = 1L;
	private String newTransactor;
	private static final Log log = Log.getLog(AssignmentHandler.class.getName());
	
	public void assign(Assignable assignable, OpenExecution execution)
			throws Exception {
		Long companyId=getCompanyId();
		if(companyId==null){
			Object compIdStr=execution.getVariable(CommonStrings.COMPANY_ID);
			if(compIdStr!=null){
				companyId=Long.parseLong(compIdStr.toString()); 
			}
		}
		getVariables(execution);
		if(StringUtils.isNotEmpty(newTransactor)){
			String[] transactors=newTransactor.split(",");
			Set<String> ts=new HashSet<String>();
			for(int i=0;i<transactors.length;i++){
				ts.add(transactors[i]);
			}
			if(ts.size()==1){
				assignable.setAssignee(ts.iterator().next());
			}else{
				for(String t : ts){
					assignable.addCandidateUser(t);
				}
				
			}
		}else{
			String activityName=((ActivityExecution)execution).getActivityName();
			log.info("开始指定办理人，环节名："+activityName);
			String processId = ((ActivityExecution)execution).getProcessDefinitionId();
			String processInstanceId = ((ActivityExecution)execution).getProcessInstance().getId();
			//设置流程实例中当前环节名称字段值
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(processInstanceId,companyId);
			if(instance==null){throw new RuntimeException("设置办理人监听中，流程实例不能为null");}
			instance.setCurrentActivity(activityName);
			workflowInstanceManager.saveWorkflowInstance(instance);
			
			Set<String> candidates = null;
			Object originalUser=execution.getVariable(CommonStrings.IS_ORIGINAL_USER); 
			execution.removeVariable(CommonStrings.IS_ORIGINAL_USER);
			Object allOriginalUsers=execution.getVariable(CommonStrings.ALL_ORIGINAL_USERS); 
			execution.removeVariable(CommonStrings.ALL_ORIGINAL_USERS);
			if("true".equals(originalUser)){
				TaskService  taskService = (TaskService)ContextUtils.getBean("taskService");
				if(ContextUtils.getCompanyId()==null){throw new RuntimeException("设置办理人监听中，公司id不能为null");}
				List<WorkflowTask> list=taskService.getCompletedTasksByTaskName(processInstanceId, ContextUtils.getCompanyId(), activityName);
				candidates = new HashSet<String>();
				if(allOriginalUsers!=null && !"".equals(allOriginalUsers)){
					String[] aous=allOriginalUsers.toString().split(",");
					for(String s:aous){
						for(WorkflowTask task:list){
							if(s.equals(task.getTransactor())){
								candidates.add(task.getTransactor());
								break;
							}
						}
					}
				}
				//当没有传入该环节上次办理人的登录名，则将所有已办理该环节的人加入候选人集合中
				if(allOriginalUsers==null ||(allOriginalUsers!=null && "".equals(allOriginalUsers))){
					for(WorkflowTask task:list){
						candidates.add(task.getTransactor());
					}
				}
			}
			if(originalUser==null || "false".equals(originalUser) || ("true".equals(originalUser)&&candidates.size()==0)){
				String creator = execution.getVariable(CommonStrings.CREATOR)==null?null:execution.getVariable(CommonStrings.CREATOR).toString();
				
				//JPDL定义扩展参数
				Map<TaskTransactorCondition, String> conditions = 
					DefinitionXmlParse.getTaskTransactor(processId, activityName);
				
				
				//根据条件选定办理人
				log.info("办理人设置条件为:"+conditions);
				Map<String,String> paramMap = new HashMap<String,String>();
				paramMap.put(TransactorConditionHandler.DOCUMENT_CREATOR, creator);
				paramMap.put(TransactorConditionHandler.PROCESS_INSTANCEID, processInstanceId);
				Object obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_PRINCI_TRANSACTOR);
				if(obj==null){//上一环节办理人委托人为空，取办理人
					obj = execution.getVariable(CommonStrings.PREVIOUS_TASK_TRANSACTOR);
				}
				if(obj!=null){
					paramMap.put(TransactorConditionHandler.PREVIOUS_TRANSACTOR, obj.toString());
				}
				
				candidates = TransactorConditionHandler.processCondition(conditions, execution,paramMap);
			}
			log.info("选定的办理人为：candidates:" +candidates);
			
			if(candidates.size() == 1){
				assignable.setAssignee(candidates.iterator().next());
			}else if(candidates.size() == 0){
				throw new WorkflowException(WorkflowException.NO_TRANSACTOR);
			}else{
				for(String candidate : candidates){
					assignable.addCandidateUser(candidate);
				}
			}
		
			
		}
		//子流程返回时，需要通知父流程的下一环节来生成任务
		Object needGenerateTask = execution.getVariable(CommonStrings.NEED_GENERATE_TASK);
		Object parentInstanceId = execution.getVariable(CommonStrings.PARENT_INSTANCE_ID);
		if(needGenerateTask!=null&&Boolean.valueOf(needGenerateTask.toString())){
			TaskService taskService = (TaskService)ContextUtils.getBean("taskService");
			ActivityExecution activityExecution = (ActivityExecution)execution;
			String activityName = activityExecution.getActivityName();
			
			String processInstanceId = ((ActivityExecution)execution).getProcessInstance().getId();
			
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			GeneralDao generalDao = (GeneralDao)ContextUtils.getBean("generalDao");
			FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
			WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(processInstanceId,companyId);
			instance.setCurrentActivity(activityName);
			workflowInstanceManager.saveWorkflowInstance(instance);
			FormView  form = formManager.getFormView(instance.getFormId());
			if(form==null){throw new RuntimeException("设置办理人监听中，表单不能为null");}
			if(form.isStandardForm()){
				if(form.getDataTable()==null){throw new RuntimeException("设置办理人监听中，表单对应的数据表不能为null");}
				String className = form.getDataTable().getEntityName();
				log.info("实体类名：" + className);
				//根据表名和id获得实体
				try {
					if(className==null){throw new RuntimeException("设置办理人监听中，表单对应的数据表的实体类名不能为null");}
					Object entity = generalDao.getObject(className, instance.getDataId());
					BeanUtils.setProperty(entity, "workflowInfo.currentActivityName", activityName);
					generalDao.save(entity);
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
			}
			taskService.generateTask(instance, execution.getId(), null);
		}
	
		if(execution!=null&&parentInstanceId!=null&&parentInstanceId.toString().equals(execution.getId())){
			 execution.removeVariable(CommonStrings.NEED_GENERATE_TASK);
			 execution.removeVariable(CommonStrings.PARENT_INSTANCE_ID);
		}
		
		
	}
	
	private void getVariables(OpenExecution execution){
		Object obj = execution.getVariable(CommonStrings.NEW_TRANSACTOR);
		if(obj!=null){
			newTransactor = obj.toString();
			execution.removeVariable(CommonStrings.NEW_TRANSACTOR);
		}else{
			newTransactor = null;
		}
	}

	private static Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
}