package com.norteksoft.acs.entity.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.GrantedAuthority;

import com.norteksoft.acs.base.enumeration.SecretGrade;

public class User extends org.springframework.security.userdetails.User {
	private static final long serialVersionUID = 1L;
	private Long userId;
	private Long companyId;
	private String companyCode;
	private String companyName;
	private String trueName;
	private String honorificTitle;
	private String password;
	private String email;
	private String theme;
	private Integer dr = 0;
	private SecretGrade secretGrade; // 用户密级
	private String roleCodes; // 角色编号
	
	private Map<Object, Object> otherInfos;
	
	public User(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, GrantedAuthority[] authorities)
			throws IllegalArgumentException {
		
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);

	}
	
	public User(Long userId, String username, String password, String email, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, GrantedAuthority[] authorities, 
			Long companyId, String companyCode, String companyName,SecretGrade secretGrade)
			throws IllegalArgumentException {
		
		this(username, password, enabled, accountNonExpired, 
				credentialsNonExpired, accountNonLocked, authorities);
		this.password = password;
		this.email = email;
		this.userId = userId;
		this.companyId = companyId;
		this.companyCode = companyCode;
		this.companyName = companyName;
		this.secretGrade = secretGrade;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Map<Object, Object> getOtherInfos() {
		if(otherInfos == null) otherInfos = new HashMap<Object, Object>();
		return otherInfos;
	}

	public void setOtherInfos(Map<Object, Object> otherInfos) {
		this.otherInfos = otherInfos;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getHonorificTitle() {
		return honorificTitle;
	}
	
	public void setHonorificTitle(String honorificTitle) {
		this.honorificTitle = honorificTitle;
	}

	public SecretGrade getSecretGrade() {
		return secretGrade;
	}

	public void setRoleCodes(String roleCodes) {
		this.roleCodes = roleCodes;
	}

	public String getRoleCodes() {
		return roleCodes;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}
}
