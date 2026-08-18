package com.firefox.center.oauth.db.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.oauth.db.mapper.OauthTenantAppMapper;
import com.firefox.center.oauth.db.model.OauthTenantApp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("auth_TMidTenantAppService")
@RequiredArgsConstructor
public class OauthTenantAppAppService extends BaseService<OauthTenantAppMapper, OauthTenantApp> {

    public OauthTenantApp selectRecord(String appId, Integer tenantId){
        return baseMapper.selectRecord(appId, tenantId);
    }

    public int addWx(long uid, String wxOpenId, String appId){
        if(baseMapper.selectCountByWx(uid, wxOpenId, appId) <= 0)
            return baseMapper.insertWx(uid, wxOpenId, appId);
        else
            return -1;
    }


}
