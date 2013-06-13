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
public class TableViewTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(TableViewTag.class);
	
	private String code;
	private Integer version;
	private Object entity;
	private Collection collection;//是集合时（子表只存主表id）
	private boolean signatureVisible;

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
			if(form.getStandard()){
				html=form.getHtml();
			}else{
				html = "<input id='id' name='id' type='hidden'/><input id='instance_id' name='instance_id' type='hidden'/><input id='form_code' name='form_code' type='hidden' value='"+form.getCode()+"'/><input id='form_version' name='form_version' type='hidden' value='"+form.getVersion()+"'/>"+form.getHtml();
			}
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
			html=formViewManager.getHtml(form, html, id, true,entity,collection,signatureVisible);
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

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public void setSignatureVisible(boolean signatureVisible) {
		this.signatureVisible = signatureVisible;
	}

}
