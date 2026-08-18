package com.firefox.center.user.feign;

import com.firefox.center.common.Record;
import com.firefox.center.common.constrains.CommonConstant;
import com.firefox.center.common.enums.StatusEnum;
import com.firefox.center.common.feign.UserFeignService;
import com.firefox.center.common.feign.pojo.ThirdDTO;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.lang.StringUtils;
import com.firefox.center.common.model.LoginAdminUser;
import com.firefox.center.common.model.LoginAppUser;
import com.firefox.center.common.model.LoginThirdUser;
import com.firefox.center.common.utils.AesUtil;
import com.firefox.center.common.utils.IdGen;
import com.firefox.center.credit.feign.CreditFeignService;
import com.firefox.center.credit.feign.pojo.FeignTUserInfo;
import com.firefox.center.user.db.model.*;
import com.firefox.center.user.db.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/4/27 16:30
 */
@RestController
@RequiredArgsConstructor
public class UserServiceImpl implements UserFeignService {

    private final TUserAdminService tUserAdminService;
    private final TUserAppService tUserAppService;
    private final TUserOneService tUserOneService;
    private final TUserThirdService tUserThirdService;
    private final TUserAppThirdService tUserAppThirdService;


    private final CreditFeignService creditFeignService;

    @Override
    public LoginAppUser findAppUserById(String id) {
        TUserApp tUserApp=tUserAppService.selectById(Long.valueOf(id));
        if(tUserApp==null){
            return null;
        }
        tUserApp.setLastLoginTime(new Date());
        tUserAppService.updateById(tUserApp);
        LoginAppUser loginAppUser = new LoginAppUser();
        BeanUtils.copyProperties(tUserApp, loginAppUser);
        return loginAppUser;
    }

    @Override
    public LoginAppUser findAppUserByOpenId(String appId, Integer tenantId, String openId) {
        TUserApp tUserApp=tUserAppService.selectByOpenId(appId, tenantId, openId);
        if(tUserApp==null){
            return null;
        }
        tUserApp.setLastLoginTime(new Date());
        tUserAppService.updateById(tUserApp);
        LoginAppUser loginAppUser = new LoginAppUser();
        BeanUtils.copyProperties(tUserApp, loginAppUser);
        return loginAppUser;
    }

    @Override
    public LoginAppUser findAppUserByUid(String appId, Integer tenantId, long uid) {
        TUserApp tUserApp=tUserAppService.selectByUid(appId, tenantId, uid);
        if(tUserApp==null){
            return null;
        }
        tUserApp.setLastLoginTime(new Date());
        tUserAppService.updateById(tUserApp);
        LoginAppUser loginAppUser = new LoginAppUser();
        BeanUtils.copyProperties(tUserApp, loginAppUser);
        return loginAppUser;
    }

    @Override
    public LoginAppUser findAppUserByUsername(String clientType, String version, String appId, Integer tenantId, String username) {
        TUserApp tUserApp = null;
        tUserApp=tUserAppService.selectByUsername(appId, tenantId, username);
        if(tUserApp==null){
            return null;
        }
        tUserApp.setLastLoginTime(new Date());
        tUserAppService.updateById(tUserApp);
        LoginAppUser loginAppUser = new LoginAppUser();
        BeanUtils.copyProperties(tUserApp, loginAppUser);
        loginAppUser.setClientType(clientType);
        loginAppUser.setVersion(version);
        return loginAppUser;
    }

    @Override
    public LoginAppUser findAppUserByPhone2(String appId, Integer tenantId, String phone) {
        TUserApp tUserApp=tUserAppService.selectByPhone(appId, tenantId, phone);
        if(tUserApp==null){
            return null;
        }
        tUserApp.setLastLoginTime(new Date());
        tUserAppService.updateById(tUserApp);
        LoginAppUser loginAppUser = new LoginAppUser();
        BeanUtils.copyProperties(tUserApp, loginAppUser);
        return loginAppUser;
    }

