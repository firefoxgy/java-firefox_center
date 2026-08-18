package com.firefox.center.user.controller;

import com.firefox.center.common.R;
import com.firefox.center.common.controller.BaseController;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.kit.Assert;
import com.firefox.center.common.utils.DateUtil;
import com.firefox.center.user.pojo.sms.dto.CheckMailDTO;
import com.firefox.center.user.pojo.sms.dto.MailSendDTO;
import com.firefox.center.user.service.MailService;
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
@RequestMapping("/v1/usercenter/mail")
@Api(tags = "短信")
@RequiredArgsConstructor
public class MailController extends BaseController {

    private final MailService mailService;
    private final SmsSignService smsSignService;


    @ApiOperation("发送邮箱验证")
    @PostMapping("send")
    public R send(@RequestBody @Validated MailSendDTO sendDTO) {
        Assert.isTrue(smsSignService.isMailMatch(sendDTO), CodeEnum.SIGN_ERROR);
        Assert.isTrue(DateUtil.nowTimeStamp()-sendDTO.getArgs().getTs()<=300, CodeEnum.TS_OVERDUE);
        //Assert.isTrue(DateUtil.nowTimeStamp()-sendDTO.getArgs().getTs()<=30000000, CodeEnum.TS_OVERDUE);
        return mailService.sendMail(sendDTO);
    }

    @ApiOperation("验证邮箱验证码")
    @PostMapping("check")
    public R check(@RequestBody CheckMailDTO checkDTO) {
        return mailService.checkCode(checkDTO);
    }

}
