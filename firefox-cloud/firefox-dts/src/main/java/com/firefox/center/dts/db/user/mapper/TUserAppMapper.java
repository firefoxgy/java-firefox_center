package com.firefox.center.dts.db.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.dts.db.user.model.TUserApp;
import com.firefox.center.dts.db.user.model.TUserOne;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户表dao
 * @author sujie
 */
public interface TUserAppMapper extends BaseMapper<TUserApp> {

    @Select("select count(id) cnum " +
            "from t_user_app " +
            "where (password is null or password='')")
    int selectCount();

    @Select("select * from t_user_app " +
            "where (password is null or password='') and id>#{startId} " +
            "order by id asc limit ${length}")
    List<TUserApp> selectList(int startId, int length);

}