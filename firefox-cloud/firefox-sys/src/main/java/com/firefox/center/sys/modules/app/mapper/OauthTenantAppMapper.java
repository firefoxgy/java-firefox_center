package com.firefox.center.sys.modules.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.sys.modules.app.entity.OauthTenantApp;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface OauthTenantAppMapper extends BaseMapper<OauthTenantApp> {

    @Select("select * from oauth_tenant_app where tenant_id=#{tenantId} and app_id=#{appId} ")
    OauthTenantApp selectRecord(Integer tenantId, String appId);

    @Update("update oauth_tenant_app set status=#{status} where tenant_id=#{tenantId} and app_id=#{appId} ")
    int updateRecord(Integer tenantId, String appId, Integer status);

}
