package com.firefox.center.gateway;/**
 * @Description
 * @author sujie
 * @date 2021年12月01日 10:05
 */

import com.firefox.center.common.utils.DateUtil;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/**
 * @program: firefox-cloud
 *
 * @description:
 *
 * @author: blue
 *
 * @create: 2021-12-01 10:05
 **/
public class Test {


    public static void main(String[] args) {
        String publicKey="-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApuDrogWydnpiQVSx/LH2\n" +
                "9YpMTJflEEIAFnj4dg6fiRSnO4IrwtFVr1A3h3GAMgexfVaFYZxATD/RPmyuCpM8\n" +
                "ffUPuI3mLEIlS+WTRK4l34d23f6CG57yb+Ombo/3Egn5lavl8LaIpmWzUrXF3C1E\n" +
                "8naYP49r3XuCej8IdebBF8ETvcpeSE3puUSS654xvBn4ADq6EmngOUYlmYcphOzd\n" +
                "xd7X0l4giL3EM4Km51FyH/F6RMwwAPYyzyEifT8Qc1+OwgYxdh1OedH3LoDQz4LY\n" +
                "w3otU/2/ea/Kp22a21lsKfl+7yNqBBXyqFkmvb13Y7Wx3wrGkmsxIRl4JNNqC+09\n" +
                "sQIDAQAB\n" +
                "-----END PUBLIC KEY-----";
        String token="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0ZW5hbnRfaWQiOjI1LCJ1c2VyX25hbWUiOiJhcHBfMTQ2MzE2MDk1OTA4MTEyMzg0MiIsIm9wZW5pZCI6IjNiNjY5MDcwMGMyYjQ2ODQ4ZWFkNzJkOGY2NjNmMjc2IiwidXR5cGUiOiJhcHAiLCJwcmVfdWlkIjoiIiwiY2xpZW50X3R5cGUiOiJ3ZWIiLCJ2ZXJzaW9uIjoiMS4wLjAiLCJjbGllbnRfaWQiOiJrcGMzbnNkZTg5M2Zrc3N2anQ5ayIsInNpZCI6bnVsbCwic2lkcyI6bnVsbCwidWlkIjo3LCJjYWNoZV91aWQiOjcsInNjb3BlIjpbImFsbCJdLCJleHAiOjE2NjE4NDA1NzgsImFwcF9pZCI6ImtwYzNuc2RlODkzZmtzc3ZqdDlrIiwianRpIjoiMTM5NjU1NGMtNzE4Yy00MjkzLWI0NGYtMjE1ZDU3NjU2YTdjIn0.gSU2zdbr-qS0u3A8_gfxpCs1RtvgTWxNuLmkYiOz6JXwGoOAigvbiMQqxhbraLfBl6zbjSmLvHPTXERD7IOi9jRd8s6zt7GSQrw8a8EDSTfiXjPSJ7IoTsdpOZRiaqJcC1HBvXXyzX6TxTslgWLH9Enomtub-nIGd7lCO1Ox84gfyw-Wkv3AQYcW34I2fjphWNhUEF9MCQLR_NVcWuLK4PeQRx3Ql2b02_WjCSMb6GhyqFBDNWA7qfJKFzNvDvITJehHe2xeqhNZMIz1HrEGq6OIq0MfrYmp2JbV_Sup61wwjwps2AhcGkpbTrtvAfyzwGO36Imgb_Ra3M0QANTc0A";
        String str = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey)).getClaims();
        System.out.println(str);

        System.out.println(DateUtil.nowTimeStamp());


    


    }
}
