package com.firefox.center.oauth.auth.smsPassword;

import com.firefox.center.common.model.LoginAppUser;
import com.firefox.center.common.utils.MD5Util;
import com.firefox.center.oauth.service.MyUserDetailService;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: sujie
 */
@Setter
public class SmsPasswordAuthenticationProvider implements AuthenticationProvider {
    private MyUserDetailService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        SmsPasswordAuthenticationToken authenticationToken = (SmsPasswordAuthenticationToken) authentication;
        String clientType = (String) authenticationToken.getClientType();
        String version = (String) authenticationToken.getVersion();
        String appId = (String) authenticationToken.getAppId();
        Integer tenantId = (Integer) authenticationToken.getTenantId();
        String phone = (String) authenticationToken.getPrincipal();
        String password = (String) authenticationToken.getCredentials();
        LoginAppUser user = userDetailsService.loadAppUserByphone(clientType, version, appId, tenantId, phone);
        if (user == null) {
            throw new InternalAuthenticationServiceException("手机号或密码错误");
        }
        if(user.getPwdEncrypt()==1){
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("手机号或密码错误");
            }
        }else{
            if(!user.getPassword().equals(MD5Util.encrypt(MD5Util.encrypt(password)+user.getSalt()))){
                throw new BadCredentialsException("手机号或密码错误");
            }
        }
        SmsPasswordAuthenticationToken authenticationResult = new SmsPasswordAuthenticationToken(user, password, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
