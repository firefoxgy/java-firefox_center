package com.firefox.center.user.pojo.user.dto;

import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "三方登录信息表")
public class ThirdDTO {
    private String appId;
    private Integer tenantId;
    private String phone;
    private String code;
    private String authType;
    private String thirdid;
    private String nickname;
    private String figureurl;
    private String gender;

}