package com.firefox.center.dts.db.ucenter.jh173.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.TUserWeiboMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUserWeibo;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("jh173_TUserWeiboService")
@DS("jh173")
public class TUserWeiboService extends BaseService<TUserWeiboMapper, TUserWeibo> {

    public TUserWeibo selectRecord(int uid){
        return baseMapper.selectRecord(uid);
    }
}
