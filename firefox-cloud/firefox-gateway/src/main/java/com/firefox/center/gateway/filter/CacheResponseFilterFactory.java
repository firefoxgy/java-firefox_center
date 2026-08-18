package com.firefox.center.gateway.filter;


import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @program: java-firefox_center
 * @description: metabse 修改响应数据过滤器
 * @author: yungeng
 * @created: 2021/08/09 11:45
 */
@Slf4j
@Component
public class CacheResponseFilterFactory extends ModifyResponseBodyGatewayFilterFactory {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public GatewayFilter apply(Config config) {
        return new ModifyResponseGatewayFilter(this.getConfig());
    }

    private Config getConfig() {
        Config cf = new Config();
        cf.setRewriteFunction(byte[].class, byte[].class, getRewriteFunction());
        return cf;
    }

    /** * 重写 Response 返回体 去除Content-Encoding 解压压缩的body */
    private RewriteFunction<byte[], byte[]> getRewriteFunction() {

        return (exchange, resp) -> {
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();
            if (response.getStatusCode() == HttpStatus.OK) {
                // 设置 HTTP 状态为 500
                byte[] respData = resp;

                //Content-Encoding: gzip 需要解压缩数据
                int code = 0;
                try {
                    String data = new String(respData, "UTF-8");

                    try {
                        JSONObject jsonBody = JSONObject.parseObject(data);
                        code = jsonBody.getInteger("code");
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                    String cacheKey = exchange.getAttribute(CacheGatewayFilterFactory.CACHE_KEY_PRE);
                    String strExp = exchange.getAttribute(CacheGatewayFilterFactory.CACHE_KEY_EXP);
                    if(!StringUtils.isEmpty(cacheKey) && CodeEnum.OK.getCode() == code) {
                        long exp = 0;
                        if(!StringUtils.isEmpty(strExp) )
                            exp = Long.parseLong(strExp);
                        redisTemplate.opsForValue().set(cacheKey, data, exp, TimeUnit.SECONDS);
                        log.info("存入缓存 key:{} cache:{} exp:{}s", cacheKey, data, exp);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return Mono.just(respData);

            }



            return Mono.just(resp);
        };

    }


    @Override
    public String name() {
        return "CacheResponseFilter";
    }
}


