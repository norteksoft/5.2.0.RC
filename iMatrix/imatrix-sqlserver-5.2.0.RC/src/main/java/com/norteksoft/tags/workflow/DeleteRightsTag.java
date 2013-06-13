package com.norteksoft.tags.workflow;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

public class DeleteRightsTag extends TagSupport {
	private static final long serialVersionUID = 5L;
	
	private Long taskId;
	
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public int doStartTag() throws JspException {
		WorkflowTaskManager taskmanager = (WorkflowTaskManager)ContextUtils.getBean("workflowTaskManager");
		WorkflowRightsManager rightsManager = (WorkflowRightsManager)ContextUtils.getBean("workflowRightsManager");
		WorkflowInstanceManager instanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
		WorkflowTask task = taskmanager.getTask(taskId);
		WorkflowInstance instance = instanceManager.getWorkflowInstance(task.getProcessInstanceId());
		boolean rights = rightsManager.workflowDeleteRight(instance, task.getName());
		if(rights) {
			return Tag.EVAL_PAGE;
		}
		return Tag.SKIP_BODY;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
}
