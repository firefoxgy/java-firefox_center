package com.firefox.center.sys.modules.app.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.R;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.sys.common.base.BaseService;
import com.firefox.center.sys.common.system.vo.LoginUser;
import com.firefox.center.sys.modules.app.entity.OauthTenant;
import com.firefox.center.sys.modules.app.mapper.OauthTenantMapper;
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
public class OauthTenantService extends BaseService<OauthTenantMapper, OauthTenant> {

    private final RedisTemplate redisTemplate;

    public IPage<OauthTenant> queryPage(OauthTenant oauthTenant, Integer pageNo, Integer pageSize) {
        QueryWrapper<OauthTenant> queryWrapper = new QueryWrapper<OauthTenant>();
        if(StrKit.notBlank(oauthTenant.getName())){
            queryWrapper.like("name", oauthTenant.getName());
        }
        Page<OauthTenant> page = new Page<OauthTenant>(pageNo, pageSize);
        return baseMapper.selectPage(page, queryWrapper);
    }

    public IPage<OauthTenant> queryPageByAppid(String appid, OauthTenant oauthTenant, Integer pageNo, Integer pageSize) {
        QueryWrapper<OauthTenant> queryWrapper = new QueryWrapper<OauthTenant>();
        if(StrKit.notBlank(appid)){
            queryWrapper.eq("ota.app_id", appid);
        }
        if(StrKit.notBlank(oauthTenant.getName())){
            queryWrapper.like("ot.name", oauthTenant.getName());
        }
        Page<OauthTenant> page = new Page<OauthTenant>(pageNo, pageSize);
        return page.setRecords(baseMapper.queryPageByAppid(page, queryWrapper));
    }

    public List<OauthTenant> selectOtherListByAppid(String appId) {
        return baseMapper.selectOtherListByAppid(appId);
    }

    public R addTenant(LoginUser user, OauthTenant oauthTenant) {
        if(oauthTenant.getId()!=null && oauthTenant.getId()!=0){
            if(baseMapper.selectById(oauthTenant.getId())!=null){
                return R.error("租户id已存在");
            }
        }
        baseMapper.insert(oauthTenant);
        return R.error("保存成功");
    }

    public void updateTenant(LoginUser user, OauthTenant oauthTenant) {
        if(oauthTenant.getId()!=null && oauthTenant.getId()!=0){
            baseMapper.updateById(oauthTenant);
        }else{
            baseMapper.insert(oauthTenant);
        }
    }

    public void upStatus(int id, int status) {
        OauthTenant oauthTenant=baseMapper.selectById(id);
        if(oauthTenant!=null){
            oauthTenant.setStatus(status);
            baseMapper.updateById(oauthTenant);
        }
    }

}
