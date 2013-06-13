package com.norteksoft.tags.search;

import java.io.Serializable;
import java.util.List;

import com.norteksoft.bs.options.entity.Option;

public class ObjectField implements Serializable {
	
	private static final long serialVersionUID = 1L;  
	
	private String enName;
	private String chName;
	private PropertyType propertyType;
	private boolean fixedField = false;
	private String optionsCode;
	private String enumName;
	private List<Option> defaultValues;
	private String keyValue;
	private String beanName;
	private String optionGroup;
	private String eventType;
	private String dbName;
	
	public ObjectField() {
	}
	
	public ObjectField(String enName, String dbName, String chName, PropertyType propertyType, boolean fixedField) {
		this.enName = enName;
		this.chName = chName;
		this.propertyType = propertyType;
		this.fixedField = fixedField;
		this.dbName = dbName;
	}
	
	public ObjectField(String enName, String dbName, String chName, PropertyType propertyType, boolean fixedField, String optionsCode) {
		this(enName, dbName, chName, propertyType, fixedField);
		this.optionsCode = optionsCode;
	}
	
	public ObjectField(String enName, String dbName, String chName, PropertyType propertyType, boolean fixedField, String optionsCode, List<Option> defaultValues) {
		this(enName, dbName, chName, propertyType, fixedField, optionsCode);
		this.defaultValues = defaultValues;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getChName() {
		return chName;
	}

	public void setChName(String chName) {
		this.chName = chName;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	public List<Option> getDefaultValues() {
		return defaultValues;
	}

	public void setDefaultValues(List<Option> options) {
		this.defaultValues = options;
	}

	public boolean getFixedField() {
		return fixedField;
	}

	public void setFixedField(boolean fixedField) {
		this.fixedField = fixedField;
	}

	public String getOptionsCode() {
		return optionsCode;
	}

	public void setOptionsCode(String optionsCode) {
		this.optionsCode = optionsCode;
	}

	public String getEnumName() {
		return enumName;
	}

	public void setEnumName(String enumName) {
		this.enumName = enumName;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getOptionGroup() {
		return optionGroup;
	}

	public void setOptionGroup(String optionGroup) {
		this.optionGroup = optionGroup;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
}
