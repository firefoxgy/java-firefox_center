package com.firefox.center.user.controller;

import com.firefox.center.common.R;
import com.firefox.center.common.Record;
import com.firefox.center.common.constants.BusinessConstants;
import com.firefox.center.common.constants.Consts;
import com.firefox.center.common.controller.BaseController;
import com.firefox.center.common.entity.FirefoxInfo;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.exception.BusinessException;
import com.firefox.center.common.kit.Assert;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.user.db.model.TUserApp;
import com.firefox.center.user.pojo.oauth.vo.TUserAppVO;
import com.firefox.center.user.pojo.oauth.vo.TUserThirdIdVO;
import com.firefox.center.user.pojo.oauth.vo.TUserThirdVO;
import com.firefox.center.user.pojo.sms.dto.CheckCodeDTO;
import com.firefox.center.user.service.SmsService;
import com.firefox.center.user.service.UserService;
import com.firefox.center.user.pojo.user.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1/usercenter/user")
@Api(tags = "用户中心")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final SmsService smsService;
    private final UserService userService;

    @PostMapping("bindphone")
    @ApiOperation(value="绑定手机号")
    public R<?> bindphone(@RequestBody @Validated BindDTO bindDTO) {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        if(StrKit.notBlank(firefoxInfo.getUid()) && firefoxInfo.getUid()!=0L){
            return R.error("手机号已绑定");
        }
        Assert.isTrue(StrKit.isMobileNumber(bindDTO.getPhone()), CodeEnum.PHONE_CHECK_ERROR);
        CheckCodeDTO checkCodeDTO = CheckCodeDTO.builder()
                .appid(firefoxInfo.getAppId())
                .tenantid(firefoxInfo.getTenantId())
                .phone(bindDTO.getPhone())
                .code(bindDTO.getCode())
                .build();
        R r=smsService.checkCode(checkCodeDTO);
        if(!r.isSuccess()){
            if(r.getCode()==CodeEnum.VERIFICATION_CODE_EMPTY.getCode()){
                throw new BusinessException(CodeEnum.VERIFICATION_CODE_EMPTY);
            }else if(r.getCode()==CodeEnum.VERIFICATION_CODE_EXPIRED.getCode()){
                throw new BusinessException(CodeEnum.VERIFICATION_CODE_EXPIRED);
            }
            throw new BusinessException(CodeEnum.VERIFICATION_CODE_EMPTY);
        }
        TUserApp tUserApp=userService.bind(firefoxInfo, bindDTO);

        firefoxInfo.setUid(tUserApp.getUid());
        TUserAppVO tUserAppVO=userService.getInfo(firefoxInfo);
        List<TUserThirdIdVO> list=userService.bindList(firefoxInfo);
        return R.ok(new Record().set("info", tUserAppVO).set("bindList", list).getColumns());
    }

    @GetMapping("info")
    @ApiOperation(value="获取用户信息")
    public R<?> info() {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        if(BusinessConstants.TYPE_APP.equals(firefoxInfo.getUType())){
            TUserAppVO tUserAppVO=userService.getInfo(firefoxInfo);
            List<TUserThirdIdVO> list=userService.bindList(firefoxInfo);
            return R.ok(new Record().set("info", tUserAppVO).set("bindList", list).getColumns());
        }
        TUserThirdVO tUserThirdVO=userService.getThirdInfo(firefoxInfo);
        return R.ok(new Record().set("info", tUserThirdVO).getColumns());
    }

    @PostMapping("editphone")
    @ApiOperation(value="修改用户手机号")
    public R editphone(@RequestBody @Validated EditPhoneDTO editPhoneDTO) {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        if(StrKit.isBlank(firefoxInfo.getUid()) || firefoxInfo.getUid()==0L){
            return R.error("未绑定手机号，不能进行此操作");
        }
        userService.editphone(firefoxInfo, editPhoneDTO);
        return R.ok("操作成功");
    }

    @PostMapping("editmail")
    @ApiOperation(value="修改用户手机号")
    public R editmail(@RequestBody @Validated EditMailDTO editMailDTO) {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        if(StrKit.isBlank(firefoxInfo.getUid()) || firefoxInfo.getUid()==0L){
            return R.error("未绑定手机号，不能进行此操作");
        }
        userService.editmail(firefoxInfo, editMailDTO);
        return R.ok("操作成功");
    }

    @PostMapping("editpwd")
    @ApiOperation(value="修改密码")
    public R editpwd(@RequestBody @Validated EditPwdDTO editPwdDTO) {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        if(StrKit.isBlank(firefoxInfo.getUid()) || firefoxInfo.getUid()==0L){
            return R.error("未绑定手机号，不能进行此操作");
        }
        userService.editpwd(firefoxInfo, editPwdDTO);
        return R.ok("操作成功");
    }

    @PostMapping("editinfo")
    @ApiOperation(value="修改用户信息")
    public R editinfo(@RequestBody @Validated EditInfoDTO EditInfoDTO) {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        if(StrKit.notBlank(firefoxInfo.getUid()) && firefoxInfo.getUid()!=0L){
            userService.editAppInfo(firefoxInfo, EditInfoDTO);
            return R.ok("操作成功");
        }
        userService.editThirdInfo(firefoxInfo, EditInfoDTO);
        return R.ok("操作成功");
    }

    @PostMapping("bindList")
    @ApiOperation(value="绑定第三方帐号")
    public R<List<TUserThirdIdVO>> bindList() {
        return R.ok(userService.bindList(getFirefoxInfo()));
    }

    @PostMapping("bind")
    @ApiOperation(value="绑定第三方帐号")
    public R bind(@RequestBody @Validated BindThirdInfoDTO bindThirdInfoDTO) {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        if(StrKit.isBlank(firefoxInfo.getUid()) || firefoxInfo.getUid()==0L){
            return R.error("未绑定手机号，不能进行此操作");
        }
        Assert.isTrue(Arrays.asList(Consts.grantType.types).contains(bindThirdInfoDTO.getType()), CodeEnum.OAUTH_UNSUPORT);
        userService.bind(firefoxInfo, bindThirdInfoDTO);
        return R.ok("操作成功");
    }

    @PostMapping("unbind")
    @ApiOperation(value="解绑第三方帐号")
    public R unbind(@RequestBody @Validated UnBindThirdInfoDTO unBindThirdInfoDTO) {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        if(StrKit.isBlank(firefoxInfo.getUid()) || firefoxInfo.getUid()==0L){
            return R.error("未绑定手机号，不能进行此操作");
        }
        userService.unbind(firefoxInfo, unBindThirdInfoDTO);
        return R.ok("操作成功");
    }

}
