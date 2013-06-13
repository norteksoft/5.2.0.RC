package com.norteksoft.mms.authority.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.mms.authority.enumeration.ItemType;
import com.norteksoft.mms.authority.enumeration.UserOperator;
import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.wf.base.enumeration.LogicOperator;
/**
 * 授权条件
 * @author Administrator
 *
 */
@Entity
@Table(name="MMS_PERMISSION_ITEM")
public class PermissionItem  extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private ItemType itemType;
	private UserOperator operator;
	private LogicOperator joinType;
	private String conditionValue;
	private String conditionName;
	private Integer displayOrder;
	@ManyToOne
	@JoinColumn(name="FK_PERMISSION_ID")
	private Permission permission;
	public ItemType getItemType() {
		return itemType;
	}
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}
	public UserOperator getOperator() {
		return operator;
	}
	public void setOperator(UserOperator operator) {
		this.operator = operator;
	}
	public LogicOperator getJoinType() {
		return joinType;
	}
	public void setJoinType(LogicOperator joinType) {
		this.joinType = joinType;
	}
	public String getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}
	public Permission getPermission() {
		return permission;
	}
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	public String getConditionName() {
		return conditionName;
	}
	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
}
