package com.firefox.center.sys.common.mail;

import com.firefox.center.sys.common.util.SendMail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final SendMail sendMail;

    public void sendMessage(String title,String message){
        sendMail.sendSimpleMail(title,message);
    }
}
