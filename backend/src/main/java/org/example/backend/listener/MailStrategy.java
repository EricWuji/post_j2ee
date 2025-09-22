package org.example.backend.listener;

import org.springframework.mail.SimpleMailMessage;

public interface MailStrategy {
    SimpleMailMessage createMailMessage(String from, Integer code, String to);
}
