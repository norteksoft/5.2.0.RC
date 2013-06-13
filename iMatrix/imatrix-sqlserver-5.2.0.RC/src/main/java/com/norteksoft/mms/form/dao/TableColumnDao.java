package com.norteksoft.mms.form.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class TableColumnDao extends HibernateDao<TableColumn, Long> {

	public List<TableColumn> getTableColumnByDataTableId(Long dataTableId){
		return find("from TableColumn tc where tc.companyId=? and tc.deleted=? and tc.dataTableId=? order by tc.displayOrder",ContextUtils.getCompanyId(),false,dataTableId);
	}
	/**
	 * 根据数据表id获得数据表字段
	 * @param page
	 * @param dataTableId
	 */
	public void getTableColumnByDataTableId(Page<TableColumn> page,Long dataTableId){
		this.searchPageByHql(page,"from TableColumn tc where tc.companyId=? and tc.deleted=? and tc.dataTableId=? and tc.dataType <> ? and tc.dataType <> ? and tc.dataType <> ? and tc.dataType <> ? order by tc.displayOrder",ContextUtils.getCompanyId(),false,dataTableId,DataType.CLOB,DataType.BLOB,DataType.COLLECTION,DataType.REFERENCE);
	}
	public List<TableColumn> getUnCompanyAllTableColumnByDataTableId(Long dataTableId){
		return findNoCompanyCondition("from TableColumn tc where tc.dataTableId=? and tc.deleted=? order by tc.displayOrder",dataTableId,false);
	}
	
	public TableColumn getTableColumnByColName(Long dataTableId,String columnName){
		List<TableColumn> cols=find("from TableColumn tc where tc.companyId=? and tc.dataTableId=? and tc.name=? and tc.deleted=false order by tc.displayOrder",ContextUtils.getCompanyId(),dataTableId,columnName);
		if(cols.size()>0)return cols.get(0);
		return null;
	}
	
	public void deleteAllTableColumns(Long dataTableId){
		this.createQuery("delete from TableColumn t where t.dataTableId=null or t.dataTableId=?", dataTableId).executeUpdate();
	}
	
	public List<TableColumn> getDeleteColumnByColumnName(String columnName,Long dataTableId) {
		return find("from TableColumn tc where tc.companyId=? and tc.deleted=? and tc.name=? and tc.dataTableId=? order by tc.displayOrder",ContextUtils.getCompanyId(),true,columnName,dataTableId);
	}
	
	public void deleteTableColumnsByTable(Long dataTableId){
		this.createQuery("delete from TableColumn t where t.dataTableId=?", dataTableId).executeUpdate();
	}
}
