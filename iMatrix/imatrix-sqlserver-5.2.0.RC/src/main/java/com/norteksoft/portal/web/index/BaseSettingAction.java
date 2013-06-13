package com.norteksoft.portal.web.index;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.base.web.struts2.Struts2Utils;
import com.norteksoft.portal.entity.BaseSetting;
import com.norteksoft.portal.service.BaseSettingManager;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/index")
@ParentPackage("default")
@Results( {
	@Result(name = CrudActionSupport.RELOAD,location="base-setting",type = "redirectAction")
})
public class BaseSettingAction extends CrudActionSupport<BaseSetting>{
	private static final long serialVersionUID = 1L;
	
	private BaseSetting baseSetting;
	private Long id;
	
	@Autowired
	private BaseSettingManager baseSettingManager;

	@Override
	public String delete() throws Exception {
		return null;
	}

	@Override
	@Action("base-setting-input")
	public String input() throws Exception {
		return SUCCESS;
	}

	@Override
	public String list() throws Exception {
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		baseSetting = baseSettingManager.getBaseSettingByLonginName();
		if(baseSetting!=null){
			id = baseSetting.getId();
		}else{
			baseSetting = new BaseSetting();
			baseSetting.setMessageVisible(true);
		}
	}

	@Override
	@Action("base-setting-save")
	public String save() throws Exception {
		baseSettingManager.saveMessage(baseSetting);
		renderText(baseSetting.getMessageVisible()+"");
		return null;
	}
	@Action("get-base-setting")
	public String getBaseSetting() throws Exception {
		String callback=Struts2Utils.getParameter("callback");
		baseSetting = baseSettingManager.getBaseSettingByLonginName();
		if(baseSetting!=null){
			if(baseSetting.getRefreshTime()==null){
				this.renderText(callback+"({msg:\""+baseSetting.getMessageVisible()+"_15"+"\"})");
			}else{
				this.renderText(callback+"({msg:\""+baseSetting.getMessageVisible()+"_"+baseSetting.getRefreshTime()+"\"})");
			}
		}else{
			this.renderText(callback+"({msg:'true_15'})");
		}
		return null;
	}

	public BaseSetting getModel() {
		return baseSetting;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
