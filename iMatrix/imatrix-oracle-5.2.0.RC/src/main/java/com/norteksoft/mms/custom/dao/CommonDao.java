package com.norteksoft.mms.custom.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.dao.ListViewDao;
import com.norteksoft.mms.form.dao.TableColumnDao;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Repository
public class CommonDao extends HibernateDao<Object, Long>{
	
	
	@Autowired
	private ListColumnDao listColumnDao;
	
	@Autowired
	private ListViewDao listViewDao;
	
	@Autowired
	private JdbcSupport jdbcDao;
	
	@Autowired
	private TableColumnDao tableColumnDao;
	
	/**
	 * 根据列表编号查询数据
	 * @param page
	 * @param listCode
	 * @return
	 */
	public Page<Object> list(Page<Object> page, String listCode){
		
		ListView view = listViewDao.getListViewByCode(listCode);
		
		String fieldSql = getSelectPartByListCode(listCode);
		
		StringBuilder sql=new StringBuilder();
		sql.append("select ");
		sql.append(fieldSql);
		sql.append(" from ");
		sql.append(view.getDataTable().getName()).append(" ");
		sql.append("where company_id=? ");
		return this.searchPageBySql(page, sql.toString(), ContextUtils.getCompanyId());
	}
	
	public Page<Object> listEntity(Page<Object> page, String entityName){
		return this.searchPageByHql(page, "from " + entityName + " t");
	}
	
	private String getSelectPartByListCode(String listCode){
		List<ListColumn> columns = listColumnDao.getColumnsByViewCode(listCode);
		StringBuffer columnsStr = new StringBuffer();
		String columnName = null;
		for (int i = 0; i < columns.size(); i++) {
			if(i != 0){
				columnsStr.append(",");
			}
			columnName = columns.get(i).getTableColumn().getName();
			if(!FormHtmlParser.isDefaultField(columnName)){
				columnsStr.append("dt_");
			}
			columnsStr.append(columns.get(i).getTableColumn().getName());
		}
		return columnsStr.toString();
	}
	
	/**
	 * 查询数据 
	 * @param tableName 表名
	 * @param id 数据ID
	 * @return
	 */
	public Object getDateById(String tableName, Long id){
		return jdbcDao.getDataMap(tableName, id);
	}

	public Object getEntityById(String entityName, Long id){
		List<Object> objs = this.find("from " + entityName + " t where t.id=?", id);
		Object obj = null;
		if(objs.size() == 1){
			obj = objs.get(0);
		}
		return obj;
	}
	
	public Long save(Map<String,String[]> parameter, FormView form, List<FormControl> fields){
		return jdbcDao.insertTable(parameter, form, fields);
	}
	
	public Long update(Map<String,String[]> parameter, FormView form, List<FormControl> fields, Long id){
		return jdbcDao.updateTable(parameter, form, fields, id);
	}
	
	public void delete(String tableName, List<Long> ids){
		jdbcDao.deleteDatas(tableName, ids);
	}
	public void delete(String tableName,Long id){
		jdbcDao.deleteData(tableName, id);
	}

	public Map<String, Object> getAmountTotal(List<String> names) {
		String listCode=Struts2Utils.getParameter("_list_code");
		ListView listView=listViewDao.getListViewByCode(listCode);
		StringBuilder sql=new StringBuilder();
		StringBuilder field=new StringBuilder();
		String name="";
		List<TableColumn> filedType=new ArrayList<TableColumn>();
		for(int i=0;i<names.size();i++){
			name=names.get(i);
			if(!FormHtmlParser.isDefaultField(names.get(i))){
				name="dt_"+name;
			}
			List<TableColumn> tableColumns=tableColumnDao.find("from TableColumn t where t.companyId=? and t.dataTableId=? and t.dbColumnName=?",ContextUtils.getCompanyId(),listView.getDataTable().getId(),name);
			TableColumn tc=tableColumns.get(0);
			filedType.add(tc);
			if(isTotalType(tc)){
				if(StringUtils.isNotEmpty(field.toString())){
					field.append(",");
				}
				field.append("sum(p.");
				field.append(name);
				field.append(")");
			}
		}
		Object[] values=null;
		Object value=null;
		if(StringUtils.isNotEmpty(field.toString())){
			sql.append("select ");
			sql.append(field.toString());
			sql.append(" from ");
			sql.append(listView.getDataTable().getName());
			sql.append(" p ");
			sql.append(" where p.company_id=? ");
			if(field.toString().contains(",")){
				values=(Object[])findBySql(sql.toString(),ContextUtils.getCompanyId()).get(0);
			}else{
				value=findBySql(sql.toString(),ContextUtils.getCompanyId()).get(0);
			}
		}
		Map<String,Object> totalValues=new HashMap<String, Object>();
		int j=0;
		for(int i=0;i<filedType.size();i++){
			if(isTotalType(filedType.get(i))){
				if(field.toString().contains(",")){
					totalValues.put(names.get(i), values[j]);
					j++;
				}else{
					totalValues.put(names.get(i), value);
				}
			}else{
				totalValues.put(names.get(i), 0);
			}
		}
		return totalValues;
	}
	
	private boolean isTotalType(TableColumn tc){
		return tc.getDataType()==DataType.AMOUNT||tc.getDataType()==DataType.DOUBLE||tc.getDataType()==DataType.FLOAT||tc.getDataType()==DataType.INTEGER||tc.getDataType()==DataType.NUMBER||tc.getDataType()==DataType.LONG;
	}
}
