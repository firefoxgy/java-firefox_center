package com.firefox.center.oauth.controller;

import com.firefox.center.common.R;
import com.firefox.center.common.captcha.RandImageUtil;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.model.LoginAdminUser;
import com.firefox.center.common.model.OauthTenantPackage;
import com.firefox.center.common.redis.template.RedisRepository;
import com.firefox.center.common.utils.MD5Util;
import com.firefox.center.common.utils.SpringUtil;
import com.firefox.center.oauth.auth.adminPassword.AdminPasswordAuthenticationToken;
import com.firefox.center.oauth.db.model.OauthCenter;
import com.firefox.center.oauth.db.model.OauthTenant;
import com.firefox.center.oauth.db.model.OauthTenantApp;
import com.firefox.center.oauth.db.service.OauthCenterService;
import com.firefox.center.oauth.db.service.OauthTenantAppAppService;
import com.firefox.center.oauth.db.service.OauthTenantService;
import com.firefox.center.oauth.service.CacheService;
import com.firefox.center.oauth.service.MyUserDetailService;
import com.firefox.center.oauth.service.RedisClientDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OAuth2 App登录相关操作
 *
 * @Author: sujie
 */
@Api(tags = "OAuth2 管理登录相关操作")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth/admin")
public class OAuth2AdminController {

    private final RedisRepository redisRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AuthorizationServerTokenServices authorizationServerTokenServices;
    private final AuthenticationManager authenticationManager;
    private final OauthTenantAppAppService oauthTenantAppAppService;
    private final OauthTenantService oauthTenantService;
    private final CacheService cacheService;
    private final OauthCenterService oauthCenterService;
    private final MyUserDetailService userDetailsService;

