package com.firefox.center.user.db.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.user.db.mapper.TUserAppMapper;
import com.firefox.center.user.db.model.TUserApp;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
public class TUserAppService extends BaseService<TUserAppMapper, TUserApp> {

    public TUserApp selectById(long id){
        return baseMapper.selectById(id);
    }

    public TUserApp selectByUid(String appId, int tenantId, long uid){
        return baseMapper.selectByUid(appId, tenantId, uid);
    }

    public TUserApp selectByPhone(String appId, int tenantId, String phone){
        return baseMapper.selectByPhone(appId, tenantId, phone);
    }

    public TUserApp selectByUsername(String appId, int tenantId, String username){
        return baseMapper.selectByUsername(appId, tenantId, username);
    }

    public TUserApp selectByEmail(String appId, int tenantId, String email){
        return baseMapper.selectByEmail(appId, tenantId, email);
    }

    public TUserApp selectByOpenId(String appId, int tenantId, String openId){
        return baseMapper.selectByOpenId(appId, tenantId, openId);
    }

}
