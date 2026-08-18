package com.firefox.center.sys.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ToolUtil {

    private static char[] numbersAndLetters = null;
    private static Random randGen = null;
    private static Object initLock = new Object();

    /*
     * 生成id
     */
    public static String getId() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(currentTime) + randomString(6);
    }

    /*
     * 生成id
     */
    public static String getId(int num) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(currentTime) + randomNum(num);
    }

    /*
     * 生成id
     */
    public static String getNumId() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(currentTime) + randomNum(6);
    }

    /*
     * 生成一个随机码
     */
    public static String randomNumString(int length) {
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            synchronized (initLock) {
                if (randGen == null) {
                    randGen = new Random();
                    numbersAndLetters = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
                }
            }
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)];
        }
        return new String(randBuffer);
    }

    public static String randomString(int length) {
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            synchronized (initLock) {
                if (randGen == null) {
                    randGen = new Random();
                    numbersAndLetters = "ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
                }
            }
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)];
        }
        return new String(randBuffer);
    }

    public static String randomNum(int length) {
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            synchronized (initLock) {
                if (randGen == null) {
                    randGen = new Random();
                    numbersAndLetters = "0123456789".toCharArray();
                }
            }
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)];
        }
        return new String(randBuffer);
    }

    public static void main(String[] args) {

        //String date="{\"path\":\"htp://www.firefox.cn/static\\\\file\\upload\\pic\\a.jpg\"}";
//        String dbStartTime=DateUtil.add(DateUtil.getDate(date, DateUtil.DATE_FORMAT_YMDHMS), Calendar.MINUTE, -1, DateUtil.DATE_FORMAT_YMDHM)+":00";
//        String dbEndTime=DateUtil.add(DateUtil.getDate(date, DateUtil.DATE_FORMAT_YMDHMS), Calendar.MINUTE, 1, DateUtil.DATE_FORMAT_YMDHM)+":00";
//
//        System.out.println(DateUtil.getDate(date, DateUtil.DATE_FORMAT_YMDHMS));
//        System.out.println(DateUtil.getDate(date, DateUtil.DATE_FORMAT_YMDHMS));
//        System.out.println(dbStartTime);
//        System.out.println(dbEndTime);
//
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MINUTE, 1);
//        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATE_FORMAT_YMDHMS);
//        System.out.println(sdf.format(cal.getTime()));
        //System.out.println(date.replaceAll("\\\\\\\\", "/").replaceAll("\\\\", "/"));
        System.out.println(getNumId());

    }
}
