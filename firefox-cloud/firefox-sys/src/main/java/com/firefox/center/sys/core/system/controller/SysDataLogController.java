package com.firefox.center.sys.core.system.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.firefox.center.common.R;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.core.system.entity.SysDataLog;
import com.firefox.center.sys.core.system.service.ISysDataLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sys/dataLog")
@RequiredArgsConstructor
public class SysDataLogController {

	private final ISysDataLogService service;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public R<IPage<SysDataLog>> queryPageList(SysDataLog dataLog,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		R<IPage<SysDataLog>> result = new R<IPage<SysDataLog>>();
		QueryWrapper<SysDataLog> queryWrapper = QueryGenerator.initQueryWrapper(dataLog, req.getParameterMap());
		Page<SysDataLog> page = new Page<SysDataLog>(pageNo, pageSize);
		IPage<SysDataLog> pageList = service.page(page, queryWrapper);
		result.setSuccess(true);
		result.setData(pageList);
		return result;
	}
	
	/**
	 * 查询对比数据
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/queryCompareList", method = RequestMethod.GET)
	public R<List<SysDataLog>> queryCompareList(HttpServletRequest req) {
		R<List<SysDataLog>> result = new R<>();
		String dataId1 = req.getParameter("dataId1");
		String dataId2 = req.getParameter("dataId2");
		List<String> idList = new ArrayList<String>();
		idList.add(dataId1);
		idList.add(dataId2);
		try {
			List<SysDataLog> list =  (List<SysDataLog>) service.listByIds(idList);
			result.setData(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}
	
	/**
	 * 查询版本信息
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/queryDataVerList", method = RequestMethod.GET)
	public R<List<SysDataLog>> queryDataVerList(HttpServletRequest req) {
		R<List<SysDataLog>> result = new R<>();
		String dataTable = req.getParameter("dataTable");
		String dataId = req.getParameter("dataId");
		QueryWrapper<SysDataLog> queryWrapper = new QueryWrapper<SysDataLog>();
		queryWrapper.eq("data_table", dataTable);
		queryWrapper.eq("data_id", dataId);
		List<SysDataLog> list = service.list(queryWrapper);
		if(list==null||list.size()<=0) {
			result.setSuccess(false);
			result.errorMsg("未找到版本信息");
		}else {
			result.setData(list);
			result.setSuccess(true);
		}
		return result;
	}
	
}
