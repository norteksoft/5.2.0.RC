package com.norteksoft.product.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.wf.base.exception.NotFoundEnabledWorkflowDefinitionException;
import com.norteksoft.wf.engine.client.FormFlowable;

/**
 * 表单和字段的api
 * @author wurong
 *
 */
public interface WorkflowFormService {

	/**
     * 根据task查询流程实例表单ID.
     * @param taskId 任务id
     * @return 表单id
     */
    public Long getFormIdByTask(Long taskId);
    
    /**
     * 根据task查询业务实体的ID
     * @param taskId 任务id
     * @return 业务实体的ID
     */
    public Long getFormFlowableIdByTask(Long taskId);
    
    /**
     * 根据task查询业务实体的ID
     * @param taskId 任务id
     * @param companyId 公司id
     * @return 业务实体的ID
     */
    @Deprecated
    public Long getFormFlowableIdByTask(Long taskId,Long companyId);
    
    /**
	 * 流程还未启动时,根据流程名称查询第一环节的字段编辑权限,以JSON格式返回
	 * @param processCode
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 * @return json格式的字段编辑权限
	 */
	public String getFieldPermissionNotStarted(String processCode);
	
	/**
	 * 流程还未启动时,根据流程名称查询第一环节的字段编辑权限,以JSON格式返回
	 * @param processCode 
	 * @param version
	 * @return json格式的字段编辑权限
	 */
	public String getFieldPermissionNotStarted(String processCode, Integer version);
	
	/**
	 * 流程还未启动时,根据流程名称查询第一环节的必填字段,以JSON格式返回
	 * @param processName
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 * @return 字段英文名称的集合
	 */
	public Collection<String> getNeedFillFieldsNotStarted(String processName);
	
	/**
	 * 流程还未启动时,根据流程名称查询第一环节的禁止编辑的字段,以JSON格式返回
	 * @param processName
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 * @return 字段英文名称的集合
	 * @deprecated
	 * 替换为<code>Collection<String> getForbiddenFieldsNotStarted(String processName)</code>
	 */
	@Deprecated
	public Collection<String> getforbiddenFieldsNotStarted(String processName);
	
	/**
	 * 流程还未启动时,根据流程名称查询第一环节的禁止编辑的字段,以JSON格式返回
	 * @param processName
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 * @return 字段英文名称的集合
	 */
	public Collection<String> getForbiddenFieldsNotStarted(String processName);
	
	/**
	 * 流程还未启动时,根据流程的definitionId查询第一环节的字段编辑权限,以JSON格式返回
	 * @param definitionId  
	 * @return json格式的字段编辑权限
	 */
	public String getFieldPermissionNotStarted(Long definitionId);
	
	/**
	 * 流程还未启动时,根据流程的definitionId查询第一环节的必填字段,以JSON格式返回
	 * @param definitionId  
	 * @return 字段英文名称的集合
	 */
	public Collection<String> getNeedFillFieldsNotStarted(Long definitionId);
	
	/**
	 * 流程还未启动时,根据流程的definitionId查询第一环节的禁止编辑的字段,以JSON格式返回
	 * @param definitionId  
	 * @return 字段英文名称的集合
	 * @deprecated
	 * 替换为<code>Collection<String> getForbiddenFieldsNotStarted(Long definitionId)</code>
	 */
	@Deprecated
	public Collection<String> getforbiddenFieldsNotStarted(Long definitionId);
	
	/**
	 * 流程还未启动时,根据流程的definitionId查询第一环节的禁止编辑的字段,以JSON格式返回
	 * @param definitionId  
	 * @return 字段英文名称的集合
	 */
	public Collection<String> getForbiddenFieldsNotStarted(Long definitionId);
	
	/**
	 * 查询流程中环节的字段编辑权限
	 * @param taskId
	 * @return 返回json格式表示的字段可编辑状态信息
	 */
	public String getFieldPermission( Long taskId);
	
	/**
	 * 查询流程中环节的必填的字段
	 * @param taskId
	 * @return 字段英文名称的集合
	 */
	public Collection<String> getNeedFillFields(Long taskId);
	
	/**
	 * 查询流程中环节的禁止编辑的字段
	 * @param taskId 任务id
	 * @return 字段英文名称的集合
	 * @deprecated
	 * 替换为<code>Collection<String> getForbiddenFields(Long taskId)</code>
	 */
	@Deprecated
	public Collection<String> getforbiddenFields(Long taskId);
	
