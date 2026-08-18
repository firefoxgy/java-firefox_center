package com.firefox.center.common.constants;

import org.springframework.beans.factory.annotation.Value;

/**
 * Description: 用戶通用常量
 *
 * @author sujie
 * @since JDK 1.8
 * date: 2020/7/14 14:50
 */
public class UserCommonData {

    /**
     * 系統初始密码
     */
    @Value("${base.system.initial-password}")
    public static String INITIAL_PASSWORD = "ryomastar";

    @Value("${my.spring.oauth2.client-secret}")
    public static String CLIENT_SECRET = "romastar-admin-secret";
}
