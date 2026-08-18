package com.firefox.center;

import com.firefox.center.common.annotation.EnableLoginArgResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/** 
* @Author: sujie
*/
@EnableDiscoveryClient
@EnableRedisHttpSession
@EnableLoginArgResolver
@SpringBootApplication
@ComponentScan(basePackages = {"com.firefox.center.*"})
@EnableFeignClients
public class AuthServerApp {
	public static void main(String[] args) {
		SpringApplication.run(AuthServerApp.class, args);
	}
}
