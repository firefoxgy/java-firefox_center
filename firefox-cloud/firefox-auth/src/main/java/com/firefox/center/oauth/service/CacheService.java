package com.firefox.center.oauth.service;

import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.redis.constant.RedisConstant;
import com.firefox.center.common.redis.template.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CacheService {

    private final RedisRepository redis;

    public void cachePath(long userId, List<String> path) {
        String key= RedisConstant.PREFIX_AUTH+"userId"+RedisConstant.SEPARATOR+userId;
        redis.opsForValueSet(key, JSONObject.toJSONString(path), 7l, TimeUnit.DAYS);
    }

}
