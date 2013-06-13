package com.norteksoft.mms.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.norteksoft.mms.base.utils.view.DynamicColumnDefinition;
import com.norteksoft.mms.base.utils.view.ExportData;
import com.norteksoft.mms.base.utils.view.GridColumnInfo;
import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.dao.ListViewDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.ListColumnManager;
import com.norteksoft.mms.module.dao.MenuDao;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.product.api.impl.WorkflowClientManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Service
@Transactional(readOnly=true)
public class MmsUtil {
	private Log log = LogFactory.getLog(WorkflowClientManager.class);
	@Autowired
	private ListViewDao viewDao;
	@Autowired
	private ListViewDao listViewDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private ListColumnDao listColumnDao;
	@Autowired
	private FormHtmlParser formHtmlParser;
	@Autowired
	private DataTableManager dataTableManager;
	
	/**
	 * 根据系统编码的集合获得列表集合
	 * @param sysCodes 系统编码集合
	 * @return
	 */
	public List<ListView> getListViews(String... sysCodes){
		return viewDao.getListViewsBySystem(sysCodes);
	}
	/**
	 * 根据列表编码获得列表
	 * @param code
	 * @return
	 */
	public ListView getListViewByCode(String code){
		return viewDao.getListViewByCode(code);
	}
	/**
	 * 保存用户自定义的列表
	 * @param code 列表编码
	 * @param name 列表名称
	 * @param sysCode 系统编码
	 */
	@Transactional(readOnly=false)
	public void saveColums(String code,String name,String tableName){
		Assert.notNull(ContextUtils.getSystemCode(), "systemCode不能为null");
		Assert.notNull(ContextUtils.getCompanyId(), "companyId不能为null");
		Menu menu=menuDao.getRootMenuByCode(ContextUtils.getSystemCode(),ContextUtils.getCompanyId());
		DataTable table=dataTableManager.getDataTableByTableName(tableName);
		if(menu!=null){
			ListView view=listViewDao.getListViewByCode(code);
			if(view!=null){
				view.setName(name);
			}else{
				view=new ListView();
				view.setCode(code);
				view.setName(name);
				view.setCreator(ContextUtils.getLoginName());
				view.setCreatorName(ContextUtils.getUserName());
				view.setCreatedTime(new Date());
				view.setDataTable(table);
			}
			view.setMenuId(menu.getId());
			viewDao.save(view);
			List<ListColumn> items=new ArrayList<ListColumn>();
			List<Object> list=JsonParser.getFormTableDatas(ListColumn.class);
			for(Object obj:list){
				ListColumn column=(ListColumn)obj;
				column.setCompanyId(ContextUtils.getCompanyId());
				column.setListView(view);
				listColumnDao.save(column);
				items.add(column);
			}
			view.setColumns(items);
		}
	}
	
	/**
	 * 根据列表code获得colNames和colModel
	 * @param code
	 */
	public GridColumnInfo getGridColumnInfo(String code){
		ListView listView=viewDao.getListViewByCode(code);
		if(listView==null){
			log.debug("ListView实体不能为null");
			throw new RuntimeException("ListView实体不能为null");
		}
		List<ListColumn> columns=listView.getColumns();
		GridColumnInfo gridColumnInfo=new GridColumnInfo();
		StringBuilder colNames=new StringBuilder();
		StringBuilder colModel=new StringBuilder();
		if(columns!=null&&columns.size()>0){
			colNames.append("[");
			colModel.append("[");
			int index=0;
			for(ListColumn column:columns){
				String vs=formHtmlParser.getValueSet(column,null,null);
				column.setOptionSet(vs);
				colNames.append(getColNames(column));
				
				if(column.getTableColumn()!=null){
					colModel.append(getColModel(column));
				}else{
					colModel.append(getTemporaryColModel(column,index));
				}
				index++;
			}
			if(colNames.charAt(colNames.length()-1)==','){
				colNames.delete(colNames.length()-1, colNames.length());
			}
			if(colModel.charAt(colModel.length()-1)==','){
				colModel.delete(colModel.length()-1, colModel.length());
			}
			colNames.append("]");
			colModel.append("]");
			gridColumnInfo.setColNames(colNames.toString());
			gridColumnInfo.setColModel(colModel.toString());
		}
		return gridColumnInfo;
	}
	
