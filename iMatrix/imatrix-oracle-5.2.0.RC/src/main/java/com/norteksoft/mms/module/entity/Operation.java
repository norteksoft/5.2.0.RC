package com.norteksoft.mms.module.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

/**
 * 功能管理
 * @author liudongxia
 *
 */

@Entity
@Table(name="MMS_OPERATION")
public class Operation extends IdEntity  implements Serializable{
	private static final long serialVersionUID = 1L;
	private String code;//编码
	private String name;//名称
	@Column(length=500)
	private String remark;//备注
	private Long systemId;//系统id
	
	@ManyToOne
	@JoinColumn(name="FK_PARENT_OPERATION_ID")
	private Operation parent; //父类型
	@OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
	private List<Operation> children = new ArrayList<Operation>(); //子类型集合
	
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	public Operation getParent() {
		return parent;
	}
	public void setParent(Operation parent) {
		this.parent = parent;
	}
	public List<Operation> getChildren() {
		return children;
	}
	public void setChildren(List<Operation> children) {
		this.children = children;
	}
}
