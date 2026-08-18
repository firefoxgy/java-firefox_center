package com.firefox.center;

import cn.hutool.crypto.SecureUtil;
import com.firefox.center.common.constrains.CommonConstant;
import com.firefox.center.common.utils.DateUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/3/25 15:24
 */
public class Test {
    public static void main(String[] args) {
//        String appId="xr_02", tenantId="10000", phone="15887253628", ts="1620284330";
//        String signStr="appid"+appId+"tenantid"+tenantId+"phone"+phone+"ts"+ts;
//        String md5Sign= SecureUtil.md5(signStr);
//        System.out.println(md5Sign);
//
//        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
//        String password=encode.encode("253628");
//        System.out.println(password);

        Date date = new Date();
        System.out.println(DateUtil.getDateYMD(date)+" 00:00:00");
        System.out.println(DateUtil.format(DateUtil.getDate(date, 1), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");

        System.out.println(DateUtil.format(DateUtil.getWeekStart(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");
        System.out.println(DateUtil.format(DateUtil.getWeekEnd(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");

        System.out.println(DateUtil.format(DateUtil.getMonthStart(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");
        System.out.println(DateUtil.format(DateUtil.getMonthEnd(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");

        System.out.println(DateUtil.format(DateUtil.getSeasonStart(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");
        System.out.println(DateUtil.format(DateUtil.getSeasonEnd(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");

        System.out.println(DateUtil.format(DateUtil.getYearStart(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");
        System.out.println(DateUtil.format(DateUtil.getYearEnd(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");


        String phone="15100012345";
        String aa=phone.substring(0,3)+"****"+phone.substring(phone.length()- 4);
        System.out.println(aa);
    }
}
