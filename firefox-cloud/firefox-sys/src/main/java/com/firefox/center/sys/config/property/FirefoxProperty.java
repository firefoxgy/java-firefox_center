package com.firefox.center.sys.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

import static com.firefox.center.sys.config.property.FirefoxProperty.PREFIX;

@Data
@Configuration
@ConfigurationProperties(prefix = PREFIX)
public class FirefoxProperty implements Serializable {
    private static final long serialVersionUID = 5243926308290263767L;
    public static final String PREFIX = "firefox";

    private String uploadType="";
    private String webRoot="";

}
