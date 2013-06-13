package com.norteksoft.wf.engine.client;


/**
 * 实体删除接口
 */
public interface FormFlowableDeleteInterface {
	/**
	 * 工作流调用，用来删除对应的数据
	 * 该接口的实现者应该完成自己数据的删除操作，包括级联数据
	 * @param dataId 当前正在走流程的实体id
	 */
	public void deleteFormFlowable(Long dataId);
	
}
