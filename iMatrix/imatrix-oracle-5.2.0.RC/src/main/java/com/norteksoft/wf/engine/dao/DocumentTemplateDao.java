package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.DocumentTemplate;

@Repository
public class DocumentTemplateDao extends HibernateDao<DocumentTemplate, Long>{
	public void getTemplate(Page<DocumentTemplate> page, Long companyId) {
		this.searchPageByHql(page, "from DocumentTemplate dtf where dtf.companyId=? order by dtf.id desc", companyId);
	}

	public void getTemplate(Page<DocumentTemplate> page, Long typeId,
			Long companyId) {
		this.searchPageByHql(page, "from DocumentTemplate dtf where dtf.typeId=? and dtf.companyId=? order by dtf.id desc", typeId, companyId);
	}
	
	public List<DocumentTemplate> getTemplate(Long typeId,Long companyId){
		return this.find("from DocumentTemplate dtf where dtf.typeId=? and dtf.companyId=?  order by dtf.id desc", typeId, companyId);
	}
}
