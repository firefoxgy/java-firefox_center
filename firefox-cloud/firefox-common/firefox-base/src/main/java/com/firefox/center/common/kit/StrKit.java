package com.firefox.center.common.kit;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: sujie
 * date: 2020-06-15
 */
public class StrKit {

    private static Random randGen = null;
    private static Object initLock = new Object();
    private static char[] numbersAndLetters = null;
    private static final String[] htmlChars = new String[]{"&", "<", ">", "'", "\""};
    private static final String[] escapeChars = new String[]{"&amp;", "&lt;", "&gt;", "&#39;", "&quot;"};

    public StrKit() {
    }

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
     * 生成一个随机码
     */
    private static String randomString(int length) {
        String chars="0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            synchronized (initLock) {
                if (randGen == null) {
                    randGen = new Random();
                    numbersAndLetters = chars.toCharArray();
                }
            }
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(chars.length())];
        }
        return new String(randBuffer);
    }

    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] = (char)(arr[0] + 32);
            return new String(arr);
        } else {
            return str;
        }
    }

    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] = (char)(arr[0] - 32);
            return new String(arr);
        } else {
            return str;
        }
    }

    public static boolean isNull(Object o) {
        return o==null?true:false;
    }

    public static boolean isBlank(Object obj) {
        if (obj == null || "null".equals(obj.toString())) {
            return true;
        } else {
            int len = obj.toString().length();
            if (len == 0) {
                return true;
            } else {
                int i = 0;

                while(i < len) {
                    switch(obj.toString().charAt(i)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ':
                            ++i;
                            break;
                        default:
                            return false;
                    }
                }

                return true;
            }
        }
    }

    public static boolean notBlank(Object obj) {
        return !isBlank(obj);
    }

    public static boolean notBlank(String... strings) {
        if (strings != null && strings.length != 0) {
            String[] var1 = strings;
            int var2 = strings.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String str = var1[var3];
                if (isBlank(str)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotEmpty(String string) {
        return string != null && !string.equals("");
    }

    public static boolean isNotBlank(Object o) {
        return o == null ? false : notBlank(o.toString());
    }

    public static boolean notNull(Object para) {
        if (para == null || "null".equals(para.toString())) {
            return false;
        }else{
            return true;
        }
    }

    public static boolean notNull(Object... paras) {
        if (paras == null) {
            return false;
        } else {
            Object[] var1 = paras;
            int var2 = paras.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                Object obj = var1[var3];
                if (obj == null) {
                    return false;
                }
            }

            return true;
        }
    }

    public static String toCamelCase(String stringWithUnderline) {
        if (stringWithUnderline.indexOf(95) == -1) {
            return stringWithUnderline;
        } else {
            stringWithUnderline = stringWithUnderline.toLowerCase();
            char[] fromArray = stringWithUnderline.toCharArray();
            char[] toArray = new char[fromArray.length];
            int j = 0;

            for(int i = 0; i < fromArray.length; ++i) {
                if (fromArray[i] == '_') {
                    ++i;
                    if (i < fromArray.length) {
                        toArray[j++] = Character.toUpperCase(fromArray[i]);
                    }
                } else {
                    toArray[j++] = fromArray[i];
                }
            }

            return new String(toArray, 0, j);
        }
    }

    public static String join(String[] stringArray) {
        StringBuilder sb = new StringBuilder();
        String[] var2 = stringArray;
        int var3 = stringArray.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String s = var2[var4];
            sb.append(s);
        }

        return sb.toString();
    }

    public static String join(String[] stringArray, String separator) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < stringArray.length; ++i) {
            if (i > 0) {
                sb.append(separator);
            }

            sb.append(stringArray[i]);
        }

        return sb.toString();
    }

    public static boolean equals(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    public static boolean areNotEmpty(String... strings) {
        if (strings != null && strings.length != 0) {
            String[] var1 = strings;
            int var2 = strings.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String string = var1[var3];
                if (string == null || "".equals(string)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static String requireNonBlank(String string) {
        if (isBlank(string)) {
            throw new NullPointerException();
        } else {
            return string;
        }
    }

    public static String requireNonBlank(String string, String message) {
        if (isBlank(string)) {
            throw new NullPointerException(message);
        } else {
            return string;
        }
    }

    public static String obtainDefaultIfBlank(String string, String defaultValue) {
        return isBlank(string) ? defaultValue : string;
    }

    public static boolean match(String string, String regex) {
        Pattern pattern = Pattern.compile(regex, 2);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        } else {
            int i = str.length();

            char chr;
            do {
                --i;
                if (i < 0) {
                    return true;
                }

                chr = str.charAt(i);
            } while(chr >= '0' && chr <= '9');

            return false;
        }
    }

    public static boolean isDecimal(String str) {
        if (str == null) {
            return false;
        } else {
            int i = str.length();

            char chr;
            do {
                do {
                    --i;
                    if (i < 0) {
                        return true;
                    }

                    chr = str.charAt(i);
                } while(chr >= '0' && chr <= '9');
            } while(chr == '.');

            return false;
        }
    }

    public static boolean isEmail(String email) {
        return Pattern.matches("\\w+@(\\w+.)+[a-z]{2,3}", email);
    }

    public static boolean isMobileNumber(String phoneNumber) {
        return Pattern.matches("^(1[3,4,5,6,7,8,9])\\d{9}$", phoneNumber);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Set<String> splitToSet(String src, String regex) {
        if (src == null) {
            return null;
        } else {
            String[] strings = src.split(regex);
            Set<String> set = new HashSet();
            String[] var4 = strings;
            int var5 = strings.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String s = var4[var6];
                if (!isBlank(s)) {
                    set.add(s.trim());
                }
            }

            return set;
        }
    }

    public static String unEscapeHtml(String content) {
        return isBlank(content) ? content : StringUtils.replaceEach(content, escapeChars, htmlChars);
    }

    public static String randomNum(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static boolean isJson(String content){
        try {
            JSONObject jsonStr= JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String phone="15887253628";
        System.out.println(phone.substring(phone.length()-6));
        Object o=null;
        System.out.println(isNotBlank(o));

    }
}
