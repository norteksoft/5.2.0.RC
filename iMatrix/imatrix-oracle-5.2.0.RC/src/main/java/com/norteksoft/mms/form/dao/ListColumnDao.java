package com.norteksoft.mms.form.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.product.api.impl.WorkflowClientManager;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class ListColumnDao extends HibernateDao<ListColumn, Long> {
	private Log log = LogFactory.getLog(WorkflowClientManager.class);
	
	public List<ListColumn> getColumns(Long viewId){
		String hql = " from ListColumn lc where lc.companyId=? and lc.listView.id=? order by lc.displayOrder";
		return this.find(hql, ContextUtils.getCompanyId(),viewId);
	}
	public List<ListColumn> getUnCompanyColumns(Long viewId){
		String hql = " from ListColumn lc where lc.listView.id=? and (lc.tableColumn is null or (lc.tableColumn is not null and lc.tableColumn.deleted=?)) order by lc.displayOrder";
		return this.findNoCompanyCondition(hql, viewId,false);
	}
	
	public List<Long> getColumnIdsByViewId(Long viewId){
		return this.find("select lc.id from ListColumn lc where lc.companyId=? and lc.listView.id=?", ContextUtils.getCompanyId(),viewId);
	}
	
	public List<ListColumn> getColumnsByViewId(Long viewId){
		return this.find("from ListColumn lc where lc.companyId=? and lc.listView.id=? and lc.visible=? order by lc.displayOrder", ContextUtils.getCompanyId(),viewId,true);
	}
	
	/**
	 * 查询列表显示列
	 */
	public List<ListColumn> getColumnsByViewCode(String code){
		return find("from ListColumn lc where lc.companyId=? and lc.listView.code=? order by lc.displayOrder", ContextUtils.getCompanyId(), code);
	}
	
	/**
	 * 查询所有查询列
	 * @param code
	 * @return
	 */
	public List<ListColumn> getQueryColumnsByCode(String code){
		return find("from ListColumn lc where lc.companyId=? and lc.listView.code=?  and lc.querySettingValue not like ? order by lc.displayOrder", ContextUtils.getCompanyId(), code,"NONE");
	}
	/**
	 * 查询所有查询列(固定查询)
	 * @param code
	 * @return
	 */
	public List<ListColumn> getQueryColumnsByCodeAndFixed(String code){
		return find("from ListColumn lc where lc.companyId=? and lc.listView.code=? and lc.querySettingValue like ?  order by lc.displayOrder", ContextUtils.getCompanyId(), code, "FIXED");
	}
	public List<ListColumn>  getDisplayColumns(Long viewId){
		String hql = " from ListColumn lc where lc.companyId=? and lc.listView.id=? and lc.visible=true order by lc.displayOrder";
		return this.find(hql, ContextUtils.getCompanyId(),viewId);
	}
	
	/**
	 * 查询所有导出列
	 * @param code
	 * @return
	 */
	public List<ListColumn> getExportColumnsByCode(String listCode){
		if(ContextUtils.getCompanyId()==null){
			log.debug("companyId不能为null");
			throw new RuntimeException("companyId不能为null");
		}
		return find("from ListColumn lc where lc.companyId=? and lc.listView.code=? and lc.exportable=? and lc.tableColumn!=null  order by lc.displayOrder", ContextUtils.getCompanyId(), listCode, true);
	}
	
	/**
	 * 获得所有导出列头名称
	 * @param listCode
	 * @return
	 */
	public List<Object> getExportHeadnameByCode(String listCode) {
		return find("select lc.headerName from ListColumn lc where lc.companyId=? and lc.listView.code=? and lc.exportable=? and lc.tableColumn!=null  order by lc.displayOrder", ContextUtils.getCompanyId(), listCode, true);
	}
	/**
	 * 通过数据表字段名获得列表中的字段
	 * @return
	 */
	public ListColumn getListColumnByTbCol(Long viewId,String tbColumnName){
		List<ListColumn> cols=find("from ListColumn lc where lc.companyId=? and lc.listView.id=?  and (lc.tableColumn!=null and lc.tableColumn.name=?) order by lc.displayOrder",ContextUtils.getCompanyId(),viewId,tbColumnName);
		if(cols.size()>0)return cols.get(0);
		return null;
	}
	
	public void deleteAllColumns(Long viewId){
		this.createQuery("delete from ListColumn t where t.listView=null or (t.listView!=null and t.listView.id=?)", viewId).executeUpdate();
	}
	
	public void deleteColumnsByTableColumn(Long tableColumnId){
		this.createQuery("delete from ListColumn t where t.tableColumn!=null and t.tableColumn.id=?", tableColumnId).executeUpdate();
	}
	
	public List<ListColumn> getColumnsByTableColumn(Long tableColumnId){
		return find("from ListColumn t where t.tableColumn!=null and t.tableColumn.id=?", tableColumnId);
	}
	
	public void deleteListColumnsByView(Long viewId){
		this.createQuery("delete from ListColumn t where t.listView!=null and t.listView.id=?", viewId).executeUpdate();
	}
	
	public String getValuesetByTableColumn(Long tableColumnId){
		String hql = "select t.valueSet from ListColumn t where t.tableColumn is not null and t.tableColumn.id=? and t.tableColumn.deleted=? and t.valueSet is not null";
		List<String> valuesets = find(hql, tableColumnId,false);
		if(valuesets.size()>0)return valuesets.get(0);
		return "";
	}
}
