package org.example.backend.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {
    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMailMessage(Map<String, Object> message) {
        String email = message.get("email").toString();
        Integer code = (Integer) message.get("code");
        String type = message.get("type").toString();
        SimpleMailMessage mailMessage = switch (type) {
            case "register" -> getSimpleMailMessage("注册验证码", "您的注册验证码是：" + code + "，该验证码5分钟内有效，请勿泄露于他人！", email);
            case "resetPassword" -> getSimpleMailMessage("重置密码验证码", "您的重置密码验证码是：" + code + "，该验证码5分钟内有效，请勿泄露于他人！", email);
            default -> null;
        };
        if (mailMessage != null) {
            sender.send(mailMessage);
        }
    }

    private SimpleMailMessage getSimpleMailMessage(String title, String content, String to) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(content);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setFrom(username);
        return simpleMailMessage;
    }

}
