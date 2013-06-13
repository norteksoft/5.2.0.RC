package com.norteksoft.wf.engine.client;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.norteksoft.wf.base.enumeration.ProcessState;


/**
 * 供需走流程的实体嵌入，通知它可以在流程和实体间交互一些必要的数据
 * @author wurong
 *
 */
@Embeddable
public class WorkflowInfo {
	
	
	/**
	 * 是否创建特事特办 ，true为创建
	 */
	protected Boolean createSpecialTask = false; //是否进行特事特办
	
	/**
	 * 特事特办办理人的登录名
	 * 多个办理人用英文逗号隔开
	 */
	@Column(length=1000, nullable=true)
	protected String specialTaskTransactor;//特事特办办理人
	/**
	 * 流程实例的优先级 ，1代表最高优先级
	 */
	protected Integer priority = 6;//任务优先级
	/**
	 * 工作流实例的标识ID
	 */
	protected String workflowId; //流程实例id
	/**
	 * 该流程的第一个任务的id
	 */
	protected Long firstTaskId;//第一个任务的ID
	/**
	 * 流程流转过程中的业务状态
	 */
	protected String state;//流程状态
	/**
	 * 流程流转过程的中标准状态 
	 */
	protected ProcessState processState = ProcessState.UNSUBMIT; //流程运行中的状态
	/**
	 * 流程第一个环节被提交是的时间
	 */
	protected Date submitTime;//提交日期
	/**
	 * 流程结束的时间
	 */
	protected Timestamp endTime;//流程结束时间
	/**
	 * 流程当前环节名
	 */
	protected String currentActivityName;//当前环节名字
	/**
	 * 流程对应表单的ID
	 */
	protected Long formId;
	/**
	 * 流程定义的id
	 */
	protected String workflowDefinitionId;//流程Id
	/**
	 * 流程定义的名字
	 */
	protected String workflowDefinitionName;//流程名字
	/**
	 * 流程定义的编号
	 */
	protected String workflowDefinitionCode;//流程编号
	/**
	 * 流程定义的版本号
	 */
	protected Integer workflowDefinitionVersion;//流程版本号
	
	/**
	 * 当发生DecisionException异常时，将选择的具体流向放入这个属性后再提交
	 */
	@Transient
	protected String transitionName;//流向名
	
	/**
	 * 当发生TransactorAssignmentException异常时，将具体的办理人的登录名放入这个属性.多个办理人之间以英文逗号隔开
	 */
	@Transient
	protected String newTransactor;//流向名
	
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Date getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public String getCurrentActivityName() {
		return currentActivityName;
	}
	public void setCurrentActivityName(String currentActivityName) {
		this.currentActivityName = currentActivityName;
	}
	public Long getFirstTaskId() {
		return firstTaskId;
	}
	public void setFirstTaskId(Long firstTaskId) {
		this.firstTaskId = firstTaskId;
	}
	public ProcessState getProcessState() {
		return processState;
	}
	public void setProcessState(ProcessState processState) {
		this.processState = processState;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Long getFormId() {
		return formId;
	}
	public void setFormId(Long formId) {
		this.formId = formId;
	}
	public String getWorkflowDefinitionId() {
		return workflowDefinitionId;
	}
	public void setWorkflowDefinitionId(String workflowDefinitionId) {
		this.workflowDefinitionId = workflowDefinitionId;
	}
	public String getWorkflowDefinitionCode() {
		return workflowDefinitionCode;
	}
	public void setWorkflowDefinitionCode(String workflowDefinitionCode) {
		this.workflowDefinitionCode = workflowDefinitionCode;
	}
	public String getWorkflowDefinitionName() {
		return workflowDefinitionName;
	}
	public void setWorkflowDefinitionName(String workflowDefinitionName) {
		this.workflowDefinitionName = workflowDefinitionName;
	}
	public Boolean getCreateSpecialTask() {
		return createSpecialTask;
	}
	public void setCreateSpecialTask(Boolean createSpecialTask) {
		this.createSpecialTask = createSpecialTask;
	}
	
	public String getSpecialTaskTransactor() {
		return specialTaskTransactor;
	}
	public void setSpecialTaskTransactor(String specialTaskTransactor) {
		this.specialTaskTransactor = specialTaskTransactor;
	}
	
	public String getTransitionName() {
		return transitionName;
	}
	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;
	}
	public String getNewTransactor() {
		return newTransactor;
	}
	public void setNewTransactor(String newTransactor) {
		this.newTransactor = newTransactor;
	}
	public Integer getWorkflowDefinitionVersion() {
		return workflowDefinitionVersion;
	}
	public void setWorkflowDefinitionVersion(Integer workflowDefinitionVersion) {
		this.workflowDefinitionVersion = workflowDefinitionVersion;
	}
	@Override
	public String toString() {
		return new StringBuilder()
				.append("WorkflowInfo [currentActivityName=").append( currentActivityName)
				.append( ", endTime=" ).append( endTime ).append( ", firstTaskId=" ).append( firstTaskId)
				.append( ", formId=" ).append( formId ).append( ", priority=" ).append( priority)
				.append(", processState=" ).append( processState ).append( ", state=" ).append( state)
				.append( ", submitTime=" ).append( submitTime ).append( ", workflowId=" ).append( workflowId).append("]").toString();
	}
	
	
}