    @ApiOperation("获取验证码")
    @GetMapping(value = "/randomImage/{key}", produces = "image/png")
    public void randomImage(HttpServletResponse response,@PathVariable String key) throws IOException {
        if (StringUtils.equalsIgnoreCase("arithmetic", "gif")) {
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
        } else {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
        Captcha captcha = RandImageUtil.createCaptcha("arithmetic");
        String lowerCaseCode = StringUtils.lowerCase(captcha.text());
        while(lowerCaseCode.indexOf("-")!=-1){
            captcha = RandImageUtil.createCaptcha("arithmetic");
            lowerCaseCode = StringUtils.lowerCase(captcha.text());
        }
        String realKey = MD5Util.encrypt(lowerCaseCode+key);
        redisRepository.opsForValueSet(realKey, lowerCaseCode, 60L);
        captcha.out(response.getOutputStream());
    }

    @ApiOperation(value = "管理端帐号密码获取token")
    @PostMapping("/password/token")
    public void getTokenByPassword(@RequestBody Map map,
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        String client_id=getPara(map.get("client_id"));
        String client_secret=getPara(map.get("client_secret"));
        Integer tenant_id=getParaToInt(map.get("tenant_id"));
        Integer center_id=getParaToInt(map.get("center_id"));
        String username=getPara(map.get("username"));
        String password=getPara(map.get("password"));
        String key=getPara(map.get("key"));
        String code=getPara(map.get("code"));
        String wxOpenId = getPara(map.get("wx_openid"));

        String realKey = MD5Util.encrypt(code+key);
        if(!code.equals(redisRepository.opsForValueGet(realKey))){
            exceptionHandler(response, R.error(CodeEnum.CAPTCHA_ERROR));
            return;
        }
        LoginAdminUser user = userDetailsService.loadAdminUserByUsername(client_id, tenant_id, username);
        if(StrKit.isNull(user)){
            exceptionHandler(response, R.error(CodeEnum.USER_NAME_OR_PASSWORD_ERROR));
            return;
        }
        if(user.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        if(center_id > 0){ //有服务中心id，校验该应用及租户是否购买相应服务中心的套餐
            if(oauthTenantService.findTenantPackageCountByCri(user.getAppId(),user.getTenantId(), center_id) <= 0) {
                exceptionHandler(response, R.error(CodeEnum.TENANT_NO_PACKAGE));
                return;
            }
        }

        List<OauthTenantPackage> packages = oauthTenantService.selectTenantPackageByAppId(user.getAppId());
        String strCenterIds = "";
        for(OauthTenantPackage item : packages) {
            if(item.getCenterId() > 0)
                if(strCenterIds.length() == 0)
                    strCenterIds = strCenterIds + item.getCenterId();
                else
                    strCenterIds = strCenterIds + "," + item.getCenterId();
        }

        AdminPasswordAuthenticationToken token = new AdminPasswordAuthenticationToken(user.getAppId(), user.getTenantId(), username, password, user.getManageAppIds(), strCenterIds);

        //将登录带过来的微信openid保存下来
        if(!StringUtils.isEmpty(wxOpenId)) {
            int ret = oauthTenantAppAppService.addWx(user.getUid(), wxOpenId, user.getAppId());
            if (ret == 0)
                log.warn("添加微信openid失败 uid:" + user.getUid() + " username:" + user.getUsername() + " appId:" + user.getAppId() + " wxopenid:" + wxOpenId);
            else if(ret == -1)
                log.info("微信openid已存在 uid:" + user.getUid() + " username:" + user.getUsername() + " appId:" + user.getAppId() + " wxopenid:" + wxOpenId);
        }

        writerToken(request, response, user.getAppId(), client_secret, user.getTenantId(), user.getUid(), token, "帐号或密码错误");
    }

    @ApiOperation(value = "管理端帐号密码获取token不用验证码")
    @PostMapping("/user/token")
    public void getUserToken(@RequestBody Map map,
        HttpServletRequest request, HttpServletResponse response) throws IOException {

        String client_id=getPara(map.get("client_id"));
        String client_secret=getPara(map.get("client_secret"));
        Integer tenant_id=getParaToInt(map.get("tenant_id"));
        Integer center_id=getParaToInt(map.get("center_id"));
        String username=getPara(map.get("username"));
        String password=getPara(map.get("password"));
        String wxOpenId = getPara(map.get("wx_openid"));

        LoginAdminUser user = userDetailsService.loadAdminUserByUsername(client_id, tenant_id, username);
        if(StrKit.isNull(user)){
            exceptionHandler(response, R.error(CodeEnum.USER_NAME_OR_PASSWORD_ERROR));
            return;
        }
        if(user.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        if(center_id > 0){ //有服务中心id，校验该应用及租户是否购买相应服务中心的套餐
            if(oauthTenantService.findTenantPackageCountByCri(user.getAppId(),user.getTenantId(), center_id) <= 0) {
                exceptionHandler(response, R.error(CodeEnum.TENANT_NO_PACKAGE));
                return;
            }
        }

        List<OauthTenantPackage> packages = oauthTenantService.selectTenantPackageByAppId(user.getAppId());
        String strCenterIds = "";
        for(OauthTenantPackage item : packages) {
            if(item.getCenterId() > 0)
                if(strCenterIds.length() == 0)
                    strCenterIds = strCenterIds + item.getCenterId();
                else
                    strCenterIds = strCenterIds + "," + item.getCenterId();
        }

        AdminPasswordAuthenticationToken token = new AdminPasswordAuthenticationToken(user.getAppId(), user.getTenantId(), username, password, user.getManageAppIds(), strCenterIds);

        //将登录带过来的微信openid保存下来
        if(!StringUtils.isEmpty(wxOpenId)) {
            int ret = oauthTenantAppAppService.addWx(user.getUid(), wxOpenId, user.getAppId());
            if (ret == 0)
                log.warn("添加微信openid失败 uid:" + user.getUid() + " username:" + user.getUsername() + " appId:" + user.getAppId() + " wxopenid:" + wxOpenId);
            else if(ret == -1)
                log.info("微信openid已存在 uid:" + user.getUid() + " username:" + user.getUsername() + " appId:" + user.getAppId() + " wxopenid:" + wxOpenId);
        }

        writerToken(request, response, user.getAppId(), client_secret, user.getTenantId(), user.getUid(), token, "帐号或密码错误");
    }

    private void writerToken(HttpServletRequest request,
                             HttpServletResponse response,
                             String clientId,
                             String clientSecret,
                             Integer tenant_id,
                             long uid,
                             AbstractAuthenticationToken token,
                             String badCredenbtialsMsg) throws IOException {
        try {
            ClientDetails clientDetails = getClient(clientId, clientSecret, null);
            checkApp(response, clientId, tenant_id);
            TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(), "customer");
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
            OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
            oAuth2Authentication.setAuthenticated(true);
            //缓存用户可以访问的中心
            cacheUserPath(clientId, tenant_id, uid);

            writerObj(response, R.ok(oAuth2AccessToken));
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            exceptionHandler(response, badCredenbtialsMsg);
        } catch (Exception e) {
            exceptionHandler(response, e);
        }
    }

    private void exceptionHandler(HttpServletResponse response, Exception e) throws IOException {
        log.error("exceptionHandler-error:", e);
        exceptionHandler(response, e.getMessage());
    }

    private void exceptionHandler(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        writerObj(response, R.error(msg));
    }

    private void exceptionHandler(HttpServletResponse response, Object obj) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        writerObj(response, obj);
    }

    private void writerObj(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try(Writer writer = response.getWriter()){
            writer.write(objectMapper.writeValueAsString(obj));
            writer.flush();
        }
    }

    private ClientDetails getClient(String clientId, String clientSecret, RedisClientDetailsService clientDetailsService) {
        if (clientDetailsService == null) {
            clientDetailsService = SpringUtil.getBean(RedisClientDetailsService.class);
        }
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
        } else if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
        }
        return clientDetails;
    }

    private void checkApp(HttpServletResponse response, String client_id, Integer tenant_id) throws IOException {
        //判断库里有没有租户id
        OauthTenant tSaasTenant=oauthTenantService.getById(tenant_id);
        if(StrKit.isNull(tSaasTenant)){
            writerObj(response, R.error(CodeEnum.TENANT_NOT_EXIST));
            return;
        }
        if(tSaasTenant.getStatus()!=1){
            writerObj(response, R.error(CodeEnum.TENANT_IS_DISABLED));
            return;
        }

        //判断应用下是否有该租户
        OauthTenantApp tMidTenantAppDTO=oauthTenantAppAppService.selectRecord(client_id, tenant_id);
        if(StrKit.isNull(tMidTenantAppDTO)){
            writerObj(response, R.error(CodeEnum.TENANT_NO_OAUTH));
            return;
        }
        if(tMidTenantAppDTO.getStatus()!=1){
            writerObj(response, R.error(CodeEnum.TENANT_IS_DISABLED));
            return;
        }
    }

    protected String getPara(Object obj){
        return obj==null?"":obj.toString();
    }

    protected Integer getParaToInt(Object obj){
        return obj==null?0:Integer.valueOf(obj.toString());
    }

    private void cacheUserPath(String clientId, Integer tenantId, long uid) {
        List<OauthCenter> centerList= oauthCenterService.selectList(clientId, tenantId);
        List<String> pathList= centerList.stream().map(center -> center.getPath()).collect(Collectors.toList());
        cacheService.cachePath(uid, pathList);
    }
}
