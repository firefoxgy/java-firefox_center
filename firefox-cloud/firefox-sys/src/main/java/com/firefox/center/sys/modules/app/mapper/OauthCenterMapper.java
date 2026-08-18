package com.firefox.center.sys.modules.app.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.firefox.center.sys.modules.app.entity.OauthCenter;
import com.firefox.center.sys.modules.app.entity.OauthTenant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface OauthCenterMapper extends BaseMapper<OauthCenter> {

    @Select("select oc.*,oac.status relStatus " +
            "from oauth_app_center oac " +
            "left join oauth_center oc on oac.center_id=oc.id " +
            "${ew.customSqlSegment} " +
            "order by id ")
    List<OauthCenter> queryPageByAppTenantId(IPage<OauthCenter> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select * from oauth_center where id not in( " +
                "select center_id from oauth_app_center where app_id=#{appId} and tenant_id=#{tenantId} " +
            ") ")
    List<OauthCenter> selectOtherListByAppTenantId(String appId, Integer tenantId);

}
