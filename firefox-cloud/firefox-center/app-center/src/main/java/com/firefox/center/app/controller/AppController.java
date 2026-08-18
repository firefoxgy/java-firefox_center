package com.firefox.center.app.controller;

import com.firefox.center.common.R;
import com.firefox.center.common.controller.BaseController;
import com.firefox.center.common.entity.FirefoxInfo;
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
@Api(tags = "应用")
@RequestMapping("/appcenter/app")
@RequiredArgsConstructor
public class AppController extends BaseController {

    @PostMapping()
    @ApiOperation(value="应用测试1")
    public R index() {
        FirefoxInfo firefox=getFirefoxInfo();
        int a=1;
        return R.ok();

    }

}