    @Override
    public LoginAppUser findAppUserByPhone(String clientType, String version, String appId, Integer tenantId, String phone) {
        TUserApp tUserApp=tUserAppService.selectByPhone(appId, tenantId, phone);
        if(tUserApp==null){
            return null;
        }
        tUserApp.setLastLoginTime(new Date());
        tUserAppService.updateById(tUserApp);
        LoginAppUser loginAppUser = new LoginAppUser();
        BeanUtils.copyProperties(tUserApp, loginAppUser);
        loginAppUser.setClientType(clientType);
        loginAppUser.setVersion(version);
        return loginAppUser;
    }

    @Override
    public LoginAppUser findOrRegByphone(String clientType, String version, String appId, Integer tenantId, String phone, String password, String username) {
        TUserApp tUserApp=tUserAppService.selectByPhone(appId, tenantId, phone);
        if(tUserApp==null){
            tUserApp=regUserApp(clientType, version, appId, tenantId, phone, password, username);
        }
        tUserApp.setLastLoginTime(new Date());
        tUserAppService.updateById(tUserApp);
        LoginAppUser loginAppUser=new LoginAppUser();
        BeanUtils.copyProperties(tUserApp, loginAppUser);
        loginAppUser.setClientType(clientType);
        loginAppUser.setVersion(version);
        return loginAppUser;
    }

    @Override
    public LoginAppUser findAppUserByEmail2(String appId, Integer tenantId, String email) {
        TUserApp tUserApp=tUserAppService.selectByEmail(appId, tenantId, email);
        if(tUserApp==null){
            return null;
        }
        tUserApp.setLastLoginTime(new Date());
        tUserAppService.updateById(tUserApp);
        LoginAppUser loginAppUser = new LoginAppUser();
        BeanUtils.copyProperties(tUserApp, loginAppUser);
        return loginAppUser;
    }

    @Override
    public LoginAppUser findAppUserByEmail(String clientType, String version, String appId, Integer tenantId, String email) {
        TUserApp tUserApp=tUserAppService.selectByEmail(appId, tenantId, email);
        if(tUserApp==null){
            return null;
        }
        tUserApp.setLastLoginTime(new Date());
        tUserAppService.updateById(tUserApp);
        LoginAppUser loginAppUser = new LoginAppUser();
        BeanUtils.copyProperties(tUserApp, loginAppUser);
        loginAppUser.setClientType(clientType);
        loginAppUser.setVersion(version);
        return loginAppUser;
    }

    @Override
    public LoginAdminUser findAdminUserByUserId(String appId, Integer tenantId, Long userId) {
        TUserAdmin tUserAdmin=tUserAdminService.selectByUserId(appId, tenantId, userId);
        if(tUserAdmin==null){
            return null;
        }
        LoginAdminUser loginAdminUser = new LoginAdminUser();
        BeanUtils.copyProperties(tUserAdmin, loginAdminUser);
        return loginAdminUser;
    }

    @Override
    public LoginAdminUser findAdminUserByUsername(String appId, Integer tenantId, String username) {
        System.out.println("appId:" +appId + " tenantId:" + tenantId);
        TUserAdmin tUserAdmin = null;
        if(StringUtils.isEmpty(appId) && tenantId <= 0)
            tUserAdmin=tUserAdminService.selectByUsername(username);
        else
            tUserAdmin=tUserAdminService.selectByUsername(appId, tenantId, username);
        if(tUserAdmin==null){
            return null;
        }
        LoginAdminUser loginAdminUser = new LoginAdminUser();
        BeanUtils.copyProperties(tUserAdmin, loginAdminUser);
        return loginAdminUser;
    }

