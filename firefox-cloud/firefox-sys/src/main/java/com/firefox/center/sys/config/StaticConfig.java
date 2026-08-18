package com.firefox.center.sys.config;

import com.firefox.center.sys.common.util.DySmsHelper;
import com.firefox.center.sys.core.message.handle.impl.EmailSendMsgHandle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 设置静态参数初始化
 */
@Configuration
public class StaticConfig {

    //@Value("${firefox.sms.accessKeyId}")
    private String accessKeyId="";

    //@Value("${firefox.sms.accessKeySecret}")
    private String accessKeySecret="";

    //@Value(value = "${spring.mail.username}")
    private String emailFrom="";


    @Bean
    public void initStatic() {
        DySmsHelper.setAccessKeyId(accessKeyId);
        DySmsHelper.setAccessKeySecret(accessKeySecret);
        EmailSendMsgHandle.setEmailFrom(emailFrom);
    }
}
