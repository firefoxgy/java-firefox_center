package com.firefox.center.sys.modules.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.sys.modules.app.entity.OauthAppCenter;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface OauthAppCenterMapper extends BaseMapper<OauthAppCenter> {

    @Select("select * from oauth_app_center where tenant_id=#{tenantId} and center_id=#{centerId} and app_id=#{appId} ")
    OauthAppCenter selectRecord(Integer tenantId, Integer centerId, String appId);

    @Update("update oauth_app_center set status=#{status} where tenant_id=#{tenantId} and center_id=#{centerId} and app_id=#{appId} ")
    int updateRecord(Integer tenantId, Integer centerId, String appId, Integer status);
}
