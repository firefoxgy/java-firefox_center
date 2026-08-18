package com.firefox.center.common.redis.constant;

/**
 * redis 工具常量
 *
 * @Author: sujie
 * @date 2021/04/21 11:59
 */
public class RedisConstant {
    private RedisConstant() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * single Redis
     */
    public final static int SINGLE = 1 ;

    /**
     * Redis cluster
     */
    public final static int CLUSTER = 2 ;

    public static final String SEPARATOR="::";
    public static final String PREFIX_AUTH="auth"+SEPARATOR+"path"+SEPARATOR;
}
