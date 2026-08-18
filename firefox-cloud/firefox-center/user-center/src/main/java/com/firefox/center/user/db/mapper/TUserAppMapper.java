package com.firefox.center.user.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.user.db.model.TUserApp;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表dao
 * @author sujie
 */
public interface TUserAppMapper extends BaseMapper<TUserApp> {

    @Select("select tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time,group_concat(tuat.sid) sids " +
            "from t_user_app tua " +
            "left join t_user_app_third tuat on tua.app_id=tuat.app_id and tua.tenant_id=tuat.tenant_id and tua.uid=tuat.uid " +
            "where tua.id = #{id} " +
            "group by tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time")
    TUserApp selectById(long id);

    @Select("select tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time,group_concat(tuat.sid) sids " +
            "from t_user_app tua " +
            "left join t_user_app_third tuat on tua.app_id=tuat.app_id and tua.tenant_id=tuat.tenant_id and tua.uid=tuat.uid " +
            "where tua.app_id = #{appId} and tua.tenant_id = #{tenantId} and tua.uid = #{uid} " +
            "group by tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time")
    TUserApp selectByUid(String appId, int tenantId, long uid);

    @Select("select tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time,group_concat(tuat.sid) sids " +
            "from t_user_app tua " +
            "left join t_user_app_third tuat on tua.app_id=tuat.app_id and tua.tenant_id=tuat.tenant_id and tua.uid=tuat.uid " +
            "where tua.app_id = #{appId} and tua.tenant_id = #{tenantId} and tua.phone = #{phone} " +
            "group by tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time")
    TUserApp selectByPhone(String appId, int tenantId, String phone);

    @Select("select tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time,group_concat(tuat.sid) sids " +
            "from t_user_app tua " +
            "left join t_user_app_third tuat on tua.app_id=tuat.app_id and tua.tenant_id=tuat.tenant_id and tua.uid=tuat.uid " +
            "where tua.app_id = #{appId} and tua.tenant_id = #{tenantId} and tua.username = #{username} " +
            "group by tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time")
    TUserApp selectByUsername(String appId, int tenantId, String username);


    @Select("select tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time,group_concat(tuat.sid) sids " +
            "from t_user_app tua " +
            "left join t_user_app_third tuat on tua.app_id=tuat.app_id and tua.tenant_id=tuat.tenant_id and tua.uid=tuat.uid " +
            "where tua.app_id = #{appId} and tua.tenant_id = #{tenantId} and tua.email = #{email} " +
            "group by tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time")
    TUserApp selectByEmail(String appId, int tenantId, String email);

    @Select("select tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time,group_concat(tuat.sid) sids " +
            "from t_user_app tua " +
            "left join t_user_app_third tuat on tua.app_id=tuat.app_id and tua.tenant_id=tuat.tenant_id and tua.uid=tuat.uid " +
            "where tua.app_id = #{appId} and tua.tenant_id = #{tenantId} and tua.open_id = #{openId} " +
            "group by tua.id,tua.open_id,tua.uid,tua.username,tua.phone,tua.email,tua.password,tua.password02,tua.salt,tua.pwd_encrypt,tua.nickname,tua.header_img,tua.gender,tua.status,tua.reg_from,tua.version,tua.app_id,tua.tenant_id,tua.source,tua.create_time")
    TUserApp selectByOpenId(String appId, int tenantId, String openId);

    @Select("select * from t_user_app where uid=#{uid} ")
    TUserApp selectByuId(long uid);

}