package com.norteksoft.wf.base.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.norteksoft.product.util.WebContextUtils;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;
@Deprecated
public class DeleteRightsTag extends TagSupport {
	private static final long serialVersionUID = 5L;
	
	private Long taskId;
	
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public int doStartTag() throws JspException {
		WorkflowTaskManager taskmanager = (WorkflowTaskManager)WebContextUtils.getBean("workflowTaskManager");
		WorkflowRightsManager rightsManager = (WorkflowRightsManager)WebContextUtils.getBean("workflowRightsManager");
		WorkflowInstanceManager instanceManager = (WorkflowInstanceManager)WebContextUtils.getBean("workflowInstanceManager");
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
