package com.firefox.center.sys.core.system.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.constant.CacheConstant;
import com.firefox.center.sys.common.constant.CommonConstant;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.common.system.util.JwtUtil;
import com.firefox.center.sys.common.system.vo.LoginUser;
import com.firefox.center.sys.common.util.ImportExcelUtil;
import com.firefox.center.sys.common.util.oConvertUtils;
import com.firefox.center.sys.core.system.entity.SysDepart;
import com.firefox.center.sys.core.system.entity.SysUser;
import com.firefox.center.sys.core.system.model.DepartIdModel;
import com.firefox.center.sys.core.system.model.SysDepartTreeModel;
import com.firefox.center.sys.core.system.service.ISysDepartService;
import com.firefox.center.sys.core.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 部门表 前端控制器
 * <p>
 * 
 * @Author: Steve @Since： 2019-01-22
 */
@Slf4j
@RestController
@RequestMapping("/sys/sysDepart")
@RequiredArgsConstructor
public class SysDepartController {

	private final ISysDepartService sysDepartService;
	public final RedisTemplate<String, Object> redisTemplate;
	private final ISysUserService sysUserService;
	/**
	 * 查询数据 查出我的部门,并以树结构数据格式响应给前端
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryMyDeptTreeList", method = RequestMethod.GET)
	public R<List<SysDepartTreeModel>> queryMyDeptTreeList() {
		R<List<SysDepartTreeModel>> result = new R<>();
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		try {
			if(oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals( CommonConstant.USER_IDENTITY_2 )){
				List<SysDepartTreeModel> list = sysDepartService.queryMyDeptTreeList(user.getDepartIds());
				result.setData(list);
				result.setMsg(CommonConstant.USER_IDENTITY_2.toString());
				result.setSuccess(true);
			}else{
				result.setMsg(CommonConstant.USER_IDENTITY_1.toString());
				result.setSuccess(true);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 查询数据 查出所有部门,并以树结构数据格式响应给前端
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	public R<List<SysDepartTreeModel>> queryTreeList() {
		R<List<SysDepartTreeModel>> result = new R<>();
		try {
			// 从内存中读取
//			List<SysDepartTreeModel> list =FindsDepartsChildrenUtil.getSysDepartTreeList();
//			if (CollectionUtils.isEmpty(list)) {
//				list = sysDepartService.queryTreeList();
//			}
			List<SysDepartTreeModel> list = sysDepartService.queryTreeList();
			result.setData(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 添加新数据 添加用户新建的部门对象数据,并保存到数据库
	 * 
	 * @param sysDepart
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@CacheEvict(value= {CacheConstant.Sys.SYS_DEPARTS_CACHE,CacheConstant.Sys.SYS_DEPART_IDS_CACHE}, allEntries=true)
	public R<SysDepart> add(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
		R<SysDepart> result = new R<SysDepart>();
		String username = JwtUtil.getUserNameByToken(request);
		try {
			sysDepart.setCreateBy(username);
			sysDepartService.saveDepartData(sysDepart, username);
			//清除部门树内存
			// FindsDepartsChildrenUtil.clearSysDepartTreeList();
			// FindsDepartsChildrenUtil.clearDepartIdModel();
			result.okMsg("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.errorMsg("操作失败");
		}
		return result;
	}

	/**
	 * 编辑数据 编辑部门的部分数据,并保存到数据库
	 * 
	 * @param sysDepart
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@CacheEvict(value= {CacheConstant.Sys.SYS_DEPARTS_CACHE,CacheConstant.Sys.SYS_DEPART_IDS_CACHE}, allEntries=true)
	public R<SysDepart> edit(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
		String username = JwtUtil.getUserNameByToken(request);
		sysDepart.setUpdateBy(username);
		R<SysDepart> result = new R<SysDepart>();
		SysDepart sysDepartEntity = sysDepartService.getById(sysDepart.getId());
		if (sysDepartEntity == null) {
			result.errorMsg("未找到对应实体");
		} else {
			boolean ok = sysDepartService.updateDepartDataById(sysDepart, username);
			// TODO 返回false说明什么？
			if (ok) {
				//清除部门树内存
				//FindsDepartsChildrenUtil.clearSysDepartTreeList();
				//FindsDepartsChildrenUtil.clearDepartIdModel();
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
	@CacheEvict(value= {CacheConstant.Sys.SYS_DEPARTS_CACHE,CacheConstant.Sys.SYS_DEPART_IDS_CACHE}, allEntries=true)
   public R<SysDepart> delete(@RequestParam(name="id",required=true) String id) {

       R<SysDepart> result = new R<SysDepart>();
       SysDepart sysDepart = sysDepartService.getById(id);
       if(sysDepart==null) {
           result.errorMsg("未找到对应实体");
       }else {
           boolean ok = sysDepartService.delete(id);
           if(ok) {
	            //清除部门树内存
	   		   //FindsDepartsChildrenUtil.clearSysDepartTreeList();
	   		   // FindsDepartsChildrenUtil.clearDepartIdModel();
               result.okMsg("删除成功!");
           }
       }
       return result;
   }


	/**
	 * 批量删除 根据前端请求的多个ID,对数据库执行删除相关部门数据的操作
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	@CacheEvict(value= {CacheConstant.Sys.SYS_DEPARTS_CACHE,CacheConstant.Sys.SYS_DEPART_IDS_CACHE}, allEntries=true)
	public R<SysDepart> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {

		R<SysDepart> result = new R<SysDepart>();
		if (ids == null || "".equals(ids.trim())) {
			result.errorMsg("参数不识别！");
		} else {
			this.sysDepartService.deleteBatchWithChildren(Arrays.asList(ids.split(",")).stream().filter(s->s.indexOf("_")==-1).collect(Collectors.toList()));
			result.okMsg("删除成功!");
		}
		return result;
	}

	/**
	 * 查询数据 添加或编辑页面对该方法发起请求,以树结构形式加载所有部门的名称,方便用户的操作
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryIdTree", method = RequestMethod.GET)
	public R<List<DepartIdModel>> queryIdTree() {
//		R<List<DepartIdModel>> result = new R<List<DepartIdModel>>();
//		List<DepartIdModel> idList;
//		try {
//			idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//			if (idList != null && idList.size() > 0) {
//				result.setData(idList);
//				result.setSuccess(true);
//			} else {
//				sysDepartService.queryTreeList();
//				idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//				result.setData(idList);
//				result.setSuccess(true);
//			}
//			return result;
//		} catch (Exception e) {
//			log.error(e.getMessage(),e);
//			result.setSuccess(false);
//			return result;
//		}
		R<List<DepartIdModel>> result = new R<>();
		try {
			List<DepartIdModel> list = sysDepartService.queryDepartIdTreeList();
			result.setData(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}
	 
	/**
	 * <p>
	 * 部门搜索功能方法,根据关键字模糊搜索相关部门
	 * </p>
	 * 
	 * @param keyWord
	 * @return
	 */
	@RequestMapping(value = "/searchBy", method = RequestMethod.GET)
	public R<List<SysDepartTreeModel>> searchBy(@RequestParam(name = "keyWord", required = true) String keyWord,@RequestParam(name = "myDeptSearch", required = false) String myDeptSearch) {
		R<List<SysDepartTreeModel>> result = new R<List<SysDepartTreeModel>>();
		//部门查询，myDeptSearch为1时为我的部门查询，登录用户为上级时查只查负责部门下数据
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String departIds = null;
		if(oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals( CommonConstant.USER_IDENTITY_2 )){
			departIds = user.getDepartIds();
		}
		List<SysDepartTreeModel> treeList = this.sysDepartService.searhBy(keyWord,myDeptSearch,departIds);
		if (treeList == null || treeList.size() == 0) {
			result.setSuccess(false);
			result.setMsg("未查询匹配数据！");
			return result;
		}
		result.setData(treeList);
		return result;
	}


