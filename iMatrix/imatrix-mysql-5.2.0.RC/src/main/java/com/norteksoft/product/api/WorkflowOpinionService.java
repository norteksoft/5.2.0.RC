package com.norteksoft.product.api;

import java.util.List;

import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.wf.engine.client.FormFlowable;

/**
 * 工作流办理意见的api
 * @author wurong
 *
 */
public interface WorkflowOpinionService {

	/**
	 * 查询某个任务的办理意见
	 * @param taskId 任务id
	 * @return 意见的集合
	 */
	public List<Opinion> getOpinions(Long taskId);
	/**
	 * 查询整个流程的办理意见
	 * @param entity 业务实体
	 * @return 意见的集合
	 */
	public List<Opinion> getOpinions(FormFlowable entity);
	/**
	 * 查询整个流程的办理意见
	 * @param entity 业务实体
	 * @return 意见的集合
	 */
	public List<Opinion> getAllOpinions(String instanceId);
	
	/**
	 * 查询整个实例中具体办理模式的意见
	 * @param entity 业务实体
	 * @param taskMode 任务方式
	 * @return  意见的集合
	 */
	public List<Opinion> getOpinions(FormFlowable entity,TaskProcessingMode taskMode);
	/**
	 * 查询整个实例中具体办理模式的意见
	 * @param entity 业务实体
	 * @param taskMode 任务方式
	 * @return  意见的集合
	 */
	public List<Opinion> getAllOpinions(String instanceId,TaskProcessingMode taskMode);
	
	/**
	 * 修改为 {@link #getOpinionsExcludeTaskMode(FormFlowable,TaskProcessingMode) </code>getOpinionsExcludeTaskMode<code>}
	 * @param entity 业务实体
	 * @param taskMode 任务方式
	 * @return  意见集合
	 */
	@Deprecated
	public List<Opinion> getOpinionsExceptTaskMode(FormFlowable entity,TaskProcessingMode taskMode);
	
	/**
	 * 查询整个实例中不是该办理模式的意见
	 * @param entity 业务实体
	 * @param taskMode 任务方式
	 * @return  意见集合
	 */
	public List<Opinion> getOpinionsExcludeTaskMode(FormFlowable entity,TaskProcessingMode taskMode);
	/**
	 * 查询整个实例中不是该办理模式的意见
	 * @param entity 业务实体
	 * @param taskMode 任务方式
	 * @return  意见集合
	 */
	public List<Opinion> getOpinionsExcludeTaskMode(String instanceId,TaskProcessingMode taskMode);
	
	/**
	 * 查询整个实例中具体环节的意见
	 * @param entity 业务实体
	 * @param taskName 任务名称
	 * @return 意见集合
	 */
	public List<Opinion> getOpinions(FormFlowable entity,String taskName);
	/**
	 * 查询整个实例中具体环节的意见
	 * @param entity 业务实体
	 * @param taskName 任务名称
	 * @return 意见集合
	 */
	public List<Opinion> getAllOpinions(String instanceId,String taskName);
	
	/**
	 * 查询整个实例中具体环节的意见
	 * @param entity
	 * @param taskNames 任务名称数组
	 * @return  附件列表
	 */
	public List<Opinion> getOpinions(FormFlowable entity,String... taskNames);
	/**
	 * 查询整个实例中具体环节的意见
	 * @param entity
	 * @param taskNames 任务名称数组
	 * @return  附件列表
	 */
	public List<Opinion> getOpinions(String instanceId,String... taskNames);
	
	/**
	 * 修改为 {@link #getOpinionsExcludeTaskName(FormFlowable,String...) </code>getOpinionsExcludeTaskName<code>}
	 * @param entity 业务实体
	 * @param taskName 任务名称
	 * @return  意见集合
	 */
	@Deprecated
	public List<Opinion> getOpinionsExceptTaskName(FormFlowable entity,String taskName);
	
	/**
	 * 修改为 {@link #getOpinionsExcludeTaskName(FormFlowable,String...) </code>getOpinionsExcludeTaskName<code>}
	 * @param entity 业务实体
	 * @param taskNames 任务名称数组
	 * @return  意见集合
	 */
	@Deprecated
	public List<Opinion> getOpinionsExceptTaskName(FormFlowable entity,String... taskNames);
	
	/**
	 * 查询整个实例中不是该环节的意见
	 * @param entity 业务实体
	 * @param taskNames 任务名称
	 * @return  意见集合
	 */
	public List<Opinion> getOpinionsExcludeTaskName(FormFlowable entity,String... taskName);
	/**
	 * 查询整个实例中不是该环节的意见
	 * @param entity 业务实体
	 * @param taskNames 任务名称
	 * @return  意见集合
	 */
	public List<Opinion> getOpinionsExcludeTaskName(String instanceId,String... taskName);
	
	/**
	 * 查询整个实例中“自定义类别”的意见
	 * @param entity 业务实体
	 * @param customField 自定义类别
	 * @return  意见集合
	 */
	public List<Opinion> getOpinionsByCustomField(FormFlowable entity,String customField);
	
