package com.norteksoft.wf.engine.client;

/**
 * 要让表单实体可以在工作流引擎中流转，必须实现该接口
 * @author wurong
 */
public interface FormFlowable {
	/**
	 * 获得数据ID，即实体的id
	 * @return 实体的id
	 */
	public Long getId();
	
	/**
	 * 获得公司ID
	 * @return 公司id
	 */
	public Long getCompanyId();
	
	/**
	 * 返回嵌入的<{@link WorkflowInfo}
	 * @return 返回<code>WorkflowInfo</code>实例
	 */
	public WorkflowInfo getWorkflowInfo();
	/**
	 * 设置嵌入的<{@link WorkflowInfo}
	 * @param workflowInfo <code>WorkflowInfo</code>实例
	 */
	public void setWorkflowInfo(WorkflowInfo workflowInfo); 
	
	/**
	 * 获得扩展字段组件
	 * @return 扩展字段组件
	 */
	public ExtendField getExtendField();
	/**
	 * 设置扩展字段组件
	 * @param extendField 扩展字段组件
	 */
	public void setExtendField(ExtendField extendField);
	
}
