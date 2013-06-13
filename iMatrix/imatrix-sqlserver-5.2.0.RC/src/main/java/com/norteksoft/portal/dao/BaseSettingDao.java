package com.norteksoft.portal.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.BaseSetting;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class BaseSettingDao extends HibernateDao<BaseSetting, Long>{

	public BaseSetting getBaseSettingByLonginName() {
		return findUnique("from BaseSetting bs where bs.creator=? and bs.companyId=?",ContextUtils.getLoginName(),ContextUtils.getCompanyId());
	}
	
	public BaseSetting getBaseSettingByLonginName(String loginName,Long companyId) {
		return findUnique("from BaseSetting bs where bs.creator=? and bs.companyId=?",loginName,companyId);
	}
}
