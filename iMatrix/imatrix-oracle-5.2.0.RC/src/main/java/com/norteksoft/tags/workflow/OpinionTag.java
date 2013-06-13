package com.norteksoft.tags.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.wf.engine.entity.Opinion;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

public class OpinionTag extends TagSupport {
	private static final long serialVersionUID = 3L;
	private Log log = LogFactory.getLog(OpinionTag.class); 
	
	private Long taskId;
	private Long companyId;
	private Long id;
	
	private String webRoot;
	
	private Boolean view=false;  //查看意见的权限 

	private Boolean edit=false; //编辑意见的权限
 
	private Boolean must=false; //意见必填

	public Boolean getView() {
		return view;
	}

	public Boolean getEdit() {
		return edit;
	}

	public Boolean getMust() {
		return must;
	}
	
	public String getWebRoot() {
		return webRoot;
	}
	
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	public String getId() {
		return String.valueOf(id);
	}
	
	public void setId(Long id) {
		if(id == null) {
			this.id = 0l;
		}
		this.id = id;
	}
	
	@Override
	public int doStartTag() throws JspException {
		webRoot = ServletActionContext.getRequest().getContextPath();
		JspWriter out=pageContext.getOut();
		List<Opinion> opinions = null;
		String workflowId = "";
		if(taskId != 0) {
			WorkflowTaskManager workflowTaskManager = (WorkflowTaskManager) ContextUtils.getBean("workflowTaskManager");
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			WorkflowRightsManager workflowRightsManager = (WorkflowRightsManager) ContextUtils.getBean("workflowRightsManager");
			WorkflowTask task = workflowTaskManager.getTask(taskId);
			edit = workflowRightsManager.editOpinionRight(task);
			view = workflowRightsManager.viewOpinionRight(task);
			if(view || edit) {
				workflowId = task.getProcessInstanceId();
				opinions = workflowInstanceManager.getOpinionsByInstanceId(task.getProcessInstanceId(), companyId);							
			}
			if(TaskState.COMPLETED.getIndex().equals(task.getActive())||TaskState.CANCELLED.getIndex().equals(task.getActive())){
				edit=false;
			}
		}
		try {
			out.println(readTemplet(opinions, workflowId));
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	}
	
	private String readTemplet(List<Opinion> opinions, String workflowId) throws Exception {
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("opinions", opinions);
		root.put("ctx", webRoot);
		root.put("view", view);
		root.put("edit", edit);
		root.put("id", getId());
		root.put("workflowId", workflowId);
		root.put("taskId", taskId);
		root.put("companyId", companyId);
		String result =TagUtil.getContent(root, "workflow/opinion.ftl");
		return result;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
}
