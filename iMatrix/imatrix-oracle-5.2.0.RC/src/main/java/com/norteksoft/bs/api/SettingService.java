package com.norteksoft.bs.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.holiday.service.HolidayManager;

@Service
@Transactional(readOnly=true)
public class SettingService {

	@Autowired
	private HolidayManager HolidayManager;
	
	/**
	 * 查询给定时间段内的节假日和工作日
	 * @param startDate 开始日期
	 * @param endDate   结束日期
	 * @return Map<String, List<Date>> key: workDate工作日，spareDate节假日
	 */
	public Map<String, List<Date>> getHolidaySettingDays(Date startDate, Date endDate){
		return HolidayManager.getHolidaySettingDays(startDate, endDate);
	}
	
	
}
