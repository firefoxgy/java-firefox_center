package com.firefox.center.user.feign;

import cn.hutool.core.thread.ThreadUtil;
import com.firefox.center.common.R;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.kit.Assert;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.config.feign.ConfigFeignService;
import com.firefox.center.config.feign.pojo.TConfigSmsDTO;
import com.firefox.center.app.feign.LogFeignService;
import com.firefox.center.app.feign.pojo.TLogSmsDTO;
import com.firefox.center.common.sms.pojo.SMSProperty;
import com.firefox.center.common.sms.service.SMSCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/4/27 16:30
 */
@RestController
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsFeignService {

    private final SMSCommonService sMSService;
    private final ConfigFeignService configFeignService;
    private final LogFeignService logFeignService;

    @Override
    public R<?> sendCode(String appId, Integer tenantId, String phone) {
        TConfigSmsDTO TConfigSms=configFeignService.getSms(appId, tenantId);
        Assert.notNull(TConfigSms, CodeEnum.SMS_APP_TEMPLATE_NULL);
        SMSProperty sMSProperty=SMSProperty.builder()
                .url(TConfigSms.getUrl())
                .appKey(TConfigSms.getAppKey())
                .appSecret(TConfigSms.getAppSecret())
                .module(TConfigSms.getModule())
                .type(TConfigSms.getType())
                .signName(TConfigSms.getSignName())
                .templateCode(TConfigSms.getCode())
                .expire(TConfigSms.getExpire().longValue())
                .build();
        String code= StrKit.randomNum(6);
        R result=sMSService.sendCode(appId, tenantId, sMSProperty, phone, code);
        saveLog(appId, tenantId, phone, code, TConfigSms, result);
        return result;
    }

    @Override
    public R<?> checkCode(String appId, Integer tenantId, String phone, String code) {
        return sMSService.checkCode(appId, tenantId, phone, code);
    }

    public void saveLog(String appId, Integer tenantId, String phone, String code, TConfigSmsDTO TConfigSms, R result){
        TLogSmsDTO tLogSmsDTO=TLogSmsDTO.builder()
                .id(StrKit.getId())
                .appId(appId)
                .tenantId(tenantId)
                .phone(phone)
                .code(code)
                .smsTemplate(TConfigSms.getCode())
                .expire(TConfigSms.getExpire().longValue())
                .createTime(new Date())
                .build();
        if(result.isSuccess()){
            tLogSmsDTO.setStatus(1);
        }else{
            tLogSmsDTO.setStatus(-1);
        }
        ThreadUtil.execAsync(() ->logFeignService.saveSmsLog(tLogSmsDTO));
    }
}
