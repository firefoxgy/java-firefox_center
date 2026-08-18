package com.firefox.center.user.db.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.user.db.mapper.TUserAppThirdMapper;
import com.firefox.center.user.db.model.TUserAppThird;
import org.springframework.stereotype.Service;

/**
 * 统一用户表 服务类
 */
@Service
public class TUserAppThirdService extends BaseService<TUserAppThirdMapper, TUserAppThird> {

    public boolean deleteRecord(String appId, Integer tenantId, long uid, long sid, String authType){
        return baseMapper.deleteRecord(appId, tenantId, uid, sid, authType)==1?true:false;
    }
}
