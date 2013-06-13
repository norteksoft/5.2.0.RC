package com.norteksoft.acs.base.utils;

import com.norteksoft.product.util.ThreadParameters;

/**
 * 请更换为 com.norteksoft.product.util.ParameterUtils
 */
@Deprecated
public class ParametersUtil {
	private static ThreadLocal<ThreadParameters> threadParameters=new ThreadLocal<ThreadParameters>();
	
	public static void setParameters(ThreadParameters parameters){
		threadParameters.set(parameters);
	}
	
	public static Long getCompanyId(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getCompanyId();
	}
	
	public static Long getUserId(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getUserId();
	}
	public static String getCompanyCode(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getCompanyCode();
	}
	
	public static String getCompanyName(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getCompanyName();
	}
	
	public static String getUserName(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getUserName();
	}
	public static String getPassword(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getPassword();
	}
	public static String getHonorificTitle(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getHonorificTitle();
	}
	public static String getLoginName(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getLoginName();
	}
	
}
