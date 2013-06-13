package com.norteksoft.product.api;

public interface PortalService {
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param name 发件人名子
	 * @param loginName 发件人登陆名
	 * @param receiverLoginName 收件人登陆名
	 * @param type 类型
	 * @param info 内容
	 * @param url 弹窗的链接
	 * @throws Exception 
	 */
	public void addMessage(String systemCode,String name,String loginName,String receiverLoginName,String type,String info,String url) throws Exception;
}
