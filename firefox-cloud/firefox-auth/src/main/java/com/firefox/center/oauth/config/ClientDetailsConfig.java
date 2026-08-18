package com.firefox.center.oauth.config;

import com.firefox.center.oauth.service.RedisAuthorizationCodeServices;
import com.firefox.center.oauth.service.RedisClientDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @Author: sujie
 * @date 2021/04/12
 */
@Order(Integer.MAX_VALUE-9)
@Configuration
public class ClientDetailsConfig {
    @Resource
    private DataSource dataSource;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 声明 ClientDetails实现
     */
    @Bean
    public RedisClientDetailsService clientDetailsService() {
        RedisClientDetailsService clientDetailsService = new RedisClientDetailsService(dataSource);
        clientDetailsService.setRedisTemplate(redisTemplate);
        return clientDetailsService;
    }

    @Bean
    public RandomValueAuthorizationCodeServices authorizationCodeServices() {
        RedisAuthorizationCodeServices redisAuthorizationCodeServices = new RedisAuthorizationCodeServices();
        redisAuthorizationCodeServices.setRedisTemplate(redisTemplate);
        return redisAuthorizationCodeServices;
    }
}
