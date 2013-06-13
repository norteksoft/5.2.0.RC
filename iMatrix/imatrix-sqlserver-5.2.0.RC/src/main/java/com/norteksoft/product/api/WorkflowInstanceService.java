package com.norteksoft.product.api;

import java.util.Map;

import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.product.api.entity.WorkflowInstance;

/**
 * 公开提供给用户使用的工作流实例的api
 * @author wurong
 *
 */
public interface WorkflowInstanceService {

	/**
	 * 封装urlMap时，表单查看url的key。
	 *@see WorkflowInstanceService#submitInstance(String, FormFlowable, Map)
	 */
	public final static String WF_FORM_URL = "workflow_form_url";

	/**
	 * 手动结束流程 
	 * @param entity 业务实体
	 */
	public void endInstance(FormFlowable entity);
	
	/**
	 * 用户保存实体后,根据流程定义名称来启动流程,如果流程启动，则不做任何处理直接返回
	 * @param definitionCode 流程定义编号
	 * @return entity 业务实体
	 */
	public void startInstance(String definitionCode, FormFlowable entity);
	
	/**
	 * 用户保存实体后,根据流程定义名称来启动流程,如果流程启动，则不做任何处理直接返回
	 * @param definitionCode 流程定义编号
	 * @param definitionVersion 流程定义版本号
	 * @return entity 业务实体
	 * 
	 */
	public void startInstance(String definitionCode,Integer definitionVersion, FormFlowable entity);
	
	/**
	 * 用户保存实体后,根据流程定义id来启动流程,如果流程启动，则不做任何处理直接返回
	 * @param workflowDefinitionId 流程定义id
	 * @return entity 业务实体
	 */
	public void startInstance(Long definitionId,FormFlowable entity);
	
	/**
	 * 提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 * @param definitionCode 流程定义编号
	 * @param entity 业务实体
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 * 
	 */
	public CompleteTaskTipType submitInstance(String definitionCode, FormFlowable entity);
	
	/**
	 * 提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 * @param definitionCode 流程定义编号
	 * @param definitionVersion 流程定义版本
	 * @param entity 业务实体
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 * 
	 */
	public CompleteTaskTipType submitInstance(String definitionCode,Integer definitionVersion, FormFlowable entity);
	
	/**
	 * 提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 * @param definitionId 工作流定义id
	 * @param entity 业务实体
	 * @param urlMap
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
     */
	public CompleteTaskTipType submitInstance(Long definitionId, FormFlowable entity);
	/**
	 * 自定义表单 用户保存实体后,根据流程定义名称来启动流程,如果流程启动，则不做任何处理直接返回
	 * @param definitionCode 流程定义编号
	 * @return Map {dataId:,instanceId:}
	 */
	public Map startCustomInstance(String definitionCode);
	
	/**
	 * 自定义表单 用户保存实体后,根据流程定义名称来启动流程,如果流程启动，则不做任何处理直接返回
	 * @param definitionCode 流程定义编号
	 * @param definitionVersion 流程定义版本号
	 * @return Map {dataId:,instanceId:}
	 * 
	 */
	public Map startCustomInstance(String definitionCode,Integer definitionVersion);
	
	/**
	 * 自定义表单 用户保存实体后,根据流程定义id来启动流程,如果流程启动，则不做任何处理直接返回
	 * @param workflowDefinitionId 流程定义id
	 * @return Map {dataId:,instanceId:}
	 */
	public Map startCustomInstance(Long definitionId);
	
	/**
	 * 自定义表单 提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 * @param definitionCode 流程定义编号
	 * @param entity 业务实体
	 * @return map {dataId:,instanceId:,result:CompleteTaskTipType}
	 * 
	 */
	public Map submitCustomInstance(String definitionCode);
	
	/**
	 * 自定义表单 提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 * @param definitionCode 流程定义编号
	 * @param definitionVersion 流程定义版本
	 * @param entity 业务实体
	 * @return map {dataId:,instanceId:,result:CompleteTaskTipType}
	 * 
	 */
	public Map submitCustomInstance(String definitionCode,Integer definitionVersion);
	
