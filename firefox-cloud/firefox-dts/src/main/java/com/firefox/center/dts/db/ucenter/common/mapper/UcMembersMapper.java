package com.firefox.center.dts.db.ucenter.common.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.dts.db.ucenter.common.model.UcMembers;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户表dao
 * @author sujie
 */
public interface UcMembersMapper extends BaseMapper<UcMembers> {

    @Select("SELECT count(a.uid) cnum " +
            "FROM uc_members a " +
            "LEFT JOIN t_userinfo b ON a.uid = b.uid " +
            "where a.status=1")
    int selectCount();

    @Select("SELECT a.*, " +
            "b.nickname, " +
            "b.headpic, " +
            "b.sex " +
            "FROM uc_members a " +
            "LEFT JOIN t_userinfo b ON a.uid = b.uid " +
            "where a.status=1 and a.uid>#{startId} " +
            "order by a.uid asc limit ${length}")
    List<UcMembers> selectList(int startId, int length);

}