	private String getTemporaryColModel(ListColumn column,int index) {
		StringBuilder colModel=new StringBuilder();
		
		colModel.append("{name:'_temporary");
		colModel.append(index);
		colModel.append("',index:'_temporary");
		colModel.append(index);
		colModel.append("'");
		if(StringUtils.isNotEmpty(column.getFormat())&&column.getFormat().indexOf("func:")!=-1){
			String functionName=column.getFormat().replace("func:", "");
			colModel.append(",formatter:");
			colModel.append(functionName);
		}
		if(StringUtils.isNotEmpty(column.getHeadStyle())){
			colModel.append(",width:");
			colModel.append(column.getHeadStyle());
		}
		if(column.getSortable()){
			colModel.append(",sortable:true");
		}else{
			colModel.append(",sortable:false");
		}
		if(column.getVisible()){
			colModel.append(",hidden:false");
		}else{
			colModel.append(",hidden:true");
		}
		colModel.append("},");
		return colModel.toString();
	}

	/**
	 * 根据列设置获得表格表体信息
	 * @param column
	 * @return
	 */
	private String getColModel(ListColumn column) {
		StringBuilder colModel=new StringBuilder();
		String columnName=column.getTableColumn().getName();
		colModel.append("{name:'");
		colModel.append(columnName);
		colModel.append("',index:'");
		colModel.append(columnName);
		colModel.append("'");
		if(StringUtils.isNotEmpty(column.getHeadStyle())){
			colModel.append(",width:");
			colModel.append(column.getHeadStyle());
		}
		if(StringUtils.isNotEmpty(column.getFormat())){
			String formatSet=column.getFormat();
			if(formatSet.startsWith("func:")){
				colModel.append(",formatter:");
				colModel.append(formatSet.substring(formatSet.indexOf(":")+1, formatSet.length()));
			}
		}
		if(column.getEditable()){
			colModel.append(",editable:true");
			String editRules=getEditRules(column);
			if(StringUtils.isNotEmpty(editRules)){
				colModel.append(",editrules:{");
				colModel.append(editRules);
				colModel.append("}");
			}
		}
		if(StringUtils.isNotEmpty(column.getValueSet())){
			colModel.append(",edittype:'select'");
			colModel.append(",formatter:'select'");
			colModel.append(",editoptions:{value:{");
			colModel.append(column.getOptionSet());
			colModel.append("}}");
		}else if(column.getTableColumn().getDataType().equals("BOOLEAN")){
			colModel.append(",edittype:'checkbox'");
			colModel.append(",formatter:formatCheckbox");
			colModel.append(",unformat:unFormatCheckbox");
			colModel.append(",editoptions: {value:\"true:false\"}");
		}else if(column.getControlValue()!=null && column.getControlValue().equals("TEXTAREA")){
			colModel.append(",edittype:'textarea'");
		}else{
			if(StringUtils.isNotEmpty(column.getFormat())){
				packagingFormatSetting(colModel,column.getFormat());
			}
		}
		if(column.getVisible()){
			colModel.append(",hidden:false");
		}else{
			colModel.append(",hidden:true");
		}
		colModel.append("},");
		return colModel.toString();
	}
	
