package com.firefox.center.credit.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.credit.db.model.TCredit;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户表dao
 * @author sujie
 */
public interface TCreditMapper extends BaseMapper<TCredit> {


    @Update("<script>"+
            "update t_credit " +
            "<if test='type==\"day\"'> " +
            "set credit_day=0 " +
            "</if> " +
            "<if test='type==\"week\"'> " +
            "set credit_week=0, credit_day=0 " +
            "</if> " +
            "<if test='type==\"month\"'> " +
            "set credit_month=0, credit_week=0, credit_day=0 " +
            "</if> " +
            "<if test='type==\"season\"'> " +
            "set credit_season=0, credit_month=0, credit_week=0, credit_day=0 " +
            "</if> " +
            "<if test='type==\"year\"'> " +
            "set credit_year=0, credit_season=0, credit_month=0, credit_week=0, credit_day=0 " +
            "</if> " +
            "</script>")
    int cleanCredit(String type);


    @Select("select * from t_credit where app_id=#{appId} and tenant_id=#{tenantId} and uid=#{uid}")
    TCredit selectRecordByUid(String appId, int tenantId, long uid);

    @Select("select * from t_credit where app_id=#{appId} and tenant_id=#{tenantId} and sid=#{sid}")
    TCredit selectRecordBySid(String appId, int tenantId, long sid);


    @Select("select id,credit_${type}, " +
                "CASE " +
                "WHEN @prevRank = credit_${type} THEN @curRank " +
                "WHEN @prevRank := credit_${type} THEN @curRank := @curRank + 1 " +
                "END AS rank " +
                "from( " +
                    "select concat('u_',uid) id,sum(credit_${type}) credit_${type} from t_credit " +
                    "where app_id=#{appId} and tenant_id=#{tenantId} and uid<>0 " +
                    "group by uid " +
                    "union " +
                    "select concat('s_',sid) id,credit_${type} from t_credit " +
                    "where app_id=#{appId} and tenant_id=#{tenantId} and uid=0 " +
                ") a1, " +
                "(SELECT @curRank :=0, @prevRank := NULL) r " +
                "ORDER BY credit_${type} desc ")
    IPage<TCredit> selectUserRankPage(IPage<TCredit> page, String appId, int tenantId, String type);

    @Select("select * from ( " +
            "select id,credit_${type}, " +
            "CASE " +
            "WHEN @prevRank = credit_${type} THEN @curRank " +
            "WHEN @prevRank := credit_${type} THEN @curRank := @curRank + 1 " +
            "END AS rank " +
            "from( " +
            "select concat('u_',uid) id,sum(credit_${type}) credit_${type} from t_credit " +
            "where app_id=#{appId} and tenant_id=#{tenantId} and uid<>0 " +
            "group by uid " +
            "union " +
            "select concat('s_',sid) id,credit_${type} from t_credit " +
            "where app_id=#{appId} and tenant_id=#{tenantId} and uid=0 " +
            ") a1, " +
            "(SELECT @curRank :=0, @prevRank := NULL) r " +
            "ORDER BY credit_${type} desc " +
            ") a2 " +
            "where id=#{userId}")
    TCredit selectUserRank(String appId, int tenantId, String type, String userId);

}