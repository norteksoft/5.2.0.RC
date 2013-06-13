package com.norteksoft.acs.entity.organization;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.acs.base.enumeration.MailboxDeploy;
import com.norteksoft.acs.base.enumeration.SecretGrade;
import com.norteksoft.acs.base.utils.log.Logger;
import com.norteksoft.acs.entity.IdEntity;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.product.util.Md5;

import flex.messaging.util.StringUtils;

/**
 * 用户表
 */
@Entity
@Table(name = "ACS_USER")
public class User extends IdEntity {

	private static final long serialVersionUID = 1L;
	
	private Date loginStart;
	private Integer failedCounts = 0; // 登录失败次数统计，用户失败几次锁定
	
	private String name;
	private String loginName;
	private String email;
	private Integer weight = 1; //权重
	private Boolean updated = false;  // ldap 密码是否更新过
	private String honorificName; //尊称
	private Float mailSize;
	private Boolean sex;
	
	private Long mainDepartmentId;//正职部门
	
	private String password;
	private SecretGrade secretGrade = SecretGrade.COMMON;
	private String cardNumber;  // 集成打印系统，记录打印卡的卡号
	
	//是否启用账户
	private Boolean enabled = true;
	//账户到期标志，true为到期，false为没到期
	private Boolean accountExpired= false;
	//账户锁定标志，true为到期，false为没到期
	private Boolean accountLocked= false;
	//账户解锁的时间
	private Date accountUnlockedTime;
	private Set<DepartmentUser> departmentUsers = new HashSet<DepartmentUser>(0);
	private Set<RoleUser> roleUsers = new HashSet<RoleUser>(0);
	private List<UserInfo> userInfos;
	private Set<WorkgroupUser> workgroupUsers = new HashSet<WorkgroupUser>(0);
	private Long companyId;
	private String roleCodes;
	private MailboxDeploy mailboxDeploy;//邮箱配置
	

	@Column(name = "FK_COMPANY_ID")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@OneToMany(mappedBy = "user")
	public Set<RoleUser> getRoleUsers() {
		return roleUsers;
	}

	public void setRoleUsers(Set<RoleUser> roleUsers) {
		this.roleUsers = roleUsers;
	}

	@Transient
	public UserInfo getUserInfo() {
		if(userInfos != null && !userInfos.isEmpty()){
			return userInfos.get(0);
		}
		return null;
	}
	
	@OneToMany(mappedBy = "user")
	public List<UserInfo> getUserInfos() {
		return userInfos;
	}

	public void setUserInfos(List<UserInfo> userInfos) {
		this.userInfos = userInfos;
	}

	public String getPassword() {
		if(password!=null && password.length()!=32){
			password = Md5.toMessageDigest(password);
		}
		return password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Logger(key="user.loginName")
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return new StringBuilder("User [")
			.append("loginName=").append(loginName)
			.append(", companyId=").append(companyId)
		 	.append(", accountNonExpired=").append(accountExpired)
			.append(", accountNonLocked=").append(accountLocked)
			.append(", failedCounts=").append(failedCounts)
			.toString();
	}

	public Boolean getAccountExpired() {
		return accountExpired;
	}

	public void setAccountExpired(Boolean accountExpired) {
		this.accountExpired = accountExpired;
	}

	public Boolean getAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(Boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	/**
	 * 设置用户与(部门-用户)中间表关系(一对多)
	 */
	@OneToMany(mappedBy = "user")
	@OrderBy("id")
	public Set<DepartmentUser> getDepartmentUsers() {
		return departmentUsers;
	}

	public void setDepartmentUsers(Set<DepartmentUser> departmentUsers) {
		this.departmentUsers = departmentUsers;
	}

	/**
	 * 设置用户与(工作组-用户)中间表关系(一对多)
	 */
	@OneToMany(mappedBy = "user")
	@OrderBy("id")
	public Set<WorkgroupUser> getWorkgroupUsers() {
		return workgroupUsers;
	}

	public void setWorkgroupUsers(Set<WorkgroupUser> workgroupUsers) {
		this.workgroupUsers = workgroupUsers;
	}

	public Integer getFailedCounts() {
		return failedCounts;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setFailedCounts(Integer failedCounts) {
		this.failedCounts = failedCounts;
	}

	public Date getLoginStart() {
		return loginStart;
	}

	public void setLoginStart(Date loginStart) {
		this.loginStart = loginStart;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Boolean getUpdated() {
		return updated;
	}

	public void setUpdated(Boolean updated) {
		this.updated = updated;
	}

	public String getHonorificName() {
		return honorificName;
	}

	public void setHonorificName(String honorificName) {
		this.honorificName = honorificName;
	}
	
	public Float getMailSize() {
		return mailSize;
	}

	public void setMailSize(Float mailSize) {
		this.mailSize = mailSize;
	}

	public Date getAccountUnlockedTime() {
		return accountUnlockedTime;
	}

	public void setAccountUnlockedTime(Date accountUnlockedTime) {
		this.accountUnlockedTime = accountUnlockedTime;
	}

	public Long getMainDepartmentId() {
		return mainDepartmentId;
	}

	public void setMainDepartmentId(Long mainDepartmentId) {
		this.mainDepartmentId = mainDepartmentId;
	}

	public SecretGrade getSecretGrade() {
		return secretGrade;
	}

	public void setSecretGrade(SecretGrade secretGrade) {
		this.secretGrade = secretGrade;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	@Transient
	public String getRoleCodes() {
		return roleCodes;
	}

	public void setRoleCodes(String roleCodes) {
		this.roleCodes = roleCodes;
	}
	
	public boolean equals(User user) {
		if(StringUtils.isEmpty(this.loginName))return false;
		return this.loginName.equals(user.getLoginName());
	}

	public MailboxDeploy getMailboxDeploy() {
		return mailboxDeploy;
	}

	public void setMailboxDeploy(MailboxDeploy mailboxDeploy) {
		this.mailboxDeploy = mailboxDeploy;
	}
}
