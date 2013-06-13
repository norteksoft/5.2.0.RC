package com.norteksoft.mms.form.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.dao.ListViewDao;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;

@Service
@Transactional(readOnly=true)
public class ListColumnManager {
	
	private ListColumnDao listColumnDao;
	private ListViewDao listViewDao;
	@Autowired
	public void setListColumnDao(ListColumnDao listColumnDao) {
		this.listColumnDao = listColumnDao;
	}
	@Autowired
	public void setListViewDao(ListViewDao listViewDao) {
		this.listViewDao = listViewDao;
	}
	public List<ListColumn> getColumns(Long viewId){
		return listColumnDao.getColumns(viewId);
	}
	public List<ListColumn> getUnCompanyColumns(Long viewId){
		return listColumnDao.getUnCompanyColumns(viewId);
	}
	@Transactional(readOnly=false)
	public void save(Long viewId){
		List<Object> list=JsonParser.getFormTableDatas(ListColumn.class);
		for(Object obj:list){
			ListColumn column=(ListColumn)obj;
			column.setCompanyId(ContextUtils.getCompanyId());
			column.setListView(listViewDao.get(viewId));
			listColumnDao.save(column);
		}
	}
	
	@Transactional(readOnly=false)
	public void deleteByViewId(Long viewId){
		List<Long> ids=listColumnDao.getColumnIdsByViewId(viewId);
		for(Long id:ids){
			listColumnDao.delete(id);
		}
	}
	
	public List<ListColumn> getColumnsByViewId(Long viewId){
		return listColumnDao.getColumnsByViewId(viewId);
	}

	/**
	 * 列表标签使用
	 */
	public List<ListColumn> getColumnsByViewCode(String code){
		return listColumnDao.getColumnsByViewCode(code);
	}
	
	/**
	 * 供其他项目使用的
	 */
	public String getSelectColumnsByViewCode(String code){
		List<ListColumn> columns = listColumnDao.getColumnsByViewCode(code);
		StringBuffer columnsStr = new StringBuffer();
		for (int i = 0; i < columns.size(); i++) {
			if(i+1==columns.size()){
				columnsStr.append(columns.get(i).getTableColumn().getName());
			}else{
				columnsStr.append(columns.get(i).getTableColumn().getName() + ", ");
			}
		}
		return columnsStr.toString();
	}
	
	/**
	 * 根据ID查询列
	 */
	public ListColumn getColumn(Long id){
		return listColumnDao.get(id);
	}
	
	/**
	 * 查询所有查询列
	 * @param code
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<ListColumn> getQueryColumnsByCode(String code){
		return listColumnDao.getQueryColumnsByCode(code);
	}
	/**
	 * 查询所有显示的列
	 * @return
	 */
	public List<ListColumn> getDisplayColumns(Long viewId){
		return listColumnDao.getDisplayColumns(viewId);
	}
	@Transactional(readOnly=false)
	public void deleteColumn(Long columnId){
		listColumnDao.delete(columnId);
	}
	
	/**
	 * 查询所有导出列
	 * @param code
	 * @return
	 */
	public List<ListColumn> getExportColumnsByCode(String listCode){
		return listColumnDao.getExportColumnsByCode(listCode);
	}
	
	/**
	 * 获得所有导出列头名称
	 * @param listCode
	 * @return
	 */
	public List<Object> getExportHeadnameByCode(String listCode) {
		return listColumnDao.getExportHeadnameByCode(listCode);
	}
	@Transactional(readOnly=false)
	public void saveColumn(ListColumn column){
		listColumnDao.save(column);
	}
	/**
	 * 通过数据表字段名获得列表中的字段
	 * @return
	 */
	public ListColumn getListColumnByTbCol(Long viewId,String tbColumnName){
		return listColumnDao.getListColumnByTbCol(viewId, tbColumnName);
	}
	
	/**
	 * 彻底删除对应数据表所有的字段
	 * @param dataTableId
	 */
	@Transactional(readOnly=false)
	public void deleteAllColumns(Long listViewId){
		listColumnDao.deleteAllColumns(listViewId);
	}
	@Transactional(readOnly=false)
	public void deleteColumnsByTableColumn(Long tableColumnId){
		listColumnDao.deleteColumnsByTableColumn(tableColumnId);
	}
	public String getValuesetByTableColumn(Long tableColumnId) {
		return listColumnDao.getValuesetByTableColumn(tableColumnId);
	}
}
