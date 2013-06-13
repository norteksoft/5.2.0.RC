package com.norteksoft.portal.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.portal.base.enumeration.MessageType;
import com.norteksoft.portal.dao.MessageInfoDao;
import com.norteksoft.portal.entity.Message;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;


@Service
@Transactional
public class MessageInfoManager {
	private Log log = LogFactory.getLog(MessageInfoManager.class);
	@Autowired
	private MessageInfoDao messageDao;
	
	@Autowired
	private UserManager userManager;
	
	/**
	 * 保存
	 * @param message
	 */
	public void saveMessage(Message message){
		this.messageDao.save(message);
	}
	
	/**
	 * 得到
	 * @param id
	 */
	public Message getMessage(Long id){
		Message message = this.messageDao.get(id);
		return message;
	}
	
	/**
	 * 删除
	 * @param id
	 */
	public void deleteMessage(Long id){
		this.messageDao.delete(id);
	}
	
	/**
	 *删除多个
	 * @param ids
	 */
	public int deleteMessage(String ids){
		int number=0;
		if(StringUtils.isNotEmpty(ids)){
			String[] strId =ids.split(",");
			for (String str : strId) {
				deleteMessage(Long.valueOf(str));
				number++;
			}
		}
		return number;
	}
	
	/**
	 * 设置读取状态
	 * @param ids
	 * @param bl
	 * @return
	 */
	public int setMessageState(String ids,boolean bl){
		int number=0;
		if(StringUtils.isNotEmpty(ids)){
			String[] strId =ids.split(",");
			for (String str : strId) {
				Message message = getMessage(Long.valueOf(str));
				message.setVisible(bl);
				saveMessage(message);
				number++;
			}
		}
		return number;
	}
	
	/**
	 * 设置读取状态
	 * @param ids
	 * @param bl
	 * @return
	 */
	public void setMessageState(Long id,boolean bl){
		Message message = getMessage(id);
		message.setVisible(bl);
		saveMessage(message);
	}
	
	/**
	 * 设置读取状态
	 * @param ids
	 * @param bl
	 * @return
	 */
	public void setMessageState(Message message,boolean bl){
		message.setVisible(bl);
		saveMessage(message);
	}
	
	public int deleteMessage(String loginName,Long companyId,MessageType messageType){
		int number=0;
		List<Message> messages=getMessages(loginName, companyId, messageType);
		if(messages!=null&&!messages.isEmpty()){
			for (Message message : messages) {
				deleteMessage(message);
				number++;
			}
		}
		return number;
	}
	
	public void deleteMessage(Message message){
		this.messageDao.delete(message);
	}
	
	/**
	 * 增加系统信息
	 * @param systemCode  //系统code
	 * @param name  //发起人真实名字
	 * @param loginName//发起人登陆名
	 * @param receiverLoginName//收件人登陆名
	 * @param type//类型
	 * @param message//信息
	 * @param url//打开任务的地址"/public/index.htm"
	 */
	public void saveMessage(String systemCode,String name,String loginName,String receiverLoginName,String type,String info,String url,MessageType messageType){
		Message message = new Message();
		Assert.notNull(ContextUtils.getCompanyId(), "companyId不能为null");
		message.setCompanyId(ContextUtils.getCompanyId());
		message.setSender(name);
		message.setSenderLoginName(loginName);
		User user=userManager.getUserByLoginName(receiverLoginName);
		if(user==null){
			log.debug("与receiverLoginName对应的用户不能为null");
			throw new RuntimeException("与receiverLoginName对应的用户不能为null");
		}
		message.setReceiver(user.getName());
		message.setReceiverLoginName(user.getLoginName());
		message.setCreatedTime(new Date());
		message.setCategory(type);
		message.setMessageType(messageType);
		if(StringUtils.isNotEmpty(url))
		message.setUrl(url.trim());
		message.setContent(info);
		message.setSystemCode(systemCode);
		this.messageDao.save(message);
	}
	
	/**
	 * 只能是在线消息用(需要打开页面的时候用(swing中用))
	 * @param systemCode
	 * @param name
	 * @param loginName
	 * @param receiverLoginName
	 * @param type
	 * @param info
	 * @param url
	 * @param flag
	 */
	public void saveMessageToPortal(String systemCode,String name,String loginName,String receiverLoginName,String type,String info,String url,MessageType messageType){
		User user=userManager.getUserByLoginName(receiverLoginName);
		if(user!=null){
			Message message = new Message();
			message.setCompanyId(ContextUtils.getCompanyId());
			message.setSenderLoginName(loginName);
			message.setReceiver(user.getName());
			message.setReceiverLoginName(user.getLoginName());
			message.setCreatedTime(new Date());
			message.setMessageType(messageType);
			if(messageType==MessageType.ONLINE_MESSAGE){
				message.setSender(name);
				message.setCategory(type);
			}else{
				message.setSender("系统");
				message.setCategory("系统消息");
			}
			message.setContent(info);
			message.setSystemCode(systemCode);
			this.messageDao.save(message);
			if(StringUtils.isNotEmpty(url)){
				if(url.trim().contains("isOpen=")){
					message.setUrl(url.trim()+message.getId());
				}else{
					message.setUrl(url.trim()+message.getId()+"&isOpen=true");
				}
			}
		}
	}
	
	/**
	 * 根据登陆名得到信息
	 * @param loginName
	 * @param companyId
	 * @param bl  true为没有看过的，false为查看过的
	 * @return
	 */
	public List<Message> getMessages(String receiverLoginName,Long companyId,boolean read){
		return this.messageDao.find("from Message m where m.receiverLoginName=? and m.companyId=? and m.visible=? order by m.createdTime desc", receiverLoginName,companyId,read);
	}
	
	public Page<Message> getMessages(Page<Message> page,String receiverLoginName,Long companyId,boolean read){
		return this.messageDao.findPage(page,"from Message m where m.receiverLoginName=? and m.companyId=? and m.visible=? order by m.createdTime desc", receiverLoginName,companyId,read);
	}
	
	public List<Message> getMessageList(String receiverLoginName,Long companyId,boolean read){
		return this.messageDao.find("from Message m where m.receiverLoginName=? and m.companyId=? and m.visible=? order by m.createdTime desc", receiverLoginName,companyId,read);
	}
	
	public List<Message> getMessages(String receiverLoginName,Long companyId,MessageType MessageType){
		return this.messageDao.find("from Message m where m.receiverLoginName=? and m.companyId=? and m.messageType=? order by m.createdTime desc", receiverLoginName,companyId,MessageType);
	}
	
	public Page<Message> getMessages(Page<Message> page,String receiverLoginName,Long companyId,MessageType messageType){
		return this.messageDao.findPage(page,"from Message m where m.receiverLoginName=? and m.companyId=? and m.messageType=? order by m.createdTime desc", receiverLoginName,companyId,messageType);
	}
	
}
