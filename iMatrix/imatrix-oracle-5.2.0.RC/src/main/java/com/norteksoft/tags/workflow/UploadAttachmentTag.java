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
import com.norteksoft.wf.engine.entity.WorkflowAttachment;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

public class UploadAttachmentTag extends TagSupport {
	private static final long serialVersionUID = 2L;
	private Log log = LogFactory.getLog(UploadAttachmentTag.class);
	
	private String webRoot;
	//必填的信息
	private Long taskId;
	private Long companyId;
	//可选的信息（有默认值）
	private String fileSizeLimit = "10";
	private String fileTypes = "*.*";
	private String fileTypesDescription = "所有文件";
	private String fileUploadLimit = "0";
	
	private Boolean deleteRight=false;
	private Boolean addRight=false;
	private Boolean downloadRight=false;

	//功能路径
	private String moduleUrl = "/widgets/workflow-swfupload/";
	private String imageUrl = "/images/annex.gif";

	public String getFileSizeLimit() {
		return fileSizeLimit;
	}

	public void setFileSizeLimit(String fileSizeLimit) {
		if(Integer.valueOf(fileSizeLimit.trim()) <= 10 || Integer.valueOf(fileSizeLimit.trim()) >= 0) {
			this.fileSizeLimit = fileSizeLimit;
		} else {
			this.fileSizeLimit = "10";
		}
	}

	public String getFileTypes() {
		return fileTypes;
	}

	public void setFileTypes(String fileTypes) {
		this.fileTypes = fileTypes;
	}

	public String getFileTypesDescription() {
		return fileTypesDescription;
	}

	public void setFileTypesDescription(String fileTypesDescription) {
		this.fileTypesDescription = fileTypesDescription;
	}

	public String getFileUploadLimit() {
		return fileUploadLimit;
	}

	public void setFileUploadLimit(String fileUploadLimit) {
		if(Integer.valueOf(fileUploadLimit.trim()) >= 0) {
			this.fileUploadLimit = fileUploadLimit;			
		} else {
			this.fileUploadLimit = "0";
		}
	}
	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getModuleUrl() {
		return moduleUrl;
	}

	public void setModuleUrl(String moduleUrl) {
		this.moduleUrl = moduleUrl;
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
	
	public Boolean getDeleteRight() {
		return deleteRight;
	}

	public Boolean getAddRight() {
		return addRight;
	}

	public Boolean getDownloadRight() {
		return downloadRight;
	}

	@Override
	public int doStartTag() throws JspException {
		log.debug("***doStartTag开始***");
		log.debug(new StringBuilder("*** veriable:[")
		.append("taskId:").append(taskId)
		.append("]").toString());
		webRoot = ServletActionContext.getRequest().getContextPath();
		JspWriter out=pageContext.getOut();
		List<WorkflowAttachment> attachments = null;
		WorkflowRightsManager workflowRightsManager = (WorkflowRightsManager) ContextUtils.getBean("workflowRightsManager");
		if(taskId != null && taskId!=0) {
			WorkflowTaskManager workflowTaskManager = (WorkflowTaskManager) ContextUtils.getBean("workflowTaskManager");
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager) ContextUtils.getBean("workflowInstanceManager");
			WorkflowTask task = workflowTaskManager.getTask(taskId);
			String workflowId = task.getProcessInstanceId();
			attachments = workflowInstanceManager.getAttachments(workflowId,getCompanyId());
			if(TaskState.COMPLETED.getIndex().equals(task.getActive())||TaskState.CANCELLED.getIndex().equals(task.getActive())){
				deleteRight=false;
				addRight=false;
				downloadRight=false;
			}else{
				deleteRight=workflowRightsManager.attachmentDeleteRight(task);
				addRight=workflowRightsManager.attachmentAddRight(task);
				downloadRight=workflowRightsManager.attachmentDownloadRight(task);
			}
		}
		try {
			out.print(readTemplet(attachments));
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		log.debug("***doStartTag结束***");
		return Tag.EVAL_PAGE;
	}
	
	private String readTemplet(List<WorkflowAttachment> attachments) throws Exception {
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("attachments", attachments);
		root.put("ctx", webRoot);
		root.put("fileSizeLimit", fileSizeLimit);
		root.put("fileTypes", fileTypes);
		root.put("fileTypesDescription", fileTypesDescription);
		root.put("fileUploadLimit", fileUploadLimit);
		root.put("moduleUrl", moduleUrl);
		root.put("imageUrl", imageUrl);
		root.put("taskId", taskId.toString());
		root.put("companyId", companyId.toString());
		root.put("deleteRight", deleteRight);
		root.put("addRight", addRight.toString());
		root.put("downloadRight", downloadRight);
		String result =TagUtil.getContent(root, "workflow/uploadattachment.ftl");
		return result;
	}
	
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
	
}
