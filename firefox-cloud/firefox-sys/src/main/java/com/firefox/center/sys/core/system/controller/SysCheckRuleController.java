package com.firefox.center.sys.core.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.aspect.annotation.AutoLog;
import com.firefox.center.sys.common.system.base.controller.BaseController;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.core.system.entity.SysCheckRule;
import com.firefox.center.sys.core.system.service.ISysCheckRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @Description: 编码校验规则
 * @Date: 2020-02-04
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "编码校验规则")
@RestController
@RequestMapping("/sys/checkRule")
@RequiredArgsConstructor
public class SysCheckRuleController extends BaseController<SysCheckRule, ISysCheckRuleService> {

    private final ISysCheckRuleService sysCheckRuleService;

    /**
     * 分页列表查询
     *
     * @param sysCheckRule
     * @param pageNo
     * @param pageSize
     * @param request
     * @return
     */
    @AutoLog(value = "编码校验规则-分页列表查询")
    @ApiOperation(value = "编码校验规则-分页列表查询", notes = "编码校验规则-分页列表查询")
    @GetMapping(value = "/list")
    public R queryPageList(
            SysCheckRule sysCheckRule,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        QueryWrapper<SysCheckRule> queryWrapper = QueryGenerator.initQueryWrapper(sysCheckRule, request.getParameterMap());
        Page<SysCheckRule> page = new Page<>(pageNo, pageSize);
        IPage<SysCheckRule> pageList = sysCheckRuleService.page(page, queryWrapper);
        return R.ok(pageList);
    }


    /**
     * 通过id查询
     *
     * @param ruleCode
     * @return
     */
    @AutoLog(value = "编码校验规则-通过Code校验传入的值")
    @ApiOperation(value = "编码校验规则-通过Code校验传入的值", notes = "编码校验规则-通过Code校验传入的值")
    @GetMapping(value = "/checkByCode")
    public R checkByCode(
            @RequestParam(name = "ruleCode") String ruleCode,
            @RequestParam(name = "value") String value
    ) throws UnsupportedEncodingException {
        SysCheckRule sysCheckRule = sysCheckRuleService.getByCode(ruleCode);
        if (sysCheckRule == null) {
            return R.error("该编码不存在");
        }
        JSONObject errorResult = sysCheckRuleService.checkValue(sysCheckRule, URLDecoder.decode(value, "UTF-8"));
        if (errorResult == null) {
            return R.ok();
        } else {
            R<Object> r = R.error(errorResult.getString("message"));
            r.setData(errorResult);
            return r;
        }
    }

    /**
     * 添加
     *
     * @param sysCheckRule
     * @return
     */
    @AutoLog(value = "编码校验规则-添加")
    @ApiOperation(value = "编码校验规则-添加", notes = "编码校验规则-添加")
    @PostMapping(value = "/add")
    public R add(@RequestBody SysCheckRule sysCheckRule) {
        sysCheckRuleService.save(sysCheckRule);
        return R.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param sysCheckRule
     * @return
     */
    @AutoLog(value = "编码校验规则-编辑")
    @ApiOperation(value = "编码校验规则-编辑", notes = "编码校验规则-编辑")
    @PutMapping(value = "/edit")
    public R edit(@RequestBody SysCheckRule sysCheckRule) {
        sysCheckRuleService.updateById(sysCheckRule);
        return R.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "编码校验规则-通过id删除")
    @ApiOperation(value = "编码校验规则-通过id删除", notes = "编码校验规则-通过id删除")
    @DeleteMapping(value = "/delete")
    public R delete(@RequestParam(name = "id", required = true) String id) {
        sysCheckRuleService.removeById(id);
        return R.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "编码校验规则-批量删除")
    @ApiOperation(value = "编码校验规则-批量删除", notes = "编码校验规则-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public R deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysCheckRuleService.removeByIds(Arrays.asList(ids.split(",")).stream().filter(s->s.indexOf("_")==-1).collect(Collectors.toList()));
        return R.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "编码校验规则-通过id查询")
    @ApiOperation(value = "编码校验规则-通过id查询", notes = "编码校验规则-通过id查询")
    @GetMapping(value = "/queryById")
    public R queryById(@RequestParam(name = "id", required = true) String id) {
        SysCheckRule sysCheckRule = sysCheckRuleService.getById(id);
        return R.ok(sysCheckRule);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param sysCheckRule
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysCheckRule sysCheckRule) {
        return super.exportXls(request, sysCheckRule, SysCheckRule.class, "编码校验规则");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public R importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SysCheckRule.class);
    }

}
