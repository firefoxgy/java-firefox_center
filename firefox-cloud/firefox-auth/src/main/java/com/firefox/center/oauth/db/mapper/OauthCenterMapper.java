package com.firefox.center.oauth.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.oauth.db.model.OauthCenter;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OauthCenterMapper extends BaseMapper<OauthCenter> {

    @Select("select oc.* " +
            "from oauth_app_center oac " +
            "left join oauth_center oc on oc.status=1 and oac.center_id=oc.id " +
            "where oac.app_id=#{appId} and oac.tenant_id=#{tenantId} and oac.status=1")
    List<OauthCenter> selectList(String appId, Integer tenantId);
}
