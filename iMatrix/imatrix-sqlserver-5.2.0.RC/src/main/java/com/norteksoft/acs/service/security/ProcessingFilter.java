package com.norteksoft.acs.service.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.concurrent.SessionRegistry;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.ui.cas.CasProcessingFilter;
import org.springframework.security.ui.rememberme.RememberMeServices;
import org.springframework.security.util.SessionUtils;

import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.syssetting.SecuritySetManager;
import com.norteksoft.acs.web.filter.SessionFailFilter;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;

public class ProcessingFilter extends CasProcessingFilter{

	private boolean invalidateSessionOnSuccessfulAuthentication = false;
    private boolean migrateInvalidatedSessionAttributes = true;
    private SessionRegistry sessionRegistry;
    private RememberMeServices rememberMeServices = null;
	private UserManager userManager;
	private SecuritySetManager securitySetManager;
    
	 @SuppressWarnings("unchecked")
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
	            Authentication authResult) throws IOException, ServletException {
	        if (logger.isDebugEnabled()) {
	            logger.debug("Authentication success: " + authResult.toString());
	        }

	        SecurityContextHolder.getContext().setAuthentication(authResult);

	        if (logger.isDebugEnabled()) {
	            logger.debug("Updated SecurityContextHolder to contain the following Authentication: '" + authResult + "'");
	        }

	        if (invalidateSessionOnSuccessfulAuthentication) {
	        	HttpSession session = request.getSession(false);
	        	if(session != null){
//	        		//替换新建的session
//		            String originalSessionId = session.getId();
//		            String mappingId = SingleSignOutFilter.getMappingId(originalSessionId);
		            SessionUtils.startNewSessionIfRequired(request, migrateInvalidatedSessionAttributes, sessionRegistry);
		            
//		            SessionMappingStorage sessionMappingStorage = SingleSignOutFilter.getSessionMappingStorage();
//		            session = request.getSession(false);
//		            sessionMappingStorage.addSessionById(mappingId, session);
	        	}
	        }
	        
	        // 添加登录日志 ================================================
	        addLoginLog(request, authResult);
			// =============================================================

	        String targetUrl = determineTargetUrl(request);
	        
	        //用ticket自动登陆    zzl
	        Map<String,String[]> map=request.getParameterMap();
	        Set<String> parameters =map.keySet();
	        if(parameters!=null&&!parameters.isEmpty()){
		       if(parameters.contains("url")&&parameters.contains("ticket")){
		    	   String url="";//路径
		    	   StringBuilder paramet=new StringBuilder();//参数
		    	   for (String parameter : parameters) {
						if(!parameter.equals("ticket")){
							if(parameter.equals("url")){
								url=map.get(parameter)[0];
							}else{
								paramet.append("&"+parameter+"="+map.get(parameter)[0]);
							}
						}
					}
		    	   targetUrl=url+paramet.toString();
		       }
	       }
	    // =============================================================   
	        
	        if(targetUrl.indexOf("type=rtxLogin") > -1){
	        	targetUrl = targetUrl.replaceAll("type=rtxLogin", "");
	        }
	        if(targetUrl.indexOf("type=auto") > -1){//?type=auto&name=aaa&pwd=ddd
	        	targetUrl = targetUrl.replaceAll("type=auto&", "");
		        	String temp=targetUrl.substring(targetUrl.indexOf("?")+1, targetUrl.indexOf("&")+1);
		        	targetUrl = targetUrl.replaceAll(temp, "");
		        	if(targetUrl.indexOf("&")>-1){
		        		temp=targetUrl.substring(targetUrl.indexOf("?")+1, targetUrl.indexOf("&")+1);
		        	}else{
		        		temp=targetUrl.substring(targetUrl.indexOf("?")+1, targetUrl.length());
		        	}
		        	targetUrl = targetUrl.replaceAll(temp, "");
	        }
	        if(request.getParameter("spring-security-redirect") != null 
	        		&& !request.getParameter("spring-security-redirect").equals("")){
	        	targetUrl = request.getParameter("spring-security-redirect");
	        }
	        if (logger.isDebugEnabled()) {
	            logger.debug("Redirecting to target URL from HTTP Session (or default): " + targetUrl);
	        }

	        onSuccessfulAuthentication(request, response, authResult);

	        rememberMeServices.loginSuccess(request, response, authResult);

	        // Fire event
	        if (this.eventPublisher != null) {
	            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
	        }
	        
	        String tgt = request.getParameter("tgt");
			if(tgt!=null){
				Cookie c = new Cookie("CASTGC", tgt);
				c.setPath("/");
				//c.setDomain(".norteksoft.com");
				response.addCookie(c);
			}
			
	        sendRedirect(request, response, targetUrl);
	    }
	 
	    public void setInvalidateSessionOnSuccessfulAuthentication(boolean invalidateSessionOnSuccessfulAuthentication) {
	        this.invalidateSessionOnSuccessfulAuthentication = invalidateSessionOnSuccessfulAuthentication;
	    }
	    public void setMigrateInvalidatedSessionAttributes(boolean migrateInvalidatedSessionAttributes) {
	        this.migrateInvalidatedSessionAttributes = migrateInvalidatedSessionAttributes;
	    }
	    public void setSessionRegistry(SessionRegistry sessionRegistry) {
	        this.sessionRegistry = sessionRegistry;
	    }
	    public RememberMeServices getRememberMeServices() {
	        return rememberMeServices;
	    }

	    public void setRememberMeServices(RememberMeServices rememberMeServices) {
	        this.rememberMeServices = rememberMeServices;
	    }
	    
	    /**
	     * 设置session失效时间
	     * @param request
	     * @param authResult
	     */
	    private void addLoginLog(HttpServletRequest request, Authentication authResult){
	        Object object = ContextUtils.getBean("securitySetManager");
			if(object instanceof SecuritySetManager){
				Integer minutes = ((SecuritySetManager)object).getLoginTimeoutValues(ContextUtils.getCompanyId());
		    	HttpSession session = request.getSession(false);
				//没有设置session过期时间则设置为半小时，否则取设置的值
	        	if(session != null){
					if(minutes == null) session.setMaxInactiveInterval(30*60 + SessionFailFilter.ADDITION_ACTIVE_TIME);
					else session.setMaxInactiveInterval(minutes*60 + SessionFailFilter.ADDITION_ACTIVE_TIME);
	        	}
			}
	    }
	    
		@Override
		protected void onSuccessfulAuthentication(HttpServletRequest request,
				HttpServletResponse response, Authentication authResult)
				throws IOException {
			super.onSuccessfulAuthentication(request, response, authResult);
			Integer overdue = securitySetManager.getPasswordIsOverdue(ContextUtils.getUserId(), ContextUtils.getCompanyId());
			if(overdue != null){
				request.setAttribute("overdue", overdue);
			}
		}

		@Override
		protected void onUnsuccessfulAuthentication(HttpServletRequest request,
				HttpServletResponse response, AuthenticationException failed)
				throws IOException {
			request.setAttribute("LOGINERROR", "LOGINERROR");
			super.onUnsuccessfulAuthentication(request, response, failed);
			Object obj = failed.getExtraInformation();
			if(obj instanceof com.norteksoft.acs.entity.security.User){
				if(!((com.norteksoft.acs.entity.security.User)obj).isEnabled()){
					request.setAttribute("ACS_USER_ENABLED", "true");
				}
			}
			Object name = request.getParameter("j_username");
			if(failed instanceof BadCredentialsException && name != null && name.toString().trim().length() > 0){
				com.norteksoft.acs.entity.organization.User user = userManager.getUserByLoginName(name.toString());
				if(user != null){
					//用户目前登陆失败的次数
					Integer counts = user.getFailedCounts();
					if(counts == null) counts = 0;
					counts += 1;
					//系统设置的允许用户登录失败的次数
					Integer allowedCounts = securitySetManager.getLoginFailedCounts(user.getCompanyId());
					if(allowedCounts == null) allowedCounts = 3;
					Date startTiem = user.getLoginStart();
					if(startTiem == null) startTiem = new Timestamp(new Date().getTime());
					//一小时内的登录
					if(new Date().getTime() - startTiem.getTime() < 3600000){
						if(counts >= allowedCounts){
							user.setAccountLocked(false);
							user.setFailedCounts(0);
						}else{
							//用户第一次登录失败时间
							if(counts == 1) 
								user.setLoginStart(new Timestamp(new Date().getTime()));
							user.setFailedCounts(counts);
						}
						userManager.saveUser(user);
					}else{//若是一小时后再登陆，则从新设置开始登陆时间、失败次数
						user.setLoginStart(new Timestamp(new Date().getTime()));
						user.setFailedCounts(1);
						userManager.saveUser(user);
					}
				}
			}
		}

		@Override
		protected void sendRedirect(HttpServletRequest request,
				HttpServletResponse response, String url) throws IOException {
			if(request.getAttribute("overdue") != null){
				url = "/portal/update-password.action?overdue="+ request.getAttribute("overdue") +"&name=" + URLEncoder.encode(ContextUtils.getUserName(),"utf-8") +"&url=" + url+ "&id=" + ContextUtils.getUserId()
				+"&resourceCtx="+PropUtils.getProp("host.resources");;
			}
			Object ajaxUrl = request.getParameter("ajaxURL");
			if(ajaxUrl != null && !"".equals(ajaxUrl.toString().trim())){
				url = ajaxUrl.toString();
				Object exception = request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
				if(exception instanceof Exception){
					if(url.contains("?")){
						url += "&error_message=\"" + ((Exception)exception).getMessage() + "\"";
					}else{
						url += "?error_message=\"" + ((Exception)exception).getMessage() + "\"";
					}
				}
			}
			if(request.getAttribute("ACS_USER_ENABLED") != null){
				url = url+"&forbidden=true";
			}
			super.sendRedirect(request, response, url);
		}

		@Required
		public void setUserManager(UserManager userManager) {
			this.userManager = userManager;
		}

		@Required
		public void setSecuritySetManager(SecuritySetManager securitySetManager) {
			this.securitySetManager = securitySetManager;
		}
}
