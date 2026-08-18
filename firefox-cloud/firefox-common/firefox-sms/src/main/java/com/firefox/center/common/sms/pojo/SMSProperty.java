package com.firefox.center.common.sms.pojo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/2/18 9:32
 */
@Data
@Builder
@Accessors(chain = true)
public class SMSProperty {

    private String url;
    private String appKey;
    private String appSecret;
    private String module;
    private Integer type;
    private String signName;
    private String templateCode;
    private Long expire;

}
