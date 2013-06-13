package com.norteksoft.tags.grid.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/grid")
@ParentPackage("default")
public class QuerySettingAction extends CRUDActionSupport{
	private static final long serialVersionUID = 1L;
	private String currentInputId;
	
	/**
	 * 弹出查询设置
	 * @return
	 * @throws Exception
	 */
	public String querySetting() throws Exception {
		String resourceCtx=PropUtils.getProp("host.resources");
		HttpServletRequest request=Struts2Utils.getRequest();
		request.setAttribute("resourceCtx",resourceCtx);
		
		return "success";
	}
	
	public String getCurrentInputId() {
		return currentInputId;
	}

	public void setCurrentInputId(String currentInputId) {
		this.currentInputId = currentInputId;
	}

	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws Exception {
		// TODO Auto-generated method stub
		return null;
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

}
