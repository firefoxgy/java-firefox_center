package com.firefox.center.app.feign;

import com.firefox.center.app.db.model.TLogMail;
import com.firefox.center.app.db.model.TLogSms;
import com.firefox.center.app.db.service.TLogMailService;
import com.firefox.center.app.db.service.TLogSmsService;
import com.firefox.center.app.feign.pojo.TLogMailDTO;
import com.firefox.center.app.feign.pojo.TLogSmsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogFeignServiceImpl implements LogFeignService {

    private final TLogMailService tLogMailService;
    private final TLogSmsService tLogSmsService;

    @Override
    public void saveSmsLog(TLogSmsDTO tLogSmsDTO) {
        TLogSms tLogSms=new TLogSms();
        BeanUtils.copyProperties(tLogSmsDTO, tLogSms);
        tLogSmsService.save(tLogSms);
    }

    @Override
    public void saveMailLog(TLogMailDTO tLogMailDTO) {
        TLogMail tLogMail=new TLogMail();
        BeanUtils.copyProperties(tLogMailDTO, tLogMail);
        tLogMailService.save(tLogMail);
    }
}
