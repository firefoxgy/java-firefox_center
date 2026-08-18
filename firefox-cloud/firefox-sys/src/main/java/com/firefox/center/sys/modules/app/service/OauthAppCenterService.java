package com.firefox.center.sys.modules.app.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.sys.common.base.BaseService;
import com.firefox.center.sys.modules.app.entity.OauthAppCenter;
import com.firefox.center.sys.modules.app.mapper.OauthAppCenterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
@DS("open")
@RequiredArgsConstructor
public class OauthAppCenterService extends BaseService<OauthAppCenterMapper, OauthAppCenter> {

    public OauthAppCenter selectRecord(Integer tenantId, Integer centerId, String appId) {
        return baseMapper.selectRecord(tenantId, centerId, appId);
    }

    public void saveAppTenantService(String appId, Integer tenantId, String serviceIds) {
        String[] serviceIdArr=serviceIds.split(",");
        OauthAppCenter oauthAppCenter=null;
        for(String serviceId:serviceIdArr){
            oauthAppCenter=OauthAppCenter.builder()
                    .appId(appId)
                    .tenantId(Integer.valueOf(tenantId))
                    .centerId(Integer.valueOf(serviceId))
                    .status(1)
                    .build();
            baseMapper.insert(oauthAppCenter);
        }
    }

    public boolean updateRecord(Integer tenantId,Integer centerId, String appId, Integer status) {
        return baseMapper.updateRecord(tenantId, centerId, appId, status)==1?true:false;
    }

}
