package com.firefox.center.oauth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.R;
import com.firefox.center.oauth.db.model.OauthClientDetails;
import com.firefox.center.oauth.db.service.OauthClientDetailsService;
import com.firefox.center.oauth.dto.ClientDto;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 角色相关接口
 *
 * @Author: sujie
 */
@Validated
@Api(tags = "应用")
@RestController
@RequestMapping("/oauth/clients")
@RequiredArgsConstructor
public class ClientController {
    private final OauthClientDetailsService tMidAppService;

    /** 
     * http://localhost:8000/oauth/clients?page=1&limit=10
     * @CreateTime 2021/4/27 15:20
     * @param 
     * @return 
     */
    @GetMapping
    @ApiOperation(value = "应用列表")
    public R<Page<OauthClientDetails>> list(@RequestParam Map<String, Object> params) {
        return R.ok(tMidAppService.listClent(params, true));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取应用")
    public OauthClientDetails get(@PathVariable Long id) {
        return tMidAppService.getById(id);
    }

    @GetMapping("/all")
    @ApiOperation(value = "所有应用")
    public R<Page<OauthClientDetails>> allClient() {
        return R.ok(tMidAppService.listClent(Maps.newHashMap(), false));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除应用")
    public void delete(@PathVariable Long id) {
        tMidAppService.delClient(id);
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "保存或者修改应用")
    public R saveOrUpdate(@RequestBody ClientDto clientDto) {
        return tMidAppService.saveClient(clientDto);
    }
}
