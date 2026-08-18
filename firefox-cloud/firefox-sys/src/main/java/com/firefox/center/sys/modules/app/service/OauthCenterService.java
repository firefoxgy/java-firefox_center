package com.firefox.center.sys.modules.app.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.sys.common.base.BaseService;
import com.firefox.center.sys.common.system.vo.LoginUser;
import com.firefox.center.sys.modules.app.entity.OauthCenter;
import com.firefox.center.sys.modules.app.entity.OauthTenant;
import com.firefox.center.sys.modules.app.mapper.OauthCenterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公司表 服务类
 */
@Service
@DS("open")
@RequiredArgsConstructor
public class OauthCenterService extends BaseService<OauthCenterMapper, OauthCenter> {

    private final RedisTemplate redisTemplate;

    public IPage<OauthCenter> queryPage(OauthCenter oauthCenter, Integer pageNo, Integer pageSize) {
        QueryWrapper<OauthCenter> queryWrapper = new QueryWrapper<OauthCenter>();
        if(StrKit.notBlank(oauthCenter.getName())){
            queryWrapper.like("name", oauthCenter.getName());
        }
        Page<OauthCenter> page = new Page<OauthCenter>(pageNo, pageSize);
        return baseMapper.selectPage(page, queryWrapper);
    }

    public IPage<OauthCenter> queryPageByAppTenantId(String appId, Integer tenantId, OauthCenter OauthCenter, Integer pageNo, Integer pageSize) {
        QueryWrapper<OauthTenant> queryWrapper = new QueryWrapper<OauthTenant>();
        if(StrKit.notBlank(appId)){
            queryWrapper.eq("oac.app_id", appId);
        }
        if(StrKit.notBlank(tenantId)){
            queryWrapper.eq("oac.tenant_id", tenantId);
        }
        if(StrKit.notBlank(OauthCenter.getName())){
            queryWrapper.like("oc.name", OauthCenter.getName());
        }
        Page<OauthCenter> page = new Page<OauthCenter>(pageNo, pageSize);
        return page.setRecords(baseMapper.queryPageByAppTenantId(page, queryWrapper));
    }

    public List<OauthCenter> selectOtherListByAppTenantId(String appId, Integer tenantId) {
        return baseMapper.selectOtherListByAppTenantId(appId, tenantId);
    }

    public void saveCenter(LoginUser user, OauthCenter oauthCenter) {
        if(oauthCenter.getId()!=null && oauthCenter.getId()!=0){
            baseMapper.updateById(oauthCenter);
        }else{
            baseMapper.insert(oauthCenter);
        }
    }

    public void upStatus(int id, int status) {
        OauthCenter oauthCenter=baseMapper.selectById(id);
        if(oauthCenter!=null){
            oauthCenter.setStatus(status);
            baseMapper.updateById(oauthCenter);
        }
    }

}
