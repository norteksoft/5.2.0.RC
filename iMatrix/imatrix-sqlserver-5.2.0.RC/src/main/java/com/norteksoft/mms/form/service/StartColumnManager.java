package com.norteksoft.mms.form.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.base.utils.view.ComboxValues;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.product.api.ApiFactory;
@Service
@Transactional(readOnly=true)
public class StartColumnManager implements ComboxValues{
	public Map<String, String> getValues(Object entity) {
		Map<String,String> map=new HashMap<String, String>();
		if(entity!=null){
			ListView view=(ListView)entity;
			StringBuilder result=new StringBuilder();
			result.append("'':'")
			 .append("请选择")
			 .append("'").append(",");
			List<ListColumn> columnList=view.getColumns();
			for(ListColumn column:columnList){
				if(column.getTableColumn()!=null&&column.getVisible()){
					result.append("'").append(column.getTableColumn().getName()).append("':")
					.append("'").append(getInternation(column.getHeaderName())).append("'").append(",");
				}
			}
			if(result.charAt(result.length()-1)==','){
				result.delete(result.length()-1, result.length());
			}
			map.put("startColumnName", result.toString());
		}
		return map;
	}
	
	 public String getInternation(String code){
		 return ApiFactory.getSettingService().getInternationOptionValue(code);
	 }

}
