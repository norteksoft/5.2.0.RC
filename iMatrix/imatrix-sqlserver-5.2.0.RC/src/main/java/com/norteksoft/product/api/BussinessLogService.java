package com.norteksoft.product.api;

public interface BussinessLogService {
	
	/**
	 * 记录日志
	 * @param operationType 日志操作类型
	 * @param message       日志信息
	 */
	void log(String operationType, String message);

	/**
	 * 记录日志
	 * @param operationType  日志操作类型
	 * @param message        日志信息
	 * @param systemId       系统ID
	 */
	void log(String operationType, String message, Long systemId);
	
	/**
	 * 
	 * @param operator       日志操作人
	 * @param operationType  日志操作类型
	 * @param message        日志信息
	 */
	void log(String operator, String operationType, String message);

	/**
	 * 
	 * @param operatorId     日志操作人ID
	 * @param operationType  日志操作类型
	 * @param message        日志信息
	 */
	void log(Long operatorId, String operationType, String message);
}
