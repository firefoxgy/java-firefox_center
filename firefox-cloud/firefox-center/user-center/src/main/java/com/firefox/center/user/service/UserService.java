package com.firefox.center.user.service;

import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.R;
import com.firefox.center.common.Record;
import com.firefox.center.common.constrains.CommonConstant;
import com.firefox.center.common.entity.FirefoxInfo;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.enums.StatusEnum;
import com.firefox.center.common.exception.BusinessException;
import com.firefox.center.common.kit.Assert;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.lang.StringUtils;
import com.firefox.center.common.utils.AesUtil;
import com.firefox.center.common.utils.AppleUtil;
import com.firefox.center.common.utils.IdGen;
import com.firefox.center.common.utils.MD5Util;
import com.firefox.center.credit.feign.CreditFeignService;
import com.firefox.center.credit.feign.pojo.FeignTCredit;
import com.firefox.center.credit.feign.pojo.FeignTUserInfo;
import com.firefox.center.user.Consts;
import com.firefox.center.user.db.model.TUserApp;
import com.firefox.center.user.db.model.TUserOne;
import com.firefox.center.user.db.model.TUserThird;
import com.firefox.center.user.db.model.TUserAppThird;
import com.firefox.center.user.db.service.TUserAppService;
import com.firefox.center.user.db.service.TUserOneService;
import com.firefox.center.user.db.service.TUserThirdService;
import com.firefox.center.user.db.service.TUserAppThirdService;
import com.firefox.center.user.pojo.oauth.vo.TUserAppVO;
import com.firefox.center.user.pojo.oauth.vo.TUserThirdIdVO;
import com.firefox.center.user.pojo.oauth.vo.TUserThirdVO;
import com.firefox.center.user.pojo.sms.dto.CheckMailDTO;
import com.firefox.center.common.sms.service.SMSCommonService;
import com.firefox.center.user.pojo.user.dto.*;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公司表 服务类
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final SMSCommonService sMSService;
    private final MailService mailService;
    private final TUserAppService tUserAppService;
    private final TUserOneService tUserOneService;
    private final TUserThirdService tUserThirdService;
    private final TUserAppThirdService tUserAppThirdService;

    private final CreditFeignService creditFeignService;

    public TUserAppVO getInfo(FirefoxInfo info) {
        TUserApp tUserApp=tUserAppService.selectByUid(info.getAppId(), info.getTenantId(), info.getUid());
        if(tUserApp==null){
            return null;
        }
        TUserAppVO tUserAppVO = new TUserAppVO();
        BeanUtils.copyProperties(tUserApp, tUserAppVO);
        return tUserAppVO;
    }

    public TUserThirdVO getThirdInfo(FirefoxInfo info) {
        TUserThird tUserThird=tUserThirdService.selectRecordBySId(info.getAppId(), info.getTenantId(), info.getSid());
        if(tUserThird==null){
            return null;
        }
        TUserThirdVO tUserThirdVO = new TUserThirdVO();
        BeanUtils.copyProperties(tUserThird, tUserThirdVO);
        return tUserThirdVO;
    }

    public void editphone(FirefoxInfo info, EditPhoneDTO editPhoneDTO){
        R r=sMSService.checkCode(info.getAppId(), info.getTenantId(), editPhoneDTO.getPhone(), editPhoneDTO.getCode());
        if(!r.isSuccess()){
            if(r.getCode()== CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                throw new BusinessException(CodeEnum.VERIFICATION_CODE_EMPTY);
            }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                throw new BusinessException(CodeEnum.VERIFICATION_CODE_EXPIRED);
            }
            throw new BusinessException(CodeEnum.VERIFICATION_CODE_EMPTY);
        }
        Assert.isFalse(info.getUid()==0L, CodeEnum.UID_NOT_FOUND);
        Assert.notBlank(info.getUid(), CodeEnum.UID_NOT_FOUND);
        TUserApp tUserApp=tUserAppService.selectByUid(info.getAppId(), info.getTenantId(), info.getUid());

        if(tUserApp.getUsername().equals(tUserApp.getPhone())){
            tUserApp.setUsername(editPhoneDTO.getPhone());
        }
        tUserApp.setPhone(editPhoneDTO.getPhone());

        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        String password=encode.encode(getPhoneLastStr(editPhoneDTO.getPhone()));
        tUserApp.setPassword(password);
        tUserApp.setPassword02(AesUtil.encode(getPhoneLastStr(editPhoneDTO.getPhone())));
        tUserApp.setPwdEncrypt(1);
        tUserAppService.updateById(tUserApp);
    }

    public void editmail(FirefoxInfo info, EditMailDTO editMailDTO){
        CheckMailDTO checkDTO=CheckMailDTO.builder()
                .appid(info.getAppId())
                .tenantid(info.getTenantId())
                .mail(editMailDTO.getMail())
                .code(editMailDTO.getCode())
                .build();
        R r=mailService.checkCode(checkDTO);
        if(!r.isSuccess()){
            if(r.getCode()== CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                throw new BusinessException(CodeEnum.VERIFICATION_CODE_EMPTY);
            }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                throw new BusinessException(CodeEnum.VERIFICATION_CODE_EXPIRED);
            }
            throw new BusinessException(CodeEnum.VERIFICATION_CODE_EMPTY);
        }
        Assert.isFalse(info.getUid()==0L, CodeEnum.UID_NOT_FOUND);
        Assert.notBlank(info.getUid(), CodeEnum.UID_NOT_FOUND);
        TUserApp tUserApp=tUserAppService.selectByUid(info.getAppId(), info.getTenantId(), info.getUid());

        tUserApp.setEmail(editMailDTO.getMail());
        tUserAppService.updateById(tUserApp);
    }

    public TUserApp bind(FirefoxInfo info, BindDTO bindDTO){
        TUserThird tUserThird=tUserThirdService.selectRecordBySId(info.getAppId(), info.getTenantId(), info.getSid());
        Assert.notNull(tUserThird, CodeEnum.UID_NOT_FOUND);

        TUserApp tUserApp=tUserAppService.selectByPhone(info.getAppId(), info.getTenantId(), bindDTO.getPhone());
        if(tUserApp==null){
            tUserApp=regUserApp(info, bindDTO);
        }
        TUserAppThird tUserAppThird= TUserAppThird.builder()
                .uid(tUserApp.getUid())
                .sid(tUserThird.getSid())
                .appId(info.getAppId())
                .tenantId(info.getTenantId())
                .build();
        tUserAppThirdService.save(tUserAppThird);

        //更新排行表的uid
        FeignTCredit feignTCredit = FeignTCredit.builder()
                .appId(info.getAppId())
                .tenantId(info.getTenantId())
                .uid(tUserApp.getUid())
                .sid(info.getSid())
                .build();
        creditFeignService.bindUid(feignTCredit);

        //保存排行用户信息
        FeignTUserInfo feignTUserInfo = FeignTUserInfo.builder()
                .appId(info.getAppId())
                .tenantId(info.getTenantId())
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

    protected TUserApp regUserApp(FirefoxInfo info, BindDTO bindDTO){
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        String password=encode.encode(getPhoneLastStr(bindDTO.getPhone()));
        String password2=AesUtil.encode(getPhoneLastStr(bindDTO.getPhone()));

        TUserOne tUserOne=tUserOneService.selectRecord(bindDTO.getPhone());
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
                    .phone(bindDTO.getPhone())
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
                .username(bindDTO.getPhone())
                .phone(bindDTO.getPhone())
                .password(password)
                .password02(password2)
                .nickname(getRandomNickname())
                .status(StatusEnum.ENABLE.getCode())
                .regFrom(info.getClientType())
                .version(info.getVersion())
                .appId(info.getAppId())
                .tenantId(info.getTenantId())
                .build();
        tUserAppService.save(tUserApp);
        return tUserApp;
    }

    public void editpwd(FirefoxInfo info, EditPwdDTO editPwdDTO){
        TUserApp tUserApp=tUserAppService.selectByUid(info.getAppId(), info.getTenantId(), info.getUid());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(tUserApp.getPwdEncrypt()==1){
            Assert.isTrue(passwordEncoder.matches(editPwdDTO.getPassword(), tUserApp.getPassword()), CodeEnum.USER_PASSWORD_ERROR);
        }else{
            Assert.isTrue(tUserApp.getPassword().equals(MD5Util.encrypt(MD5Util.encrypt(editPwdDTO.getPassword())+tUserApp.getSalt())), CodeEnum.USER_PASSWORD_ERROR);
        }
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        String password=encode.encode(editPwdDTO.getNewPassword());
        tUserApp.setPassword(password);
        tUserApp.setPassword02(AesUtil.encode(editPwdDTO.getNewPassword()));
        tUserApp.setPwdEncrypt(1);
        tUserAppService.updateById(tUserApp);
    }

    public void editAppInfo(FirefoxInfo info, EditInfoDTO editInfoDTO) {
        TUserApp tUserApp=tUserAppService.selectByUid(info.getAppId(), info.getTenantId(), info.getUid());
        Assert.notNull(tUserApp, CodeEnum.UID_NOT_FOUND);

        if(StrKit.notBlank(editInfoDTO.getNickname())){
            tUserApp.setNickname(editInfoDTO.getNickname());
        }
        if(StrKit.notBlank(editInfoDTO.getHeaderImg())){
            tUserApp.setHeaderImg(editInfoDTO.getHeaderImg());
        }
        if(StrKit.notBlank(editInfoDTO.getGender())){
            tUserApp.setGender(editInfoDTO.getGender());
        }
        tUserAppService.updateById(tUserApp);

        //保存排行用户信息
        FeignTUserInfo feignTUserInfo = FeignTUserInfo.builder()
                .appId(info.getAppId())
                .tenantId(info.getTenantId())
                .uid(tUserApp.getUid())
                .type("app")
                .username(tUserApp.getUsername())
                .nickname(tUserApp.getNickname())
                .headerImg(tUserApp.getHeaderImg())
                .gender(tUserApp.getGender()==null?0:tUserApp.getGender())
                .build();
        creditFeignService.saveUserInfo(feignTUserInfo);
    }

    public void editThirdInfo(FirefoxInfo info, EditInfoDTO editInfoDTO) {
        TUserThird tUserThird=tUserThirdService.selectRecordBySId(info.getAppId(), info.getTenantId(), info.getSid());
        Assert.notNull(tUserThird, CodeEnum.UID_NOT_FOUND);

        if(StrKit.notBlank(editInfoDTO.getNickname())){
            tUserThird.setNickname(editInfoDTO.getNickname());
        }
        if(StrKit.notBlank(editInfoDTO.getHeaderImg())){
            tUserThird.setFigureurl(editInfoDTO.getHeaderImg());
        }
        if(StrKit.notBlank(editInfoDTO.getGender())){
            tUserThird.setGender(editInfoDTO.getGender());
        }
        tUserThirdService.updateById(tUserThird);

        //保存排行用户信息
        FeignTUserInfo feignTUserInfo = FeignTUserInfo.builder()
                .appId(info.getAppId())
                .tenantId(info.getTenantId())
                .uid(info.getSid())
                .type("third")
                .nickname(tUserThird.getNickname())
                .headerImg(tUserThird.getFigureurl())
                .gender(tUserThird.getGender()==null?0:tUserThird.getGender())
                .build();
        creditFeignService.saveUserInfo(feignTUserInfo);
    }

    public List<TUserThirdIdVO> bindList(FirefoxInfo info) {
        List<TUserThird> list=tUserThirdService.selectBindList(info.getAppId(), info.getTenantId(), info.getUid());
        List<TUserThirdIdVO> voList= Lists.newArrayList();
        TUserThirdIdVO oauthUserThirdVO=null;
        for(TUserThird tUserThird:list){
            oauthUserThirdVO = new TUserThirdIdVO();
            BeanUtils.copyProperties(tUserThird, oauthUserThirdVO);
            voList.add(oauthUserThirdVO);
        }
        return voList;
    }

    public void bind(FirefoxInfo info, BindThirdInfoDTO bindThirdInfoDTO) {
        TUserThird tUserThird=tUserThirdService.selectRecordByThirdId(info.getAppId(), info.getTenantId(), bindThirdInfoDTO.getType(), bindThirdInfoDTO.getThirdid());
        if(tUserThird==null){
            String thirdid="";
            if(com.firefox.center.common.constants.Consts.grantType.APPLEID.equals(bindThirdInfoDTO.getType())){
                JSONObject json= AppleUtil.verify(bindThirdInfoDTO.getThirdid());
                Assert.notNull(json, CodeEnum.ID_TOKEN_INVALID);
                thirdid = (String) json.get("sub");
            }else{
                thirdid=bindThirdInfoDTO.getThirdid();
            }

            tUserThird = new TUserThird();
            long id= IdGen.getId();
            BeanUtils.copyProperties(bindThirdInfoDTO, tUserThird);
            tUserThird.setId(id);
            tUserThird.setSid(id);
            tUserThird.setThirdid(thirdid);
            if(StrKit.notBlank(bindThirdInfoDTO.getUnionid())){
                tUserThird.setThirdUnionId(bindThirdInfoDTO.getUnionid());
            }
            tUserThird.setAppId(info.getAppId());
            tUserThird.setTenantId(info.getTenantId());
            tUserThird.setLoginType(bindThirdInfoDTO.getType());
            tUserThirdService.save(tUserThird);
        }

        TUserAppThird tUserAppThird= TUserAppThird.builder()
                .uid(info.getUid())
                .sid(tUserThird.getSid())
                .appId(info.getAppId())
                .tenantId(info.getTenantId())
                .build();
        tUserAppThirdService.save(tUserAppThird);

        FeignTCredit feignTCredit = FeignTCredit.builder()
                .appId(info.getAppId())
                .tenantId(info.getTenantId())
                .uid(info.getUid())
                .sid(tUserThird.getSid())
                .build();
        creditFeignService.bindUid(feignTCredit);

    }

    public void unbind(FirefoxInfo info, UnBindThirdInfoDTO unBindThirdInfoDTO) {
        TUserThird oauthUserThirdPO=tUserThirdService.getById(unBindThirdInfoDTO.getId());
        tUserAppThirdService.deleteRecord(info.getAppId(), info.getTenantId(), info.getUid(), oauthUserThirdPO.getSid(), Consts.AuthType.third);

        FeignTCredit feignTCredit = FeignTCredit.builder()
                .appId(info.getAppId())
                .tenantId(info.getTenantId())
                .uid(info.getUid())
                .sid(oauthUserThirdPO.getSid())
                .build();
        creditFeignService.bindUid(feignTCredit);
    }

    protected String getPhoneLastStr(String phone){
        return phone.substring(phone.length()- CommonConstant.DEFAULT_PASSWORD_LENGTH);
    }

    protected String getRandomNickname(){
        return "kpid_"+ StringUtils.getRandom(16);
    }
}
