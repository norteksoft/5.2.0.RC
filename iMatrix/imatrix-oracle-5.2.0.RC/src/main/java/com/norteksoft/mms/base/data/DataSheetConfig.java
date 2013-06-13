package com.norteksoft.mms.base.data;

public class DataSheetConfig {
	private String fieldName;
	private String title;
	private String dataType="TEXT";
	private String enumName;
	private String defaultValue;
	private boolean ignore=false;
	private boolean identifier=false;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getEnumName() {
		return enumName;
	}
	public void setEnumName(String enumName) {
		this.enumName = enumName;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public boolean isIgnore() {
		return ignore;
	}
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	public boolean isIdentifier() {
		return identifier;
	}
	public void setIdentifier(boolean identifier) {
		this.identifier = identifier;
	}
	
	
}
