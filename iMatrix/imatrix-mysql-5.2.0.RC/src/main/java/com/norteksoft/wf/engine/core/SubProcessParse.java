package com.norteksoft.wf.engine.core;

import java.io.Serializable;

/**
 * 封装子流程初始化变量
 * @author ldx
 *
 */
public class SubProcessParse  implements Serializable{
	private static final long serialVersionUID = 1L;
	private String parentInstanceId;
	private String activityName;
	private String subDefinitionId;
	private String parentDefinitionId;
	
	private String parentExecutionId;
	private String creator;
	private int priority = 6;
	private String executionId;
	public String getParentInstanceId() {
		return parentInstanceId;
	}
	public void setParentInstanceId(String parentInstanceId) {
		this.parentInstanceId = parentInstanceId;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public String getSubDefinitionId() {
		return subDefinitionId;
	}
	public void setSubDefinitionId(String subDefinitionId) {
		this.subDefinitionId = subDefinitionId;
	}
	public String getParentDefinitionId() {
		return parentDefinitionId;
	}
	public void setParentDefinitionId(String parentDefinitionId) {
		this.parentDefinitionId = parentDefinitionId;
	}
	public String getParentExecutionId() {
		return parentExecutionId;
	}
	public void setParentExecutionId(String parentExecutionId) {
		this.parentExecutionId = parentExecutionId;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getPriority() {
		return priority;
	}
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	public String getExecutionId() {
		return executionId;
	}
}
