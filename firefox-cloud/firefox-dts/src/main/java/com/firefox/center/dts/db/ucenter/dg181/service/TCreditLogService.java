package com.firefox.center.dts.db.ucenter.dg181.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.TCreditLogMapper;
import com.firefox.center.dts.db.ucenter.common.mapper.TCreditMapper;
import com.firefox.center.dts.db.ucenter.common.model.TCredit;
import com.firefox.center.dts.db.ucenter.common.model.TCreditLog;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("dg181_TCreditLogService")
@DS("dg181")
public class TCreditLogService extends BaseService<TCreditLogMapper, TCreditLog> {

    public TCreditLog selectCredit(int uid, long startTime, long endTime){
        return baseMapper.selectCredit(uid, startTime, endTime);
    }

}
