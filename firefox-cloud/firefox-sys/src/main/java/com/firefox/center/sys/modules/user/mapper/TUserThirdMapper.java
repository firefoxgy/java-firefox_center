package com.firefox.center.sys.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.sys.modules.user.entity.TUserThird;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface TUserThirdMapper extends BaseMapper<TUserThird> {

    @Select("select tut.* " +
            "from t_user_app_third tuat " +
            "left join t_user_third tut on tuat.app_id=tut.app_id and tuat.tenant_id=tut.tenant_id and tuat.sid=tut.sid " +
            "where tuat.app_id=#{appId} and tuat.tenant_id=#{tenantId} and tuat.uid=#{uid}")
    List<TUserThird> selectList(String appId, Integer tenantId, Integer uid);

}
