package com.norteksoft.wf.engine.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.wf.base.enumeration.TrustRecordState;

/**
 * 委托管理
 * 
 */
@Entity
@Table(name="WF_TRUST_RECORD")
public class TrustRecord extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private String trustor;    //委托人的登陆名
	
	private String trustee;   //受托人登陆名
	
	private Date beginTime;    //委托生效日期 包括在有效日期内
	
	private Date endTime;     //委托截止日期 包括在有效日期内
	
	private Short style;     //委托 形式  1指定环节  2全权委托  3委托权限
	
	private String processId; //JBPM部署后的流程Key 能够唯一确定一个流程定义
	
	private String activityName;  //委托的环节名
	
	@Column(length=550)
	private String remark;  //说明
	
	private String roleIds;  //角色ID
	
	@Enumerated(EnumType.STRING)
	private TrustRecordState state;
	
	@Transient
	private String expands;  //扩展
	
	@Transient
	private String trustorName;  //委托人姓名
	
	private String trusteeName;  //受托人姓名
	
	private String selectedRoleNames;
	
	private String name;//委托名称：指定环节的委托，显示流程名称，权限委托显示权限委托，全权委托显示全权委托

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Short getStyle() {
		return style;
	}

	public void setStyle(Short style) {
		this.style = style;
	}

	public String getTrustee() {
		return trustee;
	}

	public void setTrustee(String trustee) {
		this.trustee = trustee;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getExpands() {
		return expands;
	}

	public String getTrustor() {
		return trustor;
	}

	public void setTrustor(String trustor) {
		this.trustor = trustor;
	}

	public void setExpands(String expands) {
		this.expands = expands;
	}

	public String getTrusteeName() {
		return trusteeName;
	}

	public void setTrusteeName(String trusteeName) {
		this.trusteeName = trusteeName;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public String getSelectedRoleNames() {
		return selectedRoleNames;
	}

	public void setSelectedRoleNames(String selectedRoleNames) {
		this.selectedRoleNames = selectedRoleNames;
	}

	public TrustRecordState getState() {
		return state;
	}

	public void setState(TrustRecordState state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTrustorName() {
		return trustorName;
	}

	public void setTrustorName(String trustorName) {
		this.trustorName = trustorName;
	}

}
