package com.norteksoft.wf.engine.core;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;

public class EndEventListener implements EventListener{

	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 记录流转历史，人，时间，事情，结果，流向
	 */
	public void notify(EventListenerExecution execution) throws Exception {
		//标识流程实例已完成
	}

}
