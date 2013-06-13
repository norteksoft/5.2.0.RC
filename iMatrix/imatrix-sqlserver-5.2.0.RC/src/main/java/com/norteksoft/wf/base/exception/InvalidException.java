package com.norteksoft.wf.base.exception;

/**
 * 无效表达式异常
 * @author wurong
 *
 */
public class InvalidException extends WorkflowException {

	private static final long serialVersionUID = 1L;
	
	public InvalidException(){
		super("无效表达式异常");
	}
	
	public InvalidException(String message){
		super(message);
	}
}
