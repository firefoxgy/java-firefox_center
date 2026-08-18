package com.firefox.center.oauth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.R;
import com.firefox.center.common.constants.Consts;
import com.firefox.center.common.constants.FileNameConstants;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.feign.UserFeignService;
import com.firefox.center.common.feign.pojo.ThirdDTO;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.model.LoginAppUser;
import com.firefox.center.common.model.LoginThirdUser;
import com.firefox.center.common.sms.service.SMSCommonService;
import com.firefox.center.common.utils.SpringUtil;
import com.firefox.center.oauth.auth.apple.AppleAuthenticationToken;
import com.firefox.center.oauth.auth.apple.util.AppleUtil;
import com.firefox.center.oauth.auth.mailCode.MailCodeAuthenticationToken;
import com.firefox.center.oauth.auth.mailPassword.MailPasswordAuthenticationToken;
import com.firefox.center.oauth.auth.qq.QQAuthenticationToken;
import com.firefox.center.oauth.auth.sid.SidAuthenticationToken;
import com.firefox.center.oauth.auth.smsCode.SmsCodeAuthenticationToken;
import com.firefox.center.oauth.auth.smsPassword.SmsPasswordAuthenticationToken;
import com.firefox.center.oauth.auth.uPassword.UPasswordAuthenticationToken;
import com.firefox.center.oauth.auth.uid.UidAuthenticationToken;
import com.firefox.center.oauth.auth.weibo.WeiboAuthenticationToken;
import com.firefox.center.oauth.auth.wx.WxAuthenticationToken;
import com.firefox.center.oauth.db.model.OauthCenter;
import com.firefox.center.oauth.db.model.OauthTenant;
import com.firefox.center.oauth.db.model.OauthTenantApp;
import com.firefox.center.oauth.db.service.OauthCenterService;
import com.firefox.center.oauth.db.service.OauthTenantAppAppService;
import com.firefox.center.oauth.db.service.OauthTenantService;
import com.firefox.center.oauth.service.CacheService;
import com.firefox.center.oauth.service.MyUserDetailService;
import com.firefox.center.oauth.service.RedisClientDetailsService;
import com.firefox.center.user.feign.MailFeignService;
import com.firefox.center.user.feign.SmsFeignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OAuth2 App登录相关操作
 *
 * @Author: sujie
 */
@Api(tags = "OAuth2 App登录相关操作")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth/app")
public class OAuth2AppController {

    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationServerTokenServices authorizationServerTokenServices;
    private final AuthenticationManager authenticationManager;
    private final OauthTenantAppAppService oauthTenantAppAppService;
    private final OauthTenantService oauthTenantService;
    private final UserFeignService userFeignService;
    private final SmsFeignService smsFeignService;
    private final MailFeignService mailFeignService;
    private final CacheService cacheService;
    private final OauthCenterService oauthCenterService;
    private final SMSCommonService sMSCommonService;
    private final MyUserDetailService userDetailsService;


    private static final String DEFAULT_VERSION="0.0";


