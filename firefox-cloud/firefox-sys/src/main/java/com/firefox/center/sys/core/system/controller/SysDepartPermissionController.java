package com.firefox.center.sys.core.system.controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.constant.CommonConstant;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.common.util.oConvertUtils;
import com.firefox.center.sys.core.system.entity.SysDepartPermission;
import com.firefox.center.sys.core.system.entity.SysDepartRolePermission;
import com.firefox.center.sys.core.system.entity.SysPermission;
import com.firefox.center.sys.core.system.entity.SysPermissionDataRule;
import com.firefox.center.sys.core.system.model.TreeModel;
import com.firefox.center.sys.core.system.service.ISysDepartPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.firefox.center.sys.common.system.base.controller.BaseController;
import com.firefox.center.sys.core.system.service.ISysDepartRolePermissionService;
import com.firefox.center.sys.core.system.service.ISysPermissionDataRuleService;
import com.firefox.center.sys.core.system.service.ISysPermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 部门权限表
 * @Date:   2020-02-11
 * @Version: V1.0
 */
@Slf4j
@Api(tags="部门权限表")
@RestController
@RequestMapping("/sys/sysDepartPermission")
@RequiredArgsConstructor
public class SysDepartPermissionController extends BaseController<SysDepartPermission, ISysDepartPermissionService> {

	private final ISysDepartPermissionService sysDepartPermissionService;
	private final ISysPermissionDataRuleService sysPermissionDataRuleService;
	private final ISysPermissionService sysPermissionService;
	private final ISysDepartRolePermissionService sysDepartRolePermissionService;

