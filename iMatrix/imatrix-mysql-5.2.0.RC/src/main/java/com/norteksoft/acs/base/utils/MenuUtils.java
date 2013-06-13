package com.norteksoft.acs.base.utils;

import java.util.ArrayList;
import java.util.List;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.security.SecurityResourceCache;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;



/**
 * 请更换为 com.norteksoft.product.util.SystemUrls
 */
@Deprecated
public class MenuUtils {
	
	private static final String SYSTEM_MEMCACHE_KEY = "_system_url_infos";
	private static final Integer CODE_INDEX = 0;
	private static final Integer URL_INDEX = 1;
	private static final Integer NAME_INDEX = 2;
	private static final Integer IS_PRODUCT_INDEX = 3;
	private static final Integer ENABLE_STATE_INDEX = 4;
	
	
	private MenuUtils(){}
	
	static {
		init();
	}
	
	/**
	 * 更新缓存
	 */
	public static synchronized void updateUrls(){
		init();
	}
	
	/**
	 * 初始化系统URL缓存
	 * key : SYSTEM_MEMCACHE_KEY
	 * value: [code, URL, name, isProduct]
	 */
	private static synchronized void init() {
		AcsUtils acsUtils=(AcsUtils)ContextUtils.getBean("acsUtils");
		List<Company> companys=acsUtils.getAllCompanys();
		List<com.norteksoft.product.api.entity.Menu> result=new ArrayList<com.norteksoft.product.api.entity.Menu>();
		for(Company company:companys){
			ThreadParameters parameter=new ThreadParameters();
			parameter.setCompanyId(company.getId());
			ParameterUtils.setParameters(parameter);
			List<com.norteksoft.product.api.entity.Menu> menus = ApiFactory.getMmsService().getTopMenus();
			for(com.norteksoft.product.api.entity.Menu m:menus){
				if(!result.contains(m))result.add(m);
			}
		}
		String[][] systemInfos = new String[result.size()][4];
		for(int i = 0; i < result.size(); i++){
			systemInfos[i][CODE_INDEX] = result.get(i).getCode();
			systemInfos[i][URL_INDEX] = result.get(i).getUrl();
			systemInfos[i][NAME_INDEX] = result.get(i).getName();
			systemInfos[i][IS_PRODUCT_INDEX] = "true";
		}
		MemCachedUtils.add(SYSTEM_MEMCACHE_KEY, systemInfos);
	}
	
	/**
	 * 获取系统菜单
	 * @return
	 */
	public static String getMenus(){
		StringBuilder resultMenu = new StringBuilder();
		String[][] menus = (String[][]) MemCachedUtils.get(SYSTEM_MEMCACHE_KEY);
		for(String[] menu : menus){
			if(menu.length==5){//新加了一个菜单状态
				if(DataState.ENABLE.toString().equals(menu[ENABLE_STATE_INDEX])){
					if(Boolean.parseBoolean(menu[IS_PRODUCT_INDEX])){
						resultMenu.append(processProductMenuHtml(menu));
					}else{
						resultMenu.append(processCommonMenuHtml(menu));
					}
				}
			}else{//旧版没有菜单状态
				if(Boolean.parseBoolean(menu[IS_PRODUCT_INDEX])){
					resultMenu.append(processProductMenuHtml(menu));
				}else{
					resultMenu.append(processCommonMenuHtml(menu));
				}
			}
		}
		return resultMenu.toString();
	}
	
	/**
	 * 获取portal主题
	 * @return
	 */
	public static String getPortalTheme(){
		return (String)MemCachedUtils.get("THEME_"+SystemContextUtils.getUserId());
	}
	
	/**
	 * 获取portal的访问地址
	 * @return
	 */
	public static String getPortalUrl(){
		return (String)MemCachedUtils.get("PORTALURL");
	}
	
	/**
	 * 查询业务系统的访问路径
	 * @param systemCode 系统code
	 * @return 业务系统的访问路径
	 */
	public static String getBusinessPath(String systemCode){
		String[][] menus = (String[][]) MemCachedUtils.get(SYSTEM_MEMCACHE_KEY);
		for(String[] menu : menus){
			if(systemCode.equalsIgnoreCase(menu[CODE_INDEX])){
				return menu[URL_INDEX];
			}
		}
		return "";
	}

