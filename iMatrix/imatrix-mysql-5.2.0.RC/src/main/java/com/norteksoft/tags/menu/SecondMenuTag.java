package com.norteksoft.tags.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.security.SecurityResourceCache;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;

public class SecondMenuTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(SecondMenuTag.class);
	private MenuManager menuManager;
	private Long menuId;
	private Long secondMenuId;
	private String code;
	private BusinessSystemManager businessSystemManager;
	@Override
	public int doStartTag() throws JspException {
		try {
			menuManager=(MenuManager)ContextUtils.getBean("menuManager");
			String systemCode=ContextUtils.getSystemCode();
			String currentCode = StringUtils.isEmpty(code)?systemCode:code;
			String lastMenuIdStr=pageContext.getRequest().getParameter("menuId");
			if(lastMenuIdStr == null){
				if(pageContext.getRequest().getAttribute("menuId")!=null){
					lastMenuIdStr =  pageContext.getRequest().getAttribute("menuId").toString();
				}else{
					Menu lastMenu=menuManager.getDefaultMenuByLayer(currentCode);
					if(lastMenu!=null){
						lastMenuIdStr =  lastMenu.getId().toString();
					}
				}
			}
			if(lastMenuIdStr!=null){
				Menu secondMenu=menuManager.getMenuByLastMenu(2,Long.parseLong(lastMenuIdStr));
				if(secondMenu!=null)secondMenuId=secondMenu.getId();
				menuId=Long.parseLong(lastMenuIdStr);
			}
			 JspWriter out=pageContext.getOut(); 
			 out.print(readScriptTemplate(currentCode));
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
	
	//读取脚本模板
	private String readScriptTemplate(String currentCode) throws Exception{
		Menu firstMenu=menuManager.getRootMenu(menuId);
		List<Menu> menus=menuManager.getEnableMenuByLayer(2,firstMenu.getId());
		List<Menu> finalMenus=new ArrayList<Menu>();
		
		businessSystemManager = (BusinessSystemManager)ContextUtils.getBean("businessSystemManager");
		BusinessSystem business = businessSystemManager.getSystemBySystemCode(currentCode);
		String parentBusinessCode = null;
		for(Menu menu:menus){
			String url=menu.getUrl();
			if(url.contains("?")){
				url=url.substring(0,menu.getUrl().indexOf("?"));
			}
			if(business!=null){
				parentBusinessCode = business.getParentCode();
				if(StringUtils.isNotEmpty(parentBusinessCode)){//如果是子系统，则在url前加上当前系统的编码
					//url:/form/list-data.htm,属于mms子系统，则新url应为/mms/form/list-data.htm
					url="/"+currentCode+url;
				}
			}
			String grantedAuthorities = SecurityResourceCache.getAuthoritysInCache(url);
			if(grantedAuthorities!=null){
				if(ContextUtils.isAuthority(grantedAuthorities)){
					finalMenus.add(menu);
				}
			}else{
				if("ems".equals(currentCode)){
					finalMenus.add(menu);
				}
			}
		}
		String sysUrl="";
		List<Menu> resultMenus=new ArrayList<Menu>();
		for(Menu menu:finalMenus){
			String url=menu.getUrl();
			if(url.contains("?")){
				url=url.substring(0,menu.getUrl().indexOf("?"));
				menu.setUrl(url);
			}
			Menu lastMenu = menuManager.getLastMenu(menu.getId());
			menu.setLastMenuId(lastMenu.getId());
			resultMenus.add(menu);
		}
		Map<String, Object> root=new HashMap<String, Object>();
		if(secondMenuId!=null){
			root.put("secondMenuId", secondMenuId);
		}else{
			if(resultMenus.size()>0){
				root.put("secondMenuId", resultMenus.get(0).getId());
			}
		}
		
		if(business!=null){
			parentBusinessCode = business.getParentCode();
			if(StringUtils.isEmpty(parentBusinessCode)){
				sysUrl = SystemUrls.getSystemUrl(currentCode);
			}else{
				sysUrl = SystemUrls.getSystemUrl(parentBusinessCode);
			}
		}
		
		if(PropUtils.getProp("project.model")!=null&&PropUtils.getProp("project.model").equals("developing.model")){
			if(sysUrl.indexOf("mms/common/list.htm")>0){//开发模式下自定义流程二级菜单url
				sysUrl = PropUtils.getProp("host.imatrix");
			}
		}
		if(sysUrl.lastIndexOf("/")==sysUrl.length()-1){//是80端口时
			sysUrl = sysUrl.substring(0,sysUrl.length()-1);
		 }
		root.put("systemCode",currentCode);
		root.put("sysUrl",sysUrl);
		root.put("subSysable", StringUtils.isEmpty(parentBusinessCode)?false:true);//是否是子系统,false代表不是子系统如ems,true代表是子系统如mms
		root.put("menus", resultMenus);
		String result = TagUtil.getContent(root, "menu/secondMenuTag.ftl");
		return result;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
