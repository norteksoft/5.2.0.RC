package com.norteksoft.bs.options.enumeration;
/**
 * 导入方式
 * @author Administrator
 *
 */
public enum ImportWay {
	SUCCESS("bs.import.way.success"),//所有数据正确后导入
	ONLY_SUCCESS("bs.import.way.only.success"),//只导入正确数据
	HAVE_ERROR("bs.import.way.have.error");//有错误数据就不导入
	
	public String code;
	ImportWay(String code){
		this.code=code;
	}
	public String getCode() {
		return code;
	}
}
