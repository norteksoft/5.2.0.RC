package com.norteksoft.bs.rank.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.bs.rank.enumeration.SubordinateType;
import com.norteksoft.product.orm.IdEntity;

/**
 * 下级
 * @author Administrator
 *
 */
@Entity
@Table(name = "BS_SUBORDINATE")
public class Subordinate extends IdEntity  implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "DATA_DICT_RANK_ID")
	private Superior dataDictionaryRank;
	@Enumerated(EnumType.STRING)
	private SubordinateType subordinateType;//下级类型 0 人员，1 部门，2工作组
	private Long targetId;//用户id，部门id,工作组id
	private String name;//真名,部门名称，工作组名称
	private String loginName;//登录名
	
	private Long systemId; //系统ID
	public Superior getDataDictionaryRank() {
		return dataDictionaryRank;
	}
	public void setDataDictionaryRank(Superior dataDictionaryRank) {
		this.dataDictionaryRank = dataDictionaryRank;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	public SubordinateType getSubordinateType() {
		return subordinateType;
	}
	public void setSubordinateType(SubordinateType subordinateType) {
		this.subordinateType = subordinateType;
	}
	public Long getTargetId() {
		return targetId;
	}
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	
}
