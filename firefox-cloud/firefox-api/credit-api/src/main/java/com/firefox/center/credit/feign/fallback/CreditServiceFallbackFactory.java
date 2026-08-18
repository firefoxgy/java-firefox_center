package com.firefox.center.credit.feign.fallback;

import com.firefox.center.credit.feign.CreditFeignService;
import com.firefox.center.credit.feign.pojo.FeignTCredit;
import com.firefox.center.credit.feign.pojo.FeignTCreditLog;
import com.firefox.center.credit.feign.pojo.FeignTUserInfo;
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
public class CreditServiceFallbackFactory implements FallbackFactory<CreditFeignService> {
    @Override
    public CreditFeignService create(Throwable throwable) {
        return new CreditFeignService() {

            @Override
            public boolean bindUid(FeignTCredit feignTCredit) {
                return false;
            }

            @Override
            public boolean unbindUid(FeignTCredit feignTCredit) {
                return false;
            }

            @Override
            public boolean saveCredit(FeignTCredit feignTCredit) {
                return false;
            }

            @Override
            public boolean saveCreditLog(FeignTCreditLog feignTCreditLog) {
                return false;
            }

            @Override
            public boolean saveUserInfo(FeignTUserInfo feignTUserInfo) {
                return false;
            }
        };
    }
}
