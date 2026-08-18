package com.firefox.center.config.db.service;

import com.firefox.center.config.db.mapper.TConfigSmsMapper;
import com.firefox.center.config.db.model.TConfigSms;
import com.firefox.center.db.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service()
@RequiredArgsConstructor
public class TConfigSmsService extends BaseService<TConfigSmsMapper, TConfigSms> {

    public TConfigSms getSms(String appId, Integer tenantId) {
        TConfigSms tConfigSmsPO=baseMapper.selectSmsByAppAndTenantId(appId, tenantId);
        if(null==tConfigSmsPO){
            tConfigSmsPO=baseMapper.selectSmsByAppId(appId);
        }
        return tConfigSmsPO;
    }

}
