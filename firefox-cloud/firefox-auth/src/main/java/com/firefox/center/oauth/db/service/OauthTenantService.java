package com.firefox.center.oauth.db.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.oauth.db.mapper.OauthTenantMapper;
import com.firefox.center.oauth.db.model.OauthTenant;
import com.firefox.center.common.model.OauthTenantPackage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公司表 服务类
 */
@Service("auth_TSaasTenantService")
@RequiredArgsConstructor
public class OauthTenantService extends BaseService<OauthTenantMapper, OauthTenant> {
    public int findTenantPackageCountByCri(String appId, Integer tenantId, Integer centerId) {
        return baseMapper.selectTenantPackageCountByCri(appId,tenantId, centerId);
    }

    public List<OauthTenantPackage> selectTenantPackageByAppId(String appId){
        return baseMapper.selectTenantPackageByAppId(appId);
    }


}
