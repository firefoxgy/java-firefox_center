package com.firefox.center.gateway.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.constants.BusinessConstants;
import com.firefox.center.common.constants.SecurityConstants;
import com.firefox.center.gateway.properties.MyGatewayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @program: java-firefox_center
 * @description: webase front接口请求处理过滤器
 * @author: yungeng
 * @created: 2021/08/06 10:24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenIMFilterFactory extends AbstractGatewayFilterFactory<OpenIMFilterFactory.Config> {

    private final MyGatewayProperties myGatewayProperties;
    private final Class<Config> configClass = Config.class;

    @Override
    public String name() {
        return "OpenIMFilter";
    }

    @Override
    public Class<Config> getConfigClass() {
        return configClass;
    }

    @Override
    public Config newConfig() {
        return BeanUtils.instantiateClass(this.configClass);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new InnerFilter(config);
    }

    public final static String CACHE_IM_TOKEN_PRE = "com:firefox:center:im:token_";

    public final static String HEADER_TOKEN = "token";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 创建一个内部类，来实现2个接口，指定顺序
     */
    private class InnerFilter implements GatewayFilter, Ordered {

        private Config config;

        InnerFilter(Config config) {
            this.config = config;
        }

        /**
         * 通过使用redis默认的序列化获取String类型的值
         *
         * @param key 键
         * @return String类型的值
         */
        public String getStringBySer(String key) {
            try {
                return (String)redisTemplate.execute((RedisCallback<String>) connection -> {
                    RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                    byte[] serialize = serializer.serialize(key);
                    if (serialize == null) {
                        return null;
                    }
                    byte[] value = connection.get(serialize);
                    return serializer.deserialize(value);
                });
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
            MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
            String appId = exchange.getRequest().getHeaders().getFirst(AuthenticatorTokenFilter.HEADER_MANAGE_APPS);
            String token = exchange.getAttribute(AuthenticatorTokenFilter.ATTRIBUTE_TOKEN);
            String imToken = getStringBySer(CACHE_IM_TOKEN_PRE + token);
            ServerHttpRequest newReq = exchange.getRequest().mutate()
                    .header(HEADER_TOKEN, imToken)
                    .build();
            return chain.filter(exchange.mutate().request(newReq).build()).then(Mono.fromRunnable(() -> {
                // post filter
                log.info("--------------------- 后置 Filter---------------------");
            }));


        }


        @Override
        public int getOrder() {
            return 10;
        }
    }

    public static class Config {

    }


}

