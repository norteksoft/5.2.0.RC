package com.norteksoft.product.util;


public class FtlUtils {

	public static String renderFile(Object dataModel, String text)
			throws Exception {
		String ret = StringTemplateRender.getInstance().render(dataModel, text); 
		return ret;
	}
	
}
