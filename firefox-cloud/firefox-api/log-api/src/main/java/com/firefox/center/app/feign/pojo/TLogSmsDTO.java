package com.firefox.center.app.feign.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.util.Date;

/**
 * 用户实体
 * @Author：sujie
 * @Date：2020/07/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TLogSmsDTO {

    private String id;
    private String appId;
    private Integer tenantId;
    private String phone;
    private String code;
    private String smsTemplate;
    private Long expire;
    private Integer status;
    private Date createTime;

}