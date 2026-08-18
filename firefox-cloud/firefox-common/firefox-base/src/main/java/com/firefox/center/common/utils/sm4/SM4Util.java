package com.firefox.center.common.utils.sm4;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.firefox.center.common.utils.ByteUtil;

public class SM4Util {

    private static String key="313a779e9968d4dd0e95bfb992a3af5a";

    public static String encrypt(String plainTxt, String key){
        String cipherTxt = "";
        SymmetricCrypto sm4 = new SM4(Mode.ECB, Padding.PKCS5Padding, ByteUtil.hexStringToBytes(key));
        cipherTxt = sm4.encryptHex(plainTxt);

        return cipherTxt;
    }

    public static String decrypt(String cipherTxt, String key){
        String plainTxt = "";
        SymmetricCrypto sm4 = new SM4(Mode.ECB, Padding.PKCS5Padding, ByteUtil.hexStringToBytes(key));
        plainTxt = sm4.decryptStr(cipherTxt, CharsetUtil.CHARSET_UTF_8);
        return plainTxt;
    }

    public static void main(String[] argc){
        String data="{center_id: \"8\"" + "client_secret: \"4a3bb1de48cf4d0cb4e6a96db8d55e26\"\n"
            + "code: \"14\"\n" + "key: 1652232032421\n" + "password: \"8hvHtQ6ta2aTGIzU\"\n" + "username: \"admin\"}";
        System.out.println("原文: " + data);
        String cipherTxt = encrypt(data, key);
        System.out.println("length: " + cipherTxt.length() + "密文: " + cipherTxt);
        //cipherTxt = "3b4b0d1b71b24e104b2e8418d4f109b5203d20236bc28bbce732466f8f9f310dbf912752e05f92784e57fba106bbe3f20ba191d92babbb60a6659d529146a4bf5a5c3397f84601398eeec0a57d093a57a318667b198ab3eda843b6878def85fa463ba7e2dc0d65b39ba20ffef75075ee60968e22dcdf082af938dbfd6fe32fcbf7ceccd72b893e58817ef5162e0efc2185ca5a35b5486c782e0a93ff2cdde8719c6cc0c2acf9f30dca2adbe4f4722685";
        String plainTxt = decrypt(cipherTxt, key);
        System.out.println("解密结果: " + plainTxt);
    }
}
