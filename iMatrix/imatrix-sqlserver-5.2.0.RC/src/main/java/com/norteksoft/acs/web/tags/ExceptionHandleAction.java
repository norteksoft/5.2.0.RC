package com.norteksoft.acs.web.tags;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;
public class ExceptionHandleAction extends CRUDActionSupport<Object>{
	private static final long serialVersionUID = 1L;
	private String type;//500,403,404
	private String sessionFail;//登录超时
	private String expired;//该用户已经在别处登录
	private String exceed; // 超出最大人数限制
	private String forbidden; // 是否被禁用
	
	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String list() throws Exception {
		HttpServletRequest request=Struts2Utils.getRequest();
		Object ex = request.getAttribute("javax.servlet.error.exception");
		if(ex != null && ex instanceof Exception){
			logger.error("Action Exception: ", (Exception)ex);
		}
		String webapp = request.getContextPath();
		String resourceCtx=PropUtils.getProp("host.resources");
		request.setAttribute("ctx", webapp);
		request.setAttribute("resourceCtx", StringUtils.isEmpty(resourceCtx)?webapp:resourceCtx);
		int errorCode=500;
		if(StringUtils.isNotEmpty(type)){
			String[] types=type.split(",");
			if(types.length>0){
				type=types[0];
				errorCode=Integer.parseInt(type);
			}
		}
		
		String result="500";
		switch(errorCode){
			case 403:
				result="403";
				break;
			case 404:
				result="404";
				break;
		}
		return result;
	}

	@Override
	protected void prepareModel() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getSessionFail() {
		return sessionFail;
	}

	public void setSessionFail(String sessionFail) {
		this.sessionFail = sessionFail;
	}

	public String getExpired() {
		return expired;
	}

	public void setExpired(String expired) {
		this.expired = expired;
	}

	public String getExceed() {
		return exceed;
	}

	public void setExceed(String exceed) {
		this.exceed = exceed;
	}

	public String getForbidden() {
		return forbidden;
	}

	public void setForbidden(String forbidden) {
		this.forbidden = forbidden;
	}
	
}
