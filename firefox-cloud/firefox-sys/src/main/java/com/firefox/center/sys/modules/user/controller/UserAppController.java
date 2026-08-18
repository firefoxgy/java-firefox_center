package com.firefox.center.sys.modules.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.R;
import com.firefox.center.sys.core.base.controller.BaseController;
import com.firefox.center.sys.modules.user.entity.TUserApp;
import com.firefox.center.sys.modules.user.entity.TUserThird;
import com.firefox.center.sys.modules.user.service.TUserAppService;
import com.firefox.center.sys.modules.user.service.TUserThirdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @Description: 公司管理
*/
@Slf4j
@RestController
@RequestMapping("/sys/user/app")
@RequiredArgsConstructor
public class UserAppController extends BaseController {

    private final TUserAppService tUserAppService;
    private final TUserThirdService tUserThirdService;

   /**
     * 分页列表查询
    */
   @GetMapping(value = "/page")
   public R<IPage<TUserApp>> queryPageList(TUserApp tUserApp,
                                           @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                           @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
       IPage<TUserApp> pageList = tUserAppService.queryPage(tUserApp, pageNo, pageSize);
       return R.ok(pageList);
   }

    @GetMapping(value = "/thirdList")
    public R<IPage<TUserThird>> thirdList(@RequestParam(name="appId", defaultValue="1") String appId,
                                         @RequestParam(name="tenantId", defaultValue="10") Integer tenantId,
                                         @RequestParam(name="uid", defaultValue="10") Integer uid) {
        List<TUserThird> pageList = tUserThirdService.selectList(appId, tenantId, uid);
        Page<TUserThird> page = new Page<TUserThird>();
        return R.ok(page.setRecords(pageList));
    }

   /**
     *   添加
    * @param tUserApp
    * @return
    */
   @PostMapping(value = "/save")
   public R<?> save(@RequestBody TUserApp tUserApp) {
       tUserAppService.saveUserApp(tUserApp);
       return R.ok("操作成功！");
   }

}
