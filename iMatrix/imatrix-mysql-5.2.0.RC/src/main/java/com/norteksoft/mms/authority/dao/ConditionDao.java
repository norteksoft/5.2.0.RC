package com.norteksoft.mms.authority.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.mms.authority.entity.Condition;
import com.norteksoft.mms.authority.entity.DataRule;
import com.norteksoft.mms.authority.enumeration.FieldOperator;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.enumeration.LogicOperator;

/**
 * 数据规则条件
 * @author Administrator
 *
 */
@Repository
public class ConditionDao extends HibernateDao<Condition, Long>{

	/**
	 * 根据规则ID删除条件
	 * @param valueOf
	 */
	public void deleteConditionByRuleId(Long dataRuleId) {
		this.batchExecute("delete Condition c where c.companyId=? and c.dataRule.id=? ", ContextUtils.getCompanyId(),dataRuleId);
	}

	/**
	 * 根据规则id获得数据表规则条件
	 * @param conditionPage
	 * @param id
	 */
	public void getConditionPage(Page<Condition> conditionPage, Long id) {
		this.searchPageByHql(conditionPage,"from Condition c where c.companyId=? and c.dataRule.id=? ",ContextUtils.getCompanyId(),id);
	}
	
	/**
	 * 根据规则id获得数据表规则条件
	 * @param conditionPage
	 * @param id
	 */
	public List<Condition> getConditionsByDataRuleId(Long id) {
		return this.find("from Condition c where c.companyId=? and c.dataRule.id=? ",ContextUtils.getCompanyId(),id);
	}

	public Condition getCondition(String field, FieldOperator operator,
			LogicOperator lgicOperator, DataType dataType, String conditionValue,
			Long dataRuleId) {
		List<Condition> conditions=this.find("from Condition c where c.companyId=? and c.field=? and c.operator=? and c.lgicOperator=? and c.dataType=? and c.conditionValue=? and c.dataRule.id=? ",ContextUtils.getCompanyId(),field,operator,lgicOperator,dataType,conditionValue,dataRuleId);
		if(conditions!=null && conditions.size()>0){
			return conditions.get(0);
		}else{
			return null;
		}
	}

}
