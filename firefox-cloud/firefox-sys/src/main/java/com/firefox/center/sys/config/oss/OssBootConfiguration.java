package com.firefox.center.sys.config.oss;

import com.firefox.center.sys.common.util.oss.OssBootUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssBootConfiguration {

    @Value("${firefox.oss.endpoint}")
    private String endpoint;
    @Value("${firefox.oss.accessKey}")
    private String accessKeyId;
    @Value("${firefox.oss.secretKey}")
    private String accessKeySecret;
    @Value("${firefox.oss.bucketName}")
    private String bucketName;
    @Value("${firefox.oss.staticDomain}")
    private String staticDomain;


    @Bean
    public void initOssBootConfiguration() {
        OssBootUtil.setEndPoint(endpoint);
        OssBootUtil.setAccessKeyId(accessKeyId);
        OssBootUtil.setAccessKeySecret(accessKeySecret);
        OssBootUtil.setBucketName(bucketName);
        OssBootUtil.setStaticDomain(staticDomain);
    }
}