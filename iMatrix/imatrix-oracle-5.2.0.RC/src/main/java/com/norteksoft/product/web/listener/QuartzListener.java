package com.norteksoft.product.web.listener;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.norteksoft.bs.options.entity.Timer;
import com.norteksoft.bs.options.service.JobInfoManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.Scheduler;

public class QuartzListener implements ServletContextListener {
	
	public void contextInitialized(ServletContextEvent arg0) {
		// 查找所有的已经注册的JobInfo， 添加到 Scheduler 
		JobInfoManager manager = (JobInfoManager) ContextUtils.getBean("jobInfoManager");
		List<Timer> corns = manager.getCornInfos();
		for(Timer info : corns){
			Scheduler.addJob(info);
		}
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {
		Scheduler.shutdown();
	}

}
