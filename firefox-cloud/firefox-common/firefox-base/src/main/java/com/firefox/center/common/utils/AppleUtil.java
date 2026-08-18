package com.firefox.center.common.utils;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/5/7 15:31
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

@Log4j2
public class AppleUtil {
    /**
     * 解密个人信息
     *
     * @param identityToken APP获取的identityToken
     *
     * @return 解密参数：失败返回null  sub就是用户id,用户昵称需要前端传过来
     */
    public static JSONObject verify(String identityToken) {
        try {
            String[] identityTokens = identityToken.split("\\.");
            String str = new String(Base64Utils.decodeFromString(identityTokens[1]), "UTF-8");
            JSONObject data = JSONObject.parseObject(str);
            String aud = (String) data.get("aud");
            String sub = (String) data.get("sub");
            if (verify(identityToken, aud, sub)) {
                return data;
            }
        } catch (Exception e) {
            log.info("verify(*) error ",e);
        }
        return null;
    }

    /**
     * 验证
     *
     * @param identityToken APP获取的identityToken
     * @param aud           您在您的Apple Developer帐户中的client_id
     * @param sub           用户的唯一标识符对应APP获取到的：user
     * @return true/false
     */
    private static boolean verify(String identityToken, String aud, String sub) {
        try {
            PublicKey publicKey = getPublicKey();
            if (publicKey==null){
                return false;
            }
            JwtParser jwtParser = Jwts.parser().setSigningKey(publicKey);
            jwtParser.requireIssuer("https://appleid.apple.com");
            jwtParser.requireAudience(aud);
            jwtParser.requireSubject(sub);
            Jws<Claims> claim = jwtParser.parseClaimsJws(identityToken);
            if (claim != null && claim.getBody().containsKey("auth_time")) {
                return true;
            }
        } catch (Exception e) {
            log.info("verify(*,*,*) error ", e);
        }
        return false;
    }

    private static PublicKey getPublicKey() {
        try {
            String url = "https://appleid.apple.com/auth/keys";
            RestTemplate restTemplate = new RestTemplate();
            JSONObject data = restTemplate.getForObject(url,JSONObject.class);
            JSONArray jsonArray = data.getJSONArray("keys");
            String n = jsonArray.getJSONObject(0).getString("n");
            String e = jsonArray.getJSONObject(0).getString("e");
            BigInteger modulus = new BigInteger(1, Base64.decodeBase64(n));
            BigInteger publicExponent = new BigInteger(1, Base64.decodeBase64(e));
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            log.info("getPublicKey error ", e);
        }
        return null;
    }

    public static void main(String[] args) {
        String jwt =
                "eyJraWQiOiJlWGF1bm1MIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLmNoYW5nZGFvLnR0c2Nob29sIiwiZXhwIjoxNTg5MjcwMzI3LCJpYXQiOjE1ODkyNjk3MjcsInN1YiI6IjAwMTk0MC43YTExNDFhYTAwMWM0NjllYTE1NjNjNmJhZTk5YzM3ZC4wMzA3IiwiY19oYXNoIjoienNIUW9xbTdjcDZOcmxrUHFhTmpGQSIsImVtYWlsIjoiYXEzMmsydnpjd0Bwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6InRydWUiLCJpc19wcml2YXRlX2VtYWlsIjoidHJ1ZSIsImF1dGhfdGltZSI6MTU4OTI2OTcyNywibm9uY2Vfc3VwcG9ydGVkIjp0cnVlfQ.q5unOzswOjpRYmrVKVm3FRb_Th6kkhgEvoFfTEAIETwgTXZ7bYcQM8J8tCjkGGqtt2z74Z-wTW7Q3ia209xhmwrVDIup0jcQgNTvsCEMkfo9evPIDrNRNQw2Dzw2EBKma8004NL6THYlySoDnPRoW_VQCHP_m0HnjYuIc-wtREEClf-_tOFDPpTsvUFoETHNfhpsLhqj24-zm6MSOocYY3WbUaYJQVEFCz-x6AGko1XkMtms_-JU1xakNtjMZTIVj2XyUI5MO7_eo-D9i_c7Hj-OE9HNBEvFnPxOesDzXvEoYdb7uByXEfa-H1syJMecBMRa3tL76W_CYKsONRkU9Q";
        verify(jwt);

    }
}
