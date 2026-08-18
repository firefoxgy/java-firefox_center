package com.firefox.center.sys.common.util.security;

import sun.misc.BASE64Encoder;
import java.io.FileInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CerUtil {
    public static void main(String[] args) throws Exception {
        String path="E:\\公司\\智慧云报\\dasClient.cer";
        System.out.println("-----------------公钥--------------------");
        System.out.println(getPublicKey(path));
        System.out.println("-----------------公钥--------------------");
    }

    public static String getPublicKey(String path) throws Exception {
        try{
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new FileInputStream(path));
            PublicKey publicKey = cert.getPublicKey();
            BASE64Encoder base64Encoder = new BASE64Encoder();
            return base64Encoder.encode(publicKey.getEncoded());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
