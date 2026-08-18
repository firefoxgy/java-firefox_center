package com.firefox.center.oauth.auth.uPassword;

import com.firefox.center.common.model.LoginAppUser;
import com.firefox.center.common.utils.MD5Util;
import com.firefox.center.oauth.service.MyUserDetailService;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: sujie
 */
@Setter
public class UPasswordAuthenticationProvider implements AuthenticationProvider {
    private MyUserDetailService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        UPasswordAuthenticationToken authenticationToken = (UPasswordAuthenticationToken) authentication;
        String clientType = (String) authenticationToken.getClientType();
        String version = (String) authenticationToken.getVersion();
        String appId = (String) authenticationToken.getAppId();
        Integer tenantId = (Integer) authenticationToken.getTenantId();
        String username = (String) authenticationToken.getPrincipal();
        String password = (String) authenticationToken.getCredentials();
        LoginAppUser user = userDetailsService.loadAppUserByUsername(clientType, version, appId, tenantId, username);
        if (user == null) {
            throw new InternalAuthenticationServiceException("帐号或密码错误");
        }
        if(user.getPwdEncrypt()==1){
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("帐号或密码错误");
            }
        }else{
            if(!user.getPassword().equals(MD5Util.encrypt(MD5Util.encrypt(password)+user.getSalt()))){
                throw new BadCredentialsException("帐号或密码错误");
            }
        }
        UPasswordAuthenticationToken authenticationResult = new UPasswordAuthenticationToken(user, password, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
