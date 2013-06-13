package com.norteksoft.acs.service.sale;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.sale.SubscriberItem;

@Service
@Transactional
public class SubscriberItemManager {
	
	private SimpleHibernateTemplate<SubscriberItem, Long> itemDao;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		itemDao = new SimpleHibernateTemplate<SubscriberItem, Long>(sessionFactory, SubscriberItem.class);
	}
	
	public void saveItem(SubscriberItem item){
		itemDao.save(item);
	}
	
	@SuppressWarnings("unchecked")
	public List<SubscriberItem> queryItems(Long companyId, String sysCode){
		StringBuilder hql = new StringBuilder();
		hql.append("select si from SubscriberItem si, BusinessSystem bs ");
		hql.append("where si.subsciber.tenantId=? and si.product.systemId=bs.id and bs.code=? ");
		hql.append("order by si.invalidDate desc ");
		return itemDao.find(hql.toString(), companyId, sysCode);
	}
}
