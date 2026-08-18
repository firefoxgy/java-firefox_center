package com.firefox.center.user.feign;

import com.firefox.center.common.R;
import com.firefox.center.common.constants.ServiceNameConstants;
import com.firefox.center.user.feign.fallback.SmsServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: sujie
 */
@FeignClient(name = ServiceNameConstants.USER_SERVICE, fallbackFactory = SmsServiceFallbackFactory.class)
public interface SmsFeignService {

    /**
     * feign rpc访问远程/users-anon/login接口
     *
     * @param phone 手机号
     * @return
     */
    @GetMapping(value = "/feign/sendSmsCode.do")
    R sendCode(@RequestParam("appId") String appId,
                        @RequestParam("tenantId") Integer tenantId,
                        @RequestParam("phone") String phone);

    /**
     * 通过手机号查询用户、角色信息
     *
     * @param phone 手机号
     */
    @GetMapping(value = "/feign/checkSmsCode.do")
    R checkCode(@RequestParam("appId") String appId,
                @RequestParam("tenantId") Integer tenantId,
                @RequestParam("phone") String phone,
                @RequestParam("code") String code);
}
