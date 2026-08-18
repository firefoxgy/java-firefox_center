package com.firefox.center.sys;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;

@Slf4j
@EnableScheduling
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
public class SysApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SysApplication.class);
    }

    public static void main(String[] args) throws UnknownHostException {
        //System.setProperty("spring.devtools.restart.enabled", "true");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));

        ConfigurableApplicationContext application = SpringApplication.run(SysApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        log.info("\n----------------------------------------------------------\n\t" +
                "Application Firefox-Boot is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path + "\n\t" +
                "External: \thttp://localhost:" + port + path + "\n\t" +
                "Swagger文档: \thttp://localhost:" + port + path + "/doc.html\n" +
                "----------------------------------------------------------");

    }

    /**
     * tomcat-embed-jasper引用后提示jar找不到的问题
     */
//    @Bean
//    public TomcatServletWebServerFactory tomcatFactory() {
//        return new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                ((StandardJarScanner) context.getJarScanner()).setScanManifest(false);
//            }
//        };
//    }
}