package com.firefox.center.user.feign.fallback;

import com.firefox.center.common.R;
import com.firefox.center.user.feign.SmsFeignService;
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
public class SmsServiceFallbackFactory implements FallbackFactory<SmsFeignService> {
    @Override
    public SmsFeignService create(Throwable throwable) {
        return new SmsFeignService() {


            @Override
            public R sendCode(String appId, Integer tenantId, String phone) {
                log.error("通过phone发送手机短信异常:{}", appId+":"+tenantId+":"+phone, throwable);
                return R.error();
            }

            @Override
            public R checkCode(String appId, Integer tenantId, String phone, String code) {
                log.error("通过phone验证短信验证码异常:{}", appId+":"+tenantId+":"+phone+":"+code, throwable);
                return R.error();
            }
        };
    }
}
