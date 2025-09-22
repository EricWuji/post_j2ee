package org.example.backend.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
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

    @Resource
    Map<String, MailStrategy> strategyMap;

    @RabbitHandler
    public void sendMailMessage(Map<String, Object> message) {
        String email = message.get("email").toString();
        Integer code = (Integer) message.get("code");
        String type = message.get("type").toString();
        MailStrategy mailStrategy = strategyMap.get(type);
        if (mailStrategy != null) {
            SimpleMailMessage mailMessage = mailStrategy.createMailMessage(username, code, email);
            sender.send(mailMessage);
        }
    }
}
