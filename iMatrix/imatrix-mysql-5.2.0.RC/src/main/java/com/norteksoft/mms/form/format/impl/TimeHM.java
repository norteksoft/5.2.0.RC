package com.norteksoft.mms.form.format.impl;

import com.norteksoft.mms.form.format.FormatSetting;

public class TimeHM implements FormatSetting {

	public String format(String format) {
		StringBuilder colModel=new StringBuilder();
		colModel.append("formatter:'date',");
		 colModel.append("formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H:i'}");
		 return colModel.toString();
	}

}
