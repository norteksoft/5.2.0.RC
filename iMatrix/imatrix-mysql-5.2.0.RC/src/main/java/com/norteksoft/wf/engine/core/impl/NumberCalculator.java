package com.norteksoft.wf.engine.core.impl;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.wf.base.enumeration.NumberOperator;
import com.norteksoft.wf.engine.core.Computable;

/**
 * 数字大小的运算器
 */
public class NumberCalculator implements Computable {
	public Boolean execute(String subExpression){
		if(StringUtils.contains(subExpression, NumberOperator.NLT.getCode())){
			String leftOper = StringUtils.substringBefore(subExpression, NumberOperator.NLT.getCode()).trim();
			Long preOperand = new Long(leftOper.equals("")?"0":leftOper);
			String right = StringUtils.substringAfterLast(subExpression, NumberOperator.NLT.getCode()).trim();
			Long value = new Long(right.equals("")?"0":right.replaceAll("'", ""));
			return preOperand.longValue()>=value.longValue();
		}else if(StringUtils.contains(subExpression, NumberOperator.NMT.getCode())){
			String leftOper = StringUtils.substringBefore(subExpression, NumberOperator.NMT.getCode()).trim();
			Long preOperand = new Long(leftOper.equals("")?"0":leftOper);
			String right = StringUtils.substringAfterLast(subExpression, NumberOperator.NMT.getCode()).trim();
			Long value = new Long(right.equals("")?"0":right.replaceAll("'", ""));
			return preOperand.longValue()<=value.longValue();
		}else if(StringUtils.contains(subExpression, NumberOperator.GT.getCode())){
			String leftOper = StringUtils.substringBefore(subExpression, NumberOperator.GT.getCode()).trim();
			Long preOperand = new Long(leftOper.equals("")?"0":leftOper);
			String right = StringUtils.substringAfterLast(subExpression, NumberOperator.GT.getCode()).trim();
			Long value = new Long(right.equals("")?"0":right.replaceAll("'", ""));
			return preOperand.longValue()>value.longValue();
		}else if(StringUtils.contains(subExpression, NumberOperator.LT.getCode())){
			String leftOper = StringUtils.substringBefore(subExpression, NumberOperator.LT.getCode()).trim();
			Long preOperand = new Long(leftOper.equals("")?"0":leftOper);
			String right = StringUtils.substringAfterLast(subExpression, NumberOperator.LT.getCode()).trim();
			Long value = new Long(right.equals("")?"0":right.replaceAll("'", ""));
			return preOperand.longValue()<value.longValue();
		}else if(StringUtils.contains(subExpression, NumberOperator.NET.getCode())){
			String leftOper = StringUtils.substringBefore(subExpression, NumberOperator.NET.getCode()).trim();
			Long preOperand = new Long(leftOper.equals("")?"0":leftOper);
			String right = StringUtils.substringAfterLast(subExpression, NumberOperator.NET.getCode()).trim();
			Long value = new Long(right.equals("")?"0":right.replaceAll("'", ""));
			return preOperand.longValue()!=value.longValue();
		}else if(StringUtils.contains(subExpression, NumberOperator.ET.getCode())){
			String leftOper = StringUtils.substringBefore(subExpression, NumberOperator.ET.getCode()).trim();
			Long preOperand = new Long(leftOper.equals("")?"0":leftOper);
			String right = StringUtils.substringAfterLast(subExpression, NumberOperator.ET.getCode()).trim();
			Long value = new Long(right.equals("")?"0":right.replaceAll("'", ""));
			return preOperand.longValue()==value.longValue();
		}
		throw new RuntimeException(subExpression + " is invalid expression.");
	}

}
