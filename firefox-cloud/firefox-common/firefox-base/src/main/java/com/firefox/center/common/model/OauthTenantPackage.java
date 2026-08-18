package com.firefox.center.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.firefox.center.common.model.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @program: java-firefox_center
 * @description: 租户购买套餐信息
 * @author: yungeng
 * @created: 2021/07/30 15:27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oauth_tenant_package")
public class OauthTenantPackage extends Entity {
    private static final long serialVersionUID = -8185413579135897886L;
    private Long id;
    private Long tenantId;
    private Long packageId;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Integer userLimit;
    private String appid;
    private Integer centerId;
    private String service;
}
