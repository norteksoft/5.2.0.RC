package com.norteksoft.acs.service.sale;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.sale.Subsciber;
import com.norteksoft.product.orm.Page;

/**
 * 定单管理
 * 
 */
@Service
@Transactional
public class SubsciberManager{
	private static String QUERY_BY_TENANT_HQL = "select ss from Subsciber ss where ss.tenantId=? and deleted=?";
	private SimpleHibernateTemplate<Subsciber, Long> subsciberDao;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		subsciberDao = new SimpleHibernateTemplate<Subsciber, Long>(sessionFactory, Subsciber.class);
	}
	
	public void saveSubsciber(Subsciber subsciber){
		subsciberDao.save(subsciber);
	}
	
	public void deleteSubsciber(Long id){
		Subsciber subsciber = subsciberDao.get(id);
		subsciber.setDeleted(true);
		subsciberDao.save(subsciber);
	}
	
	public Subsciber getSubsciber(Long id){
		return subsciberDao.get(id);
	}
	
	public List<Subsciber> getAllSubsciber(){
		return subsciberDao.findAll();
	}
	
	public Page<Subsciber> getAllSubsciber(Page<Subsciber> page){
		return subsciberDao.findAll(page);
	}
	
	public Page<Subsciber> getSubsciberByTenant(Page<Subsciber> page, Long tenantId){
		return subsciberDao.find(page, QUERY_BY_TENANT_HQL, tenantId, false);
	}
	
	@SuppressWarnings("unchecked")
	public Integer getAllowedNumbByCompany(Long companyId){
		List<Subsciber> subscibers = subsciberDao.find(QUERY_BY_TENANT_HQL, companyId, false);
		Integer num = 0;
		for(Subsciber sb : subscibers){
			Date now = new Date();
			if(now.after(sb.getBeginDate()) && now.before(sb.getValidDate())){
				if(num < sb.getUseNumber()){
					num = sb.getUseNumber();
				}
			}
		}
		return num;
	}
}
