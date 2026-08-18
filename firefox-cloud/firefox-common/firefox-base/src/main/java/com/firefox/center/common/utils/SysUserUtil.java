package com.firefox.center.common.utils;

import cn.hutool.core.bean.BeanUtil;
import com.firefox.center.common.model.TUserAdmin;
import org.springframework.security.core.Authentication;

import com.firefox.center.common.model.LoginAdminUser;

/**
 * 获取用户信息
 *
 * @Author: sujie
 */
public class SysUserUtil {
    private SysUserUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static LoginAdminUser getLoginAppUser(TUserAdmin user) {
        LoginAdminUser login = new LoginAdminUser();
        BeanUtil.copyProperties(user, login);
        return login;
    }

    /**
     * 获取登陆的用户名
     */
    public static String getUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof TUserAdmin) {
            username = ((TUserAdmin) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }
        return username;
    }
}
