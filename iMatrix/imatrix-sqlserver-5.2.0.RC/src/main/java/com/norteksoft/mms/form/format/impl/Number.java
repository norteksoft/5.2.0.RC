package com.norteksoft.mms.form.format.impl;

import com.norteksoft.mms.form.format.FormatSetting;

public class Number implements FormatSetting {

	public String format(String format) {
		StringBuilder colModel=new StringBuilder();
		if(format.indexOf("#,##")!=-1){
			 colModel.append("formatter:'number',");
			 if(format.indexOf(".")!=-1){
				 colModel.append("formatoptions:{decimalSeparator:\".\", thousandsSeparator: \",\", decimalPlaces: ").append(format.length()).append("-6, defaultValue: '0.00'}");
			 }else{
				 colModel.append("formatoptions:{decimalSeparator:\" \", thousandsSeparator: \",\", decimalPlaces: 0, defaultValue: '0'}");
			 }
		 }else{
			 colModel.append("formatter:'number',");
			 if(format.indexOf(".")!=-1){
				 colModel.append("formatoptions:{decimalSeparator:\".\", thousandsSeparator: \"\", decimalPlaces: ").append(format.length()).append("-2, defaultValue: '0.00'}");
			 }else{
				 colModel.append("formatoptions:{decimalSeparator:\" \", thousandsSeparator: \"\", decimalPlaces: 0, defaultValue: '0'}");
			 }
		 }
		 return colModel.toString();
	}

}
