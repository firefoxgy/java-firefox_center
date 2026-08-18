package com.firefox.center.sys.modules.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.sys.modules.app.entity.OauthClientDetails;
import org.apache.ibatis.annotations.Select;


public interface OauthClientDetailsMapper extends BaseMapper<OauthClientDetails> {

    @Select("select * from oauth_client_details where client_id = #{appid} ")
    OauthClientDetails queryByAppid(String appid);



}
