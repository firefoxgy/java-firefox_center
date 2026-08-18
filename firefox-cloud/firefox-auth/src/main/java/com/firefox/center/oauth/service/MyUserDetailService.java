package com.firefox.center.oauth.service;

import com.firefox.center.common.feign.UserFeignService;
import com.firefox.center.common.model.LoginAdminUser;
import com.firefox.center.common.model.LoginAppUser;
import com.firefox.center.common.model.LoginThirdUser;
import com.firefox.center.common.model.TUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * @Author: sujie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {

    private final UserFeignService userService;

    public TUser loadUserByUsername(String username) {
        String[] usernameArr=username.split("_");
        if(usernameArr.length!=2){
            throw new InternalAuthenticationServiceException("用户名或密码错误");
        }
        String type=usernameArr[0];
        if("app".equals(type)){
            LoginAppUser appUser=userService.findAppUserById(usernameArr[1]);
            if (appUser == null) {
                throw new InternalAuthenticationServiceException("用户名或密码错误");
            }
            return checkAppUser(appUser);
        }else{
            LoginThirdUser thirdUser=userService.findThirdUserById(usernameArr[1]);
            if (thirdUser == null) {
                throw new InternalAuthenticationServiceException("用户名或密码错误");
            }
            return checkThireUser(thirdUser);
        }
    }

    public LoginAdminUser loadAdminUserByUsername(String appId, Integer tenantId,String username) {
        LoginAdminUser loginAdminUser = userService.findAdminUserByUsername(appId, tenantId, username);
        return checkUser(loginAdminUser);
    }

    public LoginAppUser loadAppUserByUid(String appId, Integer tenantId,long uid) {
        LoginAppUser loginAppUser = userService.findAppUserByUid(appId, tenantId, uid);
        return checkUser(loginAppUser);
    }

    public LoginAppUser loadAppUserByUsername(String clientType, String version, String appId, Integer tenantId,String username) {
        LoginAppUser loginAppUser = userService.findAppUserByUsername(clientType, version, appId, tenantId, username);
        return checkUser(loginAppUser);
    }

    public LoginAppUser loadAppUserByphone(String clientType, String version, String appId, Integer tenantId,String phone) {
        LoginAppUser loginAppUser = userService.findAppUserByPhone(clientType, version, appId, tenantId, phone);
        return checkUser(loginAppUser);
    }

    public LoginAppUser loadAppUserByEmail(String clientType, String version, String appId, Integer tenantId,String email) {
        LoginAppUser loginAppUser = userService.findAppUserByEmail(clientType, version, appId, tenantId, email);
        return checkUser(loginAppUser);
    }

    public LoginThirdUser loadThirdUser(String clientType, String version, String appId, Integer tenantId, String loginType, String thirdId) {
        LoginThirdUser loginThirdUser = userService.findThirdUser(clientType, version, appId, tenantId, loginType, thirdId);
        return checkUser(loginThirdUser);
    }

    public LoginThirdUser loadThirdUserBySid(String appId, Integer tenantId, String sid) {
        LoginThirdUser loginThirdUser = userService.findThirdUserBySId(appId, tenantId, sid);
        return checkUser(loginThirdUser);
    }

    private TUser checkAppUser(LoginAppUser appUser) {
        if (appUser != null && !appUser.isEnabled()) {
            throw new DisabledException("用户已作废");
        }
        return appUser;
    }

    private TUser checkThireUser(LoginThirdUser thirdUser) {
        if (thirdUser != null && !thirdUser.isEnabled()) {
            throw new DisabledException("用户已作废");
        }
        return thirdUser;
    }

    private LoginAdminUser checkUser(LoginAdminUser loginAdminUser) {
        if (loginAdminUser != null && !loginAdminUser.isEnabled()) {
            throw new DisabledException("用户已作废");
        }
        return loginAdminUser;
    }

    private LoginAppUser checkUser(LoginAppUser loginAppUser) {
        if (loginAppUser != null && !loginAppUser.isEnabled()) {
            throw new DisabledException("用户已作废");
        }
        return loginAppUser;
    }

    private LoginThirdUser checkUser(LoginThirdUser loginThirdUser) {
        if (loginThirdUser != null && !loginThirdUser.isEnabled()) {
            throw new DisabledException("用户已作废");
        }
        return loginThirdUser;
    }

}
