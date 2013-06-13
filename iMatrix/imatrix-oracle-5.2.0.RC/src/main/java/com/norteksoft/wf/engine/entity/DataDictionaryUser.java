package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.wf.base.enumeration.DataDictUserType;

@Entity
@Table(name = "WF_DATA_DICTIONARY_USER")
public class DataDictionaryUser extends IdEntity  implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long dictId;
	@Enumerated(EnumType.STRING)
	private DataDictUserType type;//0人员，1部门，2工作组
	private String loginName;
	private String infoName;//人员名称/部门名称/工作组名称
	private Long infoId;  //部门id/工作组id

	public DataDictUserType getType() {
		return type;
	}

	public void setType(DataDictUserType type) {
		this.type = type;
	}

	public Long getDictId() {
		return dictId;
	}

	public void setDictId(Long dictId) {
		this.dictId = dictId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getInfoName() {
		return infoName;
	}

	public void setInfoName(String infoName) {
		this.infoName = infoName;
	}

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
	}

	@Override
	public String toString() {
		return "DataDictionaryUser [companyId=" + this.getCompanyId() + ", dictId="
				+ dictId + ", infoId=" + infoId + ", infoName="
				+ infoName + ", loginName=" + loginName + ", type=" + type
				+ "]";
	}
	

}
