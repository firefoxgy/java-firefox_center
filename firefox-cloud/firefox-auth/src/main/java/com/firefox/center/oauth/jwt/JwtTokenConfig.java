package com.firefox.center.oauth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * https://www.cnblogs.com/reroyalup/p/13062967.html
 * JwtTokenConfig
 *
 * @author gengyun
 * @date 2020/10/12
 */
@Configuration
@RequiredArgsConstructor
public class JwtTokenConfig {

    private final RedisConnectionFactory redisConnectionFactory;

    @Value("${FIREFOX_KEYSTORE_PASSWORD:}")
    private String keyStorePassword;

    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
        //return new RedisTemplateTokenStore(redisConnectionFactory);
    }

    /**
     *https://www.cnblogs.com/fp2952/p/8973613.html
     * D:\workspace_firefox\java-firefox_center\firefox-authenticator\src\main\resources
     * Configure keypass and storepass outside source control when generating the keystore.
     * keytool -importkeystore -srckeystore firefoxcenter.jks -destkeystore firefoxcenter.jks -deststoretype pkcs12
     *
     * keytool -list -rfc --keystore firefoxcenter.jks | openssl x509 -inform pem -pubkey
     *
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // 导入证书
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource("firefoxcenter.jks"), keyStorePassword.toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("firefoxcenter"));

        return converter;
    }

}
