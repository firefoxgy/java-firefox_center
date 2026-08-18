package com.firefox.center.config.feign;

import com.firefox.center.common.Record;
import com.firefox.center.common.constants.ServiceNameConstants;
import com.firefox.center.config.feign.fallback.ConfigFeignServiceFallbackFactory;
import com.firefox.center.config.feign.pojo.TConfigSmsDTO;
import com.firefox.center.config.feign.pojo.TTenantThirdConfDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ServiceNameConstants.CONFIG_SERVICE, fallbackFactory = ConfigFeignServiceFallbackFactory.class)
public interface ConfigFeignService {

    /**
     *  根据类型获取配置项
     */
    @PostMapping("/feign/getConfs.do")
    Record getConfs(@RequestParam(name = "type") String type);

    /**
     *  获取应用短信模板
     */
    @PostMapping("/feign/getSms.do")
    TConfigSmsDTO getSms(@RequestParam(name = "appId") String appId,
                         @RequestParam(name = "tenantId") Integer tenantId);

    /**
     *  获取租户第三方登录配置信息
     */
    @PostMapping("/feign/getThirdConf.do")
    TTenantThirdConfDTO getThirdConf(@RequestParam(name = "appId") String appId,
                                     @RequestParam(name = "tenantId") Integer tenantId,
                                     @RequestParam(name = "thirdType") String thirdType);

}
