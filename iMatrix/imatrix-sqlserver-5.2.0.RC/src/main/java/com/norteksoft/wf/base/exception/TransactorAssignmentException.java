package com.norteksoft.wf.base.exception;

/**
 * 办理人指定异常
 * @author wurong
 */
public class TransactorAssignmentException extends WorkflowException{
private static final long serialVersionUID = 1L;
	
	public TransactorAssignmentException( String message) {
		super(message);
	}
}
