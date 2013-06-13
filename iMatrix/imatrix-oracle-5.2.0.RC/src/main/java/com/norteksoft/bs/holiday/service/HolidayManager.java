package com.norteksoft.bs.holiday.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.bs.holiday.dao.HolidayDao;
import com.norteksoft.bs.holiday.entity.DateType;
import com.norteksoft.bs.holiday.entity.Holiday;

@Service
@Transactional
public class HolidayManager {
	
	@Autowired
	private HolidayDao holidayDao;
	
	/**
	 * 查询给定日期该月分中所有设置了的特殊日期
	 * @param date
	 * @return
	 */
	public List<Integer> getMonthSetting(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar newCal = Calendar.getInstance();
		newCal.setTime(new Date(0));
		newCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		newCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		newCal.set(Calendar.HOUR_OF_DAY, 0);
		
		Date startDate = newCal.getTime();
		newCal.add(Calendar.MONTH, 1);
		newCal.add(Calendar.MILLISECOND, -1);
		Date endDate = newCal.getTime();
		
		// 所有设置为特殊日期的
		List<Holiday> setting = holidayDao.getHolidaySetting(startDate, endDate);
		newCal.add(Calendar.MILLISECOND, 1);
		newCal.add(Calendar.MONTH, -1);
		int month = newCal.get(Calendar.MONTH);
		newCal.set(Calendar.DAY_OF_MONTH, 1);
		// 一个月中所有的周末
		List<Integer> holidays = new ArrayList<Integer>();
		for(;newCal.get(Calendar.MONTH) == month; newCal.add(Calendar.DAY_OF_YEAR, 1)){
			if(newCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || 
					newCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
				holidays.add(Integer.valueOf(newCal.get(Calendar.DAY_OF_MONTH)));
			}
		}
		
		for(Holiday holiday : setting){
			newCal.setTime(holiday.getSpecialDate());
			if(holiday.getDateType() == DateType.WORKING_DAY){
				holidays.remove(Integer.valueOf(newCal.get(Calendar.DAY_OF_MONTH)));
			}else if(holiday.getDateType() == DateType.HOLIDAY){
				holidays.add(Integer.valueOf(newCal.get(Calendar.DAY_OF_MONTH)));
			}
		}
		return holidays; 
	}
	
	public List<Holiday> getHolidaySetting(Date startDate, Date endDate){
		return holidayDao.getHolidaySetting(startDate, endDate);
	}
	
	/**
	 * 查询给定时间段内的节假日和工作日
	 * @param startDate
	 * @param endDate
	 * @return key: workDate工作日，spareDate节假日
	 */
	@Transactional(readOnly=true)
	public Map<String, List<Date>> getHolidaySettingDays(Date startDate, Date endDate){
		Assert.notNull(startDate, "startDate不能为null");
		Assert.notNull(endDate, "endDate不能为null");
		startDate = clearDateTime(startDate);
		endDate = clearDateTime(endDate);
		List<Holiday> holidaySetting = holidayDao.getHolidaySetting(startDate, endDate);
		endDate.setTime(endDate.getTime()+24*60*60*1000L);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		List<Date> commonDays = new ArrayList<Date>();
		List<Date> holidays = new ArrayList<Date>();
		for( ; cal.getTime().before(endDate); cal.add(Calendar.DAY_OF_YEAR, 1)){
			if(isHoliday(holidaySetting, cal.getTime())){
				holidays.add(cal.getTime());
			}else{
				commonDays.add(cal.getTime());
			}
		}
		Map<String, List<Date>> result = new HashMap<String, List<Date>>();
		result.put("spareDate", holidays);
		result.put("workDate", commonDays);
		return result;
	}
	
	public boolean isHoliday(List<Holiday> holidaySetting, Date date){
		// 优先读取设置中的
		for(Holiday hol : holidaySetting){
			if(hol.getSpecialDate().getTime()==date.getTime()){
				if(hol.getDateType() == DateType.HOLIDAY){
					return true;
				}else{
					return false;
				}
			}
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || 
				cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
			return true;
		}
		return false;
	}
	
	public Date clearDateTime(Date srcDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(srcDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public Date addDate(Date srcDate, int add){
		Calendar cal = Calendar.getInstance();
		cal.setTime(srcDate);
		cal.add(Calendar.DAY_OF_YEAR, add);
		return cal.getTime();
	}
	
	public Holiday getHoliday(Long id){
		return holidayDao.get(id);
	}
	
	public void deleteHoliday(Long id){
		holidayDao.delete(id);
	}
	
	public void deleteHoliday(Holiday holiday){
		holidayDao.delete(holiday);
	}
	
	public void saveHoliday(Holiday holiday){
		holiday.setCompanyId(holidayDao.getCompanyId());
		Holiday hol = holidayDao.getHolidayByDate(holiday.getSpecialDate());
		if(hol!=null){
			hol.setDateType(holiday.getDateType());
			holiday = hol;
		}
		holidayDao.save(holiday);
	}
	
	public void saveHoliday(List<Holiday> holidays){
		for(Holiday holiday : holidays)
			saveHoliday(holiday);
	}
}
