package com.norteksoft.tags.button;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.mms.module.entity.Button;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.mms.module.service.ModulePageManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
public class ButtonTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	
	private String code;

	private Log log = LogFactory.getLog(ButtonTag.class);
	
	private ModulePageManager modulePageManager;
	
	@Override
	public int doStartTag() throws JspException {
		modulePageManager = (ModulePageManager)ContextUtils.getBean("modulePageManager");
		
		ModulePage modulePage = modulePageManager.getModulePage(code);
		List<Button> buttons = null;
		if(modulePage != null){
			buttons = modulePage.getButtons();
		}
		if(buttons != null && buttons.size()>0){
			JspWriter out=pageContext.getOut(); 
			try {
				out.print(readScriptTemplate(buttons));
			} catch (Exception e) {
				log.error(e);
				throw new JspException(e);
			}
		}
		return Tag.EVAL_PAGE;
	}
	
	private String readScriptTemplate(List<Button> buttons) throws Exception {
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("buttons", buttons);
		String result =TagUtil.getContent(root, "button/ButtonTag.ftl");
		return result;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
