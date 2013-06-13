package com.norteksoft.acs.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionValidator implements Filter{

	public void destroy() { }
	
	public void doFilter(ServletRequest req, ServletResponse rep,
			FilterChain chan) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;

        String requestURI = request.getRequestURI();
        String con = null;
        	
        con = request.getContextPath();
        if(request.getParameter("logoutSuccessUrl")!=null 
        		&& request.getQueryString().indexOf("web")!=-1
        		     && request.getQueryString().indexOf("show")!=-1){
        	chan.doFilter(req, rep);
        }else{
        	
        	String url = requestURI.replace(con, "");
        	
			if ("/j_spring_security_logout".equals(url) && request.getSession() != null){
        		request.getSession().invalidate();
        		Cookie[] cookie = request.getCookies(); 
				if(cookie != null){
		        	for(Cookie  c : cookie){
						if("JSESSIONID".equals(c.getName())){
							c.setValue("");
							((HttpServletResponse)rep).addCookie(c);
							break;
						}
					}
	        	}
        	}
        	
        	chan.doFilter(req, rep);
        }
	}

	public void init(FilterConfig arg0) throws ServletException {
		
		
	}

}
