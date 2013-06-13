package com.norteksoft.product.util;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.norteksoft.acs.service.security.SecurityResourceCache;

public class ReadAutoAuthUtil {
	private static String AUTO_AUTH_FUNCTION_ID="IS_AUTHENTICATED_ANONYMOUSLY";
	public static Collection<AuthFunction> getAutoAuths(String systemCode){
		Collection<AuthFunction> autoAuths=new ArrayList<AuthFunction>();
		if(StringUtils.isEmpty(systemCode)){
			readAutoAuthFunction("autoAuthConfig.xml",autoAuths,"functions/function");
		}else{
			readAutoAuthFunction("publicAutoAuthConfig.xml",autoAuths,"functions/function");
			readAutoAuthFunction("autoAuthConfig.xml",autoAuths,"functions/function");
		}
		return autoAuths;
	}
	@SuppressWarnings("unchecked")
	private static Collection<AuthFunction> readAutoAuthFunction(String fileName,Collection<AuthFunction> autoAuths,String nodes){
		try{
			SAXReader reader = new SAXReader();
	    	InputStreamReader isreader=new InputStreamReader(SecurityResourceCache.class.getClassLoader().getResourceAsStream(fileName),"UTF-8");
	    	Document document=reader.read(isreader);
			List<org.dom4j.Element> tableList = document.selectNodes(nodes);
			Iterator it = tableList.iterator();
			while(it.hasNext()){//只会循环一次
				org.dom4j.Element function = (org.dom4j.Element)it.next();
				//得到column的属性
				   List<Attribute> columnAttributes = function.attributes();
				   AuthFunction autoAuth=new AuthFunction();
				   for(int i=0;i<columnAttributes.size();i++){
					   String attributeName = columnAttributes.get(i).getName();
					   if("path".equals(attributeName)){
						   autoAuth.setFunctionPath(columnAttributes.get(i).getValue());
					   }
				   }
				   autoAuth.setFunctionId(AUTO_AUTH_FUNCTION_ID);
				   autoAuths.add(autoAuth);
			}
		}catch (Exception e) {
		}
		return autoAuths;
	}
}
