package com.firefox.center.oauth.controller;

import com.firefox.center.common.R;
import com.firefox.center.common.constants.SecurityConstants;
import com.firefox.center.common.model.OauthTenantPackage;
import com.firefox.center.oauth.db.service.OauthTenantService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 App登录相关操作
 *
 * @Author: sujie
 */
@Api(tags = "OAuth2 App登录相关操作")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuth2Controller {

    private final TokenEndpoint tokenEndpoint;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OauthTenantService oauthTenantService;


    @GetMapping("/token")
    public R getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return custom(tokenEndpoint.getAccessToken(principal, parameters).getBody());
    }

    @PostMapping("/token")
    public R postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return custom(tokenEndpoint.postAccessToken(principal, parameters).getBody());
    }

    //自定义返回格式
    private R custom(OAuth2AccessToken accessToken) {
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        Map<String, Object> data = new LinkedHashMap(token.getAdditionalInformation());
        if(data != null && data.get("app_id") != null)
        {
            String clientId = (String)data.get("app_id");
            List<OauthTenantPackage> packages = oauthTenantService.selectTenantPackageByAppId(clientId);
            redisTemplate.opsForValue().set(SecurityConstants.CACHE_PACKAGE_KEY + "_" + clientId, packages);//将套餐信息缓存起来在网关校验使用
        }
        return R.ok(token);
    }
}
