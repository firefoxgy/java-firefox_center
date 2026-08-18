package com.firefox.center.dts.db.ucenter.common.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUsership;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户表dao
 * @author sujie
 */
public interface TUsershipMapper extends BaseMapper<TUsership> {

    @Select("select tu.*,um.mobile " +
            "from t_usership tu left join uc_members um " +
            "on tu.uid=um.uid " +
            "where tu.status=1 and um.mobile is not null and um.mobile<>'' ")
    List<TUsership> selectList();

    @Select("select tu.*,um.mobile " +
            "from t_usership tu left join uc_members um " +
            "on tu.otheruid=um.uid " +
            "where tu.status=1 and um.mobile is not null and um.mobile<>'' ")
    List<TUsership> selectOtherList();

}