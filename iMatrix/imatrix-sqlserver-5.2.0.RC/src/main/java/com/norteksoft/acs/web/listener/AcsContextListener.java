package com.norteksoft.acs.web.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.acs.service.log.LoginUserLogManager;
import com.norteksoft.acs.service.security.SecurityResourceCache;
import com.norteksoft.product.util.AuthFunction;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.ReadAutoAuthUtil;
import com.norteksoft.product.util.WebContextUtils;

/**
 * 通过读取web.xml中的系统编号(systemCode)参数，
 * 预先加载该系统的所有资源信息，供权限系统使用。
 * 
 * @author xiaoj
 */
@SuppressWarnings("deprecation")
public class AcsContextListener implements ServletContextListener{
	
	public void contextDestroyed(ServletContextEvent event) { }

	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		String systemCode = context.getInitParameter("systemCode");
		Object obj = getBeanFromApplicationContext(context, "businessSystemManager");
		WebContextUtils.setBusinessSystem(
				((BusinessSystemManager)obj).getSystemBySystemCode(systemCode));
		initSecurityFunctions(context);
		initLoginUserLog(context);
	}

    private Object getBeanFromApplicationContext(ServletContext servletContext, String beanName) {
    	ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
    	WebContextUtils.setContext(applicationContext);
    	Object object = applicationContext.getBean(beanName);
    	if(object == null){
    		StringBuilder builder = new StringBuilder();
    		builder.append("无法实例化Bean[").append(beanName).append("],系统启动失败");
    		throw new RuntimeException(builder.toString());
    	}
    	return object;
    }
	
	private void initSecurityFunctions(ServletContext context){
		Object obj = getBeanFromApplicationContext(context, "functionManager");
		Object bsobj = getBeanFromApplicationContext(context, "businessSystemManager");
		List<Function> functions = new ArrayList<Function>();
		if(ContextUtils.getSystemId()!=null){
			if(((BusinessSystemManager)bsobj).isParentCodeEmpty(ContextUtils.getSystemId())){//没有父系统，则查找它是否有子系统，如果有则将该系统的资源加入列表functions中
				functions = ((FunctionManager)obj).getFunctionsBySystem(ContextUtils.getSystemId());
				//将子系统的资源加入列表中
				List<Long> systemIds=((BusinessSystemManager)bsobj).getSystemIdsByParentCode(ContextUtils.getSystemCode());
				for(Long sysId:systemIds){
					List<Function> chilFunctions = ((FunctionManager)obj).getFunctionsBySystem(sysId);
					functions.addAll(chilFunctions);
				}
			}
			for(Function function: functions){
				if(StringUtils.isNotEmpty(function.getCode())){
					AuthFunction authFun=new AuthFunction();
					authFun.setFunctionPath(function.getPath());
					authFun.setFunctionId(function.getCode());
					if(function.getBusinessSystem()!=null){
						if(StringUtils.isNotEmpty(function.getBusinessSystem().getParentCode())){//如果是子系统，则在该资源的前面添加系统编码
							String path = function.getPath();
							// /mms/form/data-table-list-data.htm
							String[] paths = path.split("/");
							String syscode = null;
							if(paths.length>=4){
								syscode = paths[1];//获得系统编码为mms
							}
							if(function.getBusinessSystem().getCode().equals(syscode)){//如果获得的系统编码与当前系统编码一致，则该资源前不需再加系统编码
								MemCachedUtils.add(String.valueOf(function.getPath().hashCode()), authFun);
							}else{
								MemCachedUtils.add(String.valueOf(("/"+function.getBusinessSystem().getCode()+function.getPath()).hashCode()), authFun);
							}
						}else{
							MemCachedUtils.add(String.valueOf(function.getPath().hashCode()),authFun);
						}
					}
				}
			}
			String systemCode=ContextUtils.getSystemCode();
			if("imatrix".equals(ContextUtils.getSystemCode())){
				systemCode=null;
			}
			Collection<AuthFunction> autoFuns=ReadAutoAuthUtil.getAutoAuths(systemCode);
			for(AuthFunction autoFun: autoFuns){
				//错误页面没有systemCode
				MemCachedUtils.add(String.valueOf(autoFun.getFunctionPath().hashCode()), autoFun);
			}
		}
	}
	
	private void initLoginUserLog(ServletContext context){
		Object obj = getBeanFromApplicationContext(context, "loginUserLogManager");
		List<LoginLog> logs = ((LoginUserLogManager)obj).getLoginUserLogBySystemId();
		for(LoginLog log: logs){
			log.setExitTime(new Date());
			((LoginUserLogManager)obj).saveLoginUserLog(log);
		}
	}
}
