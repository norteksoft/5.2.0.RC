package com.norteksoft.wf.engine.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.entity.WorkflowAttachment;
import com.norteksoft.wf.engine.entity.WorkflowAttachmentFile;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;


@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "upload", type = "redirectAction")})

public class UploadAction extends CrudActionSupport<WorkflowAttachment>{

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(UploadAction.class);

	private Long id;
	
	private WorkflowAttachment workflowAttachment;
	
	private String workflowId;
	
	private Long taskId;
	
	private String processDefinitionId;
	
	private Long companyId;
	
	private WorkflowInstanceManager workflowInstanceManager;
	
	private TaskService taskService;
	
	private Boolean delete=true;  //删除权限 
	
	private Boolean insert=true; //增加权限
 
	private Boolean download=true; //下载权限
	
	private List<WorkflowAttachment> attachments;
	
	private static final String ATTACHMENT_ID = "ATTACHMENTID:";
	
	@Override
	public String delete() throws Exception {
		workflowInstanceManager.deleteAttachment(id);
		return list();
	}

	@Override
	public String input() throws Exception {
		return null;
	}

	@Override
	public String list() throws Exception {
		log.debug("*** list 处理任务方法开始");
		companyId = ContextUtils.getCompanyId();
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("task:").append(companyId)
		.append("]").toString());
		log.debug("*** list 处理任务方法结束");
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			workflowAttachment = new WorkflowAttachment();
		}else{
			workflowAttachment =workflowInstanceManager.getAttachment(id);
		}
		
	}

	@Override
	public String save() throws Exception {
		HttpServletRequest request=ServletActionContext.getRequest();
		long companyId;
		String workflowId = null;
		long taskId;
		String companyIdParam = request.getParameter("companyId");
		if(StringUtils.isEmpty(companyIdParam)) return renderText(getText("attachment.must.companyid"));
		try {
			companyId = Long.valueOf(companyIdParam);
		} catch (NumberFormatException e) {
			return this.getText("attachment.invalid.companyid");
		}
		String taskIdParam  = request.getParameter("taskId");
		if(StringUtils.isEmpty(taskIdParam)) return renderText(getText("attachment.must.taskId"));
		try {
			taskId = Long.valueOf(taskIdParam);
		} catch (NumberFormatException e) {
			return this.getText("attachment.invalid.taskId");
		}
		
		//把request强转，因为struts从新封装了request(Filedata是它的参数不能改变)
		MultiPartRequestWrapper wrapper=(MultiPartRequestWrapper)request;
		File filePath=wrapper.getFiles("Filedata")[0];
		String fileName=request.getParameter("Filename");
		BufferedInputStream bis=new BufferedInputStream(new FileInputStream(filePath));
		byte[] content=null;
		try {
			content = new byte[bis.available()];
			bis.read(content);
		}finally{
			bis.close();
		}
		WorkflowTask task;
		task = taskService.getTask(taskId);
		workflowId = task.getProcessInstanceId();
		workflowAttachment.setTaskName(task.getName());
		workflowAttachment.setTaskMode(task.getProcessingMode());
		workflowAttachment.setFileName(fileName);
		workflowAttachment.setCompanyId(companyId);
		workflowAttachment.setWorkflowId(workflowId);
		workflowAttachment.setCreatedTime(new Date());
		workflowInstanceManager.saveAttachment(workflowAttachment);
		
		WorkflowAttachmentFile attachmentFile = new WorkflowAttachmentFile();
		attachmentFile.setContent(content);
		attachmentFile.setCompanyId(companyId);
		attachmentFile.setAttachmentId(workflowAttachment.getId());
		workflowInstanceManager.saveAttachmentFile(attachmentFile);
		return this.renderText(ATTACHMENT_ID+workflowAttachment.getId().toString());
	}
	

	public void prepareDownload()throws Exception {
		prepareModel();
	}
	public String download()throws Exception{
		WorkflowAttachmentFile attachmentFile = workflowInstanceManager.getAttachmentFileByAttachmentId(workflowAttachment.getId());
		BufferedInputStream bis = new BufferedInputStream(
				new ByteArrayInputStream(attachmentFile.getContent()));
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		try {
			String fileName = workflowAttachment.getFileName();
			byte[] byname=fileName.getBytes("UTF-8");
			fileName=new String(byname,"UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ URLEncoder.encode(fileName, "UTF-8"));
			OutputStream out=response.getOutputStream();
			byte[] buffer = new byte[4096];
			int size = 0;
			while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			bis.close();
		}
		
		return null;
	}
	
	public WorkflowAttachment getModel() {
		return workflowAttachment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public List<WorkflowAttachment> getAttachments() {
		return attachments;
	}

	public Boolean getDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}

	public Boolean getInsert() {
		return insert;
	}

	public void setInsert(Boolean insert) {
		this.insert = insert;
	}

	public Boolean getDownload() {
		return download;
	}

	public void setDownload(Boolean download) {
		this.download = download;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	
	public Long getCompanyId() {
		return companyId;
	}

	@Autowired
	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}
	@Autowired
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
}
