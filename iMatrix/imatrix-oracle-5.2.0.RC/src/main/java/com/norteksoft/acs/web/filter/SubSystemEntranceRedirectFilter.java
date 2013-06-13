package com.norteksoft.acs.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.norteksoft.product.util.PropUtils;

public class SubSystemEntranceRedirectFilter implements Filter {
	
	public void destroy() { }

	public void doFilter(ServletRequest req, ServletResponse rep,
			FilterChain chan) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;
		
		String url=request.getRequestURI();
		if (PropUtils.isBasicSystem(url)) {
			if(url.endsWith("/")){
				url=url.substring(0,url.lastIndexOf("/"));
			}
			String systemCode=url.substring(url.lastIndexOf("/")+1);
			String redirectUrl=PropUtils.getProp("redirectUrl.properties",systemCode);
			response.sendRedirect(url+redirectUrl+"?_r=1");
		}else{
			chan.doFilter(req, rep);
		}
	}

	public void init(FilterConfig arg0) throws ServletException { }
}
