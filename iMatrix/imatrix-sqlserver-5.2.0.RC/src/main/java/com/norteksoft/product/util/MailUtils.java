package com.norteksoft.product.util;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.norteksoft.acs.base.enumeration.MailboxDeploy;
import com.norteksoft.acs.entity.organization.MailDeploy;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;


/**
 * 邮件发送工具
 * 
 */
public final class MailUtils {
	
	private static Log log = LogFactory.getLog(MailUtils.class);
	private MailUtils(){ }
	
	private static MailInfo getMailInfo(String email){
		CompanyManager companyManager = (CompanyManager) ContextUtils.getBean("companyManager");
		MailDeploy mailDeploy=companyManager.getMailDeployByCompanyId();
		User user=ApiFactory.getAcsService().getUserByEmail(email);
		if(user!=null){
			Boolean isAutheticate;
			String protocol="smtp";
			String host;
			Integer port=25;
			String userName;
			String password;
			String from;
			if(MailboxDeploy.INSIDE.equals(user.getMailboxDeploy())){
				isAutheticate=getSmtpAuth(mailDeploy.getSmtpAuthInside());
				if(StringUtils.isNotEmpty(mailDeploy.getTransportProtocolInside())){
					protocol=mailDeploy.getTransportProtocolInside();
				}
				Assert.notNull(mailDeploy.getSmtpHostInside(), "内网配置中的[邮件服务器地址]不能为空  ");
				host=mailDeploy.getSmtpHostInside();
				if(StringUtils.isNotEmpty(mailDeploy.getSmtpPortInside())){
					port=NumberUtils.toInt(mailDeploy.getSmtpPortInside(), 25);
				}
				Assert.notNull(mailDeploy.getHostUserInside(), "内网配置中的[默认服务器端用户名]不能为空  ");
				userName=mailDeploy.getHostUserInside();
				Assert.notNull(mailDeploy.getHostUserPasswordInside(), "内网配置中的[默认服务器用户密码]不能为空  ");
				password=mailDeploy.getHostUserPasswordInside();
				Assert.notNull(mailDeploy.getHostUserFromInside(), "内网配置中的[默认主机地址]不能为空  ");
				from=mailDeploy.getHostUserFromInside();

			}else{
				isAutheticate=getSmtpAuth(mailDeploy.getSmtpAuthExterior());
				if(StringUtils.isNotEmpty(mailDeploy.getTransportProtocolExterior())){
					protocol=mailDeploy.getTransportProtocolExterior();
				}
				Assert.notNull(mailDeploy.getSmtpHostExterior(), "外网配置中的[邮件服务器地址]不能为空  ");
				host=mailDeploy.getSmtpHostExterior();
				if(StringUtils.isNotEmpty(mailDeploy.getSmtpPortExterior())){
					port=NumberUtils.toInt(mailDeploy.getSmtpPortExterior(), 25);
				}
				Assert.notNull(mailDeploy.getHostUserExterior(), "外网配置中的[默认服务器端用户名]不能为空  ");
				userName=mailDeploy.getHostUserExterior();
				Assert.notNull(mailDeploy.getHostUserPasswordExterior(), "外网配置中的[默认服务器用户密码]不能为空  ");
				password=mailDeploy.getHostUserPasswordExterior();
				Assert.notNull(mailDeploy.getHostUserFromExterior(), "外网配置中的[默认主机地址]不能为空  ");
				from=mailDeploy.getHostUserFromExterior();
			}
			return new MailInfo(isAutheticate, protocol, host, port, userName, password, from);
		}
		return null;
	}
	
	private static Boolean getSmtpAuth(String smtpAuth) {
		Boolean isAutheticate;
		if(StringUtils.isEmpty(smtpAuth)){
			isAutheticate=true;
		}else{
			isAutheticate=Boolean.valueOf(smtpAuth);
		}
		return isAutheticate;
	}
	
	public static void sendMail(Collection<String> consignees, String subject, String content) {
		Map<String, String> fileMap = new HashMap<String, String>();
		
		for(String to : consignees){
			try {
				MailInfo info = getMailInfo(to);
				sendMail(info.isAutheticate(), info.getProtocol(), info.getHost(), info.getPort(), 
						info.getUser(), info.getPassword(), info.getFrom(), to, subject, content, fileMap);
			} catch (AddressException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			} catch (MessagingException e) {
				log.debug(PropUtils.getExceptionInfo(e));
			}
		}
	}
	