	/*
	 * values[0] = menu Id
	 * values[1] = menu URL
	 * values[2] = menu name
	 * <li id='code' ONMOUSEMOVE='mouseMove(this,3);' ONMOUSEOUT='mouseOut(this,4);'><a HREF='URL'>name</a></li>
	 */
	private static String processCommonMenuHtml(String[] values){
		if(PropUtils.isBasicSystem(values[URL_INDEX])){
			String redirectUrl=PropUtils.getProp("redirectUrl.properties",values[CODE_INDEX]);
			String grantedAuthorities = SecurityResourceCache.getAuthoritysInCache("/"+values[CODE_INDEX]+redirectUrl);
			if(grantedAuthorities!=null){
				if(ContextUtils.isAuthority(grantedAuthorities)){
					return new StringBuilder("<li id='")
					.append(values[CODE_INDEX]).append(getStyle(values[CODE_INDEX], true))
					.append("<a href=\'").append(values[URL_INDEX]).append("'>").append(values[NAME_INDEX]).append("</a></li>")
					.toString();
				}
			}
		}else{
			return new StringBuilder("<li id='")
			.append(values[CODE_INDEX]).append(getStyle(values[CODE_INDEX], true))
			.append("<a href=\'").append(values[URL_INDEX]).append("'>").append(values[NAME_INDEX]).append("</a></li>")
			.toString();
		}
		return "";
	}
	
	/*
	 * values[0] = menu Id
	 * values[1] = menu URL
	 * values[2] = menu name
	 * <li id='code' ONMOUSEMOVE='mouseMove(this,3);' ONMOUSEOUT='mouseOut(this,4);'><a HREF='URL'>name</a></li>
	 */
	private static String processProductMenuHtml(String[] values){
		if(PropUtils.isBasicSystem(values[URL_INDEX])){
			String redirectUrl=PropUtils.getProp("redirectUrl.properties",values[CODE_INDEX]);
			String grantedAuthorities = SecurityResourceCache.getAuthoritysInCache("/"+values[CODE_INDEX]+redirectUrl);
			if(grantedAuthorities!=null){
				if(ContextUtils.isAuthority(grantedAuthorities)){
					return new StringBuilder("<li id='")
					.append(values[CODE_INDEX]).append(getStyle(values[CODE_INDEX], false))
					.append("<a href=\'").append(values[URL_INDEX]).append("'>").append(values[NAME_INDEX]).append("</a></li>")
					.toString();
				}
			}
		}else{
			return new StringBuilder("<li id='")
			.append(values[CODE_INDEX]).append(getStyle(values[CODE_INDEX], false))
			.append("<a href=\'").append(values[URL_INDEX]).append("'>").append(values[NAME_INDEX]).append("</a></li>")
			.toString();
		}
		return "";
	}
	
	private static String getStyle(String sysCode, boolean isSystem){
		//FIXME 将class提取到properties中
		String result = "";
		String code = SystemContextUtils.getSystemCode();
		if(isSystem){
			if(sysCode.equals(code)){
				result = "' class='system fristBg'>";
			}else{
				result = "' class='system'>";
			}
		}else{
			if(sysCode.equals(code)){
				result = "' class='navPrdt navPrdt1'>";
			}else{
				result = "' class='navPrdt'>";
			}
		}
		return result;
	}
	
	/**
	 * 根据系统编号获取系统URL
	 * @param code
	 * @return
	 */
	public static String getSystemUrl(String code){
		String result = null;
		String[][] menus = (String[][]) MemCachedUtils.get(SYSTEM_MEMCACHE_KEY);
		for(String[] menu : menus){
			if(code.equalsIgnoreCase(menu[CODE_INDEX])){
				result = menu[URL_INDEX];
				break;
			}
		}
		if(result == null){
			com.norteksoft.product.api.entity.Menu menu = ApiFactory.getMmsService().getTopMenu(code);
			if(menu != null) result = menu.getUrl();
		}
		return result;
	}
	
}
