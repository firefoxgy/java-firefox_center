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
@ApiModel(value = "第三方用户表")
public class TUserThirdIdVO {
    /**
     * id
     */
    private Long id;
    private Long sid;

    /**
     * 第三方平台标识
     */
    private String loginType;

    private String nickname;
    private String figureurl;

}