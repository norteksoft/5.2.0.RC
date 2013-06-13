package com.norteksoft.product.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Trigger;

import com.norteksoft.bs.options.entity.Timer;
import com.norteksoft.bs.options.enumeration.TimingType;

public class CornBuilder {
	private static Log log = LogFactory.getLog(CornBuilder.class);
	
	private Corn corn;
	
	private CornBuilder(){
		corn = new Corn();
	}
	
	public static CornBuilder newCorn(){
		return new CornBuilder();
	}
	
	public String builder(){
		return corn.builder();
	}
	
	/**
	 * 分钟中的秒设置,默认为第 0 秒<br/>
	 * eg: <ol><li> * 每秒一次
	 * <li> 3  第三秒钟一次
	 * <li> 0/5   每五秒钟一次
	 * <li> 0-5  0到5秒钟各一次
	 * <li> 10,20  第10秒和第20秒各一次
	 * @param second 只有以上5种格式
	 * @return CornBuilder
	 */
	public CornBuilder second(String second){
		corn.second=second;
		return this;
	}
	
	public CornBuilder minute(String minute){
		corn.minute=minute;
		return this;
	}

	public CornBuilder hour(String hour){
		corn.hour=hour;
		return this;
	}
	
	public CornBuilder dayOfMonth(String dayOfMonth){
		corn.dayOfMonth=dayOfMonth;
		return this;
	}
	
	public CornBuilder month(String month){
		corn.month=month;
		return this;
	}
	
	public CornBuilder dayOfWeek(String dayOfWeek){
		corn.dayOfWeek=dayOfWeek;
		return this;
	}
	
	public CornBuilder year(String year){
		corn.dayOfWeek=year;
		return this;
	}
	
	private class Corn{
		String second = "0";
		String minute = "0";
		String hour = "0";
		String dayOfMonth = "*";
		String month = "*";
		String dayOfWeek = "?";
		String year = "*";
		
		String builder(){
			StringBuilder corn = new StringBuilder();
			if(!"?".equals(dayOfWeek)&&"*".equals(dayOfMonth)){
				dayOfMonth = "?";
			}
			corn.append(second).append(" ")
				.append(minute).append(" ")
				.append(hour).append(" ")
				.append(dayOfMonth).append(" ")
				.append(month).append(" ")
				.append(dayOfWeek).append(" ")
				.append(year);
			return corn.toString();
		}
	}
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	
	public static Trigger builderByCornInfo(Timer info){
		Trigger tg = null;
		String[] time = null;
		switch (info.getTimingType()) {
		case everyDate:  // 09:37
			log.debug("everyDate="+info.getCorn());
			time = info.getCorn().split(":");
			tg = Scheduler.cornTrigger(CornBuilder.newCorn().hour(time[0]).minute(time[1]).builder());
			break;
		case everyWeek:  // 1,2,3,4,5,6,7 09:37
			log.debug("everyWeek="+info.getWeekTime()+" "+info.getCorn());
			time = info.getCorn().split(":");
			tg = Scheduler.cornTrigger(CornBuilder.newCorn().dayOfWeek(prexData(info.getWeekTime())).hour(time[0]).minute(time[1]).builder());
			break;
		case everyMonth:  // 27,28,29,30,31,L 09:38, 当有指定日期时，不能包含最后一天的L
			time = info.getCorn().split(":");
			log.debug("everyMonth="+info.getDateTime()+" "+info.getCorn());
			tg = Scheduler.cornTrigger(CornBuilder.newCorn().dayOfMonth(prexData(info.getDateTime())).hour(time[0]).minute(time[1]).builder());
			break;
		case appointTime:  // 2012-06-15 09:38
			try {
				log.debug("appointTime="+info.getAppointTime());
				tg = Scheduler.getOnceTrigger(format.parse(info.getAppointTime()));
			} catch (ParseException e) {
				log.error("appointTime="+info.getAppointTime(), e);
				throw new RuntimeException(e);
			}
			break;
		case appointSet:  // 0 15 10 * * ? 2005
			log.debug("appointSet ="+info.getAppointSet());
			tg = Scheduler.cornTrigger(info.getAppointSet());
			break;
		}
		return tg;
	}
	
	public static void main(String[] args) throws Exception {
		String[] time = null;
		
		Timer i = new Timer();
		i.setTimingType(TimingType.everyDate);
		i.setCorn("09:37");
		time = i.getCorn().split(":");
		System.out.println(CornBuilder.newCorn().hour(time[0]).minute(time[1]).builder());
		
		i = new Timer();
		i.setTimingType(TimingType.everyWeek);
		i.setCorn("09:37");
		time = i.getCorn().split(":");
		System.out.println(CornBuilder.newCorn().dayOfWeek("1,2,3,4,5,6,7,L").hour(time[0]).minute(time[1]).builder());
		
		i = new Timer();
		i.setTimingType(TimingType.everyMonth);
		i.setCorn("09:37");
		time = i.getCorn().split(":");
		System.out.println(CornBuilder.newCorn().dayOfMonth("27,28,29,30,31,L").hour(time[0]).minute(time[1]).builder());
		
		System.out.println(format.parse("2012-06-15 09:38"));
		
	}
	
	public static String prexData(String data){
		if(StringUtils.isNotEmpty(data)){
			String[] datas =data.split(",");
			StringBuilder bu = new StringBuilder();
			for (String str : datas) {
				bu.append(str.split("_")[1]+",");
			}
			return StringUtils.removeEnd(bu.toString(), ",");
		}
		return null;
	}
}
