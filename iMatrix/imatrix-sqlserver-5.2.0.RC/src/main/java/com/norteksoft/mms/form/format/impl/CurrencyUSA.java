package com.norteksoft.mms.form.format.impl;

import com.norteksoft.mms.form.format.FormatSetting;

public class CurrencyUSA implements FormatSetting{

	public String format(String format) {
		StringBuilder colModel=new StringBuilder();
		colModel.append("formatter:'currency',");
		 if(format.indexOf(".")!=-1){
			 colModel.append("formatoptions:{decimalSeparator:\".\", thousandsSeparator: \",\", decimalPlaces: ").append(format.length()).append("-7, prefix: \"$\"}");
		 }else{
			 colModel.append("formatoptions:{decimalSeparator:\" \", thousandsSeparator: \",\", decimalPlaces: 0, prefix: \"$\"}");
		 }
		return colModel.toString();
	}

}
