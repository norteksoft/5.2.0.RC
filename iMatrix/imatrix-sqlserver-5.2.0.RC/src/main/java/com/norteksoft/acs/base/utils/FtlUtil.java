package com.norteksoft.acs.base.utils;

import com.norteksoft.product.util.StringTemplateRender;

/**
 * 不可以删，也不可以换包路径，xts-oa、mail等的查询用到了该类
 * @author liudongxia
 *
 */
public class FtlUtil {

	public static String renderFile(Object dataModel, String text)
			throws Exception {
		String ret = StringTemplateRender.getInstance().render(dataModel, text); 
		return ret;
	}
	
}
