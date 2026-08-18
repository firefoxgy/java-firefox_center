package com.firefox.center.dts.db.ucenter.yl172.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.TUserMpMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUserMp;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("yl172_TUserMpService")
@DS("yl172")
public class TUserMpService extends BaseService<TUserMpMapper, TUserMp> {

    public TUserMp selectRecord(int uid){
        return baseMapper.selectRecord(uid);
    }
}
