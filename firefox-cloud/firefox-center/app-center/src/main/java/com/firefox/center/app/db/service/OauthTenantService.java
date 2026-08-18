package com.firefox.center.app.db.service;

import com.firefox.center.app.db.mapper.OauthTenantMapper;
import com.firefox.center.app.db.model.OauthTenant;
import com.firefox.center.db.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("auth_TSaasTenantService")
@RequiredArgsConstructor
public class OauthTenantService extends BaseService<OauthTenantMapper, OauthTenant> {

}
