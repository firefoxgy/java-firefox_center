package com.firefox.center.oauth.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.oauth.db.model.OauthTenant;
import com.firefox.center.common.model.OauthTenantPackage;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OauthTenantMapper extends BaseMapper<OauthTenant> {

    @Select("SELECT\n" + "\tcount( id ) \n" + "FROM\n" + "\toauth_tenant_package \n" + "WHERE\n"
        + "\ttenant_id = #{tenantId} \n" + "\tAND app_id = #{appId} \n" + "\tAND center_id = #{centerId} \n"
        + "\tAND STATUS = 1 \n" + "AND (( now() BETWEEN start_time AND end_time ) \n" + "\tOR (\n"
        + "\t\tend_time IS NULL \n" + "\t\tAND unix_timestamp(\n" + "\t\tnow()) >= UNIX_TIMESTAMP( start_time )))")
    int selectTenantPackageCountByCri(String appId, Integer tenantId, Integer centerId);

    @Select("SELECT\n"
        + "\ttp.id,tp.package_id,tp.tenant_id,tp.center_id,c.service,tp.start_time,tp.end_time,tp.app_id,tp.`status`,tp.user_limit\n"
        + "FROM\n" + "\toauth_tenant_package tp LEFT JOIN oauth_center c on tp.center_id=c.id\n" + "WHERE\n"
        + "\tapp_id = #{appId} \n" + "\tAND tp.STATUS = 1 \n" + "AND (( now() BETWEEN tp.start_time AND tp.end_time ) \n"
        + "\tOR (\n" + "\t\ttp.end_time IS NULL \n" + "\t\tAND unix_timestamp(\n"
        + "\t\tnow()) >= UNIX_TIMESTAMP( tp.start_time ))) ")
    List<OauthTenantPackage> selectTenantPackageByAppId(String appId);
}
