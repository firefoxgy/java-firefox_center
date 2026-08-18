package com.firefox.center.common.ribbon;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * Feign统一配置
 *
 * @Author: sujie
 * @date 2021/04/18 14:04
 */
public class FeignAutoConfigure {

    /**
     * Feign 日志级别
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
