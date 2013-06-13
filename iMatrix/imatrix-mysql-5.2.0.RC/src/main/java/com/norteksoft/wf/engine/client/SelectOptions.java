package com.norteksoft.wf.engine.client;

import java.util.Map;

/**
 * 
 * 表单下拉框动态选项接口
 * 实现类命名规则：下拉框对应的名称+Options;
 * eg: <select name="myName"></select>, 
 * 则实现类为：MyNameOptions, 并标注为@Service
 * 
 * @author xj
 */
public interface SelectOptions {

	/**
	 * 获取所有的option选项
	 * @return map<key, value> 
	 * 	key: 选项对应的值, value: 选项对应的显示值.
	 */
	Map<String, String> getOptions();
	
}
