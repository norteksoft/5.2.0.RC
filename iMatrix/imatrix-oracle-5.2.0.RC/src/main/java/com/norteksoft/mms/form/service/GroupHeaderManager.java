package com.norteksoft.mms.form.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.form.dao.GroupHeaderDao;
import com.norteksoft.mms.form.entity.GroupHeader;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;

@Service
@Transactional(readOnly=true)
public class GroupHeaderManager {
	@Autowired
	private GroupHeaderDao groupHeaderDao;
	
	@Transactional(readOnly=false)
	public void save(Long viewId) {
		List<Object> list=JsonParser.getFormTableDatas(GroupHeader.class);
		for(Object obj:list){
			GroupHeader groupHeader=(GroupHeader)obj;
			groupHeader.setCompanyId(ContextUtils.getCompanyId());
			groupHeader.setListViewId(viewId);
			groupHeaderDao.save(groupHeader);
		}
	}
	
	@Transactional(readOnly=false)
	public void delete(Long id) {
		groupHeaderDao.delete(id);
	}
	
	public List<GroupHeader> getGroupHeadersByViewId(Long listViewId){
		return groupHeaderDao.getGroupHeadersByViewId(listViewId);
	}
	
	public GroupHeader getGroupHeaderByInfo(Long listViewId, String startColumnName,Integer numberOfColumns,String titleText){
		return groupHeaderDao.getGroupHeaderByInfo(listViewId, startColumnName, numberOfColumns, titleText);
	}
	@Transactional(readOnly=false)
	public void save(GroupHeader header){
		groupHeaderDao.save(header);
	}

}
