package com.firefox.center.dts.db.ucenter.ys188.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.TUserAppleMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUserApple;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("ys188_TUserAppleService")
@DS("ys188")
public class TUserAppleService extends BaseService<TUserAppleMapper, TUserApple> {

    public TUserApple selectRecord(int uid){
        return baseMapper.selectRecord(uid);
    }
}
