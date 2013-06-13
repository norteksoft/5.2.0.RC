package com.norteksoft.mms.base.utils.view;

import java.util.Map;

public interface ComboxValues {
	/**
	 * 列表组件中，编辑时，下拉框中的值
	 * @return Map<String,String>,其key为fieldName,value格式为'key':'value','key':'value',...
	 * 考虑到entity中有多个需要下拉框编辑的字段时
	 * 参数说明：entity 当是formGrid和customGrid标签时，改值才有用，其值应为主表实体；当是grid、subGrid标签时，改值不用传
	 */
	public Map<String,String> getValues(Object entity);
}
