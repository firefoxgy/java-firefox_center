package com.firefox.center.common.ribbon;

import com.firefox.center.common.ribbon.config.RestTemplateProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.DefaultPropertiesFactory;
import org.springframework.context.annotation.Bean;

/**
 * Ribbon扩展配置类
 *
 * @Author: sujie
 * @date 2021/04/17 9:24
 */
@EnableConfigurationProperties(RestTemplateProperties.class)
public class RibbonAutoConfigure  {
    @Bean
    public DefaultPropertiesFactory defaultPropertiesFactory() {
        return new DefaultPropertiesFactory();
    }
}
