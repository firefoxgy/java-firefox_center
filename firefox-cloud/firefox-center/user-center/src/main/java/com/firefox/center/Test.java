package com.firefox.center;

import cn.hutool.crypto.SecureUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/3/25 15:24
 */
public class Test {

    public static void main(String[] args) {
//        String appId="kpc3nsde893fkssvjt9k", tenantId="10000", phone="15887253628", ts="1626946817";
//        String signStr="appid"+appId+"tenantid"+tenantId+"phone"+phone+"ts"+ts+"keykpsms";
//        System.out.println(signStr);
//        String md5Sign= SecureUtil.md5(signStr);
//        System.out.println(md5Sign);
//
//        String appId2="kpc3nsde893fkssvjt9k", tenantId2="10000", mail2="153282687@qq.com", ts2="1621996223";
//        String signStr2="appid"+appId2+"tenantid"+tenantId2+"mail"+mail2+"ts"+ts2+"keykpmail";
//        System.out.println(signStr2);
//        String md5Sign2= SecureUtil.md5(signStr2);
//        System.out.println(md5Sign2);

        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        String password=encode.encode("123");
        System.out.println(password);


    }
}
