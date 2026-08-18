package com.firefox.center.gateway.config;

import com.firefox.center.gateway.service.RouteOperator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: java-firefox_center
 * @description: 监听配置
 * @author: yungeng
 * @created: 2021/12/01 17:47
 */
@Configuration
public class RouteOperatorConfig {
    @Bean
    public RouteOperator routeOperator(ObjectMapper objectMapper,
        RouteDefinitionWriter routeDefinitionWriter,
        ApplicationEventPublisher applicationEventPublisher) {

        return new RouteOperator(objectMapper,
            routeDefinitionWriter,
            applicationEventPublisher);
    }
}