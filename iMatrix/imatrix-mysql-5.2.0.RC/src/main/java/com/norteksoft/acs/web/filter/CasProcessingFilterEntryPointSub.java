package com.norteksoft.acs.web.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AuthenticationEntryPoint;
import org.springframework.security.ui.cas.CasProcessingFilterEntryPoint;

public class CasProcessingFilterEntryPointSub extends
		CasProcessingFilterEntryPoint implements AuthenticationEntryPoint, InitializingBean{
	private boolean encodeServiceUrlWithSessionId = true;

	@Override
	public void commence(ServletRequest servletRequest,
			ServletResponse servletResponse,
			AuthenticationException authenticationException)
			throws IOException, ServletException {

		final HttpServletResponse response = (HttpServletResponse) servletResponse;
        String urlEncodedService = CommonUtils.constructServiceUrl(null, response, this.getServiceProperties().getService(), null, "ticket", this.encodeServiceUrlWithSessionId);
        String type=servletRequest.getParameter("type");
        if(type!=null){
        	if("rtxLogin".equals(type)){
        		urlEncodedService=urlEncodedService+";type="+type;
        	}else if("auto".equals(type)){
        		urlEncodedService=urlEncodedService+";type="+type+";"+servletRequest.getParameter("name")+"="+servletRequest.getParameter("pwd");
        	}
        }
        // 告诉  cas 访问的系统编号
        //String uri = ((HttpServletRequest)servletRequest).getRequestURI();
        //String sys = SystemUrls.getSysCodeFromUri(uri);
        String redirectUrl = CommonUtils.constructRedirectUrl(this.getLoginUrl(), "service", urlEncodedService, this.getServiceProperties().isSendRenew(), false);
        //redirectUrl = redirectUrl.replace("?service=", "?sys="+sys+"&service=");
        response.sendRedirect(redirectUrl);
	}
}
