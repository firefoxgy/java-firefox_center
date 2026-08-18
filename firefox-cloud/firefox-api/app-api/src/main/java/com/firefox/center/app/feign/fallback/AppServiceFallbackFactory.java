package com.firefox.center.app.feign.fallback;

import com.firefox.center.app.feign.AppFeignService;
import com.firefox.center.app.feign.pojo.OauthTenantDTO;
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
public class AppServiceFallbackFactory implements FallbackFactory<AppFeignService> {
    @Override
    public AppFeignService create(Throwable throwable) {
        return new AppFeignService() {

            @Override
            public OauthTenantDTO getTenant(Integer tenantId) {
                return null;
            }

        };
    }
}
