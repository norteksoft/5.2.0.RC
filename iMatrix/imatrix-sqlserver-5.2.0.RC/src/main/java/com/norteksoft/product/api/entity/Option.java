package com.norteksoft.product.api.entity;

import java.io.Serializable;

import com.norteksoft.product.api.utils.BeanUtil;

public class Option implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//entity
	private Long id;
	private boolean deleted;
	private Long companyId;
	//Option
	private com.norteksoft.bs.options.entity.OptionGroup optionGroup; // 所属的选项组
	private String name; // 选项的名字 
	private String value; // 选项的值
	private Integer optionIndex; // 选项出现的顺序
	private Boolean selected; // 是否默认被选中
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public OptionGroup getOptionGroup() {
		return BeanUtil.turnToModelOptionGroup(optionGroup);
	}
	public void setOptionGroup(
			com.norteksoft.bs.options.entity.OptionGroup optionGroup) {
		this.optionGroup = optionGroup;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getOptionIndex() {
		return optionIndex;
	}
	public void setOptionIndex(Integer optionIndex) {
		this.optionIndex = optionIndex;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	
}