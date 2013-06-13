package com.norteksoft.bs.options.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.norteksoft.bs.options.enumeration.ImportType;
import com.norteksoft.bs.options.enumeration.ImportWay;
import com.norteksoft.product.orm.IdEntity;

/**
 * 导入定义
 * @author Administrator
 *
 */
@Entity
@Table(name="BS_IMPORT_DEFINITION")
public class ImportDefinition extends IdEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String code;//编号
	private String alias;//别名
	private String name;//数据表名
	private String relevanceName;//关联表名
	private String foreignKey;//外键
	@Column(length=500)
	private String remark;//备注
	@Enumerated(EnumType.STRING)
	private ImportType importType;//导入类型
	@Enumerated(EnumType.STRING)
	private ImportWay importWay;//导入方式
	private String divide;//分隔符
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="importDefinition")
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderBy("displayOrder asc")
	private List<ImportColumn> importColumns;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRelevanceName() {
		return relevanceName;
	}
	public void setRelevanceName(String relevanceName) {
		this.relevanceName = relevanceName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public ImportType getImportType() {
		return importType;
	}
	public void setImportType(ImportType importType) {
		this.importType = importType;
	}
	public List<ImportColumn> getImportColumns() {
		return importColumns;
	}
	public void setImportColumns(List<ImportColumn> importColumns) {
		this.importColumns = importColumns;
	}
	public String getDivide() {
		return divide;
	}
	public void setDivide(String divide) {
		this.divide = divide;
	}
	public ImportWay getImportWay() {
		return importWay;
	}
	public void setImportWay(ImportWay importWay) {
		this.importWay = importWay;
	}
	public String getForeignKey() {
		return foreignKey;
	}
	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}
	
}