	/**
	 * 格式设置
	 * @param colModel
	 * @param column
	 */
	private void packagingFormatSetting(StringBuilder colModel,String formatSetting) {
		if(formatSetting.indexOf("$#,##")!=-1){
			colModel.append(",formatter:'currency'");
			if(formatSetting.indexOf(".")!=-1){
				colModel.append(",formatoptions:{decimalSeparator:'.', thousandsSeparator: ',', decimalPlaces: ");
				colModel.append(formatSetting.length()-7);
				colModel.append(", prefix: '$'}");
			}else{
				colModel.append(",formatoptions:{decimalSeparator:' ', thousandsSeparator: ',', decimalPlaces: 0, prefix: '$'}");
			}
		}else if(formatSetting.indexOf("￥#,##")!=-1){
			colModel.append(",formatter:'currency'");
			if(formatSetting.indexOf(".")!=-1){
				colModel.append(",formatoptions:{decimalSeparator:'.', thousandsSeparator: ',', decimalPlaces: ");
				colModel.append(formatSetting.length()-7);
				colModel.append(", prefix: '￥'}");
			}else{
				colModel.append(",formatoptions:{decimalSeparator:' ', thousandsSeparator: ',', decimalPlaces: 0, prefix: '￥'}");
			}
		}else if(formatSetting.indexOf("%")!=-1){
			colModel.append(",formatter:'currency'");
			if(formatSetting.indexOf(".")!=-1){
				colModel.append(",formatoptions:{decimalSeparator:'00.', thousandsSeparator: '', decimalPlaces: ");
				colModel.append(formatSetting.length()-3);
				colModel.append(", suffix: '%'}");
			}else{
				colModel.append(",formatoptions:{decimalSeparator:'00', thousandsSeparator: '', decimalPlaces: 0, prefix: '%'}");
			}
		}else if(formatSetting.equals("yyyy-m-d")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y-m-d'}");
		}else if(formatSetting.equals("yyyy-m-d hh:mm:ss")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y-m-d H:i:s'}");
		}else if(formatSetting.equals("yyyy-m")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y-m'}");
		}else if(formatSetting.equals("m-d")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'m-d'}");
		}else if(formatSetting.equals("yyyy年m月d日")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y年m月d日'}");
		}else if(formatSetting.equals("yyyy年m月d日hh时mm分ss秒")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y年m月d日H时i分s秒'}");
		}else if(formatSetting.equals("yyyy年m月")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y年m月'}");
		}else if(formatSetting.equals("m月d日")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'m月d日'}");
		}else if(formatSetting.equals("h:mm")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H:i'}");
		}else if(formatSetting.equals("h:mm:ss")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H:i:s'}");
		}else if(formatSetting.equals("h时mm分")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H时i分'}");
		}else if(formatSetting.equals("h时mm分ss秒")){
			colModel.append(",formatter:'date'");
			colModel.append(",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H时i分s秒'}");
		}else if(formatSetting.indexOf("func:")!=-1){
			colModel.append(",formatter:");
			colModel.append(formatSetting.replace("func:", ""));
		}else{
			if(formatSetting.indexOf("#,##")!=-1){
				colModel.append(",formatter:'number'");
				if(formatSetting.indexOf(".")!=-1){
					colModel.append(",formatoptions:{decimalSeparator:'.', thousandsSeparator: ',', decimalPlaces:");
					colModel.append(formatSetting.length()-6);
					colModel.append(", defaultValue: '0.00'}");
				}else{
					colModel.append(",formatoptions:{decimalSeparator:' ', thousandsSeparator: ',', decimalPlaces: 0, defaultValue: '0'}");
				}
			}else{
				colModel.append(",formatter:'number'");
				if(formatSetting.indexOf(".")!=-1){
					colModel.append(",formatoptions:{decimalSeparator:'.', thousandsSeparator: '', decimalPlaces:");
					colModel.append(formatSetting.length()-2);
					colModel.append(", defaultValue: '0.00'}");
				}else{
					colModel.append(",formatoptions:{decimalSeparator:' ', thousandsSeparator: '', decimalPlaces: 0, defaultValue: '0'}");
				}
			}
		}
	}
	/**
	 * 获得编辑规则
	 * @param column
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String getEditRules(ListColumn column) {
		StringBuilder editRules=new StringBuilder();
		if(StringUtils.isNotEmpty(column.getEditRules())){
			editRules.append(column.getEditRules());
		}
		if(DataType.NUMBER.equals(column.getTableColumn().getDataType())||DataType.AMOUNT.equals(column.getTableColumn().getDataType())||DataType.DOUBLE.equals(column.getTableColumn().getDataType())||DataType.FLOAT.equals(column.getTableColumn().getDataType())){
			if(StringUtils.isNotEmpty(editRules.toString())){
				editRules.append(",");
			}
			editRules.append("number:true");
		}else if(DataType.INTEGER.equals(column.getTableColumn().getDataType())||DataType.LONG.equals(column.getTableColumn().getDataType())){
			if(StringUtils.isNotEmpty(editRules.toString())){
				editRules.append(",");
			}
			editRules.append("integer:true");
		}
		return editRules.toString();
	}
	/**
	 * 根据列设置获得表格表头值
	 * @param column
	 * @return
	 */
	private String getColNames(ListColumn column) {
		StringBuilder colNames=new StringBuilder();
		colNames.append("'");
		colNames.append(formHtmlParser.getInternation(column.getHeaderName()));
		colNames.append("',");
		return colNames.toString();
	}
	
	/**
	 * 获得动态列的实体集合
	 * @return
	 */
	public Map<String,DynamicColumnDefinition> getDynamicColumnName(){
		String dynamicColumns=Struts2Utils.getParameter("dynamicColumns");
		
		return JsonParser.json2Map(String.class, DynamicColumnDefinition.class, dynamicColumns);
	}
	
	/**
	 * 获得带有动态列导出数据
	 * @param page
	 * @param listCode
	 * @return
	 */
	public ExportData getDynamicColumnExportData(Page<?> page,ExportDynamicColumnValues exportDynamicColumnValues){
		Assert.notNull(exportDynamicColumnValues, "ExportDynamicColumnValues不能为null");
		ExportData exportData=new ExportData();
		String listCode=Struts2Utils.getParameter("_list_code");
		String[] dynamicColumnNames=getDynamicColumnNames();
		ListColumnManager listColumnManager = (ListColumnManager) ContextUtils.getBean("listColumnManager");
		List<ListColumn> columns=listColumnManager.getExportColumnsByCode(listCode);
		if(columns!=null&&columns.size()>0){
			String rowDatas=PageUtils.pageToRowData(page, columns);
			List<Object> head=new ArrayList<Object>();
			for(ListColumn lc:columns){
				head.add(formHtmlParser.getInternation(lc.getHeaderName()));
			}
			if(dynamicColumnNames != null)
				for(String dcn:dynamicColumnNames){
					head.add(dcn);
				}
			List<List<Object>> bodyData=getBodyData(rowDatas,columns);
			exportDynamicColumnValues.addValuesTo(bodyData);
			exportData.setHead(head);
			exportData.setBodyData(bodyData);
			exportData.setFormat(getFormatting(columns));
			exportData.setDataType(getDataType(columns));
			exportData.setValueSet(getValueSet(columns));
		}
		return exportData;
	}
	
	/**
	 * 获得动态列名
	 * @return
	 */
	public String[] getDynamicColumnNames(){
		String dynamicColumnName=Struts2Utils.getParameter("_dynamic_column_name");
		if(StringUtils.isNotEmpty(dynamicColumnName)){
			return dynamicColumnName.split(",");
		}else{
			return null;
		}
	}
	
	/**
	 * 获得导出数据
	 * @return
	 */
	public ExportData getExportData(Page<?> page,String listCode){
		ExportData exportData=new ExportData();
		ListColumnManager listColumnManager = (ListColumnManager) ContextUtils.getBean("listColumnManager");
		if(listColumnManager==null){
			log.debug("ListColumnManager不能为null");
			throw new RuntimeException("ListColumnManager不能为null");
		}
		List<ListColumn> columns=listColumnManager.getExportColumnsByCode(listCode);
		if(columns!=null&&columns.size()>0){
			String rowDatas=PageUtils.pageToRowData(page, columns);
			List<Object> head=listColumnManager.getExportHeadnameByCode(listCode);
			exportData.setHead(head);
			rowDatas=rowDatas.replaceAll("\\\\", "\\\\\\\\").replaceAll("\t", "").replaceAll("\r\n", "").replaceAll("\n", "");
			exportData.setBodyData(getBodyData(rowDatas,columns));
			exportData.setFormat(getFormatting(columns));
			exportData.setDataType(getDataType(columns));
			exportData.setValueSet(getValueSet(columns));
		}
		return exportData;
	}
	
	/**
	 * 获得每列数据表中对应的数据类型
	 * @param columns
	 * @return
	 */
	private String[] getDataType(List<ListColumn> columns) {
		String[] dataType=new String[columns.size()];
		int i=0;
		for(ListColumn listColumn:columns){
			dataType[i]=listColumn.getTableColumn().getDataType().name();
			i++;
		}
		return dataType;
	}
	private String[] getValueSet(List<ListColumn> columns) {
		String[] valueSet=new String[columns.size()];
		int i=0;
		for(ListColumn listColumn:columns){
			String valset=formHtmlParser.getValueSet(listColumn,null,null);
			valueSet[i]=valset==null?"":valset;
			i++;
		}
		return valueSet;
	}
	
	/**
	 *  获得每列对应的格式设置
	 * @param columns
	 * @return
	 */
	private String[] getFormatting(List<ListColumn> columns) {
		String[] formatting=new String[columns.size()];
		int i=0;
		for(ListColumn listColumn:columns){
			formatting[i]=listColumn.getFormat()==null?"":listColumn.getFormat();
			i++;
		}
		return formatting;
	}
	
	/**
	 * 获得表体数据
	 * @param rowDatas
	 * @param columns
	 * @param columnLength
	 * @return
	 */
	private List<List<Object>> getBodyData(String rowDatas,List<ListColumn> columns){
		List<List<Object>> bodyData=new ArrayList<List<Object>>();
		MapType mt = TypeFactory.defaultInstance().constructMapType(
				HashMap.class, String.class, String.class);
		CollectionType ct = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, mt);
		List<Map<String, String>> objs = JsonParser.json2Object(ct, rowDatas);
		Map<String, String> valueSets = new HashMap<String, String>();//保存解析第一行的值设置，避免重复解析值设置
		Map<String, Map<String, String>> propMaps = new HashMap<String, Map<String, String>>();//保存解析第一行的值设置转换成的Map形式，避免重复解析值设置的json字符串
		for(Map<String, String> obj : objs){
			List<Object> rowData=new ArrayList<Object>();
			rowData.add(obj.get("id"));
			for(ListColumn listColumn:columns){
				String columnName=listColumn.getTableColumn().getName();
				String columnValue=obj.get(columnName);//数据库中的值
				if(StringUtils.isNotEmpty(listColumn.getValueSet())){
						String valueSet=valueSets.get(columnName);
						if(StringUtils.isEmpty(valueSet)){
							valueSet=formHtmlParser.getValueSet(listColumn,null,null);
						}
						if(StringUtils.isNotEmpty(valueSet)){
							valueSets.put(columnName,valueSet );
							Map<String, String> myPropMap = propMaps.get(columnName);
							if(myPropMap==null||myPropMap.size()<=0){
								Map<String, String> propMap = JsonParser.json2Map(String.class, String.class, "{"+valueSet+"}");
								propMaps.put(columnName, propMap);
							}
							myPropMap = propMaps.get(columnName);
							columnValue=myPropMap.get(columnValue);//在页面中显示的值
						}
				}
				rowData.add(columnValue);
			}
			bodyData.add(rowData);
		}
		return bodyData;
	}
	
	/**
	 * 根据列表code获得列名
	 * @param code
	 * @return
	 */
	public String getColumnsByCode(String code){
		List<ListColumn> listColumns = listColumnDao.getColumnsByViewCode(code);
		StringBuilder columns = new StringBuilder();
		for(ListColumn lc:listColumns){
			if(lc.getTableColumn()!=null){
				if(StringUtils.isNotEmpty(columns.toString())){
					columns.append(",");
				}
				//存在实体的数据表
				ListView view = lc.getListView();
				DataTable table = null;
				String entityName=null;
				if(view!=null){table=view.getDataTable();}
				if(table!=null)entityName=table.getEntityName();
				if(StringUtils.isNotEmpty(entityName)){//存在实体
					columns.append(lc.getTableColumn().getName());
				}else{//自定义表
					columns.append(lc.getTableColumn().getDbColumnName());
				}
			}
		}
		return columns.toString();
	}
	
	/**
	 * 根据列表code获得导出列名
	 * @param code
	 * @return
	 */
	public String getExportColumnsByCode(String code){
		List<ListColumn> listColumns = listColumnDao.getExportColumnsByCode(code);
		StringBuilder columns = new StringBuilder();
		for(ListColumn lc:listColumns){
			if(lc.getTableColumn()!=null){
				if(StringUtils.isNotEmpty(columns.toString())){
					columns.append(",");
				}
				columns.append(lc.getTableColumn().getName());
			}
		}
		return columns.toString();
	}
	
}
