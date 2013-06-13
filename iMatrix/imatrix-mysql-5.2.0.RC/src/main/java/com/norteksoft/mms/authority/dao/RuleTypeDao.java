package com.norteksoft.mms.authority.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.mms.authority.entity.RuleType;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class RuleTypeDao extends HibernateDao<RuleType, Long> {
		
	public Page<RuleType> list(Page<RuleType> page){
		return findPage(page, "from RuleType r where companyId=?",ContextUtils.getCompanyId());
	}
	
	public List<RuleType> getAllRuleType(){
		return find("from RuleType r where r.companyId=?", ContextUtils.getCompanyId());
	}

	public List<RuleType> getRuleTypeByCode(String code, Long id) {
		if(id==null){
			return find("from RuleType r where r.code=? and r.companyId=?",code,ContextUtils.getCompanyId());
		}else{
			return find("from RuleType r where r.code=? and r.id<>? and r.companyId=?",code,id,ContextUtils.getCompanyId());
		}
	}

	public List<RuleType> getRootRuleTypeByCompany() {
		return find("from RuleType r where r.parent.id=null and r.companyId=? order by r.id ",ContextUtils.getCompanyId());
	}

	public List<RuleType> getTypsByParentId(Long parentId) {
		return find("from RuleType r where r.parent.id=? and r.companyId=? order by r.id ",parentId,ContextUtils.getCompanyId());
	}
	
	public RuleType getRuleTypeByCode(String code){
		return findUnique("from RuleType r where r.code=? and r.companyId=?", code,ContextUtils.getCompanyId());
	}
}
