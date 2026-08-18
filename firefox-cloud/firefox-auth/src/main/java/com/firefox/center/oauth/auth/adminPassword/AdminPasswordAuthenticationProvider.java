package com.firefox.center.oauth.auth.adminPassword;

import com.firefox.center.common.model.LoginAdminUser;
import com.firefox.center.common.model.OauthTenantPackage;
import com.firefox.center.oauth.db.service.OauthTenantService;
import com.firefox.center.oauth.service.MyUserDetailService;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * @Author: sujie
 */
@Setter
public class AdminPasswordAuthenticationProvider implements AuthenticationProvider {
    private MyUserDetailService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        AdminPasswordAuthenticationToken authenticationToken = (AdminPasswordAuthenticationToken) authentication;
        String appId = (String) authenticationToken.getAppId();
        Integer tenantId = (Integer) authenticationToken.getTenantId();
        String username = (String) authenticationToken.getPrincipal();
        String password = (String) authenticationToken.getCredentials();
        String centers = (String) authenticationToken.getCenters();
        LoginAdminUser user = userDetailsService.loadAdminUserByUsername(appId, tenantId, username);
        user.setCenters(centers);

        if (user == null) {
            throw new InternalAuthenticationServiceException("帐号或密码错误");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("帐号或密码错误");
        }
        AdminPasswordAuthenticationToken authenticationResult = new AdminPasswordAuthenticationToken(user, password, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AdminPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
