package com.firefox.center;

import com.firefox.center.common.annotation.EnableLoginArgResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author: sujie
 */
@EnableScheduling
@EnableLoginArgResolver
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@EnableConfigurationProperties
@SpringBootApplication
@ComponentScan(basePackages = {"com.firefox.center.*"})
public class CreditCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(CreditCenterApp.class, args);
    }
}
