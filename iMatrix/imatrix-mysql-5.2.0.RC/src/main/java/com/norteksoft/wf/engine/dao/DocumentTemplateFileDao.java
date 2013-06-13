package com.norteksoft.wf.engine.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.DocumentTemplateFile;

@Repository
public class DocumentTemplateFileDao extends HibernateDao<DocumentTemplateFile, Long>{

	/**
	 * 根据模板ID获得模板文件
	 * @param templateId
	 * @return
	 */
	public DocumentTemplateFile getDocumentTemplateFile(Long templateId,Long companyId) {
		return this.findUnique("from DocumentTemplateFile dtf where dtf.companyId=? and dtf.templateId=? ", companyId,templateId);
	}

	/**
	 * 根据模板ID删除模板文件
	 * @param long1
	 */
	public void deleteTemplateFile(Long templateId,Long companyId) {
		StringBuilder hql = new StringBuilder();
		hql.append("delete from DocumentTemplateFile dtf where dtf.companyId=? and dtf.templateId=? ");
		this.batchExecute(hql.toString(), companyId,templateId);
	}

}
