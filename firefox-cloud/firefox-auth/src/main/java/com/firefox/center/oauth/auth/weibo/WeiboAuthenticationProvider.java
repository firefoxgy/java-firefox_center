package com.firefox.center.oauth.auth.weibo;

import com.firefox.center.common.constants.Consts;
import com.firefox.center.common.model.LoginAppUser;
import com.firefox.center.common.model.LoginThirdUser;
import com.firefox.center.oauth.service.MyUserDetailService;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: sujie
 */
@Setter
public class WeiboAuthenticationProvider implements AuthenticationProvider {
    private MyUserDetailService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        WeiboAuthenticationToken authenticationToken = (WeiboAuthenticationToken) authentication;
        String clientType = (String) authenticationToken.getClientType();
        String version = (String) authenticationToken.getVersion();
        String appId = (String) authenticationToken.getAppId();
        Integer tenantId = (Integer) authenticationToken.getTenantId();
        String thirdid = (String) authenticationToken.getPrincipal();
        String password = (String) authenticationToken.getCredentials();
        LoginThirdUser user = userDetailsService.loadThirdUser(clientType, version, appId, tenantId, Consts.grantType.WEIBO, thirdid);
        if (user == null) {
            throw new InternalAuthenticationServiceException("帐号未注册");
        }
        WeiboAuthenticationToken authenticationResult = new WeiboAuthenticationToken(user, password, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WeiboAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
