package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import com.norteksoft.mms.form.enumeration.ControlType;
import com.norteksoft.mms.form.enumeration.DataType;

/**
 * 表单控件
 * @author wurong
 */
public class FormControl implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//<input id="column3" title="字段san" name="column3" datatype="TEXT" request="0" format="no" readolny="0" columnid="3" plugintype="TEXT" />
	private String name;//控件名
	private String dbName;//数据库中对应的字段名
	private String title;//控件标题
	private String controlId = "";//控件id
	private String signatureVisible;//签章是否显示
//	private Long tableColumnId;//数据表字段id  
	private String format;//输入格式
	private String formatType = "no";//验证格式类型  no:不验证  string:正则  enum:枚举
	private String formatTip;//格式提示
	private ControlType controlType = ControlType.TEXT;//控件类型
	private Boolean readOlny = false;//是否只读 默认为否
	private Boolean request = false;//是否必填 默认不必填
	private DataType dataType = DataType.TEXT;//数据类型
	private String controlValue;//对应的值
	private Integer componentWidth;//控件宽度
	private Integer componentHeight;//控件高度
	private String dataSrc;//数据来源,存放数据表名称
	private String dataSrcName;//数据来源名称
	private String dataFieldNames;
	private String dataFields;
	private String dataControlIds;
	private Integer maxLength;//最大长度
	private String clickEvent;//控件点击事件
	private String classStyle;//样式类名
	private String styleContent;//内联样式
	private String tableName;//表单对应的数据表名称
	//部门人员控件
	private String showDeptControlValue;  //显示信息的输入框的控件名称
	private String showDeptControlId;  //显示信息的输入框的控件id
	private String showDeptCotrolType;//显示信息的输入框类型
	private String saveDeptControlValue;  //保存信息的输入框的控件名称
	private String saveDeptControlId;  //保存信息的输入框的控件id
	private String deptMultiple;//类型:多选或单选
	private String deptTreeType;//树的类型
	//计算控件
	private String computational;//计算公式
	private Integer precision;//计算精度
	private Integer fontSize;//字体大小
	
	//下拉菜单控件
	private String initSelectValue;
	private String childControlIds;
	private String selectValues;
	//数据选择控件
	private String dataQuerys;//1,0,1...;字段是否需要查询字符串
	
	//数据获取
	private String queryProperty;
	private String referenceControl;
	//紧急程度设置控件
	private String urgencyValues;
	private String urgencyDescribes;
	//列表控件
	private String lcTitles;//列表头，已逗号隔开的字符串
	private String lcSums;
	private String lcSizes;
	private String lcCals;//列表的计算公式
	
	//按钮控件
	private String showButtonControlId;
	
	//标签控件
	private boolean printable;//打印时是否显示
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getControlId() {
		return controlId;
	}
	public void setControlId(String controlId) {
		this.controlId = controlId;
	}
	public ControlType getControlType() {
		return controlType;
	}
	public void setControlType(ControlType controlType) {
		this.controlType = controlType;
	}
