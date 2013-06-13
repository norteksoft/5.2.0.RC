package com.norteksoft.mms.form.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.form.dao.TableColumnDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;

@Service
@Transactional(readOnly = true)
public class TableColumnManager {

	private TableColumnDao tableColumnDao;

	@Autowired
	public void setTableColumnDao(TableColumnDao tableColumnDao) {
		this.tableColumnDao = tableColumnDao;
	}

	/**
	 * 字段保存
	 * 
	 * @param tableColumn
	 */
	@Transactional(readOnly = false)
	public void saveColumn(TableColumn tableColumn, boolean canChange) {
		tableColumn.setCompanyId(ContextUtils.getCompanyId());
		tableColumnDao.save(tableColumn);
//		if(!canChange && !tableColumn.getDataTable().getExistedTable()){
//			jdbcDao.addTableColumn(tableColumn.getDataTable().getTableName(), tableColumn);
//		}
	}

	/**
	 * 批量保存字段
	 * @param dataTable
	 */
	@Transactional(readOnly=false)
	public void saveTableColumns(DataTable dataTable){
		List<Object> list=JsonParser.getFormTableDatas(TableColumn.class);
		for(Object obj:list){
			TableColumn column=(TableColumn)obj;
			column.setCompanyId(ContextUtils.getCompanyId());
			column.setDataTableId(dataTable.getId());
			tableColumnDao.save(column);
		}
	}
	/**
	 * 删除数据表字段
	 * @param columnId
	 */
	@Transactional(readOnly=false)
	public void deleteTableColumn(Long columnId){
		TableColumn column=tableColumnDao.get(columnId);
		column.setDeleted(true);
		tableColumnDao.save(column);
//		tableColumnDao.delete(columnId);
	}
	/**
	 * 彻底删除对应数据表所有的字段
	 * @param dataTableId
	 */
	@Transactional(readOnly=false)
	public void deleteAllTableColumns(Long dataTableId){
		tableColumnDao.deleteAllTableColumns(dataTableId);
	}
	
	public TableColumn getTableColumnByColName(Long dataTableId,String columnName){
		return tableColumnDao.getTableColumnByColName(dataTableId, columnName);
	}
	
	public List<TableColumn> getUnCompanyAllTableColumnByDataTableId(Long dataTableId){
		return tableColumnDao.getUnCompanyAllTableColumnByDataTableId(dataTableId);
	}
	
	public List<TableColumn> getTableColumnByDataTableId(Long dataTableId){
		return tableColumnDao.getTableColumnByDataTableId(dataTableId);
	}
	
	public List<TableColumn> getDeleteColumnByColumnName(String columnName,Long dataTableId) {
		return tableColumnDao.getDeleteColumnByColumnName(columnName,dataTableId);
	}
}
