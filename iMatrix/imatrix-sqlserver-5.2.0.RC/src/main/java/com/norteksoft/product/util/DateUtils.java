package com.norteksoft.product.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
	public static String folder="NortekSoft";
	
	/**
	 * 根据给定的 年 和 月 返回 当月的第一天和最后一天
	 * @param 年，月
	 * @return String[0]-->当月的第一天,String[1]-->当月的最后一天
	 */
	public static Date getStartAndEnd(String yearandmonth){
		try {
			String str = new String();
			str = yearandmonth+"-"+"01"+" "+"00:00:00";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			Date d=format.parse(str);
			return d;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getStart(String yearandmonth){
		String str = new String();
		str = yearandmonth+"-"+"01";
		return str;
	}
	
	/**
	 * 根据当月第一天获取当月的最后一天
	 * @param 当月第一天的日期
	 * @return 当月最后一天的日期
	 */
	public static Date getLastDay(String beginDate){
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(beginDate);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(Calendar.MONTH,1);
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			date = calendar.getTime();
			String s=format.format(date);
			s=s+" "+"23:00:00";
			return format.parse(s);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 当前是星期几
	 * @param calendar
	 * @return
	 */
	public static String getWeek(Calendar calendar) {
		int r=calendar.get(Calendar.DAY_OF_WEEK);
		 switch (r) {
		  case 1:
			  return "星期日";
		  case 2:
			  return "星期一";
		  case 3:
			  return "星期二";
		  case 4:
			  return "星期三";
		  case 5:
			  return "星期四";
		  case 6:
			  return "星期五";
		  case 7:
			  return "星期六";
		 }
		 return null;
	}
	
	/**
	 * 得到当前天的上午，下午
	 * @return
	 */
	public static Date getDate(Date date,int h,int s,int m){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), h, s, m);
		return cal.getTime();
	}
	
	/***
	 * 计算2个日期之间的天数
	 * @param beginTime
	 * @param endTime
	 * @return 相差的天数
	 */
	public static long getDateMinus(Date beginTime,Date endTime)throws Exception{
			long time = (endTime.getTime()-beginTime.getTime())/1000/60/60/24;
			return time;
	}
	
	/***
	 * 计算2个日期之间的天数
	 * @param beginTime
	 * @param endTime
	 * @return 相差的天数
	 */
	public static long getDateMinus(String beginTime,String endTime)throws Exception{
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");  
			try {
				Date begin = s.parse(beginTime);
				Date end = s.parse(endTime);
				long time = (end.getTime()-begin.getTime())/1000/60/60/24;
				return time;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return 0;
	}
	
}
