package com.firefox.center.user.db.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.user.db.mapper.TUserOneMapper;
import com.firefox.center.user.db.model.TUserOne;
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
