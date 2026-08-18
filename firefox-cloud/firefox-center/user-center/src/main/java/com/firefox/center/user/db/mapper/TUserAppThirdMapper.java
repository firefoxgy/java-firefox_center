package com.firefox.center.user.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.user.db.model.TUserAppThird;
import org.apache.ibatis.annotations.Update;

/**
 * 用户关联表dao
 * @author sujie
 */
public interface TUserAppThirdMapper extends BaseMapper<TUserAppThird> {

    @Update("delete from t_user_app_third where uid = #{uid} and sid = #{sid} and app_id = #{appId} and tenant_id = #{tenantId}")
    int deleteRecord(String appId, Integer tenantId, long uid, long sid, String authType);
}