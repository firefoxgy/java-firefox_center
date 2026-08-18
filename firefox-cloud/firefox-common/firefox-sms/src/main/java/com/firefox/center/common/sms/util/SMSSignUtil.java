package com.firefox.center.common.sms.util;


import com.firefox.center.common.Record;
import com.firefox.center.common.sms.pojo.SignData;
import com.firefox.center.common.utils.DateUtil;

import java.util.*;

/**
 * 签名生成类
 */
public class SMSSignUtil {

    public static void main(String[] args) throws Exception {
        String secret="";
        Record params = new Record();
        params.set("appkey", "xNQivxXwYjPc5bnbOX1TznrpPQkEET3v");
        params.set("module", "ynwhy_20200601");
        params.set("ts", DateUtil.nowTimeStamp());
        params.set("type", "1");
        params.set("json", new Record().set("code", "123123").toJson());
        params.set("tel", "13708462893");
        params.set("signName", "春城晚报开屏新闻");
        params.set("templateCode", "SMS_122297647");
        String sign=sign(params.getColumns(), secret);
        params.set("sign", sign);

    }

    public static String sign(Map<String,Object> map, String secret) throws Exception {
        List<SignData> list = new ArrayList<>();
        Iterator it = map.entrySet().iterator() ;
        while (it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next() ;
            Object keyname = entry.getKey() ;
            Object value = entry.getValue() ;
            SignData temp = new SignData(keyname.toString(),value.toString());
            list.add(temp);
        }

        Collections.sort(list, new Comparator<SignData>() {
            @Override
            public int compare(SignData b1, SignData b2) {
                return b1.getKey().compareTo(b2.getKey());
            }
        });
        String signStr = secret;
        for (int i = 0; i < list.size(); i++) {
            signStr += list.get(i).getKey() +  list.get(i).getValue();
        }
        signStr=signStr.replaceAll("\"","\\\\\"");
        return SHA1Util.HmacSHA1Encrypt(signStr, secret).toUpperCase();
    }

}

