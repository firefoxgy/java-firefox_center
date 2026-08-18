package com.firefox.center.sys.common.wx.mini;

import com.alibaba.fastjson.JSONObject;
import com.firefox.center.sys.common.cache.TokenCache;
import com.firefox.center.sys.common.util.RetryUtils;
import com.firefox.center.sys.common.util.SpringBoot;
import com.firefox.center.sys.common.util.StrKit;
import com.firefox.center.common.Record;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.Callable;

@Component
@Slf4j
public class WxMini {
    //获取openId接口
    private static final String API_JSCODE2SESSION = "https://api.weixin.qq.com/sns/jscode2session";
    //获取token接口
    private static final String API_TOKEN = "https://api.weixin.qq.com/cgi-bin/token";
    //发送消息接口
    private static final String API_MESSAGE_SEND = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";

    //访客预约待审核通知
    public static final String NOTICE_TEMPLATE_APPLY = "n1tO4Qq8yB47Rj10Fuly1E4Qf7varU-LSt0Ii_L5NrI";
    public static final String NOTICE_PAGE_APPLY = "pages/personal-center/my-audit";
    //拜访审核结果通知
    public static final String NOTICE_TEMPLATE_APPLY_RESULT = "v3evKMf0exTdATQZBuAxD5k6Fb_QFLbEnkHfTCo6CXo";
    public static final String NOTICE_PAGE_APPLY_RESULT = "pages/pass-record/index";
    //公司公告通知
    public static final String NOTICE_TEMPLATE_NOTICE_RESULT = "vkVOcvJgtVAKdY89jp9VjXR1iKZ9UlWzzW3ErXH4AY0";
    public static final String NOTICE_PAGE_NOTICE_RESULT = "pages-notice/detail?noticeId=";

    @Value("${wx.mini.state}")
    private String state;
    @Value("${wx.mini.appId}")
    private String appId;
    @Value("${wx.mini.appSecret}")
    private String appSecret;

    private final RestTemplate restTemplate;
    private final TokenCache tokenCache;

    public WxMini(RestTemplate restTemplate,
                  TokenCache tokenCache) {
        this.restTemplate = restTemplate;
        this.tokenCache = tokenCache;
    }

    //获取openId
    public String getOpenId(String code){
        String requestUrl = API_JSCODE2SESSION+"?appid="+appId+"&secret="+appSecret+"&js_code="+code+"&grant_type=authorization_code";
        String res = restTemplate.getForObject(requestUrl,String.class);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if (jsonObject.get("errcode") != null) {
            return "";
        }
        return jsonObject.get("openid").toString();
    }

    //发送消息
    /**
     * {
     *   "touser": "OPENID",
     *   "template_id": "TEMPLATE_ID",
     *   "page": "index",
     *   "miniprogram_state":"developer",
     *   "lang":"zh_CN",
     *   "data": {
     *       "number01": {
     *           "value": "339208499"
     *       },
     *       "date01": {
     *           "value": "2015年01月05日"
     *       },
     *       "site01": {
     *           "value": "TIT创意园"
     *       } ,
     *       "site02": {
     *           "value": "广州市新港中路397号"
     *       }
     *   }
     * }
     */
    public boolean messageSend(String openId, String templateId, String page, Record data){
        String requestUrl = API_MESSAGE_SEND+"?access_token="+getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Record params = new Record()
                .set("touser", openId)
                .set("template_id",  templateId)
                .set("page",  page)
                .set("miniprogram_state",  state)
                .set("lang",  "zh_CN")
                .set("data",  data.getColumns());
        HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(params.getColumns(), headers);
        RestTemplate restTemplate = (RestTemplate) SpringBoot.getBean(RestTemplate.class);
        ResponseEntity<String> entity = restTemplate.postForEntity(requestUrl, request, String.class);
        JSONObject json = JSONObject.parseObject(entity.getBody());
        System.out.println("返回结果"+entity.getBody());
        if(json.getIntValue("errcode")==0){
            return true;
        }else{
            return false;
        }
    }

    //获取token
    public String getToken() {
        String token = getAvailableAccessToken();
        if (!StrKit.notBlank(token)) {
            synchronized(this) {
                token = getAvailableAccessToken();
                if (!StrKit.notBlank(token)) {
                    token = refreshAccessToken();
                }
            }
        }
        return token;
    }

    private String getAvailableAccessToken() {
        return tokenCache.getToken(TokenCache.CACHE_WX_MINI);
    }

    /**
     * 无条件强制更新 token 值，不再对 cache 中的 token 进行判断
     * @return token
     */
    public String refreshAccessToken() {
        String token="";
        // 最多三次请求
        AccessToken result = RetryUtils.retryOnException(3, new Callable<AccessToken>() {
            @Override
            public AccessToken call() throws Exception {
                String requestUrl = API_TOKEN+"?appid="+appId+"&secret="+appSecret+"&grant_type=client_credential";
                RestTemplate restTemplate = (RestTemplate) SpringBoot.getBean(RestTemplate.class);
                ResponseEntity<String> entity = restTemplate.getForEntity(requestUrl, String.class);
                return new AccessToken(entity.getBody());
            }
        });
        if (null != result) {
            tokenCache.setToken(TokenCache.CACHE_WX_MINI, result.getToken(), 7192);
            token=result.getToken();
        }
        return token;
    }

}
