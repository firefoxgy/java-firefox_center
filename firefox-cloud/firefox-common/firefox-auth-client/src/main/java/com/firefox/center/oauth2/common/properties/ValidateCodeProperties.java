package com.firefox.center.oauth2.common.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 验证码配置
 *
 * @Author: sujie
 * @date 2021/04/4
 */
@Setter
@Getter
public class ValidateCodeProperties {
    /**
     * 设置认证通时不需要验证码的clientId
     */
    private String[] ignoreClientCode = {};
}
