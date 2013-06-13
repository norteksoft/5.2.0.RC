package com.norteksoft.wf.engine.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionTemplateFile;

@Repository
public class WorkflowDefinitionTemplateFileDao extends HibernateDao<WorkflowDefinitionTemplateFile, Long>{

	/**
	 * 根据模板ID获得模板文件
	 * @param templateId
	 * @return
	 */
	public WorkflowDefinitionTemplateFile getWorkflowDefinitionTemplateFileByTemplateId(
			Long templateId) {
		
		return this.findUniqueNoCompanyCondition("from WorkflowDefinitionTemplateFile dtf where dtf.templateId=? ",templateId);
	}
	
	/**
	 * 根据模板ID获得模板文件Xml
	 * @param templateId
	 * @return
	 */
	public String getTemplateXml(Long templateId){
		return this.get(templateId).getXml();
	}

	/**
	 * 根据模板ID删除模板文件
	 * @param valueOf
	 * @param companyId
	 */
	public void deleteTemplateFile(Long templateId) {
		StringBuilder hql = new StringBuilder();
		hql.append("delete from WorkflowDefinitionTemplateFile dtf where dtf.companyId=? and dtf.templateId=? ");
		this.batchExecute(hql.toString(), ContextUtils.getCompanyId(),templateId);
	}

}
