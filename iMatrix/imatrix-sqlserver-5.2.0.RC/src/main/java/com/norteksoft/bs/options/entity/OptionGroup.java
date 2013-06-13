package com.norteksoft.bs.options.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 选项组实体
 * @author hjc
 */
@Entity
@Table(name="BS_OPTION_GROUP")
public class OptionGroup extends IdEntity  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@OneToMany(mappedBy="optionGroup", cascade=CascadeType.REMOVE)
	private List<Option> options = new ArrayList<Option>(); // 选项的集合 
	
	private String code; // 选项组编号
	
	private String workCode;//业务编码
	
	private String name; // 页面上显示的NAME
	
	private String description; // 项目组描述
	
	private Long systemId;//系统id（所属系统）

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	
	public String getWorkCode() {
		return workCode;
	}

	public void setWorkCode(String workCode) {
		this.workCode = workCode;
	}

	@Override
	public String toString() {
		return "id:"+this.getId()+"；选项组名称："+this.name+"；选项组编号："+this.code;
	}
}
