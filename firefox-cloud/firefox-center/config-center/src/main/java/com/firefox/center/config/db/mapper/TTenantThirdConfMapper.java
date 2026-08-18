package com.firefox.center.config.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.config.db.model.TTenantThirdConf;
import org.apache.ibatis.annotations.Select;

public interface TTenantThirdConfMapper extends BaseMapper<TTenantThirdConf> {

    @Select("select * from t_tenant_third_conf where app_id=#{appId} and tenant_id=#{tenantId} and third_type=#{thirdType}")
    TTenantThirdConf selectRecord(String appId, Integer tenantId, String thirdType);
}