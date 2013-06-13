package com.norteksoft.acs.entity.organization;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

/**
 * 邮件配置
 * @author Administrator
 *
 */
@Entity
@Table(name = "ACS_MAIL_DEPLOY")
public class MailDeploy extends IdEntity{
	private static final long serialVersionUID = 1L;
	
	private String smtpAuthInside;//邮件服务器是否需要验证用户存在与否 (内网)
	private String transportProtocolInside;//邮件服务器使用的协议 (内网)
	private String smtpHostInside;//邮件服务器地址(内网)
	private String smtpPortInside;//邮件服务器使用的端口(内网)
	private String hostUserInside;//默认服务器端用户名(内网)
	private String hostUserPasswordInside;//默认服务器用户密码(内网)
	private String hostUserFromInside;//默认主机地址(内网)
	
	private String smtpAuthExterior;//邮件服务器是否需要验证用户存在与否 (外网)
	private String transportProtocolExterior;//邮件服务器使用的协议 (外网)
	private String smtpHostExterior;//邮件服务器地址(外网)
	private String smtpPortExterior;//邮件服务器使用的端口(外网)
	private String hostUserExterior;//默认服务器端用户名(外网)
	private String hostUserPasswordExterior;//默认服务器用户密码(外网)
	private String hostUserFromExterior;//默认主机地址(外网)
	private Long companyId;
	
	public String getSmtpAuthInside() {
		return smtpAuthInside;
	}
	public void setSmtpAuthInside(String smtpAuthInside) {
		this.smtpAuthInside = smtpAuthInside;
	}
	public String getTransportProtocolInside() {
		return transportProtocolInside;
	}
	public void setTransportProtocolInside(String transportProtocolInside) {
		this.transportProtocolInside = transportProtocolInside;
	}
	public String getSmtpHostInside() {
		return smtpHostInside;
	}
	public void setSmtpHostInside(String smtpHostInside) {
		this.smtpHostInside = smtpHostInside;
	}
	public String getSmtpPortInside() {
		return smtpPortInside;
	}
	public void setSmtpPortInside(String smtpPortInside) {
		this.smtpPortInside = smtpPortInside;
	}
	public String getHostUserInside() {
		return hostUserInside;
	}
	public void setHostUserInside(String hostUserInside) {
		this.hostUserInside = hostUserInside;
	}
	public String getHostUserPasswordInside() {
		return hostUserPasswordInside;
	}
	public void setHostUserPasswordInside(String hostUserPasswordInside) {
		this.hostUserPasswordInside = hostUserPasswordInside;
	}
	public String getHostUserFromInside() {
		return hostUserFromInside;
	}
	public void setHostUserFromInside(String hostUserFromInside) {
		this.hostUserFromInside = hostUserFromInside;
	}
	public String getSmtpAuthExterior() {
		return smtpAuthExterior;
	}
	public void setSmtpAuthExterior(String smtpAuthExterior) {
		this.smtpAuthExterior = smtpAuthExterior;
	}
	public String getTransportProtocolExterior() {
		return transportProtocolExterior;
	}
	public void setTransportProtocolExterior(String transportProtocolExterior) {
		this.transportProtocolExterior = transportProtocolExterior;
	}
	public String getSmtpHostExterior() {
		return smtpHostExterior;
	}
	public void setSmtpHostExterior(String smtpHostExterior) {
		this.smtpHostExterior = smtpHostExterior;
	}
	public String getSmtpPortExterior() {
		return smtpPortExterior;
	}
	public void setSmtpPortExterior(String smtpPortExterior) {
		this.smtpPortExterior = smtpPortExterior;
	}
	public String getHostUserExterior() {
		return hostUserExterior;
	}
	public void setHostUserExterior(String hostUserExterior) {
		this.hostUserExterior = hostUserExterior;
	}
	public String getHostUserPasswordExterior() {
		return hostUserPasswordExterior;
	}
	public void setHostUserPasswordExterior(String hostUserPasswordExterior) {
		this.hostUserPasswordExterior = hostUserPasswordExterior;
	}
	public String getHostUserFromExterior() {
		return hostUserFromExterior;
	}
	public void setHostUserFromExterior(String hostUserFromExterior) {
		this.hostUserFromExterior = hostUserFromExterior;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
}
