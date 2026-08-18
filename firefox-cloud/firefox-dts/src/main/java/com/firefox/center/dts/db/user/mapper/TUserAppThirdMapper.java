package com.firefox.center.dts.db.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.dts.db.user.model.TUserAppThird;
import com.firefox.center.dts.db.user.model.TUserOne;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户关联表dao
 * @author sujie
 */
public interface TUserAppThirdMapper extends BaseMapper<TUserAppThird> {

    @Update("delete from t_user_app_third where app_id=#{appId} and tenant_id=#{tenantId}")
    void deleteRecord(String appId, int tenantId);

}