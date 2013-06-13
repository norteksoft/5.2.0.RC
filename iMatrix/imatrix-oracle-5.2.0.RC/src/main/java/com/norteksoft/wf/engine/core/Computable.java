package com.norteksoft.wf.engine.core;

/**
 * 运算器需要继承的接口
 */
public interface Computable {
	/**
	 * 计算一个二元逻辑表达式的真假
	 * @param subExpression 二元逻辑表达式
	 * @return 逻辑表达式的计算结果
	 * @throws Exception
	 */
	public Boolean execute(String subExpression);
}
