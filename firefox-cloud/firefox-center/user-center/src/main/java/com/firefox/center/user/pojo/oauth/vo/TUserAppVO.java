package com.firefox.center.user.pojo.oauth.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
@ApiModel(value = "App端用户表")
public class TUserAppVO {
    private Long sid;
    private Long uid;
    private String openId;
    private String sids;
    private String username;
    private String phone;
    private String email;
    private String nickname;
    private String headerImg;
    private Integer gender;
    private Integer status;
    private String appId;
    private Integer tenantId;

}