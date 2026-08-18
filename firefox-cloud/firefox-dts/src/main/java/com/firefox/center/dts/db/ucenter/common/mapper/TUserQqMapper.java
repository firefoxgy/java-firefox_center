package com.firefox.center.dts.db.ucenter.common.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUserQq;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表dao
 * @author sujie
 */
public interface TUserQqMapper extends BaseMapper<TUserQq> {

    @Select("select a.*,b.nickname nickname2,b.headpic,b.sex sex2 from t_user_qq a left join t_userinfo b on a.uid=b.uid where a.uid=#{uid}")
    TUserQq selectRecord(int uid);

}