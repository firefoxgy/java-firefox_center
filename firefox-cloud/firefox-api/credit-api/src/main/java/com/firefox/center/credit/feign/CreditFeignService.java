package com.firefox.center.credit.feign;

import com.firefox.center.common.constants.ServiceNameConstants;
import com.firefox.center.credit.feign.fallback.CreditServiceFallbackFactory;
import com.firefox.center.credit.feign.pojo.FeignTCredit;
import com.firefox.center.credit.feign.pojo.FeignTCreditLog;
import com.firefox.center.credit.feign.pojo.FeignTUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: sujie
 */
@FeignClient(name = ServiceNameConstants.CREDIT_SERVICE, fallbackFactory = CreditServiceFallbackFactory.class)
public interface CreditFeignService {

    /**
     *
     * @param feignTCredit
     * @return
     */
    @PostMapping(value = "/feign/updateUid.do")
    boolean bindUid(@RequestBody FeignTCredit feignTCredit);

    /**
     *
     * @param feignTCredit
     * @return
     */
    @PostMapping(value = "/feign/unbindUid.do")
    boolean unbindUid(@RequestBody FeignTCredit feignTCredit);

    /**
     *
     * @param feignTCredit
     * @return
     */
    @PostMapping(value = "/feign/saveCredit.do")
    boolean saveCredit(@RequestBody FeignTCredit feignTCredit);

    /**
     *
     * @param feignTCreditLog
     */
    @PostMapping(value = "/feign/saveCreditLog.do")
    boolean saveCreditLog(@RequestBody FeignTCreditLog feignTCreditLog);

    /**
     *
     * @param feignTUserInfo
     */
    @PostMapping(value = "/feign/saveUserInfo.do")
    boolean saveUserInfo(@RequestBody FeignTUserInfo feignTUserInfo);

}
