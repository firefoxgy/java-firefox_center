package com.firefox.center.sys.modules.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.Assert;
import com.firefox.center.sys.common.exception.ExceptionCode;
import com.firefox.center.sys.core.base.controller.BaseController;
import com.firefox.center.sys.modules.app.entity.OauthCenter;
import com.firefox.center.sys.modules.app.entity.OauthClientDetails;
import com.firefox.center.sys.modules.app.service.OauthCenterService;
import com.firefox.center.sys.modules.app.service.OauthClientDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
* @Description: 公司管理
*/
@Slf4j
@RestController
@RequestMapping("/sys/center/service")
@RequiredArgsConstructor
public class OauthCenterController extends BaseController {

    private final OauthCenterService oauthCenterService;

   /**
     * 分页列表查询
    */
   @GetMapping(value = "/page")
   public R<IPage<OauthCenter>> queryPageList(OauthCenter oauthCenter,
                                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
       IPage<OauthCenter> pageList = oauthCenterService.queryPage(oauthCenter, pageNo, pageSize);
       return R.ok(pageList);
   }

   /**
     *   添加
    * @param oauthCenter
    * @return
    */
   @PostMapping(value = "/save")
   public R<?> save(@RequestBody OauthCenter oauthCenter) {
       oauthCenterService.saveCenter(getUser(), oauthCenter);
       return R.ok("操作成功！");
   }

   /**
    *通过id删除
    * @return
    */
   @PostMapping(value = "/upStatus")
   public R<?> upStatus(@RequestBody Map map) {
       Assert.isTrue(map.containsKey("id"), ExceptionCode.ID_IS_NULL);
       oauthCenterService.upStatus(Integer.valueOf(map.get("id").toString()), Integer.valueOf(map.get("status").toString()));
       return R.ok("更新成功！");
   }

}
