package com.norteksoft.portal.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.WidgetParameter;
import com.norteksoft.portal.entity.WidgetParameterValue;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class WidgetParameterValueDao extends HibernateDao<WidgetParameterValue, Long>{
	@Autowired
	private WidgetParameterDao widgetParameterDao;
	
	public List<WidgetParameterValue> getWidgetParameterValues(Long widgetParameterId){
		StringBuilder hql = new StringBuilder("FROM WidgetParameterValue wpv WHERE wpv.widgetParameter.id=? AND wpv.userId=0");
		return this.find(hql.toString(),widgetParameterId);
	}
	
	public List<WidgetParameterValue> getWidgetParameterValuesByUser(Long widgetParameterId,Long userId){
		StringBuilder hql = new StringBuilder("FROM WidgetParameterValue wpv WHERE wpv.widgetParameter.id=? AND wpv.userId=?");
		return this.find(hql.toString(),widgetParameterId,userId);
	}
	
	public WidgetParameterValue getWidgetParameterValue(Long widgetParameterId){
		StringBuilder vhql = new StringBuilder("FROM WidgetParameterValue wpv WHERE wpv.widgetParameter.id=?");
		return this.findUnique(vhql.toString(),widgetParameterId);
	}
	
	public List<WidgetParameterValue> getWidgetParameterValuesByUserId(Long widgetParameterId, Long webpageId){
		String hql = "FROM WidgetParameterValue wpv WHERE wpv.widgetParameter.id=? AND wpv.userId=? and wpv.webPageId=?";
		return this.find(hql,widgetParameterId,ContextUtils.getUserId(), webpageId);
	}
	
	public List<WidgetParameterValue> getWidgetParameterValuesByUserIdAndWebpageId(Long widgetParameterId,Long webPageId){
		String hql = "FROM WidgetParameterValue wpv WHERE wpv.widgetParameter.id=? AND wpv.userId=? AND wpv.webPageId=?";
		return this.find(hql,widgetParameterId,ContextUtils.getUserId(),webPageId);
	}
	
	/**
	 * 获得底层平台小窗体参数值设置
	 * @return
	 */
	public List<WidgetParameterValue> getAllDefaultParameterValues(Long companyId){
		List<WidgetParameter> widgets=widgetParameterDao.getAllDefaultWidgetParameters(companyId);
		StringBuilder hql = new StringBuilder("from WidgetParameterValue wpv where wpv.companyId=? and wpv.userId=0 ");
		Object[] values=new Object[1];
		if(widgets.size()>0){
			hql.append(" and ");
			values=new Object[1+widgets.size()];
		}
		values[0]=companyId;
		for(int i=0;i<widgets.size();i++){
			if(i==0)hql.append("(");
			hql.append(" wpv.widgetParameter.id=? ");
			if(i<widgets.size()-1){
				hql.append(" or ");
			}
			if(i==widgets.size()-1)hql.append(")");
			values[1+i]=widgets.get(i).getId();
		}
		 return this.find(hql.toString(), values);
	}
	
	/**
	 * 根据系统获得底层平台小窗体参数值设置
	 * @return
	 */
	public List<WidgetParameterValue> getWidgetParameterValueBySystem(String systemIds,Long companyId){
		List<WidgetParameter> widgets=widgetParameterDao.getWidgetParameterBySystem(systemIds,companyId);
		 StringBuilder hql=new StringBuilder("from WidgetParameterValue wpv where wpv.companyId=? and wpv.userId=0 ");
		 Object[] values=new Object[1];
		if(widgets.size()>0){
			hql.append(" and ");
			values=new Object[1+widgets.size()];
		}
		values[0]=companyId;
		for(int i=0;i<widgets.size();i++){
			if(i==0)hql.append("(");
			hql.append(" wpv.widgetParameter.id=? ");
			if(i<widgets.size()-1){
				hql.append(" or ");
			}
			if(i==widgets.size()-1)hql.append(")");
			values[1+i]=widgets.get(i).getId();
		}
		 return this.find(hql.toString(), values);
	}
	
	public WidgetParameterValue getWidgetParameterValueByValue(String paramValue,Long parameterId){
		StringBuilder vhql = new StringBuilder("FROM WidgetParameterValue wpv WHERE wpv.widgetParameter.id=? and wpv.value=? and wpv.userId=0 ");
		List<WidgetParameterValue> paramValues=this.find(vhql.toString(),parameterId,paramValue);
		if(paramValues.size()>0){
			return paramValues.get(0);
		}
		return null;
	}
	
}
