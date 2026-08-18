package com.firefox.center.sys.core.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.aspect.annotation.PermissionData;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.common.util.oConvertUtils;
import com.firefox.center.sys.core.system.entity.SysTenant;
import com.firefox.center.sys.core.system.service.ISysTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户配置信息
 */
@Slf4j
@RestController
@RequestMapping("/sys/tenant")
@RequiredArgsConstructor
public class SysTenantController {

    private final ISysTenantService sysTenantService;

    /**
     * 获取列表数据
     * @param sysTenant
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @PermissionData(pageComponent = "system/TenantList")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public R<IPage<SysTenant>> queryPageList(SysTenant sysTenant,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		R<IPage<SysTenant>> result = new R<IPage<SysTenant>>();
		QueryWrapper<SysTenant> queryWrapper = QueryGenerator.initQueryWrapper(sysTenant, req.getParameterMap());
		Page<SysTenant> page = new Page<SysTenant>(pageNo, pageSize);
		IPage<SysTenant> pageList = sysTenantService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setData(pageList);
		return result;
	}

    /**
     *   添加
     * @param
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public R<SysTenant> add(@RequestBody SysTenant sysTenant) {
        R<SysTenant> result = new R<SysTenant>();
        if(sysTenantService.getById(sysTenant.getId())!=null){
            return result.errorMsg("该编号已存在!");
        }
        try {
            sysTenantService.save(sysTenant);
            result.okMsg("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.errorMsg("操作失败");
        }
        return result;
    }

    /**
     *  编辑
     * @param
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public R<SysTenant> edit(@RequestBody SysTenant tenant) {
        R<SysTenant> result = new R<SysTenant>();
        SysTenant sysTenant = sysTenantService.getById(tenant.getId());
        if(sysTenant==null) {
            result.setSuccess(false);
            result.errorMsg("未找到对应实体");
        }else {
            boolean ok = sysTenantService.updateById(tenant);
            if(ok) {
                result.okMsg("修改成功!");
            }
        }
        return result;
    }

    /**
     *   通过id删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public R<?> delete(@RequestParam(name="id",required=true) String id) {
        sysTenantService.removeById(id);
        return R.ok("删除成功");
    }

    /**
     *  批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public R<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        R<?> result = new R<>();
        if(oConvertUtils.isEmpty(ids)) {
            result.setSuccess(false);
            result.errorMsg("未选中租户！");
        }else {
            sysTenantService.removeByIds(Arrays.asList(ids.split(",")).stream().filter(s->s.indexOf("_")==-1).collect(Collectors.toList()));
            result.okMsg("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public R<SysTenant> queryById(@RequestParam(name="id",required=true) String id) {
        R<SysTenant> result = new R<SysTenant>();
        SysTenant sysTenant = sysTenantService.getById(id);
        if(sysTenant==null) {
            result.setSuccess(false);
            result.errorMsg("未找到对应实体");
        }else {
            result.setData(sysTenant);
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * 查询有效的 租户数据
     * @return
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public R<List<SysTenant>> queryList(@RequestParam(name="ids",required=false) String ids) {
        R<List<SysTenant>> result = new R<List<SysTenant>>();
        LambdaQueryWrapper<SysTenant> query = new LambdaQueryWrapper<>();
        query.eq(SysTenant::getStatus, 1);
        if(oConvertUtils.isNotEmpty(ids)){
            query.in(SysTenant::getId, ids.split(","));
        }
        Date now = new Date();
        query.ge(SysTenant::getEndDate, now);
        query.le(SysTenant::getBeginDate, now);
        List<SysTenant> ls = sysTenantService.list(query);
        result.setSuccess(true);
        result.setData(ls);
        return result;
    }
}
