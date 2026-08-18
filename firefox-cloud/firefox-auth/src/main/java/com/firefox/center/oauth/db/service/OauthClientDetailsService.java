package com.firefox.center.oauth.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.R;
import com.firefox.center.common.constants.CommonConstant;
import com.firefox.center.common.constants.SecurityConstants;
import com.firefox.center.common.lock.DistributedLock;
import com.firefox.center.common.redis.template.RedisRepository;
import com.firefox.center.common.service.impl.SuperServiceImpl;
import com.firefox.center.oauth.db.mapper.OauthClientDetailsMapper;
import com.firefox.center.oauth.db.model.OauthClientDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: sujie
 */
@Slf4j
@Service("auth_TMidAppService")
@RequiredArgsConstructor
public class OauthClientDetailsService extends SuperServiceImpl<OauthClientDetailsMapper, OauthClientDetails> {
    private final static String LOCK_KEY_CLIENTID = CommonConstant.LOCK_KEY_PREFIX+"clientId:";

    private final RedisRepository redisRepository;
    private final PasswordEncoder passwordEncoder;
    private final DistributedLock lock;

    public R saveClient(OauthClientDetails tMidApp) {
        tMidApp.setClientSecret(tMidApp.getClientSecret());
        String clientId = tMidApp.getClientId();
        super.saveOrUpdateIdempotency(tMidApp, lock
                , LOCK_KEY_CLIENTID+clientId
                , new QueryWrapper<OauthClientDetails>().eq("client_id", clientId)
                , clientId + "已存在");
        // 写入redis缓存
        redisRepository.set(clientRedisKey(tMidApp.getClientId()), tMidApp);
        return R.ok("操作成功");
    }

    public Page<OauthClientDetails> listClent(Map<String, Object> params, boolean isPage) {
        Page<OauthClientDetails> page;
        if (isPage) {
            page = new Page<>(MapUtils.getInteger(params, "page"), MapUtils.getInteger(params, "limit"));
        } else {
            page = new Page<>(1, -1);
        }
        List<OauthClientDetails> list = baseMapper.findList(page, params);
        page.setRecords(list);
        return page;
    }

    public void delClient(long id) {
        String clientId = baseMapper.selectById(id).getClientId();
        baseMapper.deleteById(id);
        redisRepository.del(clientRedisKey(clientId));
    }

    private String clientRedisKey(String clientId) {
        return SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId;
    }
}
