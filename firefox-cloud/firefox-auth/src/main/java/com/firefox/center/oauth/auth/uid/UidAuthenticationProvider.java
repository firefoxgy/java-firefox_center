package com.firefox.center.oauth.auth.uid;

import com.firefox.center.common.model.LoginAppUser;
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
public class UidAuthenticationProvider implements AuthenticationProvider {
    private MyUserDetailService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        UidAuthenticationToken authenticationToken = (UidAuthenticationToken) authentication;
        String appId = (String) authenticationToken.getAppId();
        Integer tenantId = (Integer) authenticationToken.getTenantId();
        String uid = (String) authenticationToken.getPrincipal();
        LoginAppUser user = userDetailsService.loadAppUserByUid(appId, tenantId, Long.valueOf(uid));
        if (user == null) {
            throw new InternalAuthenticationServiceException("用户不存在");
        }
        UidAuthenticationToken authenticationResult = new UidAuthenticationToken(user, uid, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UidAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
