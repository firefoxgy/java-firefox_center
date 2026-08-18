package com.firefox.center.oauth.db.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.oauth.db.mapper.OauthCenterMapper;
import com.firefox.center.oauth.db.model.OauthCenter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 中心服务类
 */
@Service("auth_TMidCenterService")
@RequiredArgsConstructor
public class OauthCenterService extends BaseService<OauthCenterMapper, OauthCenter> {

    public List<OauthCenter> selectList(String appId, Integer tenantId){
        return baseMapper.selectList(appId, tenantId);
    }
}
