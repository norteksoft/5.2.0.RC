package com.norteksoft.wf.engine.core;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.wf.base.enumeration.LogicOperator;
import com.norteksoft.wf.base.utils.BeanShellUtil;

/**
 * 解析用户条件设置
 * @author wurong
 */
public class ConditionParseUtil {
	private static final String AND = "&&";
	private static final String OR = "||";
	
	public static  boolean parseCondition(String condition,Computable computable){
		if(StringUtils.isEmpty(condition)) return false;
		if(condition.equalsIgnoreCase("true")) return true;
		if(condition.equalsIgnoreCase("false")) return false;
		
		String[] strs = BeanShellUtil.splitExpression(condition);
		String temp = condition;
		Boolean result = false;
		for(int i=0;i<strs.length;i++){
			result = computable.execute(strs[i]);
			temp = StringUtils.replace(temp, strs[i].trim(), result.toString());
		}
		temp = temp.replaceAll(LogicOperator.AND.getCode(), AND);
		temp = temp.replaceAll(LogicOperator.OR.getCode(), OR);
		boolean expressResult = BeanShellUtil.evel(temp);
		return expressResult;
	}
	
}
