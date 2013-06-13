package com.norteksoft.wf.engine.client;



/**
 * 开始子流程前，将首先调用该接口
 */
public interface BeforeStartSubProcess {

	/**
	 * 将要进入子流程前，将首先执行该方法
	 * @param dataId 当前正在走的表单的id，即父流程实体的id
	 * @return 是否进入子流程，如果是返回true ，将进入子流程；否则，跳过执行子流程 ，继续向下执行
	 */
	public boolean isIntoSubProcess(Long dataId);
	
}
