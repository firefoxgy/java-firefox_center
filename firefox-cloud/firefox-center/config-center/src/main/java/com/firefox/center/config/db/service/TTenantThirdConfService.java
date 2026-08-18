package com.firefox.center.config.db.service;

import com.firefox.center.config.db.mapper.TTenantThirdConfMapper;
import com.firefox.center.config.db.model.TTenantThirdConf;
import com.firefox.center.db.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service()
@RequiredArgsConstructor
public class TTenantThirdConfService extends BaseService<TTenantThirdConfMapper, TTenantThirdConf> {

    public TTenantThirdConf selectRecord(String appId, Integer tenantId, String thirdType) {
        return baseMapper.selectRecord(appId, tenantId, thirdType);
    }

}
