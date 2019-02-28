package com.hrtx.global;

import com.hrtx.dto.Parameter;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class SendMailUtils {
//	private static String hostName="smtp.yeah.net";
//	private static int smtpPort=25;
//	private static String authentication_email="wytest1@yeah.net";
//	private static String authentication_pwd="010101";
//	private static String from_email="wytest1@yeah.net";
//	private static String from_name="E购通专用邮箱";
	private static String hostName=SystemParam.get("mail_server");
	private static int smtpPort=NumberUtils.toInt(SystemParam.get("mail_host"),25);
	private static String authentication_email=SystemParam.get("mail_email");
	private static String authentication_pwd=SystemParam.get("mail_pwd");
	private static String from_email=SystemParam.get("mail_send_email");
	private static String from_name=SystemParam.get("mail_send_name");

	private static final Logger log = LoggerFactory.getLogger(SendMailUtils.class);
	private static ExecutorService executor;

	static {
		executor = Executors.newFixedThreadPool(50);
	}
	
	/**
	 * 功能:发送邮件
	 * 
	 * @param mialTitle
	 *            邮件主题
	 * @param mailContent
	 *            邮件内容
	 * @param receiver
	 *            接收方
	 * @param attachList
	 *            邮件附件
	 */
	public static void sendAttachmentMail(String mialTitle, String mailContent, Parameter receiver, List<Parameter> attachList) {
		HtmlEmail email = new HtmlEmail();// 可以发送html类型的邮件
		email.setHostName(hostName);// 指定要使用的邮件服务器
		email.setSmtpPort(smtpPort);//设置SMTP 端口
//		log.info(String.format("发送方邮箱[%s]，用户名[%s]",XMLconfig.getConfig().getString("mail.sendMail.email"),XMLconfig.getConfig().getString("mail.sendMail.pwd")));
		email.setAuthentication(authentication_email,authentication_pwd);//设置邮件的登录用户名和密码
		
		email.setCharset("UTF-8");
		email.setSubject(mialTitle);// 设置主题
		//email.setSSL(true);//设置SMTP 安全：SSL
		try {
//			log.info(String.format("发送方邮箱[%s]，用户名[%s]",XMLconfig.getConfig().getString("mail.sendMail.email"),XMLconfig.getConfig().getString("mail.sendMail.name")));
			email.setFrom(from_email,from_name);//设置发送者和名字
			log.info(String.format("接受方邮箱[%s]，用户名[%s]，标题[%s]",receiver.getKey(),receiver.getValue(),mialTitle));
			email.addTo(receiver.getKey(),receiver.getValue());//设置接收者和名字
			email.setHtmlMsg(mailContent);// 可以发送html
			
			//发送附件
			for (int i=0;attachList!=null&&i<attachList.size();i++) {
				Parameter bean=attachList.get(i);
				EmailAttachment attachment = new EmailAttachment();
				attachment.setPath(bean.getKey());
				attachment.setName(MimeUtility.encodeText(bean.getValue()));
				attachment.setDisposition(EmailAttachment.ATTACHMENT);
//				attachment.setDescription("Picture of John");
				email.attach(attachment);
			}
			
			email.send();
		} catch (EmailException e) {
			log.error(String.format("邮件[%s]发送错误",mialTitle),e);
		} catch (UnsupportedEncodingException e) {
			log.error(String.format("邮件[%s]发送附件的文件名称编码错误",mialTitle),e);
		}
		log.error(String.format("邮件[%s]发送结束",mialTitle));
	}
	
	/**
	 * 功能:发送邮件(线程池方式) 
	 * 
	 * @param mialTitle
	 *            邮件主题
	 * @param mailContent
	 *            邮件内容
	 * @param receiver
	 *            接收方
	 * @param attachList
	 *            邮件附件
	 */
	public static void sendEmail(final String mialTitle,final String mailContent,final Parameter receiver,final List<Parameter> attachList) {
		executor.execute(new Runnable() {
			public void run() {
				sendAttachmentMail(mialTitle, mailContent, receiver, attachList);
			}
		});
	}

	public static void main(String[] args) {
		String[] vs={"zhouyq@egt365.com"};
		for(String v:vs){
			log.info(v+"\t"+v.substring(0,v.indexOf("@")));
			Parameter receiver=new Parameter(v,v.substring(0,v.indexOf("@")));
			List<Parameter> list = new ArrayList<Parameter>();
			list.add(new Parameter("D:\\test\\edit_img\\CM8052211432001.jpeg","test.jpeg"));
//			SendMail.sendEmail("测试标题","邮件正文", receiver, list);
            List contexts = new ArrayList();
            contexts.add(new Object[]{"a","b","c"});
            contexts.add(new Object[]{"a","b","c"});
            contexts.add(new Object[]{"a","b","c"});
			SendMailUtils.sendAttachmentMail("测试标题",EmailUtils.baseHtml(EmailUtils.tableHtml("zyq", contexts)), receiver, list);
		}
	}
	
	
}