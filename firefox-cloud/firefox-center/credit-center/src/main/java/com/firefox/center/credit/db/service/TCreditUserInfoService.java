package com.firefox.center.credit.db.service;


import com.firefox.center.credit.db.mapper.TCreditUserInfoMapper;
import com.firefox.center.credit.db.model.TCreditUserInfo;
import com.firefox.center.db.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class TCreditUserInfoService extends BaseService<TCreditUserInfoMapper, TCreditUserInfo> {

    public TCreditUserInfo selectRecord(String appId, int tenantId, long uid, String type){
        return baseMapper.selectRecord(appId, tenantId, uid, type);
    }

}
