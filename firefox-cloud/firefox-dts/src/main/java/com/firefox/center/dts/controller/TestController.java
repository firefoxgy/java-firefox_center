package com.firefox.center.dts.controller;

import com.firefox.center.common.R;
import com.firefox.center.common.controller.BaseController;
import com.firefox.center.dts.job.thread.*;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1/dts/test")
@Api(tags = "用户中心")
@RequiredArgsConstructor
public class TestController extends BaseController {

    private final PasswordThread passwordThread;

    private final Kp145Thread kp145Thread;
    private final Xp171Thread xp171Thread;
    private final Yl172Thread yl172Thread;
    private final Jh173Thread jh173Thread;
    private final Zyy174Thread zyy174Thread;
    private final Fg175Thread fg175Thread;
    private final Zx176Thread zx176Thread;
    private final Ls177Thread ls177Thread;
    private final Ml178Thread ml178Thread;
    private final Qj179Thread qj179Thread;
    private final Sj180Thread sj180Thread;
    private final Dg181Thread dg181Thread;
    private final Lp182Thread lp182Thread;
    private final Lj183Thread lj183Thread;
    private final Ld186Thread ld186Thread;
    private final Yj187Thread yj187Thread;
    private final Ys188Thread ys188Thread;
    private final Wx189Thread wx189Thread;
    private final Sf190Thread sf190Thread;
    private final Gs191Thread gs191Thread;
    private final Mh192Thread mh192Thread;

    @GetMapping("exec")
    public R orderPage() {
//        kp145Thread.execute();
//        xp171Thread.execute();
//        yl172Thread.execute();
//        jh173Thread.execute();
//        zyy174Thread.execute();
//        fg175Thread.execute();
//        zx176Thread.execute();
//        ls177Thread.execute();
//        ml178Thread.execute();
//        qj179Thread.execute();
//        sj180Thread.execute();
        dg181Thread.execute();
//        lp182Thread.execute();
//        lj183Thread.execute();
//        ld186Thread.execute();
//        yj187Thread.execute();
//        ys188Thread.execute();
//        wx189Thread.execute();
//        sf190Thread.execute();
//        gs191Thread.execute();
//        mh192Thread.execute();
        return R.ok();
    }

    @GetMapping("pwd")
    public R pwd() {
        passwordThread.execute();
        return R.ok();
    }

}
