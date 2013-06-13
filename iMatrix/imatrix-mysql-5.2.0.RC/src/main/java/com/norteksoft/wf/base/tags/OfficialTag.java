package com.norteksoft.wf.base.tags;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.mms.base.utils.FremarkParseUtils;
import com.norteksoft.product.util.WebContextUtils;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.service.WorkflowTaskManager;
import com.norteksoft.wf.engine.entity.Document;
import com.norteksoft.wf.engine.service.OfficeManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;
@Deprecated
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
			 WorkflowTaskManager workflowTaskManager = (WorkflowTaskManager) WebContextUtils.getBean("workflowTaskManager");
			 WorkflowRightsManager workflowRightsManager = (WorkflowRightsManager) WebContextUtils.getBean("workflowRightsManager");
			 OfficeManager officeManager = (OfficeManager) WebContextUtils.getBean("officeManager");
			 WorkflowTask task = workflowTaskManager.getTask(taskId);
			 workflowId = task.getProcessInstanceId();
			 deleteRight=workflowRightsManager.officialTextDeleteRight(task);
			 createRight=workflowRightsManager.officialTextCreateRight(task);
			 if(TaskState.COMPLETED.getIndex().equals(task.getActive())||TaskState.CANCELLED.getIndex().equals(task.getActive())){
				 deleteRight=false;
				 createRight=false;
			 }
			 offices = officeManager.getAllDocumentsByWorkflowInstanceId(workflowId,WebContextUtils.getCompanyId());
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
//		String templetCode =JarFileUtil.readFile(getClass(), "template/tags/official.ftl");
		String file = WorkflowHistoryTag.class.getResource("/template/tags/official.ftl").getFile();
		String templetCode=FileUtils.readFileToString(new File(file),"utf-8");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("ctx", webRoot);
		root.put("offices", offices);
		root.put("workflowId", workflowId);
		root.put("taskId", taskId);
		root.put("companyId", WebContextUtils.getCompanyId().toString());
		root.put("deleteRight", deleteRight);
		root.put("createRight", createRight);
		String result = FremarkParseUtils.parseFremarkTemplate(templetCode, pageContext, root);
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