	/**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysDepart sysDepart,HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysDepart> queryWrapper = QueryGenerator.initQueryWrapper(sysDepart, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysDepart> pageList = sysDepartService.list(queryWrapper);
        //按字典排序
        Collections.sort(pageList, new Comparator<SysDepart>() {
            @Override
			public int compare(SysDepart arg0, SysDepart arg1) {
            	return arg0.getOrgCode().compareTo(arg1.getOrgCode());
            }
        });
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "部门列表");
        mv.addObject(NormalExcelConstants.CLASS, SysDepart.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("部门列表数据", "导出人:"+user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	@CacheEvict(value= {CacheConstant.Sys.SYS_DEPARTS_CACHE,CacheConstant.Sys.SYS_DEPART_IDS_CACHE}, allEntries=true)
    public R<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		List<String> errorMessageList = new ArrayList<>();
		List<SysDepart> listSysDeparts = null;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
            	// orgCode编码长度
            	int codeLength = 3;
                listSysDeparts = ExcelImportUtil.importExcel(file.getInputStream(), SysDepart.class, params);
                //按长度排序
                Collections.sort(listSysDeparts, new Comparator<SysDepart>() {
                    @Override
					public int compare(SysDepart arg0, SysDepart arg1) {
                    	return arg0.getOrgCode().length() - arg1.getOrgCode().length();
                    }
                });

                int num = 0;
                for (SysDepart sysDepart : listSysDeparts) {
                	String orgCode = sysDepart.getOrgCode();
                	if(orgCode.length() > codeLength) {
                		String parentCode = orgCode.substring(0, orgCode.length()-codeLength);
                		QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<SysDepart>();
                		queryWrapper.eq("org_code", parentCode);
                		try {
                		SysDepart parentDept = sysDepartService.getOne(queryWrapper);
                		if(!parentDept.equals(null)) {
							sysDepart.setParentId(parentDept.getId());
						} else {
							sysDepart.setParentId("");
						}
                		}catch (Exception e) {
                			//没有查找到parentDept
                		}
                	}else{
                		sysDepart.setParentId("");
					}
					sysDepart.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
					ImportExcelUtil.importDateSaveOne(sysDepart, ISysDepartService.class, errorMessageList, num, CommonConstant.SQL_INDEX_UNIQ_DEPART_ORG_CODE);
					num++;
                }
				//清空部门缓存
				Set keys3 = redisTemplate.keys(CacheConstant.Sys.SYS_DEPARTS_CACHE + "*");
				Set keys4 = redisTemplate.keys(CacheConstant.Sys.SYS_DEPART_IDS_CACHE + "*");
				redisTemplate.delete(keys3);
				redisTemplate.delete(keys4);
				return ImportExcelUtil.imporReturnRes(errorMessageList.size(), listSysDeparts.size() - errorMessageList.size(), errorMessageList);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return R.error("文件导入失败:"+e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return R.error("文件导入失败！");
    }


	/**
	 * 查询所有部门信息
	 * @return
	 */
	@GetMapping("listAll")
	public R<List<SysDepart>> listAll(@RequestParam(name = "id", required = false) String id) {
		R<List<SysDepart>> result = new R<>();
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.orderByAsc(SysDepart::getOrgCode);
		if(oConvertUtils.isNotEmpty(id)){
			String arr[] = id.split(",");
			query.in(SysDepart::getId,arr);
		}
		List<SysDepart> ls = this.sysDepartService.list(query);
		result.setSuccess(true);
		result.setData(ls);
		return result;
	}
	/**
	 * 查询数据 查出所有部门,并以树结构数据格式响应给前端
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryTreeByKeyWord", method = RequestMethod.GET)
	public R<Map<String,Object>> queryTreeByKeyWord(@RequestParam(name = "keyWord", required = false) String keyWord) {
		R<Map<String,Object>> result = new R<>();
		try {
			Map<String,Object> map=new HashMap<String,Object>();
			List<SysDepartTreeModel> list = sysDepartService.queryTreeByKeyWord(keyWord);
			//根据keyWord获取用户信息
			LambdaQueryWrapper<SysUser> queryUser = new LambdaQueryWrapper<SysUser>();
			queryUser.eq(SysUser::getDelFlag,0);
			queryUser.and(i -> i.like(SysUser::getUsername, keyWord).or().like(SysUser::getRealname, keyWord));
			List<SysUser> sysUsers = this.sysUserService.list(queryUser);
			map.put("userList",sysUsers);
			map.put("departList",list);
			result.setData(map);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 根据部门编码获取部门信息
	 *
	 * @param orgCode
	 * @return
	 */
	@GetMapping("/getDepartName")
	public R<SysDepart> getDepartName(@RequestParam(name = "orgCode") String orgCode) {
		R<SysDepart> result = new R<>();
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<>();
		query.eq(SysDepart::getOrgCode, orgCode);
		SysDepart sysDepart = sysDepartService.getOne(query);
		result.setSuccess(true);
		result.setData(sysDepart);
		return result;
	}
}
