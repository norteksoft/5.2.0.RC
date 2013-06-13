package com.norteksoft.acs.service.syssetting;

import java.util.List;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.organization.CompanyManager;

/**
 * 用户解锁任务
 * 
 * @author xiaoj
 */
public class UnlockUserJob{
	
	private SecuritySetManager securitySetManager;
	private CompanyManager companyManager;
	
	public void setSecuritySetManager(SecuritySetManager securitySetManager) {
		this.securitySetManager = securitySetManager;
	}

	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}

	protected void execute(){
		//获取所有的公司
		List<Company> companys = companyManager.getCompanys();
		for(Company company : companys){
			//解锁用户
			securitySetManager.unclockUserAccount(company.getId());
			//使过期的账户过期
			securitySetManager.expiredUserAccount(company.getId());
		}
	}
	
	
}
