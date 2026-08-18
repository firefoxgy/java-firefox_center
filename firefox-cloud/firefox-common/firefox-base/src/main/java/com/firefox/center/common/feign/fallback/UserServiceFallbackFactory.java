package com.firefox.center.common.feign.fallback;

import com.firefox.center.common.feign.pojo.ThirdDTO;
import com.firefox.center.common.model.*;
import com.firefox.center.common.feign.UserFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * userService降级工场
 *
 * @Author: sujie
 * @date 2021/04/18
 */
@Slf4j
@Component
public class UserServiceFallbackFactory implements FallbackFactory<UserFeignService> {
    @Override
    public UserFeignService create(Throwable throwable) {
        return new UserFeignService() {
            @Override
            public LoginAppUser findAppUserById(String id) {
                log.error("通过id查询App用户异常:{}", id, throwable);
                return null;
            }

            @Override
            public LoginAppUser findAppUserByOpenId(String appId, Integer tenantId, String openId) {
                log.error("通过openId查询用户异常:{}", appId+":"+tenantId+":"+openId, throwable);
                return null;
            }

            @Override
            public LoginAppUser findAppUserByUid(String appId, Integer tenantId, long uid) {
                log.error("通过username查询用户异常:{}", appId+":"+tenantId+":"+uid, throwable);
                return null;
            }

            @Override
            public LoginAppUser findAppUserByUsername(String clientType, String version, String appId, Integer tenantId, String username) {
                log.error("通过username查询用户异常:{}", appId+":"+tenantId+":"+username, throwable);
                return null;
            }

            @Override
            public LoginAppUser findAppUserByPhone2(String appId, Integer tenantId, String phone) {
                log.error("通过phone查询用户异常:{}", appId+":"+tenantId+":"+phone, throwable);
                return null;
            }

            @Override
            public LoginAppUser findAppUserByPhone(String clientType, String version, String appId, Integer tenantId, String phone) {
                log.error("通过phone查询用户异常:{}", appId+":"+tenantId+":"+phone, throwable);
                return null;
            }

            @Override
            public LoginAppUser findOrRegByphone(String clientType, String version, String appId, Integer tenantId, String phone, String password, String username) {
                log.error("通过phone查询注册用户异常:{}", appId+":"+tenantId+":"+phone+":"+password+":"+username, throwable);
                return null;
            }

            @Override
            public LoginAppUser findAppUserByEmail2(String appId, Integer tenantId, String email) {
                log.error("通过email查询用户异常:{}", appId+":"+tenantId+":"+email, throwable);
                return null;
            }

            @Override
            public LoginAppUser findAppUserByEmail(String clientType, String version, String appId, Integer tenantId, String email) {
                log.error("通过email查询用户异常:{}", appId+":"+tenantId+":"+email, throwable);
                return null;
            }

            @Override
            public LoginAdminUser findAdminUserByUserId(String appId, Integer tenantId, Long userId) {
                log.error("通过userId查询Admin用户异常:{}", appId+":"+tenantId+":"+userId, throwable);
                return null;
            }

            @Override
            public LoginAdminUser findAdminUserByUsername(String appId, Integer tenantId, String username) {
                log.error("通过username查询Admin用户异常:{}", appId+":"+tenantId+":"+username, throwable);
                return null;
            }

            @Override
            public LoginThirdUser findThirdUser(String clientType, String version, String appId, Integer tenantId, String loginType, String thirdid) {
                log.error("通过thirdid查询用户异常:{}", appId+":"+tenantId+":"+loginType+":"+thirdid, throwable);
                return null;
            }

            @Override
            public LoginThirdUser findThirdUserById(String sid) {
                log.error("通过id查询third用户异常:{}",sid , throwable);
                return null;
            }

            @Override
            public LoginThirdUser findThirdUserBySId(String appId, Integer tenantId, String sid) {
                log.error("通过sid查询用户异常:{}", appId+":"+tenantId+":"+sid, throwable);
                return null;
            }

            @Override
            public LoginThirdUser regUserFromThird(ThirdDTO thirdDTO) {
                log.error("通过thirdid注册App用户异常:{}",thirdDTO , throwable);
                return null;
            }

            @Override
            public LoginThirdUser regSUserFromThird(ThirdDTO thirdDTO) {
                log.error("通过thirdid注册第三方用户异常:{}",thirdDTO , throwable);
                return null;
            }

            @Override
            public LoginThirdUser findUserByUid(String uid) {
                log.error("通过uid查询用户异常:{}", uid, throwable);
                return null;
            }

            @Override
            public LoginThirdUser findUserBySid(String loginType, String sid) {
                log.error("通过sid查询用户异常:{}", loginType+":"+sid, throwable);
                return null;
            }

            @Override
            public boolean editUserPassword(String appId, Integer tenantId, String phone, String password) {
                log.error("修改用户密码异常:{}", phone+":"+password, throwable);
                return false;
            }

            @Override
            public boolean editUserPasswordByMail(String appId, Integer tenantId, String mail, String password) {
                log.error("修改用户密码异常:{}", mail+":"+password, throwable);
                return false;
            }

            @Override
            public TUserApp getAppUserInfo(String appId, Integer tenantId, Long uid) {
                log.error("获取App用户异常异常:{}", uid, throwable);
                return null;
            }

            @Override
            public TUserThird getThirdUserInfo(String appId, Integer tenantId, Long sid) {
                log.error("获取三方用户异常异常:{}", sid, throwable);
                return null;
            }
        };
    }
}
