package com.firefox.center.user.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.user.db.model.TUserThird;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户表dao
 * @author sujie
 */
public interface TUserThirdMapper extends BaseMapper<TUserThird> {

    @Select("select tut.id,tut.sid,tut.login_type,tut.externalappid,tut.thirdid,tut.third_union_id,tut.nickname,tut.email,tut.figureurl,tut.gender,tut.country,tut.province,tut.city,tut.status,tut.reg_from,tut.version,tut.app_id,tut.tenant_id,tut.create_time,tuat1.uid,tua.phone,tua.open_id, " +
            "group_concat(tuat2.sid) sids " +
            "from t_user_third tut " +
            "left join t_user_app_third tuat1 on tut.app_id=tuat1.app_id and tut.tenant_id=tuat1.tenant_id and tut.sid=tuat1.sid " +
            "left join t_user_app_third tuat2 on tuat1.app_id=tuat2.app_id and tuat1.tenant_id=tuat2.tenant_id  and tuat1.uid=tuat2.uid " +
            "left join t_user_app tua on tuat1.app_id=tua.app_id and tuat1.tenant_id=tua.tenant_id and tuat1.uid=tua.uid " +
            "where tut.id = #{id} " +
            "group by tut.id,tut.sid,tut.login_type,tut.externalappid,tut.thirdid,tut.third_union_id,tut.nickname,tut.email,tut.figureurl,tut.gender,tut.country,tut.province,tut.city,tut.status,tut.reg_from,tut.version,tut.app_id,tut.tenant_id,tut.create_time,tuat1.uid,tua.phone,tua.open_id")
    TUserThird selectRecordById(long id);

    @Select("select tut.id,tut.sid,tut.login_type,tut.externalappid,tut.thirdid,tut.third_union_id,tut.nickname,tut.email,tut.figureurl,tut.gender,tut.country,tut.province,tut.city,tut.status,tut.reg_from,tut.version,tut.app_id,tut.tenant_id,tut.create_time,tuat1.uid,tua.phone,tua.open_id, " +
            "group_concat(tuat2.sid) sids " +
            "from t_user_third tut " +
            "left join t_user_app_third tuat1 on tut.app_id=tuat1.app_id and tut.tenant_id=tuat1.tenant_id and tut.sid=tuat1.sid " +
            "left join t_user_app_third tuat2 on tuat1.app_id=tuat2.app_id and tuat1.tenant_id=tuat2.tenant_id  and tuat1.uid=tuat2.uid " +
            "left join t_user_app tua on tuat1.app_id=tua.app_id and tuat1.tenant_id=tua.tenant_id and tuat1.uid=tua.uid " +
            "where tut.app_id = #{appId} and tut.tenant_id = #{tenantId} and tut.sid = #{sid} " +
            "group by tut.id,tut.sid,tut.login_type,tut.externalappid,tut.thirdid,tut.third_union_id,tut.nickname,tut.email,tut.figureurl,tut.gender,tut.country,tut.province,tut.city,tut.status,tut.reg_from,tut.version,tut.app_id,tut.tenant_id,tut.create_time,tuat1.uid,tua.phone,tua.open_id")
    TUserThird selectRecordBySId(String appId, int tenantId, long sid);

    @Select("select tut.id,tut.sid,tut.login_type,tut.externalappid,tut.thirdid,tut.third_union_id,tut.nickname,tut.email,tut.figureurl,tut.gender,tut.country,tut.province,tut.city,tut.status,tut.reg_from,tut.version,tut.app_id,tut.tenant_id,tut.create_time,tuat1.uid,tua.phone,tua.open_id, " +
            "group_concat(tuat2.sid) sids " +
            "from t_user_third tut " +
            "left join t_user_app_third tuat1 on tut.app_id=tuat1.app_id and tut.tenant_id=tuat1.tenant_id and tut.sid=tuat1.sid " +
            "left join t_user_app_third tuat2 on tuat1.app_id=tuat2.app_id and tuat1.tenant_id=tuat2.tenant_id  and tuat1.uid=tuat2.uid " +
            "left join t_user_app tua on tuat1.app_id=tua.app_id and tuat1.tenant_id=tua.tenant_id and tuat1.uid=tua.uid " +
            "where tut.app_id = #{appId} and tut.tenant_id = #{tenantId} and tut.login_type = #{loginType} and tut.third_union_id = #{unionId} " +
            "group by tut.id,tut.sid,tut.login_type,tut.externalappid,tut.thirdid,tut.third_union_id,tut.nickname,tut.email,tut.figureurl,tut.gender,tut.country,tut.province,tut.city,tut.status,tut.reg_from,tut.version,tut.app_id,tut.tenant_id,tut.create_time,tuat1.uid,tua.phone,tua.open_id")
    TUserThird selectRecordByUnionId(String appId, int tenantId, String loginType, String unionId);

