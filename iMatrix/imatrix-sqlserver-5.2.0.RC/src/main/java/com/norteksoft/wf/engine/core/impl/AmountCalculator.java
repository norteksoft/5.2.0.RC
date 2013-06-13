package com.norteksoft.wf.engine.core.impl;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.wf.base.enumeration.NumberOperator;
import com.norteksoft.wf.engine.core.Computable;

/**
 * 金额大小关系的运算器
 */
public class AmountCalculator implements Computable {
	public Boolean execute(String subExpression) {
		if(StringUtils.contains(subExpression, NumberOperator.NLT.getCode())){
			Double preOperand = new Double(StringUtils.substringBefore(subExpression, NumberOperator.NLT.getCode()).trim());
			Double value = new Double(StringUtils.substringAfterLast(subExpression, NumberOperator.NLT.getCode()).trim());
			return preOperand.doubleValue()>=value.doubleValue();
		}else if(StringUtils.contains(subExpression, NumberOperator.NMT.getCode())){
			Double preOperand = new Double(StringUtils.substringBefore(subExpression, NumberOperator.NMT.getCode()).trim());
			Double value = new Double(StringUtils.substringAfterLast(subExpression, NumberOperator.NMT.getCode()).trim());
			return preOperand.doubleValue()<=value.doubleValue();
		}else if(StringUtils.contains(subExpression, NumberOperator.GT.getCode())){
			Double preOperand = new Double(StringUtils.substringBefore(subExpression, NumberOperator.GT.getCode()).trim());
			Double value = new Double(StringUtils.substringAfterLast(subExpression, NumberOperator.GT.getCode()).trim());
			return preOperand.doubleValue()>value.doubleValue();
		}else if(StringUtils.contains(subExpression, NumberOperator.LT.getCode())){
			Double preOperand = new Double(StringUtils.substringBefore(subExpression, NumberOperator.LT.getCode()).trim());
			Double value = new Double(StringUtils.substringAfterLast(subExpression, NumberOperator.LT.getCode()).trim());
			return preOperand.doubleValue()<value.doubleValue();
		}else if(StringUtils.contains(subExpression, NumberOperator.NET.getCode())){
			Double preOperand = new Double(StringUtils.substringBefore(subExpression, NumberOperator.NET.getCode()).trim());
			Double value = new Double(StringUtils.substringAfterLast(subExpression, NumberOperator.NET.getCode()).trim());
			return preOperand.doubleValue()!=value.doubleValue();
		}else if(StringUtils.contains(subExpression, NumberOperator.ET.getCode())){
			Double preOperand = new Double(StringUtils.substringBefore(subExpression, NumberOperator.ET.getCode()).trim());
			Double value = new Double(StringUtils.substringAfterLast(subExpression, NumberOperator.ET.getCode()).trim());
			return preOperand.doubleValue()==value.doubleValue();
		}
		throw new RuntimeException(subExpression + " is invalid expression.");
	}

}
