package com.norteksoft.tags.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.wf.engine.entity.Document;
import com.norteksoft.wf.engine.service.OfficeManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

public class OfficialTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(OfficialTag.class); 
	
	private Long taskId;
	
	private String webRoot;
	private String workflowId="";
	private Boolean deleteRight=false;//删除正文的权限
	private Boolean createRight=false;  //创建正文的权限 
	@Override
	public int doStartTag() throws JspException{  
		webRoot = ((HttpServletRequest)this.pageContext.getRequest()).getContextPath();
		List<Document> offices=null;
		if(taskId!=0){
			 WorkflowTaskManager workflowTaskManager = (WorkflowTaskManager) ContextUtils.getBean("workflowTaskManager");
			 WorkflowRightsManager workflowRightsManager = (WorkflowRightsManager) ContextUtils.getBean("workflowRightsManager");
			 OfficeManager officeManager = (OfficeManager) ContextUtils.getBean("officeManager");
			 WorkflowTask task = workflowTaskManager.getTask(taskId);
			 workflowId = task.getProcessInstanceId();
			 deleteRight=workflowRightsManager.officialTextDeleteRight(task);
			 createRight=workflowRightsManager.officialTextCreateRight(task);
			 if(TaskState.COMPLETED.getIndex().equals(task.getActive())||TaskState.CANCELLED.getIndex().equals(task.getActive())){
				 deleteRight=false;
				 createRight=false;
			 }
			 offices = officeManager.getAllDocumentsByWorkflowInstanceId(workflowId,ContextUtils.getCompanyId());
		}
		 try {
			 ((HttpServletRequest)this.pageContext.getRequest()).setCharacterEncoding("utf-8");
			 JspWriter out=pageContext.getOut(); 
			 out.print(readTemplet(offices,workflowId));
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		 return Tag.EVAL_PAGE;
	 }
	
	private String readTemplet(List<Document> offices,String workflowId) throws Exception {
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("ctx", webRoot);
		root.put("offices", offices);
		root.put("workflowId", workflowId);
		root.put("taskId", taskId);
		root.put("companyId", ContextUtils.getCompanyId().toString());
		root.put("deleteRight", deleteRight);
		root.put("createRight", createRight);
		String result =TagUtil.getContent(root, "workflow/official.ftl");
		return result;
	}

	 
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
	public String getWebRoot() {
		return webRoot;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public Boolean getDeleteRight() {
		return deleteRight;
	}

	public Boolean getCreateRight() {
		return createRight;
	}
}