	/**
	 * 自定义表单 提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 * @param definitionId 工作流定义id
	 * @param entity 业务实体
	 * @param urlMap
	 * @return map {dataId:,instanceId:,result:CompleteTaskTipType}
	 */
	public Map submitCustomInstance(Long definitionId);
	
	/**
	 * 提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 * @param definitionName 流程定义名字
	 * @param entity 业务实体
	 * @param urlMap 封装了url的Map .urlMap中了流程中任务办理页面的url，供task系统打开该url来办理任务；还有表单的查看页面的url，供流程监控中打开表单。
	 * 					办理页面的url，要以流程定义名称为key，url为值。如果有子流程，子流程的url和父流程的url封装方法一样。
	 * 					表单查看页面的url，要以{@link  WorkflowInstanceService#WF_FORM_URL}为key，url为值。实体的id会在打开url时，会拼接在url最后。
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	 *@deprecated 
     * 替换为 <code>submitInstance(String definitionName, FormFlowable entity)</code>；原来urlMap中的参数可以在流程定义中流程属性的参数设置中设置。
     */
    @Deprecated
	public CompleteTaskTipType submitInstance(String definitionName, FormFlowable entity, Map<String,String> urlMap);
	
	
	/**
	 * 提交流程
	 * 提交前用户保存自己的实体；
	 * 如果流程没有启动，则启动流程并提交
	 * 若流程已经在保存时启动了，则只提交流程
	 * @param workflowDefinitionId 工作流定义id
	 * @param entity 业务实体
	 * @param urlMap
	 * @return CompleteTaskTipType {@link com.norteksoft.wf.base.enumeration.CompleteTaskTipType}
	  *@deprecated 
     * 替换为 <code>submitInstance(Long workflowDefinitionId, FormFlowable entity)</code>；原来urlMap中的参数可以在流程定义中流程属性的参数设置中设置。
     */
    @Deprecated
	public CompleteTaskTipType submitInstance(Long definitionId, FormFlowable entity, Map<String,String> urlMap);
	
	/**
	 * 删除流程实例，(调用该方法删除工作流实例时，调用者不用删除自己的数据，只需要实现接口FormFlowableDeleteInterface {@link com.norteksoft.wf.engine.client.FormFlowableDeleteInterface})， 然后将实现类设置到流程定义->流程属性->参数设置中的删除流程实例设置执行方法中
	 * @param entity 业务实体
	 */
	public void deleteInstance(FormFlowable entity);
	/**
	 * 自定义表单 删除流程实例，(调用该方法删除工作流实例时，调用者不用删除自己的数据，只需要实现接口FormFlowableDeleteInterface {@link com.norteksoft.wf.engine.client.FormFlowableDeleteInterface})， 然后将实现类设置到流程定义->流程属性->参数设置中的删除流程实例设置执行方法中
	 * @param entity 业务实体
	 */
	public void deleteInstance(String instanceId);
	
	 /**
	 * 在环节办理时，当前环节办理人是否有权删除流程实例
	 * @param entity 业务实体
	 * @param taskName 当前环节名称
	 * @return true 为可以，false为不可以
	 */
	public boolean canDeleteInstanceInTask(FormFlowable entity, String taskName);
	/**
	 * 自定义表单  在环节办理时，当前环节办理人是否有权删除流程实例
	 * @param instanceId 流程实例id
	 * @param taskName 当前环节名称
	 * @return true 为可以，false为不可以
	 */
	public boolean canDeleteInstanceInTask(String instanceId, String taskName);
	
	/**
	 * 流程是否结束
	 * @param entity 业务实体
	 * @return
	 */
	public boolean isInstanceComplete(FormFlowable entity);
	
	 /**
     * 根据实例id查询流程实例
     * @param workflowId 流程实例的唯一标识
     * @return 流程实例
     */
    public WorkflowInstance getInstance(String workflowId);
}
