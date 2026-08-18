package com.firefox.center.credit.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.firefox.center.credit.property.FirefoxProperties.PREFIX;

@Data
@Configuration
@ConfigurationProperties(prefix = PREFIX)
public class FirefoxProperties {
    public static final String PREFIX = "firefox";

    private Sharding sharding = new Sharding();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sharding {
        private String start;
    }

}
