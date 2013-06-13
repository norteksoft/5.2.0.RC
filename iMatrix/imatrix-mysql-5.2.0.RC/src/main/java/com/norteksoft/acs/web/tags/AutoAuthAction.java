package com.norteksoft.acs.web.tags;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;


import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.security.SecurityResourceCache;
import com.norteksoft.product.util.AuthFunction;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.ReadAutoAuthUtil;

public class AutoAuthAction extends CRUDActionSupport{
	private static final long serialVersionUID = 1L;
	
	private String systemCode;
	private BusinessSystemManager businessSystemManager;
	@Autowired
	public void setBusinessSystemManager(
			BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}
	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws Exception {
		BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
		if(system!=null){
			if(system.getImatrixable()){//如果是底层平台
				systemCode=null;
			}
		}
		Collection<AuthFunction> autoFuns=ReadAutoAuthUtil.getAutoAuths(systemCode);
		String funPath = "";
		for(AuthFunction autoFun: autoFuns){
			funPath = String.valueOf(autoFun.getFunctionPath().hashCode());
			MemCachedUtils.add(funPath, autoFun);
		}
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

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

}
