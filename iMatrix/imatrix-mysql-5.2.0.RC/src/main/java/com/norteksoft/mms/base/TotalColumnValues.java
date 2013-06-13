package com.norteksoft.mms.base;

import java.util.List;
import java.util.Map;

/**
 * 合计列值
 * @author Administrator
 *
 */
public interface TotalColumnValues {
	/**
	 * 列表页面中用合计所有页时，需要调用该接口并重写以下方法,获得合计的字段名和字段的合计值
	 * @return Map<String,Object>,其key为字段名,value为字段合计值
	 */
	public Map<String,Object> getValues(List<String> result);
}
