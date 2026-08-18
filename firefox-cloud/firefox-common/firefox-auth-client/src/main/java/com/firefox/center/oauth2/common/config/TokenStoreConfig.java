package com.firefox.center.oauth2.common.config;

import com.firefox.center.oauth2.common.store.AuthDbTokenStore;
import com.firefox.center.oauth2.common.store.AuthJwtTokenStore;
import com.firefox.center.oauth2.common.store.AuthRedisTokenStore;
import com.firefox.center.oauth2.common.store.ResJwtTokenStore;
import com.firefox.center.oauth2.common.store.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * token存储配置
 *
 * @Author: sujie
 */
public class TokenStoreConfig {
    @Configuration
    @ConditionalOnProperty(prefix = "firefox.oauth2.token.store", name = "type", havingValue = "db")
    @Import(AuthDbTokenStore.class)
    public class JdbcTokenConfig {
    }

    @Configuration
    @ConditionalOnProperty(prefix = "firefox.oauth2.token.store", name = "type", havingValue = "redis", matchIfMissing = true)
    @Import(AuthRedisTokenStore.class)
    public class RedisTokenConfig {
    }

    @Configuration
    @ConditionalOnProperty(prefix = "firefox.oauth2.token.store", name = "type", havingValue = "authJwt")
    @Import(AuthJwtTokenStore.class)
    public class AuthJwtTokenConfig {
    }

    @Configuration
    @ConditionalOnProperty(prefix = "firefox.oauth2.token.store", name = "type", havingValue = "resJwt")
    @Import(ResJwtTokenStore.class)
    public class ResJwtTokenConfig {
    }
}
