package com.norteksoft.acs.web.listener;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.security.ui.session.HttpSessionEventPublisher;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.entity.security.User;
import com.norteksoft.acs.service.log.LoginUserLogManager;
import com.norteksoft.acs.web.filter.ExceededOnlineUserFilter;
import com.norteksoft.product.util.ContextUtils;

/**
 * 监听session的创建及销毁
 * 
 * @author xiaoj
 */
public class AcsHttpSessionListener extends HttpSessionEventPublisher {
	//private static final Log loger = LogFactory.getLog(AcsHttpSessionListener.class);
	
	/**
	 * session创建时记录用户登陆日志
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {  }

	/**
	 * session销毁时记录用户登出日志
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null && !"roleAnonymous".equals(authentication.getName())){
			Object manager = getBeanFromApplicationContext(event.getSession().getServletContext(), "loginUserLogManager");
			if(manager instanceof LoginUserLogManager){
				LoginUserLogManager logManager = (LoginUserLogManager) manager;
				//查询最新的登录日志，修改其登出时间
				Object userObj = authentication.getPrincipal();
		    	if(userObj instanceof User){
		    		User user = (User) userObj;
		    		//==============
		    		ExceededOnlineUserFilter.ConcurrencyStorage.remove(user.getCompanyId(), user.getUsername());
		    		//==============
		    		List<LoginLog> logs = logManager.getLoginLogs(user.getUserId());
					for(LoginLog log : logs){
						log.setExitTime(new Date());
						logManager.saveLoginUserLog(log);
					}
		    	}
			}
		}else {//if (event.getSession() instanceof SecurityContextImpl) {
			recordLogout(event.getSession());
		}
		super.sessionDestroyed(event);
	}
	
	public static void recordLogout(HttpSession session){
		if(session == null) return;
		SecurityContextImpl context = (SecurityContextImpl)session.getAttribute("SPRING_SECURITY_CONTEXT");
		if(context != null){
			User user = (User)context.getAuthentication().getPrincipal();
			//==============
    		ExceededOnlineUserFilter.ConcurrencyStorage.remove(user.getCompanyId(), user.getUsername());
    		//==============
			LoginUserLogManager logManager = (LoginUserLogManager)ContextUtils.getBean("loginUserLogManager");
			List<LoginLog> logs = logManager.getLoginLogs(user.getUserId());
			for(LoginLog log : logs){
				log.setExitTime(new Date());
				logManager.saveLoginUserLog(log);
			}
		}
	}
	
    private Object getBeanFromApplicationContext(ServletContext servletContext, String beanName) {
    	ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
    	Object object = applicationContext.getBean(beanName);
    	if(object!= null){
    		return object;
    	}
        return null;
    }
    
}
