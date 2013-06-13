package com.norteksoft.wf.engine.client;

import java.util.Set;



/**
 * 选择具体的办理人
 */
public interface SingleTransactorSelector {

	/**
	 * 过滤传过来的办理人
	 * @param dataId 业务实体id
	 * @param transactors 可供选择的办理人
	 * @param moreTransactor 是否为多人办理
	 * @return 具体办理人的登录名
	 */
	public Set<String> filter(Long dataId,Set<String> transactors,boolean moreTransactor);
	
}
