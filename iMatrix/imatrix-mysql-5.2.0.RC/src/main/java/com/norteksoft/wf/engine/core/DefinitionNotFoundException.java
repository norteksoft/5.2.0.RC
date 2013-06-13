package com.norteksoft.wf.engine.core;

/**
 *  <code>DefinitionNotFoundException</code> 是在需要流程定义，但根据用户给的条件未找到该定义时，抛出该异常
 * <p>
 * @author wurong
 *
 */
@SuppressWarnings("serial")
public class DefinitionNotFoundException extends RuntimeException {
	/**
	 * 用 null 作为其详细消息构造一个新的运行时异常。
	 */
	public DefinitionNotFoundException(){
		super();
	} 
	public DefinitionNotFoundException(String message) {
		super(message);
	}
	public DefinitionNotFoundException(String message, Throwable cause) {
		super(message,cause);
	}
	public DefinitionNotFoundException(Throwable cause) {
		super(cause);
	}

}
