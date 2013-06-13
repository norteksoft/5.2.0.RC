package com.norteksoft.acs.web.filter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.syssetting.SecuritySetManager;
import com.norteksoft.product.util.ContextUtils;

@SuppressWarnings("deprecation")
public class AcsAuthenticationProcessingFilter extends AuthenticationProcessingFilter {
	private UserManager userManager;
	private SecuritySetManager securitySetManager;
	
	@Override
	protected void onPreAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException {
		super.onPreAuthentication(request, response);
	}

	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, Authentication authResult)
			throws IOException {
		super.onSuccessfulAuthentication(request, response, authResult);
		Integer overdue = securitySetManager.getPasswordIsOverdue(ContextUtils.getUserId(), ContextUtils.getCompanyId());
		if(overdue != null && overdue != 0){
			request.setAttribute("overdue", overdue);
		}
	}

	@Override
	protected void onUnsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed)
			throws IOException {
		request.setAttribute("LOGINERROR", "LOGINERROR");
		super.onUnsuccessfulAuthentication(request, response, failed);
		Object name = obtainUsername(request);
		if(failed instanceof BadCredentialsException && name != null && name.toString().trim().length() > 0){
			User user = userManager.getUserByLoginName(name.toString());
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
	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		if(request.getAttribute("overdue") != null){
			url = "/portal/update-password.action?overdue="+ request.getAttribute("overdue") +"&name=" + obtainUsername(request) +"&url=" + url+ "&id=" + ContextUtils.getUserId();
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
