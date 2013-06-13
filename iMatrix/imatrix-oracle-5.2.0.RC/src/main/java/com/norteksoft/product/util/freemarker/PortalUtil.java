package com.norteksoft.product.util.freemarker;

import java.util.Map;


public class PortalUtil {
	/**
	 * 
	 * @param dataModel 模板中需要用到的变量
	 * @param fileName 模板的文件名，相对于"template/portal/"目录的相对路径，如"gridTag.ftl","menuTag.ftl"等
	 * @return 根据模板文件生成的文本
	 * @throws Exception
	 */
	private static final String PORTAL_DIR="portal/";
	public static String getContent(Map dataModel, String fileName)
			throws Exception {
		String ret =new TemplateRender().render(dataModel, PORTAL_DIR+fileName); 
		return ret;
	}
	
}
