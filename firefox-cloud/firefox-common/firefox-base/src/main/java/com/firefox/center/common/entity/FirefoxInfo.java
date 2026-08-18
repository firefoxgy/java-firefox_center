package com.firefox.center.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: ZJL
 * @Description: 一般业务超类
 * @Date: 2020/06/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
public class FirefoxInfo {

    private String clientType;
    private String version;
    private String appId;
    private Integer tenantId;
    private long uid;
    private long sid;
    private String uType;
    private long cid;

}
