package com.firefox.center.app.controller;

import com.firefox.center.common.R;
import com.firefox.center.common.controller.BaseController;
import com.firefox.center.common.entity.FirefoxInfo;
import com.firefox.center.app.db.service.TLogSmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@Api(tags = "日志")
@RequestMapping("/logcenter/sms")
@RequiredArgsConstructor
public class LogSmsController extends BaseController {

    private final TLogSmsService tMidAppService;

    @PostMapping()
    @ApiOperation(value="应用测试1")
    public R index() {
        FirefoxInfo firefox=getFirefoxInfo();
        int a=1;
        return R.ok();

    }

}
