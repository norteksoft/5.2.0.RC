package com.norteksoft.mms.form.service;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.entity.ImportColumn;
import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.impl.DefaultDataImporterCallBack;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class ImportDataTableManager extends DefaultDataImporterCallBack{
	@Autowired
	private DataTableManager dataTableManager;
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private ImportListViewManager importListViewManager;
	@Autowired
	private TableColumnManager tableColumnManager;
	
	public String saveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
		if("MMS_DATA_TABLE".equals(importDefinition.getCode())){
			saveDataTable(rowValue,importDefinition);
		}else if("MMS_TABLE_COLUMN".equals(importDefinition.getCode())){
			saveTableColumn(rowValue,importDefinition);
		}
		return "";
	}

	private void saveTableColumn(String[] rowValue,ImportDefinition importDefinition) {
		DataTable table=dataTableManager.getDataTableByTableName(rowValue[0]);
		TableColumn column=null;
		if("true".equals(StringUtils.lowerCase(rowValue[2]))){//如是删除的字段则新建
			column=new TableColumn();
		}else{//如不是删除的字段
			column=tableColumnManager.getTableColumnByColName(table.getId(),rowValue[1]);
			if(column==null){//该字段不存在，则新建
				column=new TableColumn();
			}
		}
		int i=0;
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			if(i==0){
				column.setDataTableId(table.getId());
			}else{
				if(i<rowValue.length && StringUtils.isNotEmpty(rowValue[i])){
					String enumname="";
					if(i==6)
						enumname="com.norteksoft.mms.form.enumeration.DataType";
					importListViewManager.setValue(column,importColumn,rowValue[i],enumname);
				}
			}
			i++;
		}
		column.setCreatedTime(new Date());
		column.setCreator(ContextUtils.getLoginName());
		column.setCreatorName(ContextUtils.getUserName());
		column.setCompanyId(ContextUtils.getCompanyId());
		tableColumnManager.saveColumn(column,false);
	}

	private void saveDataTable(String[] rowValue,ImportDefinition importDefinition) {
		DataTable table=dataTableManager.getDataTableByTableName(rowValue[0]);
		if(table==null){
			table=new DataTable();
		}
		int i=0;
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			if(i==4){
				Menu menu=menuManager.getMenuByCode(rowValue[i]);
				table.setMenuId(menu.getId());
			}else{
				if(i<rowValue.length && StringUtils.isNotEmpty(rowValue[i])){
					String enumname="";
					if(i==3)
						enumname="com.norteksoft.product.enumeration.DataState";
					importListViewManager.setValue(table,importColumn,rowValue[i],enumname);
				}
					
			}
			i++;
		}
		table.setCreatedTime(new Date());
		table.setCreator(ContextUtils.getLoginName());
		table.setCreatorName(ContextUtils.getUserName());
		table.setCompanyId(ContextUtils.getCompanyId());
		dataTableManager.saveDataTable(table);
	}
}
