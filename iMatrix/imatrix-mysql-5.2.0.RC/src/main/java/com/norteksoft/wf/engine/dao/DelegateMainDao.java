package com.norteksoft.wf.engine.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.TrustRecord;

@Repository
public class DelegateMainDao extends HibernateDao<TrustRecord, Long>{

	public void deleteDelegateMainByFlowId(String processId) {
		this.createQuery("delete TrustRecord dm where dm.processId=?", processId).executeUpdate();
	}
	
}
