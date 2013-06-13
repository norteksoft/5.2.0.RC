package com.norteksoft.wf.engine.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.wf.engine.entity.DocumentTemplate;
import com.norteksoft.wf.engine.entity.DocumentTemplateFile;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.DocumentTemplateFileManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "office-template", type = "redirectAction") })
public class OfficeTemplateAction extends CrudActionSupport<DocumentTemplate> {
	private static final long serialVersionUID = 1L;
	
	private Log log=LogFactory.getLog(CrudActionSupport.class);
	
	private Page<DocumentTemplate> page = new Page<DocumentTemplate>(0,true);
	private Long id;
	private Long typeId = 0l;
	private DocumentTemplateFile documentTemplateFile;
	private DocumentTemplate documentTemplate;
	private List<WorkflowType> typeList ;
	private List<Long> templateIds;
	private String backTypeId;
	
	@Autowired
	private DocumentTemplateFileManager documentTemplateFileManager;
	@Autowired
	private WorkflowTypeManager workflowTypeManager;
	
	@Override
	public String delete() throws Exception {
		documentTemplateFileManager.deleteTemplateByIds(templateIds);
		ApiFactory.getBussinessLogService().log("正文模板", 
				"删除正文模板", 
				ContextUtils.getSystemId("wf"));
		this.addActionMessage("已删除");
		return list();
	}

	@Override
	public String input() throws Exception {
		typeList = workflowTypeManager.getAllWorkflowType();
		typeId = documentTemplate.getTypeId();
		ApiFactory.getBussinessLogService().log("正文模板", 
				"正文模板表单页面", 
				ContextUtils.getSystemId("wf"));
		return "input";
	}

	@Override
	public String list() throws Exception {
		if(page.getPageSize()>1){
			if(typeId==null||typeId==0){
				documentTemplateFileManager.getTemplate(page);
			}else{
				documentTemplateFileManager.getTemplate(page,typeId);
			}
			ApiFactory.getBussinessLogService().log("正文模板", 
					"正文模板列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			documentTemplate = new DocumentTemplate();
		}else{
			documentTemplate = documentTemplateFileManager.getDocumentTemplate(id);
		}
	}

	@Override
	public String save() throws Exception {
		documentTemplate.setCompanyId(ContextUtils.getCompanyId());
		documentTemplate.setSystemId(ContextUtils.getSystemId());
		documentTemplateFileManager.saveTemplate(documentTemplate);
		ApiFactory.getBussinessLogService().log("正文模板", 
				"保存正文模板", 
				ContextUtils.getSystemId("wf"));
		this.addActionMessage(getText("template.save.success"));
		return input();
	}
	
	/**
	 * 上传模板
	 * @return
	 * @throws Exception
	 */
	public String upload() throws Exception{
		log.debug(" *** into upload method  *** ");
		//从request中获取参数并封装实体
		HttpServletRequest request = ServletActionContext.getRequest();
		MultiPartRequestWrapper wrapper = (MultiPartRequestWrapper) request;
		File file = wrapper.getFiles("Filedata")[0];
		String fileName = request.getParameter("Filename");
		String templateId = request.getParameter("templateId");
		if(StringUtils.isNotEmpty(templateId)) id = Long.valueOf(templateId);
		documentTemplate = documentTemplateFileManager.getDocumentTemplate(id);
		documentTemplate.setFileName(fileName);
		Long currentTime = System.currentTimeMillis();
		documentTemplate.setCreatedTime(new Date());
		
		documentTemplate.setFileType(StringUtils.substring(fileName, fileName.lastIndexOf('.')));
		documentTemplate.setRecordId(currentTime);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		documentTemplate.setFileSize(new Integer(bis.available())/1024);
		byte[] content=null;
		try {
			content = new byte[bis.available()];
			bis.read(content);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			bis.close();
		}
		documentTemplateFile=documentTemplateFileManager.getDocumentTemplateFile(id);
		if(documentTemplateFile==null){
			documentTemplateFile=new DocumentTemplateFile();
		}
		documentTemplateFile.setTemplateId(id);
		documentTemplateFile.setFileBody(content);
//		documentTemplateFileManager.saveTemplate(documentTemplate);
		documentTemplateFileManager.saveTemplateFile(documentTemplateFile,documentTemplate);
		ApiFactory.getBussinessLogService().log("正文模板", 
				"上传正文模板", 
				ContextUtils.getSystemId("wf"));
		return null;
	}
	public DocumentTemplate getModel() {
		return documentTemplate;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTypeId() {
		return typeId;
	}

	public Page<DocumentTemplate> getPage() {
		return page;
	}

	public void setPage(Page<DocumentTemplate> page) {
		this.page = page;
	}
	public List<WorkflowType> getTypeList() {
		return typeList;
	}
	public void setTemplateIds(List<Long> templateIds) {
		this.templateIds = templateIds;
	}
	public String getBackTypeId() {
		return backTypeId;
	}
	public void setBackTypeId(String backTypeId) {
		this.backTypeId = backTypeId;
	}
}
