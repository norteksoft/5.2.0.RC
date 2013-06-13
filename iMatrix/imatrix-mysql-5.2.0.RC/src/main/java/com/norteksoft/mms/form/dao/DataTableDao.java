package com.norteksoft.mms.form.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.product.api.impl.WorkflowClientManager;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class DataTableDao extends HibernateDao<DataTable, Long> {
	private Log log = LogFactory.getLog(WorkflowClientManager.class);
	
	public void findAllDataTable(Page<DataTable> tables){
		String hql = "from DataTable dt where dt.companyId = ? order by dt.createdTime desc";
		findPage(tables, hql, ContextUtils.getCompanyId());
	}
	public void findSystemAllDataTable(Page<DataTable> tables,Long menuId){
		String hql ="from DataTable dt where dt.companyId = ? and dt.menuId = ? and dt.entityName!=null order by dt.createdTime desc";
		this.searchPageByHql(tables, hql, ContextUtils.getCompanyId(),menuId);
	}
	
	public void findSystemDefaultDataTable(Page<DataTable> tables,Long menuId){
		String hql ="from DataTable dt where dt.companyId = ? and dt.menuId = ? and dt.entityName is null order by dt.createdTime desc";
		this.searchPageByHql(tables, hql, ContextUtils.getCompanyId(),menuId);
	}
	
	public void findAllEnabledDataTable(Page<DataTable> tables){
		String hql = "from DataTable dt where dt.companyId = ? and dt.tableState=? order by dt.createdTime desc";
		findPage(tables, hql, ContextUtils.getCompanyId(), DataState.ENABLE);
	}
	
	public List<DataTable> getEnabledDataTables() {
		String hql = "from DataTable dt where (dt.tableState=? or dt.tableState=? ) and dt.companyId=? order by dt.createdTime";
		return this.find(hql, DataState.ENABLE,DataState.DISABLE,ContextUtils.getCompanyId());
	}
	
	public List<DataTable> getAllEnabledDataTables() {
		String hql = "from DataTable dt where dt.tableState=? and dt.companyId=? order by dt.createdTime";
		return this.find(hql, DataState.ENABLE,ContextUtils.getCompanyId());
	}
	
	public DataTable getDataTableByEntity(String entityName){
		String hql = "from DataTable dt where dt.entityName=? and dt.companyId=?";
		List<DataTable> tables = find(hql, entityName, ContextUtils.getCompanyId());
		if(tables.size()>0){
			return tables.get(0);
		}else{
			return null;
		}
	}
	
	public DataTable findDataTableByName(String name){
		String hql = "from DataTable dt where dt.name=? and dt.companyId=?";
		if(ContextUtils.getCompanyId()==null){
			log.debug("companyId不能为null");
			throw new RuntimeException("companyId不能为null");
		}
		List<DataTable> tables = find(hql, name, ContextUtils.getCompanyId());
		if(tables.size()>0){
			return tables.get(0);
		}else{
			return null;
		}
	}
	public List<DataTable> getStandardDataTables() {
		String hql = "from DataTable dt where entityName is not null  and dt.name<>'null' and dt.companyId=? order by dt.createdTime";
		return this.find(hql, ContextUtils.getCompanyId());
	}
	public List<DataTable> getDefaultDataTables() {
		String hql = "from DataTable dt where entityName is null  and dt.name<>'null' and dt.companyId=? order by dt.createdTime";
		return this.find(hql, ContextUtils.getCompanyId());
	}
	
	public List<DataTable> getAllDataTablesByMenu(Long menuId){
		String hql = "from DataTable dt where dt.companyId = ? and dt.menuId=? order by dt.createdTime desc";
		return this.find(hql,ContextUtils.getCompanyId(),menuId);
	}
	public List<DataTable> getUnCompanyAllDataTablesByMenu(Long menuId){
		String hql = "from DataTable dt where dt.menuId=? order by dt.createdTime desc";
		return this.findNoCompanyCondition(hql,menuId);
	}
}
