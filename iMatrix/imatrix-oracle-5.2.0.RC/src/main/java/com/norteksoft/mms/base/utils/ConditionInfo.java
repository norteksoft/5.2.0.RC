package com.norteksoft.mms.base.utils;

import com.norteksoft.mms.authority.enumeration.FieldOperator;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.wf.base.enumeration.LogicOperator;

public class ConditionInfo {
	
	private int index;
	private FieldOperator fieldOperator;
	private DataType dataType;
	private Object filedValue;
	private String conditionValue;
	private LogicOperator joinType;
	
	public ConditionInfo() {}
	
	public ConditionInfo(int index,DataType dataType, FieldOperator fieldOperator,
			Object filedValue, String conditionValue, LogicOperator joinType) {
		this.index = index;
		this.dataType = dataType;
		this.fieldOperator = fieldOperator;
		this.filedValue = filedValue;
		this.conditionValue = conditionValue;
		this.joinType = joinType;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public FieldOperator getFieldOperator() {
		return fieldOperator;
	}
	public void setFieldOperator(FieldOperator fieldOperator) {
		this.fieldOperator = fieldOperator;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public Object getFiledValue() {
		return filedValue;
	}
	public void setFiledValue(Object filedValue) {
		this.filedValue = filedValue;
	}
	public String getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}
	public String getJoinType() {
		switch (joinType) {
		case AND: return "&&";
		case OR: return "||";
		}
		return "";
	}
}
