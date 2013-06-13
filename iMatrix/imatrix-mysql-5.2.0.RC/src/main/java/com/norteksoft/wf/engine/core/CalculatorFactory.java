package com.norteksoft.wf.engine.core;

import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.wf.engine.core.impl.AmountCalculator;
import com.norteksoft.wf.engine.core.impl.DateCalculator;
import com.norteksoft.wf.engine.core.impl.NumberCalculator;
import com.norteksoft.wf.engine.core.impl.TextCalculator;
import com.norteksoft.wf.engine.core.impl.TimeCalculator;

/**
 * 运算器的生成工厂
 */
public class CalculatorFactory {

	/*
	 * TEXT("文本"),
	DATE("日期"),
	TIME("时间"),
	INTEGER("整型"),
	LONG("长整型"),
	DOUBLE("双精度浮点数"),
	FLOAT("单精度浮点数"),
	BOOLEAN("布尔型"),
	CLOB("大文本"),
	BLOB("大字段")
	 */
	public static Computable getCalculator(DataType type){
		Computable computable = null;
		if(type==DataType.AMOUNT||type==DataType.DOUBLE||type==DataType.FLOAT){
			computable = new AmountCalculator();
		}else if(type==DataType.NUMBER||type==DataType.LONG||type==DataType.INTEGER){
			computable = new NumberCalculator();
		}else if(type==DataType.DATE){
			computable = new DateCalculator();
		}else if(type==DataType.TEXT||type==DataType.BOOLEAN||type==DataType.ENUM){
			computable = new TextCalculator();
		}else if(type==DataType.TIME){
			computable = new TimeCalculator();
		}
		return computable;
	}
}
