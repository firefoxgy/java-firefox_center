package com.firefox.center.dts.db.ucenter.common.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.dts.db.ucenter.common.model.TCredit;
import com.firefox.center.dts.db.ucenter.common.model.TCreditLog;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表dao
 * @author sujie
 */
public interface TCreditLogMapper extends BaseMapper<TCreditLog> {

    @Select("select sum(case when num is null then 0 else num end) snum " +
            "from t_credit_log " +
            "where uid=#{uid} and (create_time>=#{startTime} and create_time<#{endTime})")
    TCreditLog selectCredit(int uid, long startTime, long endTime);

}