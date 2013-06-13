package com.norteksoft.wf.base.exception;

/**
 * 工作流系统异常
 * @author wurong
 *
 */
public class WorkflowException extends RuntimeException {
	public static final String NO_TRANSACTOR = "transactorAssignmentException.no.transactor(没有办理人被指定)";//没有办理人被指定
	public static final String MORE_TRANSITION = "decisionException.more.transition(多个流向满足条件)";//多个流向满足条件
	public static final String NO_TRANSITION = "decisionException.no.transition(没有流向满足条件)";//没有流向满足条件
	
	private static final long serialVersionUID = 1L;
	
	
	public WorkflowException(){
		super();
	} 
	public WorkflowException(String message){
		super(message);
	} 
	public WorkflowException(String message, Throwable cause){
		super(message,cause);
	} 
	public WorkflowException(Throwable cause){
		super(cause);
	}  

	
}
