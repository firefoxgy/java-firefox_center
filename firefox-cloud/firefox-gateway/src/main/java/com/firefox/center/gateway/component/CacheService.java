package com.firefox.center.gateway.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.firefox.center.common.kit.StrKit;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String,String> redisTemplate;

    public static final String SEPARATOR="::";
    public static final String PREFIX_AUTH="auth"+SEPARATOR+"path"+SEPARATOR;

    public List<String> getPath(long userId) {
        String key= PREFIX_AUTH+"userId"+SEPARATOR+userId;
        String authStr = redisTemplate.opsForValue().get(key);
        if (StrKit.isBlank(authStr)){
            return Lists.newArrayList();
        }
        return JSONArray.parseArray(JSON.parse(authStr).toString() , String.class);
    }

}
