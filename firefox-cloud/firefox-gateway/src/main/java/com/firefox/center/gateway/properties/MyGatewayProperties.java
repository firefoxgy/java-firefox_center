package com.firefox.center.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;
import static com.firefox.center.gateway.properties.MyGatewayProperties.PREFIX;

/**
 * @Author: ZJL
 * @Description: 自定义安全配置类
 * @Date: 2020/06/10
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = PREFIX)
public class MyGatewayProperties {

    public static final String PREFIX = "my.gateway";

    @Value("${enable-logger:true}")
    private Boolean enableLogger;
    private FilterIgnore ignore;
    private Sm4 sm4;

    @Getter
    @Setter
    public static class FilterIgnore{
        private List<String> tokenFilter;
        private List<String> menuResourceFilter;
        private List<String> loggerFilter;
    }

    @Getter
    @Setter
    public static class Sm4{
        private String key;
    }

}
