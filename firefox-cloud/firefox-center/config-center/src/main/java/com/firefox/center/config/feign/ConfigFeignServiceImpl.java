package com.firefox.center.config.feign;

import com.firefox.center.config.ConfigConstants;
import com.firefox.center.config.db.model.TTenantThirdConf;
import com.firefox.center.config.db.service.TTenantThirdConfService;
import com.firefox.center.config.feign.pojo.TConfigSmsDTO;
import com.firefox.center.config.db.model.TConfigSms;
import com.firefox.center.config.db.service.TConfigAllService;
import com.firefox.center.config.db.service.TConfigSmsService;
import com.firefox.center.common.Record;
import com.firefox.center.config.feign.pojo.TTenantThirdConfDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ConfigFeignServiceImpl implements ConfigFeignService {

    private final TConfigAllService tConfigAllService;
    private final TConfigSmsService tConfigSmsService;
    private final TTenantThirdConfService tTenantThirdConfService;

    @Override
    public Record getConfs(String type) {
        return tConfigAllService.getConfByType(type);
    }

    /** 
     * 获取短信模板
     * @CreateTime 2021/2/10 14:56
     * @param 
     * @return 
     */
    @Override
    public TConfigSmsDTO getSms(String appId, Integer tenantId) {
        TConfigSms tConfigSms=tConfigSmsService.getSms(appId, tenantId);
        if(tConfigSms==null){
            return null;
        }
        Record smsRecord=tConfigAllService.getConfByType(ConfigConstants.Sms.TYPE);
        String url="";
        int length=6;
        if(smsRecord.get(ConfigConstants.Sms.SMS_SEND_API)!=null){
            url=smsRecord.getStr(ConfigConstants.Sms.SMS_SEND_API);
        }
        if(smsRecord.get(ConfigConstants.Sms.SMS_CODE_NUM)!=null){
            length=smsRecord.getInt(ConfigConstants.Sms.SMS_CODE_NUM);
        }
        TConfigSmsDTO tConfigSmsDTO =new TConfigSmsDTO();
        BeanUtils.copyProperties(tConfigSms, tConfigSmsDTO);
        tConfigSmsDTO.setUrl(url);
        tConfigSmsDTO.setLength(length);
        return tConfigSmsDTO;
    }

    @Override
    public TTenantThirdConfDTO getThirdConf(String appId, Integer tenantId, String thirdType) {
        TTenantThirdConf tTenantThirdConf=tTenantThirdConfService.selectRecord(appId, tenantId, thirdType);
        TTenantThirdConfDTO tTenantThirdConfDTO =new TTenantThirdConfDTO();
        BeanUtils.copyProperties(tTenantThirdConf, tTenantThirdConfDTO);
        return tTenantThirdConfDTO;
    }

}
