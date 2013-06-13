package com.norteksoft.tags.workflow;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

public class TablePrintRightsTag extends TagSupport {
	private static final long serialVersionUID = 4L;
	
	private Long taskId;
	
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public int doStartTag() throws JspException {
		WorkflowTaskManager taskmanager = (WorkflowTaskManager)ContextUtils.getBean("workflowTaskManager");
		WorkflowRightsManager rightsManager = (WorkflowRightsManager)ContextUtils.getBean("workflowRightsManager");
		WorkflowTask task = taskmanager.getTask(taskId);
		boolean rights = rightsManager.printFormRight(task);
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
