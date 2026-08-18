package com.firefox.center.sys.modules.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.Assert;
import com.firefox.center.sys.common.exception.ExceptionCode;
import com.firefox.center.sys.core.base.controller.BaseController;
import com.firefox.center.sys.core.base.model.TreeModel;
import com.firefox.center.sys.modules.app.entity.*;
import com.firefox.center.sys.modules.app.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @Description: 公司管理
*/
@Slf4j
@RestController
@RequestMapping("/sys/center/app")
@RequiredArgsConstructor
public class OauthClientDetailsController extends BaseController {

    private final OauthCenterService oauthCenterService;
    private final OauthTenantService oauthTenantService;
    private final OauthAppCenterService oauthAppCenterService;
    private final OauthTenantAppService oauthTenantAppService;
    private final OauthClientDetailsService oauthClientDetailsService;

    /**
     * 分页列表查询
    */
    @GetMapping(value = "/page")
    public R<IPage<OauthClientDetails>> queryPageList(OauthClientDetails oauthClientDetails,
                                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
       IPage<OauthClientDetails> pageList = oauthClientDetailsService.queryPage(oauthClientDetails, pageNo, pageSize);
       return R.ok(pageList);
    }

    @GetMapping(value = "/tenantPage")
    public R<IPage<OauthTenant>> queryTenantPageList(OauthTenant oauthTenant,
                                                     @RequestParam(name="appId", defaultValue="1") String appId,
                                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        IPage<OauthTenant> pageList = oauthTenantService.queryPageByAppid(appId, oauthTenant, pageNo, pageSize);
        return R.ok(pageList);
    }

    @GetMapping(value = "/servicePage")
    public R<IPage<OauthCenter>> queryServicePageList(OauthCenter oauthCenter,
                                                      @RequestParam(name="appId", defaultValue="1") String appId,
                                                      @RequestParam(name="tenantId", defaultValue="1") Integer tenantId,
                                                      @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                      @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        IPage<OauthCenter> pageList = oauthCenterService.queryPageByAppTenantId(appId, tenantId, oauthCenter, pageNo, pageSize);
        return R.ok(pageList);
    }

    /**
     * 用户角色授权功能，查询菜单权限树
     * @param appId
     * @return
     */
    @RequestMapping(value = "/queryAppTenantTreeList", method = RequestMethod.GET)
    public R<List<TreeModel>> queryAppTenantTreeList(@RequestParam(name="appId", defaultValue="1") String appId) {
        List<OauthTenant> list=oauthTenantService.selectOtherListByAppid(appId);
        List<TreeModel> treeList = new ArrayList<>();
        getAppTenantTreeModelList(treeList, list);
        return R.ok(treeList);
    }


    @RequestMapping(value = "/queryAppTenantServiceTreeList", method = RequestMethod.GET)
    public R<List<TreeModel>> queryAppTenantServiceTreeList(@RequestParam(name="appId", defaultValue="1") String appId,
                                                            @RequestParam(name="tenantId", defaultValue="1") Integer tenantId) {
        List<OauthCenter> list=oauthCenterService.selectOtherListByAppTenantId(appId, tenantId);
        List<TreeModel> treeList = new ArrayList<>();
        getAppServiceTreeModelList(treeList, list);
        return R.ok(treeList);
    }

    /**
     * 用户角色授权功能，查询菜单权限树
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryAppServiceTreeList", method = RequestMethod.GET)
    public R<Map<String,Object>> queryAppServiceTreeList(HttpServletRequest request) {
        R<Map<String,Object>> result = new R<>();
        //全部权限ids
        List<Integer> ids = new ArrayList<>();
        try {
            List<OauthCenter> list=oauthCenterService.list();
            for(OauthCenter oauthCenter : list) {
                ids.add(oauthCenter.getId());
            }
            List<TreeModel> treeList = new ArrayList<>();
            getAppServiceTreeModelList(treeList, list);
            Map<String,Object> resMap = new HashMap<String,Object>();
            resMap.put("treeList", treeList); //全部树节点数据
            resMap.put("ids", ids);//全部树ids
            result.setData(resMap);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     *   添加
    * @param oauthClientDetails
    * @return
    */
    @PostMapping(value = "/save")
    public R<?> save(@RequestBody OauthClientDetails oauthClientDetails) {
       oauthClientDetailsService.saveApp(getUser(), oauthClientDetails);
       return R.ok("操作成功！");
    }

