package com.firefox.center.dts.db.user.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.user.mapper.TUserOneMapper;
import com.firefox.center.dts.db.user.model.TUserOne;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
public class TUserOneService extends BaseService<TUserOneMapper, TUserOne> {

    public TUserOne selectRecord(String phone){
        return baseMapper.selectRecord(phone);
    }

}
