package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionTemplate;

@Repository
public class WorkflowDefinitionTemplateDao extends HibernateDao<WorkflowDefinitionTemplate, Long>{
	public List<WorkflowDefinitionTemplate> getWorkflowDefinitionTemplates(Long typeId, Long companyId) {
		return this.find("from WorkflowDefinitionTemplate ft where ft.companyId = ? and ft.typeId=?",companyId,typeId);
	}
	
	/**
	 * 获得所有的流程定义模板
	 * @param page
	 */
	public void getTemplateXml(Page<WorkflowDefinitionTemplate> page) {
		this.searchPageByHql(page, "from WorkflowDefinitionTemplate wdt where wdt.companyId=? ", ContextUtils.getCompanyId());
	}

	/**
	 * 根据流程类型获得流程定义模板
	 * @param page
	 * @param typeId
	 */
	public void getTemplateXml(Page<WorkflowDefinitionTemplate> page,
			Long typeId) {
		this.searchPageByHql(page, "from WorkflowDefinitionTemplate wdt where wdt.companyId=? and wdt.typeId=? ", ContextUtils.getCompanyId(),typeId);
	}
}
