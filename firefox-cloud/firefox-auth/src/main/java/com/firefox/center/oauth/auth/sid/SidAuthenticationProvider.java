package com.firefox.center.oauth.auth.sid;

import com.firefox.center.common.model.LoginThirdUser;
import com.firefox.center.oauth.service.MyUserDetailService;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: sujie
 */
@Setter
public class SidAuthenticationProvider implements AuthenticationProvider {
    private MyUserDetailService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        SidAuthenticationToken authenticationToken = (SidAuthenticationToken) authentication;
        String appId = (String) authenticationToken.getAppId();
        Integer tenantId = (Integer) authenticationToken.getTenantId();
        String sid = (String) authenticationToken.getPrincipal();
        LoginThirdUser user = userDetailsService.loadThirdUserBySid(appId, tenantId, sid);
        if (user == null) {
            throw new InternalAuthenticationServiceException("用户不存在");
        }
        SidAuthenticationToken authenticationResult = new SidAuthenticationToken(user, sid, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SidAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