	public static boolean sendMail(String to, String subject, String content)
			throws AddressException, MessagingException {
		Map<String, String> fileMap = new HashMap<String, String>();
		MailInfo info = getMailInfo(to);
		return sendMail(info.isAutheticate(), info.getProtocol(), info.getHost(), info.getPort(), 
				info.getUser(), info.getPassword(), info.getFrom(), to, subject, content, fileMap);
	}
	
	public static boolean sendMailQuietly(String to, String subject, String content){
		Map<String, String> fileMap = new HashMap<String, String>();
		MailInfo info = getMailInfo(to);
		try {
			return sendMail(info.isAutheticate(), info.getProtocol(), info.getHost(), info.getPort(), 
					info.getUser(), info.getPassword(), info.getFrom(), to, subject, content, fileMap);
		} catch (AddressException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		} catch (MessagingException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
		return false;
	}

	/**
	 * @param isAutheticate
	 *            邮件务器是否验证用户
	 * @param protocol
	 *            协认
	 * @param host
	 *            邮件务器地址
	 * @param port
	 *            端口
	 * @param user
	 *            用户
	 * @param password
	 *            密码
	 * @param from
	 *            邮件发送地址
	 * @param to
	 *            邮件接收地址
	 * @param subject
	 *            标题
	 * @param content
	 *            内容
	 * @param filePathMap
	 *            附件，它是一个“文件名=全路径地址”的映射
	 * @return
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public static boolean sendMail(Boolean isAutheticate, String protocol,
			String host, Integer port, String user, String password,
			String from, String to, String subject, String content,
			Map<String, String> filePathMap) throws AddressException,
			MessagingException {
		boolean bool = false;
		Properties p = new Properties();
		p.put("mail.smtp.auth", isAutheticate.toString());
		p.put("mail.transport.protocol", protocol);
		p.put("mail.smtp.host", host);
		p.put("mail.smtp.port", port);
		// 建立会话
		Session session = Session.getInstance(p);
		// 建立消息
		Message msg = new MimeMessage(session);
		// 设置发件人
		msg.setFrom(new InternetAddress(from));
		// 收件人
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		// 发送日期
		msg.setSentDate(new Date());
		// 主题
		msg.setSubject(subject);
		// 设置邮件内容，作为Multipart对象的一部分
		MimeBodyPart mbp = new MimeBodyPart();
		mbp.setText(content);
		Multipart mulp = new MimeMultipart();
		mulp.addBodyPart(mbp);
		// 文件件名
		String fileName = null;
		// 全路径
		String fileFullPath = null;
		DataSource source = null;
		if (filePathMap != null && filePathMap.size() > 0) {
			Iterator<Entry<String, String>> it = filePathMap.entrySet().iterator();
			while (it.hasNext()) {
				// 为每个附件做为Multipart对象的一部分
				mbp = new MimeBodyPart();
				Map.Entry<String, String> entry =  it.next();
				fileName = entry.getKey();
				fileFullPath = entry.getValue();
				if (fileName == null || fileName.equals("")
						|| fileFullPath == null || fileFullPath.equals("")) {
					continue;
				}
				File f = new File(fileFullPath);
				if (!f.exists()) {
					continue;
				}
				source = new FileDataSource(fileFullPath);
				mbp.setDataHandler(new DataHandler(source));
				mbp.setFileName(fileName);
				mulp.addBodyPart(mbp);
			}
		}
		// 设置信息内容，将Multipart 对象加入信息中
		msg.setContent(mulp);
		// 登陆邮件服务器进行用户验证
		Transport tran = session.getTransport(protocol);
		tran.connect(host, user, password);
		// 发送
		tran.sendMessage(msg, msg.getAllRecipients());
		bool = true;
		return bool;
	}

	/**
	 * @param args
	 * @throws MessagingException
	 * @throws AddressException
	 */
//	public static void main(String[] args) throws AddressException,
//			MessagingException {
//		// 调试使用
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("mail.jar", "D:\\lib\\mail.jar");
//		map.put("1.txt", "D:\\lib\\1.txt");
//		//sendMail(true, "smtp", "smtp.126.com", 25, "username", "password",
//		//		"from@126.com", "to@163.com", "title", "content", map);
//		sendMail("to@gmail.com", "mail test", "是否收到？");
//		System.out.println("ok!");
//
//	}
}

class MailInfo{
	private boolean isAutheticate;
	private String protocol;
	private String host;
	private Integer port;
	private String user;
	private String password;
	private String from;
	public boolean isAutheticate() {
		return isAutheticate;
	}
	public void setAutheticate(boolean isAutheticate) {
		this.isAutheticate = isAutheticate;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public MailInfo(boolean isAutheticate, String protocol, String host,
			Integer port, String user, String password, String from) {
		this.isAutheticate = isAutheticate;
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.from = from;
	}
	
}
