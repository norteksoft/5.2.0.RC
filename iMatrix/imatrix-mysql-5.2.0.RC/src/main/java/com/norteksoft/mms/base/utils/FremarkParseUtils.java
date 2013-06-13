package com.norteksoft.mms.base.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.struts2.ServletActionContext;

import freemarker.cache.StringTemplateLoader;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.IncludePage;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateModel;




public class FremarkParseUtils {
	private static final String KEY_JSP_TAGLIBS = "JspTaglibs";
	private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";
	private static final String ATTR_SESSION_MODEL = ".freemarker.Session";
	private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
	private static final String KEY_APPLICATION = "Application";
	private static  final String KEY_APPLICATION_PRIVATE = "__FreeMarkerServlet.Application__";
	private static final String KEY_SESSION = "Session";
	private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
	private static final String ATTR_REQUEST_PARAMETERS_MODEL = ".freemarker.RequestParameters";
    
	private static final String KEY_REQUEST = "Request";
	private static final String KEY_INCLUDE = "include_page";
	private static final String KEY_REQUEST_PRIVATE = "__FreeMarkerServlet.Request__";
	private static final String KEY_REQUEST_PARAMETERS = "RequestParameters";
	
	private static TemplateModel createModel(PageContext pageContext,Map<String, Object> map) {
		ObjectWrapper wrapper = ObjectWrapper.DEFAULT_WRAPPER;
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
		HttpServlet servlete = new ProxyServlet(pageContext.getServletConfig(), pageContext.getServletContext());
		ServletContextHashModel servletContextModel = new ServletContextHashModel(servlete, wrapper);
		ServletActionContext.getServletContext().setAttribute(ATTR_APPLICATION_MODEL,servletContextModel);
		TaglibFactory taglibs = new TaglibFactory(ServletActionContext.getServletContext());
		ServletActionContext.getServletContext().setAttribute(ATTR_JSP_TAGLIBS_MODEL,taglibs);
		AllHttpScopesHashModel params = new AllHttpScopesHashModel(wrapper, pageContext.getServletContext(), request);
		params.putUnlistedModel(KEY_APPLICATION, servletContextModel);
	    params.putUnlistedModel(KEY_APPLICATION_PRIVATE, servletContextModel);
		params.putUnlistedModel(KEY_JSP_TAGLIBS, (TemplateModel)ServletActionContext.getServletContext().getAttribute(ATTR_JSP_TAGLIBS_MODEL));
			
		// Create hash model wrapper for session
	    HttpSessionHashModel sessionModel;
	    HttpSession session = request.getSession(false);
	    sessionModel = (HttpSessionHashModel) session.getAttribute(ATTR_SESSION_MODEL);
	    if (sessionModel == null ) {
	        sessionModel = new HttpSessionHashModel(session, wrapper);
	        session.setAttribute(ATTR_SESSION_MODEL, sessionModel);
	    }
	    params.putUnlistedModel(KEY_SESSION, sessionModel);
	
	    // Create hash model wrapper for request
	    HttpRequestHashModel requestModel = (HttpRequestHashModel) request.getAttribute(ATTR_REQUEST_MODEL);
	    if (requestModel == null || requestModel.getRequest() != request)
	    {
	        requestModel = new HttpRequestHashModel(request, response, wrapper);
	        request.setAttribute(ATTR_REQUEST_MODEL, requestModel);
	        request.setAttribute(ATTR_REQUEST_PARAMETERS_MODEL,new HttpRequestParametersHashModel(request));
	    }
	    params.putUnlistedModel(KEY_REQUEST, requestModel);
	    params.putUnlistedModel(KEY_INCLUDE, new IncludePage(request, response));
	    params.putUnlistedModel(KEY_REQUEST_PRIVATE, requestModel);
	
	    // Create hash model wrapper for request parameters
	    HttpRequestParametersHashModel requestParametersModel = (HttpRequestParametersHashModel) request.getAttribute(ATTR_REQUEST_PARAMETERS_MODEL);
	    params.putUnlistedModel(KEY_REQUEST_PARAMETERS, requestParametersModel);
	    //将用户数据放入model中
	    params.putAll(map);
	    return params;
	}
	  
	private static Configuration config = null;

	static FremarkParseUtils getInstance() {
		return new FremarkParseUtils();
	}

	FremarkParseUtils() {
		if (config == null) {
			config = new Configuration();
			config.setTemplateLoader(new StringTemplateLoader());

			try {
				config.setSetting("datetime_format", "yyyy-MM-dd HH:mm:ss");
				config.setLocale(Locale.CHINA);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	private String render(TemplateModel dataModel, String text) throws Exception {
		String ret = null;
		BufferedReader reader=null;
		StringWriter stringWriter=null;
		BufferedWriter writer=null;
		try { 
			reader= new BufferedReader(new StringReader(text));
			Template template = new Template(null, reader, config, "UTF-8");
			stringWriter= new StringWriter();
			writer = new BufferedWriter(stringWriter);
			template.process(dataModel, writer);
			writer.flush();
			ret = stringWriter.toString();

		} catch (Exception ex) {
			ex.printStackTrace();
			ret = null;
		}finally{
			reader.close();
			stringWriter.close();
			writer.close();
		}
		return ret;
	} 
 /**
  *  解析fremark模板（模板中可以含jsp标签）
  *  模板文件可以向下面例子一样引用jsp标签:
  *  <#global c=JspTaglibs["http://java.sun.com/jsp/jstl/core"]>
  *	<#escape x as x?c>
  *	<@c.out value="测试c标签" ></@c.out>
  *		模板使用标签测试中
  *	</#escape>
  *  
  * @param temlate 模板文件
  * @param pageContext 
  * @param map 用户数据
  * @return 解析后的html
  * @throws Exception
  */
	public static String parseFremarkTemplate(String temlate,PageContext pageContext,Map<String, Object> map)  throws Exception {
		TemplateModel model = createModel(pageContext,map);
		return FremarkParseUtils.getInstance().render(model, temlate);
	}
	  
}
/*
 * 对servlet的简单实现，用来代理config和context
 */
class ProxyServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private ServletConfig config;
	private ServletContext context;
	ProxyServlet(ServletConfig config, ServletContext context){
		this.config = config;
		this.context = context;
	}
	@Override
	public ServletConfig getServletConfig() {
		return config;
	}
	@Override
	public ServletContext getServletContext() {
		return context;
	}
	
}