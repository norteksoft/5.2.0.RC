package com.norteksoft.mms.authority.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.mms.authority.entity.DataRule;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

/**
 * 数据规则
 * @author Administrator
 *
 */
@Repository
public class DataRuleDao extends HibernateDao<DataRule, Long>{
	/**
	 * 获得所有数据规则
	 * @param page
	 */
	public void getDataRulePage(Page<DataRule> page) {
		this.searchPageByHql(page, "from DataRule d where d.companyId=? ", ContextUtils.getCompanyId());
	}
	
	public List<DataRule> getDataRuleByDataTable(Long tableId){
		return this.find("from DataRule d where d.dataTableId=?  ", tableId);
	}
	
	public List<DataRule> getAllDataRule(){
		return this.find("from DataRule d where d.companyId=?  ", ContextUtils.getCompanyId());
	}

	/**
	 * 根据编号获得规则
	 * @param code
	 * @return
	 */
	public DataRule getDataRuleByCode(String code) {
		return this.findUnique("from DataRule d where d.companyId=? and d.code=? ",ContextUtils.getCompanyId(),code);
	}

	/**
	 * 根据编号和ID获得编号相同且ID不同的规则
	 * @param code
	 * @param id
	 * @return
	 */
	public DataRule getDataRuleByCode(String code, Long id) {
		return this.findUnique("from DataRule d where d.companyId=? and d.code=? and d.id <> ? ",ContextUtils.getCompanyId(),code,id);
	}
	/**
	 * 根据规则类型查询数据规则
	 * @param ruleTypeId
	 * @return
	 */
	public List<DataRule> getDataRulesByRuleType(Long ruleTypeId){
		return this.find("from DataRule d where d.ruleTypeId=?  ", ruleTypeId);
	}

	/**
	 * 根据规则类型查询数据规则
	 * @param page
	 * @param ruleTypeId
	 */
	public void getDataRulesByRuleType(Page<DataRule> page, Long ruleTypeId) {
		this.searchPageByHql(page, "from DataRule d where d.companyId=? and d.ruleTypeId=? ", ContextUtils.getCompanyId(),ruleTypeId);
	}

}