    @ApiOperation(value = "App端通过手机号找回密码")
    @PostMapping("/sms/setpwd")
    public R<T> smsSetpwd(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "phone", value = "账号") String phone,
            @NotBlank @ApiParam(required = true, name = "code", value = "密码") String code,
            @NotBlank @ApiParam(required = true, name = "password", value = "密码") String password,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(!StrKit.isMobileNumber(phone)){
            return R.error(CodeEnum.PHONE_CHECK_ERROR);
        }
        //验证验证码
        if(StrKit.isBlank(code)){
            return R.error(CodeEnum.PHONE_VERIFICATION_CODE_NULL);
        }
        R r=smsFeignService.checkCode(client_id, tenant_id, phone, code);
        if(!r.isSuccess()){
            if(r.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                return R.error(CodeEnum.VERIFICATION_CODE_EMPTY);
            }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                return R.error(CodeEnum.VERIFICATION_CODE_EXPIRED);
            }
            return R.error(CodeEnum.VERIFICATION_CODE_EMPTY);
        }
        if(StrKit.isBlank(password)){
            return R.error(CodeEnum.PASSWORD_VERIFICATION_FAILURE);
        }
        if(StrKit.isNull(userFeignService.findAppUserByPhone2(client_id, tenant_id, phone))){
            return R.error(CodeEnum.UID_NOT_FOUND);
        }
        if(userFeignService.editUserPassword(client_id, tenant_id, phone, password)){
            return R.ok("设置成功");
        }
        return R.ok("设置失败");
    }

    @ApiOperation(value = "App端通过邮箱找回密码")
    @PostMapping("/mail/setpwd")
    public R<T> mailSetpwd(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "mail", value = "邮箱") String mail,
            @NotBlank @ApiParam(required = true, name = "code", value = "密码") String code,
            @NotBlank @ApiParam(required = true, name = "password", value = "密码") String password,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(StrKit.isBlank(mail)){
            return R.error(CodeEnum.MAIL_NULL);
        }
        //验证验证码
        if(StrKit.isBlank(code)){
            return R.error(CodeEnum.MAIL_CODE_NULL);
        }
        R r=mailFeignService.checkCode(client_id, tenant_id, mail, code);
        if(!r.isSuccess()){
            if(r.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                return R.error(CodeEnum.VERIFICATION_CODE_EMPTY);
            }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                return R.error(CodeEnum.VERIFICATION_CODE_EXPIRED);
            }
            return R.error(CodeEnum.VERIFICATION_CODE_EMPTY);
        }
        if(StrKit.isBlank(password)){
            return R.error(CodeEnum.PASSWORD_VERIFICATION_FAILURE);
        }
        if(StrKit.isNull(userFeignService.findAppUserByEmail2(client_id, tenant_id, mail))){
            return R.error(CodeEnum.UID_NOT_FOUND);
        }
        if(userFeignService.editUserPasswordByMail(client_id, tenant_id, mail, password)){
            return R.ok("设置成功");
        }
        return R.ok("设置失败");
    }


    @ApiOperation(value = "App端手机号密码获取token")
    @PostMapping("/smspassword/token")
    public void getTokenByPassword(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "client_type", value = "客户端类型 andriod(安卓)|ios(苹果)|wxmp(微信公众号)|wxmini(微信小程序)") String client_type,
            @ApiParam(name = "version", value = "客户端version版本号") String version,
            @NotBlank @ApiParam(required = true, name = "phone", value = "手机号") String phone,
            @NotBlank @ApiParam(required = true, name = "password", value = "密码") String password,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(StrKit.isBlank(version)){
            version=DEFAULT_VERSION;
        }
        LoginAppUser user = userDetailsService.loadAppUserByphone(client_type, version, client_id, tenant_id, phone);
        if(StrKit.isNull(user)){
            exceptionHandler(response, R.error(CodeEnum.USER_NAME_OR_PASSWORD_ERROR));
            return;
        }
        if(user.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        SmsPasswordAuthenticationToken token = new SmsPasswordAuthenticationToken(client_type, version, client_id, tenant_id, phone, password);
        writerToken(request, response, client_id, client_secret, tenant_id, user.getUid(), token, "手机号或密码错误");
    }

    @ApiOperation(value = "App端帐号密码获取token")
    @PostMapping("/upassword/token")
    public void getTokenByUPassword(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "client_type", value = "客户端类型 andriod(安卓)|ios(苹果)|wxmp(微信公众号)|wxmini(微信小程序)") String client_type,
            @ApiParam(name = "version", value = "客户端version版本号") String version,
            @NotBlank @ApiParam(required = true, name = "username", value = "账号") String username,
            @NotBlank @ApiParam(required = true, name = "password", value = "密码") String password,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(StrKit.isBlank(version)){
            version=DEFAULT_VERSION;
        }
        LoginAppUser user = userDetailsService.loadAppUserByUsername(client_type, version, client_id, tenant_id, username);
        if(StrKit.isNull(user)){
            exceptionHandler(response, R.error(CodeEnum.USER_NAME_OR_PASSWORD_ERROR));
            return;
        }
        if(user.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        UPasswordAuthenticationToken token = new UPasswordAuthenticationToken(client_type, version, client_id, tenant_id, username, password);
        writerToken(request, response, client_id, client_secret, tenant_id, user.getUid(), token, "用户名或密码错误");
    }

    @ApiOperation(value = "App端手机号验证码获取token")
    @PostMapping("/smscode/token")
    public void getTokenBySms(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "client_type", value = "客户端类型 andriod(安卓)|ios(苹果)|wxmp(微信公众号)|wxmini(微信小程序)") String client_type,
            @ApiParam(name = "version", value = "客户端version版本号") String version,
            @NotBlank @ApiParam(required = true, name = "phone", value = "账号") String phone,
            @NotBlank @ApiParam(required = true, name = "code", value = "验证码") String code,
            @ApiParam(name = "password", value = "用户登录密码") String password,
            @ApiParam(name = "username", value = "用户登录帐号") String username,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(StrKit.isBlank(version)){
            version=DEFAULT_VERSION;
        }
        if(!StrKit.isMobileNumber(phone)){
            exceptionHandler(response, R.error(CodeEnum.PHONE_CHECK_ERROR));
            return;
        }
        //验证验证码
        if(StrKit.isBlank(code)){
            exceptionHandler(response, R.error(CodeEnum.PHONE_VERIFICATION_CODE_NULL));
            return;
        }
        R r=smsFeignService.checkCode(client_id, tenant_id, phone, code);
        if(!r.isSuccess()){
            if(r.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                return;
            }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EXPIRED));
                return;
            }
            exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
            return;
        }
        username=username==null?"":username;
        LoginAppUser loginAppUser=userFeignService.findOrRegByphone(client_type, version, client_id, tenant_id, phone, password, username);
        if(StrKit.isNull(loginAppUser)){
            exceptionHandler(response, R.error(CodeEnum.USER_NAME_OR_PASSWORD_ERROR));
            return;
        }
        if(loginAppUser.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        SmsCodeAuthenticationToken token = new SmsCodeAuthenticationToken(client_type, version, client_id, tenant_id, phone, phone);
        writerToken(request, response, client_id, client_secret, tenant_id, loginAppUser.getUid(), token, "手机号或验证码错误");
    }

    @ApiOperation(value = "App端邮箱验证码获取token")
    @PostMapping("/mailCode/token")
    public void getTokenByEmailCode(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "client_type", value = "客户端类型 andriod(安卓)|ios(苹果)|wxmp(微信公众号)|wxmini(微信小程序)") String client_type,
            @ApiParam(name = "version", value = "客户端version版本号") String version,
            @NotBlank @ApiParam(required = true, name = "mail", value = "邮箱") String mail,
            @NotBlank @ApiParam(required = true, name = "code", value = "验证码") String mailCode,
            @ApiParam(name = "nickname", value = "密码") String nickname,
            @ApiParam(name = "figureurl", value = "密码") String figureurl,
            @ApiParam(name = "gender", value = "性别") Integer gender,
            @ApiParam(name = "phone", value = "手机号") String phone,
            @ApiParam(required = true, name = "code", value = "验证码") String phoneCode,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        String grantType=Consts.grantType.MAIL;
        if(StrKit.isBlank(version)){
            version=DEFAULT_VERSION;
        }
        if(!StrKit.isEmail(mail)){
            exceptionHandler(response, R.error(CodeEnum.MAIL_CHECK_ERROR));
            return;
        }
        //验证验证码
        if(StrKit.isBlank(mailCode)){
            exceptionHandler(response, R.error(CodeEnum.PHONE_VERIFICATION_CODE_NULL));
            return;
        }
        R r1=mailFeignService.checkCode(client_id, tenant_id, mail, mailCode);
        if(!r1.isSuccess()){
            if(r1.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                return;
            }else if(r1.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EXPIRED));
                return;
            }
            exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
            return;
        }
        LoginThirdUser loginThirdUser=userFeignService.findThirdUser(client_type, version, client_id, tenant_id, grantType, mail);
        if(loginThirdUser==null){//thirdid没有注册过
            //验证手机号
            if(StrKit.isNotBlank(phone)){
                if(!StrKit.isMobileNumber(phone)){
                    exceptionHandler(response, R.error(CodeEnum.PHONE_CHECK_ERROR));
                    return;
                }
                if(StrKit.isBlank(phoneCode)){
                    exceptionHandler(response, R.error(CodeEnum.PHONE_VERIFICATION_CODE_NULL));
                    return;
                }
                //验证验证码
                R r2=sMSCommonService.checkCode(client_id, tenant_id, phone, phoneCode);
                if(!r2.isSuccess()){
                    if(r2.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                        exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                        return;
                    }else if(r2.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                        exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EXPIRED));
                        return;
                    }
                    exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                    return;
                }
            }
            ThirdDTO thirdDTO=ThirdDTO.builder()
                    .clientType(client_type)
                    .version(version)
                    .appId(client_id)
                    .tenantId(tenant_id)
                    .phone(phone)
                    .authType(grantType)
                    .externalappid("")
                    .thirdid(mail)
                    .nickname(nickname)
                    .figureurl(figureurl)
                    .gender(gender)
                    .phone(phone)
                    .build();
            loginThirdUser=userFeignService.regUserFromThird(thirdDTO);
        }
        if(loginThirdUser.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        MailCodeAuthenticationToken token = new MailCodeAuthenticationToken(client_type, version, client_id, tenant_id, mail, mail);
        writerToken(request, response, client_id, client_secret, tenant_id, loginThirdUser.getSid(), token, "邮箱验证码登录错误");
    }

    @ApiOperation(value = "邮箱密码获取token")
    @PostMapping("/mailPassword/token")
    public void getTokenByEmail(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "client_type", value = "客户端类型 andriod(安卓)|ios(苹果)|wxmp(微信公众号)|wxmini(微信小程序)") String client_type,
            @ApiParam(name = "version", value = "客户端version版本号") String version,
            @NotBlank @ApiParam(required = true, name = "mail", value = "邮箱") String mail,
            @NotBlank @ApiParam(required = true, name = "password", value = "密码") String password,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(StrKit.isBlank(version)){
            version=DEFAULT_VERSION;
        }
        LoginAppUser user = userDetailsService.loadAppUserByEmail(client_type, version, client_id, tenant_id, mail);
        if(StrKit.isNull(user)){
            exceptionHandler(response, R.error(CodeEnum.USER_NAME_OR_PASSWORD_ERROR));
            return;
        }
        if(user.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        MailPasswordAuthenticationToken token = new MailPasswordAuthenticationToken(client_type, version, client_id, tenant_id, mail, password);
        writerToken(request, response, client_id, client_secret, tenant_id, user.getUid(), token, "邮箱或密码错误");
    }

    @ApiOperation(value = "主帐号登录token换取token")
    @PostMapping("/uid/token")
    public void getTokenByUid(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {

        String bearerToken=request.getHeader("cw-authorization");
        //验证sid
        if(StrKit.isBlank(bearerToken)){
            exceptionHandler(response, R.error(CodeEnum.TOKEN_EMPTY));
            return;
        }
        String client_id="", uid="", client_type="", version=DEFAULT_VERSION;
        int tenant_id=0;
        if(bearerToken.startsWith("bearer ")){
            String token = bearerToken.split(" ")[1];
            try {
                //公钥解密校验
                ClassPathResource resource = new ClassPathResource(FileNameConstants.PUBLIC_KEY);
                String publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
                String tokenInfo = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey)).getClaims();
                JSONObject jsonToken = JSON.parseObject(tokenInfo);
                client_id=jsonToken.getString("client_id");
                client_type=jsonToken.getString("client_type");
                version=jsonToken.getString("version");
                uid=jsonToken.getString("uid");
                tenant_id=jsonToken.getInteger("tenant_id");
            } catch (Exception e) {
                e.printStackTrace();
                exceptionHandler(response, R.error(CodeEnum.NONE_AUTHORIZATION));
            }
        }
        //验证sid
        if(StrKit.isBlank(uid)){
            exceptionHandler(response, R.error(CodeEnum.UID_EMPTY));
            return;
        }
        LoginAppUser loginAppUser=userFeignService.findAppUserByUid(client_id, tenant_id, Long.valueOf(uid));
        if(loginAppUser==null){
            exceptionHandler(response, R.error(CodeEnum.USER_NOT_EXIST));
            return;
        }
        if(loginAppUser.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        UidAuthenticationToken token = new UidAuthenticationToken(client_type, version, client_id, tenant_id, uid);
        ClientDetails clientDetails = getClient(client_id, null);
        writerToken(request, response, clientDetails, tenant_id, loginAppUser.getUid(), token, "wuid登录错误");
    }

    @ApiOperation(value = "第三方登录token换取token")
    @PostMapping("/swap/token")
    public void getTokenBySid(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String bearerToken=request.getHeader("cw-authorization");
        //验证sid
        if(StrKit.isBlank(bearerToken)){
            exceptionHandler(response, R.error(CodeEnum.TOKEN_EMPTY));
            return;
        }
        String client_id="", sid="", client_type="", version=DEFAULT_VERSION;
        int tenant_id=0;
        if(bearerToken.startsWith("bearer ")){
            String token = bearerToken.split(" ")[1];
            try {
                //公钥解密校验
                ClassPathResource resource = new ClassPathResource(FileNameConstants.PUBLIC_KEY);
                String publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
                String tokenInfo = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey)).getClaims();
                JSONObject jsonToken = JSON.parseObject(tokenInfo);
                client_id=jsonToken.getString("client_id");
                client_type=jsonToken.getString("client_type");
                version=jsonToken.getString("version");
                sid=jsonToken.getString("sid");
                tenant_id=jsonToken.getInteger("tenant_id");
            } catch (Exception e) {
                e.printStackTrace();
                exceptionHandler(response, R.error(CodeEnum.NONE_AUTHORIZATION));
            }
        }
        //验证sid
        if(StrKit.isBlank(sid)){
            exceptionHandler(response, R.error(CodeEnum.SID_EMPTY));
            return;
        }
        LoginThirdUser loginThirdUser=userFeignService.findThirdUserBySId(client_id, tenant_id, sid);
        if(loginThirdUser==null){//thirdid没有注册过
            exceptionHandler(response, R.error(CodeEnum.USER_NOT_EXIST));
            return;
        }
        if(loginThirdUser.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        SidAuthenticationToken token = new SidAuthenticationToken(client_type, version, client_id, tenant_id, sid);
        ClientDetails clientDetails = getClient(client_id, null);
        writerToken(request, response, clientDetails, tenant_id, loginThirdUser.getSid(), token, "swap登录错误");
    }

    @ApiOperation(value = "qq获取token")
    @PostMapping("/qq/token")
    public void getTokenByQQ(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "client_type", value = "客户端类型 andriod(安卓)|ios(苹果)|wxmp(微信公众号)|wxmini(微信小程序)") String client_type,
            @ApiParam(name = "version", value = "客户端version版本号") String version,
            @NotBlank @ApiParam(required = true, name = "externalappid", value = "QQ appid") String externalappid,
            @NotBlank @ApiParam(required = true, name = "openid", value = "QQ openid") String openid,
            @ApiParam(name = "nickname", value = "密码") String nickname,
            @ApiParam(name = "figureurl", value = "密码") String figureurl,
            @ApiParam(name = "gender", value = "性别") Integer gender,
            @ApiParam(name = "phone", value = "手机号") String phone,
            @ApiParam(name = "code", value = "验证码") String code,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        String grantType=Consts.grantType.QQ;
        if(StrKit.isBlank(version)){
            version=DEFAULT_VERSION;
        }
        //验证thirdid
        if(StrKit.isBlank(openid)){
            exceptionHandler(response, R.error(CodeEnum.THIRD_ID_EMPTY));
            return;
        }
        LoginThirdUser loginThirdUser=userFeignService.findThirdUser(client_type, version, client_id, tenant_id, grantType, openid);
        if(loginThirdUser==null){//thirdid没有注册过
            //验证手机号
            if(StrKit.isNotBlank(phone)){
                if(!StrKit.isMobileNumber(phone)){
                    exceptionHandler(response, R.error(CodeEnum.PHONE_CHECK_ERROR));
                    return;
                }
                if(StrKit.isBlank(code)){
                    exceptionHandler(response, R.error(CodeEnum.PHONE_VERIFICATION_CODE_NULL));
                    return;
                }
                //验证验证码
                R r=sMSCommonService.checkCode(client_id, tenant_id, phone, code);
                if(!r.isSuccess()){
                    if(r.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                        exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                        return;
                    }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                        exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EXPIRED));
                        return;
                    }
                    exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                    return;
                }
            }
            ThirdDTO thirdDTO=ThirdDTO.builder()
                    .clientType(client_type)
                    .version(version)
                    .appId(client_id)
                    .tenantId(tenant_id)
                    .phone(phone)
                    .authType(grantType)
                    .externalappid(externalappid)
                    .thirdid(openid)
                    .nickname(nickname)
                    .figureurl(figureurl)
                    .gender(gender)
                    .phone(phone)
                    .build();
            loginThirdUser=userFeignService.regUserFromThird(thirdDTO);
        }
        if(loginThirdUser.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        QQAuthenticationToken token = new QQAuthenticationToken(client_type, version, client_id, tenant_id, openid, openid);
        writerToken(request, response, client_id, client_secret, tenant_id, loginThirdUser.getSid(), token, "qq登录错误");
    }

    @ApiOperation(value = "weixin获取token")
    @PostMapping("/weixin/token")
    public void getTokenByWeixin(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "client_type", value = "客户端类型 andriod(安卓)|ios(苹果)|wxmp(微信公众号)|wxmini(微信小程序)") String client_type,
            @ApiParam(name = "version", value = "客户端version版本号") String version,
            @NotBlank @ApiParam(required = true, name = "externalappid", value = "QQ appid") String externalappid,
            @NotBlank @ApiParam(required = true, name = "openid", value = "微信openid") String openid,
            @ApiParam(name = "unionid", value = "微信unionid") String unionid,
            @ApiParam(name = "nickname", value = "密码") String nickname,
            @ApiParam(name = "figureurl", value = "密码") String figureurl,
            @ApiParam(name = "gender", value = "密码") Integer gender,
            @ApiParam(name = "country", value = "密码") String country,
            @ApiParam(name = "province", value = "密码") String province,
            @ApiParam(name = "city", value = "密码") String city,
            @ApiParam(name = "phone", value = "手机号") String phone,
            @ApiParam(name = "code", value = "验证码") String code,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        String grantType=Consts.grantType.WEIXIN;
        if(StrKit.isBlank(version)){
            version=DEFAULT_VERSION;
        }
        //验证thirdid
        if(StrKit.isBlank(openid)){
            exceptionHandler(response, R.error(CodeEnum.THIRD_ID_EMPTY));
            return;
        }
        LoginThirdUser loginThirdUser=userFeignService.findThirdUser(client_type, version, client_id, tenant_id, grantType, openid);
        if(loginThirdUser==null){//thirdid没有注册过
            //验证手机号
            if(StrKit.isNotBlank(phone)){
                if(!StrKit.isMobileNumber(phone)){
                    exceptionHandler(response, R.error(CodeEnum.PHONE_CHECK_ERROR));
                    return;
                }
                if(StrKit.isBlank(code)){
                    exceptionHandler(response, R.error(CodeEnum.PHONE_VERIFICATION_CODE_NULL));
                    return;
                }
                //验证验证码
                R r=sMSCommonService.checkCode(client_id, tenant_id, phone, code);
                if(!r.isSuccess()){
                    if(r.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                        exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                        return;
                    }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                        exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EXPIRED));
                        return;
                    }
                    exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                    return;
                }
            }
            country=country==null?"":country;
            province=province==null?"":province;
            city=city==null?"":city;
            ThirdDTO thirdDTO=ThirdDTO.builder()
                    .appId(client_id)
                    .tenantId(tenant_id)
                    .phone(phone)
                    .authType(grantType)
                    .externalappid(externalappid)
                    .thirdid(openid)
                    .thirdUnionId(unionid)
                    .nickname(nickname)
                    .figureurl(figureurl)
                    .gender(gender)
                    .country(country)
                    .province(province)
                    .city(city)
                    .phone(phone)
                    .build();
            loginThirdUser=userFeignService.regUserFromThird(thirdDTO);
        }
        if(loginThirdUser.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        WxAuthenticationToken token = new WxAuthenticationToken(client_type, version, client_id, tenant_id, openid, openid);
        writerToken(request, response, client_id, client_secret, tenant_id, loginThirdUser.getSid(), token, "weixin登录错误");
    }

