package com.norteksoft.product.api;

import java.util.List;
import java.util.Map;

import com.norteksoft.mms.base.ExportDynamicColumnValues;
import com.norteksoft.mms.base.utils.view.DynamicColumnDefinition;
import com.norteksoft.mms.base.utils.view.ExportData;
import com.norteksoft.mms.base.utils.view.GridColumnInfo;
import com.norteksoft.product.api.entity.FormView;
import com.norteksoft.product.api.entity.ListView;
import com.norteksoft.product.api.entity.Menu;
import com.norteksoft.product.orm.Page;

public interface MmsService {
	/**
	 * 删除自定义的列表，如demo中的“模板”
	 * @param code
	 */
	public void deleteCustomListView(String code);
	
	/**
	 * 查询所有的一级菜单
	 * @return
	 */
	public List<Menu> getTopMenus();
	
	/**
	 * 根据菜单编号查询一级菜单
	 * @param code
	 * @return
	 */
	public Menu getTopMenu(String code);
	
	/**
	 * 根据系统编码的集合获得列表集合
	 * @param sysCodes 系统编码集合
	 * @return
	 */
	public List<ListView> getListViews(String... sysCodes);
	/**
	 * 根据列表编码获得列表
	 * @param code
	 * @return
	 */
	public ListView getListViewByCode(String code);
	/**
	 * 根据列表编码获得列表
	 * @param code
	 * @return
	 */
	public FormView getFormViewByCode(String code,Integer version);
	/**
	 * 保存用户自定义的列表
	 * @param code 列表编码
	 * @param name 列表名称
	 * @param tableName 对应的数据表名称
	 */
	public void saveColums(String code,String name,String tableName);
	/**
	 * 根据列表code获得colNames和colModel
	 * @param code
	 */
	public GridColumnInfo getGridColumnInfo(String code);
	/**
	 * 获得动态列的实体集合
	 * @return
	 */
	public Map<String,DynamicColumnDefinition> getDynamicColumnName();
	/**
	 * 获得带有动态列导出数据
	 * @param page
	 * @param listCode
	 * @return
	 */
	public ExportData getDynamicColumnExportData(Page<?> page,ExportDynamicColumnValues exportDynamicColumnValues);
	/**
	 * 获得导出数据
	 * @return
	 */
	public ExportData getExportData(Page<?> page,String listCode);
	/**
	 * 根据列表code获得列名
	 * @param code
	 * @return
	 */
	public String getColumnsByCode(String code);
	/**
	 * 根据列表code获得导出列名
	 * @param code
	 * @return
	 */
	public String getExportColumnsByCode(String code);
	/**
	 * 保存列表实体
	 * @param view
	 */
	public void saveView(ListView view);
	
	/**
	 * 获得动态列名
	 * @return
	 */
	public String[] getDynamicColumnNames();
	/**
	 * 根据数据表名称获得默认列表
	 * @param tableName
	 * @return
	 */
	public ListView getDefaultListViewByDataTable(String tableName);
	
	/**
	 * 根据表单编号取表单中签章字段的list
	 * @param code
	 * @return
	 */
	public List<String> getSignatureFieldByFormViewCode(String code);
	
	/**
	 * 根据表单编号和版本号取表单中签章字段的list
	 * @param code
	 * @return
	 */
	public List<String> getSignatureFieldByFormViewCode(String code,Integer version);
	
}
