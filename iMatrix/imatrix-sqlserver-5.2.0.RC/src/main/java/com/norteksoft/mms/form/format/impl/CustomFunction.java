package com.norteksoft.mms.form.format.impl;

import com.norteksoft.mms.form.format.FormatSetting;

public class CustomFunction implements FormatSetting {

	public String format(String format) {
		StringBuilder colModel=new StringBuilder();
		String[] funcs= format.split(",");
		for(int i=0;i<funcs.length;i++){
			if(i==0){
				if(funcs[i].indexOf("unfunc:")!=-1){
					colModel.append("unformat:").append(funcs[i].replace("unfunc:", ""));
				}else if(funcs[i].indexOf("func:")!=-1){
					colModel.append("formatter:").append(funcs[i].replace("func:", ""));
				}
			}else if(i==1){
				if(funcs.length>1)colModel.append(",");
				colModel.append("unformat:").append(funcs[i].replace("unfunc:", ""));
			}
		}
		 return colModel.toString();
	}

}
