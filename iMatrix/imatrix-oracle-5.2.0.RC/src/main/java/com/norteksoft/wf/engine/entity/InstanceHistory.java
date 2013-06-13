package com.norteksoft.wf.engine.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntity;

@Entity
@Table(name="WF_INSTANCE_HISTORY")
public class InstanceHistory  extends IdEntity implements Serializable{

	/**
	 * 历史类别：流程跳转
	 */
	public static final Integer TYPE_FLOW_START = 0;
	public static final Integer TYPE_FLOW_INTO = 1;
	public static final Integer TYPE_FLOW_LEAVE = 2;
	public static final Integer TYPE_FLOW_END = 3;
	/**
	 * 历史类别：人工环节
	 */
	public static final Integer TYPE_TASK = 4;
	/**
	 * 历史类别：自动环节
	 */
	public static final Integer TYPE_AUTO = 5;
	
	private static final long serialVersionUID = 1L;
	private Integer type;  //类型： 0：流程跳转， 1：人工环节，2：自动环节
	
	private String taskName;
	private Long taskId;
	
	private String transactionResult;     //办理结果
	
	@Transient
	private String transactorOpinion;    //办理意见
	
	private String instanceId; //实例ID
	
	private String executionId; //
	
	private String transactor; //办理人
	
	private Boolean effective = true; //有效性，当环节被退回时失效
	
	private Boolean specialTask=false;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public String getTransactor() {
		return transactor;
	}

	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}

	public Boolean getEffective() {
		return effective;
	}

	public void setEffective(Boolean effective) {
		this.effective = effective;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public Boolean getSpecialTask() {
		return specialTask;
	}

	public void setSpecialTask(Boolean specialTask) {
		this.specialTask = specialTask;
	}

	public String getTransactionResult() {
		return transactionResult;
	}

	public void setTransactionResult(String transactionResult) {
		this.transactionResult = transactionResult;
	}

	public String getTransactorOpinion() {
		return transactorOpinion;
	}

	public void setTransactorOpinion(String transactorOpinion) {
		this.transactorOpinion = transactorOpinion;
	}

	public InstanceHistory(){}
	
	public InstanceHistory(Long companyId, String instanceId, Integer type, String info){
		this.setCompanyId(companyId);
		this.setCreatedTime(new Date());
		this.instanceId = instanceId;
		this.type = type;
		this.transactionResult = info;
	}

	public InstanceHistory(Long companyId, String instanceId, Integer type, String info,String taskName){
		this.setCompanyId(companyId);
		this.setCreatedTime(new Date());
		this.instanceId = instanceId;
		this.type = type;
		this.transactionResult = info;
		this.taskName = taskName;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
}
