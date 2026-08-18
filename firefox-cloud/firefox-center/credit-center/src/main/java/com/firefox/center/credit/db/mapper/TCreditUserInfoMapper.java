package com.firefox.center.credit.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.credit.db.model.TCredit;
import com.firefox.center.credit.db.model.TCreditUserInfo;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表dao
 * @author sujie
 */
public interface TCreditUserInfoMapper extends BaseMapper<TCreditUserInfo> {

    @Select("select * from t_credit_userinfo where app_id=#{appId} and tenant_id=#{tenantId} and uid=#{uid} and type=#{type}")
    TCreditUserInfo selectRecord(String appId, int tenantId, long uid, String type);

}