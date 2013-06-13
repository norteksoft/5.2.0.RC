package com.norteksoft.mms.base.utils.view;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.enumeration.DefaultValue;
import com.norteksoft.mms.form.enumeration.EditControlType;
import com.norteksoft.mms.form.enumeration.EventType;

/**
 * 动态列定义
 * @author Administrator
 *
 */
public class DynamicColumnDefinition {
	private String colName;//表头名称
	private String name;//实体对应的属性名
	private Boolean editable=false;//是否编辑
	private EditControlType edittype=EditControlType.TEXT;//编辑列表时控件的类型 （和jqgrid的命名保持一致）
	@JsonIgnore
	private String editoptions;//编辑是的选项（和jqgrid的命名保持一致）
	private DataType type=DataType.TEXT;//实体对应的属性类型
	private String editRules;//编辑规则
	private Boolean isTotal=false;//是否合计
	private Boolean exportable=true;//是否导出
	
	private String colWidth;//列宽
	private Boolean visible;//是否显示
	private EventType eventType;//编辑时触发的事件
	@Enumerated(EnumType.STRING)
	private DefaultValue defaultValue;//编辑时默认值设置
	
	public DynamicColumnDefinition(){
		
	}
	
	public DynamicColumnDefinition(String colName,String name){
		this.colName=colName;
		this.name=name;
		this.editable=false;
		this.edittype=EditControlType.TEXT;
		this.setType(DataType.TEXT);
		this.visible=true;
		this.colWidth="100";
		this.defaultValue=DefaultValue.CURRENT_NOTHING;
	}
	
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Boolean getEditable() {
		return editable;
	}
	public void setEditable(Boolean editable) {
		this.editable = editable;
	}
	public EditControlType getEdittype() {
		return edittype;
	}
	public void setEdittype(EditControlType edittype) {
		this.edittype = edittype;
	}
	public String getEditoptions() {
		return editoptions;
	}
	public void setEditoptions(String editoptions) {
		this.editoptions = editoptions;
	}
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
	public String getEditRules() {
		return editRules;
	}
	public void setEditRules(String editRules) {
		this.editRules = editRules;
	}

	public Boolean getIsTotal() {
		return isTotal;
	}

	public void setIsTotal(Boolean isTotal) {
		this.isTotal = isTotal;
	}

	public String getColWidth() {
		return colWidth;
	}

	public void setColWidth(String colWidth) {
		this.colWidth = colWidth;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public DefaultValue getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(DefaultValue defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Boolean getExportable() {
		return exportable;
	}

	public void setExportable(Boolean exportable) {
		this.exportable = exportable;
	}

}
