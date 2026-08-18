package com.firefox.center.sys.common.cache;


import com.firefox.center.sys.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCache {

    private final RedisUtil redisUtil;

    public static final String CACHE_DAILY = "token_daily";
    public static final String CACHE_PAY = "token_pay";
    public static final String CACHE_WX_MINI = "token_wx_mini";
    public static final String CACHE_DINGTALK = "token_dingtalk";

    public String getToken(String cacheKey) {
        Object oToken=redisUtil.get(cacheKey);
        return oToken==null?"":oToken.toString();
    }

    public void setToken(String cacheKey, String token, long expire) {
        redisUtil.set(cacheKey, token, expire);
    }

}
