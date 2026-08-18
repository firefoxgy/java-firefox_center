package com.firefox.center.sys.core.system.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefox.center.common.R;
import com.firefox.center.sys.common.constant.CommonConstant;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.common.util.oConvertUtils;
import com.firefox.center.sys.core.Consts;
import com.firefox.center.sys.core.base.controller.BaseController;
import com.firefox.center.sys.core.system.entity.SysPermission;
import com.firefox.center.sys.core.system.entity.SysPermissionDataRule;
import com.firefox.center.sys.core.system.entity.SysRole;
import com.firefox.center.sys.core.system.entity.SysRolePermission;
import com.firefox.center.sys.core.system.model.TreeModel;
import com.firefox.center.sys.core.system.service.ISysPermissionDataRuleService;
import com.firefox.center.sys.core.system.service.ISysPermissionService;
import com.firefox.center.sys.core.system.service.ISysRolePermissionService;
import com.firefox.center.sys.core.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.firefox.center.sys.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@Slf4j
@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class SysRoleController extends BaseController {

	private final ISysRoleService sysRoleService;
	private final ISysPermissionDataRuleService sysPermissionDataRuleService;
	private final ISysRolePermissionService sysRolePermissionService;
	private final ISysPermissionService sysPermissionService;

	/**
	  * 分页列表查询
	 * @param role
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public R<IPage<SysRole>> queryPageList(SysRole role,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		R<IPage<SysRole>> result = new R<IPage<SysRole>>();
		QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(role, req.getParameterMap());
		if(!getUser().getUsername().equals(Consts.User.SUPER)){
			queryWrapper.ne("id", Consts.Role.ROLE_SUPER_ID);
		}
		Page<SysRole> page = new Page<SysRole>(pageNo, pageSize);
		IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setData(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public R<SysRole> add(@RequestBody SysRole role) {
		R<SysRole> result = new R<SysRole>();
		try {
			role.setCreateTime(new Date());
			sysRoleService.save(role);
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
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public R<SysRole> edit(@RequestBody SysRole role) {
		R<SysRole> result = new R<SysRole>();
		SysRole sysrole = sysRoleService.getById(role.getId());
		if(sysrole==null) {
			result.setSuccess(false);
			result.errorMsg("未找到对应实体");
		}else {
			role.setUpdateTime(new Date());
			boolean ok = sysRoleService.updateById(role);
			//TODO 返回false说明什么？
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
		sysRoleService.deleteRole(id);
		return R.ok("删除角色成功");
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public R<SysRole> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		R<SysRole> result = new R<SysRole>();
		if(oConvertUtils.isEmpty(ids)) {
			result.setSuccess(false);
			result.errorMsg("未选中角色！");
		}else {
			sysRoleService.deleteBatchRole(ids.split(","));
			result.okMsg("删除角色成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public R<SysRole> queryById(@RequestParam(name="id",required=true) String id) {
		R<SysRole> result = new R<SysRole>();
		SysRole sysrole = sysRoleService.getById(id);
		if(sysrole==null) {
			result.setSuccess(false);
			result.errorMsg("未找到对应实体");
		}else {
			result.setData(sysrole);
			result.setSuccess(true);
		}
		return result;
	}
	
	@RequestMapping(value = "/queryall", method = RequestMethod.GET)
	public R<List<SysRole>> queryall() {
		R<List<SysRole>> result = new R<>();
		List<SysRole> list = sysRoleService.list();
		if(list==null||list.size()<=0) {
			result.setSuccess(false);
			result.errorMsg("未找到角色信息");
		}else {
			result.setData(list);
			result.setSuccess(true);
		}
		return result;
	}
	
	/**
	  * 校验角色编码唯一
	 */
	@RequestMapping(value = "/checkRoleCode", method = RequestMethod.GET)
	public R<Boolean> checkUsername(String id,String roleCode) {
		R<Boolean> result = new R<>();
		result.setData(true);//如果此参数为false则程序发生异常
		log.info("--验证角色编码是否唯一---id:"+id+"--roleCode:"+roleCode);
		try {
			SysRole role = null;
			if(oConvertUtils.isNotEmpty(id)) {
				role = sysRoleService.getById(id);
			}
			SysRole newRole = sysRoleService.getOne(new QueryWrapper<SysRole>().lambda().eq(SysRole::getRoleCode, roleCode));
			if(newRole!=null) {
				//如果根据传入的roleCode查询到信息了，那么就需要做校验了。
				if(role==null) {
					//role为空=>新增模式=>只要roleCode存在则返回false
					result.setSuccess(false);
					result.setMsg("角色编码已存在");
					return result;
				}else if(!id.equals(newRole.getId())) {
					//否则=>编辑模式=>判断两者ID是否一致-
					result.setSuccess(false);
					result.setMsg("角色编码已存在");
					return result;
				}
			}
		} catch (Exception e) {
			result.setSuccess(false);
			result.setData(false);
			result.setMsg(e.getMessage());
			return result;
		}
		result.setSuccess(true);
		return result;
	}

	/**
	 * 导出excel
	 * @param request
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(SysRole sysRole,HttpServletRequest request) {
		// Step.1 组装查询条件
		QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(sysRole, request.getParameterMap());
		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<SysRole> pageList = sysRoleService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME,"角色列表");
		mv.addObject(NormalExcelConstants.CLASS,SysRole.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		mv.addObject(NormalExcelConstants.PARAMS,new ExportParams("角色列表数据","导出人:"+user.getRealname(),"导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST,pageList);
		return mv;
	}

	/**
	 * 通过excel导入数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public R<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// 获取上传文件对象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				return sysRoleService.importExcelCheckRoleCode(file, params);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return R.error("文件导入失败:" + e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return R.error("文件导入失败！");
	}
	
	/**
	 * 查询数据规则数据
	 */
	@GetMapping(value = "/datarule/{permissionId}/{roleId}")
	public R<?> loadDatarule(@PathVariable("permissionId") String permissionId,@PathVariable("roleId") String roleId) {
		List<SysPermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
		if(list==null || list.size()==0) {
			return R.error("未找到权限配置信息");
		}else {
			Map<String,Object> map = new HashMap<>();
			map.put("datarule", list);
			LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
					.eq(SysRolePermission::getPermissionId, permissionId)
					.isNotNull(SysRolePermission::getDataRuleIds)
					.eq(SysRolePermission::getRoleId,roleId);
			SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
			if(sysRolePermission==null) {
				//return R.error("未找到角色菜单配置信息");
			}else {
				String drChecked = sysRolePermission.getDataRuleIds();
				if(oConvertUtils.isNotEmpty(drChecked)) {
					map.put("drChecked", drChecked.endsWith(",")?drChecked.substring(0, drChecked.length()-1):drChecked);
				}
			}
			return R.ok(map);
			//TODO 以后按钮权限的查询也走这个请求 无非在map中多加两个key
		}
	}
	
	/**
	 * 保存数据规则至角色菜单关联表
	 */
	@PostMapping(value = "/datarule")
	public R<?> saveDatarule(@RequestBody JSONObject jsonObject) {
		try {
			String permissionId = jsonObject.getString("permissionId");
			String roleId = jsonObject.getString("roleId");
			String dataRuleIds = jsonObject.getString("dataRuleIds");
			log.info("保存数据规则>>"+"菜单ID:"+permissionId+"角色ID:"+ roleId+"数据权限ID:"+dataRuleIds);
			LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
					.eq(SysRolePermission::getPermissionId, permissionId)
					.eq(SysRolePermission::getRoleId,roleId);
			SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
			if(sysRolePermission==null) {
				return R.error("请先保存角色菜单权限!");
			}else {
				sysRolePermission.setDataRuleIds(dataRuleIds);
				this.sysRolePermissionService.updateById(sysRolePermission);
			}
		} catch (Exception e) {
			log.error("SysRoleController.saveDatarule()发生异常：" + e.getMessage(),e);
			return R.error("保存失败");
		}
		return R.ok("保存成功!");
	}
	
	
	/**
	 * 用户角色授权功能，查询菜单权限树
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	public R<Map<String,Object>> queryTreeList(HttpServletRequest request) {
		R<Map<String,Object>> result = new R<>();
		//全部权限ids
		List<String> ids = new ArrayList<>();
		try {
			List<SysPermission> list=null;
			if(!getUser().getUsername().equals(Consts.User.SUPER)){
				list= sysPermissionService.queryByUser(getUser().getUsername());
			}else{
				LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
				query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
				query.orderByAsc(SysPermission::getSortNo);
				list = sysPermissionService.list(query);
			}
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
	
	private void getTreeModelList(List<TreeModel> treeList,List<SysPermission> metaList,TreeModel temp) {
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
