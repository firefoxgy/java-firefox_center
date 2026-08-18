package com.firefox.center.user.db.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.user.db.mapper.TUserThirdMapper;
import com.firefox.center.user.db.model.TUserThird;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 公司表 服务类
 */
@Service
public class TUserThirdService extends BaseService<TUserThirdMapper, TUserThird> {

    public TUserThird selectRecordById(long id){
        return baseMapper.selectRecordById(id);
    }

    public TUserThird selectRecordBySId(String appId, int tenantId, long sid){
        return baseMapper.selectRecordBySId(appId, tenantId, sid);
    }

    public TUserThird selectRecordByUnionId(String appId, int tenantId, String loginType, String unionId){
        return baseMapper.selectRecordByUnionId(appId, tenantId, loginType, unionId);
    }

    public TUserThird selectRecordByThirdId(String appId, int tenantId, String thirdid){
        return baseMapper.selectRecordByThirdId1(appId, tenantId, thirdid);
    }

    public TUserThird selectRecordByThirdId(String appId, int tenantId, String loginType, String thirdid){
        return baseMapper.selectRecordByThirdId2(appId, tenantId, loginType, thirdid);
    }

    public List<TUserThird> selectBindList(String appId, int tenantId, long userId){
        return baseMapper.selectBindList(appId, tenantId, userId);
    }

    public boolean deleteRecord(String appId, int tenantId, String loginType, long id){
        return baseMapper.deleteRecord(appId, tenantId, loginType, id)==1?true:false;
    }

}
