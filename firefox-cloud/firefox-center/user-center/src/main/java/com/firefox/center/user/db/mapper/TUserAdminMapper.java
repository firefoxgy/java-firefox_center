package com.firefox.center.user.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.user.db.model.TUserAdmin;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户表dao
 * @author sujie
 */
public interface TUserAdminMapper extends BaseMapper<TUserAdmin> {

    @Select("select * from t_user_admin where username = #{username}")
    TUserAdmin selectByUsername(String username);

    @Select("select * from t_user_admin where app_id = #{appId} and tenant_id = #{tenantId} and username = #{username}")
    TUserAdmin selectByUsernameAndAppId(String appId, Integer tenantId, String username);

    @Select("select * from t_user_admin where app_id = #{appId} and tenant_id = #{tenantId} and uid = #{uid}")
    TUserAdmin selectByUserId(String appId, Integer tenantId, long uid);

    @Select("select tua.*,ot.name tenant_name,ot.client_manager manager from t_user_admin tua left join oauth_tenant ot on tua.tenant_id=ot.id where tua.app_id = #{appId} and tua.tenant_id = #{tenantId} and tua.uid = #{uid}")
    TUserAdmin selectByUserId2(String appId, Integer tenantId, long uid);

    @Update({"UPDATE  t_user_admin_third ", "SET status = #{status} ", " WHERE uid=#{uid} and thirdid=#{wxOpenId}"})
    int updateStatusByUidAndWxopenId(long uid, String wxOpenId, int status);
}