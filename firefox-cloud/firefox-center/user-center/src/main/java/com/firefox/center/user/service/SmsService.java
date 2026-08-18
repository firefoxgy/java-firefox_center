package com.firefox.center.user.service;

import cn.hutool.core.thread.ThreadUtil;
import com.firefox.center.common.R;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.kit.Assert;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.config.feign.ConfigFeignService;
import com.firefox.center.config.feign.pojo.TConfigSmsDTO;
import com.firefox.center.app.feign.LogFeignService;
import com.firefox.center.app.feign.pojo.TLogSmsDTO;
import com.firefox.center.user.pojo.sms.dto.CheckCodeDTO;
import com.firefox.center.user.pojo.sms.dto.CodeSendDTO;
import com.firefox.center.common.sms.pojo.SMSProperty;
import com.firefox.center.common.sms.service.SMSCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 公司表 服务类
 */
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SMSCommonService sMSCommonService;
    private final ConfigFeignService configFeignService;
    private final LogFeignService logFeignService;

    public R<?> sendCode(CodeSendDTO sendDTO){
        String appId=sendDTO.getArgs().getAppid();
        Integer tenantId=sendDTO.getArgs().getTenantid();
        String phone=sendDTO.getArgs().getPhone();

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
        R result=sMSCommonService.sendCode(appId, tenantId, sMSProperty, phone, code);
        saveLog(appId, tenantId, phone, code, TConfigSms, result);
        return result;
    }

    public R<?> checkCode(CheckCodeDTO checkDTO){
        String appId=checkDTO.getAppid();
        Integer tenantId=checkDTO.getTenantid();
        String phone=checkDTO.getPhone();
        String code=checkDTO.getCode();
        return sMSCommonService.checkCode(appId, tenantId, phone, code);
    }

    public R<?> cacheCode(){
        String appId="xr_02";
        String tenantId="10000";
        String phone="15887253628";
        String code="123321";
        sMSCommonService.cacheCode(appId, tenantId, phone, code, 1000000l);
        return R.ok();
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
