package com.norteksoft.wf.engine.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.jbpm.pvm.internal.model.ExecutionImpl;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.service.InstanceHistoryManager;

public class TransitionListener implements EventListener{

	private static final long serialVersionUID = 1L;

	public void notify(EventListenerExecution execution) throws Exception {
		ExecutionImpl parent = ((ExecutionImpl)execution).getProcessInstance().getSuperProcessExecution();
		if(parent == null){
			String instanceId = execution.getProcessInstance().getId();
			String processName = ((ExecutionImpl)execution).getProcessDefinition().getName();
			String nextTask = ((ExecutionImpl)execution).getTransition().getDestination().getName();
			InstanceHistoryManager instanceHistoryManager = (InstanceHistoryManager) ContextUtils.getBean("instanceHistoryManager");
			//流程启动记录
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			StringBuilder msg = new StringBuilder();
			msg.append(dateFormat.format(new Date())).append(", ");
			msg.append("[ ").append(processName).append(" ]流程启动");
			InstanceHistory ih = new InstanceHistory(ContextUtils.getCompanyId(), 
					instanceId, InstanceHistory.TYPE_FLOW_START, 
					msg.toString());
			instanceHistoryManager.saveHistory(ih);
			//流程进入第一个环节记录
			msg = new StringBuilder();
			msg.append(dateFormat.format(new Date())).append(", ");
			msg.append("流程进入[ ").append(nextTask).append(" ]");
			ih = new InstanceHistory(ContextUtils.getCompanyId(), 
					instanceId, InstanceHistory.TYPE_FLOW_INTO, 
					msg.toString());
			ih.setTaskName(nextTask);
			ih.setExecutionId(execution.getId());
			instanceHistoryManager.saveHistory(ih);
		}
	}

}
