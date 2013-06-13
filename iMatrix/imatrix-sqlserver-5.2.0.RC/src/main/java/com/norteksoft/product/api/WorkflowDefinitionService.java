package com.norteksoft.product.api;

import java.util.List;

import com.norteksoft.product.api.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowType;

/**
 * 公开提供给用户使用的工作流定义api
 * @author wurong
 *
 */
public interface WorkflowDefinitionService {
	
	/**
	 * 获取启用且版本最高的流程定义
	 * @param workflowDefinitionName 工作流定义名称
	 * @param companyId 公司id
	 * @return 流程定义
	 */
	public WorkflowDefinition getEnabledHighestVersionWorkflowDefinition(String workflowDefinitionCode);
	
	  /**
     * 根据任务id获得流程定义的id. 如果对应的任务不存在，则返回null
     * @param taskId 任务id
     * @return 流程定义id
     */
    public Long getWorkflowDefinitionIdByTask(Long taskId);
    
    /**
     * 用定义id查询流程定义
     * @param workflowDefinitionId 定义id
     * @return 流程定义
     */
    public WorkflowDefinition getWorkflowDefinition(Long workflowDefinitionId);
    
    /**
	 * 查询指定类型的已启用的流程定义
	 * @param typeNo 流程类型编号
	 * @return 流程定义集合
	 */
	public List<WorkflowDefinition> getWorkflowDefinitionsByTypeCode(String typeNo);
	/**
	 * 查询指定类型的已启用的流程定义
	 * @param workflowDefinitionCode 流程定义编号
	 * @return 流程定义集合
	 */
	public List<WorkflowDefinition> getWorkflowDefinitionsByCode(String workflowDefinitionCode);
	/**
	 * 根据流程定义编号和版本获得流程定义
	 * @param workflowDefinitionCode
	 * @param workflowDefinitionVersion
	 * @return
	 */
	public WorkflowDefinition getWorkflowDefinitionByCodeAndVersion(String workflowDefinitionCode,Integer workflowDefinitionVersion);
	/**
	 * 获得是审批系统的流程类型
	 * @return
	 */
	public List<WorkflowType> getApproveSystemWorkflowTypes();
	
	public List<WorkflowDefinition> getWorkflowDefinitionsByFormCodeAndVersion(String formCode,Integer version);
	
	/**
	 * 根据流程名称模糊查询某类别下的流程
	 * @param companyId
	 * @param typeId
	 * @return
	 */
	public List<WorkflowDefinition> getWorkflowDefinitionsByName(String typeNo,String name);
}
