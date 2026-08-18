package com.firefox.center.dts.db.user.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.user.mapper.TUserDtsLogMapper;
import com.firefox.center.dts.db.user.model.TUserDtsLog;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
public class TUserDtsLogService extends BaseService<TUserDtsLogMapper, TUserDtsLog> {

    public TUserDtsLog selectRecord(String appId, int tenantId){
        return baseMapper.selectRecord(appId, tenantId);
    }
}
