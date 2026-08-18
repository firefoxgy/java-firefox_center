package com.firefox.center;

import com.firefox.center.common.annotation.EnableLoginArgResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author: sujie
 */
@EnableLoginArgResolver
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@SpringBootApplication
@ComponentScan(basePackages = {"com.firefox.center.*"})
public class AppCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(AppCenterApp.class, args);
    }
}
