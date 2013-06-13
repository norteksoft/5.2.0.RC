package com.norteksoft.tags.grid;

import java.util.Collection;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.util.ContextUtils;
public class FormPrintTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(FormPrintTag.class);
	
	private String code;
	private Integer version;
	private Object entity;

	public void setCode(String code) {
		this.code = code;
	}
	
	public void setEntity(Object entity) {
		this.entity = entity;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int doStartTag() throws JspException {
		FormViewManager formViewManager = (FormViewManager) ContextUtils.getBean("formViewManager");
		
		FormView form = null;
		if(version != null){
			form = formViewManager.getCurrentFormViewByCodeAndVersion(code, version);
		}else{
			form = formViewManager.getHighFormViewByCode(code);
		}
		
		String html = new String("");
		if(form != null){
			html="<div class=\"form-print\">"+form.getHtml();
		}
		if(StringUtils.isNotEmpty(html)){
			Long id = null;
			if(form.getStandard()){
				try {
					if(entity!=null){
						Object value = BeanUtils.getProperty(entity, "id");
						if(value!=null)id=Long.valueOf(value.toString());
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
			}else{
				if(entity instanceof Map){
					if(((Map) entity).get("id") != null){ id = Long.valueOf(((Map) entity).get("id").toString());}
				}
			}
			html=formViewManager.getPrintHtml(form, html, id, true,entity)+"</div>";
		}
		JspWriter out = pageContext.getOut();
		try {
			out.print(html);
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	}
	
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