    @Override
    public LoginThirdUser findThirdUser(String clientType, String version, String appId, Integer tenantId, String loginType, String thirdid) {
        TUserThird tUserThird=tUserThirdService.selectRecordByThirdId(appId, tenantId, loginType, thirdid);
        if(tUserThird==null){
            return null;
        }

        tUserThird.setLastLoginTime(new Date());
        tUserThirdService.updateById(tUserThird);
        if(tUserThird.getUid()!=null && tUserThird.getUid()!=0L){
            TUserApp tUserApp=tUserAppService.selectByUid(appId, tenantId, tUserThird.getUid());
            tUserThird.setNickname(tUserApp.getNickname());
            tUserThird.setFigureurl(tUserApp.getHeaderImg());
            tUserThird.setGender(tUserApp.getGender());
        }
        LoginThirdUser loginThirdUser = new LoginThirdUser();
        BeanUtils.copyProperties(tUserThird, loginThirdUser);
        loginThirdUser.setClientType(clientType);
        loginThirdUser.setVersion(version);
        return loginThirdUser;
    }

    @Override
    public LoginThirdUser findThirdUserById(String sid) {
        TUserThird tUserThird=tUserThirdService.selectRecordById(Long.valueOf(sid));
        if(tUserThird==null){
            return null;
        }
        tUserThird.setLastLoginTime(new Date());
        tUserThirdService.updateById(tUserThird);
        if(tUserThird.getUid()!=null && tUserThird.getUid()!=0L){
            TUserApp tUserApp=tUserAppService.selectByUid(tUserThird.getAppId(), tUserThird.getTenantId(), tUserThird.getUid());
            tUserThird.setNickname(tUserApp.getNickname());
            tUserThird.setFigureurl(tUserApp.getHeaderImg());
            tUserThird.setGender(tUserApp.getGender());
        }
        LoginThirdUser loginThirdUser = new LoginThirdUser();
        BeanUtils.copyProperties(tUserThird, loginThirdUser);
        return loginThirdUser;
    }

    @Override
    public LoginThirdUser findThirdUserBySId(String appId, Integer tenantId, String sid) {
        TUserThird tUserThird=tUserThirdService.selectRecordBySId(appId, tenantId, Long.valueOf(sid));
        if(tUserThird==null){
            return null;
        }
        tUserThird.setLastLoginTime(new Date());
        tUserThirdService.updateById(tUserThird);
        if(tUserThird.getUid()!=null && tUserThird.getUid()!=0L){
            TUserApp tUserApp=tUserAppService.selectByUid(appId, tenantId, tUserThird.getUid());
            tUserThird.setNickname(tUserApp.getNickname());
            tUserThird.setFigureurl(tUserApp.getHeaderImg());
            tUserThird.setGender(tUserApp.getGender());
        }
        LoginThirdUser loginThirdUser = new LoginThirdUser();
        BeanUtils.copyProperties(tUserThird, loginThirdUser);
        return loginThirdUser;
    }

    @Override
    public LoginThirdUser regUserFromThird(@RequestBody ThirdDTO thirdDTO) {
        return regSUserFromThird(thirdDTO);
    }