//	public Long getTableColumnId() {
//		return tableColumnId;
//	}
//	public void setTableColumnId(Long tableColumnId) {
//		this.tableColumnId = tableColumnId;
//	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public Boolean getReadOlny() {
		return readOlny;
	}
	public void setReadOlny(Boolean readOlny) {
		this.readOlny = readOlny;
	}
	public Boolean getRequest() {
		return request;
	}
	public void setRequest(Boolean request) {
		this.request = request;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public String getControlValue() {
		return controlValue;
	}
	public void setControlValue(String controlValue) {
		this.controlValue = controlValue;
	}
	public String getShowDeptControlId() {
		return showDeptControlId;
	}
	public void setShowDeptControlId(String showDeptControlId) {
		this.showDeptControlId = showDeptControlId;
	}
	public String getShowDeptCotrolType() {
		return showDeptCotrolType;
	}
	public void setShowDeptCotrolType(String showDeptCotrolType) {
		this.showDeptCotrolType = showDeptCotrolType;
	}
	public String getSaveDeptControlId() {
		return saveDeptControlId;
	}
	public void setSaveDeptControlId(String saveDeptControlId) {
		this.saveDeptControlId = saveDeptControlId;
	}
	public String getDeptMultiple() {
		return deptMultiple;
	}
	public void setDeptMultiple(String deptMultiple) {
		this.deptMultiple = deptMultiple;
	}
	public String getDeptTreeType() {
		return deptTreeType;
	}
	public void setDeptTreeType(String deptTreeType) {
		this.deptTreeType = deptTreeType;
	}
	public String getShowDeptControlValue() {
		return showDeptControlValue;
	}
	public void setShowDeptControlValue(String showDeptControlValue) {
		this.showDeptControlValue = showDeptControlValue;
	}
	public String getSaveDeptControlValue() {
		return saveDeptControlValue;
	}
	public void setSaveDeptControlValue(String saveDeptControlValue) {
		this.saveDeptControlValue = saveDeptControlValue;
	}
	public String getComputational() {
		return computational;
	}
	public void setComputational(String computational) {
		this.computational = computational;
	}
	public Integer getPrecision() {
		return precision;
	}
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}
	public Integer getFontSize() {
		return fontSize;
	}
	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}
	public Integer getComponentWidth() {
		return componentWidth;
	}
	public void setComponentWidth(Integer componentWidth) {
		this.componentWidth = componentWidth;
	}
	public Integer getComponentHeight() {
		return componentHeight;
	}
	public void setComponentHeight(Integer componentHeight) {
		this.componentHeight = componentHeight;
	}
	public void setInitSelectValue(String initSelectValue) {
		this.initSelectValue = initSelectValue;
	}
	public String getInitSelectValue() {
		return initSelectValue;
	}
	public void setChildControlIds(String childControlIds) {
		this.childControlIds = childControlIds;
	}
	public String getChildControlIds() {
		return childControlIds;
	}
	public void setSelectValues(String selectValues) {
		this.selectValues = selectValues;
	}
	public String getSelectValues() {
		return selectValues;
	}
	public String getDataSrc() {
		return dataSrc;
	}
	public void setDataSrc(String dataSrc) {
		this.dataSrc = dataSrc;
	}
	public String getDataFieldNames() {
		return dataFieldNames;
	}
	public void setDataFieldNames(String dataFieldNames) {
		this.dataFieldNames = dataFieldNames;
	}
	public String getDataFields() {
		return dataFields;
	}
	public void setDataFields(String dataFields) {
		this.dataFields = dataFields;
	}
	public String getDataControlIds() {
		return dataControlIds;
	}
	public void setDataControlIds(String dataControlIds) {
		this.dataControlIds = dataControlIds;
	}
	public String getDataQuerys() {
		return dataQuerys;
	}
	public void setDataQuerys(String dataQuerys) {
		this.dataQuerys = dataQuerys;
	}
	public void setDataSrcName(String dataSrcName) {
		this.dataSrcName = dataSrcName;
	}
	public String getDataSrcName() {
		return dataSrcName;
	}
	public String getQueryProperty() {
		return queryProperty;
	}
	public void setQueryProperty(String queryProperty) {
		this.queryProperty = queryProperty;
	}
	public void setReferenceControl(String referenceControl) {
		this.referenceControl = referenceControl;
	}
	public String getReferenceControl() {
		return referenceControl;
	}
	public void setUrgencyDescribes(String urgencyDescribes) {
		this.urgencyDescribes = urgencyDescribes;
	}
	public String getUrgencyDescribes() {
		return urgencyDescribes;
	}
	public void setUrgencyValues(String urgencyValues) {
		this.urgencyValues = urgencyValues;
	}
	public String getUrgencyValues() {
		return urgencyValues;
	}
	public String getFormatType() {
		return formatType;
	}
	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}
	public String getFormatTip() {
		return formatTip;
	}
	public void setFormatTip(String formatTip) {
		this.formatTip = formatTip;
	}
	public Integer getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	public String getLcTitles() {
		return lcTitles;
	}
	public void setLcTitles(String lcTitles) {
		this.lcTitles = lcTitles;
	}
	public String getLcSums() {
		return lcSums;
	}
	public void setLcSums(String lcSums) {
		this.lcSums = lcSums;
	}
	public String getLcSizes() {
		return lcSizes;
	}
	public void setLcSizes(String lcSizes) {
		this.lcSizes = lcSizes;
	}
	public String getLcCals() {
		return lcCals;
	}
	public void setLcCals(String lcCals) {
		this.lcCals = lcCals;
	}
	public String getClickEvent() {
		return clickEvent;
	}
	public void setClickEvent(String clickEvent) {
		this.clickEvent = clickEvent;
	}
	public String getClassStyle() {
		return classStyle;
	}
	public void setClassStyle(String classStyle) {
		this.classStyle = classStyle;
	}
	public String getStyleContent() {
		return styleContent;
	}
	public void setStyleContent(String styleContent) {
		this.styleContent = styleContent;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSignatureVisible() {
		return signatureVisible;
	}
	public void setSignatureVisible(String signatureVisible) {
		this.signatureVisible = signatureVisible;
	}
	public String getShowButtonControlId() {
		return showButtonControlId;
	}
	public void setShowButtonControlId(String showButtonControlId) {
		this.showButtonControlId = showButtonControlId;
	}
	public boolean isPrintable() {
		return printable;
	}
	public void setPrintable(boolean printable) {
		this.printable = printable;
	}
	
}
