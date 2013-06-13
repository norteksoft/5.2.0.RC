package com.norteksoft.portal.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.Countdown;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class CountDownDao extends HibernateDao<Countdown, Long>{
	/**
	 * 根据userId与companyId得到倒计时
	 * @param userId
	 * @param companyId
	 * @return
	 */
	public Countdown getCountDownByUserIdAndCompanyId(Long userId, Long companyId){
		return this.findUnique("from CountDown cd where cd.userId=? and cd.companyId=?", userId, companyId);
	}
}
