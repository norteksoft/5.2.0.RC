package com.norteksoft.acs.web.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SessionFailFilter implements Filter {
	
	public static final int ADDITION_ACTIVE_TIME = 8*60*60; 
	private static final int FAIL_NUMBER = 3613; // 当session.getMaxInactiveInterval()时间为1小时13秒时，表示session已经失效 

	private static Log log = LogFactory.getLog(SessionFailFilter.class);

	public void destroy() { }

	public void doFilter(ServletRequest req, ServletResponse rep,
			FilterChain chan) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;
		HttpSession session = request.getSession(false);
		//这两个地址不用判断session过期
		if(request.getRequestURI().contains("/j_spring_security_logout")||request.getRequestURI().contains("/exception-handle.action")){
			if(StringUtils.isNotEmpty(request.getParameter("expired"))){
				cleanCookie(request, response);
			}
		}else {
			if (isSessionFailed(session)) {
				session.setMaxInactiveInterval(FAIL_NUMBER);
				//session.invalidate();
				//cleanCookie(request, response);
				//SecurityContextHolder.clearContext();
				response.sendRedirect(request.getContextPath()+"/portal/exception-handle.action?type=403&sessionFail=yes");
				return;//重定向后就不需要传递到其他filter了
			}
		}
		chan.doFilter(req, rep);
	}

	//判断session超时
	private boolean isSessionFailed(HttpSession session) {
		boolean sessionFail=false;
		/*
		 * session为null说明是第一次登陆（这时要出正常的登陆页面）或者是session超时后被回收了（这时要出403页面）
		 * 代码无法判断这两种情况
		 * 解决方案 创建session时设置session永不失效，通过这个filter来判断时间使session失效
		 */
		if(session==null){
			sessionFail=false;
			log.debug("session is null");
		}else{
			sessionFail = isSessionFail(session);
			if(sessionFail) {
				ExceededOnlineUserFilter.removeConcurrencyStorage(session);
			}
		}
		return sessionFail;
	}
	
	public static boolean isSessionFail(HttpSession session){
		if(session.getMaxInactiveInterval()==FAIL_NUMBER) return true;
		int maxActiveTime = session.getMaxInactiveInterval() - ADDITION_ACTIVE_TIME;
		log.debug("session Inactive Interval:[" + maxActiveTime + "]");
		long lastAccessedTime = session.getLastAccessedTime();
		Date currentTime = new Date();// 当前时间
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(lastAccessedTime));
		cal.add(Calendar.SECOND, maxActiveTime);
		Date failTime = cal.getTime();// 失效时间
		return (maxActiveTime > 0 && currentTime.after(failTime))?true:false;
	}
	
	private void cleanCookie(HttpServletRequest request, HttpServletResponse response){
		Cookie[] cookie = request.getCookies();
		if (cookie != null) {
			for (int i = 0; i < cookie.length; i++) {
				cookie[i].setValue(null);
				response.addCookie(cookie[i]);
			}
		}
	}

	public String dateFormat(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	public void init(FilterConfig arg0) throws ServletException { }

}
