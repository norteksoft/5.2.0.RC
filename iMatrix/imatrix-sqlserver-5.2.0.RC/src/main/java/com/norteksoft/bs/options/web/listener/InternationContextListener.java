package com.norteksoft.bs.options.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jbpm.internal.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.norteksoft.bs.options.service.InternationManager;


public class InternationContextListener implements ServletContextListener{
	private static final Log log = Log.getLog(InternationContextListener.class.getName());
	Logger logg =  LoggerFactory.getLogger(InternationContextListener.class);
	public void contextDestroyed(ServletContextEvent arg0) { }

	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		initInternations(context);
	}
	
	private Object getBeanFromApplicationContext(ServletContext servletContext, String beanName) {
    	ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
    	Object object = applicationContext.getBean(beanName);
    	if(object == null){
    		StringBuilder builder = new StringBuilder();
    		builder.append("无法实例化Bean[").append(beanName).append("],系统启动失败");
    		log.debug(builder.toString());
    		throw new RuntimeException(builder.toString());
    	}
    	return object;
    }
	
	private void initInternations(ServletContext context){
		logg.debug("================================================initInternations");
		Object obj = getBeanFromApplicationContext(context, "internationManager");
		((InternationManager)obj).initAllInternations();
	}

}
