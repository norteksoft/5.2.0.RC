package com.norteksoft.mms.form.format;

public interface FormatSetting {
	/**
	 * 根据mms中格式设置的配置获得jqgrid对应的格式
	 * @param formatSetting  mms中配置的格式设置
	 * @return
	 */
	public String format(String format);

}
