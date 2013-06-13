package com.norteksoft.mms.authority.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;
/**
 * 数据规则
 * @author Administrator
 *
 */
@Entity
@Table(name="MMS_DATA_RULE")
public class DataRule  extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String name;
	private Long dataTableId;
	private String dataTableName;
	private String remark;
	private Long ruleTypeId;
	private String ruleTypeName;
	private Long systemId;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="dataRule")
	private List<Condition> conditions;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getDataTableId() {
		return dataTableId;
	}
	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getRuleTypeId() {
		return ruleTypeId;
	}
	public void setRuleTypeId(Long ruleTypeId) {
		this.ruleTypeId = ruleTypeId;
	}
	public List<Condition> getConditions() {
		return conditions;
	}
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	public String getDataTableName() {
		return dataTableName;
	}
	public void setDataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
	}
	public String getRuleTypeName() {
		return ruleTypeName;
	}
	public void setRuleTypeName(String ruleTypeName) {
		this.ruleTypeName = ruleTypeName;
	}
}
