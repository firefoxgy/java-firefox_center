package com.firefox.center.oauth.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.oauth.db.model.OauthTenantApp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface OauthTenantAppMapper extends BaseMapper<OauthTenantApp> {

    @Select("select * from oauth_tenant_app where app_id = #{appId} and tenant_id = #{tenantId}")
    OauthTenantApp selectRecord(String appId, Integer tenantId);

    @Insert({ "insert into t_user_admin_third(uid, login_type, thirdid,app_id, create_time) values(#{uid}, 'wx', #{wxOpenId}, #{appId}, now())" })
    int insertWx(long uid, String wxOpenId, String appId);

    @Select("select count(id) from t_user_admin_third where app_id = #{appId} and uid = #{uid} and thirdid=#{wxOpenId}")
    int selectCountByWx(long uid, String wxOpenId, String appId);

}
