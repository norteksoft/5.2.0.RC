package com.norteksoft.wf.base.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bsh.EvalError;
import bsh.Interpreter;

import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.wf.base.enumeration.LogicOperator;
import com.norteksoft.wf.base.exception.InvalidException;
import com.norteksoft.wf.engine.core.CalculatorFactory;
import com.norteksoft.wf.engine.core.Computable;

/**
 * BeanShell工具类
 * @author wurong
 *
 */
public class BeanShellUtil {

	private static Interpreter i = new Interpreter();
	
	private static Log log=LogFactory.getLog(BeanShellUtil.class);
	
	/**
	 * 判断一个表达式的真假
	 * @param exce 表达式
	 * @return 表达式的计算结果
	 */
	public static Boolean evel(String exce) {
		try {
			log.debug("表单式为:" + exce);
			i.eval("result=" + exce);
			log.debug("表达式运算结果为:"+ i.get("result").toString());
			if(i.get("result").toString().equalsIgnoreCase("true") || i.get("result").toString().equalsIgnoreCase("false")){
				return new Boolean(i.get("result").toString());
			}else{
				throw new InvalidException("表达式的计算结果不为ture 或者 false");
				
			}
		} catch (EvalError e) {
			log.debug("无效的表达式异常:" + exce);
			throw  new InvalidException("无效的表达式");
		}
	}
	
	public static Object evelExpress(String exce){
		try {
			log.debug("表单式为:" + exce);
			i.eval("result=" + exce);
			log.debug("表达式运算结果为:"+ i.get("result").toString());
			return i.get("result");
		} catch (EvalError e) {
			log.debug("无效的表达式异常:" + exce);
			throw  new InvalidException("无效的表达式");
		}
	}
	
	private static String LOGMESSAGE_INVOKING_METHOD = " invoking method: ";
	private static final String ASTERISK_REGEX = "\\*";
	private static final String PARENTHESES_REGEX = "[)(]";
	private static final String EMPTY_STRING = "";
	
	public static boolean execute(String replacedStr,DataType type,String targetStr,String value){
		 if(type==DataType.TEXT||type==DataType.BOOLEAN||type==DataType.ENUM){
				value = value + "(";
			}
			Computable computable = CalculatorFactory.getCalculator(type);
			return  computable.execute(StringUtils.replace(replacedStr, targetStr, value));
	}
	
	/*
	 * 将表达式分割为原子表达式
	 */
	public static String[] splitExpression(String express){
		log.info(LOGMESSAGE_INVOKING_METHOD + "String[] splitExpression(String express)");
		
		express = express.replaceAll(LogicOperator.AND.getCode(), ASTERISK_REGEX);
		express = express.replaceAll(LogicOperator.OR.getCode(), ASTERISK_REGEX);
		express = express.replaceAll(PARENTHESES_REGEX, EMPTY_STRING);
		return express.split(ASTERISK_REGEX);
	}
}
