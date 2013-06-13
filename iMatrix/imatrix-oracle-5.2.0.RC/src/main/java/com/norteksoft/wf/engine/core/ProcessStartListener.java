package com.norteksoft.wf.engine.core;


import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.jbpm.internal.log.Log;
import org.springframework.util.Assert;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;


public class ProcessStartListener implements EventListener {

  private static final long serialVersionUID = 1L;
  private static final Log log = Log.getLog(ProcessStartListener.class.getName());
  
  
public void notify(EventListenerExecution execution) {
	Assert.notNull(execution,"流程发起监听中，execution不能为null");
	  WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
	  String parentWorkflowId = execution.getVariable(CommonStrings.PARENT_WORKFLOW_ID)==null?null:execution.getVariable(CommonStrings.PARENT_WORKFLOW_ID).toString();
	  String parentExecutionId = execution.getVariable(CommonStrings.PARENT_EXECUTION_ID)==null?null:execution.getVariable(CommonStrings.PARENT_EXECUTION_ID).toString();
	  String parentTacheName = execution.getVariable(CommonStrings.PARENT_TACHE_NAME)==null?null:execution.getVariable(CommonStrings.PARENT_TACHE_NAME).toString();
	  String processDefinitionId = execution.getProcessDefinitionId();
	  String workflowId = execution.getProcessInstance().getId();
	  ((TaskService) ContextUtils.getBean("taskService")).executionVariableCommand(new ExecutionVariableCommand(execution.getId(),CommonStrings.PROCESS_ID,workflowId));
	  log.debug("ProcessStartListener 开始创建流程实例");
	  workflowInstanceManager.newWorkflowInstance(processDefinitionId, workflowId,  parentWorkflowId,parentExecutionId, parentTacheName);
  }
}
