package com.firefox.center.config.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.config.db.model.TConfigAllVersion;
import org.apache.ibatis.annotations.Select;

public interface TConfigVersionMapper extends BaseMapper<TConfigAllVersion> {

    @Select("select version from t_config_all_version where type = #{type} ")
    Integer selectVersionByType(String type);
}