package com.norteksoft.mms.base;

import java.util.List;
import java.util.Map;

/**
 * 动态列值
 * @author Administrator
 *
 */
public interface DynamicColumnValues {
	/**
	 * 列表页面中用动态列表组件时，需要调用该接口并重写以下方法,把动态显示的字段名和字段值追加进去
	 * @return Map<String,Object>,其key为字段名,value为字段值
	 */
	public void addValuesTo(List< Map<String,Object>> result);
}
