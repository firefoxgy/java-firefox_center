package com.firefox.center.dts.db.ucenter.common.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUserApple;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表dao
 * @author sujie
 */
public interface TUserAppleMapper extends BaseMapper<TUserApple> {

    @Select("select a.*,b.nickname nickname2,b.headpic,b.sex sex2 from t_user_apple a left join t_userinfo b on a.uid=b.uid where a.uid=#{uid}")
    TUserApple selectRecord(int uid);

}