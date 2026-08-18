package com.firefox.center.sys.modules.dashboard.controller;

import com.firefox.center.common.R;
import com.firefox.center.sys.modules.dashboard.service.DashboardService;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
* @Description: 首页仪表盘
*/
@Slf4j
@RestController
@RequestMapping("/sys/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping(value = "/sum")
    public R<?> sum(@RequestParam @ApiParam(hidden = true) Map map) {
        return R.ok(dashboardService.selectAllData(map));
    }

    @GetMapping(value = "/chart")
    public R<?> chart() {
        return R.ok(dashboardService.selectChartData());
    }

}
