package com.firefox.center.sys.core.message.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.system.base.controller.BaseController;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.common.system.vo.LoginUser;
import com.firefox.center.sys.core.message.entity.MsgParams;
import com.firefox.center.sys.core.message.entity.SysMessageTemplate;
import com.firefox.center.sys.core.message.service.ISysMessageTemplateService;
import com.firefox.center.sys.core.message.util.PushMsgUtil;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 消息模板
 * * @Sate: 2019-04-09
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/message/sysMessageTemplate")
@RequiredArgsConstructor
public class SysMessageTemplateController extends BaseController<SysMessageTemplate, ISysMessageTemplateService> {

	private final ISysMessageTemplateService sysMessageTemplateService;
	private final PushMsgUtil pushMsgUtil;

	/**
	 * 分页列表查询
	 * 
	 * @param sysMessageTemplate
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public R<?> queryPageList(SysMessageTemplate sysMessageTemplate,
							  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
							  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
							  HttpServletRequest req) {
		QueryWrapper<SysMessageTemplate> queryWrapper = QueryGenerator.initQueryWrapper(sysMessageTemplate, req.getParameterMap());
		Page<SysMessageTemplate> page = new Page<SysMessageTemplate>(pageNo, pageSize);
		IPage<SysMessageTemplate> pageList = sysMessageTemplateService.page(page, queryWrapper);
        return R.ok(pageList);
	}

	/**
	 * 添加
	 * 
	 * @param sysMessageTemplate
	 * @return
	 */
	@PostMapping(value = "/add")
	public R<?> add(@RequestBody SysMessageTemplate sysMessageTemplate) {
		sysMessageTemplateService.save(sysMessageTemplate);
        return R.ok("添加成功！");
	}

	/**
	 * 编辑
	 * 
	 * @param sysMessageTemplate
	 * @return
	 */
	@PutMapping(value = "/edit")
	public R<?> edit(@RequestBody SysMessageTemplate sysMessageTemplate) {
		sysMessageTemplateService.updateById(sysMessageTemplate);
        return R.ok("更新成功！");
	}

	/**
	 * 通过id删除
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public R<?> delete(@RequestParam(name = "id", required = true) String id) {
		sysMessageTemplateService.removeById(id);
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
		this.sysMessageTemplateService.removeByIds(Arrays.asList(ids.split(",")).stream().filter(s->s.indexOf("_")==-1).collect(Collectors.toList()));
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
		SysMessageTemplate sysMessageTemplate = sysMessageTemplateService.getById(id);
        return R.ok(sysMessageTemplate);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 */
	@GetMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request,SysMessageTemplate sysMessageTemplate) {
		return super.exportXls(request, sysMessageTemplate, SysMessageTemplate.class,"推送消息模板");
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
		return super.importExcel(request, response, SysMessageTemplate.class);
	}

	/**
	 * 发送消息
	 */
	@PostMapping(value = "/sendMsg")
	public R<SysMessageTemplate> sendMessage(@RequestBody MsgParams msgParams) {
		R<SysMessageTemplate> result = new R<SysMessageTemplate>();
		Map<String, String> map = null;
		try {
			map = (Map<String, String>) JSON.parse(msgParams.getTestData());
		} catch (Exception e) {
			result.errorMsg("解析Json出错！");
			return result;
		}
		LoginUser user=(LoginUser) SecurityUtils.getSubject().getPrincipal();
		boolean is_sendSuccess = pushMsgUtil.sendMessage(user, msgParams.getTemplateCode(), map, msgParams.getReceiver());
		if (is_sendSuccess) {
			result.setMsg("发送成功！");
		} else {
			result.errorMsg("发送失败！");
		}
		return result;
	}
}
