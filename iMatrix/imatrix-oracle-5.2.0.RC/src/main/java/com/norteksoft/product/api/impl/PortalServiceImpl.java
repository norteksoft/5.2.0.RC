package com.norteksoft.product.api.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.portal.base.enumeration.MessageType;
import com.norteksoft.portal.service.MessageInfoManager;
import com.norteksoft.product.api.PortalService;

@Service
@Transactional
public class PortalServiceImpl implements PortalService {

	@Autowired
	private MessageInfoManager messageManager;
	
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param sender 发件人名字
	 * @param senderLoginName 发件人登陆名
	 * @param receiverLoginName 收件人登陆名
	 * @param category 类别
	 * @param content 内容
	 * @param url 弹窗的链接
	 */
	public void addMessage(String systemCode,String sender,String senderLoginName,String receiverLoginName,String category,
			String content, String url) throws Exception{
		messageManager.saveMessage(systemCode,sender, senderLoginName,receiverLoginName,category, content, url,MessageType.SYSTEM_MESSAGE);
	}

}
