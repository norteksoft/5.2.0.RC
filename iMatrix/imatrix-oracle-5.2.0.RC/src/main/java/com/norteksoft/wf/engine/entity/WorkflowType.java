package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;

@Entity
@Table(name="WF_TYPE")
public class WorkflowType extends IdEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;      //类型名称 唯一
	
	private String code;      //类型编号 唯一
	
	private Boolean approveSystem=false;      //是否是审批系统

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getApproveSystem() {
		return approveSystem;
	}

	public void setApproveSystem(Boolean approveSystem) {
		this.approveSystem = approveSystem;
	}

	@Override
	public String toString() {
		return new StringBuilder()
		.append("BasicType [companyId=").append(this.getCompanyId()).append(", creator=").append(this.getCreator())
		.append( ", name=" ).append( name ).append("]").toString();
	}
	
	
}
