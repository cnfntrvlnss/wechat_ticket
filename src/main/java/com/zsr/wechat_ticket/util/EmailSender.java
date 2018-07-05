package com.zsr.wechat_ticket.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

	public static boolean sendQQEmail(String to, String msg) {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");// 连接协议
        properties.put("mail.smtp.host", "smtp.qq.com");// 主机名
        properties.put("mail.smtp.port", 465);// 端口号
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
        properties.put("mail.debug", "true");// 设置是否显示debug信息 true 会在控制台显示相关信息
        // 得到回话对象
        Session session = Session.getInstance(properties);
        // 获取邮件对象
        Message message = new MimeMessage(session);
        try {
            // 设置发件人邮箱地址
            message.setFrom(new InternetAddress("357604901@qq.com"));
            // 设置收件人邮箱地址 
            message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress("zhouqf@inspur.com"),new InternetAddress("zhengshr@inspur.com"),new InternetAddress("543051040@qq.com"),new InternetAddress("916976682@qq.com")});
            //message.setRecipient(Message.RecipientType.TO, new InternetAddress("xxx@qq.com"));//一个收件人
            // 设置邮件标题
            message.setSubject("xmqtest");
            // 设置邮件内容
            message.setText("邮件内容邮件内容邮件内容xmqtest");
            // 得到邮差对象
            Transport transport = session.getTransport();
            // 连接自己的邮箱账户
            transport.connect("357604901@qq.com", "rjinfmljgeifcaaa");// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            return true;
 	
        }catch(Exception e) {
        	e.printStackTrace();
        	return false;
        }
	}
}
