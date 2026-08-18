package com.firefox.center.dts.db.user.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.user.mapper.TUserAppThirdMapper;
import com.firefox.center.dts.db.user.model.TUserAppThird;
import org.springframework.stereotype.Service;

/**
 * 统一用户表 服务类
 */
@Service
public class TUserAppThirdService extends BaseService<TUserAppThirdMapper, TUserAppThird> {

    public void deleteRecord(String appId, int tenantId){
        baseMapper.deleteRecord(appId, tenantId);
    }

}
