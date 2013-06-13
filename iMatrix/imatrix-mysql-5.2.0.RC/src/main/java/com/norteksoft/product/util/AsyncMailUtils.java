package com.norteksoft.product.util;

import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;


/**
 * 邮件发送工具
 * 
 */
public final class AsyncMailUtils {
	private AsyncMailUtils(){
		
	}

	public static void sendMail(String to, String subject, String content){
		Thread thread=new Thread(new MailSender(to, subject, content,ContextUtils.getCompanyId(),ContextUtils.getUserId()),"mailSender");
		thread.start();
	}
	public static void sendMail(Set<String> toEmails, String subject, String content){
		Thread thread=new Thread(new MailSender(toEmails, subject, content,ContextUtils.getCompanyId(),ContextUtils.getUserId()),"mailSender");
		thread.start();
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
	public static void sendMail(Boolean isAutheticate, String protocol,
			String host, Integer port, String user, String password,
			String from, String to, String subject, String content,
			Map<String, String> filePathMap){
		MailSender mailSender=new MailSender(to, subject, content,ContextUtils.getCompanyId(),ContextUtils.getUserId());
		mailSender.setIsAutheticate(isAutheticate);
		mailSender.setProtocol(protocol);
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUser(user);
		mailSender.setPassword(password);
		mailSender.setFrom(from);
		mailSender.setFilePathMap(filePathMap);
		
		Thread thread=new Thread(mailSender);
		thread.start();
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
//		System.out.println("1");
//		sendMail("huhongchun@gmail.com", "mail test", "是否收到？");
//		System.out.println("2");
//		sendMail("huhongchun@gmail.com", "mail test", "是否收到？");
//		System.out.println("ok!");
//
//	}
}
