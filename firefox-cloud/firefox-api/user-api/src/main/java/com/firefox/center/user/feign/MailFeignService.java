package com.firefox.center.user.feign;

import com.firefox.center.common.R;
import com.firefox.center.common.constants.ServiceNameConstants;
import com.firefox.center.user.feign.fallback.MailServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: sujie
 */
@FeignClient(name = ServiceNameConstants.USER_SERVICE, fallbackFactory = MailServiceFallbackFactory.class)
public interface MailFeignService {

    /**
     * feign rpc访问远程/users-anon/login接口
     *
     * @param mail 邮箱
     * @return
     */
    @GetMapping(value = "/feign/sendMailCode.do")
    void sendCode(@RequestParam("appId") String appId,
               @RequestParam("tenantId") Integer tenantId,
               @RequestParam("mail") String mail);

    /**
     * 通过手机号查询用户、角色信息
     *
     * @param mail 邮箱
     */
    @GetMapping(value = "/feign/checkMailCode.do")
    R checkCode(@RequestParam("appId") String appId,
                @RequestParam("tenantId") Integer tenantId,
                @RequestParam("mail") String mail,
                @RequestParam("code") String code);
}
