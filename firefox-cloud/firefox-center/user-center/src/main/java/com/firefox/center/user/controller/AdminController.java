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
import com.firefox.center.user.db.model.TUserAdmin;
import com.firefox.center.user.db.model.TUserApp;
import com.firefox.center.user.db.service.TUserAdminService;
import com.firefox.center.user.pojo.oauth.vo.TUserAppVO;
import com.firefox.center.user.pojo.oauth.vo.TUserThirdIdVO;
import com.firefox.center.user.pojo.oauth.vo.TUserThirdVO;
import com.firefox.center.user.pojo.sms.dto.CheckCodeDTO;
import com.firefox.center.user.pojo.user.dto.*;
import com.firefox.center.user.pojo.user.vo.TUserAdminVO;
import com.firefox.center.user.service.SmsService;
import com.firefox.center.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1/usercenter/admin")
@Api(tags = "用户中心-管理员")
@RequiredArgsConstructor
public class AdminController extends BaseController {

    private final TUserAdminService tUserAdminService;

    @GetMapping("info")
    @ApiOperation(value="获取用户信息")
    public R<?> info() {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        TUserAdminVO tUserAdminVO=tUserAdminService.selectByUserId2(firefoxInfo.getAppId(), firefoxInfo.getTenantId(), firefoxInfo.getUid());
        return R.ok(tUserAdminVO);
    }

    @PostMapping("edit")
    @ApiOperation(value="修改用户信息")
    public R editphone(@RequestBody @Validated EditAdminDTO editAdminDTO) {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        if(StrKit.isBlank(firefoxInfo.getUid()) || firefoxInfo.getUid()==0L){
            return R.error("未授权的操作");
        }
        tUserAdminService.editinfo(firefoxInfo, editAdminDTO);
        return R.ok("操作成功");
    }

    @PostMapping("editpwd")
    @ApiOperation(value="修改密码")
    public R editpwd(@RequestBody @Validated EditPwdDTO editPwdDTO) {
        FirefoxInfo firefoxInfo=getFirefoxInfo();
        if(StrKit.isBlank(firefoxInfo.getUid()) || firefoxInfo.getUid()==0L){
            return R.error("未授权的操作");
        }
        tUserAdminService.editpwd(firefoxInfo, editPwdDTO);
        return R.ok("操作成功");
    }

    @PostMapping("disableUserThirdid")
    @ApiOperation(value="解绑第三方账号")
    public R disableUserThirdId(@RequestBody Map map, HttpServletRequest request) {
        String openId = getPara(map.get("openId"));
        String sUid = request.getHeader(BusinessConstants.HEADER_USER_ID);
        long uid = getParaToLong(sUid);
        if(tUserAdminService.disableUserThirdId(uid, openId))
            return R.ok("操作成功");
        else
            return R.error("操作失败");
    }

    protected String getPara(Object obj){
        return obj==null?"":obj.toString();
    }

    protected Long getParaToLong(Object obj){
        return obj==null?0:Long.valueOf(obj.toString());
    }

}
