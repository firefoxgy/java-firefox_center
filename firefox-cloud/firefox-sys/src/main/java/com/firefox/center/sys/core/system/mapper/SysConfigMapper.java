package com.firefox.center.sys.core.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.sys.core.system.entity.SysConfig;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface SysConfigMapper extends BaseMapper<SysConfig> {

    @Select("select * from sys_config")
    List<SysConfig> queryAll();

    @Update("update sys_config set config_value = #{value} where config_code = #{code}")
    void updateConf(String code, String value);
}
