package com.firefox.center.config.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @program: java-firefox_center
 * @description: nacos配置中心配置
 * @author: yungeng
 * @created: 2021/12/02 09:35
 */
@ConfigurationProperties(prefix="firefox.config.gateway.route", ignoreUnknownFields = true)
@Configuration
@Data
public class GatewayRouteProperties {

    private String dataId;

    private String group;

}