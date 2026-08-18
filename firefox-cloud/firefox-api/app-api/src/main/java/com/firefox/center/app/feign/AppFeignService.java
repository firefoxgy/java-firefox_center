package com.firefox.center.app.feign;


import com.firefox.center.app.feign.fallback.AppServiceFallbackFactory;
import com.firefox.center.app.feign.pojo.OauthTenantDTO;
import com.firefox.center.common.constants.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: sujie
 */
@FeignClient(name = ServiceNameConstants.App_SERVICE, fallbackFactory = AppServiceFallbackFactory.class)
public interface AppFeignService {

    /**
     *
     * @param tenantId
     * @return
     */
    @GetMapping(value = "/feign/getTenant.do")
    OauthTenantDTO getTenant(@RequestParam("tenantId") Integer tenantId);

}
