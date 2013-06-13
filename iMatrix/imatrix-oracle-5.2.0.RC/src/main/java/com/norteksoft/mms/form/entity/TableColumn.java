package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.orm.IdEntity;

import flex.messaging.util.StringUtils;

/**
 * 数据表列配置
 * @author wurong
 */
@Entity
@Table(name="MMS_TABLE_COLUMN")
public class TableColumn extends IdEntity  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String name;//列名
	private String alias;//别名
	private String dbColumnName;//数据库表中的字段名
	@Enumerated(EnumType.STRING)
	private DataType dataType;//数据类型
	private String defaultValue;//默认值
	private Integer maxLength;//最大长度
	private Integer displayOrder;//显示顺序
	@Column(name="FK_DATA_TABLE_ID")
	private Long dataTableId;//数据表
	private Boolean deleted = false;//是否已删除
	@Transient
	private String operate;
	@Transient
	private String searchValue;
	private String objectPath;//路径，自动生成代码时需要，当是枚举、引用、集合时存放其路径,精确到类名
	private Boolean  casual=false;//是否是临时的字段，自动生成代码时需要
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
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public Integer getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	public Long getDataTableId() {
		return dataTableId;
	}
	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}
	public String getDbColumnName() {
		return dbColumnName;
	}
	public void setDbColumnName(String dbColumnName) {
		this.dbColumnName = dbColumnName;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public void setOperate(String operate) {
		this.operate = operate;
	}
	public String getOperate() {
		return operate;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public String getObjectPath() {
		return objectPath;
	}
	public void setObjectPath(String objectPath) {
		this.objectPath = objectPath;
	}
	public Boolean getCasual() {
		return casual;
	}
	public void setCasual(Boolean casual) {
		this.casual = casual;
	}
	@Transient
	public String getDisplayName(){
		return this.getAlias()+"("+this.getDataType().getCode()+")";
	}
	
	public boolean equals(TableColumn tableColumn) {
		if(StringUtils.isEmpty(name))return false;
		return this.name.equals(tableColumn.getName());
	}
}
