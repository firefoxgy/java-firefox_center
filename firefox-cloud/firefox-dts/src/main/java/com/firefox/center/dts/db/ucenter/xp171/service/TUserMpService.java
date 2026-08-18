package com.firefox.center.dts.db.ucenter.xp171.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.TUserMpMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUserMp;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("xp171_TUserMpService")
@DS("xp171")
public class TUserMpService extends BaseService<TUserMpMapper, TUserMp> {

    public TUserMp selectRecord(int uid){
        return baseMapper.selectRecord(uid);
    }
}
