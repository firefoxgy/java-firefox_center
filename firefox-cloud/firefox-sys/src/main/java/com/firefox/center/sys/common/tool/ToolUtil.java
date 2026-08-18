package com.firefox.center.sys.common.tool;

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
     * 生成一个随机码
     */
    private static String randomString(int length) {
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
            randBuffer[i] = numbersAndLetters[randGen.nextInt(32)];
        }
        return new String(randBuffer);
    }

    public static String randomStrString(int length) {
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
            randBuffer[i] = numbersAndLetters[randGen.nextInt(32)];
        }
        return new String(randBuffer);
    }

    public static String randomNumString(int length) {
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
            randBuffer[i] = numbersAndLetters[randGen.nextInt(32)];
        }
        return new String(randBuffer);
    }

    public static void main(String[] args) {
    }
}
