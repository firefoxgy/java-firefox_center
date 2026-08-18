package com.firefox.center.app.feign;

import com.firefox.center.app.db.model.OauthTenant;
import com.firefox.center.app.db.service.OauthTenantService;
import com.firefox.center.app.feign.pojo.OauthTenantDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppFeignServiceImpl implements AppFeignService {

    private final OauthTenantService oauthTenantService;

    @Override
    public OauthTenantDTO getTenant(Integer tenantId) {
        OauthTenant oauthTenant=oauthTenantService.getById(tenantId);
        if(oauthTenant==null){
            return null;
        }
        OauthTenantDTO oauthTenantDTO = new OauthTenantDTO();
        BeanUtils.copyProperties(oauthTenant, oauthTenantDTO);
        return oauthTenantDTO;
    }
}
