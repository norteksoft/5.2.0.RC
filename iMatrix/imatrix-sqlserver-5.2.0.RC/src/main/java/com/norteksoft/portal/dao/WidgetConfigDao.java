package com.norteksoft.portal.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.WidgetConfig;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;


@Repository
public class WidgetConfigDao extends HibernateDao<WidgetConfig, Long>{
	
	public WidgetConfig getWidgetConfig(Long webPageId,Long widgetId){
		return this.findUnique("FROM WidgetConfig w WHERE w.webpageId=? AND w.widgetId=? AND w.userId=?",webPageId,widgetId,ContextUtils.getUserId());
	}
	
	public List<WidgetConfig> getWidgetConfigs(Long webpageId){
		StringBuilder hql = new StringBuilder("from WidgetConfig wc where wc.webpageId=? and wc.userId=? and wc.visible=true");
		return this.find(hql.toString(), webpageId,ContextUtils.getUserId());
	}
	
	public List<WidgetConfig> getCustomerWidgetConfigs(Long webpageId){
		StringBuilder hql = new StringBuilder("from WidgetConfig wc where wc.webpageId=? and wc.visible=true");
		return this.find(hql.toString(), webpageId);
	}
	
	public WidgetConfig getWidgetConfigByWidgetId(Long widgetId,Long webpageId){
		String hql="From WidgetConfig wc WHERE wc.userId=? AND wc.widgetId=? AND wc.webpageId=?";
		return this.findUnique(hql,ContextUtils.getUserId(),widgetId,webpageId);
	}
	
	public List<WidgetConfig> getWidgetConfigsByWidgetId(Long widgetId){
		String hql="from WidgetConfig wc where wc.widgetId=? and wc.companyId=?";
		return this.find(hql,widgetId,ContextUtils.getCompanyId());
	}
	
	public List<WidgetConfig> getVisibleWidgetConfigsByWidgetId(Long widgetId){
		String hql="from WidgetConfig wc where wc.widgetId=? and wc.companyId=? and wc.visible=true";
		return this.find(hql,widgetId,ContextUtils.getCompanyId());
	}
	
}
