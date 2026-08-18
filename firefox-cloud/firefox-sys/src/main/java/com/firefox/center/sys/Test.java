package com.firefox.center.sys;

import com.firefox.center.sys.common.util.PasswordUtil;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/6/7 11:06
 */
public class Test {
    public static void main(String[] args) {
        String username="test123", password="a0CYBV1NZBsPwkmx", salt="JrFj2Bk1";
        System.out.println(PasswordUtil.encrypt(username, password, salt));
    }
}
