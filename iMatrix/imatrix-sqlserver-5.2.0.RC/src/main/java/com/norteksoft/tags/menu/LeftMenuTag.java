package com.norteksoft.tags.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.security.SecurityResourceCache;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;

public class LeftMenuTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(LeftMenuTag.class);
	private MenuManager menuManager;
	private AcsUtils acsUtils;
	private Long thirdMenuId;
	private Long fourMenuId;
	private Long menuId;
	@Override
	public int doStartTag() throws JspException {
		try {
			Long secondMenuId = null;
			String thirdMenuCode = null;
			
			menuManager=(MenuManager)ContextUtils.getBean("menuManager");
			acsUtils=(AcsUtils)ContextUtils.getBean("acsUtils");
			String lastMenuIdStr=pageContext.getRequest().getParameter("menuId");
			if(lastMenuIdStr == null) {
				Object attr=pageContext.getRequest().getAttribute("menuId");
				if(attr!=null){
					lastMenuIdStr =  attr.toString();
				}
			}
			if(lastMenuIdStr==null){
				//获得当前系统中第一个叶子节点
				Menu leafMenu = menuManager.getLeafMenuBySystem(ContextUtils.getSystemId());
				if(leafMenu!=null){
					lastMenuIdStr = leafMenu.getId().toString();
				}
			}
			
			if(lastMenuIdStr!=null){
				Menu secondMenu=menuManager.getMenuByLastMenu(2,Long.parseLong(lastMenuIdStr));
				if(secondMenu!=null)secondMenuId=secondMenu.getId();
				Menu thirdMenu=menuManager.getMenuByLastMenu(3,Long.parseLong(lastMenuIdStr));
				if(thirdMenu!=null){thirdMenuId=thirdMenu.getId();thirdMenuCode=thirdMenu.getCode();}
				Menu fourMenu=menuManager.getMenuByLastMenu(4,Long.parseLong(lastMenuIdStr));
				if(fourMenu!=null)fourMenuId=fourMenu.getId();
				menuId=Long.parseLong(lastMenuIdStr);
			}
			 JspWriter out=pageContext.getOut(); 
			 out.print(readScriptTemplate(secondMenuId,thirdMenuCode));
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
	private String readScriptTemplate(Long secondMenuId,String thirdMenuCode) throws Exception{
		List<Menu> thirdMenus=null;
		List<Menu> fourMenus=null;
		if(secondMenuId!=null)thirdMenus=menuManager.getEnableMenuByLayer(3,secondMenuId);
		if(thirdMenuId!=null)fourMenus=menuManager.getEnableMenuByLayer(4,thirdMenuId);
		List<Menu> finalThirdMenus=new ArrayList<Menu>();
		if(thirdMenus!=null){
			for(Menu menu:thirdMenus){
				String grantedAuthorities = SecurityResourceCache.getAuthoritysInCache(menu.getUrl());
				if(grantedAuthorities!=null){
					if(acsUtils.isAuthority(grantedAuthorities, ContextUtils.getUserId(), ContextUtils.getCompanyId())){
						finalThirdMenus.add(menu);
					}
				}else{
					String tempurl=(String)pageContext.getRequest().getAttribute("struts.request_uri");
					String[] urls=tempurl.split("/");
					String code=urls[1];
					if("ems".equals(code)){
						finalThirdMenus.add(menu);
					}
				}
			}
		}
		List<Menu> finalFourMenus=new ArrayList<Menu>();
		if(fourMenus!=null){
			for(Menu menu:fourMenus){
				if("#this".equals(menu.getUrl())){
					finalFourMenus.add(menu);
				}else{
					String grantedAuthorities = SecurityResourceCache.getAuthoritysInCache(menu.getUrl());
					if(grantedAuthorities!=null){
						if(acsUtils.isAuthority(grantedAuthorities, ContextUtils.getUserId(), ContextUtils.getCompanyId())){
							finalFourMenus.add(menu);
						}
					}else{
						String tempurl=(String)pageContext.getRequest().getAttribute("struts.request_uri");
						String[] urls=tempurl.split("/");
						String code=urls[1];
						if("ems".equals(code)){
							finalFourMenus.add(menu);
						}
					}
				}
			}
		}
		Menu firstMenu=menuManager.getRootMenu(menuId);
		String sysUrl="";
		if(PropUtils.getProp("project.model")!=null && PropUtils.getProp("project.model").equals("developing.model")){
			sysUrl=SystemUrls.getSystemPath(firstMenu.getCode(),new String[]{"",firstMenu.getUrl(),"",""});		
		}else{
			sysUrl=menuManager.getSysUrl(firstMenu.getSystemId());	
		}
		List<Menu> resultThirdMenus=new ArrayList<Menu>();
		for(Menu menu:finalThirdMenus){
			Menu lastMenu = menuManager.getLastMenu(menu.getId());
			menu.setLastMenuId(lastMenu.getId());
			resultThirdMenus.add(menu);
		}
		List<Menu> resultFourMenus=new ArrayList<Menu>();
		for(Menu menu:finalFourMenus){
			Menu lastMenu = menuManager.getLastMenu(menu.getId());
			menu.setLastMenuId(lastMenu.getId());
			resultFourMenus.add(menu);
		}
		Map<String, Object> root=new HashMap<String, Object>();
		//root.put("secondMenuId", secondMenuId==null?0l:secondMenuId);
		root.put("thirdMenuId", thirdMenuId==null?0l:thirdMenuId);
		root.put("fourMenuId", fourMenuId==null?0l:fourMenuId);
		root.put("thirdMenus", resultThirdMenus);
		root.put("fourMenus", resultFourMenus);
		if(PropUtils.getProp("project.model")!=null && PropUtils.getProp("project.model").equals("developing.model")){
			root.put("sysUrl", sysUrl.replace("/mms/common/list.htm", ""));
		}else{
			root.put("sysUrl", sysUrl.replace("/mms", ""));
		}
		root.put("thirdMenuCode", thirdMenuCode==null?"":thirdMenuCode);
		root.put("menuId", menuId);
		String result = TagUtil.getContent(root, "menu/leftMenuTag.ftl");
		return result;
	}
	
	public void setThirdMenuId(Long thirdMenuId) {
		this.thirdMenuId = thirdMenuId;
	}
	public Long getThirdMenuId() {
		return thirdMenuId;
	}
	public void setFourMenuId(Long fourMenuId) {
		this.fourMenuId = fourMenuId;
	}
	public Long getFourMenuId() {
		return fourMenuId;
	}
}
