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
public class ThirdDTO {
    private String clientType;
    private String version;
    private String appId;
    private Integer tenantId;
    private String phone;
    private String code;
    private String authType;
    private String externalappid;
    private String thirdid;
    private String thirdUnionId;
    private String nickname;
    private String figureurl;
    private Integer gender;
    private String country;
    private String province;
    private String city;

}