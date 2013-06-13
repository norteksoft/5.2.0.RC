package com.norteksoft.mms.form.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.entity.GroupHeader;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class GroupHeaderDao extends HibernateDao<GroupHeader, Long>{
	public List<GroupHeader> getGroupHeadersByViewId(Long listViewId){
		return find("from GroupHeader g where g.listViewId=?", listViewId);
	}
	
	public GroupHeader getGroupHeaderByInfo(Long listViewId, String startColumnName,Integer numberOfColumns,String titleText){
		List<GroupHeader> header=find("from GroupHeader g where g.listViewId=? and g.startColumnName=? and g.numberOfColumns=? and g.titleText=?", listViewId,startColumnName,numberOfColumns,titleText);
		if(header.size()>0)return header.get(0);
		return null;
	}
}
