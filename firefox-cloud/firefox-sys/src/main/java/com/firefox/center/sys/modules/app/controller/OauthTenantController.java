package com.firefox.center.sys.modules.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.Assert;
import com.firefox.center.sys.common.exception.ExceptionCode;
import com.firefox.center.sys.core.base.controller.BaseController;
import com.firefox.center.sys.modules.app.entity.OauthTenant;
import com.firefox.center.sys.modules.app.service.OauthTenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
* @Description: 公司管理
*/
@Slf4j
@RestController
@RequestMapping("/sys/center/tenant")
@RequiredArgsConstructor
public class OauthTenantController extends BaseController {

    private final OauthTenantService oauthTenantService;

   /**
     * 分页列表查询
    */
   @GetMapping(value = "/page")
   public R<IPage<OauthTenant>> queryPageList(OauthTenant oauthTenant,
                                              @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                              @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
       IPage<OauthTenant> pageList = oauthTenantService.queryPage(oauthTenant, pageNo, pageSize);
       return R.ok(pageList);
   }

   /**
     *   添加
    * @param oauthTenant
    * @return
    */
   @PostMapping(value = "/add")
   public R<?> add(@RequestBody OauthTenant oauthTenant) {
       return oauthTenantService.addTenant(getUser(), oauthTenant);
   }

    @PostMapping(value = "/update")
    public R<?> update(@RequestBody OauthTenant oauthTenant) {
        oauthTenantService.updateTenant(getUser(), oauthTenant);
        return R.ok("操作成功！");
    }

   /**
    *通过id删除
    * @return
    */
   @PostMapping(value = "/upStatus")
   public R<?> upStatus(@RequestBody Map map) {
       Assert.isTrue(map.containsKey("id"), ExceptionCode.ID_IS_NULL);
       oauthTenantService.upStatus(Integer.valueOf(map.get("id").toString()), Integer.valueOf(map.get("status").toString()));
       return R.ok("更新成功！");
   }

}
