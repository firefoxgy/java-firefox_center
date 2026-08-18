package com.firefox.center.sys.core.message.controller;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefox.center.common.R;
import com.firefox.center.sys.common.system.base.controller.BaseController;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.core.message.entity.SysMessage;
import com.firefox.center.sys.core.message.service.ISysMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 消息
 * * @date: 2019-04-09
 * @version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/message/sysMessage")
@RequiredArgsConstructor
public class SysMessageController extends BaseController<SysMessage, ISysMessageService> {

	private final ISysMessageService sysMessageService;

	/**
	 * 分页列表查询
	 * 
	 * @param sysMessage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public R<?> queryPageList(SysMessage sysMessage,
							  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
							  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
							  HttpServletRequest req) {
		QueryWrapper<SysMessage> queryWrapper = QueryGenerator.initQueryWrapper(sysMessage, req.getParameterMap());
		Page<SysMessage> page = new Page<SysMessage>(pageNo, pageSize);
		IPage<SysMessage> pageList = sysMessageService.page(page, queryWrapper);
        return R.ok(pageList);
	}

	/**
	 * 添加
	 * 
	 * @param sysMessage
	 * @return
	 */
	@PostMapping(value = "/add")
	public R<?> add(@RequestBody SysMessage sysMessage) {
		sysMessageService.save(sysMessage);
		return R.ok("添加成功！");
	}

	/**
	 * 编辑
	 * 
	 * @param sysMessage
	 * @return
	 */
	@PutMapping(value = "/edit")
	public R<?> edit(@RequestBody SysMessage sysMessage) {
		sysMessageService.updateById(sysMessage);
        return R.ok("修改成功!");

	}

	/**
	 * 通过id删除
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public R<?> delete(@RequestParam(name = "id", required = true) String id) {
		sysMessageService.removeById(id);
        return R.ok("删除成功!");
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public R<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		sysMessageService.removeByIds(Arrays.asList(ids.split(",")).stream().filter(s->s.indexOf("_")==-1).collect(Collectors.toList()));
	    return R.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public R<?> queryById(@RequestParam(name = "id", required = true) String id) {
		return R.ok(sysMessageService.getById(id));
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 */
	@GetMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysMessage sysMessage) {
		return super.exportXls(request,sysMessage,SysMessage.class, "推送消息模板");
	}

	/**
	 * excel导入
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping(value = "/importExcel")
	public R<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, SysMessage.class);
	}

}
