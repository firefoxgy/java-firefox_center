package com.firefox.center.common.feign;

import com.firefox.center.common.constants.ServiceNameConstants;
import com.firefox.center.common.feign.fallback.UserServiceFallbackFactory;
import com.firefox.center.common.feign.pojo.ThirdDTO;
import com.firefox.center.common.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: sujie
 */
@FeignClient(name = ServiceNameConstants.USER_SERVICE, fallbackFactory = UserServiceFallbackFactory.class)
public interface UserFeignService {

    @GetMapping(value = "/feign/findAppUserById.do")
    LoginAppUser findAppUserById(@RequestParam("id") String id);

    @GetMapping(value = "/feign/findAppUserByOpenId.do")
    LoginAppUser findAppUserByOpenId(@RequestParam("appId") String appId,
                                       @RequestParam("tenantId") Integer tenantId,
                                       @RequestParam("openId") String openId);

    @GetMapping(value = "/feign/findAppUserByUid.do")
    LoginAppUser findAppUserByUid(@RequestParam("appId") String appId,
                                       @RequestParam("tenantId") Integer tenantId,
                                       @RequestParam("uid") long uid);

    @GetMapping(value = "/feign/findAppUserByUsername.do")
    LoginAppUser findAppUserByUsername(@RequestParam("clientType") String clientType,
                                       @RequestParam("version") String version,
                                       @RequestParam("appId") String appId,
                                       @RequestParam("tenantId") Integer tenantId,
                                       @RequestParam("username") String username);

    @GetMapping(value = "/feign/findAppUserByPhone2.do")
    LoginAppUser findAppUserByPhone2(@RequestParam("appId") String appId,
                                    @RequestParam("tenantId") Integer tenantId,
                                    @RequestParam("phone") String phone);

    @GetMapping(value = "/feign/findAppUserByPhone.do")
    LoginAppUser findAppUserByPhone(@RequestParam("clientType") String clientType,
                                    @RequestParam("version") String version,
                                    @RequestParam("appId") String appId,
                                    @RequestParam("tenantId") Integer tenantId,
                                    @RequestParam("phone") String phone);

    @GetMapping(value = "/feign/getOrRegByphone.do")
    LoginAppUser findOrRegByphone(@RequestParam("clientType") String clientType,
                                  @RequestParam("version") String version,
                                  @RequestParam(name = "appId") String appId,
                                  @RequestParam(name = "tenantId") Integer tenantId,
                                  @RequestParam(name = "phone") String phone,
                                  @RequestParam(name = "password") String password,
                                  @RequestParam(name = "username") String username);

    @GetMapping(value = "/feign/findAppUserByEmail2.do")
    LoginAppUser findAppUserByEmail2(@RequestParam("appId") String appId,
                                    @RequestParam("tenantId") Integer tenantId,
                                    @RequestParam("email") String email);

    @GetMapping(value = "/feign/findAppUserByEmail.do")
    LoginAppUser findAppUserByEmail(@RequestParam("clientType") String clientType,
                                    @RequestParam("version") String version,
                                    @RequestParam("appId") String appId,
                                    @RequestParam("tenantId") Integer tenantId,
                                    @RequestParam("email") String email);

    @GetMapping(value = "/feign/findAdminUserByUserId.do")
    LoginAdminUser findAdminUserByUserId(@RequestParam("appId") String appId,
                                     @RequestParam("tenantId") Integer tenantId,
                                     @RequestParam("userId") Long userId);

    @GetMapping(value = "/feign/findAdminUserByUsername.do")
    LoginAdminUser findAdminUserByUsername(@RequestParam("appId") String appId,
                                  @RequestParam("tenantId") Integer tenantId,
                                  @RequestParam("username") String username);

    @GetMapping(value = "/feign/findThirdUser.do")
    LoginThirdUser findThirdUser(@RequestParam("clientType") String clientType,
                                 @RequestParam("version") String version,
                                 @RequestParam("appId") String appId,
                                 @RequestParam("tenantId") Integer tenantId,
                                 @RequestParam("loginType") String loginType,
                                 @RequestParam("thirdid") String thirdid);

    @GetMapping(value = "/feign/findThirdUserById.do")
    LoginThirdUser findThirdUserById(@RequestParam("sid") String sid);

    @GetMapping(value = "/feign/findThirdUserBySId.do")
    LoginThirdUser findThirdUserBySId(@RequestParam("appId") String appId,
                                      @RequestParam("tenantId") Integer tenantId,
                                      @RequestParam("sid") String sid);

    @PostMapping("/feign/getFromThirdId.do")
    LoginThirdUser regUserFromThird(@RequestBody ThirdDTO thirdDTO);

    @PostMapping("/feign/regSUserFromThird.do")
    LoginThirdUser regSUserFromThird(@RequestBody ThirdDTO thirdDTO);

    @GetMapping(value = "/feign/findUserByUid.do")
    LoginThirdUser findUserByUid(@RequestParam("uid") String uid);

    @GetMapping(value = "/feign/findUserBySid.do")
    LoginThirdUser findUserBySid(@RequestParam("loginType") String loginType,
                                 @RequestParam("sid") String sid);

    @PostMapping(value = "/feign/editUserPassword.do")
    boolean editUserPassword( @RequestParam("appId") String appId,
                              @RequestParam("tenantId") Integer tenantId,
                              @RequestParam("phone") String phone,
                              @RequestParam("password") String password
                              );

    @PostMapping(value = "/feign/editUserPasswordByMail.do")
    boolean editUserPasswordByMail( @RequestParam("appId") String appId,
                              @RequestParam("tenantId") Integer tenantId,
                              @RequestParam("mail") String mail,
                              @RequestParam("password") String password
    );


    @GetMapping(value = "/feign/getAppUserInfo.do")
    TUserApp getAppUserInfo(@RequestParam("appId") String appId,
                            @RequestParam("tenantId") Integer tenantId,
                            @RequestParam("uid") Long uid);

    @GetMapping(value = "/feign/getThirdUserInfo.do")
    TUserThird getThirdUserInfo(@RequestParam("appId") String appId,
                                @RequestParam("tenantId") Integer tenantId,
                                @RequestParam("sid") Long sid);

}
