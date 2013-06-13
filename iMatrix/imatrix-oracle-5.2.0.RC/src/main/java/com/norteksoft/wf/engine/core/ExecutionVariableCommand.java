package com.norteksoft.wf.engine.core;

import java.util.Map;

import org.jbpm.api.Execution;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.cmd.Command;
import org.jbpm.api.cmd.Environment;
import org.jbpm.pvm.internal.model.ExecutionImpl;

public class ExecutionVariableCommand implements Command<Execution>{

	private static final long serialVersionUID = 1L;
	private String executionId;
	private String variableName;
	private Map<String, String> variables; 
	private Object variableValue;
	
	public ExecutionVariableCommand(String executionId, String variableName,Object variableValue) {
		this.executionId = executionId;
		this.variableName = variableName;
		this.variableValue = variableValue;
	}
	
	public ExecutionVariableCommand(String executionId, Map<String, String> variables) {
		this.executionId = executionId;
		this.variables = variables;
	}

	public Execution execute(Environment environment) throws Exception {
		
		ProcessEngine engine = environment.get(ProcessEngine.class);
		ExecutionImpl execution = (ExecutionImpl) engine.getExecutionService().findExecutionById(executionId);
		if(variableName != null){
			execution.createVariable(variableName, variableValue);
		}
		if(variables != null){
			for(String key : variables.keySet()){
				execution.createVariable(key, variables.get(key));
			}
		}
		return null;
	}

}
