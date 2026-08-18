package com.firefox.center.gateway;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @program: java-firefox_center
 * @description: sadf
 * @author: yungeng
 * @created: 2024/03/06 09:01
 */
public class PasswdEncryptTest {

    public static void main(String[] args) {

        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        String password=encode.encode("tt@SDasdf123");

        System.out.println(password);
    }
}
