package com.norteksoft.bs.holiday.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.holiday.entity.Holiday;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class HolidayDao extends HibernateDao<Holiday, Long>{
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	public List<Holiday> getHolidaySetting(Date startDate, Date endDate){
		return this.find("from Holiday h where h.companyId=? and h.specialDate between ? and ?", getCompanyId(), startDate, endDate);
	}
	
	public Holiday getHolidayByDate(Date date){
		List<Holiday> list = this.find("from Holiday h where h.companyId=? and h.specialDate=?", getCompanyId(), date);
		if(list.size() == 1) return list.get(0);
		return null;
	}
}
