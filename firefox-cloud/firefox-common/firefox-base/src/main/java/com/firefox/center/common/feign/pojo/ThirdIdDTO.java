package com.firefox.center.common.feign.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 第三方登录实体
 * @Author：sujie
 * @Date：2020/07/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ThirdIdDTO {
    private String appId;
    private int tenantId;
    private String loginType;
    private String thirdid;

}