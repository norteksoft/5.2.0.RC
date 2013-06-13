package com.norteksoft.mms.authority.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.authority.dao.ConditionDao;
import com.norteksoft.mms.authority.entity.Condition;
import com.norteksoft.product.orm.Page;

@Service
@Transactional
public class ConditionManager {
	@Autowired
	private ConditionDao conditionDao;

	/**
	 * 根据id删除数据规则条件
	 * @param id
	 */
	public void delete(Long id) {
		conditionDao.delete(id);
	}

	/**
	 * 根据规则id获得数据表规则条件
	 * @param conditionPage
	 * @param id
	 */
	public void getConditionPage(Page<Condition> conditionPage, Long id) {
		conditionDao.getConditionPage(conditionPage,id);
	}

}
