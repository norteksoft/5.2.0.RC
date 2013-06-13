package com.norteksoft.product.util;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norteksoft.bs.options.entity.Timer;
import com.norteksoft.bs.options.entity.RestJob;

public class Scheduler {
	public static final String every = "";
	
	static Logger log = LoggerFactory.getLogger(Scheduler.class);
	
	private static SchedulerFactory factory;
	private static org.quartz.Scheduler scheduler;
	public static final String TRIGGER_GROUP = "QuartzTriggers";
	public static final String JOB_GROUP = "QuartzJobs";
	public static final String JOB_PREFIX = "user-job-";
	public static final String TRIGGER_PREFIX = "user-trig-";
	
	static{
		log.debug(" init scheduler start ... ");
		factory = new StdSchedulerFactory();
		try {
			scheduler = factory.getScheduler();
			scheduler.start();
			log.debug(" ... init scheduler end. ");
		} catch (SchedulerException e) {
			log.error(" init quartz exception. ", e);
		}
	}
	
	private Scheduler(){}
	
	/**
	 * 添加任务, 使用默认的触发器, 在每天凌晨执行
	 * @param job 执行的任务, 继承自 com.norteksoft.quartz.Job
	 */
	public static <T extends Job> void addJob(T job){
		addJob(job, null, null);
	}
	
	/**
	 * 添加任务, 使用默认的触发器, 在每天凌晨执行
	 * @param job 执行的任务, 继承自 com.norteksoft.quartz.Job
	 * @param jobId job需要动态更新时必须提供
	 */
	public static <T extends Job> void addJob(T job, String jobId){
		addJob(job, null, jobId);
	}
	
	/**
	 * 使用给定的触发器添加任务
	 * @param job 执行的任务, 继承自 com.norteksoft.quartz.Job
	 * @param trigger 
	 */
	public static <T extends Job> void addJob(T job, Trigger trigger){
		addJob(job, trigger, null);
	}
	
	/**
	 * 使用给定的触发器添加任务
	 * @param job 执行的任务, 继承自 com.norteksoft.quartz.Job
	 * @param trigger 如果需要使用 replaceTrigger 方法，则此处 trigger 必须指定 ID(使用 withIdentity() 方法)
	 * @param jobId job需要动态更新时必须提供
	 */
	public synchronized static <T extends Job> void addJob(T job, Trigger trigger, String jobId){
		if(StringUtils.isBlank(jobId)){
			jobId = Count.triggerId();
		}else{
			jobId = JOB_PREFIX+jobId;
		}
		JobDetail jobDetail = JobBuilder.newJob(job.getClass())
							.withIdentity(jobId, JOB_GROUP)
							.usingJobData(new JobDataMap(getPorpMap(job)))
							.build();
		try {
			if(trigger == null){
				scheduler.scheduleJob(jobDetail, getDefaultTrigger());
			}else{
				scheduler.scheduleJob(jobDetail, trigger);
			}
		} catch (SchedulerException e) {
			log.error(" schedule job exception. ", e);
		}
	}
	
	private static Map<String, Object> getPorpMap(Job job){
		Map<String, Object> map = new HashMap<String, Object>();
		Method[] ms = job.getClass().getMethods();
		String propName = null;
		String firstChar = null;
		Object value = null;
		for(Method m : ms){
			if(m.getName().startsWith("get")&&!"getClass".equals(m.getName())){
				propName = m.getName().substring(3);
				firstChar = propName.substring(0, 1);
				propName = propName.replaceFirst(firstChar, firstChar.toLowerCase());
				try {
					value = m.invoke(job);
				} catch (Exception e) {
					log.error(" invoke get method error[ prop : "+propName+" ]. ", e);
					throw new RuntimeException(e);
				}
				map.put(propName, value);
			}
		}
		return map;
	}
	// =============================================================================
	
