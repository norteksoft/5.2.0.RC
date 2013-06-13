package com.norteksoft.portal.web.index;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import com.norteksoft.acs.base.web.struts2.Struts2Utils;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.portal.base.enumeration.MessageType;
import com.norteksoft.portal.entity.BaseSetting;
import com.norteksoft.portal.entity.Message;
import com.norteksoft.portal.entity.Widget;
import com.norteksoft.portal.service.BaseSettingManager;
import com.norteksoft.portal.service.IndexManager;
import com.norteksoft.portal.service.MessageInfoManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.CrudActionSupport;
/**
 * 个人消息管理
 * @author zzl
 *
 */

@Namespace("/index")
@ParentPackage("default")
@Results({@Result(name=CrudActionSupport.RELOAD,location="my-message",type="redirectAction")})
public class MyMessageAction extends CrudActionSupport<Message>{
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String ids;
	
	private Page<Message> pages = new Page<Message>(0, true);
	
	private List<Message> messages= new ArrayList<Message>();

	private Message message;
	
	private String messageType="SYSTEM_MESSAGE";
	
	private String userNames;
	
	private String loginNames;
	
	private Boolean isOpen=false;//是否是open出来的页面
	
	private Boolean bl;
	
	private Long messageId;
	
	@Autowired
	private MessageInfoManager messageManager;
	
	@Autowired
	private AcsUtils acsUtils;
	
	@Autowired
	private BaseSettingManager baseSettingManager;
	@Autowired
	private IndexManager indexManager;
	
	/**
	 * 删除
	 */
	@Action("my-message-delete")
	@Override
	public String delete() throws Exception {
		int num = messageManager.deleteMessage(ids);
		addActionMessage("<font class=\"onSuccess\"><nobr>已成功删除"+num+"条数据!</nobr></font>");
		ApiFactory.getBussinessLogService().log("个人消息管理", "删除个人消息", ContextUtils.getSystemId("portal"));
		return "my-message";
	}
	
	/**
	 * 标识为读取状态
	 */
	@Action("my-message-stateAll")
	public String stateAll() throws Exception {
		int num = messageManager.setMessageState(ids,bl);
		addActionMessage("<font class=\"onSuccess\"><nobr>已成功操作"+num+"条数据!</nobr></font>");
		ApiFactory.getBussinessLogService().log("个人消息管理", "将消息标识为已读或未读状态", ContextUtils.getSystemId("portal"));
		return "my-message";
	}
	
	/**
	 * 清空
	 * @return
	 * @throws Exception
	 */
	@Action("my-message-deleteAll")
	public String deleteAll() throws Exception {
		int num = 0;
		if(messageType.equals("SYSTEM_MESSAGE")){
			 num = messageManager.deleteMessage(ContextUtils.getLoginName() , ContextUtils.getCompanyId(), MessageType.SYSTEM_MESSAGE);
		}else if(messageType.equals("ONLINE_MESSAGE")){
			 num = messageManager.deleteMessage(ContextUtils.getLoginName() , ContextUtils.getCompanyId(), MessageType.ONLINE_MESSAGE);
		}
		addActionMessage("<font class=\"onSuccess\"><nobr>已成功删除"+num+"条数据!</nobr></font>");
		ApiFactory.getBussinessLogService().log("个人消息管理", "清空个人消息", ContextUtils.getSystemId("portal"));
		return "my-message";
	}

	/**
	 * 新建页面
	 */
	@Action("my-message-input")
	@Override
	public String input() throws Exception {
		return "my-message-input";
	}

