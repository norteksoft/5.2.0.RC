package com.norteksoft.wf.base.exception;


/**
 * 没有发现启用的流程定义异常
 * @author wurong
 *
 */
public class NotFoundEnabledWorkflowDefinitionException extends WorkflowException {

	private static final long serialVersionUID = 1L;
	
	public NotFoundEnabledWorkflowDefinitionException(){
		super("没有发现启用的流程定义异常");
	}
	
	public NotFoundEnabledWorkflowDefinitionException(String message){
		super(message);
	}
}
