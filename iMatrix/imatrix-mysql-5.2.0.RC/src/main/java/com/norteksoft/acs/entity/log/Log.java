package com.norteksoft.acs.entity.log;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.acs.base.enumeration.OperatorType;
import com.norteksoft.acs.entity.IdEntity;

@Entity
@Table(name = "ACS_LOG")
public class Log extends IdEntity {
	private static final long serialVersionUID = 1L;

	//操作人姓名
	private String operator;

	//操作时间
	private Date createdTime;
	
	//模块名称
	private String moduleName;

	//子模块名称
	private String subModuleName;

	//功能名称
	private String functionName;

	//操作类型
	private String operationType;
	
	//存储xml格式的数据
	private String xmlText;
	
	//对添加和删除的数据记录数据ID
	private Long dataId;
	
	//日志提示
	private String message;
	
	//系统名称
	private String systemName;
	
	//公司名称
	private String companyName;
	
	//公司ID
	private Long companyId;
	
	private Boolean adminLog = false;

	private Long systemId;
	
	private OperatorType operatorType;//操作员类型
	
	private String ipAddress;
	

	@Column(name = "FK_SYSTEM_ID")
	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	@Column(name = "FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getSubModuleName() {
		return subModuleName;
	}

	public void setSubModuleName(String subModuleName) {
		this.subModuleName = subModuleName;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getXmlText() {
		return xmlText;
	}

	public void setXmlText(String xmlText) {
		this.xmlText = xmlText;
	}

	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	@Lob
    @Column(name="MESSAGE", columnDefinition="LONGTEXT", nullable=true) 
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Boolean getAdminLog() {
		return adminLog;
	}

	public void setAdminLog(Boolean adminLog) {
		this.adminLog = adminLog;
	}

	public OperatorType getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(OperatorType operatorType) {
		this.operatorType = operatorType;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
