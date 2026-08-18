package com.firefox.center.dts.db.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.dts.db.user.model.TUserDtsLog;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表dao
 * @author sujie
 */
public interface TUserDtsLogMapper extends BaseMapper<TUserDtsLog> {

    @Select("select * from t_user_dts_log where app_id=#{appId} and tenant_id=#{tenantId}")
    TUserDtsLog selectRecord(String appId, int tenantId);
}