package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.IdEntity;

@Entity
@Table(name="WF_DEFINITION")
public class WorkflowDefinition extends IdEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;      //流程名称
	
	private String code;      //流程编号
	
	private String adminName; //流程管理员名称
	
	private String adminLoginName; //流程管理员登陆名
	
	private String formName;  //流程表单名称
	
	private String formCode;//表单code
	
	private Integer fromVersion;//表单版本
	
	private Integer version;   //流程版本
	
	private DataState enable  = DataState.DRAFT;    //流程状态 0为草稿状态 可修改  1为启用状态 只可以进行禁用操作     2为禁用状态 可以增加新版本
	
	private String processId; //JBPM部署后的流程Key 能够唯一确定一个流程定义
	
	private Long typeId;//所属流程类型id
	
	private Long systemId; //系统ID
	
	@Column(length=64)
	private String processType;//流程类型(在类ProcessType中取值)
	
	private Boolean allowPredefineStep = true;//当时自由流时是否允许预设步骤
	
	private String customType;//自定义类别
	
	@Transient
	private Integer instanceCount = 0;//实例总数
	
	@Transient
	private Integer endCount = 0;//已结束实例数

	
	@Transient
	private WorkflowDefinitionFile workflowDefinitionFile;
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	
	public Long getSystemId() {
		return systemId;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAdminLoginName() {
		return adminLoginName;
	}

	public void setAdminLoginName(String adminLoginName) {
		this.adminLoginName = adminLoginName;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public Integer getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(Integer fromVersion) {
		this.fromVersion = fromVersion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	public DataState getEnable() {
		return enable;
	}

	public void setEnable(DataState enable) {
		this.enable = enable;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public WorkflowDefinitionFile getWorkflowDefinitionFile() {
		return workflowDefinitionFile;
	}

	public void setWorkflowDefinitionFile(
			WorkflowDefinitionFile workflowDefinitionFile) {
		this.workflowDefinitionFile = workflowDefinitionFile;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}


	public Integer getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(Integer instanceCount) {
		this.instanceCount = instanceCount;
	}

	public Integer getEndCount() {
		return endCount;
	}

	public void setEndCount(Integer endCount) {
		this.endCount = endCount;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public Boolean getAllowPredefineStep() {
		return allowPredefineStep;
	}

	public void setAllowPredefineStep(Boolean allowPredefineStep) {
		this.allowPredefineStep = allowPredefineStep;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCustomType() {
		return customType;
	}

	public void setCustomType(String customType) {
		this.customType = customType;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("WorkflowDefinition [adminCode=" ).append( adminLoginName ).append( ", adminName=")
				.append(adminName ).append( ", companyId=" ).append( this.getCompanyId() ).append( ", creator=")
				.append( this.getCreator() ).append( ", enable=" ).append( enable ).append( ", formName=" ).append( formName)
				.append( ", fromVersion=" ).append( fromVersion ).append( ", name=")
				.append( name ).append( ", processId=" ).append( processId ).append( ", systemId=" ).append( systemId)
				.append( ", typeId=" ).append( typeId ).append( ", version=" ).append( version ).append( "]").toString();
	}
	
	
	
}
