package com.firefox.center.sys.modules.app.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.firefox.center.sys.modules.app.entity.OauthTenant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface OauthTenantMapper extends BaseMapper<OauthTenant> {

    @Select("select ot.*,ota.status relStatus " +
            "from oauth_tenant_app ota " +
            "left join oauth_tenant ot on ota.tenant_id=ot.id " +
            "${ew.customSqlSegment} " +
            "order by id ")
    List<OauthTenant> queryPageByAppid(IPage<OauthTenant> page, @Param(Constants.WRAPPER) Wrapper wrapper);


    @Select("select * from oauth_tenant where id not in( " +
                "select tenant_id from oauth_tenant_app where app_id=#{appId} " +
            ") ")
    List<OauthTenant> selectOtherListByAppid(String appId);

}
