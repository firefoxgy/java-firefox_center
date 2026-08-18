package com.firefox.center.config;

import com.firefox.center.common.config.DefaultAsycTaskConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: sujie
 * 线程池配置、启用异步
 * @Async quartz 需要使用
 */
@Configuration
public class AsycTaskExecutorConfig extends DefaultAsycTaskConfig {

}
