package com.firefox.center.app.feign;


import com.firefox.center.common.constants.ServiceNameConstants;
import com.firefox.center.app.feign.fallback.LogFeignServiceFallbackFactory;
import com.firefox.center.app.feign.pojo.TLogMailDTO;
import com.firefox.center.app.feign.pojo.TLogSmsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ServiceNameConstants.LOG_SERVICE, fallbackFactory = LogFeignServiceFallbackFactory.class)
public interface LogFeignService {

    /**
     * 保存短信日志
     * @author sujie
     * @param tLogSmsDTO
     * @return
     */
    @PostMapping("/feign/saveSmsLog.do")
    void saveSmsLog(@RequestBody TLogSmsDTO tLogSmsDTO);

    @PostMapping("/feign/saveMailLog.do")
    void saveMailLog(@RequestBody TLogMailDTO tLogMailDTO);

}