	/**
	 * 分页列表查询
	 *
	 * @param sysDepartPermission
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="部门权限表-分页列表查询", notes="部门权限表-分页列表查询")
	@GetMapping(value = "/list")
	public R<?> queryPageList(SysDepartPermission sysDepartPermission,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SysDepartPermission> queryWrapper = QueryGenerator.initQueryWrapper(sysDepartPermission, req.getParameterMap());
		Page<SysDepartPermission> page = new Page<SysDepartPermission>(pageNo, pageSize);
		IPage<SysDepartPermission> pageList = sysDepartPermissionService.page(page, queryWrapper);
		return R.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param sysDepartPermission
	 * @return
	 */
	@ApiOperation(value="部门权限表-添加", notes="部门权限表-添加")
	@PostMapping(value = "/add")
	public R<?> add(@RequestBody SysDepartPermission sysDepartPermission) {
		sysDepartPermissionService.save(sysDepartPermission);
		return R.ok("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param sysDepartPermission
	 * @return
	 */
	@ApiOperation(value="部门权限表-编辑", notes="部门权限表-编辑")
	@PutMapping(value = "/edit")
	public R<?> edit(@RequestBody SysDepartPermission sysDepartPermission) {
		sysDepartPermissionService.updateById(sysDepartPermission);
		return R.ok("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="部门权限表-通过id删除", notes="部门权限表-通过id删除")
	@DeleteMapping(value = "/delete")
	public R<?> delete(@RequestParam(name="id",required=true) String id) {
		sysDepartPermissionService.removeById(id);
		return R.ok("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@ApiOperation(value="部门权限表-批量删除", notes="部门权限表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public R<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysDepartPermissionService.removeByIds(Arrays.asList(ids.split(",")).stream().filter(s->s.indexOf("_")==-1).collect(Collectors.toList()));
		return R.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="部门权限表-通过id查询", notes="部门权限表-通过id查询")
	@GetMapping(value = "/queryById")
	public R<?> queryById(@RequestParam(name="id",required=true) String id) {
		SysDepartPermission sysDepartPermission = sysDepartPermissionService.getById(id);
		return R.ok(sysDepartPermission);
	}

	/**
	* 导出excel
	*
	* @param request
	* @param sysDepartPermission
	*/
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysDepartPermission sysDepartPermission) {
	  return super.exportXls(request, sysDepartPermission, SysDepartPermission.class, "部门权限表");
	}

	/**
	* 通过excel导入数据
	*
	* @param request
	* @param response
	* @return
	*/
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public R<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
	  return super.importExcel(request, response, SysDepartPermission.class);
	}

	/**
	* 部门管理授权查询数据规则数据
	*/
	@GetMapping(value = "/datarule/{permissionId}/{departId}")
	public R<?> loadDatarule(@PathVariable("permissionId") String permissionId,@PathVariable("departId") String departId) {
		List<SysPermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
		if(list==null || list.size()==0) {
			return R.error("未找到权限配置信息");
		}else {
			Map<String,Object> map = new HashMap<>();
			map.put("datarule", list);
			LambdaQueryWrapper<SysDepartPermission> query = new LambdaQueryWrapper<SysDepartPermission>()
				 .eq(SysDepartPermission::getPermissionId, permissionId)
				 .eq(SysDepartPermission::getDepartId,departId);
			SysDepartPermission sysDepartPermission = sysDepartPermissionService.getOne(query);
			if(sysDepartPermission==null) {
			 //return R.error("未找到角色菜单配置信息");
			}else {
				String drChecked = sysDepartPermission.getDataRuleIds();
				if(oConvertUtils.isNotEmpty(drChecked)) {
					map.put("drChecked", drChecked.endsWith(",")?drChecked.substring(0, drChecked.length()-1):drChecked);
				}
			}
			return R.ok(map);
			//TODO 以后按钮权限的查询也走这个请求 无非在map中多加两个key
		}
	}

	/**
	* 保存数据规则至部门菜单关联表
	*/
	@PostMapping(value = "/datarule")
	public R<?> saveDatarule(@RequestBody JSONObject jsonObject) {
		try {
			String permissionId = jsonObject.getString("permissionId");
			String departId = jsonObject.getString("departId");
			String dataRuleIds = jsonObject.getString("dataRuleIds");
			log.info("保存数据规则>>"+"菜单ID:"+permissionId+"部门ID:"+ departId+"数据权限ID:"+dataRuleIds);
			LambdaQueryWrapper<SysDepartPermission> query = new LambdaQueryWrapper<SysDepartPermission>()
				 .eq(SysDepartPermission::getPermissionId, permissionId)
				 .eq(SysDepartPermission::getDepartId,departId);
			SysDepartPermission sysDepartPermission = sysDepartPermissionService.getOne(query);
			if(sysDepartPermission==null) {
				return R.error("请先保存部门菜单权限!");
			}else {
				sysDepartPermission.setDataRuleIds(dataRuleIds);
			 	this.sysDepartPermissionService.updateById(sysDepartPermission);
			}
		} catch (Exception e) {
		 	log.error("SysDepartPermissionController.saveDatarule()发生异常：" + e.getMessage(),e);
		 	return R.error("保存失败");
		}
		return R.ok("保存成功!");
	}

	 /**
	  * 查询角色授权
	  *
	  * @return
	  */
	 @RequestMapping(value = "/queryDeptRolePermission", method = RequestMethod.GET)
	 public R<List<String>> queryDeptRolePermission(@RequestParam(name = "roleId", required = true) String roleId) {
		 R<List<String>> result = new R<>();
		 try {
			 List<SysDepartRolePermission> list = sysDepartRolePermissionService.list(new QueryWrapper<SysDepartRolePermission>().lambda().eq(SysDepartRolePermission::getRoleId, roleId));
			 result.setData(list.stream().map(SysDepartRolePermission -> String.valueOf(SysDepartRolePermission.getPermissionId())).collect(Collectors.toList()));
			 result.setSuccess(true);
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
		 }
		 return result;
	 }

	 /**
	  * 保存角色授权
	  *
	  * @return
	  */
	 @RequestMapping(value = "/saveDeptRolePermission", method = RequestMethod.POST)
	 public R<String> saveDeptRolePermission(@RequestBody JSONObject json) {
		 long start = System.currentTimeMillis();
		 R<String> result = new R<>();
		 try {
			 String roleId = json.getString("roleId");
			 String permissionIds = json.getString("permissionIds");
			 String lastPermissionIds = json.getString("lastpermissionIds");
			 this.sysDepartRolePermissionService.saveDeptRolePermission(roleId, permissionIds, lastPermissionIds);
			 result.okMsg("保存成功！");
			 log.info("======部门角色授权成功=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");
		 } catch (Exception e) {
			 result.setSuccess(false);
			 result.errorMsg("授权失败！");
			 log.error(e.getMessage(), e);
		 }
		 return result;
	 }

	 /**
	  * 用户角色授权功能，查询菜单权限树
	  * @param request
	  * @return
	  */
	 @RequestMapping(value = "/queryTreeListForDeptRole", method = RequestMethod.GET)
	 public R<Map<String,Object>> queryTreeListForDeptRole(@RequestParam(name="departId",required=true) String departId,HttpServletRequest request) {
		 R<Map<String,Object>> result = new R<>();
		 //全部权限ids
		 List<String> ids = new ArrayList<>();
		 try {
			 LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			 query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			 query.orderByAsc(SysPermission::getSortNo);
			 query.inSql(SysPermission::getId,"select permission_id  from sys_depart_permission where depart_id='"+departId+"'");
			 List<SysPermission> list = sysPermissionService.list(query);
			 for(SysPermission sysPer : list) {
				 ids.add(sysPer.getId());
			 }
			 List<TreeModel> treeList = new ArrayList<>();
			 getTreeModelList(treeList, list, null);
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

	 private void getTreeModelList(List<TreeModel> treeList, List<SysPermission> metaList, TreeModel temp) {
		 for (SysPermission permission : metaList) {
			 String tempPid = permission.getParentId();
			 TreeModel tree = new TreeModel(permission.getId(), tempPid, permission.getName(),permission.getRuleFlag(), permission.isLeaf());
			 if(temp==null && oConvertUtils.isEmpty(tempPid)) {
				 treeList.add(tree);
				 if(!tree.getIsLeaf()) {
					 getTreeModelList(treeList, metaList, tree);
				 }
			 }else if(temp!=null && tempPid!=null && tempPid.equals(temp.getKey())){
				 temp.getChildren().add(tree);
				 if(!tree.getIsLeaf()) {
					 getTreeModelList(treeList, metaList, tree);
				 }
			 }

		 }
	 }

}
