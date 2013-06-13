package com.norteksoft.wf.engine.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jbpm.internal.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;


/**
 * 在系统启动时初始化所有的流程定义
 * @author Administrator
 */
public class WorkflowContextListener  implements ServletContextListener{
	private static final Log log = Log.getLog(WorkflowContextListener.class.getName());
	Logger logg =  LoggerFactory.getLogger(WorkflowContextListener.class);
	public void contextDestroyed(ServletContextEvent event) { }

	public void contextInitialized(ServletContextEvent event) {
//		try {
//			Security.checkAuthorizationFile();
//		} catch (IllegalAccess e) {
//			log.error(e.getMessage());
//		}
		ServletContext context = event.getServletContext();
		initSecurityWorkflowDefinitions(context);
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
	
	private void initSecurityWorkflowDefinitions(ServletContext context){
		logg.debug("================================================initSecurityWorkflowDefinitions");
		Object obj = getBeanFromApplicationContext(context, "workflowDefinitionManager");
		((WorkflowDefinitionManager)obj).initAllWorkflowDefinition();
		
		//将标准表单中所有签章字段存入缓存
		
		Object viewManager = getBeanFromApplicationContext(context, "formViewManager");
		((FormViewManager)viewManager).getAllSignatureFields();
		//给实例中流程编码赋值,再启动注释该语句
//		obj=getBeanFromApplicationContext(context, "workflowInstanceManager");
//		((WorkflowInstanceManager)obj).initAllWorkflowInstances();
	}
	
}
