package com.firefox.center.dts.db.ucenter.dg181.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.TUserQqMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUserQq;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("dg181_TUserQqService")
@DS("dg181")
public class TUserQqService extends BaseService<TUserQqMapper, TUserQq> {

    public TUserQq selectRecord(int uid){
        return baseMapper.selectRecord(uid);
    }
}
