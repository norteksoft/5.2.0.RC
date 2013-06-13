package com.norteksoft.acs.service.sale;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.sale.SalesModule;
import com.norteksoft.product.orm.Page;


/**
 * 销售包管理
 */
@Service
@Transactional
public class SalesModuleManager{
	private SimpleHibernateTemplate<SalesModule, Long> salesModuleDao;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		salesModuleDao = new SimpleHibernateTemplate<SalesModule, Long>(sessionFactory, SalesModule.class);
	}
	
	public void saveSalesModule(SalesModule salesModule){
		salesModuleDao.save(salesModule);
	}
	
	public void deleteSalesModule(Long id){
		SalesModule salesModule = salesModuleDao.get(id);
		salesModule.setDeleted(false);
		salesModuleDao.save(salesModule);
	}
	
	public Page<SalesModule> getAllSalesMdule(Page<SalesModule> page){
		return salesModuleDao.findAll(page);
	}
	
	public List<SalesModule> getAllSalesMdule(){
		return salesModuleDao.findAll();
	}
	
	public SalesModule getSalesModule(Long id){
		return salesModuleDao.get(id);
	}
	
	public List<SalesModule> getSalesModulesBySystem(Long systemId){
		return salesModuleDao.findByCriteria(Restrictions.eq("systemId", systemId), Restrictions.eq("deleted", false));
	}
}
