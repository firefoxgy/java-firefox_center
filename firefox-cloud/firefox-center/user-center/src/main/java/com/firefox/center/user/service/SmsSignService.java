package com.firefox.center.user.service;

import cn.hutool.crypto.SecureUtil;
import com.firefox.center.user.pojo.sms.dto.CodeSendDTO;
import com.firefox.center.user.pojo.sms.dto.MailSendDTO;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
public class SmsSignService {

    public boolean isCodeMatch(CodeSendDTO sendDTO){
        String appId=sendDTO.getArgs().getAppid();
        Integer tenantId=sendDTO.getArgs().getTenantid();
        String phone=sendDTO.getArgs().getPhone();
        long ts=sendDTO.getArgs().getTs();
        String sign=sendDTO.getSign();

        String signStr="appid"+appId+"tenantid"+tenantId+"phone"+phone+"ts"+ts+"keykpsms";
        String md5Sign= SecureUtil.md5(signStr);
        return md5Sign.equals(sign);
    }

    public boolean isMailMatch(MailSendDTO sendDTO){
        String appId=sendDTO.getArgs().getAppid();
        Integer tenantId=sendDTO.getArgs().getTenantid();
        String mail=sendDTO.getArgs().getMail();
        long ts=sendDTO.getArgs().getTs();
        String sign=sendDTO.getSign();

        String signStr="appid"+appId+"tenantid"+tenantId+"mail"+mail+"ts"+ts+"keykpmail";
        String md5Sign= SecureUtil.md5(signStr);
        return md5Sign.equals(sign);
    }

}
