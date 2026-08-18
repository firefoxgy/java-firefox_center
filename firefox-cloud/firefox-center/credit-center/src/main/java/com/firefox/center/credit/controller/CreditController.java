package com.firefox.center.credit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.firefox.center.common.R;
import com.firefox.center.common.controller.BaseController;
import com.firefox.center.common.entity.FirefoxInfo;
import com.firefox.center.common.kit.Assert;
import com.firefox.center.credit.db.model.TCredit;
import com.firefox.center.credit.db.model.TCreditBehavior;
import com.firefox.center.credit.db.model.TCreditLog;
import com.firefox.center.credit.db.model.TCreditType;
import com.firefox.center.credit.db.service.TCreditBehaviorService;
import com.firefox.center.credit.db.service.TCreditLogService;
import com.firefox.center.credit.db.service.TCreditService;
import com.firefox.center.credit.db.service.TCreditTypeService;
import com.firefox.center.credit.job.thread.CleanCreditThread;
import com.firefox.center.credit.pojo.dto.CreditRegDTO;
import com.firefox.center.credit.pojo.vo.CreditRankVO;
import com.firefox.center.credit.service.CreditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1/creditcenter/credit")
@Api(tags = "积分")
@RequiredArgsConstructor
public class CreditController extends BaseController {

    private final CreditService creditService;
    private final TCreditService tCreditService;
    private final TCreditLogService tCreditLogService;
    private final TCreditTypeService tCreditTypeService;
    private final TCreditBehaviorService tCreditBehaviorService;
    private final CleanCreditThread cleanCreditThread;
    private static final String[] RANK_TYPE = {"day", "week", "month", "season", "year"};

    @GetMapping("rank")
    @ApiOperation("积分排名")
    public R rank() {
        return R.ok(creditService.getRank(getFirefoxInfo()).getColumns());
    }

    @GetMapping("page")
    public R<IPage<CreditRankVO>> page(@NotBlank @RequestParam(name="type",required=true, defaultValue="day") String type,
                                       @RequestParam(name="_index",required=false, defaultValue = "1") Integer _index,
                                       @RequestParam(name="_size",required=false, defaultValue = "10") Integer _size) {
        Assert.isTrue(Arrays.asList(RANK_TYPE).contains(type), "类型不支持");
        return R.ok(creditService.getRankPage(getFirefoxInfo(), getPage(), type));
    }

    @PostMapping("reg")
    @ApiOperation("积分登记")
    public R reg(@RequestBody @Validated CreditRegDTO creditRegDTO) {
        TCreditType tCreditType=tCreditTypeService.queryRecord(creditRegDTO.getTypeNo());
        Assert.notNull(tCreditType, "typeNo未登记");
        TCreditBehavior tCreditBehavior=tCreditBehaviorService.queryRecord(tCreditType.getId(), creditRegDTO.getBehaviorNo());
        creditService.reg(getFirefoxInfo(), tCreditType, tCreditBehavior, creditRegDTO);
        return R.ok("登记成功");
    }

    @GetMapping("clean")
    @ApiOperation("积分清零")
    public R clean(@RequestParam(name="key",required=true, defaultValue="") String key,
                   @RequestParam(name="type",required=true, defaultValue="") String type) {
        Assert.isTrue("5imDJyRzhRhRpn9ievc1RPHD1l9iMWZ5".equals(key), "key值输入错误");
        Assert.isTrue(Arrays.asList(RANK_TYPE).contains(type), "类型不支持");
        cleanCreditThread.cleanCredit(type);
        return R.ok("登记成功");
    }

    @GetMapping("logPage")
    public R<IPage<TCreditLog>> logPage(@RequestParam(name="_index",required=false, defaultValue = "1") Integer _index,
                                        @RequestParam(name="_size",required=false, defaultValue = "10") Integer _size) {
        return R.ok(tCreditLogService.queryPage(getFirefoxInfo(), getPage()));
    }

}