	/**
	 * 查询整个实例中“自定义类别”的意见
	 * @param entity 业务实体
	 * @param customField 自定义类别
	 * @return  意见集合
	 */
	public List<Opinion> getOpinionsByCustomField(String instanceId,String customField);
	
	/**
	 * 修改为 {@link #getOpinionsExcludeCustomField(FormFlowable,String) </code>getOpinionsExcludeCustomField<code>}
	 * @param entity 业务实体
	 * @param customField 自定义类别
	 * @return  意见集合
	 */
	@Deprecated
	public List<Opinion> getOpinionsExceptCustomField(FormFlowable entity,String customField);
	
	/**
	 * 查询整个实例中不是“自定义类别”的意见
	 * @param entity 业务实体
	 * @param customField 自定义类别
	 * @return  意见集合
	 */
	public List<Opinion> getOpinionsExcludeCustomField(FormFlowable entity,String customField);
	/**
	 * 查询整个实例中不是“自定义类别”的意见
	 * @param entity 业务实体
	 * @param customField 自定义类别
	 * @return  意见集合
	 */
	public List<Opinion> getOpinionsExcludeCustomField(String instanceId,String customField);
	
	/**
	 * 查询具体某条办理意见
	 * @param opinionId 意见的id
	 * @return  意见实体
	 */
	public Opinion getOpinionById(Long opinionId);
	
	/**
	 * 保存意见,环节的办理模式为当前环节办理模式,且任务名为当前任务名
	 * @param opinion 意见实体
	 * @param taskId 任务id
	 *  注意:taskId是必须传的设置的参数即：opinion.setTaskId(taskId)，注意在修改正文时，设置意见id,即：opinion.setId(opinionId);
	 */
	public void saveOpinion(Opinion opinion);
	/**
	 * 保存意见
	 */
	public void saveOpinion(Opinion opinion,Long taskId);
	
	/**
	 * 删除意见
	 * @param opinionId 意见id
	 */
	public void deleteOpinion(Long opinionId);
	
	/**
	 * 修改为 {@link #getOpinionsByTaskCode(FormFlowable,String...) </code>getOpinionsByTaskCode<code>}
	 */
	@Deprecated
	public List<Opinion> getOpinionsByTacheCode(FormFlowable entity,String tacheCode);
	
	/**
	 * 修改为 {@link #getOpinionsByTaskCode(FormFlowable,String...) </code>getOpinionsByTaskCode<code>}
	 */
	@Deprecated
	public List<Opinion> getOpinionsByTacheCode(FormFlowable entity,String... taskCodes);

	/**
	 * 查询整个实例中具体环节编码的意见
	 * @param entity
	 * @param tacheCodes 环节编码数组
	 * @return  意见集合
	 */
	public List<Opinion> getOpinionsByTaskCode(FormFlowable entity,String... taskCodes);
	/**
	 * 查询整个实例中具体环节编码的意见
	 * @param entity
	 * @param tacheCodes 环节编码数组
	 * @return  意见集合
	 */
	public List<Opinion> getOpinionsByTaskCode(String instanceId,String... taskCodes);
	
	/**
	 * 查询整个实例中具体环节的意见
	 * @param entity
	 * @param taskName 环节名称
	 * @return 意见集合
	 * @deprecated
	 *  为兼容历史数据，bky中用到
	 */
	public List<Opinion> getOpinionsByTaskName(FormFlowable entity,String taskName);
	
	/**
	 * 查询整个实例中具体环节的意见
	 * @param entity
	 * @param taskNames 环节名称数组
	 * @return  意见集合
	 * @deprecated
	 *  为兼容历史数据，bky中用到
	 */
	public List<Opinion> getOpinionsByTaskName(FormFlowable entity,String... taskNames);
	
	/**
	 * 返回当前用户编辑意见的权限
	 * @param taskId 任务id
	 */
	@Deprecated
	public boolean editOpinion( Long taskId );
	
	/**
	 * 返回意见是否必填
	 * @param taskId 任务id
	 */
	@Deprecated
	public boolean mustOpinion(Long taskId );
	
	/**
	 * 流程还未启动时编辑意见的权限
	 * @param definitionCode 流程定义编号
	 */
	@Deprecated
	public boolean editOpinionNotStarted(String definitionCode );
	
	/**
	 * 流程还未启动时编辑意见的权限
	 * @param definitionId 流程定义id
	 */
	@Deprecated
	public boolean editOpinionNotStarted(Long definitionId );
	
	/**
	 * 流程还未启动时意见是否必填
	 * @param definitionCode 流程定义编号
	 */
	@Deprecated
	public boolean mustOpinionNotStarted(String definitionCode);
	
	/**
	 * 流程还未启动时意见是否必填
	 * @param definitionId 流程定义id
	 */
	@Deprecated
	public boolean mustOpinionNotStarted(Long definitionId);
	/**
	 * 查询任务和意见
	 * @param taskId
	 * @return 返回集合,Object[任务实体,意见实体]
	 */
	@Deprecated
	public List<Object[]> getTaskAndOpinion( Long taskId);
}
