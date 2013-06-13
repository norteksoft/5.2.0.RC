package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 视图基类
 * @author wurong
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="viewType",discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("View")
@Table(name="MMS_VIEW")
public class View extends IdEntity  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String code;//编码
	
	private String name;//名称
	@ManyToOne
	@JoinColumn(name="FK_DATA_TABLE_ID")
	private DataTable dataTable;//数据表
	
	@Column(length=500)
	private String remark;//备注

	private Boolean standard=false;//是否是标准的视图
	@Column(name="FK_MENU_ID")
	private Long menuId;//菜单列表

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

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Boolean getStandard() {
		return standard;
	}

	public void setStandard(Boolean standard) {
		this.standard = standard;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
}
