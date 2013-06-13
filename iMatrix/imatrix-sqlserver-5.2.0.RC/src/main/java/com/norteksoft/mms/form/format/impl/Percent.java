package com.norteksoft.mms.form.format.impl;

import com.norteksoft.mms.form.format.FormatSetting;

public class Percent implements FormatSetting{

	public String format(String format) {
		StringBuilder colModel=new StringBuilder();
		 colModel.append("formatter:'currency',");
		 if(format.indexOf(".")!=-1){
			 colModel.append("formatoptions:{decimalSeparator:\"00.\", thousandsSeparator: \"\", decimalPlaces:").append(format.length()).append("-3, suffix: \"%\"}");
		 }else{
			 colModel.append("formatoptions:{decimalSeparator:\"00\", thousandsSeparator: \"\", decimalPlaces:0, suffix: \"%\"}");
		 }
		 return colModel.toString();
	}

}
