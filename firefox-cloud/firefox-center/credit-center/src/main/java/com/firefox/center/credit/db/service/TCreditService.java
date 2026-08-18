package com.firefox.center.credit.db.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.firefox.center.credit.db.mapper.TCreditMapper;
import com.firefox.center.credit.db.model.TCredit;
import com.firefox.center.db.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class TCreditService extends BaseService<TCreditMapper, TCredit> {

    public TCredit queryRecordByUid(String appId, int tenantId, long uid) {
        return baseMapper.selectRecordByUid(appId, tenantId, uid);
    }

    public TCredit queryRecordBySid(String appId, int tenantId, long sid) {
        return baseMapper.selectRecordBySid(appId, tenantId, sid);
    }

    public TCredit queryRecord(String appId, int tenantId, long uid, long sid) {
        QueryWrapper<TCredit> queryWrapper = new QueryWrapper<TCredit>();
        queryWrapper.eq("app_id", appId);
        queryWrapper.eq("tenant_id", tenantId);
        if(uid!=0L){
            queryWrapper.eq("uid", uid);
        }else{
            if(sid!=0L){
                queryWrapper.eq("sid", sid);
            }
        }
        return baseMapper.selectOne(queryWrapper);
    }

    public TCredit selectUserRank(String appId, int tenantId, String type, String userId){
        return baseMapper.selectUserRank(appId, tenantId, type, userId);
    }

    public IPage<TCredit> selectUserRankPage(IPage<TCredit> page, String appId, int tenantId, String type){
        return baseMapper.selectUserRankPage(page, appId, tenantId, type);
    }

    public int cleanCredit(String type){
        return baseMapper.cleanCredit(type);
    }

}