    @Select("select tut.id,tut.sid,tut.login_type,tut.externalappid,tut.thirdid,tut.third_union_id,tut.nickname,tut.email,tut.figureurl,tut.gender,tut.country,tut.province,tut.city,tut.status,tut.reg_from,tut.version,tut.app_id,tut.tenant_id,tut.create_time,tuat1.uid,tua.phone,tua.open_id, " +
            "group_concat(tuat2.sid) sids " +
            "from t_user_third tut " +
            "left join t_user_app_third tuat1 on tut.app_id=tuat1.app_id and tut.tenant_id=tuat1.tenant_id and tut.sid=tuat1.sid " +
            "left join t_user_app_third tuat2 on tuat1.app_id=tuat2.app_id and tuat1.tenant_id=tuat2.tenant_id  and tuat1.uid=tuat2.uid " +
            "left join t_user_app tua on tuat1.app_id=tua.app_id and tuat1.tenant_id=tua.tenant_id and tuat1.uid=tua.uid " +
            "where tut.app_id = #{appId} and tut.tenant_id = #{tenantId} and tut.thirdid = #{thirdid} " +
            "group by tut.id,tut.sid,tut.login_type,tut.externalappid,tut.thirdid,tut.third_union_id,tut.nickname,tut.email,tut.figureurl,tut.gender,tut.country,tut.province,tut.city,tut.status,tut.reg_from,tut.version,tut.app_id,tut.tenant_id,tut.create_time,tuat1.uid,tua.phone,tua.open_id")
    TUserThird selectRecordByThirdId1(String appId, int tenantId, String thirdid);

    @Select("select tut.id,tut.sid,tut.login_type,tut.externalappid,tut.thirdid,tut.third_union_id,tut.nickname,tut.email,tut.figureurl,tut.gender,tut.country,tut.province,tut.city,tut.status,tut.reg_from,tut.version,tut.app_id,tut.tenant_id,tut.create_time,tuat1.uid,tua.phone,tua.open_id, " +
            "group_concat(tuat2.sid) sids " +
            "from t_user_third tut " +
            "left join t_user_app_third tuat1 on tut.app_id=tuat1.app_id and tut.tenant_id=tuat1.tenant_id and tut.sid=tuat1.sid " +
            "left join t_user_app_third tuat2 on tuat1.app_id=tuat2.app_id and tuat1.tenant_id=tuat2.tenant_id  and tuat1.uid=tuat2.uid " +
            "left join t_user_app tua on tuat1.app_id=tua.app_id and tuat1.tenant_id=tua.tenant_id and tuat1.uid=tua.uid " +
            "where tut.app_id = #{appId} and tut.tenant_id = #{tenantId} and tut.login_type = #{loginType} and tut.thirdid = #{thirdid} " +
            "group by tut.id,tut.sid,tut.login_type,tut.externalappid,tut.thirdid,tut.third_union_id,tut.nickname,tut.email,tut.figureurl,tut.gender,tut.country,tut.province,tut.city,tut.status,tut.reg_from,tut.version,tut.app_id,tut.tenant_id,tut.create_time,tuat1.uid,tua.phone,tua.open_id")
    TUserThird selectRecordByThirdId2(String appId, int tenantId, String loginType, String thirdid);

    @Select("select tut.id,tut.sid,tut.login_type,tut.nickname,tut.figureurl from t_user_app_third tuat " +
            "left join t_user_third tut on tuat.app_id=tut.app_id and tuat.tenant_id=tut.tenant_id and tuat.sid=tut.sid " +
            "where tuat.uid=#{uid} and tut.app_id = #{appId} and tut.tenant_id = #{tenantId}")
    List<TUserThird> selectBindList(String appId, int tenantId, long uid);

    @Update("delete from t_user_third where app_id = #{appId} and tenant_id = #{tenantId} and login_type = #{loginType} and sid = #{sid}")
    int deleteRecord(String appId, int tenantId, String loginType, long sid);

}