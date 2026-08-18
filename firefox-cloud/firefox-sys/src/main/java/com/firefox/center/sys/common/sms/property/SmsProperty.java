package com.firefox.center.sys.common.sms.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

import static com.firefox.center.sys.common.sms.property.SmsProperty.PREFIX;

@Data
@Configuration
@ConfigurationProperties(prefix = PREFIX)
public class SmsProperty implements Serializable {
    private static final long serialVersionUID = 5243926308290263767L;
    public static final String PREFIX = "firefox.sms";
    private String apiurl = "";           //短信的api地址
    private String appKey = "";           //短信的appkey
    private String appSecret = "";        //短信的appSecret
    private String module = "";           //子系统接入结算系统的token私钥
    private String type = "";             //该参数是token通信协议，可为空
    private String signName = "";         //该参数是请求时间戳，可为空
    private String templateCode = "";     // 该参数是请求方式
    private Integer expire = 300;         // 验证码有效期

}
