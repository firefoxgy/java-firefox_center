package com.firefox.center;

import cn.hutool.crypto.SecureUtil;
import com.firefox.center.common.utils.DateUtil;
import com.google.common.collect.Lists;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/3/25 15:24
 */
public class Test {
    public static void main(String[] args) {
//        String appId="kpc3nsde893fkssvjt9k", tenantId="10000", phone="15887253628", ts="1621913760";
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
//
//        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
//        String password=encode.encode("123");
//        System.out.println(password);\

        String start="202002";
        List<String> list=getMonth(start);
        list.forEach(System.out::println);
    }

    protected static List<String> getMonth(String start){
        String pattern="yyyyMM";
        List<String> list = Lists.newArrayList();
        list.add(start);
        Date startDate=DateUtil.add(DateUtil.format(start, pattern), Calendar.MONTH, 1);
        Date now = new Date();
        while(!isSameYM(startDate, now)){
            list.add(DateUtil.formatDate(startDate, pattern));
            startDate=DateUtil.add(startDate, Calendar.MONTH, 1);
        }
        list.add(DateUtil.formatDate(startDate, pattern));
        return list;
    }

    public static boolean isSameYM(Date date1, Date date2) {
        try {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
            boolean isSameMonth = cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
            boolean isSameDate = isSameYear && isSameMonth;
            return isSameDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
