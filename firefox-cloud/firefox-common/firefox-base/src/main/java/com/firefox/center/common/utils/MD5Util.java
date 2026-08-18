package com.firefox.center.common.utils;

import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/5/11 14:53
 */
public class MD5Util {

    //盐，用于混交md5
    public static final String SLAT = "&%www.firefox.cn***&&%%$$#@";

    public static String encrypt(String dataStr) {
        String md5 = "";
        try {
            md5 = DigestUtils.md5DigestAsHex(dataStr.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String encrypt(String dataStr, String salt) {
        return encrypt(dataStr + salt);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(encrypt("1390840873775841282"));
    }

}