    @Override
    public LoginThirdUser regSUserFromThird(@RequestBody ThirdDTO thirdDTO) {
        TUserThird tUserThird=tUserThirdService.selectRecordByThirdId(thirdDTO.getAppId(), thirdDTO.getTenantId(), thirdDTO.getAuthType(), thirdDTO.getThirdid());
        TUserApp tUserApp=null;
        long id= IdGen.getId();
        if(tUserThird==null){
            //没有查到注册第三方登录
            tUserThird= TUserThird.builder()
                    .id(id)
                    .sid(id)
                    .loginType(thirdDTO.getAuthType())
                    .externalappid(thirdDTO.getExternalappid())
                    .thirdid(thirdDTO.getThirdid())
                    .status(StatusEnum.ENABLE.getCode())
                    .appId(thirdDTO.getAppId())
                    .tenantId(thirdDTO.getTenantId())
                    .build();
            if(StrKit.notBlank(thirdDTO.getNickname())){
                tUserThird.setNickname(thirdDTO.getNickname());
            }
            if(StrKit.notBlank(thirdDTO.getFigureurl())){
                tUserThird.setFigureurl(thirdDTO.getFigureurl());
            }
            if(StrKit.notBlank(thirdDTO.getGender())){
                tUserThird.setGender(thirdDTO.getGender());
            }
            if(StrKit.notBlank(thirdDTO.getThirdUnionId())){
                tUserThird.setThirdUnionId(thirdDTO.getThirdUnionId());
            }
            if(StrKit.notBlank(thirdDTO.getCountry())){
                tUserThird.setCountry(thirdDTO.getCountry());
            }
            if(StrKit.notBlank(thirdDTO.getProvince())){
                tUserThird.setProvince(thirdDTO.getProvince());
            }
            if(StrKit.notBlank(thirdDTO.getCity())){
                tUserThird.setCity(thirdDTO.getCity());
            }
            tUserThirdService.save(tUserThird);

            if(StrKit.notBlank(thirdDTO.getPhone())){//手机号参数不为空
                tUserApp=tUserAppService.selectByPhone(thirdDTO.getAppId(), thirdDTO.getTenantId(), thirdDTO.getPhone());
                //手机号没注册,用手机号注册统一用户，并关联手机帐号
                if(tUserApp==null){
                    tUserApp=regUserApp(thirdDTO.getClientType(), thirdDTO.getVersion(), thirdDTO.getAppId(), thirdDTO.getTenantId(), thirdDTO.getPhone(), "", thirdDTO.getPhone());
                }
                //关联第三方帐号
                TUserAppThird tUserAppThird= TUserAppThird.builder()
                        .uid(tUserApp.getUid())
                        .sid(tUserThird.getSid())
                        .build();
                tUserAppThirdService.save(tUserAppThird);

                tUserThird.setUid(tUserApp.getUid());
                tUserThird.setPhone(thirdDTO.getPhone());
                tUserThird.setOpenId(tUserApp.getOpenId());

                //保存排行用户信息
                FeignTUserInfo feignTUserInfo = FeignTUserInfo.builder()
                        .appId(thirdDTO.getAppId())
                        .tenantId(thirdDTO.getTenantId())
                        .uid(tUserApp.getUid())
                        .type("app")
                        .username(tUserApp.getUsername())
                        .nickname(tUserApp.getNickname())
                        .headerImg(tUserApp.getHeaderImg())
                        .gender(tUserApp.getGender()==null?0:tUserApp.getGender())
                        .build();
                creditFeignService.saveUserInfo(feignTUserInfo);
            }else{
                //保存排行用户信息
                FeignTUserInfo feignTUserInfo = FeignTUserInfo.builder()
                        .appId(thirdDTO.getAppId())
                        .tenantId(thirdDTO.getTenantId())
                        .uid(id)
                        .type("third")
                        .build();
                if(StrKit.notBlank(thirdDTO.getNickname())){
                    feignTUserInfo.setNickname(thirdDTO.getNickname());
                }
                if(StrKit.notBlank(thirdDTO.getFigureurl())){
                    feignTUserInfo.setHeaderImg(thirdDTO.getFigureurl());
                }
                if(StrKit.notBlank(thirdDTO.getGender())){
                    feignTUserInfo.setGender(thirdDTO.getGender());
                }
                creditFeignService.saveUserInfo(feignTUserInfo);
            }
        }
        LoginThirdUser loginThirdUser = new LoginThirdUser();
        BeanUtils.copyProperties(tUserThird, loginThirdUser);

        loginThirdUser.setClientType(thirdDTO.getClientType());
        loginThirdUser.setVersion(thirdDTO.getVersion());
        return loginThirdUser;
    }

    @Override
    public LoginThirdUser findUserByUid(String uid) {
        return null;
    }

    @Override
    public LoginThirdUser findUserBySid(String loginType, String sid) {
        return null;
    }

