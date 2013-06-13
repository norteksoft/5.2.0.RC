package com.norteksoft.mms.authority.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.authority.dao.DataRuleDao;
import com.norteksoft.mms.authority.dao.RuleTypeDao;
import com.norteksoft.mms.authority.entity.DataRule;
import com.norteksoft.mms.authority.entity.RuleType;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class RuleTypeManager {
	@Autowired
	private RuleTypeDao ruleTypeDao;
	@Autowired
	private DataRuleDao dataRuleDao;

	public RuleType getRuleType(Long id){
		return ruleTypeDao.get(id);
	}
	
	public void saveRuleType(RuleType ruleType){
		ruleTypeDao.save(ruleType);
	}
	
	public String deleteRuleType(Long id){
		List<DataRule> rules = dataRuleDao.getDataRulesByRuleType(id);
		List<RuleType> childrenTypes = getRuleTypeByParnetId(id);
		if(rules.size()>0){
			//要删除的规则类别下包含数据规则，不能删除
			return "NOT_DELETE_HAS_DATA";
		}else if(childrenTypes.size()>0){
			//要删除的规则类别下包含子规则类别，不能删除
			return "NOT_DELETE_HAS_TYPE";
		}else{
			ruleTypeDao.delete(id);
			return "OK";
		}
	}
	
	public void deleteRuleType(RuleType ruleType){
		ruleTypeDao.delete(ruleType);
	}
	
	public Page<RuleType> list(Page<RuleType>page){
		return ruleTypeDao.list(page);
	}
	
	public List<RuleType> listAll(){
		return ruleTypeDao.getAllRuleType();
	}

	public List<RuleType> getRuleTypeByParnetId(Long parentId) {
		String hql = "from RuleType r where r.companyId=? and r.parent!=null and r.parent.id=?";
		return ruleTypeDao.find(hql, ContextUtils.getCompanyId(),parentId);
	}

	public String validateCode(String code, Long id) {
		List<RuleType> types = ruleTypeDao.getRuleTypeByCode(code,id);
		if(types.size()>0){
			return "false";
		}
		return "true";
	}

	public List<RuleType> getRootRuleTypeByCompany() {
		return ruleTypeDao.getRootRuleTypeByCompany();
	}

	public List<RuleType> getTypsByParentId(Long parentId) {
		return ruleTypeDao.getTypsByParentId(parentId);
	}
}
