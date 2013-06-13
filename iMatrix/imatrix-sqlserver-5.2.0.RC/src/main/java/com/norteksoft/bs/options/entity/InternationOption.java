package com.norteksoft.bs.options.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 国际化选项
 * @author liudongxia
 *
 */
@Entity
@Table(name="BS_INTERNATION_OPTION")
public class InternationOption extends IdEntity{
	private static final long serialVersionUID = 1L;
	private Long category;//语言种类,选项组中定义的，记录的是选项的id
	private String categoryName;//语言种类名称,选项组中定义的，记录的是选项的名称
	private String value;//值
	@ManyToOne
	@JoinColumn(name="FK_INTERNATION_ID")
	private Internation internation;
	
	public Long getCategory() {
		return category;
	}
	public void setCategory(Long category) {
		this.category = category;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Internation getInternation() {
		return internation;
	}
	public void setInternation(Internation internation) {
		this.internation = internation;
	}
}
