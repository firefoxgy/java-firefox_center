package com.firefox.center.sys.modules.app.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.sys.common.base.BaseService;
import com.firefox.center.sys.modules.app.entity.OauthTenantApp;
import com.firefox.center.sys.modules.app.mapper.OauthTenantAppMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
@DS("open")
@RequiredArgsConstructor
public class OauthTenantAppService extends BaseService<OauthTenantAppMapper, OauthTenantApp> {

    private final RedisTemplate redisTemplate;

    public OauthTenantApp selectRecord(Integer tenantId, String appId) {
        return baseMapper.selectRecord(tenantId, appId);
    }

    public void saveAppTenant(String appId, String tenantIds) {
        String[] tenantIdArr=tenantIds.split(",");
        OauthTenantApp oauthTenantApp=null;
        for(String tenantId:tenantIdArr){
            oauthTenantApp=OauthTenantApp.builder()
                    .appId(appId)
                    .tenantId(Integer.valueOf(tenantId))
                    .status(1)
                    .build();
            baseMapper.insert(oauthTenantApp);
        }
    }

    public boolean updateRecord(Integer tenantId, String appId, Integer status) {
        return baseMapper.updateRecord(tenantId, appId, status)==1?true:false;
    }

}
