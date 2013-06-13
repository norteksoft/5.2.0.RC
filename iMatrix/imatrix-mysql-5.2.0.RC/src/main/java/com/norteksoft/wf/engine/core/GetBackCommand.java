package com.norteksoft.wf.engine.core;

import java.util.List;

import org.jbpm.api.Execution;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.cmd.Command;
import org.jbpm.api.cmd.Environment;
import org.jbpm.api.model.Activity;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.model.TransitionImpl;

public class GetBackCommand implements Command<Activity> {

	private static final long serialVersionUID = 1L;
	private static final String DYNAMIC_TRANSITION_NAME = "dynamic_transition_name";
	private String executionId; //待取回任务的execution
	private String preTaskName; //待取回任务的任务名
	
	public GetBackCommand(String executionId, String preTaskName) {
		this.executionId = executionId;
		this.preTaskName = preTaskName;
	}

	public Activity execute(Environment environment) throws Exception {
		ProcessEngine engine = environment.get(ProcessEngine.class);
		
		ExecutionImpl execution = (ExecutionImpl) engine.getExecutionService().findExecutionById(executionId);
		ProcessDefinitionImpl definition = (ProcessDefinitionImpl) engine.getRepositoryService().createProcessDefinitionQuery()
				.processDefinitionId(execution.getProcessDefinitionId()).uniqueResult();
		
		//需要取回的任务
		ActivityImpl activity = definition.getActivity(preTaskName);
		
		if(Execution.STATE_INACTIVE_CONCURRENT_ROOT.equals(execution.getState())){
			//不支持并发取回
			return null;
		}else{
			//直流
			ActivityImpl fromActivity = execution.getActivity();
			TransitionImpl transition = fromActivity.createOutgoingTransition();
			transition.setDestination(activity);
			transition.setName(DYNAMIC_TRANSITION_NAME);
			
			//添加transition
			//添加transition
			List<TransitionImpl> transitionImpls=(List<TransitionImpl>)activity.getIncomingTransitions();
			transitionImpls.add(transition);
//			activity.getIncomingTransitions().add(transition);
			
			//查询execution中的当前任务并完成它
			Task task = engine.getTaskService().createTaskQuery().processInstanceId(execution.getProcessInstance().getId())
				.activityName(execution.getActivityName()).uniqueResult();
			if(task==null){
				Execution tempExecution = execution.findActiveExecutionIn(execution.getActivityName());
				if(tempExecution==null)return null;//并发时返回null
				engine.getExecutionService().signalExecutionById(tempExecution.getId(),DYNAMIC_TRANSITION_NAME);
			}else{
				engine.getTaskService().completeTask(task.getId(), DYNAMIC_TRANSITION_NAME);
			}
			
			//移除transition
			activity.getIncomingTransitions().remove(transition);
			fromActivity.getOutgoingTransitions().remove(transition);
			
			//用下面方式直接移除，将会有空指针异常
			//activity.removeIncomingTransition(transition);
		}
		
		return activity;
	}

}
