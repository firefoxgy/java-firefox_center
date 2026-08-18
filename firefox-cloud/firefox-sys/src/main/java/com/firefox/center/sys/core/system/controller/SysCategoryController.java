package com.firefox.center.sys.core.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.common.system.vo.DictModel;
import com.firefox.center.sys.common.system.vo.LoginUser;
import com.firefox.center.sys.common.util.oConvertUtils;
import com.firefox.center.sys.core.system.entity.SysCategory;
import com.firefox.center.sys.core.system.model.TreeSelectModel;
import com.firefox.center.sys.core.system.service.ISysCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

 /**
 * @Description: 分类字典
 * @Date:   2019-05-29
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/category")
@RequiredArgsConstructor
public class SysCategoryController {

	private final ISysCategoryService sysCategoryService;
	
	/**
	  * 分页列表查询
	 * @param sysCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/rootList")
	public R<IPage<SysCategory>> queryPageList(SysCategory sysCategory,
											   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											   HttpServletRequest req) {
		if(oConvertUtils.isEmpty(sysCategory.getPid())){
			sysCategory.setPid("0");
		}
		R<IPage<SysCategory>> result = new R<IPage<SysCategory>>();
		QueryWrapper<SysCategory> queryWrapper = new QueryWrapper<SysCategory>();
		queryWrapper.eq("pid", sysCategory.getPid());
		Page<SysCategory> page = new Page<SysCategory>(pageNo, pageSize);
		IPage<SysCategory> pageList = sysCategoryService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setData(pageList);
		return result;
	}
	
	@GetMapping(value = "/childList")
	public R<List<SysCategory>> queryPageList(SysCategory sysCategory,HttpServletRequest req) {
		R<List<SysCategory>> result = new R<List<SysCategory>>();
		QueryWrapper<SysCategory> queryWrapper = QueryGenerator.initQueryWrapper(sysCategory, req.getParameterMap());
		List<SysCategory> list = sysCategoryService.list(queryWrapper);
		result.setSuccess(true);
		result.setData(list);
		return result;
	}
	
	
	/**
	  *   添加
	 * @param sysCategory
	 * @return
	 */
	@PostMapping(value = "/add")
	public R<SysCategory> add(@RequestBody SysCategory sysCategory) {
		R<SysCategory> result = new R<SysCategory>();
		try {
			sysCategoryService.addSysCategory(sysCategory);
			result.okMsg("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.setSuccess(false);
			result.errorMsg("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param sysCategory
	 * @return
	 */
	@PutMapping(value = "/edit")
	public R<SysCategory> edit(@RequestBody SysCategory sysCategory) {
		R<SysCategory> result = new R<SysCategory>();
		SysCategory sysCategoryEntity = sysCategoryService.getById(sysCategory.getId());
		if(sysCategoryEntity==null) {
			result.setSuccess(false);
			result.errorMsg("未找到对应实体");
		}else {
			sysCategoryService.updateSysCategory(sysCategory);
			result.okMsg("修改成功!");
		}
		return result;
	}
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public R<SysCategory> delete(@RequestParam(name="id",required=true) String id) {
		R<SysCategory> result = new R<SysCategory>();
		SysCategory sysCategory = sysCategoryService.getById(id);
		if(sysCategory==null) {
			result.setSuccess(false);
			result.errorMsg("未找到对应实体");
		}else {
			boolean ok = sysCategoryService.removeById(id);
			if(ok) {
				result.okMsg("删除成功!");
			}
		}
		
		return result;
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public R<SysCategory> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		R<SysCategory> result = new R<SysCategory>();
		if(ids==null || "".equals(ids.trim())) {
			result.setSuccess(false);
			result.errorMsg("参数不识别！");
		}else {
			this.sysCategoryService.removeByIds(Arrays.asList(ids.split(",")).stream().filter(s->s.indexOf("_")==-1).collect(Collectors.toList()));
			result.okMsg("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public R<SysCategory> queryById(@RequestParam(name="id",required=true) String id) {
		R<SysCategory> result = new R<SysCategory>();
		SysCategory sysCategory = sysCategoryService.getById(id);
		if(sysCategory==null) {
			result.setSuccess(false);
			result.errorMsg("未找到对应实体");
		}else {
			result.setData(sysCategory);
			result.setSuccess(true);
		}
		return result;
	}

  /**
      * 导出excel
   *
   * @param request
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, SysCategory sysCategory) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<SysCategory> queryWrapper = QueryGenerator.initQueryWrapper(sysCategory, request.getParameterMap());
      List<SysCategory> pageList = sysCategoryService.list(queryWrapper);
      // Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      // 过滤选中数据
      String selections = request.getParameter("selections");
      if(oConvertUtils.isEmpty(selections)) {
    	  mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      }else {
    	  List<String> selectionList = Arrays.asList(selections.split(","));
    	  List<SysCategory> exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
    	  mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
      }
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "分类字典列表");
      mv.addObject(NormalExcelConstants.CLASS, SysCategory.class);
      LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("分类字典列表数据", "导出人:"+user.getRealname(), "导出信息"));
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
              List<SysCategory> listSysCategorys = ExcelImportUtil.importExcel(file.getInputStream(), SysCategory.class, params);
			 //按照编码长度排序
              Collections.sort(listSysCategorys);
			  log.info("排序后的list====>",listSysCategorys);
              for (SysCategory sysCategoryExcel : listSysCategorys) {
				  String code = sysCategoryExcel.getCode();
				  if(code.length()>3){
					  String pCode = sysCategoryExcel.getCode().substring(0,code.length()-3);
					  log.info("pCode====>",pCode);
					  String pId=sysCategoryService.queryIdByCode(pCode);
					  log.info("pId====>",pId);
					  if(StringUtils.isNotBlank(pId)){
						  sysCategoryExcel.setPid(pId);
					  }
				  }else{
					  sysCategoryExcel.setPid("0");
				  }
                  sysCategoryService.save(sysCategoryExcel);
              }
              return R.ok("文件导入成功！数据行数：" + listSysCategorys.size());
          } catch (Exception e) {
              log.error(e.getMessage(), e);
              return R.error("文件导入失败："+e.getMessage());
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
     * 加载单个数据 用于回显
   */
    @RequestMapping(value = "/loadOne", method = RequestMethod.GET)
 	public R<SysCategory> loadOne(@RequestParam(name="field") String field,@RequestParam(name="val") String val) {
		R<SysCategory> result = new R<SysCategory>();
 		try {
 			
 			QueryWrapper<SysCategory> query = new QueryWrapper<SysCategory>();
 			query.eq(field, val);
 			List<SysCategory> ls = this.sysCategoryService.list(query);
 			if(ls==null || ls.size()==0) {
 				result.setMsg("查询无果");
 	 			result.setSuccess(false);
 			}else if(ls.size()>1) {
 				result.setMsg("查询数据异常,["+field+"]存在多个值:"+val);
 	 			result.setSuccess(false);
 			}else {
 				result.setSuccess(true);
 				result.setData(ls.get(0));
 			}
 		} catch (Exception e) {
 			e.printStackTrace();
 			result.setMsg(e.getMessage());
 			result.setSuccess(false);
 		}
 		return result;
 	}
   
    /**
          * 加载节点的子数据
     */
    @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
	public R<List<TreeSelectModel>> loadTreeChildren(@RequestParam(name="pid") String pid) {
		R<List<TreeSelectModel>> result = new R<List<TreeSelectModel>>();
		try {
			List<TreeSelectModel> ls = this.sysCategoryService.queryListByPid(pid);
			result.setData(ls);
			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg(e.getMessage());
			result.setSuccess(false);
		}
		return result;
	}
    
    /**
         * 加载一级节点/如果是同步 则所有数据
     */
    @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
   	public R<List<TreeSelectModel>> loadTreeRoot(@RequestParam(name="async") Boolean async,@RequestParam(name="pcode") String pcode) {
   		R<List<TreeSelectModel>> result = new R<List<TreeSelectModel>>();
   		try {
   			List<TreeSelectModel> ls = this.sysCategoryService.queryListByCode(pcode);
   			if(!async) {
   				loadAllCategoryChildren(ls);
   			}
   			result.setData(ls);
   			result.setSuccess(true);
   		} catch (Exception e) {
   			e.printStackTrace();
   			result.setMsg(e.getMessage());
   			result.setSuccess(false);
   		}
   		return result;
   	}
  
    /**
         * 递归求子节点 同步加载用到
     */
  	private void loadAllCategoryChildren(List<TreeSelectModel> ls) {
  		for (TreeSelectModel tsm : ls) {
			List<TreeSelectModel> temp = this.sysCategoryService.queryListByPid(tsm.getKey());
			if(temp!=null && temp.size()>0) {
				tsm.setChildren(temp);
				loadAllCategoryChildren(temp);
			}
		}
  	}

	 /**
	  * 校验编码
	  * @param pid
	  * @param code
	  * @return
	  */
	 @GetMapping(value = "/checkCode")
	 public R<?> checkCode(@RequestParam(name="pid",required = false) String pid,@RequestParam(name="code",required = false) String code) {
		if(oConvertUtils.isEmpty(code)){
			return R.error("错误,类型编码为空!");
		}
		if(oConvertUtils.isEmpty(pid)){
			return R.ok();
		}
		SysCategory parent = this.sysCategoryService.getById(pid);
		if(code.startsWith(parent.getCode())){
			return R.ok();
		}else{
			return R.error("编码不符合规范,须以\""+parent.getCode()+"\"开头!");
		}

	 }


	 /**
	  * 分类字典树控件 加载节点
	  * @param pid
	  * @param pcode
	  * @param condition
	  * @return
	  */
	 @RequestMapping(value = "/loadTreeData", method = RequestMethod.GET)
	 public R<List<TreeSelectModel>> loadDict(@RequestParam(name="pid",required = false) String pid,@RequestParam(name="pcode",required = false) String pcode, @RequestParam(name="condition",required = false) String condition) {
		 R<List<TreeSelectModel>> result = new R<List<TreeSelectModel>>();
		 //pid如果传值了 就忽略pcode的作用
		 if(oConvertUtils.isEmpty(pid)){
		 	if(oConvertUtils.isEmpty(pcode)){
				result.setSuccess(false);
				result.setMsg("加载分类字典树参数有误.[null]!");
				return result;
			}else{
		 		if(ISysCategoryService.ROOT_PID_VALUE.equals(pcode)){
					pid = ISysCategoryService.ROOT_PID_VALUE;
				}else{
					pid = this.sysCategoryService.queryIdByCode(pcode);
				}
				if(oConvertUtils.isEmpty(pid)){
					result.setSuccess(false);
					result.setMsg("加载分类字典树参数有误.[code]!");
					return result;
				}
			}
		 }
		 Map<String, String> query = null;
		 if(oConvertUtils.isNotEmpty(condition)) {
			 query = JSON.parseObject(condition, Map.class);
		 }
		 List<TreeSelectModel> ls = sysCategoryService.queryListByPid(pid,query);
		 result.setSuccess(true);
		 result.setData(ls);
		 return result;
	 }

	 /**
	  * 分类字典控件数据回显[表单页面]
	  *
	  * @param ids
	  * @return
	  */
	 @RequestMapping(value = "/loadDictItem", method = RequestMethod.GET)
	 public R<List<String>> loadDictItem(@RequestParam(name = "ids") String ids) {
		 R<List<String>> result = new R<>();
		 // 非空判断
		 if (StringUtils.isBlank(ids)) {
			 result.setSuccess(false);
			 result.setMsg("ids 不能为空");
			 return result;
		 }
		 String[] idArray = ids.split(",");
		 LambdaQueryWrapper<SysCategory> query = new LambdaQueryWrapper<>();
		 query.in(SysCategory::getId, Arrays.asList(idArray));
		 // 查询数据
		 List<SysCategory> list = this.sysCategoryService.list(query);
		 // 取出name并返回
		 List<String> textList = list.stream().map(SysCategory::getName).collect(Collectors.toList());
		 result.setSuccess(true);
		 result.setData(textList);
		 return result;
	 }

	 /**
	  * [列表页面]加载分类字典数据 用于值的替换
	  * @param code
	  * @return
	  */
	 @RequestMapping(value = "/loadAllData", method = RequestMethod.GET)
	 public R<List<DictModel>> loadAllData(@RequestParam(name="code",required = true) String code) {
		 R<List<DictModel>> result = new R<List<DictModel>>();
		 LambdaQueryWrapper<SysCategory> query = new LambdaQueryWrapper<SysCategory>();
		 if(oConvertUtils.isNotEmpty(code) && !"0".equals(code)){
			 query.likeRight(SysCategory::getCode,code);
		 }
		 List<SysCategory> list = this.sysCategoryService.list(query);
		 if(list==null || list.size()==0) {
			 result.setMsg("无数据,参数有误.[code]");
			 result.setSuccess(false);
			 return result;
		 }
		 List<DictModel> rdList = new ArrayList<DictModel>();
		 for (SysCategory c : list) {
			 rdList.add(new DictModel(c.getId(),c.getName()));
		 }
		 result.setSuccess(true);
		 result.setData(rdList);
		 return result;
	 }



}
