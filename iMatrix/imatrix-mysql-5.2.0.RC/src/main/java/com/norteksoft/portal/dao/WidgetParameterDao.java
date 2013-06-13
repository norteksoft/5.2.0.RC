package com.norteksoft.portal.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.Widget;
import com.norteksoft.portal.entity.WidgetParameter;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class WidgetParameterDao extends HibernateDao<WidgetParameter, Long>{
	@Autowired
	private WidgetDao widgetDao;
	
	public List<WidgetParameter> getWidgetParameters(Long widgetId){
		StringBuilder hql = new StringBuilder("FROM WidgetParameter wp WHERE wp.widget.id=? ");
		return this.find(hql.toString(),widgetId);
	}
	
	public List<WidgetParameter> getAllWidgetParameters(){
		StringBuilder hql = new StringBuilder("FROM WidgetParameter wp");
		return this.find(hql.toString());
	}
	
	public List<WidgetParameter> getWidgetParameterBySystem(String systemIds,Long companyId){
		List<Widget> widgets=widgetDao.getWidgetsBySystem(systemIds,companyId);
		 StringBuilder hql=new StringBuilder("from WidgetParameter wp where wp.companyId=?");
		 Object[] values=new Object[1];
		if(widgets.size()>0){
			hql.append(" and ");
			values=new Object[1+widgets.size()];
		}
		values[0]=companyId;
		for(int i=0;i<widgets.size();i++){
			if(i==0)hql.append("(");
			hql.append(" wp.widget.id=? ");
			if(i<widgets.size()-1){
				hql.append(" or ");
			}
			if(i==widgets.size()-1)hql.append(")");
			values[1+i]=widgets.get(i).getId();
		}
		 return this.find(hql.toString(), values);
	}
	
	public WidgetParameter getWidgetParameterByCode(String code,Long widgetId){
		List<WidgetParameter> params=this.find("from WidgetParameter wp where wp.code=? and wp.widget.id=?", code,widgetId);
		if(params.size()>0)return params.get(0);
		return null;
	}
	
	/**
	 * 获得底层平台小窗体参数
	 * @return
	 */
	public List<WidgetParameter> getAllDefaultWidgetParameters(Long companyId){
		List<Widget> widgets=widgetDao.getDefaultWidgets(companyId);
		StringBuilder hql = new StringBuilder("from WidgetParameter wp where wp.companyId=? ");
		Object[] values=new Object[1];
		if(widgets.size()>0){
			hql.append(" and ");
			values=new Object[1+widgets.size()];
		}
		values[0]=companyId;
		for(int i=0;i<widgets.size();i++){
			if(i==0)hql.append("(");
			hql.append(" wp.widget.id=? ");
			if(i<widgets.size()-1){
				hql.append(" or ");
			}
			if(i==widgets.size()-1)hql.append(")");
			values[1+i]=widgets.get(i).getId();
		}
		 return this.find(hql.toString(), values);
	}

}
