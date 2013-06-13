package com.norteksoft.product.api;

import java.util.List;

import com.norteksoft.product.api.entity.Document;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.wf.engine.client.FormFlowable;

/**
 * 正文的api
 * @author wurong
 *
 */
public interface WorkflowDocumentService {

	/**
	 * 获得某个环节的正文
	 * @param taskId 环节id
	 */
	public List<Document> getDocuments(Long taskId); 
	
	/**
	 * 获得整个实例正文列表
	 * @param entity 业务实体
	 * @return 正文列表
	 */
	public List<Document> getDocuments(FormFlowable entity); 
	/**
	 * 获得整个实例正文列表
	 * @param instanceId 实例id
	 * @return 正文列表
	 */
	public List<Document> getDocuments(String instanceId); 
	
	/**
	 * 查询整个实例中具体办理模式的正文
	 * @param entity
	 * @param taskMode
	 * @return  正文列表
	 */
	public List<Document> getDocuments(FormFlowable entity,TaskProcessingMode taskMode);
	/**
	 * 查询整个实例中具体办理模式的正文
	 * @param instanceId 实例id
	 * @param taskMode
	 * @return  正文列表
	 */
	public List<Document> getAllDocuments(String instanceId,TaskProcessingMode taskMode);
	
	/**
	 * 修改为 {@link #getDocumentsExcludeTaskMode(FormFlowable,TaskProcessingMode) </code>getDocumentsExcludeTaskMode<code>}
	 * 查询整个实例中不是该办理模式的正文
	 * @param entity
	 * @param taskMode
	 * @return  正文列表
	 */
	@Deprecated
	public List<Document> getDocumentsExceptTaskMode(FormFlowable entity,TaskProcessingMode taskMode);
	
	/**
	 * 查询整个实例中不是该办理模式的正文
	 * @param entity
	 * @param taskMode
	 * @return  正文列表
	 */
	public List<Document> getDocumentsExcludeTaskMode(FormFlowable entity,TaskProcessingMode taskMode);
	
	/**
	* 查询整个实例中不是该办理模式的正文
	* @param instanceId 实例id
	* @param taskMode
	* @return  正文列表
	*/
	public List<Document> getDocumentsExcludeTaskMode(String instanceId,TaskProcessingMode taskMode);
	
	/**
	 * 查询整个实例中具体环节的正文
	 * @param entity
	 * @param taskName
	 * @return  正文列表
	 */
	public List<Document> getDocuments(FormFlowable entity,String taskName);
	/**
	 * 查询整个实例中具体环节的正文
	 * @param instanceId 实例id
	 * @param taskName
	 * @return  正文列表
	 */
	public List<Document> getAllDocuments(String instanceId,String taskName);
	
	/**
	 * 修改为 {@link #getDocumentsExcludeTaskName(FormFlowable,String) </code>getDocumentsExcludeTaskName<code>}
	 * 查询整个实例中不是该环节的正文
	 * @param entity
	 * @param taskName
	 * @return  正文列表
	 */
	@Deprecated
	public List<Document> getDocumentsExceptTaskName(FormFlowable entity,String taskName);
	
	/**
	 * 查询整个实例中不是该环节的正文
	 * @param entity
	 * @param taskName
	 * @return  正文列表
	 */
	public List<Document> getDocumentsExcludeTaskName(FormFlowable entity,String taskName);
	/**
	 * 查询整个实例中不是该环节的正文
	 * @param instanceId 实例id
	 * @param taskName
	 * @return  正文列表
	 */
	public List<Document> getDocumentsExcludeTaskName(String instanceId,String taskName);
	
	/**
	 * 查询整个实例中“自定义类别”的正文
	 * @param entity
	 * @param customField 自定义类别
	 * @return  正文列表
	 */
	public List<Document> getDocumentsByCustomField(FormFlowable entity,String customField);
	/**
	 * 查询整个实例中“自定义类别”的正文
	 * @param instanceId 实例id
	 * @param customField 自定义类别
	 * @return  正文列表
	 */
	public List<Document> getAllDocumentsByCustomField(String instanceId,String customField);
	
	/**
	 * 修改为 {@link #getDocumentsExcludeCustomField(FormFlowable,String) </code>getDocumentsExcludeCustomField<code>}
	 * 查询整个实例中不是“自定义类别”的正文
	 * @param entity
	 * @param customField 自定义类别
	 * @return  正文列表
	 */
	@Deprecated
	public List<Document> getDocumentsExceptCustomField(FormFlowable entity,String customField);
	
	/**
	 * 查询整个实例中不是“自定义类别”的正文
	 * @param entity
	 * @param customField 自定义类别
	 * @return  正文列表
	 */
	public List<Document> getDocumentsExcludeCustomField(FormFlowable entity,String customField);
	/**
	 * 查询整个实例中不是“自定义类别”的正文
	 * @param instanceId 实例id
	 * @param customField 自定义类别
	 * @return  正文列表
	 */
	public List<Document> getDocumentsExcludeCustomField(String instanceId,String customField);
	
	
	/**
	 * 返回包装好的正文实例
	 * @param entity
	 * @param fileType 文件类型
	 * @return 正文实例
	 */
	public Document createDocument(FormFlowable entity,String fileType);
	
	/**
	 * 返回包装好的正文实例
	 * @param instanceId 实例id
	 * @param fileType 文件类型
	 * @return 正文实例
	 */
	public Document createDocument(String instanceId,String fileType);
	
	/**
	 * 查询正文实例
	 * @param documentId
	 * @return 正文实例
	 */
	public Document getDocument(Long documentId);
	
	/**
	 * 保存正文
	 * @param document taskId是必须传的设置的参数，注意在修改正文时，设置文档id,即：document.setId(documentId);
	 */
	public void saveDocument(Document document);
	
	/**
	 * 保存意见,环节的办理模式为当前环节办理模式,且任务名为当前任务名
	 * @param document
	 * @param entity
	 */
	public void saveDocument(Document document ,FormFlowable entity);
	
	/**
	 * 删除正文
	 * @param documentId
	 */
	public void deleteDocument(Long documentId);
	
	/**
	 * 返回环节办理人是否具有创建正文的权限 
	 * @param taskId
	 * 
	 */
	@Deprecated
	public boolean officialTextCreateRight(Long taskId);
	
	/**
	 * 返回环节办理人是否具有编辑正文的权限 
	 * @param taskId
	 */
	@Deprecated
	public boolean officialTextEditRight(Long taskId);
	
	/**
	 * 返回环节办理人是否具有删除正文的权限 
	 * @param taskId
	 * @return
	 */
	@Deprecated
	public boolean officialTextDeleteRight(Long taskId);
	
	/**
	 * 流程还未启动时创建正文的权限
	 * @param workflowDefinitionName 流程定义名字
	 * @return 有权限返回true，否则返回false
	 */
	@Deprecated
	public boolean officialTextCreateRightNotStarted(String workflowDefinitionCode);

	/**
	 * 流程还未启动时创建正文的权限
	 * @param workflowDefinitionId 流程定义id
	 * @return 有权限返回true，否则返回false
	 */
	@Deprecated
	public boolean officialTextCreateRightNotStarted(Long workflowDefinitionId);
}
