package com.norteksoft.acs.entity.sysSetting;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

@Entity
@Table(name="ACS_SERVER_CONFIG")
public class ServerConfig extends IdEntity{

	private static final long serialVersionUID = 1L;
	
    
    private Long companyId;
    private LdapType ldapType;
    private String ldapUsername;
    
    private String ldapPassword;
    
    private String ldapUrl;
    
    private String rtxUrl;
    
    private Boolean ldapInvocation = false; //(ldap是否启用)
    
    private Boolean rtxInvocation = false; //(rtx是否启用)
    
    private Boolean extern = false;
    private ExternalType externalType = ExternalType.HTTP;
    private String externalUrl;
	

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getLdapUsername() {
		return ldapUsername;
	}

	public void setLdapUsername(String ldapUsername) {
		this.ldapUsername = ldapUsername;
	}

	public String getLdapPassword() {
		return ldapPassword;
	}

	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}


	public String getLdapUrl() {
		return ldapUrl;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public Boolean getLdapInvocation() {
		return ldapInvocation;
	}

	public void setLdapInvocation(Boolean ldapInvocation) {
		this.ldapInvocation = ldapInvocation;
	}

	public Boolean getRtxInvocation() {
		return rtxInvocation;
	}

	public void setRtxInvocation(Boolean rtxInvocation) {
		this.rtxInvocation = rtxInvocation;
	}

	public String getRtxUrl() {
		return rtxUrl;
	}

	public void setRtxUrl(String rtxUrl) {
		this.rtxUrl = rtxUrl;
	}

	
	public Boolean getExtern() {
		return extern;
	}

	public void setExtern(Boolean extern) {
		this.extern = extern;
	}

	@Enumerated(EnumType.STRING)
	public LdapType getLdapType() {
		return ldapType;
	}

	public void setLdapType(LdapType ldapType) {
		this.ldapType = ldapType;
	}

	@Enumerated(EnumType.STRING)
	public ExternalType getExternalType() {
		return externalType;
	}

	public void setExternalType(ExternalType externalType) {
		this.externalType = externalType;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}
	
}
