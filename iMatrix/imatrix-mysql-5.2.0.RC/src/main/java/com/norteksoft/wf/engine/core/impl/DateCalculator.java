package com.norteksoft.wf.engine.core.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.wf.base.enumeration.DateOperator;
import com.norteksoft.wf.engine.core.Computable;

/**
 * 日期前后的运算器
 */
public class DateCalculator implements Computable {
	
	private  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public Boolean execute(String subExpression) {
		try {
			if(StringUtils.contains(subExpression, DateOperator.NLT.getCode())){
				Long leftOperand = simpleDateFormat.parse(StringUtils.substringBefore(subExpression, DateOperator.NLT.getCode()).trim()).getTime();
				Long rightOperand = simpleDateFormat.parse(StringUtils.substringAfterLast(subExpression, DateOperator.NLT.getCode()).trim()).getTime();
				return leftOperand.longValue()>=rightOperand.longValue();
			}else if(StringUtils.contains(subExpression, DateOperator.NGT.getCode())){
				Long leftOperand = simpleDateFormat.parse(StringUtils.substringBefore(subExpression, DateOperator.NGT.getCode()).trim()).getTime();
				Long rightOperand = simpleDateFormat.parse(StringUtils.substringAfterLast(subExpression, DateOperator.NGT.getCode()).trim()).getTime();
				return leftOperand.longValue()<=rightOperand.longValue();
			}else if(StringUtils.contains(subExpression, DateOperator.GT.getCode())){
				Long leftOperand = simpleDateFormat.parse(StringUtils.substringBefore(subExpression, DateOperator.GT.getCode()).trim()).getTime();
				Long rightOperand = simpleDateFormat.parse(StringUtils.substringAfterLast(subExpression, DateOperator.GT.getCode()).trim()).getTime();
				return leftOperand.longValue()>rightOperand.longValue();
			}else if(StringUtils.contains(subExpression, DateOperator.LT.getCode())){
				Long leftOperand = simpleDateFormat.parse(StringUtils.substringBefore(subExpression, DateOperator.LT.getCode()).trim()).getTime();
				Long rightOperand = simpleDateFormat.parse(StringUtils.substringAfterLast(subExpression, DateOperator.LT.getCode()).trim()).getTime();
				return leftOperand.longValue()<rightOperand.longValue();
			}else if(StringUtils.contains(subExpression, DateOperator.ET.getCode())){
				Long leftOperand = simpleDateFormat.parse(StringUtils.substringBefore(subExpression, DateOperator.ET.getCode()).trim()).getTime();
				Long rightOperand = simpleDateFormat.parse(StringUtils.substringAfterLast(subExpression, DateOperator.ET.getCode()).trim()).getTime();
				return leftOperand.longValue()==rightOperand.longValue();
			}else if(StringUtils.contains(subExpression, DateOperator.NET.getCode())){
				Long leftOperand = simpleDateFormat.parse(StringUtils.substringBefore(subExpression, DateOperator.NET.getCode()).trim()).getTime();
				Long rightOperand = simpleDateFormat.parse(StringUtils.substringAfterLast(subExpression, DateOperator.NET.getCode()).trim()).getTime();
				return leftOperand.longValue()!=rightOperand.longValue();
			}
			throw new RuntimeException(subExpression + " is invalid expression.");
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}


}