    @PostMapping(value = "/saveAppTenant")
    public R<?> saveAppTenant(@RequestBody Map map) {
        Assert.isTrue(map.containsKey("appId"), ExceptionCode.ID_IS_NULL);
        String appId=map.get("appId").toString();
        String tenantIds=map.get("tenantIds").toString();
        oauthTenantAppService.saveAppTenant(appId, tenantIds);
        return R.ok("授权成功！");
    }

    @PostMapping(value = "/saveAppTenantService")
    public R<?> saveAppTenantService(@RequestBody Map map) {
        Assert.isTrue(map.containsKey("appId"), ExceptionCode.ID_IS_NULL);
        String appId=map.get("appId").toString();
        String tenantId=map.get("tenantId").toString();
        String servicesIds=map.get("servicesIds").toString();
        oauthAppCenterService.saveAppTenantService(appId, Integer.valueOf(tenantId), servicesIds);
        return R.ok("授权成功！");
    }

    @PostMapping(value = "/updateAppTenant")
    public R<?> updateAppTenant(@RequestBody Map map) {
        Assert.isTrue(map.containsKey("appId"), ExceptionCode.ID_IS_NULL);
        String appId=map.get("appId").toString();
        Integer tenantId=Integer.valueOf(map.get("tenantId").toString());
        OauthTenantApp oauthTenantApp=oauthTenantAppService.selectRecord(tenantId, appId);
        if(oauthTenantApp!=null){
            Integer status=Integer.valueOf(map.get("status").toString());
            oauthTenantAppService.updateRecord(tenantId, appId, status);
        }
        return R.ok("操作成功！");
    }

    @PostMapping(value = "/updateAppTenantService")
    public R<?> updateAppTenantService(@RequestBody Map map) {
        Assert.isTrue(map.containsKey("appId"), ExceptionCode.ID_IS_NULL);
        String appId=map.get("appId").toString();
        Integer tenantId=Integer.valueOf(map.get("tenantId").toString());
        Integer centerId=Integer.valueOf(map.get("centerId").toString());
        OauthAppCenter oauthAppCenter=oauthAppCenterService.selectRecord(tenantId, centerId, appId);
        if(oauthAppCenter!=null){
            Integer status=Integer.valueOf(map.get("status").toString());
            oauthAppCenterService.updateRecord(tenantId, centerId, appId, status);
        }
        return R.ok("操作成功！");
    }

    @DeleteMapping(value = "/delete")
    public R<?> delete(@RequestParam(name="id",required=true) Integer id) {
       return R.ok("删除成功!");
   }


    private void getAppTenantTreeModelList(List<TreeModel> treeList, List<OauthTenant> metaList) {
        for (OauthTenant oauthTenant : metaList) {
            TreeModel tree = TreeModel.builder()
                    .key(oauthTenant.getId().toString())
                    .value(oauthTenant.getId().toString())
                    .id(oauthTenant.getId().toString())
                    .title(oauthTenant.getName())
                    .parentId("-1")
                    .checkable(true)
                    .selectable(true)
                    .isLeaf(true)
                    .build();
            treeList.add(tree);
            if(!tree.isLeaf()) {
                getAppTenantTreeModelList(treeList, metaList);
            }
        }
    }
    private void getAppServiceTreeModelList(List<TreeModel> treeList, List<OauthCenter> metaList) {
        for (OauthCenter oauthCenter : metaList) {
            TreeModel tree = TreeModel.builder()
                    .key(oauthCenter.getId().toString())
                    .value(oauthCenter.getId().toString())
                    .id(oauthCenter.getId().toString())
                    .title(oauthCenter.getName())
                    .parentId("-1")
                    .checkable(true)
                    .selectable(true)
                    .isLeaf(true)
                    .build();
            treeList.add(tree);
            if(!tree.isLeaf()) {
                getAppServiceTreeModelList(treeList, metaList);
            }
        }
    }

}
