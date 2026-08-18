package com.firefox.center.sys.core.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.constant.CommonConstant;
import com.firefox.center.sys.common.system.vo.LoginUser;
import com.firefox.center.sys.common.util.oConvertUtils;
import com.firefox.center.sys.core.system.entity.SysAnnouncementSend;
import com.firefox.center.sys.core.system.model.AnnouncementSendModel;
import com.firefox.center.sys.core.system.service.ISysAnnouncementSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

 /**
 * @Title: Controller
 * @Description: 用户通告阅读标记表
 * @Date:  2019-02-21
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/sysAnnouncementSend")
@RequiredArgsConstructor
public class SysAnnouncementSendController {

	private final ISysAnnouncementSendService sysAnnouncementSendService;
	
	/**
	  * 分页列表查询
	 * @param sysAnnouncementSend
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public R<IPage<SysAnnouncementSend>> queryPageList(SysAnnouncementSend sysAnnouncementSend,
													   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													   HttpServletRequest req) {
		R<IPage<SysAnnouncementSend>> result = new R<IPage<SysAnnouncementSend>>();
		QueryWrapper<SysAnnouncementSend> queryWrapper = new QueryWrapper<SysAnnouncementSend>(sysAnnouncementSend);
		Page<SysAnnouncementSend> page = new Page<SysAnnouncementSend>(pageNo,pageSize);
		//排序逻辑 处理
		String column = req.getParameter("column");
		String order = req.getParameter("order");
		if(oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
			if("asc".equals(order)) {
				queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
			}else {
				queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
			}
		}
		IPage<SysAnnouncementSend> pageList = sysAnnouncementSendService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setData(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param sysAnnouncementSend
	 * @return
	 */
	@PostMapping(value = "/add")
	public R<SysAnnouncementSend> add(@RequestBody SysAnnouncementSend sysAnnouncementSend) {
		R<SysAnnouncementSend> result = new R<SysAnnouncementSend>();
		try {
			sysAnnouncementSendService.save(sysAnnouncementSend);
			result.okMsg("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.errorMsg("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param sysAnnouncementSend
	 * @return
	 */
	@PutMapping(value = "/edit")
	public R<SysAnnouncementSend> eidt(@RequestBody SysAnnouncementSend sysAnnouncementSend) {
		R<SysAnnouncementSend> result = new R<SysAnnouncementSend>();
		SysAnnouncementSend sysAnnouncementSendEntity = sysAnnouncementSendService.getById(sysAnnouncementSend.getId());
		if(sysAnnouncementSendEntity==null) {
			result.errorMsg("未找到对应实体");
		}else {
			boolean ok = sysAnnouncementSendService.updateById(sysAnnouncementSend);
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
	@DeleteMapping(value = "/delete")
	public R<SysAnnouncementSend> delete(@RequestParam(name="id",required=true) String id) {
		R<SysAnnouncementSend> result = new R<SysAnnouncementSend>();
		SysAnnouncementSend sysAnnouncementSend = sysAnnouncementSendService.getById(id);
		if(sysAnnouncementSend==null) {
			result.errorMsg("未找到对应实体");
		}else {
			boolean ok = sysAnnouncementSendService.removeById(id);
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
	public R<SysAnnouncementSend> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		R<SysAnnouncementSend> result = new R<SysAnnouncementSend>();
		if(ids==null || "".equals(ids.trim())) {
			result.errorMsg("参数不识别！");
		}else {
			this.sysAnnouncementSendService.removeByIds(Arrays.asList(ids.split(",")).stream().filter(s->s.indexOf("_")==-1).collect(Collectors.toList()));
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
	public R<SysAnnouncementSend> queryById(@RequestParam(name="id",required=true) String id) {
		R<SysAnnouncementSend> result = new R<SysAnnouncementSend>();
		SysAnnouncementSend sysAnnouncementSend = sysAnnouncementSendService.getById(id);
		if(sysAnnouncementSend==null) {
			result.errorMsg("未找到对应实体");
		}else {
			result.setData(sysAnnouncementSend);
			result.setSuccess(true);
		}
		return result;
	}
	
	/**
	 * @功能：更新用户系统消息阅读状态
	 * @param json
	 * @return
	 */
	@PutMapping(value = "/editByAnntIdAndUserId")
	public R<SysAnnouncementSend> editById(@RequestBody JSONObject json) {
		R<SysAnnouncementSend> result = new R<SysAnnouncementSend>();
		String anntId = json.getString("anntId");
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		String userId = sysUser.getId();
		LambdaUpdateWrapper<SysAnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
		updateWrapper.set(SysAnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
		updateWrapper.set(SysAnnouncementSend::getReadTime, new Date());
		updateWrapper.last("where annt_id ='"+anntId+"' and user_id ='"+userId+"'");
		SysAnnouncementSend announcementSend = new SysAnnouncementSend();
		sysAnnouncementSendService.update(announcementSend, updateWrapper);
		result.setSuccess(true);
		return result;
	}
	
	/**
	 * @功能：获取我的消息
	 * @return
	 */
	@GetMapping(value = "/getMyAnnouncementSend")
	public R<IPage<AnnouncementSendModel>> getMyAnnouncementSend(AnnouncementSendModel announcementSendModel,
			@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
			  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		R<IPage<AnnouncementSendModel>> result = new R<IPage<AnnouncementSendModel>>();
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		String userId = sysUser.getId();
		announcementSendModel.setUserId(userId);
		announcementSendModel.setPageNo((pageNo-1)*pageSize);
		announcementSendModel.setPageSize(pageSize);
		Page<AnnouncementSendModel> pageList = new Page<AnnouncementSendModel>(pageNo,pageSize);
		pageList = sysAnnouncementSendService.getMyAnnouncementSendPage(pageList, announcementSendModel);
		result.setData(pageList);
		result.setSuccess(true);
		return result;
	}

	/**
	 * @功能：一键已读
	 * @return
	 */
	@PutMapping(value = "/readAll")
	public R<SysAnnouncementSend> readAll() {
		R<SysAnnouncementSend> result = new R<SysAnnouncementSend>();
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		String userId = sysUser.getId();
		LambdaUpdateWrapper<SysAnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
		updateWrapper.set(SysAnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
		updateWrapper.set(SysAnnouncementSend::getReadTime, new Date());
		updateWrapper.last("where user_id ='"+userId+"'");
		SysAnnouncementSend announcementSend = new SysAnnouncementSend();
		sysAnnouncementSendService.update(announcementSend, updateWrapper);
		result.setSuccess(true);
		result.setMsg("全部已读");
		return result;
	}
}
