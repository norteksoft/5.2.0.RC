package com.norteksoft.bs.holiday.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.holiday.entity.DateType;
import com.norteksoft.bs.holiday.entity.Holiday;
import com.norteksoft.bs.holiday.service.HolidayManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/holiday")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "holiday", type = "redirectAction")})
public class HolidayAction extends CrudActionSupport<Holiday>{
	private static final long serialVersionUID = 1L;
	@Autowired
	private HolidayManager holidayManager;
	private Long id;
	private Holiday holiday;	
	private Date startDate; // 开始日期
	private Date endDate; // 结束日期
	private DateType dateType;
	private Date targetDate;
	private String specialDates;
	
	public String list() throws Exception {
		if(targetDate == null) targetDate = new Date();
		List<Integer> dates = holidayManager.getMonthSetting(targetDate);
		specialDates = calendarString(targetDate, dates);
		ApiFactory.getBussinessLogService().log("节假日设置", "查看节假日设置",ContextUtils.getSystemId("bs"));
		return SUCCESS;
	}
	// 日历json格式，包含节假日
	private String calendarString(Date date, List<Integer> dates){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		Calendar newCal = Calendar.getInstance();
		newCal.setTime(new Date(0));
		newCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		newCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		
		int month = newCal.get(Calendar.MONTH);
		boolean hasWeekday = false;
		StringBuilder calString = new StringBuilder("[{");
		for(;newCal.get(Calendar.MONTH) == month; newCal.add(Calendar.DAY_OF_YEAR, 1)){
			if(!hasWeekday){
				calString.append("'firstWeekday':").append(newCal.get(Calendar.DAY_OF_WEEK));
				calString.append(",'year':").append(newCal.get(Calendar.YEAR));
				calString.append(",'month':").append(newCal.get(Calendar.MONTH));
				calString.append(",'days':[");
				hasWeekday = true;
			}
			calString.append("{").append("'day':").append(newCal.get(Calendar.DAY_OF_MONTH));
			calString.append(",'isHoliday':");
			if(dates.contains(newCal.get(Calendar.DAY_OF_MONTH))){
				calString.append("true");
			}else{
				calString.append("false");
			}
			calString.append("},");
		}
		calString.delete(calString.length()-1, calString.length());
		calString.append("]}]");
		return calString.toString();
	}

	@Action("holiday-input")
	public String input() throws Exception {
		return SUCCESS;
	}

	@Action("holiday-save")
	public String save() throws Exception {
		List<Holiday> holidays = new ArrayList<Holiday>();
		if(endDate == null){
			holiday = createHoliday(startDate);
			holidays.add(holiday);
		}else{
			endDate = DateUtils.addDays(endDate, 1);
			for(;startDate.before(endDate);){
				holiday = createHoliday(startDate);
				holidays.add(holiday);
				startDate = DateUtils.addDays(startDate, 1);
			}
		}
		holidayManager.saveHoliday(holidays);
		ApiFactory.getBussinessLogService().log("节假日设置", "保存节假日设置",ContextUtils.getSystemId("bs"));
		return RELOAD;
	}
	
	private Holiday createHoliday(Date specialDate){
		holiday = new Holiday();
		holiday.setSpecialDate(specialDate);
		holiday.setDateType(dateType);
		return holiday;
	}

	@Action("holiday-delete")
	public String delete() throws Exception {
		holidayManager.deleteHoliday(id);
		ApiFactory.getBussinessLogService().log("节假日设置", "删除节假日设置",ContextUtils.getSystemId("bs"));
		return RELOAD;
	}

	protected void prepareModel() throws Exception {
		if(id == null){
			holiday = new Holiday();
		}else{
			holiday = holidayManager.getHoliday(id);
		}
	}

	public Holiday getModel() {
		return null;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setDateType(DateType dateType) {
		this.dateType = dateType;
	}

	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	public String getSpecialDates() {
		return specialDates;
	}

}
