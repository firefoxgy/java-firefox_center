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
public class TLogMailDTO {
    private String id;
    private String appId;
    private Integer tenantId;
    private String mail;
    private String title;
    private String content;
    private Integer status;
    private Date createTime;

}