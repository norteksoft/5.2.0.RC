package com.norteksoft.tags.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;

public class MenuTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(MenuTag.class);
	private MenuManager menuManager;
	private String imgSrc;
	private Long menuId;
	private Integer showNum=1;
	private String code;//系统code
	@Override
	public int doStartTag() throws JspException {
		try {
			menuManager=(MenuManager)ContextUtils.getBean("menuManager");
			String lastMenuIdStr=pageContext.getRequest().getParameter("menuId");
			if(lastMenuIdStr!=null){
				menuId=Long.parseLong(lastMenuIdStr);
			}else{
				if(pageContext.getRequest().getAttribute("menuId")!=null){
					menuId =  Long.parseLong(pageContext.getRequest().getAttribute("menuId").toString());
				}else{
					String url=(String)pageContext.getRequest().getAttribute("struts.request_uri");
					String[] urls=url.split("/");
					//底层系统应用地址
					String systemCode=ContextUtils.getSystemCode();
					String code=urls[1];
					if(urls.length>=3){
						String tempCode = urls[2];
						Menu tempMenu=menuManager.getMenuByCode(tempCode);
						if(tempMenu !=null){
							code=tempCode;
						}
					}
					Menu lastMenu=menuManager.getDefaultMenuByLayer(StringUtils.isEmpty(code)?systemCode:code);
					if(lastMenu!=null){
						menuId =  lastMenu.getId();
					}
				}
			}
			 JspWriter out=pageContext.getOut(); 
			 out.print(readScriptTemplate());
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	}
	
	//读取脚本模板
	private String readScriptTemplate() throws Exception{
		String webapp=((HttpServletRequest)pageContext.getRequest()).getContextPath();
		menuManager.initAllMenus();
		List<Menu> menus=menuManager.getEnabledRootMenuByCompany();
		List<Menu> resultMenus=new ArrayList<Menu>();
		for(Menu menu:menus){
			if(PropUtils.isBasicSystem(menu.getUrl())){//如果是底层系统
				String redirectUrl=PropUtils.getProp("redirectUrl.properties",menu.getCode());
				if(ContextUtils.isAuthority(redirectUrl,menu.getCode())){//有该权限才在一级菜单中显示
					Menu tempMenu = menu.clone();
					Menu lastMenu = menuManager.getLastMenu(tempMenu.getId());
					if(PropUtils.getProp("project.model")!=null&&PropUtils.getProp("project.model").equals("developing.model")){
						tempMenu.setUrl(SystemUrls.getSystemPath(menu.getCode(), new String[]{"",menu.getUrl(),"",""}));
					}
					tempMenu.setLastMenuId(lastMenu.getId());
					resultMenus.add(tempMenu);
				}
			}else{//如果不是底层系统，而是业务系统时直接显示在一级菜单中
				Menu tempMenu = menu.clone();
				Menu lastMenu = menuManager.getLastMenu(tempMenu.getId());
				if(PropUtils.getProp("project.model")!=null&&PropUtils.getProp("project.model").equals("developing.model")){
					tempMenu.setUrl(SystemUrls.getSystemPath(menu.getCode(), new String[]{"",menu.getUrl(),"",""}));
				}
				tempMenu.setLastMenuId(lastMenu.getId());
				resultMenus.add(tempMenu);
			}
		}
		Menu firstMenu = null;
		Long systemId=ContextUtils.getSystemId("portal");
		if(menuId!=null){
			Menu menu = menuManager.getRootMenu(menuId);
			firstMenu = menu.clone();
		}
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("imgSrc", imgSrc==null?"":imgSrc);
		if(menuId!=null){
			root.put("firstMenuId", firstMenu.getId());
		}else{
			root.put("firstMenuId", 0l);
		}
		
		//交换一级菜单中显示的和更多中的菜单（开始） 
		int lastIndexOf=resultMenus.lastIndexOf(firstMenu);
		if(lastIndexOf>=showNum){
			Menu temp=resultMenus.get(showNum-1);
			if(PropUtils.getProp("project.model")!=null&&PropUtils.getProp("project.model").equals("developing.model")){
				firstMenu.setUrl(SystemUrls.getSystemPath(firstMenu.getCode(), new String[]{"",firstMenu.getUrl(),"",""}));
			}
			firstMenu.setLastMenuId(menuId);
			resultMenus.set(showNum-1, firstMenu);
			resultMenus.set(lastIndexOf, temp);
		}
		//交换一级菜单中显示的和更多中的菜单（ 结束）
		String imatrixUrl=SystemUrls.getSystemUrl("imatrix");
		root.put("showNum", showNum);
		root.put("moreSystem", "更多");
		root.put("menus", resultMenus);
		root.put("menuSize", resultMenus.size());
		root.put("systemId", systemId);
		root.put("ctx", webapp);
		root.put("imatrixUrl", imatrixUrl);
		String result = TagUtil.getContent(root, "menu/menuTag.ftl");
		return result;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	public String getImgSrc() {
		return imgSrc;
	}

	public Integer getShowNum() {
		return showNum;
	}

	public void setShowNum(Integer showNum) {
		this.showNum = showNum;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
