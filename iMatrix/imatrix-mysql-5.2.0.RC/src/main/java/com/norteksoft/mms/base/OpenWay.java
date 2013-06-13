package com.norteksoft.mms.base;
/**
 * 一级菜单打开方式
 * @author qiao
 *
 */
public enum OpenWay {
	CURRENT_PAGE_OPEN("firstMenu.open.way.currentPageOpen"),//本页打开
	NEW_PAGE_OPEN("firstMenu.open.way.newPageOpen"),//新窗口打开
	COLORBOX_OPEN("firstMenu.open.way.colorboxOpen");//弹框打开
	
	public String code;
	OpenWay(String code){
		this.code=code;
	}
	public String getCode() {
		return code;
	}
}
