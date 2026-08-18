package com.firefox.center.sys.modules.app.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.constants.SecurityConstants;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.lang.StringUtils;
import com.firefox.center.sys.common.base.BaseService;
import com.firefox.center.sys.common.system.vo.LoginUser;
import com.firefox.center.sys.modules.app.entity.OauthClientDetails;
import com.firefox.center.sys.modules.app.mapper.OauthClientDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
@DS("open")
@RequiredArgsConstructor
public class OauthClientDetailsService extends BaseService<OauthClientDetailsMapper, OauthClientDetails> {

    private final RedisTemplate redisTemplate;

    public IPage<OauthClientDetails> queryPage(OauthClientDetails oauthClientDetails, Integer pageNo, Integer pageSize) {
        QueryWrapper<OauthClientDetails> queryWrapper = new QueryWrapper<OauthClientDetails>();
        if(StrKit.notBlank(oauthClientDetails.getTitle())){
            queryWrapper.like("title", oauthClientDetails.getTitle());
        }
        Page<OauthClientDetails> page = new Page<OauthClientDetails>(pageNo, pageSize);
        return baseMapper.selectPage(page, queryWrapper);
    }

    public void saveApp(LoginUser user, OauthClientDetails oauthClientDetails) {
        if(oauthClientDetails.getId()!=null && oauthClientDetails.getId()!=0){
            oauthClientDetails.setUpdateUser(user.getId());
            baseMapper.updateById(oauthClientDetails);

            removeRedisCache(oauthClientDetails.getClientId());
        }else{
            String client_secret_pre= StrKit.uuid();
            BCryptPasswordEncoder encode = new BCryptPasswordEncoder();           ;

            oauthClientDetails.setClientId(getRandomAppId());
            oauthClientDetails.setClientSecretPre(client_secret_pre);
            oauthClientDetails.setClientSecret(encode.encode(client_secret_pre));
            oauthClientDetails.setScope("all");
            oauthClientDetails.setAuthorizedGrantTypes("authorization_code,password,refresh_token,client_credentials");
            oauthClientDetails.setRefreshTokenValidity(2592000);
            oauthClientDetails.setCreateUser(user.getId());
            oauthClientDetails.setUpdateUser(user.getId());
            baseMapper.insert(oauthClientDetails);
        }
    }

    protected static String getRandomAppId(){
        return "kp"+ StringUtils.getRandom(18);
    }

    private void removeRedisCache(String clientId) {
        redisTemplate.opsForValue().get(clientRedisKey(clientId));
    }

    private String clientRedisKey(String clientId) {
        return SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId;
    }

}
