package com.norteksoft.wf.engine.client;

import java.util.Map;

/**
 * 子流程开始前调用
 * @author wurong
 *
 */
public interface OnStartingSubProcess {

	/**
	 * 父实体的id
	 */
	public static final String PARENT_ENTITY_ID = "parentEntityId";
	/**
	 * 子流程文档创建人
	 */
	public static final String SUB_DOCUMENT_CREATOR = "subDocumentCreator";
	/**
	 * 返回子流程对应的实体，实体必须经过持久化
	 * @param param map封装了流程引擎传过来的参数  key为本接口中的常量
	 */
	public FormFlowable getRequiredSubEntity(Map<String,Object> param);
}
