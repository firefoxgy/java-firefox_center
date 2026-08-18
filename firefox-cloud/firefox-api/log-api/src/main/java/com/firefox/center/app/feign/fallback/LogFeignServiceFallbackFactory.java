package com.firefox.center.app.feign.fallback;

import com.firefox.center.app.feign.LogFeignService;
import com.firefox.center.app.feign.pojo.TLogMailDTO;
import com.firefox.center.app.feign.pojo.TLogSmsDTO;
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
public class LogFeignServiceFallbackFactory implements FallbackFactory<LogFeignService> {
    @Override
    public LogFeignService create(Throwable throwable) {
        return new LogFeignService() {
            @Override
            public void saveSmsLog(TLogSmsDTO tLogSmsDTO) {
                log.error("保存短信日志异常:{}", tLogSmsDTO, throwable);
            }

            @Override
            public void saveMailLog(TLogMailDTO tLogMailDTO) {
                log.error("保存邮箱日志异常:{}", tLogMailDTO, throwable);
            }
        };
    }
}
