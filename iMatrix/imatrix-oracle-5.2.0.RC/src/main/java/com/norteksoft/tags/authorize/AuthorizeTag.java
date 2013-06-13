package com.norteksoft.tags.authorize;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.SystemUrls;

import flex.messaging.util.StringUtils;

public class AuthorizeTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private String code;//资源编码
	private String systemCode;//系统编码，当是主子系统时才需给该值赋值

	//标签开始时调用的出来方法
	@Override
	public int doStartTag() throws JspException {
		try {
			String resultUrl = "";
			String[] codes = code.split(",");
			FunctionManager functionManager=(FunctionManager)ContextUtils.getBean("functionManager");
			if(StringUtils.isEmpty(systemCode)){
				systemCode = ContextUtils.getSystemCode();
			}
			for(String mycode:codes){
				if(ContextUtils.isAuthority(mycode)){
					String path = functionManager.getFunctionPathByCode(mycode, systemCode);
					resultUrl = SystemUrls.getSystemUrl(systemCode)+ path;
					break;
				}
			}
			//将信息内容输出到JSP页面
			pageContext.getOut().print(resultUrl);
		} catch (Exception e) {
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
		//跳过标签体的执行
//		return SKIP_BODY;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
}
