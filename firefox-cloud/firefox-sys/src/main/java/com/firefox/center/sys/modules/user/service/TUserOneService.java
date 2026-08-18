package com.firefox.center.sys.modules.user.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.sys.modules.user.entity.TUserOne;
import com.firefox.center.sys.modules.user.mapper.TUserOneMapper;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
@DS("open")
public class TUserOneService extends BaseService<TUserOneMapper, TUserOne> {

    public TUserOne selectRecord(String phone){
        return baseMapper.selectRecord(phone);
    }

}
