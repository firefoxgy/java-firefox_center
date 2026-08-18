package com.firefox.center.dts.db.ucenter.dg181.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.TUsershipMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUsership;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公司表 服务类
 */
@Service("dg181_TUsershipService")
@DS("dg181")
public class TUsershipService extends BaseService<TUsershipMapper, TUsership> {

    public List<TUsership> selectList(){
        return baseMapper.selectList();
    }

    public List<TUsership> selectOtherList(){
        return baseMapper.selectOtherList();
    }

}