	/**
	 * 主入口
	 */
	@Override
	public String list() throws Exception {
		if(pages.getPageSize()>1){
			if(messageType.equals("SYSTEM_MESSAGE")){
				messageManager.getMessages(pages,ContextUtils.getLoginName() , ContextUtils.getCompanyId(), MessageType.SYSTEM_MESSAGE);//系统
			}else if(messageType.equals("ONLINE_MESSAGE")){
				messageManager.getMessages(pages,ContextUtils.getLoginName() , ContextUtils.getCompanyId(), MessageType.ONLINE_MESSAGE);//系统
			}
			this.renderText(PageUtils.pageToJson(pages));
			ApiFactory.getBussinessLogService().log("个人消息管理", "查看个人消息列表", ContextUtils.getSystemId("portal"));
			return null;
		}
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			message  = new Message();
		}else{
			message= messageManager.getMessage(id);
		}
		
	}

	/**
	 * 保存
	 */
	@Action("my-message-save")
	@Override
	public String save() throws Exception {
		message.setCreatedTime(new Date());
		if(StringUtils.isNotEmpty(loginNames)){ 
			if("ALLCOMPANYID".equals(loginNames)){
				List<User> users = acsUtils.getUsersByCompany(ContextUtils.getCompanyId());
				for(User user : users){
					messageManager.saveMessageToPortal("portal", ContextUtils.getUserName(),ContextUtils.getLoginName(), user.getLoginName(), "在线消息", message.getContent(), "/index/my-message-view.htm?isOpen=true&id=",MessageType.valueOf(messageType));
				}
			}else{
				String[] logins=loginNames.split(",");
				for (int i = 0; i < logins.length; i++) {
					messageManager.saveMessageToPortal("portal", ContextUtils.getUserName(),ContextUtils.getLoginName(), logins[i], "在线消息", message.getContent(), "/index/my-message-view.htm?isOpen=true&id=",MessageType.valueOf(messageType));
				}
			}
		}
		ApiFactory.getBussinessLogService().log("个人消息管理", "保存个人消息", ContextUtils.getSystemId("portal"));
		this.renderText("ok-"+messageType);
		return null;
	}
	
	/**
	 * 取信息
	 * @return
	 */
	@Action("my-message-getInfor")
	public String getInfor()throws Exception{
		BaseSetting baseSetting = baseSettingManager.getBaseSettingByLonginName();
		if(baseSetting==null || baseSetting.getShowRows()==null){
			baseSetting = new BaseSetting();
			baseSetting.setShowRows(15);
		}
		Page<Message> messagePage = new Page<Message>(baseSetting.getShowRows(), true);
		messagePage=messageManager.getMessages(messagePage,ContextUtils.getLoginName(), ContextUtils.getCompanyId(),true);
		messages=messagePage.getResult();
		String callback=Struts2Utils.getParameter("callback");
		if(messages!=null&&!messages.isEmpty()){
			StringBuffer bu = new StringBuffer();
			bu.append("<div style='font-size: 12px;padding:6px 6px  2px 6px;'>");
			bu.append("<table style='width: 100%;height:100%;'>");
			SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm");
			for (Message mess : messages) {
				String title="<td class='remassage-title' >";
				bu.append("<tr id='mess"+mess.getId()+"'>");
				if(mess.getMessageType()!=null&&mess.getMessageType().equals(MessageType.SYSTEM_MESSAGE)){//为系统消息
					title="<td class='remassage-title' >";
				}else if(mess.getMessageType()==null||mess.getMessageType().equals(MessageType.ONLINE_MESSAGE)){//为在线个人消息
					title="<td class='remassage-title-p' >";
				}
				   bu.append(title);
				   if(mess.getMessageType()!=null&&mess.getMessageType().equals(MessageType.SYSTEM_MESSAGE)&& !mess.getCategory().equals("系统消息")){//为系统消息
					   String url=mess.getUrl();
						if(StringUtils.isNotEmpty(url)){//是否有url
							String werRoot = SystemUrls.getSystemUrl(mess.getSystemCode());
							url=werRoot+url;
							if(url.indexOf("/message-task.htm")>=0){//如果是待办事宜任务
								Widget widget = indexManager.getWidgetByCode("task");//获得待办事宜小窗体id，用于更新小窗体
								if(widget!=null){
									bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='setMessageState("+mess.getId()+");taskMessageOpen(\\\""+url.trim()+"\\\","+widget.getId()+");'>");
								}else{
									bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='setMessageState("+mess.getId()+");messageOpen(\\\""+url.trim()+"\\\");'>");
								}
							}else{
								bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='setMessageState("+mess.getId()+");messageOpen(\\\""+url.trim()+"\\\");'>");
							}
						}else{
							bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='setMessageState("+mess.getId()+")'>");
						}
					  
					}else if(mess.getCategory().equals("系统消息")&&mess.getMessageType().equals(MessageType.SYSTEM_MESSAGE)){
						bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='openMessageInput("+mess.getId()+")'>");
					}else if(mess.getMessageType()==null||mess.getMessageType().equals(MessageType.ONLINE_MESSAGE)){//为在线个人消息
						 bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='openMessageInput("+mess.getId()+")'>");
					}
						   String str=mess.getContent();
							if(StringUtils.isEmpty(str)){
								str="空的消息！";
							}else if(StringUtils.isNotEmpty(str)&&str.length()>60){
								str=str.replace("\r", "\\n").replace("\n", "\\n").replace("\t", "\\n").replace("\r\n", "\\n").replace("\n", "\\n").replace("\"", "‘").replace("\\\\", "\\\\\\\\");
								str=StringUtils.substring(str, 0, 59)+"...";
							}else{
								str=str.replace("\r", "\\n").replace("\n", "\\n").replace("\t", "\\n").replace("\r\n", "\\n").replace("\n", "\\n").replace("\"", "‘").replace("\\\\", "\\\\\\\\");
								
							}
							bu.append(str);
						bu.append("</a>");
					bu.append("</td>");
					bu.append("<td class='remassage-name' >");
						bu.append(mess.getSender()+"<br>"+format.format(mess.getCreatedTime()));
					bu.append("</td>");
				bu.append("</tr>");
			}
			bu.append("</table>");
			bu.append("</div>");
			this.renderText(callback+"({msg:\""+bu.toString()+"\"})");
			return null;
		}else{
			this.renderText(callback+"({msg:\"error\"})");
			return null;
		}
	}
	
	public void prepareView() throws Exception{
		prepareModel();
	}
	
	/**
	 * 查看
	 * @return
	 * @throws Exception
	 */
	@Action("my-message-view")
	public String view()throws Exception{
		messageManager.setMessageState(message,false);
		return "my-message-view";
	}

	public void prepareSetState() throws Exception{
		prepareModel();
	}
	
	/**
	 * 设置查看状态
	 * @return
	 * @throws Exception
	 */
	@Action("my-message-setState")
	public String setState()throws Exception{
		messageManager.setMessageState(message,false);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'ok'})");
		return null;
	}
	@Action("my-message-error")
	public String messageError()throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(readScriptTemplet());
		return null;
	}
	
	private String readScriptTemplet() throws Exception{
		String resourceCtx=PropUtils.getProp("host.resources");
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("resourceCtx",resourceCtx);
		root.put("errorInfo",Struts2Utils.getParameter("errorInfo"));
		String result =TagUtil.getContent(root, "message-error.ftl");
		return result;
	}
	
	
	public Message getModel() {
		return message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Page<Message> getPages() {
		return pages;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getLoginNames() {
		return loginNames;
	}

	public void setLoginNames(String loginNames) {
		this.loginNames = loginNames;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}
	public List<Message> getMessages() {
		return messages;
	}

	public Boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}

	public Boolean getBl() {
		return bl;
	}

	public void setBl(Boolean bl) {
		this.bl = bl;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}
