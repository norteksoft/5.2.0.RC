package com.norteksoft.mms.authority.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.mms.authority.enumeration.FieldOperator;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.wf.base.enumeration.LogicOperator;
/**
 * 数据规则条件
 * @author Administrator
 *
 */
@Entity
@Table(name="MMS_CONDITION")
public class Condition extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String fieldName;//字段名
	@Enumerated(EnumType.STRING)
	private FieldOperator operator;//比较符号
	private String conditionValue;//条件值
	@Enumerated(EnumType.STRING)
	private LogicOperator lgicOperator;//条件连接类型
	private String field;//数据表字段
	@Enumerated(EnumType.STRING)
	private DataType dataType;//字段数据类型
	private String enumPath;//当dataType值为枚举类型时，该值有用
	@ManyToOne
	@JoinColumn(name="FK_DATA_RULE_ID")
	private DataRule dataRule;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public FieldOperator getOperator() {
		return operator;
	}
	public void setOperator(FieldOperator operator) {
		this.operator = operator;
	}
	public String getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}
	public LogicOperator getLgicOperator() {
		return lgicOperator;
	}
	public void setLgicOperator(LogicOperator lgicOperator) {
		this.lgicOperator = lgicOperator;
	}
	public DataRule getDataRule() {
		return dataRule;
	}
	public void setDataRule(DataRule dataRule) {
		this.dataRule = dataRule;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getEnumPath() {
		return enumPath;
	}
	public void setEnumPath(String enumPath) {
		this.enumPath = enumPath;
	}
	
}
