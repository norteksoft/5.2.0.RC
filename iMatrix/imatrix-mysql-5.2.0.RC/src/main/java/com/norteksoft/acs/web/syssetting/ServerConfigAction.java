package com.norteksoft.acs.web.syssetting;

import javax.naming.ldap.LdapContext;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.utils.Ldaper;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.sysSetting.ServerConfig;
import com.norteksoft.acs.service.syssetting.ServerConfigManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;

@Namespace("/syssetting")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/syssetting/server-config.action", type = "redirect") })
public class ServerConfigAction extends CRUDActionSupport<ServerConfig> {

	private static final long serialVersionUID = 4622265559442003480L;

	private ServerConfig serverConfig;

	private Long id;
	
	public ServerConfigManager serverConfigManager;
	
	private Boolean ldapInvocation;
	
	private Boolean rtxInvocation;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";

	@Override
	public String delete() throws Exception {
		return null;
	}

	/**
	 * 查看具体设置
	 */
	@Override
	@Action("server-config")
	public String list() throws Exception {
		serverConfig = serverConfigManager.getServerConfigByCompanyId(ContextUtils.getCompanyId());
		if(serverConfig!=null){
			id = serverConfig.getId();
		}else{
			serverConfig  = new ServerConfig();
			serverConfig.setRtxInvocation(false);
			serverConfig.setLdapInvocation(false);
			serverConfigManager.save(serverConfig);
		}
		ApiFactory.getBussinessLogService().log("登陆方式设置", 
				"查看登陆方式设置",ContextUtils.getSystemId("acs"));
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			serverConfig  = new ServerConfig();
		}else{
			serverConfig = serverConfigManager.getServerConfig(id);
		}
	}

	/**
	 * 保存设置
	 */
	@Override
	public String save() throws Exception {
		serverConfig.setCompanyId(ContextUtils.getCompanyId());
		serverConfigManager.save(serverConfig);
		if(serverConfig.getLdapInvocation()){
			LdapContext context = Ldaper.getConnectionFromPool();
			if(context==null){
				serverConfig.setLdapInvocation(false);
				serverConfig.setRtxInvocation(false);
				serverConfigManager.save(serverConfig);
				addActionMessage(ERROR_MESSAGE_LEFT+"连接失败!"+MESSAGE_RIGHT);
			}else{
				try {
					context.close();
				} catch (Exception e) { }
				addActionMessage(SUCCESS_MESSAGE_LEFT+"连接成功!"+MESSAGE_RIGHT);
			}
		}
		ApiFactory.getBussinessLogService().log("登陆方式设置", 
				"启用登陆方式",ContextUtils.getSystemId("acs"));
		return SUCCESS;
		
	}

	public void prepareModifyLoginTimeouts() throws Exception {
		prepareModel();
	}


	public ServerConfig getModel() {

		return serverConfig;
	}

	
	
	@Required
	public void setServerConfigManager(ServerConfigManager serverConfigManager) {
		this.serverConfigManager = serverConfigManager;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getLdapInvocation() {
		return ldapInvocation;
	}

	public Boolean getRtxInvocation() {
		return rtxInvocation;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	
}
