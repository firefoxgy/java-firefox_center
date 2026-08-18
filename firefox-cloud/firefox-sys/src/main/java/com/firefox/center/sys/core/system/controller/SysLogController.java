package com.firefox.center.sys.core.system.controller;


import java.util.Arrays;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.firefox.center.common.R;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.common.util.oConvertUtils;
import com.firefox.center.sys.core.system.entity.SysLog;
import com.firefox.center.sys.core.system.entity.SysRole;
import com.firefox.center.sys.core.system.service.ISysLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 系统日志表 前端控制器
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Slf4j
@RestController
@RequestMapping("/sys/log")
@RequiredArgsConstructor
public class SysLogController {

	private final ISysLogService sysLogService;
	
	/**
	 * @功能：查询日志记录
	 * @param syslog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public R<IPage<SysLog>> queryPageList(SysLog syslog,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		R<IPage<SysLog>> result = new R<IPage<SysLog>>();
		QueryWrapper<SysLog> queryWrapper = QueryGenerator.initQueryWrapper(syslog, req.getParameterMap());
		Page<SysLog> page = new Page<SysLog>(pageNo, pageSize);
		//日志关键词
		String keyWord = req.getParameter("keyWord");
		if(oConvertUtils.isNotEmpty(keyWord)) {
			queryWrapper.like("log_content",keyWord);
		}
		//TODO 过滤逻辑处理
		//TODO begin、end逻辑处理
		//TODO 一个强大的功能，前端传一个字段字符串，后台只返回这些字符串对应的字段
		//创建时间/创建人的赋值
		IPage<SysLog> pageList = sysLogService.page(page, queryWrapper);
		log.info("查询当前页："+pageList.getCurrent());
		log.info("查询当前页数量："+pageList.getSize());
		log.info("查询结果数量："+pageList.getRecords().size());
		log.info("数据总数："+pageList.getTotal());
		result.setSuccess(true);
		result.setData(pageList);
		return result;
	}
	
	/**
	 * @功能：删除单个日志记录
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public R<SysLog> delete(@RequestParam(name="id",required=true) String id) {
		R<SysLog> result = new R<SysLog>();
		SysLog sysLog = sysLogService.getById(id);
		if(sysLog==null) {
			result.setSuccess(false);
			result.errorMsg("未找到对应实体");
		}else {
			boolean ok = sysLogService.removeById(id);
			if(ok) {
				result.okMsg("删除成功!");
			}
		}
		return result;
	}
	
	/**
	 * @功能：批量，全部清空日志记录
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public R<SysRole> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		R<SysRole> result = new R<SysRole>();
		if(ids==null || "".equals(ids.trim())) {
			result.setSuccess(false);
			result.errorMsg("参数不识别！");
		}else {
			if("allclear".equals(ids)) {
				this.sysLogService.removeAll();
				result.okMsg("清除成功!");
			}
			this.sysLogService.removeByIds(Arrays.asList(ids.split(",")).stream().filter(s->s.indexOf("_")==-1).collect(Collectors.toList()));
			result.okMsg("删除成功!");
		}
		return result;
	}
	
	
}
