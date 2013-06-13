package com.norteksoft.product.util;

import java.lang.reflect.Member;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.norteksoft.product.web.struts2.Struts2Utils;
import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;




public class DateTypeConvertor extends DefaultTypeConverter {

	@Override
	public Object convertValue(Map<String, Object> context, Object target,
			Member member, String propertyName, Object value, Class toType) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(toType==Date.class){
				String[] params = (String[])value;
				try {
					return sdf.parseObject(params[0]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		    return null;
	}
	
	
}
