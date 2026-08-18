package com.firefox.center.dts.db.ucenter.ld186.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.TUserQqMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUserQq;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("ld186_TUserQqService")
@DS("ld186")
public class TUserQqService extends BaseService<TUserQqMapper, TUserQq> {

    public TUserQq selectRecord(int uid){
        return baseMapper.selectRecord(uid);
    }
}
