package com.norteksoft.wf.engine.client;


/**
 * 流向流过时将调用该接口
 * @author wurong
 *
 */
public interface OnExecutingTransation {

	/**
	 * 流向流过是被调用
	 * @param dataId 当前正在走流程的实体的id
	 */
	public void execute(Long dataId);
	
}
