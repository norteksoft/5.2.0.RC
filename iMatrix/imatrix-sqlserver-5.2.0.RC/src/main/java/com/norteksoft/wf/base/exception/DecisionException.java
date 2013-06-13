package com.norteksoft.wf.base.exception;

import java.util.List;
/**
 * 判断环节异常
 * @author wurong
 */
public class DecisionException extends WorkflowException{
	private static final long serialVersionUID = 1L;
	
	private List<String[]> transitionNames;

	public DecisionException( String message,List<String[]> transitionNames) {
		super(message);
		this.transitionNames = transitionNames;
	}

	public List<String[]> getTransitionNames() {
		return transitionNames;
	}
}
