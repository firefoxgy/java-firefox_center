package com.firefox.center.oauth.jwt;

import com.firefox.center.common.Record;
import com.firefox.center.common.constants.BusinessConstants;
import com.firefox.center.common.constants.Consts;
import com.firefox.center.common.model.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;

/**
 * JWTokenEnhancer
 *
 * @author fengzheng
 * @date 2019/10/12
 */
public class JWTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        String clientId=oAuth2Authentication.getOAuth2Request().getClientId();
        String tenant_id="";
        Map<String, String> map=oAuth2Authentication.getOAuth2Request().getRequestParameters();
        int TenantId = 0;
        if(map.containsKey(Consts.oauth.PARAM_TENANT_ID)){
            tenant_id=map.get(Consts.oauth.PARAM_TENANT_ID).toString();
            TenantId = Integer.parseInt(tenant_id);
        }
        Object principal =oAuth2Authentication.getPrincipal();
        String clientType="", version="", AppId="",centers="";

        Long uid=0L, sid=0L,cache_uid=0L;
        String openid="", sids="", utype="",userName="", manageAppIds="";
        if (principal instanceof LoginAdminUser) {
            LoginAdminUser user = (LoginAdminUser) principal;
            clientType=user.getClientType();
            version=user.getVersion();
            AppId = user.getAppId();
            TenantId = user.getTenantId();
            openid=user.getOpenId();
            uid = user.getUid();
            sid = user.getSid();
            sids = user.getSids();
            userName = user.getUsername();
            manageAppIds = user.getManageAppIds();
            centers = user.getCenters();
            cache_uid=uid;
            utype= BusinessConstants.TYPE_ADMIN;
        } else if (principal instanceof LoginAppUser) {
            LoginAppUser user = (LoginAppUser) principal;
            clientType=user.getClientType();
            version=user.getVersion();
            AppId = user.getAppId();
            TenantId = user.getTenantId();
            openid=user.getOpenId();
            uid = user.getUid();
            sid = user.getSid();
            sids = user.getSids();
            userName = user.getUsername();
            cache_uid=uid;
            utype=BusinessConstants.TYPE_APP;
        } else if (principal instanceof LoginThirdUser) {
            LoginThirdUser user = (LoginThirdUser) principal;
            clientType=user.getClientType();
            version=user.getVersion();
            AppId = user.getAppId();
            TenantId = user.getTenantId();
            openid=user.getOpenId();
            uid = user.getUid();
            sid = user.getSid();
            sids = user.getSids();
            cache_uid=sid;
            utype=BusinessConstants.TYPE_THIRD;
        }else{
            utype=BusinessConstants.TYPE_CREDENTIALS;
            AppId = principal.toString();
        }
        Record params = new Record()
                .set("client_type", clientType)
                .set("version", version)
                .set("app_id", AppId)
                .set("tenant_id", TenantId)
                .set("openid", openid)
                .set("utype", utype)
                .set("uid", uid)
                .set("user_name", userName)
                .set("sid", sid)
                .set("sids", sids)
                .set("cache_uid", cache_uid)
                .set("manage_app_ids", manageAppIds)
                .set("centers",centers);
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(params.getColumns());
        return oAuth2AccessToken;
    }

}