package com.firefox.center.sys.common.util;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

public class Base64Util {

    private static Base64 base64 = new Base64();

    public static String encode(String text) throws UnsupportedEncodingException {
        byte[] textByte = text.getBytes("UTF-8");
        return base64.encodeToString(textByte);
    }

    public static String decode(String text) throws UnsupportedEncodingException {
        return new String(base64.decode(text), "UTF-8");
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        System.out.println(encode("{\"validate\":\"\",\"notifyData\":{\"typeCode\":\"\",\"returnCode\":\"\",\"returnMsg\":\"\",\"trxAmount\": \"\",\"payMethod\":\"\",\"orderStatus\":\"\",\"orderId\":\"\",\"refundStatus\": \"\",\"refundId\": \"\",\"completeDate\":\"\",\"completeTime\": \"\",\"orderNo\":\"\"}}\n"));
        System.out.println(decode("eyJ2YWxpZGF0ZSI6IiIsIm5vdGlmeURhdGEiOnsidHlwZUNvZGUiOiIiLCJyZXR1cm5Db2RlIjoiIiwicmV0dXJuTXNnIjoiIiwidHJ4QW1vdW50IjogIiIsInBheU1ldGhvZCI6IiIsIm9yZGVyU3RhdHVzIjoiIiwib3JkZXJJZCI6IiIsInJlZnVuZFN0YXR1cyI6ICIiLCJyZWZ1bmRJZCI6ICIiLCJjb21wbGV0ZURhdGUiOiIiLCJjb21wbGV0ZVRpbWUiOiAiIiwib3JkZXJObyI6IiJ9fQo="));
    }
}
