package com.norteksoft.wf.engine.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.orm.Page;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionTemplateDao;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionTemplateFileDao;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionTemplate;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionTemplateFile;

@Service
@Transactional
public class WorkflowDefinitionTemplateManager {
	@Autowired
	private WorkflowDefinitionTemplateDao workflowDefinitionTemplateDao;
	@Autowired
	private WorkflowDefinitionTemplateFileDao workflowDefinitionTemplateFileDao;

	/**
	 * 根据ID获得流程定义模板
	 * @param id
	 * @return
	 */
	public WorkflowDefinitionTemplate getWorkflowDefinitionTemplate(Long id) {
		return workflowDefinitionTemplateDao.get(id);
	}

	/**
	 * 获得所有的流程定义模板
	 * @param page
	 */
	public void getTemplate(Page<WorkflowDefinitionTemplate> page) {
		workflowDefinitionTemplateDao.getTemplateXml(page);
	}

	/**
	 * 根据流程类型获得流程定义模板
	 * @param page
	 * @param typeId2
	 */
	public void getTemplate(Page<WorkflowDefinitionTemplate> page, Long typeId) {
		workflowDefinitionTemplateDao.getTemplateXml(page,typeId);
	}

	/**
	 * 保存流程定义模板
	 * @param workflowDefinitionTemplate
	 */
	public void save(WorkflowDefinitionTemplate workflowDefinitionTemplate) {
		workflowDefinitionTemplateDao.save(workflowDefinitionTemplate);
	}

	/**
	 * 删除流程定义模板
	 * @param ids
	 */
	public void delete(String ids) {
		String[] str=ids.split(",");
		for(String id:str){
			WorkflowDefinitionTemplate wdt=getWorkflowDefinitionTemplate(Long.valueOf(id));
			if(StringUtils.isNotEmpty(wdt.getPreviewImage())){
				String[] fileName = wdt.getPreviewImage().split("/");
				File wdtFile = new File(getLocalPath()+fileName[fileName.length-1]);//把文件路径替换成本地路径
				wdtFile.delete();
			}
			workflowDefinitionTemplateFileDao.deleteTemplateFile(Long.valueOf(id));
			workflowDefinitionTemplateDao.delete(Long.valueOf(id));
		}
	}
	
	/**
	 * 获取本地路径，用于保存图片和删除
	 * @return
	 */
	public String getLocalPath() {
		String localPath = ServletActionContext.getServletContext().getRealPath("/");
		return localPath+"wf/images/";
	}

	/**
	 * 根据模板ID获得模板文件
	 * @param id
	 * @return
	 */
	public WorkflowDefinitionTemplateFile getWorkflowDefinitionTemplateFileByTemplateId(
			Long templateId) {
		return workflowDefinitionTemplateFileDao.getWorkflowDefinitionTemplateFileByTemplateId(templateId);
	}

	/**
	 * 保存流程定义模板和流程定义模板文件
	 * @param workflowDefinitionTemplate
	 * @param workflowDefinitionTemplateFile
	 */
	public void save(WorkflowDefinitionTemplate workflowDefinitionTemplate,
			WorkflowDefinitionTemplateFile workflowDefinitionTemplateFile) {
		workflowDefinitionTemplateFileDao.save(workflowDefinitionTemplateFile);
		workflowDefinitionTemplateDao.save(workflowDefinitionTemplate);
	}
	
}
