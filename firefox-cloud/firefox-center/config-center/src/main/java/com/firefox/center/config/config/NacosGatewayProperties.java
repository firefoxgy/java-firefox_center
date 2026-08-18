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
@ConfigurationProperties(prefix="firefox.config", ignoreUnknownFields = true)
@Configuration
@Data
public class NacosGatewayProperties {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NAMESPACE = "namespace";
    public static final String KEY_SERVER_ADDR = "serverAddr";


    private String serverAddr;

    private String dataId;

    private String group;

    private Long timeout;

    private String username;

    private String password;

    private String namespace;

}