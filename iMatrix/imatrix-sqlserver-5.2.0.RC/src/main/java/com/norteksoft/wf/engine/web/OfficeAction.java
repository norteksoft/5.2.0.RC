package com.norteksoft.wf.engine.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.entity.Document;
import com.norteksoft.wf.engine.entity.DocumentFile;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.OfficeManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowRightsManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "office", type = "redirectAction") })
public class OfficeAction extends CrudActionSupport<Document> {
	private static final long serialVersionUID = 1L;
	
//	private Log log=LogFactory.getLog(CrudActionSupport.class);
	
	
	private Long id;
	private Document document;
	
	private OfficeManager officeManager;
	private WorkflowInstanceManager workflowInstanceManager;
	private WorkflowRightsManager workflowRightsManager;
	private List<Document> offices;
	private TaskService taskService;
	
	private String currentUserLonginName;
	private String currentUserName;
	
	private String workflowId;
	private Long taskId;
	
	private Boolean urgenFlag;//是否应急方案中点进的
	private Boolean viewFlag;//是否只是查看正文
	
	@Autowired
	public void setWorkflowRightsManager(
			WorkflowRightsManager workflowRightsManager) {
		this.workflowRightsManager = workflowRightsManager;
	}
	
	@Autowired
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	@Override
	public String delete() throws Exception {
		return null;
	}
    //上传正文
	@Override
	public String input() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		MultiPartRequestWrapper wrapper = (MultiPartRequestWrapper)request;
		File filePath = wrapper.getFiles("Filedata")[0];
		String fileName = request.getParameter("Filename");
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
		Document document = new Document();
		
		document.setFileSize(bis.available());
		document.setFileName(fileName);
		document.setCreatedTime(new Date());
		if(request.getParameter("taskId") != null){
			long taskId = Long.valueOf(request.getParameter("taskId"));
			WorkflowTask task = taskService.getTask(taskId);
			document.setTaskMode(task.getProcessingMode());
			document.setTaskName(task.getName());
			document.setWorkflowId(task.getProcessInstanceId());
			document.setCompanyId(task.getCompanyId());
			document.setCreator(task.getTransactor());
			document.setCreatorName(task.getTransactorName());
		}
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		if(!("pdf").equals(fileType) ){
			fileType = "."+fileType;
		}
		document.setFileType(fileType);
		officeManager.saveDocument(document);
		DocumentFile file = new DocumentFile();
		byte[] content=null;
		try {
			content = new byte[bis.available()];
			bis.read(content);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			bis.close();
		}
        file.setFileBody(content);
        file.setDocumentId(document.getId());
		file.setCompanyId(document.getCompanyId());
        officeManager.saveDocumentFile( file);
		return "DOCUMENTID:"+document.getId();
	}

	//显示正文列表
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			document = new Document();
		}else{
			document = officeManager.getDocument(id);
		}
		
	}

	@Override
	public String save() throws Exception {
		//TODO
		return null;
	}
	
	public void prepareCreateOffice() throws Exception{
		prepareModel();
	}
	//新建word/excel
	public String createOffice() throws Exception {
	
		WorkflowInstance  instance = null;
		WorkflowTask task = null;
		if(id==null){
			task = taskService.getTask(taskId);
			instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
			document.setTaskName(task.getName());
			document.setTaskMode(task.getProcessingMode());
			document.setWorkflowId(instance.getProcessInstanceId());
			document.setCreator(ContextUtils.getLoginName());
			document.setCreatorName(ContextUtils.getUserName());
			document.setCompanyId(ContextUtils.getCompanyId());
			document.setEditType("-1,0,0,0,0,0,1,1");//允许编辑，不显示和保留痕迹，允许批注
			document.setPrintSetting(true);
			document.setDownloadSetting(true);
			if(document.getFileType().equalsIgnoreCase("pdf")){
				return "pdf";
			}else{
				String template = DefinitionXmlParse.getOfficialTextTemplate(instance.getProcessDefinitionId(),task.getName());
				if(StringUtils.isNotEmpty(template))document.setTemplateId(Long.valueOf(StringUtils.substringBetween(template, "[", "]")));
				setOfficeRight(document,instance,task);
				return "view";
			}
		}else{
			if(taskId==null){
				instance = workflowInstanceManager.getWorkflowInstance(document.getWorkflowId());
				task=taskService.getTask(instance.getFirstTaskId());
			}else{
				task = taskService.getTask(taskId);
				instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
			}
			setOfficeRight(document,instance,task);
			if(document.getFileType().equalsIgnoreCase("pdf")){
				return "pdf";
			}else{
				return "view";
			}
		}
		
	}
	
	private void setOfficeRight(Document document ,WorkflowInstance instance,WorkflowTask task){
		StringBuilder editType = new StringBuilder("-1");// "-1,0,1,1,0,0,1,1";//查看保留痕迹
		if(urgenFlag!=null && urgenFlag){
			editType.append(",0,1,1,0,0,1,1");
			document.setEditType(editType.toString());
			document.setPrintSetting(true);
			document.setDownloadSetting(true);
		}else if(viewFlag!=null&&viewFlag){
			editType.append(",1,0,0,0,0,1,1");
			document.setEditType(editType.toString());
			document.setPrintSetting(false);
			document.setDownloadSetting(false);
		 }else{
			 //是否保户文档
			 if(TaskState.WAIT_TRANSACT.getIndex().equals(task.getActive())&&workflowRightsManager.officialTextEditRight(task)){
				 editType.append(",0");
			 }else{
				 editType.append(",1");
			 }
			 //是否显示痕迹
			 if(workflowRightsManager.officialTextViewTrace(task)){
				 editType.append(",1");
			 }else{
				 editType.append(",0");
			 }
			 //是否保留痕迹
			 if(workflowRightsManager.officialTextRetainTrace(task)){
				 editType.append(",1");
			 }else{
				 editType.append(",0");
			 }
			 editType.append(",0,0,1,1");
			 document.setEditType(editType.toString());
			 boolean downloadSetting = workflowRightsManager.officialTextDownloadRight(task);
			 boolean printSetting = workflowRightsManager.officialTextPrintRight(task);
			 document.setPrintSetting(printSetting);
			 document.setDownloadSetting(downloadSetting);
		 }
	}
	
	/**
	 * 删除正文
	 */
	public String deleteText() throws Exception{
		officeManager.deleteText(id);
		//renderText("ok");
		return list();
	}
	
	public String webOfficeServer() throws Exception{
		
		return null;
	}
	
	
	
	
	//上传文件
	public String upload() throws Exception{
		//companyId = ContextUtils.getCompanyId();
		return "upload";
	}
	public Document getModel() {
		return document;
	}

    @Required
    public void setOfficeManager(OfficeManager officeManager) {
		this.officeManager = officeManager;
	}
    @Required
    public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}

	public List<Document> getOffices() {
		return offices;
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
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getCurrentUserLonginName() {
		currentUserLonginName = ContextUtils.getLoginName();
		return currentUserLonginName;
	}

	public String getCurrentUserName() {
		currentUserName=ContextUtils.getUserName();
		return currentUserName;
	}
	
	public void setViewFlag(Boolean viewFlag) {
		this.viewFlag = viewFlag;
	}

	public void setUrgenFlag(Boolean urgenFlag) {
		this.urgenFlag = urgenFlag;
	}
	
	
}