	/**
	 * 查询流程中环节的禁止编辑的字段
	 * @param taskId 任务id
	 * @return 字段英文名称的集合
	 */
	public Collection<String> getForbiddenFields(Long taskId);
	
	/**
	 * 所有字段可编辑状态信息查询
	 * @param editable 当editable为false时 表示所有字段都禁止填写
	 * @return 返回json格式表示的字段可编辑状态信息
	 */
	public String getFieldPermission(boolean editable);
	
	/**
	 * 自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param entity 需要自动填写的实体
	 * @param wfDefinationId 流程定义ID
	 * @param taskId 任务id 如果该id为空，将填充第一个环节的值
	 */
	@Deprecated
	public void autoFilledEntityBeforeByDefinitionId(FormFlowable entity,Long definitionId);
	
	/**
	 * 修改为 {@link #fillEntityByDefinition(FormFlowable,String) </code>fillEntityByDefinition<code>}
	 * @param entity 需要自动填写的实体
	 * @param wfDefinationName 流程定义名称
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 */
	@Deprecated
	public void autoFilledEntityBeforeByDefinationName(FormFlowable entity,String wfDefinationName);
	
	/**
	 * 标准表单 流程还未启动时,自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param entity 需要自动填写的实体
	 * @param wfDefinationCode 流程定义编码
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 */
	public void fillEntityByDefinition(FormFlowable entity,String wfDefinationCode);
	
	/**
	 * 标准表单 流程还未启动时,自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param entity 需要自动填写的实体
	 * @param wfDefinationCode 流程定义编码
	 * @param version 流程定义版本
	 */
	public void fillEntityByDefinition(FormFlowable entity,String wfDefinationCode, Integer version);
	/**
	 *  自定义表单  流程还未启动时,自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param data 需要自动填写的实体
	 * @param wfDefinationCode 流程定义编码
	 * @param Long... systemId 自定义表单发起的流程，ContextUtils.getSystemId()获得的系统id是mms的id，不是流程定义真正的id
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 */
	public void fillEntityByDefinition(Map data,String wfDefinationCode,Long... systemId);
	
	/**
	 * 自定义表单  流程还未启动时,自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param entity 需要自动填写的实体
	 * @param wfDefinationCode 流程定义编码
	 * @param version 流程定义版本
	 * @param Long... systemId 自定义表单发起的流程，ContextUtils.getSystemId()获得的系统id是mms的id，不是流程定义真正的id
	 */
	public void fillEntityByDefinition(Map data,String wfDefinationCode, Integer version,Long... systemId);
	
	/**
	 * 修改为 {@link #fillEntityByTask(FormFlowable,Long) </code>fillEntityByTask<code>}
	 * @param entity 需要自动填写的实体  流程必须启动
	 * @param taskId 任务id 
	 */
	@Deprecated
	public void autoFilledEntityBefore(FormFlowable entity,Long taskId);
	
	/**
	 * 标准表单 自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param entity 需要自动填写的实体  流程必须启动
	 * @param taskId 任务id 
	 */
	public void fillEntityByTask(FormFlowable entity,Long taskId);
	/**
	 * 自定义表单 自动填写实体，在转向办理页面时，根据本环节定义的自动填写字段，来自动填充实体
	 * @param entity 需要自动填写的实体  流程必须启动
	 * @param taskId 任务id 
	 */
	public void fillEntityByTask(Map data,Long taskId);
	
	/**
	 * 返回表单打印权限
	 * @exception NotFoundEnabledWorkflowDefinitionException if not found started workflowDefinition by workflowDefinitionName
	 */
	@Deprecated
	public boolean formPrintRightNotStarted(String workflowDefinitionName);
	
	/**
	 * 返回表单打印权限
	 */
	@Deprecated
	public boolean formPrintRightNotStarted(Long definitionId);
	
	/**
	 * 返回表单打印权限
	 */
	@Deprecated
	public boolean formPrintRight(Long taskId);
	
	
	/**
	 * 根据formId查询所有表单字段
	 * @param formId
	 * @return 字段列表
	 */
	public List<FormControl> getFormControls(Long formId);
	/**
	 * 自定义表单 保存数据
	 * @param parameter
	 * @return
	 */
	public Long saveData(Map<String,String[]> parameter);
	
}
