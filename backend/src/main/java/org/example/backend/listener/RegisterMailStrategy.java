package org.example.backend.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component("register")
public class RegisterMailStrategy implements MailStrategy {

    @Value("${spring.mail.username}")
    String username;

    @Override
    public SimpleMailMessage createMailMessage(String from, Integer code, String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("注册验证码");
        message.setText("您的注册验证码是：" + code + "，该验证码5分钟内有效，请勿泄露于他人！");
        message.setTo(to);
        message.setFrom(from);
        message.setFrom(username);
        return message;
    }
}
