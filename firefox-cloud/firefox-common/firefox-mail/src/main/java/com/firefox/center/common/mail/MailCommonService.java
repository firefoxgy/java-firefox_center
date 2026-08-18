package com.firefox.center.common.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/5/7 15:57
 */
@Component
@RequiredArgsConstructor
public class MailCommonService {

    private final JavaMailSender sender;

    public void sendSimpleMail(String mailTo, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kaiping_sujie@163.com");
        message.setTo(mailTo);
        message.setSubject(title);
        message.setText(content);
        sender.send(message);
    }

}