//    @ApiOperation(value = "weixin获取token")
//    @PostMapping("/weixin/token")
//    public void getTokenByWeixin(
//            @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
//            @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
//            @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
//            @ApiParam(required = true, name = "code", value = "邮箱") String code,
//            @ApiParam(name = "phone", value = "手机号") String phone,
//            @ApiParam(name = "code", value = "验证码") String code,
//            HttpServletRequest request, HttpServletResponse response) throws IOException {
//        checkApp(client_id, tenant_id);
//        //验证code
//        Assert.isTrue(StrKit.isNotBlank(code), CodeEnum.AUTH_CODE_EMPTY);
//        TTenantThirdConfDTO tTenantThirdConfDTO=configFeignService.getThirdConf(client_id, tenant_id, Consts.grantType.WEIXIN);
//        Assert.notNull(tTenantThirdConfDTO, CodeEnum.APP_THIRD_CONF_EMPTY);
//
//        //验证手机号
//        if(StrKit.isNotBlank(phone)){
//            Assert.isTrue(StrKit.isMobileNumber(phone), CodeEnum.PHONE_CHECK_ERROR);
//            Assert.isTrue(StrKit.isNotBlank(code), CodeEnum.PHONE_VERIFICATION_CODE_NULL);
//            //验证验证码
//            R r=sMSCommonService.checkCode(client_id, tenant_id, phone, code);
//            if(!r.isSuccess()){
//                if(r.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
//                    throw new BusinessException(CodeEnum.VERIFICATION_CODE_EMPTY);
//                }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
//                    throw new BusinessException(CodeEnum.VERIFICATION_CODE_EXPIRED);
//                }
//                throw new BusinessException(CodeEnum.VERIFICATION_CODE_EMPTY);
//            }
//        }
//        LoginThirdUser loginThirdUser=wxService.weChatLogin(tTenantThirdConfDTO, phone, code);
//        WxAuthenticationToken token = new WxAuthenticationToken(client_id, tenant_id, loginThirdUser.getThirdid(), loginThirdUser.getThirdid());
//        writerToken(request, response, client_id, client_secret, token, "weixin登录错误");
//    }

    @ApiOperation(value = "weibo获取token")
    @PostMapping("/weibo/token")
    public void getTokenByWeibo(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "client_type", value = "客户端类型 andriod(安卓)|ios(苹果)|wxmp(微信公众号)|wxmini(微信小程序)") String client_type,
            @ApiParam(name = "version", value = "客户端version版本号") String version,
            @NotBlank @ApiParam(required = true, name = "externalappid", value = "QQ appid") String externalappid,
            @NotBlank @ApiParam(required = true, name = "uid", value = "微博uid") String uid,
            @ApiParam(name = "nickname", value = "密码") String nickname,
            @ApiParam(name = "figureurl", value = "密码") String figureurl,
            @ApiParam(name = "gender", value = "密码") Integer gender,
            @ApiParam(name = "country", value = "密码") String country,
            @ApiParam(name = "province", value = "密码") String province,
            @ApiParam(name = "city", value = "密码") String city,
            @ApiParam(name = "phone", value = "手机号") String phone,
            @ApiParam(name = "code", value = "验证码") String code,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        String grantType=Consts.grantType.WEIBO;
        if(StrKit.isBlank(version)){
            version=DEFAULT_VERSION;
        }
        //验证thirdid
        if(StrKit.isBlank(uid)){
            exceptionHandler(response, R.error(CodeEnum.THIRD_ID_EMPTY));
            return;
        }
        LoginThirdUser loginThirdUser=userFeignService.findThirdUser(client_type, version, client_id, tenant_id, grantType, uid);
        if(loginThirdUser==null){//thirdid没有注册过
            //验证手机号
            if(StrKit.isNotBlank(phone)){
                if(!StrKit.isMobileNumber(phone)){
                    exceptionHandler(response, R.error(CodeEnum.PHONE_CHECK_ERROR));
                    return;
                }
                if(StrKit.isBlank(code)){
                    exceptionHandler(response, R.error(CodeEnum.PHONE_VERIFICATION_CODE_NULL));
                    return;
                }
                //验证验证码
                R r=sMSCommonService.checkCode(client_id, tenant_id, phone, code);
                if(!r.isSuccess()){
                    if(r.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                        exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                        return;
                    }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                        exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EXPIRED));
                        return;
                    }
                    exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                    return;
                }
            }
            country=country==null?"":country;
            province=province==null?"":province;
            city=city==null?"":city;
            ThirdDTO thirdDTO=ThirdDTO.builder()
                    .appId(client_id)
                    .tenantId(tenant_id)
                    .phone(phone)
                    .authType(grantType)
                    .externalappid(externalappid)
                    .thirdid(uid)
                    .nickname(nickname)
                    .figureurl(figureurl)
                    .gender(gender)
                    .country(country)
                    .province(province)
                    .city(city)
                    .phone(phone)
                    .build();
            loginThirdUser=userFeignService.regUserFromThird(thirdDTO);
        }
        if(loginThirdUser.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        WeiboAuthenticationToken token = new WeiboAuthenticationToken(client_type, version, client_id, tenant_id, uid, uid);
        writerToken(request, response, client_id, client_secret, tenant_id, loginThirdUser.getSid(), token, "weibo登录错误");
    }

    @ApiOperation(value = "appleId获取token")
    @PostMapping("/apple/token")
    public void getTokenByAppleId(
            @NotBlank @ApiParam(required = true, name = "client_id", value = "client_id") String client_id,
            @NotBlank @ApiParam(required = true, name = "client_secret", value = "client_secret") String client_secret,
            @NotNull @ApiParam(required = true, name = "tenant_id", value = "租户id") Integer tenant_id,
            @NotBlank @ApiParam(required = true, name = "client_type", value = "客户端类型 andriod(安卓)|ios(苹果)|wxmp(微信公众号)|wxmini(微信小程序)") String client_type,
            @ApiParam(name = "version", value = "客户端version版本号") String version,
            @NotBlank @ApiParam(required = true, name = "externalappid", value = "QQ appid") String externalappid,
            @NotBlank @ApiParam(required = true, name = "id_token ", value = "苹果ID登录返回的identityToken值") String id_token ,
            @ApiParam(name = "nickname", value = "密码") String nickname,
            @ApiParam(name = "figureurl", value = "密码") String figureurl,
            @ApiParam(name = "gender", value = "密码") Integer gender,
            @ApiParam(name = "phone", value = "手机号") String phone,
            @ApiParam(name = "code", value = "验证码") String code,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        String grantType=Consts.grantType.APPLEID;
        if(StrKit.isBlank(version)){
            version=DEFAULT_VERSION;
        }
        //验证id_token
        if(StrKit.isBlank(id_token)){
            exceptionHandler(response, R.error(CodeEnum.THIRD_ID_EMPTY));
            return;
        }
        JSONObject json=AppleUtil.verify(id_token);
        if(StrKit.isBlank(json)){
            exceptionHandler(response, R.error(CodeEnum.APPLE_TOKEN_EXPIRE));
            return;
        }
        String openid = (String) json.get("sub");
        LoginThirdUser loginThirdUser=userFeignService.findThirdUser(client_type, version, client_id, tenant_id, grantType, openid);
        if(loginThirdUser==null){//thirdid没有注册过
            //验证手机号
            if(StrKit.isNotBlank(phone)){
                if(!StrKit.isMobileNumber(phone)){
                    exceptionHandler(response, R.error(CodeEnum.PHONE_CHECK_ERROR));
                    return;
                }
                if(StrKit.isBlank(code)){
                    exceptionHandler(response, R.error(CodeEnum.PHONE_VERIFICATION_CODE_NULL));
                    return;
                }
                //验证验证码
                R r=sMSCommonService.checkCode(client_id, tenant_id, phone, code);
                if(!r.isSuccess()){
                    if(r.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                        exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                        return;
                    }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                        exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EXPIRED));
                        return;
                    }
                    exceptionHandler(response, R.error(CodeEnum.VERIFICATION_CODE_EMPTY));
                    return;
                }
            }
            ThirdDTO thirdDTO=ThirdDTO.builder()
                    .appId(client_id)
                    .tenantId(tenant_id)
                    .phone(phone)
                    .authType(grantType)
                    .externalappid(externalappid)
                    .thirdid(openid)
                    .nickname(nickname)
                    .figureurl(figureurl)
                    .gender(gender)
                    .phone(phone)
                    .build();
            loginThirdUser=userFeignService.regUserFromThird(thirdDTO);
        }
        if(loginThirdUser.getStatus()!=1){
            exceptionHandler(response, R.error(CodeEnum.USER_IS_DISABLED));
            return;
        }
        AppleAuthenticationToken token = new AppleAuthenticationToken(client_type, version, client_id, tenant_id, openid, openid);
        writerToken(request, response, client_id, client_secret, tenant_id, loginThirdUser.getSid(), token, "appleId登录错误");
    }

    private void writerToken(HttpServletRequest request,
                             HttpServletResponse response,
                             String clientId,
                             String clientSecret,
                             Integer tenant_id,
                             long uid,
                             AbstractAuthenticationToken token,
                             String badCredenbtialsMsg) throws IOException {
        try {
            ClientDetails clientDetails = getClient(clientId, clientSecret, null);
            checkApp(response, clientId, tenant_id);
            TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(), "customer");
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
            OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
            oAuth2Authentication.setAuthenticated(true);
            //缓存用户可以访问的中心
            cacheUserPath(clientId, tenant_id, uid);

            writerObj(response, R.ok(oAuth2AccessToken));
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            exceptionHandler(response, badCredenbtialsMsg);
        } catch (Exception e) {
            exceptionHandler(response, e);
        }
    }

    private void writerToken(HttpServletRequest request,
                             HttpServletResponse response,
                             ClientDetails clientDetails,
                             Integer tenant_id,
                             long uid,
                             AbstractAuthenticationToken token,
                             String badCredenbtialsMsg) throws IOException {
        try {
            TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientDetails.getClientId(), clientDetails.getScope(), "customer");
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
            OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
            oAuth2Authentication.setAuthenticated(true);
            //缓存用户可以访问的中心
            cacheUserPath(clientDetails.getClientId(), tenant_id, uid);

            writerObj(response, R.ok(oAuth2AccessToken));
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            exceptionHandler(response, badCredenbtialsMsg);
        } catch (Exception e) {
            exceptionHandler(response, e);
        }
    }

    private void exceptionHandler(HttpServletResponse response, Exception e) throws IOException {
        log.error("exceptionHandler-error:", e);
        exceptionHandler(response, e.getMessage());
    }

    private void exceptionHandler(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        writerObj(response, R.error(msg));
    }

    private void exceptionHandler(HttpServletResponse response, Object obj) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        writerObj(response, obj);
    }

    private void writerObj(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try(Writer writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(obj));
            writer.flush();
        }
    }

    private ClientDetails getClient(String clientId, RedisClientDetailsService clientDetailsService) {
        if (clientDetailsService == null) {
            clientDetailsService = SpringUtil.getBean(RedisClientDetailsService.class);
        }
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
        }
        return clientDetails;
    }

    private ClientDetails getClient(String clientId, String clientSecret, RedisClientDetailsService clientDetailsService) {
        if (clientDetailsService == null) {
            clientDetailsService = SpringUtil.getBean(RedisClientDetailsService.class);
        }
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
        } else if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
        }
        return clientDetails;
    }

    private void checkApp(HttpServletResponse response, String client_id, Integer tenant_id) throws IOException {
        //判断库里有没有租户id
        OauthTenant tSaasTenant=oauthTenantService.getById(tenant_id);
        if(StrKit.isNull(tSaasTenant)){
            writerObj(response, R.error(CodeEnum.TENANT_NOT_EXIST));
            return;
        }
        if(tSaasTenant.getStatus()!=1){
            writerObj(response, R.error(CodeEnum.TENANT_IS_DISABLED));
            return;
        }

        //判断应用下是否有该租户
        OauthTenantApp tMidTenantAppDTO=oauthTenantAppAppService.selectRecord(client_id, tenant_id);
        if(StrKit.isNull(tMidTenantAppDTO)){
            writerObj(response, R.error(CodeEnum.TENANT_NO_OAUTH));
            return;
        }
        if(tMidTenantAppDTO.getStatus()!=1){
            writerObj(response, R.error(CodeEnum.TENANT_IS_DISABLED));
            return;
        }
    }


    private void cacheUserPath(String clientId, Integer tenantId, long uid) {
        List<OauthCenter> centerList= oauthCenterService.selectList(clientId, tenantId);
        List<String> pathList= centerList.stream().map(center -> center.getPath()).collect(Collectors.toList());
        cacheService.cachePath(uid, pathList);
    }
}