    @Override
    public boolean editUserPassword(String appId, Integer tenantId, String phone, String password) {
        TUserApp tUserApp=tUserAppService.selectByPhone(appId, tenantId, phone);
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        String cryptPassword=encode.encode(password);
        tUserApp.setPassword(cryptPassword);
        tUserApp.setPassword02(AesUtil.encode(password));
        tUserApp.setPwdEncrypt(1);
        return tUserAppService.updateById(tUserApp);
    }

    @Override
    public boolean editUserPasswordByMail(String appId, Integer tenantId, String mail, String password) {
        TUserApp tUserApp=tUserAppService.selectByEmail(appId, tenantId, mail);
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        String cryptPassword=encode.encode(password);
        tUserApp.setPassword(cryptPassword);
        tUserApp.setPassword02(AesUtil.encode(password));
        tUserApp.setPwdEncrypt(1);
        return tUserAppService.updateById(tUserApp);
    }

    @Override
    public com.firefox.center.common.model.TUserApp getAppUserInfo(String appId, Integer tenantId, Long uid) {
        TUserApp tUserApp=tUserAppService.selectByUid(appId, tenantId, uid);
        com.firefox.center.common.model.TUserApp userApp=new com.firefox.center.common.model.TUserApp();
        BeanUtils.copyProperties(tUserApp, userApp);
        return userApp;
    }

    @Override
    public com.firefox.center.common.model.TUserThird getThirdUserInfo(String appId, Integer tenantId, Long sid) {
        TUserThird tUserThird=tUserThirdService.selectRecordBySId(appId, tenantId, sid);
        com.firefox.center.common.model.TUserThird userThird=new com.firefox.center.common.model.TUserThird();
        BeanUtils.copyProperties(tUserThird, userThird);
        return userThird;
    }


    protected TUserApp regUserApp(String clientType, String version, String appId, Integer tenantId, String phone, String password, String username){
        username= StrKit.notBlank(username)?username:phone;
        //注册手机号
        String password2=AesUtil.encode(password);
        if(StrKit.isBlank(password)){
            BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
            password=encode.encode(getPhoneLastStr(phone));
        }
        TUserOne tUserOne=tUserOneService.selectRecord(phone);
        String openId="";
        Long openIntId=0L;
        if(tUserOne==null) {
            Record record = IdGen.getMd5Id();
            openId = record.getStr("md5");
            openIntId = record.getLong("id");
            tUserOne = TUserOne.builder()
                    .id(IdGen.getId())
                    .openId(openId)
                    .openIntId(openIntId)
                    .phone(phone)
                    .build();
            tUserOneService.save(tUserOne);
        }else{
            openId=tUserOne.getOpenId();
            openIntId=tUserOne.getOpenIntId();
        }
        long id= IdGen.getId();
        TUserApp tUserApp= TUserApp.builder()
                .id(id)
                .openIntId(openIntId)
                .openId(openId)
                .uid(id)
                .username(username)
                .phone(phone)
                .password(password)
                .password02(password2)
                .nickname(getRandomNickname())
                .status(StatusEnum.ENABLE.getCode())
                .regFrom(clientType)
                .version(version)
                .appId(appId)
                .tenantId(tenantId)
                .build();
        tUserAppService.save(tUserApp);

        //保存排行用户信息
        FeignTUserInfo feignTUserInfo = FeignTUserInfo.builder()
                .appId(appId)
                .tenantId(tenantId)
                .uid(tUserApp.getUid())
                .type("app")
                .username(tUserApp.getUsername())
                .nickname(tUserApp.getNickname())
                .headerImg(tUserApp.getHeaderImg())
                .gender(tUserApp.getGender()==null?0:tUserApp.getGender())
                .build();
        creditFeignService.saveUserInfo(feignTUserInfo);

        return tUserApp;
    }

    protected String getPhoneLastStr(String phone){
        return phone.substring(phone.length()- CommonConstant.DEFAULT_PASSWORD_LENGTH);
    }

    protected String getRandomNickname(){
        return "kpid_"+ StringUtils.getRandom(16);
    }

}
