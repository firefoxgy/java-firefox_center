package com.firefox.center.credit.feign.pojo;

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
public class FeignTUserInfo {

    private static final long serialVersionUID = -5886012896705137070L;
    private Long id;
    private String appId;
    private Integer tenantId;
    private Long uid;
    private String type;
    private String username;
    private String nickname;
    private String headerImg;
    private Integer gender;

}