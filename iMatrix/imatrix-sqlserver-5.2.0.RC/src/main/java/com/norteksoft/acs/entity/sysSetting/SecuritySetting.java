package com.norteksoft.acs.entity.sysSetting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.acs.base.enumeration.LoginFailSetType;
import com.norteksoft.acs.entity.IdEntity;
/**
 * 安全设置
 * @author chenchenhu
 *
 */
@Entity
@Table(name = "ACS_SECURITY_SETTING")

public class SecuritySetting extends IdEntity{
	private static final long serialVersionUID = 1L;

	private String value;//设置名称的值
	
	private String name;//设置名称（固定六个值login-security（登录安全设置）、loginTimeouts（系统登录超时设置）、password-over-notice（密码过期通知设置）、admin-password-overdue（管理员密码过期设置）、user-password-overdue（一般用户密码过期设置）、password-complexity（密码复杂设置））
	
	private String remarks;//设置名称的备注
	
	private Long companyId;
	
	@Enumerated(EnumType.STRING)
	private LoginFailSetType failSetType=LoginFailSetType.VALIDATE_CODE;//登录安全设置/超过登录次数后处理方式
	private Integer lockedTime=30;//登录安全设置/锁定时间(分钟) 
	

	@Column(name = "FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LoginFailSetType getFailSetType() {
		return failSetType;
	}

	public void setFailSetType(LoginFailSetType failSetType) {
		this.failSetType = failSetType;
	}

	public Integer getLockedTime() {
		return lockedTime;
	}

	public void setLockedTime(Integer lockedTime) {
		this.lockedTime = lockedTime;
	}

	
	
}
