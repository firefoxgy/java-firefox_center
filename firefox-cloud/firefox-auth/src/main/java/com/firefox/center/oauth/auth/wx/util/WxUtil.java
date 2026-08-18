package com.firefox.center.oauth.auth.wx.util;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/5/7 10:11
 */

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class WxUtil {

    /**
     * @Title: getAccessToken
     * @Description: 获取接口调用凭证
     * @param: @return
     * @return: String
     */
    public static JSONObject getAccessToken(String appId, String appSecret, String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appSecret + "&code=" + code
                + "&grant_type=authorization_code";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String body = responseEntity.getBody();
        // 返回结果转换为json对象
        JSONObject jObject = JSONObject.parseObject(body);
        return jObject;
    }

    public static JSONObject getUserInfo(String accessToken, String openid) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        String body = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        // 返回结果转换为json对象
        JSONObject jObject = JSONObject.parseObject(body);
        return jObject;
    }
}
