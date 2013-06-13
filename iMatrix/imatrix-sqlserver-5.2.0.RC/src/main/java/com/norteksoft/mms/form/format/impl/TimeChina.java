package com.norteksoft.mms.form.format.impl;

import com.norteksoft.mms.form.format.FormatSetting;

public class TimeChina implements FormatSetting {

	public String format(String format) {
		StringBuilder colModel=new StringBuilder();
		colModel.append("formatter:'date',");
		 colModel.append("formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y年m月d日H时i分s秒'}");
		 return colModel.toString();
	}

}
