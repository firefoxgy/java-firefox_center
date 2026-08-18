package com.firefox.center.config.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.config.db.model.TConfigSms;
import org.apache.ibatis.annotations.Select;

public interface TConfigSmsMapper extends BaseMapper<TConfigSms> {

    @Select("select ts.* " +
            "from t_config_app_sms tas left join t_config_sms ts " +
            "on tas.app_id=#{appId} and tas.sms_id=ts.id")
    TConfigSms selectSmsByAppId(String appId);

    @Select("select ts.* " +
            "from t_config_app_tenant_sms tat left join t_config_sms ts " +
            "on tat.app_id=#{appId} and tat.tenant_id=#{tenantId} and tat.sms_id=ts.id")
    TConfigSms selectSmsByAppAndTenantId(String appId, Integer tenantId);
}