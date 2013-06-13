package com.norteksoft.bs.options.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.bs.options.enumeration.BusinessType;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.orm.IdEntity;

/**
 * 导入列
 * @author Administrator
 *
 */
@Entity
@Table(name="BS_IMPORT_COLUMN")
public class ImportColumn extends IdEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;//字段名称
	private String alias;//字段别名
	@Enumerated(EnumType.STRING)
	private DataType dataType;//字段类型
	private Integer displayOrder;//显示顺序
	private Integer width;//固定长度
	private Boolean notNull=false;//不可以为空
	private String defaultValue;//默认值
	@Enumerated(EnumType.STRING)
	private BusinessType businessType;//主键类型
	
	@ManyToOne
	@JoinColumn(name="FK_IMPORT_DEFINITION_ID")
	private ImportDefinition importDefinition;//导入定义
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public ImportDefinition getImportDefinition() {
		return importDefinition;
	}
	public void setImportDefinition(ImportDefinition importDefinition) {
		this.importDefinition = importDefinition;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Boolean getNotNull() {
		return notNull;
	}
	public void setNotNull(Boolean notNull) {
		this.notNull = notNull;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public BusinessType getBusinessType() {
		return businessType;
	}
	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}
}
