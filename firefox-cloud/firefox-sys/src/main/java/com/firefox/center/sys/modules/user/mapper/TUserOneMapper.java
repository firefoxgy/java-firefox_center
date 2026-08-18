package com.firefox.center.sys.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.sys.modules.user.entity.TUserOne;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表dao
 * @author sujie
 */
public interface TUserOneMapper extends BaseMapper<TUserOne> {

    @Select("select * from t_user_one where phone=#{phone}")
    TUserOne selectRecord(String phone);

}