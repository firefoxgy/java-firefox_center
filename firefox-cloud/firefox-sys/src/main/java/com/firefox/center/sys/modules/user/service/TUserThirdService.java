package com.firefox.center.sys.modules.user.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.sys.common.base.BaseService;
import com.firefox.center.sys.modules.user.entity.TUserThird;
import com.firefox.center.sys.modules.user.mapper.TUserThirdMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公司表 服务类
 */
@Service
@DS("open")
@RequiredArgsConstructor
public class TUserThirdService extends BaseService<TUserThirdMapper, TUserThird> {

    public List<TUserThird> selectList(String appId, Integer tenantId, Integer uid) {
        return baseMapper.selectList(appId, tenantId, uid);
    }

}
