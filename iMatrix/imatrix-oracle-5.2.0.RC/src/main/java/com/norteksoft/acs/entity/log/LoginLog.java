package com.norteksoft.acs.entity.log;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.acs.base.enumeration.OperatorType;
import com.norteksoft.acs.entity.IdEntity;

@Entity
@Table(name = "ACS_LOGIN_LOG")
public class LoginLog extends IdEntity{

	private static final long serialVersionUID = 1L;
	
	private Long userId;
	private String userName;
	private Date loginTime;
	private Date exitTime;
	private String ipAddress;
	
	private Long companyId;
	private Long systemId;
	private Boolean adminLog = false;
	private OperatorType operatorType;//操作员类型
	

	@Column(name = "FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	@Column(name = "FK_SYSTEM_ID")
	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getExitTime() {
		return exitTime;
	}

	public void setExitTime(Date exitTime) {
		this.exitTime = exitTime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

}
