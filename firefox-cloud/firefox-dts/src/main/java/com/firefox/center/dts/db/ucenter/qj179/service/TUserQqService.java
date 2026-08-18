package com.firefox.center.dts.db.ucenter.qj179.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.TUserQqMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUserQq;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("qj179_TUserQqService")
@DS("qj179")
public class TUserQqService extends BaseService<TUserQqMapper, TUserQq> {

    public TUserQq selectRecord(int uid){
        return baseMapper.selectRecord(uid);
    }
}
