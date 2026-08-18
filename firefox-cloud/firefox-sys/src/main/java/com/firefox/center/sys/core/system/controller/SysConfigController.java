package com.firefox.center.sys.core.system.controller;


import com.firefox.center.common.R;
import com.firefox.center.sys.core.system.entity.SysConfig;
import com.firefox.center.sys.core.system.service.ISysConfigService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 系统配置管理
 */
@RestController
@RequestMapping("/sys/config")
@Slf4j
public class SysConfigController {

	private final ISysConfigService iSysConfigService;

	@Autowired
	public SysConfigController(ISysConfigService iSysConfigService) {
		this.iSysConfigService = iSysConfigService;
	}

	@ApiOperation("系统配置")
	@GetMapping("getConf")
	public R<?> getConf() {
		return R.ok(iSysConfigService.findAll());
	}

	@PostMapping("/insert")
	public R<?> insert(@Validated @ApiParam(hidden = true) SysConfig config){
		iSysConfigService.saveConf(config);
		return R.ok("保存成功");
	}

	@PostMapping("/save")
	public R<?> save(@RequestBody @ApiParam(hidden = true) Map<String, String> queryMap){
		iSysConfigService.udpateConf(queryMap);
		return R.ok("保存成功");
	}
	
}
