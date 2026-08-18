package com.firefox.center.config.feign.fallback;

import com.firefox.center.common.Record;
import com.firefox.center.config.feign.ConfigFeignService;
import com.firefox.center.config.feign.pojo.TConfigSmsDTO;
import com.firefox.center.config.feign.pojo.TTenantThirdConfDTO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * userService降级工场
 *
 * @Author: sujie
 * @date 2021/04/18
 */
@Slf4j
@Component
public class ConfigFeignServiceFallbackFactory implements FallbackFactory<ConfigFeignService> {
    @Override
    public ConfigFeignService create(Throwable throwable) {
        return new ConfigFeignService() {
            @Override
            public Record getConfs(String type) {
                log.error("通过appId查询应用异常:{}", type, throwable);
                return null;
            }

            @Override
            public TConfigSmsDTO getSms(String appId, Integer tenantId) {
                log.error("通过appId查询应用异常:{}", appId, throwable);
                return null;
            }

            @Override
            public TTenantThirdConfDTO getThirdConf(String appId, Integer tenantId, String thirdType) {
                log.error("通过thirdType查询应用异常:{}", appId+":"+tenantId+":"+thirdType, throwable);
                return null;
            }
        };
    }
}
