package com.norteksoft.mms.form.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.base.utils.view.ComboxValues;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.api.ApiFactory;
@Service
@Transactional(readOnly=true)
public class ListViewColumnManager implements ComboxValues{
	@Autowired
	private TableColumnManager tableColumnManager;
	public Map<String, String> getValues(Object entity) {
		Map<String,String> map=new HashMap<String, String>();
		if(entity!=null){
			ListView view=(ListView)entity;
			StringBuilder result=new StringBuilder();
			result.append("'':'")
			 .append("请选择")
			 .append("'").append(",");
			DataTable table=view.getDataTable();
			List<TableColumn> columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
			for(TableColumn column:columns){
				if(column.getDataType()!=DataType.CLOB&&column.getDataType()!=DataType.BLOB){
					result.append("'").append(column.getId()).append("':")
					.append("'").append(getInternation(column.getAlias())).append("'").append(",");
				}
			}
			result.append("'_temporary':'占位符'");
			map.put("tableColumn", result.toString());
		}
		return map;
	}
	
	public String getInternation(String code){
		 return ApiFactory.getSettingService().getInternationOptionValue(code);
	 }

}
