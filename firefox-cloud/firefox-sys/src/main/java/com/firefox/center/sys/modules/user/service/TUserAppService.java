package com.firefox.center.sys.modules.user.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.Record;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.utils.IdGen;
import com.firefox.center.sys.common.base.BaseService;
import com.firefox.center.sys.modules.user.entity.TUserApp;
import com.firefox.center.sys.modules.user.entity.TUserOne;
import com.firefox.center.sys.modules.user.mapper.TUserAppMapper;
import com.firefox.center.sys.modules.user.mapper.TUserOneMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司表 服务类
 */
@Service
@DS("open")
@RequiredArgsConstructor
public class TUserAppService extends BaseService<TUserAppMapper, TUserApp> {

    private final TUserOneMapper tUserOneMapper;

    public IPage<TUserApp> queryPage(TUserApp tUserApp, Integer pageNo, Integer pageSize) {
        QueryWrapper<TUserApp> queryWrapper = new QueryWrapper<TUserApp>();
        if(StrKit.notBlank(tUserApp.getId())){
            queryWrapper.eq("id", tUserApp.getId());
        }
        if(StrKit.notBlank(tUserApp.getPhone())){
            queryWrapper.like("phone", tUserApp.getPhone());
        }
        if(StrKit.notBlank(tUserApp.getTenantId())){
            queryWrapper.eq("tenant_id", tUserApp.getTenantId());
        }
        if(StrKit.notBlank(tUserApp.getAppId())){
            queryWrapper.eq("app_id", tUserApp.getAppId());
        }
        Page<TUserApp> page = new Page<TUserApp>(pageNo, pageSize);
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Transactional
    public void saveUserApp(TUserApp tUserApp) {
        if(tUserApp.getId()!=null && tUserApp.getId()!=0){
            baseMapper.updateById(tUserApp);
        }else{
            TUserOne tUserOne=tUserOneMapper.selectRecord(tUserApp.getPhone());
            String openId="";
            Long openIntId=0L;
            if(tUserOne==null){
                Record record= IdGen.getMd5Id();
                openId=record.getStr("md5");
                openIntId=record.getLong("id");
                tUserOne=TUserOne.builder()
                        .id(IdGen.getId())
                        .openId(openId)
                        .openIntId(openIntId)
                        .phone(tUserApp.getPhone())
                        .build();
                tUserOneMapper.insert(tUserOne);
            }else{
                openId=tUserOne.getOpenId();
                openIntId=tUserOne.getOpenIntId();
            }
            long id= IdGen.getId();
            tUserApp.setId(id);
            tUserApp.setOpenIntId(openIntId);
            tUserApp.setOpenId(openId);
            tUserApp.setUid(id);
            tUserApp.setRegFrom("admin");
            tUserApp.setSource(1);
            baseMapper.insert(tUserApp);
        }
    }

}