	/**
	 * 更换JOB
	 */
	public static void replaceJob(Job job, String jobId){
		JobDetail jobDetail = JobBuilder.newJob(job.getClass())
			.withIdentity(JOB_PREFIX+jobId, JOB_GROUP)
			.usingJobData(new JobDataMap(getPorpMap(job)))
			.build();
		try {
			scheduler.addJob(jobDetail, true);
		} catch (SchedulerException e) {
			log.error(" replace job exception. ", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 更换触发器，触发器的Identity和被替换的触发器的Identity必须相等
	 * @param newTrig 
	 */
	public static void replaceTrigger(Trigger newTrig){
		try {
			scheduler.rescheduleJob(newTrig.getKey(), newTrig);
		} catch (SchedulerException e) {
			log.error(" replace trigger exception. ", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 添加JOB
	 * @param info
	 */
	public static void addJob(Timer info){
		addJob(new RestJob(info), 
				CornBuilder.builderByCornInfo(info),
				info.getId().toString());
	}
	
	/**
	 * 删除JOB
	 */
	public static void deleteJob(Timer info){
		try {
			scheduler.deleteJob(JobKey.jobKey(JOB_PREFIX+info.getId(), JOB_GROUP));
		} catch (SchedulerException e) {
			log.error(" delete job exception. ", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 暂停JOB
	 */
	public static void pauseJob(Timer info){
		try {
			scheduler.pauseJob(JobKey.jobKey(JOB_PREFIX+info.getId(), JOB_GROUP));
		} catch (SchedulerException e) {
			log.error(" pause job exception. ", e);
			throw new RuntimeException(e);
		}
	}
	
	public static void resumeJob(Timer info){
		try {
			scheduler.resumeJob(JobKey.jobKey(JOB_PREFIX+info.getId(), JOB_GROUP));
		} catch (SchedulerException e) {
			log.error(" pause job exception. ", e);
			throw new RuntimeException(e);
		}
	}
	// ============================  Trigger start  ===================================
	
	/**
	 * 根据corn获取Trigger
	 */
	public static Trigger cornTrigger(String corn){
		try {
			CronExpression ce = new CronExpression(corn);
			return TriggerBuilder.newTrigger()
			        .withSchedule(CronScheduleBuilder.cronSchedule(ce))
			        .build();
		} catch (ParseException e) {
			log.error(" Parse corn=["+corn+"] exception. ", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 只在给定的时间执行一次的Trigger
	 * <br/>
	 * 执行完成后，自动清除任务；在没有执行前，可以删除任务
	 * @return
	 */
	public static Trigger getOnceTrigger(Date date){
		return TriggerBuilder.newTrigger()
			.withIdentity(Count.triggerId(), TRIGGER_GROUP)
	        .startAt(date)
	        .build();
	}
	
	/**
	 * 默认Trigger，从今天起，每天凌晨运行
	 * @return
	 */
	public static Trigger getDefaultTrigger(){
		return getDefaultTrigger(null);
	}
	
	/**
	 * 默认Trigger，每天凌晨运行
	 * @return
	 */
	public static Trigger getDefaultTrigger(String triggerId){
		if(StringUtils.isBlank(triggerId)){
			triggerId = Count.triggerId();
		}else{
			triggerId = TRIGGER_PREFIX+triggerId;
		}
		return TriggerBuilder.newTrigger()
			.withIdentity(triggerId, TRIGGER_GROUP)
	        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
	        .build();
	}
	
	// ============================  Trigger end  ===================================
	
	public static void shutdown(){
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			log.error(" shutdown schedule exception. ", e);
		}
	}
	
	static class Count{
		private static int jobCount = 0;
		private static int triggerCount = 0;
		
		public static String jobId(){
			return "job-" + (++jobCount);
		}
		public static String triggerId(){
			return "trig-" + (++triggerCount);
		}
	}
	public static org.quartz.Scheduler getScheduler() {
		return scheduler;
	}
}
