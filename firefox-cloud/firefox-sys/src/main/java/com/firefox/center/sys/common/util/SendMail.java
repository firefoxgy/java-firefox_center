package com.firefox.center.sys.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendMail {

    private final JavaMailSender sender;

    public void sendSimpleMail(String title,String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kaiping_sujie@163.com");
        message.setTo("153282687@qq.com");
        message.setSubject(title);
        message.setText(content);
        sender.send(message);
    }
}
