package com.firefox.center.user.controller;

import com.firefox.center.common.R;
import com.firefox.center.common.controller.BaseController;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.kit.Assert;
import com.firefox.center.common.utils.DateUtil;
import com.firefox.center.user.pojo.sms.dto.CheckCodeDTO;
import com.firefox.center.user.pojo.sms.dto.CodeSendDTO;
import com.firefox.center.user.service.SmsService;
import com.firefox.center.user.service.SmsSignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1/usercenter/sms")
@Api(tags = "短信")
@RequiredArgsConstructor
public class SmsController extends BaseController {

    private final SmsService smsService;
    private final SmsSignService smsSignService;


    @ApiOperation("发送短信")
    @PostMapping("send")
    public R send(@RequestBody @Validated CodeSendDTO sendDTO) {
        Assert.isTrue(smsSignService.isCodeMatch(sendDTO), CodeEnum.SIGN_ERROR);
        Assert.isTrue(DateUtil.nowTimeStamp()-sendDTO.getArgs().getTs()<=300, CodeEnum.TS_OVERDUE);
        //Assert.isTrue(DateUtil.nowTimeStamp()-sendDTO.getArgs().getTs()<=30000000, CodeEnum.TS_OVERDUE);
        return smsService.sendCode(sendDTO);
    }

    @ApiOperation("验证短信验证码")
    @PostMapping("check")
    public R check(@RequestBody CheckCodeDTO checkDTO) {
        return smsService.checkCode(checkDTO);
    }


    @ApiOperation("验证短信验证码")
    @PostMapping("code")
    public R code() {
        return smsService.cacheCode();
    }

}
